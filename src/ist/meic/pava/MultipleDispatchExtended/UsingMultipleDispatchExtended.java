package ist.meic.pava.MultipleDispatchExtended;


import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class UsingMultipleDispatchExtended {


    static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE = new HashMap<>();

    static {
        WRAPPER_TO_PRIMITIVE.put(Integer.class, int.class);
        WRAPPER_TO_PRIMITIVE.put(Double.class, double.class);
        WRAPPER_TO_PRIMITIVE.put(Long.class, long.class);
        WRAPPER_TO_PRIMITIVE.put(Float.class, float.class);
        WRAPPER_TO_PRIMITIVE.put(Short.class, short.class);
        WRAPPER_TO_PRIMITIVE.put(Boolean.class, boolean.class);
        WRAPPER_TO_PRIMITIVE.put(Character.class, char.class);
        WRAPPER_TO_PRIMITIVE.put(Byte.class, byte.class);
    }


    public static Object invoke(Object receiver, String name, Object... args) {

        Class<?>[] argsTypes;

        // First it tries to find and call the most specific method with primitive types
        argsTypes = Arrays
                .stream(args)
                .map(arg -> arg == null ? Object.class : arg.getClass())
                .map(type -> isWrappedType(type) ? WRAPPER_TO_PRIMITIVE.get(type) : type)
                .toArray(Class[]::new);
        try {
            return invokeMethod(receiver, name, argsTypes, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {

            // Then, if a exception is caught, it retries it with no primitive types
            argsTypes = Arrays
                    .stream(args)
                    .map(o -> o == null ? Object.class : o.getClass())
                    .toArray(Class[]::new);
            try {
                return invokeMethod(receiver, name, argsTypes, args);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static boolean isWrappedType(Class<?> type) {
        return WRAPPER_TO_PRIMITIVE.containsKey(type);
    }

    // Invoke a non varargs method or call specific method do deal with varargs method
    private static Object invokeMethod(Object receiver,
                                       String name,
                                       Class<?>[] argsTypes,
                                       Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = bestMethod(receiver.getClass(), name, argsTypes);

        if (method.isVarArgs()) {
            return varargsMethodInvoke(receiver, args, method);
        }
        return method.invoke(receiver, args);
    }

    // Build and fill the type array needed to invoke a varargs method
    private static Object varargsMethodInvoke(Object receiver,
                                              Object[] args,
                                              Method method) throws IllegalAccessException, InvocationTargetException {

        int methodParams = method.getParameterTypes().length;
        Class<?> arrayComponentType = method.getParameterTypes()[methodParams - 1].getComponentType();
        Object varargsArray = Array.newInstance(arrayComponentType, args.length - methodParams + 1);

        if (methodParams == 1) {
            IntStream
                    .range(0, args.length)
                    .forEach(i -> Array.set(varargsArray, i, args[i]));
            return method.invoke(receiver, varargsArray);
        } else {
            IntStream
                    .range(0, args.length - methodParams + 1)
                    .forEach(i -> Array.set(varargsArray, i, args[args.length - methodParams + i]));
            Object[] newArgs = (Object[]) Array.newInstance(Object.class, methodParams);
            IntStream
                    .range(0, methodParams - 1)
                    .forEach(i -> Array.set(newArgs, i, args[i]));
            Array.set(newArgs, methodParams - 1, varargsArray);
            return method.invoke(receiver, newArgs);
        }
    }

    // Get a method from receiver type or its superclasses types, whom parameters types match exactly the args types
    private static Method bestMethod(Class<?> receiverType,
                                     String name,
                                     Class<?>... argsType) throws NoSuchMethodException {
        try {
            return receiverType.getMethod(name, argsType);
        } catch (NoSuchMethodException e) {
            return getMostSpecificMethod(receiverType, name, argsType);
        }
    }

    // Filter and sort the methods according the specification project to return the most specific one
    private static Method getMostSpecificMethod(Class<?> receiverType,
                                                String name,
                                                Class<?>[] argsType) throws NoSuchMethodException {
        return Arrays.stream(receiverType.getMethods())
                .filter(method -> method.getName().equals(name))
                .filter(method -> method.getParameterTypes().length == argsType.length || method.isVarArgs())
                .filter(method -> checkIfMethodsParamsAreCompatible(method, argsType))
                .min((receiverTypeHierarchyComparator)
                        .thenComparing(argsTypeHierarchyComparator)
                        .thenComparing(getArgsTypeInterfaceHierarchyComparator(argsType)))
                .orElseThrow(NoSuchMethodException::new);
    }

    // Call the method parameters validator according the method is varargs or not
    static boolean checkIfMethodsParamsAreCompatible(Method method, Class<?>... argsType) {
        int numberOfArgs = method.getParameterTypes().length;
        return method.isVarArgs() ?
                checkIfVarArgsMethodParamsAreCompatible(method, argsType) :
                argsAreCompatibleWithMethodParams(method, numberOfArgs, argsType);
    }

    // Validate varargs method parameters including the possibility of being a method
    // with varargs and non varargs parameters
    private static boolean checkIfVarArgsMethodParamsAreCompatible(Method method,
                                                                   Class<?>... argsType) {
        int methodParams = method.getParameterTypes().length;
        if (methodParams == 1) {
            return argsAreCompatibleWithMethodVarargsComponentType(method, argsType.length, argsType);
        } else {
            int numberOfNonVarargs = methodParams - 1;
            int numberOfVarargs = argsType.length - numberOfNonVarargs;
            return argsAreCompatibleWithMethodParams(method, numberOfNonVarargs, argsType)
                    && argsAreCompatibleWithMethodVarargsComponentType(method, numberOfVarargs, argsType);
        }
    }

    // validate non varargs method parameters according arguments.It accepts all the types that
    // are extended or implemented by arg type
    private static boolean argsAreCompatibleWithMethodParams(Method method,
                                                             int numberOfArgs,
                                                             Class<?>[] argsType) {
        return IntStream
                .range(0, numberOfArgs)
                .allMatch(i -> method.getParameterTypes()[i].isAssignableFrom(argsType[i]));
    }

    // validate varargs method parameters according arguments
    private static boolean argsAreCompatibleWithMethodVarargsComponentType(Method method,
                                                                           int numberOfVarargs,
                                                                           Class<?>[] argsType) {
        Class<?> varargsComponentType = method
                .getParameterTypes()[method.getParameterTypes().length - 1]
                .getComponentType();

        return IntStream
                .range(argsType.length - numberOfVarargs, argsType.length)
                .allMatch(i -> varargsComponentType.isAssignableFrom(argsType[i]));
    }

    // Compare two methods according the args types hierarchy including varargs methods. In same circumstances, a non varargs method
    // will be more specific then a varargs method
    static Comparator<Method> argsTypeHierarchyComparator = (m1, m2) -> {
        int m1Params = m1.getParameterTypes().length;
        int m2Params = m2.getParameterTypes().length;
        for (int i = 0; i < Integer.max(m1Params, m2Params); i++) {

            Class<?> c1 = getParameterType(m1, m1Params, i);
            Class<?> c2 = getParameterType(m2, m2Params, i);

            boolean c1IsSubType = c2.isAssignableFrom(c1);
            boolean c2IsSubtype = c1.isAssignableFrom(c2);

            if (c1IsSubType && !c2IsSubtype)
                return -1;
            else if (c2IsSubtype && !c1IsSubType)
                return 1;
        }
        if (m1.isVarArgs() && !m2.isVarArgs())
            return 1;
        if (!m1.isVarArgs() && m2.isVarArgs())
            return -1;
        return 0;
    };

    // Compare each method parameter with interfaces implemented by the argument, giving priority to
    // the one that is subtype of the other and it is implemented first on argument type class
    static Comparator<Method> getArgsTypeInterfaceHierarchyComparator(Class<?>[] argsType) {
        return ((m1, m2) -> {
            for (int i = 0; i < argsType.length; i++) {
                for (Class<?> classInterface : getAllInterfaces(argsType[i].getInterfaces())) {
                    if (paramTypeEqualsToArgInterfaceType(m1, i, classInterface) &&
                            !paramTypeEqualsToArgInterfaceType(m2, i, classInterface))
                        return -1;
                    if (!paramTypeEqualsToArgInterfaceType(m1, i, classInterface) &&
                            paramTypeEqualsToArgInterfaceType(m2, i, classInterface))
                        return 1;
                }
            }
            return 0;
        });
    }

    private static boolean paramTypeEqualsToArgInterfaceType(Method m, int i, Class<?> classInterface) {
        return m.getParameterTypes()[i].equals(classInterface);
    }

    // recursively return all interfaces implemented
    public static Class<?>[] getAllInterfaces(Class<?>[] interfaces) {
        return Arrays
                .stream(interfaces)
                .flatMap(in -> Stream
                        .concat(Stream.of(in), Stream.of(getAllInterfaces(in.getInterfaces()))))
                .distinct()
                .sorted(classHierarchyComparator)
                .toArray(Class[]::new);
    }

    // compare two types according if one is subtype of the other
    static Comparator<Class<?>> classHierarchyComparator = (c1, c2) -> {
        boolean c1IsSubType = c2.isAssignableFrom(c1);
        boolean c2IsSubType = c1.isAssignableFrom(c2);
        if (c1IsSubType && !c2IsSubType)
            return -1;
        if (!c1IsSubType && c2IsSubType)
            return 1;
        return 0;
    };


    // Return the nth parameter type according if it is a varargs method or not
    private static Class<?> getParameterType(Method method, int methodParams, int n) {
        Class<?> c1;
        if (method.isVarArgs()) {
            c1 = isMethodLastParameter(n, method) ?
                    method.getParameterTypes()[methodParams - 1].getComponentType() :
                    method.getParameterTypes()[n];
        } else {
            c1 = method.getParameterTypes()[n];
        }
        return c1;
    }

    // Used in varargs methods when they are being compared with non varargs methods and
    // it is needed to extend the varargs component type through the iterations
    static boolean isMethodLastParameter(int n, Method method) {
        return n >= method.getParameterTypes().length - 1;
    }

    // Compare two methods according their declaring class.
    static Comparator<Method> receiverTypeHierarchyComparator = (m1, m2) -> {
        Class<?> c1 = m1.getDeclaringClass();
        Class<?> c2 = m2.getDeclaringClass();
        boolean c1IsSubType = c2.isAssignableFrom(c1);
        boolean c2IsSubtype = c1.isAssignableFrom(c2);
        if (c1IsSubType && !c2IsSubtype)
            return -1;
        if (c2IsSubtype && !c1IsSubType)
            return 1;
        return 0;
    };

}
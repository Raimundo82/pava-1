package ist.meic.pava.multipledispatchextended;


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

    private UsingMultipleDispatchExtended() {
    }

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

    public static class MultipleDispatchExtendedException extends RuntimeException {
        private static final String NO_METHOD_AVAILABLE = "\nNo Method found with name [%s], on class [%s], compatible with parameters: %s";


        public MultipleDispatchExtendedException(String receiver, String name, String types, Throwable t) {
            super(String.format(NO_METHOD_AVAILABLE, receiver, name, types), t);
        }
    }

    // Map arguments values to its types in order to find an applicable and most specific method
    public static void invoke(Object receiver, String name, Object... args) {
        args = args == null ? new Object[1] : args;
        Class<?>[] argsTypes;

        // First it tries to find and call the most specific method with primitive types
        argsTypes = Arrays.stream(args)
                .map(arg -> arg == null ? Object.class : arg.getClass())
                .map(type -> isWrappedType(type) ? WRAPPER_TO_PRIMITIVE.get(type) : type)
                .toArray(Class[]::new);

        try {
            invokeMethod(receiver, name, argsTypes, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
            // Then, if a exception is caught, it retries it with no primitive types
            argsTypes = Arrays.stream(args)
                    .map(o -> o == null ? Object.class : o.getClass())
                    .toArray(Class[]::new);

            try {
                invokeMethod(receiver, name, argsTypes, args);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                throw new MultipleDispatchExtendedException(
                        name,
                        receiver.getClass().getName(),
                        Arrays.toString(Stream.of(argsTypes).map(Class::getName).toArray()),
                        ex);
            }
        }
    }

    private static boolean isWrappedType(Class<?> type) {
        return WRAPPER_TO_PRIMITIVE.containsKey(type);
    }

    // Invoke a non varargs method or call the specific method do deal with varargs method
    private static void invokeMethod(Object receiver,
                                     String name,
                                     Class<?>[] argsTypes,
                                     Object[] args
    ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = bestMethod(receiver.getClass(), name, argsTypes);

        if (method.isVarArgs()) {
            varargsMethodInvoke(receiver, args, method);
            return;
        }
        method.invoke(receiver, args);
    }

    // Build and fill the array(s) needed to invoke a varargs method
    private static void varargsMethodInvoke(Object receiver,
                                            Object[] args,
                                            Method method) throws IllegalAccessException, InvocationTargetException {

        int methodParams = method.getParameterCount();
        Class<?> arrayComponentType = method.getParameterTypes()[methodParams - 1].getComponentType();
        Object varargsArray = Array.newInstance(arrayComponentType, args.length - methodParams + 1);

        IntStream
               .range(0, args.length - methodParams + 1)
               .forEach(i -> Array.set(varargsArray, i, args[methodParams - 1 + i]));
       Object[] newArgs = (Object[]) Array.newInstance(Object.class, methodParams);
       IntStream
               .range(0, methodParams - 1)
               .forEach(i -> Array.set(newArgs, i, args[i]));
        Array.set(newArgs, methodParams - 1, varargsArray);
        method.invoke(receiver, newArgs);
     }
    

    // Get a method from receiver type or from its superclasses types, whom parameters types match exactly the
    // arguments types
    private static Method bestMethod(Class<?> receiverType,
                                     String name,
                                     Class<?>... argsType) throws NoSuchMethodException {


        try {
            return receiverType.getMethod(name, argsType);
        } catch (NoSuchMethodException e) {
            return getMostSpecificMethod(receiverType, name, argsType);
        }
    }

    // Filter and sort the methods according the project specification and return the most specific one
    private static Method getMostSpecificMethod(Class<?> receiverType,
                                                String name,
                                                Class<?>[] argsType) throws NoSuchMethodException {
        return Arrays.stream(receiverType.getMethods())
                .filter(method -> method.getName().equals(name))
                .filter(method -> method.getParameterCount() == argsType.length ||
                        (method.isVarArgs() && argsType.length >= method.getParameterCount() - 1))
                .filter(method -> isMethodApplicable(method, argsType))
                .min((receiverTypeHierarchyComparator)
                        .thenComparing(parameterTypesHierarchyComparator(false))
                        .thenComparing(parameterTypesHierarchyComparator(true))
                        .thenComparing(sameLevelHierarchyTypesComparator(argsType)))
                .orElseThrow(NoSuchMethodException::new);
    }

    // Call the method parameters validator according if it is or not varargs
    private static boolean isMethodApplicable(Method method, Class<?>... argsType) {
        int numberOfArgs = method.getParameterCount();
        return method.isVarArgs() ?
                isVarargsMethodApplicable(method, numberOfArgs, argsType) :
                isNonVarargsMethodApplicable(method, numberOfArgs, argsType);
    }

    // Validate varargs method parameters including the possibility of being a method
    // with varargs and non varargs parameters
    private static boolean isVarargsMethodApplicable(Method method,
                                                     int methodParams,
                                                     Class<?>... argsType) {
        if (methodParams == 1) {
            return isVarargsComponentTypeApplicable(method, argsType.length, argsType);
        } else {
            int numberOfNonVarargs = methodParams - 1;
            int numberOfVarargs = argsType.length - numberOfNonVarargs;
            return isNonVarargsMethodApplicable(method, numberOfNonVarargs, argsType)
                    && isVarargsComponentTypeApplicable(method, numberOfVarargs, argsType);
        }
    }

    // validate non varargs method parameters according arguments.It accepts all the types that
    // are extended or implemented by arg type
    private static boolean isNonVarargsMethodApplicable(Method method,
                                                        int numberOfArgs,
                                                        Class<?>[] argsType) {
        return IntStream
                .range(0, numberOfArgs)
                .allMatch(i -> method.getParameterTypes()[i].isAssignableFrom(argsType[i]));
    }

    // validate varargs method parameters according arguments
    private static boolean isVarargsComponentTypeApplicable(Method method,
                                                            int numberOfVarargs,
                                                            Class<?>[] argsType) {
        Class<?> varargsComponentType = method
                .getParameterTypes()[method.getParameterCount() - 1]
                .getComponentType();

        return IntStream
                .range(argsType.length - numberOfVarargs, argsType.length)
                .allMatch(i -> varargsComponentType.isAssignableFrom(argsType[i]));
    }

    // Parameter method comparator that works bottom up left to right order when !isInInterfaceMode and,
    // in same hierarchy level of all parameters, gives priority to non varargs methods, like is done
    // in java compile time. When isInInterfaceMode ,it works in top down left right order, to find the
    // method with the most higher hierarchy interface that are implemented by the arguments.
    private static Comparator<Method> parameterTypesHierarchyComparator(boolean isInInterfaceMode) {
        return ((m1, m2) -> {
            if (!m1.isVarArgs() && m2.isVarArgs()) {
                return -1;
            }
            if (m1.isVarArgs() && !m2.isVarArgs()) {
                return 1;
            }
            for (int i = 0; i < m1.getParameterCount(); i++) {
                Class<?> c1 = m1.getParameterTypes()[i];
                Class<?> c2 = m2.getParameterTypes()[i];

                if (!isInInterfaceMode) {
                    if (c1.isInterface() && c2.isInterface())
                        return 0;
                    if (!c1.isInterface() && c2.isInterface())
                        return -1;
                    if (c1.isInterface())
                        return 1;
                }

                boolean c1IsSubType = c2.isAssignableFrom(c1);
                boolean c2IsSubtype = c1.isAssignableFrom(c2);

                if (c1IsSubType && !c2IsSubtype)
                    return isInInterfaceMode ? 1 : -1;
                else if (c2IsSubtype && !c1IsSubType)
                    return isInInterfaceMode ? -1 : 1;
            }
            return 0;
        });
    }

    // Compare each method parameter with interfaces implemented by the argument,
    // giving priority to the one that is implemented first on argument type class
    private static Comparator<Method> sameLevelHierarchyTypesComparator(Class<?>[] argsType) {
        return ((m1, m2) -> {
            for (int i = 0; i < argsType.length; i++) {
                Class<?>[] allInterfaces = getAllInterfaces(argsType[i].getInterfaces());
                for (Class<?> classInterface : allInterfaces) {
                    if (isParamTypeEqualsToArgType(m1, i, classInterface) &&
                            !isParamTypeEqualsToArgType(m2, i, classInterface))
                        return -1;
                    if (!isParamTypeEqualsToArgType(m1, i, classInterface) &&
                            isParamTypeEqualsToArgType(m2, i, classInterface))
                        return 1;
                }
            }
            return 0;
        });
    }

    private static boolean isParamTypeEqualsToArgType(Method m, int i, Class<?> interfaceType) {
        Class<?> parameterType = m.getParameterTypes()[i];
        return parameterType.equals(interfaceType);
    }

    // recursively return all interfaces implemented
    private static Class<?>[] getAllInterfaces(Class<?>[] interfaces) {

        return Arrays.stream(interfaces)
                .flatMap(in -> Stream
                        .concat(Stream.of(in), Stream.of(getAllInterfaces(in.getInterfaces()))))
                .distinct()
                .sorted(classHierarchyComparator)
                .toArray(Class[]::new);
    }

    // compare two types according if one is subtype of the other
    private static final Comparator<Class<?>> classHierarchyComparator = (c1, c2) -> {
        boolean c1IsSubType = c2.isAssignableFrom(c1);
        boolean c2IsSubType = c1.isAssignableFrom(c2);
        if (c1IsSubType && !c2IsSubType)
            return -1;
        if (!c1IsSubType && c2IsSubType)
            return 1;
        return 0;
    };

    // Compare two methods according their declaring class.
    private static final Comparator<Method> receiverTypeHierarchyComparator = (m1, m2) -> {
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

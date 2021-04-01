package ist.meic.pava.MultipleDispatchExtended;


import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

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

        // First check if it is possible to call a method with primitive types
        if (hasWrappedClasses(args)) {
            Class<?>[] argsPrimitiveTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++)
                argsPrimitiveTypes[i] = isWrapped(args[i]) ?
                        WRAPPER_TO_PRIMITIVE.get(args[i].getClass()) :
                        args[i].getClass();
            try {
                return invokeMethod(receiver, name, argsPrimitiveTypes, args);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
            }
        }

        Class<?>[] argsTypes = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
        try {
            return invokeMethod(receiver, name, argsTypes, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Object invokeMethod(Object receiver, String name, Class<?>[] argsTypes, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = bestMethod(receiver.getClass(), name, argsTypes);

        if (method.isVarArgs()) {
            return varargsMethodInvoke(receiver, args, method);
        }
        return method.invoke(receiver, args);
    }

    private static Object varargsMethodInvoke(Object receiver, Object[] args, Method method) throws IllegalAccessException, InvocationTargetException {
        int methodParams = method.getParameterTypes().length;
        Class<?> arrayComponentType = method.getParameterTypes()[methodParams - 1].getComponentType();
        Object varargsArray = Array.newInstance(arrayComponentType, args.length - methodParams + 1);
        if (methodParams == 1) {
            IntStream.range(0, args.length).forEach(i -> Array.set(varargsArray, i, args[i]));
            return method.invoke(receiver, varargsArray);
        } else {
            IntStream.range(0, args.length - methodParams + 1).forEach(i -> Array.set(varargsArray, i, args[args.length - methodParams + i]));
            Object[] newArgs = (Object[]) Array.newInstance(Object.class, methodParams);
            IntStream.range(0, methodParams - 1).forEach(i -> Array.set(newArgs, i, args[i]));
            Array.set(newArgs, methodParams - 1, varargsArray);
            return method.invoke(receiver, newArgs);
        }
    }

    private static Method bestMethod(Class<?> receiverType, String name, Class<?>... argsType) throws
            NoSuchMethodException {
        try {
            return receiverType.getMethod(name, argsType);
        } catch (NoSuchMethodException e) {
            return getMostSpecificMethod(receiverType, name, argsType);
        }
    }

    private static Method getMostSpecificMethod(Class<?> receiverType, String name, Class<?>[] argsType) throws
            NoSuchMethodException {
        return Arrays.stream(receiverType.getMethods())
                .filter(method -> method.getName().equals(name))
                .filter(method -> method.getParameterTypes().length == argsType.length || method.isVarArgs())
                .filter(method -> checkIfMethodArgsAreValid(method, argsType))
                .min(argsTypeHierarchyComparator.thenComparing(receiverTypeHierarchyComparator))
                .orElseThrow(NoSuchMethodException::new);
    }

    static boolean checkIfMethodArgsAreValid(Method method, Class<?>... argsType) {
        int numberOfArgs = method.getParameterTypes().length;
        return method.isVarArgs() ?
                checkIfVarArgsMethodParamsAreValid(method, argsType) :
                argsAreCompatibleWithMethodParams(method, numberOfArgs, argsType);
    }

    private static boolean checkIfVarArgsMethodParamsAreValid(Method method, Class<?>... argsType) {
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

    private static boolean argsAreCompatibleWithMethodParams(Method method, int numberOfArgs, Class<?>[] argsType) {
        return IntStream
                .range(0, numberOfArgs)
                .allMatch(i -> method.getParameterTypes()[i].isAssignableFrom(argsType[i]));
    }


    private static boolean argsAreCompatibleWithMethodVarargsComponentType(Method method, int numberOfVarargs, Class<?>[]
            argsType) {
        Class<?> varargsComponentType = method.getParameterTypes()[method.getParameterTypes().length - 1].getComponentType();
        return IntStream
                .range(argsType.length - numberOfVarargs, argsType.length)
                .allMatch(i -> varargsComponentType.isAssignableFrom(argsType[i]));
    }


    static Comparator<Method> argsTypeHierarchyComparator = (m1, m2) -> {
        if (m1.isVarArgs() && !m2.isVarArgs()) return -1;
        if (!m1.isVarArgs() && m2.isVarArgs()) return 1;
        for (int i = 0; i < m1.getParameterTypes().length; i++) {

            Class<?> c1 = m1.isVarArgs() && isMethodLastParameter(i, m1) ?
                    m1.getParameterTypes()[i].getComponentType() :
                    m1.getParameterTypes()[i];

            Class<?> c2 = m2.isVarArgs() && isMethodLastParameter(i, m2) ?
                    m2.getParameterTypes()[i].getComponentType() :
                    m2.getParameterTypes()[i];

            boolean c1IsSubType = c2.isAssignableFrom(c1);
            boolean c2IsSubtype = c1.isAssignableFrom(c2);

            if (c1IsSubType && !c2IsSubtype)
                return -1;
            else if (c2IsSubtype && !c1IsSubType)
                return 1;
        }
        return 0;
    };

    static boolean isMethodLastParameter(int i, Method method) {
        return method.getParameterTypes()[i] == method.getParameterTypes()[method.getParameterTypes().length - 1];
    }

    static Comparator<Method> receiverTypeHierarchyComparator = (m1, m2) -> {
        Class<?> c1 = m1.getDeclaringClass();
        Class<?> c2 = m2.getDeclaringClass();
        boolean b1 = c1.isAssignableFrom(c2);
        boolean b2 = c2.isAssignableFrom(c1);
        if (b1)
            return 1;
        if (b2)
            return -1;
        return 0;
    };

    private static boolean hasWrappedClasses(Object... args) {
        return Arrays.stream(args)
                .anyMatch(arg -> WRAPPER_TO_PRIMITIVE.containsKey(arg.getClass()));
    }

    private static boolean isWrapped(Object arg) {
        return WRAPPER_TO_PRIMITIVE.containsKey(arg.getClass());
    }

}


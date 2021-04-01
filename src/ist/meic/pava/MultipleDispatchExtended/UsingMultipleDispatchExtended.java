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
            int methodParams = method.getParameterTypes().length;
            Object newVarArgs = Array.newInstance(method.getParameterTypes()[methodParams - 1].getComponentType(), args.length - methodParams + 1);
            if (methodParams == 1) {
                IntStream.range(0, args.length).forEach(i -> Array.set(newVarArgs, i, args[i]));
                return method.invoke(receiver, newVarArgs);
            } else {
                IntStream.range(0, args.length - methodParams + 1).forEach(i -> Array.set(newVarArgs, i, args[args.length - methodParams + i]));
                Object[] newArgs = (Object[]) Array.newInstance(Object.class, methodParams);
                IntStream.range(0, methodParams - 1).forEach(i -> Array.set(newArgs, i, args[i]));
                Array.set(newArgs, methodParams - 1, newVarArgs);
                return method.invoke(receiver, newArgs);
            }
        }
        return method.invoke(receiver, args);
    }

    private static Method bestMethod(Class<?> receiverType, String name, Class<?>... argsType) throws
            NoSuchMethodException {
        try {
            return receiverType.getMethod(name, argsType);

        } catch (NoSuchMethodException e) {
            Method mostSpecificMethod = getMostSpecificMethod(receiverType, name, argsType);
            return bestMethod(receiverType, name, mostSpecificMethod.getParameterTypes());
        }
    }

    private static Method getMostSpecificMethod(Class<?> receiverType, String name, Class<?>[] argsType) throws
            NoSuchMethodException {
        return Arrays.stream(receiverType.getMethods())
                .filter(method -> method.getName().equals(name))
                .filter(method -> method.getParameterTypes().length == argsType.length || method.isVarArgs())
                .filter(method -> checkIfMethodArgsAreValid(method, argsType))
                .min(compHierarchyArgs.thenComparing(compHierarchyDeclaringClass))
                .orElseThrow(NoSuchMethodException::new);
    }

    static boolean checkIfMethodArgsAreValid(Method method, Class<?>... argsType) {
        int numberOfArgs = method.getParameterTypes().length;
        return method.isVarArgs() ?
                checkIfVarArgsMethodParamsAreValid(method, argsType) :
                argsAreCompatibleWithMethodParams(method, numberOfArgs, argsType);
    }

    private static boolean argsAreCompatibleWithMethodParams(Method method, int numberOfArgs, Class<?>[] argsType) {
        return IntStream
                .range(0, numberOfArgs)
                .allMatch(i -> method.getParameterTypes()[i].isAssignableFrom(argsType[i]));
    }

    private static boolean checkIfVarArgsMethodParamsAreValid(Method method, Class<?>... argsType) {
        int methodParams = method.getParameterTypes().length;
        if (methodParams == 1) {
            return checkVarArgsTypes(method, argsType.length, argsType);

        } else {
            int numberOfArgs = methodParams - 1;
            int numberOfVarArgs = argsType.length - numberOfArgs;
            return argsAreCompatibleWithMethodParams(method, numberOfArgs, argsType)
                    && checkVarArgsTypes(method, numberOfVarArgs, argsType);

        }
    }

    private static boolean checkVarArgsTypes(Method method, int numberOfVarArgs, Class<?>... argsType) {
        if (varArgsTypesAreAllEqual(argsType, numberOfVarArgs)) {
            if (IntStream
                    .range(argsType.length - numberOfVarArgs, argsType.length)
                    .allMatch(i -> argsType[i].isAssignableFrom(Number.class) ||
                            argsType[i].isAssignableFrom(Character.class) ||
                            argsType[i].isAssignableFrom(Boolean.class))) {
                return checkIfAreArgsFromSuperReferenceTypes(method, numberOfVarArgs, argsType);
            }
            return allArgsAreEqualToVarArgsElementsType(method, numberOfVarArgs, argsType);
        } else {
            return checkIfAreArgsFromSuperReferenceTypes(method, numberOfVarArgs, argsType);
        }
    }

    private static boolean checkIfAreArgsFromSuperReferenceTypes(Method method, int numberOfVarArgs, Class<?>...
            argsType) {
        if (IntStream
                .range(argsType.length - numberOfVarArgs, argsType.length)
                .allMatch(i -> argsType[i].isAssignableFrom(Number.class)))
            return allArgsAreAssignableFromNumberOrObject(method);

        if (IntStream
                .range(argsType.length - numberOfVarArgs, argsType.length)
                .allMatch(i -> argsType[i].isAssignableFrom(Character.class)))
            return allArgsAreAssignableFromCharacterOrObject(method);

        if (IntStream
                .range(argsType.length - numberOfVarArgs, argsType.length)
                .allMatch(i -> argsType[i].isAssignableFrom(Boolean.class)))
            return allArgsAreAssignableFromBooleanOrObject(method);
        return Stream.of(method.getParameterTypes()[0]).anyMatch(c -> c.equals(Object[].class));
    }

    private static boolean allArgsAreAssignableFromBooleanOrObject(Method method) {
        return Stream.of(method.getParameterTypes()[0])
                .anyMatch(c -> c.equals(Object[].class) || c.equals(Boolean[].class));
    }

    private static boolean allArgsAreAssignableFromCharacterOrObject(Method method) {
        return Stream.of(method.getParameterTypes()[0])
                .anyMatch(c -> c.equals(Object[].class) || c.equals(Character[].class));
    }

    private static boolean allArgsAreAssignableFromNumberOrObject(Method method) {
        return Stream.of(method.getParameterTypes()[0])
                .anyMatch(c -> c.equals(Object[].class) || c.equals(Number[].class));
    }

    private static boolean allArgsAreEqualToVarArgsElementsType(Method method, int numberOfVarArgs, Class<?>[]
            argsType) {
        return IntStream
                .range(argsType.length - numberOfVarArgs, argsType.length)
                .allMatch(i -> method.getParameterTypes()[method.getParameterTypes().length - 1].getComponentType().equals(argsType[i]));
    }

    private static boolean varArgsTypesAreAllEqual(Class<?>[] argsType, int numberOfVarArgs) {
        return IntStream
                .range(argsType.length - numberOfVarArgs, argsType.length)
                .allMatch(i -> argsType[i].equals(argsType[numberOfVarArgs - 1]));
    }


    static Comparator<Method> compHierarchyArgs = (m1, m2) -> {
        for (int i = 0; i < m1.getParameterTypes().length; i++) {
            Class<?> c1 = m1.getParameterTypes()[i];
            Class<?> c2 = m2.getParameterTypes()[i];
            boolean c1IsSubType = c2.isAssignableFrom(c1);
            boolean c2IsSubtype = c1.isAssignableFrom(c2);

            if (c1IsSubType && !c2IsSubtype)
                return -1;
            else if (c2IsSubtype && !c1IsSubType)
                return 1;
        }
        return 0;
    };

    static Comparator<Method> compHierarchyDeclaringClass = (m1, m2) -> {
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


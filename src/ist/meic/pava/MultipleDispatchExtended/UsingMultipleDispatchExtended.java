package ist.meic.pava.MultipleDispatchExtended;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
        if (checkIfAllArgsArePrimitive(args)) {
            Class<?>[] argsPrimitiveTypes = IntStream
                    .iterate(0, n -> n + 1)
                    .limit(args.length).mapToObj(i -> WRAPPER_TO_PRIMITIVE.get(args[i].getClass()))
                    .toArray(Class[]::new);

            try {
                Method method = bestMethod(receiver.getClass(), name, argsPrimitiveTypes);
                return method.invoke(receiver, args);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored){}
        }

        Class<?>[] argsTypes = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
        try {
            Method method = bestMethod(receiver.getClass(), name, argsTypes);
            return method.invoke(receiver, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }


    private static Method bestMethod(Class<?> receiverType, String name, Class<?>... argsType) throws NoSuchMethodException {
        try {
            return receiverType.getMethod(name, argsType);

        } catch (NoSuchMethodException e) {
            Method mostSpecificMethod = getMostSpecificMethod(receiverType, name, argsType);
            return bestMethod(receiverType, name, mostSpecificMethod.getParameterTypes());
        }
    }

    private static Method getMostSpecificMethod(Class<?> receiverType, String name, Class<?>[] argsType) throws NoSuchMethodException {
        return Arrays.stream(receiverType.getDeclaredMethods())
                .filter(method -> method.getName().equals(name))
                .filter(method -> method.getParameterTypes().length == argsType.length)
                .filter(method -> Modifier.toString(method.getModifiers()).equals("public"))
                .filter(method -> checkIfMethodArgsAreValid(method, argsType))
                .min(getMethodComparator())
                .orElseThrow(NoSuchMethodException::new);
    }

    private static boolean checkIfMethodArgsAreValid(Method method, Class<?>... argsType) {
        int numberOfArgs = method.getParameterTypes().length;
        return method.isVarArgs() || IntStream
                .range(0, numberOfArgs)
                .allMatch(i -> method.getParameterTypes()[i].isAssignableFrom(argsType[i]));
    }

    private static Comparator<Method> getMethodComparator() {
        return (m1, m2) -> {
            for (int i = 0; i < m1.getParameterTypes().length; i++) {
                Class<?> parameterTypeOne = m1.getParameterTypes()[i];
                Class<?> parameterTypeTwo = m2.getParameterTypes()[i];
                boolean b1 = parameterTypeTwo.isAssignableFrom(parameterTypeOne);
                boolean b2 = parameterTypeOne.isAssignableFrom(parameterTypeTwo);
                if (parameterTypeOne == parameterTypeTwo)
                    continue;
                if (b1 && !b2)
                    return -1;
                else if (!b1 && b2)
                    return 1;
            }
            return 0;
        };
    }

    private static boolean checkIfAllArgsArePrimitive(Object... args) {
        return Arrays.stream(args)
                .allMatch(arg -> WRAPPER_TO_PRIMITIVE.containsKey(arg.getClass()));
    }
}


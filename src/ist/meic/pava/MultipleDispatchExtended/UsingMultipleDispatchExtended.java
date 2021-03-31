package ist.meic.pava.MultipleDispatchExtended;


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
                Method method = bestMethod(receiver.getClass(), name, argsPrimitiveTypes);
                return method.invoke(receiver, args);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
            }
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
        return Arrays.stream(receiverType.getMethods())
                .filter(method -> method.getName().equals(name))
                .filter(method -> method.getParameterTypes().length == argsType.length)
                .filter(method -> checkIfMethodArgsAreValid(method, argsType))
                .min(compHierarchyArgs.thenComparing(compHierarchyDeclaringClass))
                .orElseThrow(NoSuchMethodException::new);
    }

    private static boolean checkIfMethodArgsAreValid(Method method, Class<?>... argsType) {
        int numberOfArgs = method.getParameterTypes().length;
        return method.isVarArgs() || IntStream
                .range(0, numberOfArgs)
                .allMatch(i -> method.getParameterTypes()[i].isAssignableFrom(argsType[i]));
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


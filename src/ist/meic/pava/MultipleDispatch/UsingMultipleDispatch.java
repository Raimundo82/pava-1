package ist.meic.pava.MultipleDispatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

public class UsingMultipleDispatch {


    public static Object invoke(Object receiver, String name, Object... args) {

        Class<?>[] argsType = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);
        try {
            Method method = bestMethod(receiver.getClass(), name, argsType);
            return method.invoke(receiver, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
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
    }




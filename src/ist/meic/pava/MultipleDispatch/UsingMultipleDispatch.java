package ist.meic.pava.MultipleDispatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;

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
        return Arrays.stream(receiverType.getDeclaredMethods())
                .filter(method -> method.getName().equals(name))
                .filter(method -> Modifier.toString(method.getModifiers()).equals("public"))
                .filter(method -> checkIfMethodArgsAreValid(method, argsType))
                .filter(method -> method.getParameterTypes().length == argsType.length)
                .min(getMethodComparator())
                .orElseThrow(NoSuchMethodException::new);
    }

    private static boolean checkIfMethodArgsAreValid(Method method, Class<?>... argsType) {
        if (!method.isVarArgs()) {
            for (int i = 0; i < method.getParameterTypes().length; i++) {
                if (!method.getParameterTypes()[i].isAssignableFrom(argsType[i]))
                    return false;
            }
            return true;
        }
        return true;
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
                    return 1;
                else if (!b1 && b2)
                    return -1;
            }
            return 0;
        };
    }
}


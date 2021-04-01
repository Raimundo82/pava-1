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
            return getMostSpecificMethod(receiverType, name, argsType);
        }
    }

    private static Method getMostSpecificMethod(Class<?> receiverType, String name, Class<?>[] argsType) throws NoSuchMethodException {
        return Arrays.stream(receiverType.getMethods())
                .filter(method -> method.getName().equals(name))
                .filter(method -> method.getParameterTypes().length == argsType.length)
                .filter(method -> checkIfMethodsParamsAreCompatible(method, argsType))
                .min(receiverTypeHierarchyComparator.thenComparing(argsTypeHierarchyComparator))
                .orElseThrow(NoSuchMethodException::new);
    }

    private static boolean checkIfMethodsParamsAreCompatible(Method method, Class<?>... argsType) {
        int numberOfArgs = method.getParameterTypes().length;
        return IntStream
                .range(0, numberOfArgs)
                .allMatch(i -> method.getParameterTypes()[i].isAssignableFrom(argsType[i]));
    }

    // compare the in terms of hierarchy of t
    static Comparator<Method> argsTypeHierarchyComparator = (m1, m2) -> {
        for (int i = 0; i < m1.getParameterTypes().length; i++) {
            Class<?> c1 = m1.getParameterTypes()[i];
            Class<?> c2 = m2.getParameterTypes()[i];
            boolean c1IsSubType = c2.isAssignableFrom(c1);
            boolean c2IsSubtype = c1.isAssignableFrom(c2);

            if (c1IsSubType && !c2IsSubtype)
                return -1;
            if (c2IsSubtype && !c1IsSubType)
                return 1;
        }
        return 0;
    };

    static Comparator<Method> receiverTypeHierarchyComparator = (m1, m2) -> {
        Class<?> c1 = m1.getDeclaringClass();
        Class<?> c2 = m2.getDeclaringClass();
        boolean c1IsSubType = c1.isAssignableFrom(c2);
        boolean c2IsSubtype = c2.isAssignableFrom(c1);
        if (c1IsSubType && !c2IsSubtype)
            return 1;
        if (c2IsSubtype && !c1IsSubType)
            return -1;
        return 0;
    };
}




package ist.meic.pava.multipledispatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class UsingMultipleDispatch {

    private UsingMultipleDispatch() {
    }

    public static class MultipleDispatchException extends RuntimeException {
        private static final String NO_METHOD_AVAILABLE = "\nNo Method found with name [%s]," +
                "on class [%s], compatible with parameters: %s";

        public MultipleDispatchException(String receiver, String name, String types, Throwable t) {
            super(String.format(NO_METHOD_AVAILABLE, receiver, name, types), t);
        }
    }

    // Map arguments values to its types in order to find an applicable and most specific method
    public static void invoke(Object receiver, String name, Object... args) {

        args = args == null ? new Object[1] : args;
        Class<?>[] argsTypes = Arrays.stream(args)
                .map(arg -> arg == null ? Object.class : arg.getClass())
                .toArray(Class[]::new);

        try {
            Method method = bestMethod(receiver.getClass(), name, argsTypes);
            method.invoke(receiver, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            throw new MultipleDispatchException(
                    name,
                    receiver.getClass().getName(),
                    Arrays.toString(Stream.of(argsTypes).map(Class::getName).toArray()),
                    ex);
        }
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
                .filter(method -> method.getParameterCount() == argsType.length)
                .filter(method -> isMethodApplicable(method, argsType))
                .min(receiverTypeHierarchyComparator.thenComparing(argsTypeHierarchyComparator))
                .orElseThrow(NoSuchMethodException::new);
    }

    // Call the method parameters validator
    private static boolean isMethodApplicable(Method method, Class<?>... argsType) {
        int numberOfArgs = method.getParameterCount();
        Class<?>[] parameterTypes = method.getParameterTypes();
        return IntStream
                .range(0, numberOfArgs)
                .allMatch(i -> parameterTypes[i].isAssignableFrom(argsType[i])
                        && !parameterTypes[i].isInterface());
    }


    // Compare two methods according the arguments types hierarchy
    static Comparator<Method> argsTypeHierarchyComparator = (m1, m2) -> {
        for (int i = 0; i < m1.getParameterCount(); i++) {
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
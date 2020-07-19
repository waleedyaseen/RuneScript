package me.waliedyassen.runescript.util;

/**
 * Contains various utilities for reflection api.
 *
 * @author Walied K. Yassen
 */
public final class ReflectionUtil {

    /**
     * Returns the boxed variant of the specified class type.
     *
     * @param type
     *         the class type that we want the boxed variant for.
     *
     * @return the boxed variant class type or the same class type if there is no boxed variant.
     */
    public static Class<?> box(Class<?> type) {
        if (type.isPrimitive()) {
            if (type == byte.class) {
                type = Byte.class;
            } else if (type == short.class) {
                type = Short.class;
            } else if (type == int.class) {
                type = Integer.class;
            } else if (type == long.class) {
                type = Long.class;
            } else if (type == boolean.class) {
                type = Boolean.class;
            } else if (type == char.class) {
                type = Character.class;
            } else {
                throw new IllegalStateException("Unrecognized primitive class type type: " + type.getSimpleName());
            }
        }
        return type;
    }

    private ReflectionUtil() {
        // NOOP
    }
}


package me.waliedyassen.runescript.config.type;

import lombok.Data;
import me.waliedyassen.runescript.type.PrimitiveType;

/**
 * A basic implementation of the {@link ConfigVarType} interface.
 *
 * @author Walied K. Yassen
 */
@Data
public final class BasicConfigVarType implements ConfigVarType {

    /**
     * The native type of the configuration type.
     */
    private final Class<?> nativeType;

    /**
     * The components that made up one entity of the type
     */
    private final PrimitiveType[] components;
}

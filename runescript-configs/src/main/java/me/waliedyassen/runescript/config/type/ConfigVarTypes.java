package me.waliedyassen.runescript.config.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.type.PrimitiveType;

/**
 * Contains most of the common {@link ConfigVarType} types.
 *
 * @author Walied K. Yassen
 */
@Getter
@RequiredArgsConstructor
public enum ConfigVarTypes implements ConfigVarType {
    STRING(String.class, new PrimitiveType[]{PrimitiveType.STRING});


    /**
     * The native type of the type.
     */
    private final Class<?> nativeType;

    /**
     * The components that made up this type.
     */
    private final PrimitiveType[] components;
}

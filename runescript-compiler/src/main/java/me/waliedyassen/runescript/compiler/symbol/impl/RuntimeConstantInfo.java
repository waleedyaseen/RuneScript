package me.waliedyassen.runescript.compiler.symbol.impl;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.symbol.Symbol;
import me.waliedyassen.runescript.type.PrimitiveType;

/**
 * The symbol information for a runtime constant value, which is a constant that is replaced at runtime with another value.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class RuntimeConstantInfo extends Symbol {

    /**
     * The name of the constant.
     */
    private final String name;

    /**
     * The type of the constant.
     */
    private final PrimitiveType type;

    /**
     * The value of the constant.
     */
    private final Object value;
}

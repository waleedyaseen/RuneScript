package me.waliedyassen.runescript.config.type.rule;

import me.waliedyassen.runescript.config.ast.AstProperty;
import me.waliedyassen.runescript.config.ast.value.AstValue;
import me.waliedyassen.runescript.config.ast.value.AstValueInteger;
import me.waliedyassen.runescript.config.semantics.typecheck.TypeChecking;

/**
 * A configuration rule that can be used for testing values.
 *
 * @author Walied K. Yassen
 */
public interface ConfigRule {

    /**
     * Tests the configuration value against this configuration rule.
     *
     * @param checking
     *         the type checker that we are using for the configuration.
     * @param property
     *         the property which the value lies in.
     * @param value
     *         the value that we are testing.
     */
    void test(TypeChecking checking, AstProperty property, AstValue value);

    /**
     * Resolves the specified {@link AstValue} to an integer.
     *
     * @param checking
     *         the type checking of the configuration.
     * @param value
     *         the value that we are checking.
     *
     * @return the integer value.
     *
     * @throws IllegalStateException
     *         if the specified value cannot be resolved to an integer.
     */
    static Integer resolveInteger(TypeChecking checking, AstValue value) {
        if (value instanceof AstValueInteger) {
            return ((AstValueInteger) value).getValue();
        } else {
            throw new IllegalStateException("Unrecognised value type: " + value);
        }
    }
}

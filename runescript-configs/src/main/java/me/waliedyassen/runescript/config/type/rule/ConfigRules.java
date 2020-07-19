
package me.waliedyassen.runescript.config.type.rule;

import lombok.var;
import me.waliedyassen.runescript.config.ast.AstProperty;
import me.waliedyassen.runescript.config.ast.value.AstValue;
import me.waliedyassen.runescript.config.ast.value.AstValueInteger;
import me.waliedyassen.runescript.config.semantics.SemanticError;
import me.waliedyassen.runescript.config.semantics.typecheck.TypeChecking;

/**
 * An enum of common rules for configuration properties.
 *
 * @author Walied K. Yassen
 */
public enum ConfigRules implements ConfigRule {

    /**
     * A rule that only allow positive integer values.
     */
    POSITIVE_ONLY {
        /**
         * {@inheritDoc}
         */
        @Override
        public void test(TypeChecking checking, AstProperty property, AstValue value) {
            var integer = resolveInteger(checking, value);
            if (integer < 1) {
                checking.getChecker().reportError(new SemanticError(value, "Expected a positive value for this component"));
            }
        }
    },

    /**
     * Emits the property with empty operands if the value is false.
     */
    EMIT_EMPTY_IF_FALSE,

    /**
     * Emits the property with empty operands if the value is true.
     */
    EMIT_EMPTY_IF_TRUE;

    /**
     * {@inheritDoc}
     */
    @Override
    public void test(TypeChecking checking, AstProperty property, AstValue value) {
        // NOOP
    }

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
    protected Integer resolveInteger(TypeChecking checking, AstValue value) {
        if (value instanceof AstValueInteger) {
            return ((AstValueInteger) value).getValue();
        } else {
            throw new IllegalStateException("Unrecognised value type: " + value);
        }
    }
}
package me.waliedyassen.runescript.config.type.rule;

import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.config.ast.AstProperty;
import me.waliedyassen.runescript.config.ast.value.AstValue;
import me.waliedyassen.runescript.config.semantics.SemanticError;
import me.waliedyassen.runescript.config.semantics.typecheck.TypeChecking;

/**
 * A rule which checks if an integer is between a specific range inclusively.
 *
 * @author Walied K. yassen
 */
@RequiredArgsConstructor
public final class ConfigRangeRule implements ConfigRule {

    /**
     * The minimum value of the range.
     */
    private final int minimum;

    /**
     * The maximum value of the range.
     */
    private final int maximum;


    /**
     * {@inheritDoc}
     */
    @Override
    public void test(TypeChecking checking, AstProperty property, AstValue value) {
        var integer = ConfigRule.resolveInteger(checking, value);
        if (integer < minimum || integer > maximum) {
            checking.getChecker().reportError(new SemanticError(value, "Value of this component must be in range [" + minimum + "," + maximum + "]"));
        }
    }
}

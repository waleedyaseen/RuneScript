package me.waliedyassen.runescript.config.var.rule.impl;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.config.ast.AstConfig;
import me.waliedyassen.runescript.config.ast.AstProperty;
import me.waliedyassen.runescript.config.ast.value.AstValue;
import me.waliedyassen.runescript.config.semantics.SemanticError;
import me.waliedyassen.runescript.config.semantics.typecheck.TypeChecking;
import me.waliedyassen.runescript.config.var.rule.ConfigRule;

/**
 * A configuration rule which ensures a property with a specific name is present.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class ConfigRequireRule implements ConfigRule {

    /**
     * The required sibling properties.
     */
    private final String name;

    /**
     * {@inheritDoc}
     */
    @Override
    public void test(TypeChecking checking, AstConfig config, AstProperty property, AstValue value) {
        if (config.findProperty(name) == null) {
            checking.getChecker().reportError(new SemanticError(property, String.format("Property '%s' requires property '%s'", property.getKey().getText(), name)));
        }
    }
}

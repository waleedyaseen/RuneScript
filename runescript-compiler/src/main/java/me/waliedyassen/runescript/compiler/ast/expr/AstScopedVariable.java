package me.waliedyassen.runescript.compiler.ast.expr;

import lombok.Getter;
import lombok.Setter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;
import me.waliedyassen.runescript.compiler.symbol.impl.variable.VariableInfo;
import me.waliedyassen.runescript.compiler.util.VariableScope;


/**
 * A scoped variable AST expression node.
 *
 * @author Walied K. Yassen
 */
public final class AstScopedVariable extends AstBaseVariable {

    /**
     * The scope of the variable.
     */
    @Getter
    private final VariableScope scope;

    /**
     * The variable info which is resolved at type checking phase.
     */
    @Getter
    @Setter
    private VariableInfo variableInfo;

    /**
     * Constructs a new {@link AstScopedVariable} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param scope
     *         the scope of the varaible.
     * @param name
     *         the name of the variable.
     */
    public AstScopedVariable(Range range, VariableScope scope, AstIdentifier name) {
        super(range, name);
        this.scope = scope;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E, S> E accept(AstVisitor<E, S> visitor) {
        return visitor.visit(this);
    }
}

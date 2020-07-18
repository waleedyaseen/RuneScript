package me.waliedyassen.runescript.compiler.ast.stmt;

import lombok.Getter;
import lombok.Setter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.AstNode;
import me.waliedyassen.runescript.compiler.ast.expr.AstIdentifier;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;
import me.waliedyassen.runescript.compiler.symbol.impl.variable.VariableInfo;
import me.waliedyassen.runescript.compiler.util.VariableScope;

/**
 * Represents an AST variable node.
 *
 * @author Walied K. Yassen
 */
public final class AstVariable extends AstNode {

    /**
     * The scope of the variable.
     */
    @Getter
    private final VariableScope scope;

    /**
     * The name of the variable.
     */
    @Getter
    private final AstIdentifier name;

    /**
     * The information of the variable.
     */
    @Getter
    @Setter
    private VariableInfo info;

    /**
     * Constructs a new {@link AstVariable} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param scope
     *         the scope of the variable.
     * @param name
     *         the name of the variable.
     */
    public AstVariable(Range range, VariableScope scope, AstIdentifier name) {
        super(range);
        this.scope = scope;
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E, S> Object accept(AstVisitor<E, S> visitor) {
        return visitor.visit(this);
    }
}

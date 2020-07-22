package me.waliedyassen.runescript.compiler.ast.expr;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;

/**
 * The base class for all of the AST variable nodes.
 *
 * @author Walied K. Yassen
 */
public abstract class AstBaseVariable extends AstExpression {

    /**
     * The name of the variable.
     */
    @Getter
    private final AstIdentifier name;

    /**
     * Constructs a new {@link AstBaseVariable} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param name
     *         the name of the variable.
     */
    public AstBaseVariable(Range range, AstIdentifier name) {
        super(range);
        this.name = name;
    }
}
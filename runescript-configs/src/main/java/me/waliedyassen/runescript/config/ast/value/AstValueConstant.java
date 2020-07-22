package me.waliedyassen.runescript.config.ast.value;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.config.ast.AstIdentifier;
import me.waliedyassen.runescript.config.ast.visitor.AstVisitor;

/**
 * Represents a constant reference value node.
 *
 * @author Walied K. Yassen
 */
public final class AstValueConstant extends AstValue {

    /**
     * The name of the constant.
     */
    @Getter
    private final AstIdentifier name;

    /**
     * Constructs a new {@link AstValueConstant} type object instance.
     *
     * @param range
     *         the source code range of the node.
     * @param name
     *         the name of the constant
     */
    public AstValueConstant(Range range, AstIdentifier name) {
        super(range);
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

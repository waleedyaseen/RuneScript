package me.waliedyassen.runescript.config.ast.value;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.config.ast.visitor.AstVisitor;
import me.waliedyassen.runescript.type.PrimitiveType;

/**
 * Represents a configuration property type value node.
 *
 * @author Walied K. Yassen
 */
public final class AstValueType extends AstValue {

    /**
     * The primitive type value.
     */
    @Getter
    private final PrimitiveType type;

    /**
     * Constructs a new {@link AstValueType} object instance.
     *
     * @param range
     *         the source code range of the node..
     * @param type
     *         the type of the value.
     */
    public AstValueType(Range range, PrimitiveType type) {
        super(range);
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

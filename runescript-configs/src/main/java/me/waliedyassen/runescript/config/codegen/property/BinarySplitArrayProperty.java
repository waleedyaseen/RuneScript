package me.waliedyassen.runescript.config.codegen.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.type.PrimitiveType;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Represents a binary split array property.
 *
 * @author Walied K. Yassen
 */
@Getter
@RequiredArgsConstructor
public final class BinarySplitArrayProperty implements BinaryProperty {

    /**
     * The code of the property.
     */
    private final int code;

    /**
     * The size type of the property.
     */
    private final PrimitiveType sizeType;

    /**
     * The values of the property.
     */
    private final BinarySplitArrayValue[] values;

    /**
     * Constructs a new {@link BinarySplitArrayValue} type object instance.
     *
     * @param code
     *         the code of property.
     * @param sizeType
     *         the size type of the property.
     * @param maxSize
     *         the max size of the property.
     */
    public BinarySplitArrayProperty(int code, PrimitiveType sizeType, int maxSize) {
        this.code = code;
        this.sizeType = sizeType;
        this.values = new BinarySplitArrayValue[maxSize];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void write(DataOutputStream stream) throws IOException {
        writeCode(stream);
        var size = 0;
        for (var value : values) {
            if (value != null) {
                size++;
            }
        }
        sizeType.getSerializer().serialize(size, stream);
        for (var value : values) {
            if (value == null) {
                continue;
            }
            value.serialize(stream);
        }
    }

    /**
     * Returns the {@link BinarySplitArrayValue} type object instance.
     *
     * @param index
     *         the index of the value.
     *
     * @return the {@link BinarySplitArrayValue} object.
     */
    public BinarySplitArrayValue getValue(int index) {
        return values[index];
    }

    /**
     * Adds a new value to the binary properties.
     *
     * @param index
     *         the index of the value.
     * @param componentsCount
     *         the amount of components in the property.
     *
     * @return the added {@link BinarySplitArrayValue} value object.
     */
    public BinarySplitArrayValue addValue(int index, int componentsCount) {
        return values[index] = new BinarySplitArrayValue(new PrimitiveType[componentsCount], new Object[componentsCount]);
    }

    /**
     * Represents a binary split array value.
     *
     * @author Walied K. Yassen
     */
    @Getter
    @RequiredArgsConstructor
    public static final class BinarySplitArrayValue {

        /**
         * The component types of the value.
         */
        private final PrimitiveType[] types;

        /**
         * The component value sof the value.
         */
        private final Object[] values;

        /**
         * Writes the content of this value to the specified {@link DataOutputStream stream}.
         *
         * @param stream
         *         the stream to write the content of the value to.
         *
         * @throws IOException
         *         if anything occurs while writing the value content to the stream.
         */
        @SuppressWarnings("unchecked")
        public void serialize(DataOutputStream stream) throws IOException {
            for (int index = 0; index < types.length; index++) {
                types[index].getSerializer().serialize(values[index], stream);
            }
        }
    }
}

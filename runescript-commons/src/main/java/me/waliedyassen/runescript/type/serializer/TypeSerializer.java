package me.waliedyassen.runescript.type.serializer;

import lombok.var;
import me.waliedyassen.runescript.type.PrimitiveType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A serializer for a specific type that writes and reads from or to {@link DataOutputStream stream}.
 *
 * @param <T>
 *         the type which the serializer is for.
 *
 * @author Walied k. Yassen
 */
public interface TypeSerializer<T> {

    /**
     * The {@link Boolean} type serializer.
     */
    TypeSerializer<Boolean> BOOLEAN = new TypeSerializer<Boolean>() {
        @Override
        public void serialize(Boolean value, DataOutputStream stream) throws IOException {
            stream.writeByte(value ? 1 : 0);
        }

        @Override
        public Boolean deserialize(DataInputStream stream) throws IOException {
            return stream.readByte() == 1;
        }
    };

    /**
     * The {@link Byte} type serializer.
     */
    TypeSerializer<Byte> BYTE = new TypeSerializer<Byte>() {
        @Override
        public void serialize(Byte value, DataOutputStream stream) throws IOException {
            stream.writeByte(value);
        }

        @Override
        public Byte deserialize(DataInputStream stream) throws IOException {
            return stream.readByte();
        }
    };

    /**
     * The {@link Short} type serializer.
     */
    TypeSerializer<Short> SHORT = new TypeSerializer<Short>() {
        @Override
        public void serialize(Short value, DataOutputStream stream) throws IOException {
            stream.writeShort(value);
        }

        @Override
        public Short deserialize(DataInputStream stream) throws IOException {
            return stream.readShort();
        }
    };

    /**
     * The {@link Integer} type serializer.
     */
    TypeSerializer<Integer> INT = new TypeSerializer<Integer>() {
        @Override
        public void serialize(Integer value, DataOutputStream stream) throws IOException {
            stream.writeInt(value);
        }

        @Override
        public Integer deserialize(DataInputStream stream) throws IOException {
            return stream.readInt();
        }
    };

    /**
     * The {@link Long} type serializer.
     */
    TypeSerializer<Long> LONG = new TypeSerializer<Long>() {
        @Override
        public void serialize(Long value, DataOutputStream stream) throws IOException {
            stream.writeLong(value);
        }

        @Override
        public Long deserialize(DataInputStream stream) throws IOException {
            return stream.readLong();
        }
    };

    /**
     * The {@link String} type serializer.
     */
    TypeSerializer<String> STRING = new TypeSerializer<String>() {
        @Override
        public void serialize(String value, DataOutputStream stream) throws IOException {
            stream.writeBytes(value);
            stream.writeByte(0);
        }

        @Override
        public String deserialize(DataInputStream stream) throws IOException {
            StringBuilder builder = new StringBuilder();
            while (true) {
                int ch = stream.readUnsignedByte();
                if (ch == 0) {
                    break;
                }
                builder.append((char) ch);
            }
            return builder.toString();
        }
    };

    /**
     * The {@link Boolean} type serializer.
     */
    TypeSerializer<PrimitiveType> TYPE = new TypeSerializer<PrimitiveType>() {
        @Override
        public void serialize(PrimitiveType value, DataOutputStream stream) throws IOException {
            var code = value.getCode();
            if (code > 0xff) {
                throw new IllegalArgumentException("You cannot serialise primitive type: " + value);
            }
            stream.writeByte(code);
        }

        @Override
        public PrimitiveType deserialize(DataInputStream stream) throws IOException {
            var code = stream.readUnsignedByte();
            var type = PrimitiveType.forCode((char) code);
            if (type == null) {
                throw new IllegalArgumentException("Failed to find a matching primitive type for code: " + code);
            }
            return type;
        }
    };

    /**
     * Serializes the specified {@link T value} into the specified {@link DataOutputStream stream}.
     *
     * @param value
     *         the value that we want to serialize.
     * @param stream
     *         the stream to write the serialized data into.
     *
     * @throws IOException
     *         if anything occurs while serializing.
     */
    void serialize(T value, DataOutputStream stream) throws IOException;

    /**
     * Deserializes a {@link T} type value from the specified {@link DataInputStream stream}.
     *
     * @param stream
     *         the stream to deserialize the value from.
     *
     * @return the deserialized {@link T} value object.
     *
     * @throws IOException
     *         if anything occurs while deserializing the value.
     */
    T deserialize(DataInputStream stream) throws IOException;
}

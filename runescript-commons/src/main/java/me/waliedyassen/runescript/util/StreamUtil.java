package me.waliedyassen.runescript.util;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Contains various useful utilities for writing to stream.
 *
 * @author Walied K. Yassen
 */
public final class StreamUtil {

    /**
     * Writes a triple-byte integer value to the specified {@link DataOutputStream stream}.
     *
     * @param stream
     *         the stream that we want to write to.
     * @param value
     *         the value that we want to write.
     *
     * @throws IOException
     *         if anything occurs while writing the string to the stream.
     */
    public static void writeTriByte(DataOutputStream stream, int value) throws IOException {
        stream.writeShort(value >> 8);
        stream.writeByte(value & 0xff);
    }

    /**
     * Writes a C-Style string (null terminated string) to the specified {@link DataOutputStream stream}.
     *
     * @param stream
     *         the stream that we want to write to.
     * @param value
     *         the value that we want to write.
     *
     * @throws IOException
     *         if anything occurs while writing the string to the stream.
     */
    public static void writeString(DataOutputStream stream, String value) throws IOException {
        stream.writeBytes(value);
        stream.writeByte(0);
    }

    private StreamUtil() {
        // NOOP
    }
}

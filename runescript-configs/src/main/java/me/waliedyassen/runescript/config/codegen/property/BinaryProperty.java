/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.codegen.property;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The base class for all of the binary properties.
 *
 * @author Walied K. Yassen
 */
public interface BinaryProperty {

    /**
     * Returns the code of the property.
     *
     * @return the code of the property.
     */
    int getCode();

    /**
     * Writes the content of the binary property to the specified {@link DataOutputStream stream}.
     *
     * @param stream
     *         the stream to write the the content data to.
     *
     * @throws IOException
     *         if anything occurs while writing the content data of the binary property to the stream.
     */
    void write(DataOutputStream stream) throws IOException;

    /**
     * Writes the code of the binary property to the specified {@link DataOutputStream stream}.
     *
     * @param stream
     *         teh stream to write the code to.
     *
     * @throws IOException
     *         if anything occurs while writing the code to the stream.
     */
    default void writeCode(DataOutputStream stream) throws IOException {
        stream.writeByte(getCode());
    }
}

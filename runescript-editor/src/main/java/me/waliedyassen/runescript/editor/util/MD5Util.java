/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.util;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A utility static-class that is responsible for hashing data using the MD5 algorithm.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class MD5Util {

    /**
     * The hash algorithm digset.
     */
    private static MessageDigest digset;

    // Initialise the message digset.
    static {
        try {
            digset = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to find the MD5 MessageDigset instance");
        }
    }

    /**
     * Attempts to calculate the MD5 hash for the specified {@code data}.
     *
     * @param data the data to calculate the MD5 hash for.
     * @return the hashed MD5 aa ta.
     */
    public static byte[] calculate(byte[] data) {
        if (digset == null) {
            return new byte[0];
        }
        return digset.digest(data);
    }
}

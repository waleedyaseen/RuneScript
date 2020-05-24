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
import java.util.zip.CRC32;

/**
 * A utility static-class that is responsible for hashing data using the different algorithms.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class ChecksumUtil {

    /**
     * The message digest of the MD5 algorithm.
     */
    private static MessageDigest md5Digest;

    /**
     * The CRC32 object which we are going to use to calculate CRC-32 hashes.
     */
    private static final CRC32 crc32 = new CRC32();

    // Initialise the message digest(s).
    static {
        try {
            md5Digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to find the MD5 MessageDigest instance");
        }
    }

    /**
     * Calculates the MD5 hash for the specified {@code data}.
     *
     * @param data the data to calculate the hash for.
     * @return the calculated MD5 hash of the specified {@code data}.
     */
    public static byte[] calculateMd5(byte[] data) {
        if (md5Digest == null) {
            return new byte[0];
        }
        return md5Digest.digest(data);
    }

    /**
     * Calculates the CRC-32 hash for the specified {@code data}.
     *
     * @param data the data to calculate the hash for.
     * @return the calculated CRC-32 hash of the specified {@code data}.
     */
    public static int calculateCrc32(byte[] data) {
        synchronized (crc32) {
            crc32.reset();
            crc32.update(data);
            return (int) crc32.getValue();
        }
    }
}

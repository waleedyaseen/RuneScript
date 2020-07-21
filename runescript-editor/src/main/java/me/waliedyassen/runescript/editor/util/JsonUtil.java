/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.var;

/**
 * Contains useful utilities related to Json loading and saving.
 *
 * @author Walied K. Yassen
 */
public final class JsonUtil {

    /**
     * The jackson library {@link ObjectMapper} object.
     */
    @Getter
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker().
                withFieldVisibility(JsonAutoDetect.Visibility.ANY).
                withGetterVisibility(JsonAutoDetect.Visibility.NONE).
                withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
        );
    }

    /**
     * Attempts to match the field with the specified {@code name} in the specified {@link JsonNode node} to an {@link
     * ObjectNode}.
     *
     * @param node
     *         the node which contains the field of the object.
     * @param name
     *         the name of the field of the object.
     * @param message
     *         the error message if the field was not present or is invalid.
     *
     * @return the matched {@link ObjectNode} object.
     *
     * @throws IllegalStateException
     *         if the field did not match an object node or is invalid.
     */
    public static ObjectNode getObjectOrThrow(JsonNode node, String name, String message) {
        var object = node.get(name);
        if (object == null || !object.isObject()) {
            throw new IllegalStateException(message);
        }
        return (ObjectNode) object;
    }

    /**
     * Attempts to match the field with the specified {@code name} in the specified {@link JsonNode node} to a {@link
     * String}.
     *
     * @param node
     *         the node which contains the field of the {@link String} object.
     * @param name
     *         the name of the field of the {@link String} object.
     * @param message
     *         the error message if the field was not present or is invalid.
     *
     * @return the matched {@link String} object.
     *
     * @throws IllegalStateException
     *         if the field did not match a {@link String} object or is invalid.
     */
    public static String getTextOrThrow(JsonNode node, String name, String message) {
        var textNode = node.get(name);
        if (textNode == null) {
            throw new IllegalStateException(message);
        }
        var text = textNode.textValue();
        if (text == null || text.isEmpty()) {
            throw new IllegalStateException(message);
        }
        return text;
    }

    /**
     * Attempts to match the field with the specified {@code name} in the specified {@link JsonNode node} to a {@code
     * boolean}.
     *
     * @param node
     *         the node which contains the field of the {@code boolean} value.
     * @param name
     *         the name of the field of the {@code boolean} value.
     * @param message
     *         the error message if the field was not present or is invalid.
     *
     * @return the matched {@code boolean} value.
     *
     * @throws IllegalStateException
     *         if the field did not match a {@code boolean} value or is invalid.
     */
    public static boolean getBooleanOrThrow(JsonNode node, String name, String message) {
        var textNode = node.get(name);
        if (textNode == null) {
            throw new IllegalStateException(message);
        }
        return textNode.booleanValue();
    }

    /**
     * Attempts to match the field with the specified {@code name} in the specified {@link JsonNode node} to a {@code
     * boolean}. If no value was present, the specified default value will be returned.
     *
     * @param node
     *         the node which contains the field of the {@code boolean} value.
     * @param name
     *         the name of the field of the {@code boolean} value.
     *
     * @return the matched {@code boolean} value or the already specified default value.
     */
    public static boolean getBooleanOrDefault(JsonNode node, String name, boolean defaultValue) {
        var textNode = node.get(name);
        if (textNode == null) {
            return defaultValue;
        }
        return textNode.booleanValue();
    }

    private JsonUtil() {
        // NOOP
    }
}

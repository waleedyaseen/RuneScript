/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.syntax.visitor;

import me.waliedyassen.runescript.config.syntax.ConfigSyntax;
import me.waliedyassen.runescript.config.syntax.ConstantSyntax;
import me.waliedyassen.runescript.config.syntax.IdentifierSyntax;
import me.waliedyassen.runescript.config.syntax.PropertySyntax;
import me.waliedyassen.runescript.config.syntax.value.*;

/**
 * Represents the Abstract-Syntax-Tree node visitor.
 *
 * @param <T>
 *         the return type of the visit methods.
 */
public interface SyntaxVisitor<T> {

    /**
     * Gets called when we have just visited an {@link ConfigSyntax} object.
     *
     * @param config
     *         the object we have just visited.
     *
     * @return An object which is specified by the implementation.
     */
    T visit(ConfigSyntax config);

    /**
     * Gets called when we have just visited an {@link ConstantSyntax} object.
     *
     * @param syntax
     *         the object we have just visited.
     *
     * @return An object which is specified by the implementation.
     */
    T visit(ConstantSyntax syntax);

    /**
     * Gets called when we have just visited an {@link PropertySyntax} object.
     *
     * @param property
     *         the object we have just visited.
     *
     * @return An object which is specified by the implementation.
     */
    T visit(PropertySyntax property);

    /**
     * Gets called when we have just visited an {@link ValueStringSyntax} object.
     *
     * @param value
     *         the object we have just visited.
     *
     * @return An object which is specified by the implementation.
     */
    T visit(ValueStringSyntax value);

    /**
     * Gets called when we have just visited an {@link ValueIntegerSyntax} object.
     *
     * @param value
     *         the object we have just visited.
     *
     * @return An object which is specified by the implementation.
     */
    T visit(ValueIntegerSyntax value);

    /**
     * Gets called when we have just visited an {@link ValueLongSyntax} object.
     *
     * @param value
     *         the object we have just visited.
     *
     * @return An object which is specified by the implementation.
     */
    T visit(ValueLongSyntax value);

    /**
     * Gets called when we have just visited an {@link ValueBooleanSyntax} object.
     *
     * @param value
     *         the object we have just visited.
     *
     * @return An object which is specified by the implementation.
     */
    T visit(ValueBooleanSyntax value);

    /**
     * Gets called when we have just visited an {@link ValueTypeSyntax} object.
     *
     * @param value
     *         the object we have just visited.
     *
     * @return An object which is specified by the implementation.
     */
    T visit(ValueTypeSyntax value);

    /**
     * Gets called when we have just visited an {@link ValueConstantSyntax} object.
     *
     * @param value
     *         the object we have just visited.
     *
     * @return An object which is specified by the implementation.
     */
    T visit(ValueConstantSyntax value);

    /**
     * Gets called when we have just visited an {@link ValueConfigSyntax} object.
     *
     * @param value
     *         the object we have just visited.
     *
     * @return An object which is specified by the implementation.
     */
    T visit(ValueConfigSyntax value);

    /**
     * Gets called when we have just visited an {@link ValueCoordgridSyntax} object.
     *
     * @param value
     *         the object we have just visited.
     *
     * @return An object which is specified by the implementation.
     */
    T visit(ValueCoordgridSyntax value);

    /**
     * Gets called when we have just visited an {@link IdentifierSyntax} object.
     *
     * @param identifier
     *         the object we have just visited.
     *
     * @return An object which is specified by the implementation.
     */
    T visit(IdentifierSyntax identifier);
}

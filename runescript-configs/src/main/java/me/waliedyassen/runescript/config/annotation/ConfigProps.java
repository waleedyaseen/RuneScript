/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to specify the configuration entry properties in the source code and in the encoder.
 *
 * @author Walied K. Yassen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigProps {

    /**
     * The name of the configuration entry in the source code, the value of this attribute will be passed to the
     * parser.
     *
     * @return the name of the configuration entry in the source code.
     */
    String name();

    /**
     * The opcode of the configuration entry which the encoder will encode this entry under.
     *
     * @return the opcode of the configuration entry.
     */
    int opcode();

    /**
     * Whether or not this configuration entry is required and cannot be left empty.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    boolean required() default false;
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
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
 * An annotation to specify the configuration array binding entry properties.
 *
 * @author Walied K. Yassen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigArray {

    /**
     * The format of the array variables. The %s is the the variable name provided in {@link ConfigProps#name()} and %d
     * is the {@code variable index + 1} in the array.
     *
     * @return the format of the configuration array variables.
     */
    String format() default "%s%d";

    /**
     * The size of ths configuration array binding entry.
     *
     * @return the size of the configuration array binding entry.
     */
    int size();
}

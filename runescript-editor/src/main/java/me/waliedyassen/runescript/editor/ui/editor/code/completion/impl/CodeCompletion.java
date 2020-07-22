/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor.code.completion.impl;

/**
 * The base class for all of our completion implementation.
 *
 * @author Walied K. Yassen
 */
public interface CodeCompletion {

    /**
     * {@inheritDoc}
     */
    @Override
    boolean equals(Object o);

    /**
     * {@inheritDoc}
     */
    @Override
    int hashCode();
}

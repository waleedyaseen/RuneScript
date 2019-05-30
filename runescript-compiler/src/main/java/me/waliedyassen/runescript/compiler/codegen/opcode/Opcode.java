/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.opcode;

/**
 * Represents a single RuneScript bytecode opcode.
 *
 * @author Walied K. Yassen
 */
public interface Opcode {

    /**
     * Gets the code number of the opcode.
     *
     * @return the code number of the opcode.
     */
    int getCode();
}

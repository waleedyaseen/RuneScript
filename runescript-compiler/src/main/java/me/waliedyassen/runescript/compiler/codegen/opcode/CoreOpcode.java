/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.opcode;

/**
 * Represents the core opcodes RuneScript language.
 *
 * @author Walied K. Yassen
 */
public enum CoreOpcode {

    /**
     * The push integer constant core instruction opcode.
     */
    PUSH_INT_CONSTANT,

    /**
     * The push string constant core instruction opcode.
     */
    PUSH_STRING_CONSTANT,

    /**
     * The push long constant core instruction opcode.
     */
    PUSH_LONG_CONSTANT,

    /**
     * The pop int discard core instruction opcode.
     */
    POP_INT_DISCARD,

    /**
     * The pop string discard core instruction opcode.
     */
    POP_STRING_DISCARD,

    /**
     * The pop long discard core instruction opcode.
     */
    POP_LONG_DISCARD,

    /**
     * The push int local core instruction opcode.
     */
    PUSH_INT_LOCAL,

    /**
     * The push string local core instruction opcode.
     */
    PUSH_STRING_LOCAL,

    /**
     * The push long local core instruction opcode.
     */
    PUSH_LONG_LOCAL,

    /**
     * The pop int local core instruction opcode.
     */
    POP_INT_LOCAL,

    /**
     * The pop string local core instruction opcode.
     */
    POP_STRING_LOCAL,

    /**
     * The pop long local core instruction opcode.
     */
    POP_LONG_LOCAL,

    /**
     * The push player variable core instruction opcode.
     */
    PUSH_VARP,

    /**
     * The push player variable bit core instruction opcode.
     */
    PUSH_VARP_BIT,

    /**
     * The push client variable integer core instruction opcode.
     */
    PUSH_VARC_INT,

    /**
     * The push client variable string core instruction opcode.
     */
    PUSH_VARC_STRING,

    /**
     * The pop player variable core instruction opcode.
     */
    POP_VARP,

    /**
     * The pop player variable core instruction opcode.
     */
    POP_VARP_BIT,

    /**
     * The pop client variable integer core instruction opcode.
     */
    POP_VARC_INT,

    /**
     * The pop client variable string core instruction opcode.
     */
    POP_VARC_STRING,

    /**
     * The define array core instruction opcode.
     */
    DEFINE_ARRAY,

    /**
     * The push array integer core instruction opcode.
     */
    PUSH_ARRAY_INT,

    /**
     * The pop array integer core instruction opcode.
     */
    POP_ARRAY_INT,

    /**
     * The unconditional branch core core instruction opcode.
     */
    BRANCH,

    /**
     * The conditional integer "if equals" branch core instruction opcode.
     */
    BRANCH_EQUALS,

    /**
     * The conditional integer "if not equals" branch core instruction opcode.
     */
    BRANCH_NOT,

    /**
     * The conditional integer "if less than" branch core instruction opcode.
     */
    BRANCH_LESS_THAN,

    /**
     * The conditional integer "if greater than branch" core instruction opcode.
     */
    BRANCH_GREATER_THAN,

    /**
     * The conditional integer "if less than or equals" branch core instruction opcode.
     */
    BRANCH_LESS_THAN_OR_EQUALS,

    /**
     * The conditional integer "if greater than or equals" branch core instruction opcode.
     */
    BRANCH_GREATER_THAN_OR_EQUALS,

    /**
     * The conditional integer "if equals true" branch core instruction opcode.
     */
    BRANCH_IF_TRUE,

    /**
     * The conditional integer "if equals false" branch core instruction opcode.
     */
    BRANCH_IF_FALSE,

    /**
     * The conditional long "if equals" branch core instruction opcode.
     */
    LONG_BRANCH_EQUALS,

    /**
     * The conditional long "if not equals" branch core instruction opcode.
     */
    LONG_BRANCH_NOT,

    /**
     * The conditional long "if less than" branch core instruction opcode.
     */
    LONG_BRANCH_LESS_THAN,

    /**
     * The conditional long "if greater than" branch core instruction opcode.
     */
    LONG_BRANCH_GREATER_THAN,

    /**
     * The conditional integer "if less than or equals" branch core instruction opcode.
     */
    LONG_BRANCH_LESS_THAN_OR_EQUALS,

    /**
     * The conditional integer "if greater than or equals" branch core instruction opcode.
     */
    LONG_BRANCH_GREATER_THAN_OR_EQUALS,

    /**
     * The switch branch core instruction opcode.
     */
    SWITCH,

    /**
     * The return core instruction opcode.
     */
    RETURN,

    /**
     * The string concatenation (or joining) core instruction opcode.
     */
    JOIN_STRING,

    /**
     * The gosub with parameters core instruction opcode.
     */
    GOSUB_WITH_PARAMS,

    /**
     * The arithmetic addition command instruction opcode.
     */
    ADD,

    /**
     * The arithmetic subtraction command instruction opcode.
     */
    SUB,

    /**
     * The arithmetic multiplication command instruction opcode.
     */
    MUL,

    /**
     * The arithmetic division command instruction opcode.
     */
    DIV,

    /**
     * The arithmetic modulo command instruction opcode.
     */
    MOD;

    /**
     * Checks whether or not this opcode requires a large operand (32-bit) and not a small operand (8-bit).
     *
     * @return <code>true</code> if it does otherwise <code>false</code>.
     */
    public boolean isLargeOperand() {
        switch (this) {
            case RETURN:
            case POP_INT_DISCARD:
            case POP_STRING_DISCARD:
            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case MOD:
                return false;
            default:
                return true;
        }
    }
}
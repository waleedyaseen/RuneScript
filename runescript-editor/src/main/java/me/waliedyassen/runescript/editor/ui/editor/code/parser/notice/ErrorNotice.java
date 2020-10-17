/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor.code.parser.notice;

import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.Parser;

import java.awt.*;

/**
 * A parser notice that indicates there is an error somewhere.
 *
 * @author Walied K. Yassen
 */
public final class ErrorNotice extends DefaultParserNotice {

    /**
     * Constructs a new {@link ErrorNotice} type object instance.
     *
     * @param parser  the parser which produced this error notice.
     * @param message the message of the error.
     * @param line    the line which the error is located at.
     * @param offset  the start offset of the error in source code.
     * @param width   the width of the error in source code.
     */
    public ErrorNotice(Parser parser, String message, int line, int offset, int width) {
        super(parser, message, line, offset, width);
        setLevel(Level.ERROR);
        setColor(Color.red);
    }
}
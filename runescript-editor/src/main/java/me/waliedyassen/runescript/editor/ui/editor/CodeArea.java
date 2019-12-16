/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.editor.ui.editor;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

/**
 * Represents a RuneScript Editor code area.
 *
 * @author Walied K. Yassen
 */
public class CodeArea extends RSyntaxTextArea {

    /**
     * Constructs a new {@link CodeArea} type object instance.
     */
    public CodeArea() {
        setAutoIndentEnabled(true);
        setAntiAliasingEnabled(true);
        setAnimateBracketMatching(true);
        setAutoscrolls(true);
        setWrapStyleWord(false);
    }
}

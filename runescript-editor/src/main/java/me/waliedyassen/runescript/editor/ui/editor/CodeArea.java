/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor;

import lombok.extern.slf4j.Slf4j;
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;

import javax.swing.event.HyperlinkEvent;

/**
 * Represents a RuneScript Editor code area.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class CodeArea extends RSyntaxTextArea {

    /**
     * The syntax style of the code area.
     */
    private static final String SYNTAX_STYLE_RUNESCRIPT = "text/runescript";

    /**
     * The default theme for the code areas.
     */
    private static Theme theme;

    /**
     * Constructs a new {@link CodeArea} type object instance.
     */
    public CodeArea() {
        setSyntaxEditingStyle(SYNTAX_STYLE_RUNESCRIPT);
        setAutoIndentEnabled(true);
        setAntiAliasingEnabled(true);
        setAnimateBracketMatching(true);
        setCodeFoldingEnabled(true);
        setAutoscrolls(true);
        setWrapStyleWord(false);
        if (theme != null) {
            theme.apply(this);
        }
    }

    static {
        try {
            theme = null;//Theme.load(CodeArea.class.getResourceAsStream("theme.xml"));
        } catch (Throwable e) {
            log.error("Failed to load the Theme object from the resource file", e);
        }
    }
}

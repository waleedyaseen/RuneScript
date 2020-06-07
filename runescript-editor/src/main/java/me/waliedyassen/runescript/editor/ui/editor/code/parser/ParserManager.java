/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor.code.parser;

import lombok.var;
import me.waliedyassen.runescript.editor.ui.editor.code.CodeEditor;
import me.waliedyassen.runescript.editor.ui.editor.code.parser.impl.CodeParser;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

/**
 * A static-class which contains utilities that are responsible for fetching or install parsers for
 * {@link RSyntaxTextArea} objects.
 *
 * @author Walied K. Yassen
 */
public final class ParserManager {

    /**
     * Returns the the {@link CodeParser} that is currently installed in the specified {@link RSyntaxTextArea} object.
     *
     * @param textArea the text area object which we want to check for the code parser.
     * @return the {@link CodeParser} object if present otherwise {@code null}.
     */
    public static CodeParser getCodeParser(RSyntaxTextArea textArea) {
        for (var index = 0; index < textArea.getParserCount(); index++) {
            var parser = textArea.getParser(index);
            if (parser instanceof CodeParser) {
                return (CodeParser) parser;
            }
        }
        return null;
    }

    /**
     * Installs a code parser for the specified {@link RSyntaxTextArea text area}.
     *
     * @param codeEditor the text area to install the parser for.
     */
    public static void installCodeParser(CodeEditor codeEditor) {
        codeEditor.getTextArea().addParser(new CodeParser(codeEditor));
    }

    private ParserManager() {
        // NOOP
    }
}

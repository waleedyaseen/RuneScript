/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor;

import lombok.extern.slf4j.Slf4j;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.ui.editor.folder.CodeFolder;
import me.waliedyassen.runescript.editor.ui.editor.theme.CodeTheme;
import me.waliedyassen.runescript.editor.ui.editor.tokenMaker.CodeTokenMaker;
import me.waliedyassen.runescript.editor.ui.editor.tokenMaker.factory.TokenMakerFactoryImpl;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;

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
        setTabSize(2);
        new CodeTheme(this).apply(this);
    }

    // Register the language highlighter and other stuff in the future.
    static {
        TokenMakerFactoryImpl.register(SYNTAX_STYLE_RUNESCRIPT, () -> new CodeTokenMaker(Api.getApi().getCompiler().getLexicalTable(), Api.getApi().getCompiler().getSymbolTable()));
        FoldParserManager.get().addFoldParserMapping(SYNTAX_STYLE_RUNESCRIPT, new CodeFolder());
    }
}

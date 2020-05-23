/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor.code;

import lombok.extern.slf4j.Slf4j;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.file.FileType;
import me.waliedyassen.runescript.editor.ui.editor.code.folder.CodeFolder;
import me.waliedyassen.runescript.editor.ui.editor.code.parser.ParserManager;
import me.waliedyassen.runescript.editor.ui.editor.code.theme.CodeTheme;
import me.waliedyassen.runescript.editor.ui.editor.code.tokenMaker.CodeTokenMaker;
import me.waliedyassen.runescript.editor.ui.editor.code.tokenMaker.factory.TokenMakerFactoryImpl;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;

import java.nio.file.Path;

/**
 * Represents a RuneScript Editor code area.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class CodeEditor extends FileEditor {

    /**
     * The syntax style of the code area.
     */
    private static final String SYNTAX_STYLE_RUNESCRIPT = "text/runescript";


    /**
     * Constructs a new {@link CodeEditor} type object instance.
     *
     * @param fileType the type of the file we are editing.
     * @param path     the path of the file we are editing.
     */
    public CodeEditor(FileType fileType, Path path) {
        super(fileType, path);
        textArea.setSyntaxEditingStyle(SYNTAX_STYLE_RUNESCRIPT);
        textArea.setTabSize(2);
        textArea.setAutoIndentEnabled(true);
        textArea.setAntiAliasingEnabled(true);
        textArea.setAnimateBracketMatching(true);
        textArea.setCodeFoldingEnabled(true);
        textArea.setAutoscrolls(true);
        textArea.setWrapStyleWord(false);
        new CodeTheme(textArea).apply(textArea);
        ParserManager.installCodeParser(this);
        textArea.setPopupMenu(null);
    }

    // Register the language highlighter and other stuff in the future.
    static {
        TokenMakerFactoryImpl.register(SYNTAX_STYLE_RUNESCRIPT, () -> new CodeTokenMaker(Api.getApi().getCompiler().getLexicalTable(), Api.getApi().getCompiler().getSymbolTable()));
        FoldParserManager.get().addFoldParserMapping(SYNTAX_STYLE_RUNESCRIPT, new CodeFolder());
    }
}

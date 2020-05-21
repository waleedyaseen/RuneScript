/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.ui.editor.folder.CodeFolder;
import me.waliedyassen.runescript.editor.ui.editor.parser.ParserManager;
import me.waliedyassen.runescript.editor.ui.editor.theme.CodeTheme;
import me.waliedyassen.runescript.editor.ui.editor.tokenMaker.CodeTokenMaker;
import me.waliedyassen.runescript.editor.ui.editor.tokenMaker.factory.TokenMakerFactoryImpl;
import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

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
     * The view pane of the code area.
     */
    @Getter
    private final JComponent viewPane = new JPanel(new BorderLayout());

    /**
     * The path of the code area.
     */
    @Getter
    private final Path path;

    /**
     * Constructs a new {@link CodeArea} type object instance.
     *
     * @param path the file path of the code area.
     */
    public CodeArea(Path path) {
        this.path = path;
        setSyntaxEditingStyle(SYNTAX_STYLE_RUNESCRIPT);
        setTabSize(2);
        setAutoIndentEnabled(true);
        setAntiAliasingEnabled(true);
        setAnimateBracketMatching(true);
        setCodeFoldingEnabled(true);
        setAutoscrolls(true);
        setWrapStyleWord(false);
        viewPane.add(new RTextScrollPane(this));
        viewPane.add(new ErrorStrip(this), BorderLayout.LINE_END);
        new CodeTheme(this).apply(this);
        ParserManager.installCodeParser(this);
    }

    // Register the language highlighter and other stuff in the future.
    static {
        TokenMakerFactoryImpl.register(SYNTAX_STYLE_RUNESCRIPT, () -> new CodeTokenMaker(Api.getApi().getCompiler().getLexicalTable(), Api.getApi().getCompiler().getSymbolTable()));
        FoldParserManager.get().addFoldParserMapping(SYNTAX_STYLE_RUNESCRIPT, new CodeFolder());
    }
}

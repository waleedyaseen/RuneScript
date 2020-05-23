/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor.code;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.ui.editor.Editor;
import me.waliedyassen.runescript.editor.ui.editor.code.folder.CodeFolder;
import me.waliedyassen.runescript.editor.ui.editor.code.parser.ParserManager;
import me.waliedyassen.runescript.editor.ui.editor.code.theme.CodeTheme;
import me.waliedyassen.runescript.editor.ui.editor.code.tokenMaker.CodeTokenMaker;
import me.waliedyassen.runescript.editor.ui.editor.code.tokenMaker.factory.TokenMakerFactoryImpl;
import me.waliedyassen.runescript.editor.util.MD5Util;
import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * Represents a RuneScript Editor code area.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class CodeEditor extends Editor<Path> {

    /**
     * The syntax style of the code area.
     */
    private static final String SYNTAX_STYLE_RUNESCRIPT = "text/runescript";

    /**
     * The text area of the code editor.
     */
    @Getter
    private final RSyntaxTextArea textArea = new RSyntaxTextArea();

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
     * The checksum of the editor content on the local disk.
     */
    private byte[] diskChecksum;

    /**
     * Constructs a new {@link CodeEditor} type object instance.
     *
     * @param path the file path of the code area.
     */
    public CodeEditor(Path path) {
        this.path = path;
        textArea.setSyntaxEditingStyle(SYNTAX_STYLE_RUNESCRIPT);
        textArea.setTabSize(2);
        textArea.setAutoIndentEnabled(true);
        textArea.setAntiAliasingEnabled(true);
        textArea.setAnimateBracketMatching(true);
        textArea.setCodeFoldingEnabled(true);
        textArea.setAutoscrolls(true);
        textArea.setWrapStyleWord(false);
        viewPane.add(new RTextScrollPane(textArea));
        viewPane.add(new ErrorStrip(textArea), BorderLayout.LINE_END);
        new CodeTheme(textArea).apply(textArea);
        ParserManager.installCodeParser(this);
        textArea.setPopupMenu(null);
    }

    // Register the language highlighter and other stuff in the future.
    static {
        TokenMakerFactoryImpl.register(SYNTAX_STYLE_RUNESCRIPT, () -> new CodeTokenMaker(Api.getApi().getCompiler().getLexicalTable(), Api.getApi().getCompiler().getSymbolTable()));
        FoldParserManager.get().addFoldParserMapping(SYNTAX_STYLE_RUNESCRIPT, new CodeFolder());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reload() {
        try {
            var data = Files.readAllBytes(path);
            try (var reader = new InputStreamReader(new ByteArrayInputStream(data))) {
                textArea.read(reader, null);
            }
            diskChecksum = MD5Util.calculate(data);
        } catch (Throwable e) {
            log.error("An error occured while trying to save code editor content to disk", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save() {
        try {
            var content = textArea.getText().getBytes();
            Files.write(path, content, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            diskChecksum = MD5Util.calculate(content);
        } catch (Throwable e) {
            log.error("An error occured while trying to save code editor content to disk", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent getViewComponent() {
        return viewPane;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return path.getFileName().toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTooltip() {
        return path.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isModified() {
        return !Arrays.equals(MD5Util.calculate(textArea.getText().getBytes()), diskChecksum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Path getKey() {
        return path;
    }
}

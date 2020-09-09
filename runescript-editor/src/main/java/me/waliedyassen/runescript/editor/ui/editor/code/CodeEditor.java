/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor.code;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.file.FileType;
import me.waliedyassen.runescript.editor.file.impl.ConfigFileType;
import me.waliedyassen.runescript.editor.file.impl.ScriptFileType;
import me.waliedyassen.runescript.editor.ui.editor.code.completion.CodeCompletionProvider;
import me.waliedyassen.runescript.editor.ui.editor.code.folder.CodeFolder;
import me.waliedyassen.runescript.editor.ui.editor.code.parser.ParserManager;
import me.waliedyassen.runescript.editor.ui.editor.code.theme.CodeTheme;
import me.waliedyassen.runescript.editor.ui.editor.code.tokenMaker.CodeTokenMaker;
import me.waliedyassen.runescript.editor.ui.editor.code.tokenMaker.factory.TokenMakerFactoryImpl;
import org.fife.ui.autocomplete.AutoCompletion;
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
     * The syntax style of the code area.
     */
    private static final String SYNTAX_STYLE_RUNECONFIG = "text/runeconfig";

    /**
     * Whether or not this code area is for configs.
     */
    private final boolean config;

    /**
     * Constructs a new {@link CodeEditor} type object instance.
     *
     * @param fileType the type of the file we are editing.
     * @param path     the path of the file we are editing.
     */
    public CodeEditor(FileType fileType, Path path) {
        super(fileType, path);
        config = fileType instanceof ConfigFileType;
        textArea.setSyntaxEditingStyle(config ? SYNTAX_STYLE_RUNECONFIG : SYNTAX_STYLE_RUNESCRIPT);
        textArea.setTabSize(2);
        textArea.setAutoIndentEnabled(true);
        textArea.setAntiAliasingEnabled(true);
        textArea.setAnimateBracketMatching(true);
        textArea.setCodeFoldingEnabled(true);
        textArea.setAutoscrolls(true);
        textArea.setWrapStyleWord(false);
        new CodeTheme(textArea).apply(textArea);
        ParserManager.installCodeParser(this);
        installAutoComplete();
    }

    /**
     * Installs the auto complete system on code editor.
     */
    private void installAutoComplete() {
        var autoCompletion = new AutoCompletion(new CodeCompletionProvider());
        if (fileType instanceof ScriptFileType) {
            autoCompletion.setParameterAssistanceEnabled(true);
        }
        autoCompletion.install(textArea);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save() {
        var project = Api.getApi().getProjectManager().getCurrentProject().get();
        super.save();
        project.getCache().recompile(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void restoreModification() {
        var project = Api.getApi().getProjectManager().getCurrentProject().get();
        project.getCache().recompile(path);
    }

    // Register the language highlighter and other stuff in the future.
    static {
        TokenMakerFactoryImpl.register(SYNTAX_STYLE_RUNESCRIPT, () -> new CodeTokenMaker(Api.getApi().getScriptCompiler().getLexicalTable(), Api.getApi().getScriptCompiler().getSymbolTable(), false));
        FoldParserManager.get().addFoldParserMapping(SYNTAX_STYLE_RUNESCRIPT, new CodeFolder(false));
        TokenMakerFactoryImpl.register(SYNTAX_STYLE_RUNECONFIG, () -> new CodeTokenMaker(Api.getApi().getConfigCompiler().getLexicalTable(), Api.getApi().getConfigCompiler().getSymbolTable(), true));
        FoldParserManager.get().addFoldParserMapping(SYNTAX_STYLE_RUNECONFIG, new CodeFolder(true));
    }
}

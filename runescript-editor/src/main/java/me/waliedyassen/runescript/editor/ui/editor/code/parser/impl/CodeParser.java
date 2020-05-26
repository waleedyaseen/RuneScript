/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor.code.parser.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.waliedyassen.runescript.commons.document.LineColumn;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.ui.editor.code.CodeEditor;
import me.waliedyassen.runescript.editor.ui.editor.code.parser.notice.ErrorNotice;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;

import javax.swing.text.BadLocationException;

/**
 * The RuneScript language text area code parser.
 *
 * @author Walied K. Yassen
 */
@Slf4j
@RequiredArgsConstructor
public final class CodeParser extends AbstractParser {

    /**
     * The result of the
     */
    private final DefaultParseResult parseResult = new DefaultParseResult(this);

    /**
     * The text area which the parser is for.
     */
    private final CodeEditor codeEditor;

    /**
     * {@inheritDoc}
     */
    @Override
    public ParseResult parse(RSyntaxDocument doc, String style) {
        var textArea = codeEditor.getTextArea();
        parseResult.clearNotices();
        parseResult.setParsedLines(0, textArea.getLineCount() - 1);
        var errorPath = codeEditor.getKey().toAbsolutePath().toString();
        var project = Api.getApi().getProjectManager().getCurrentProject().get();
        var errorsView = Api.getApi().getUi().getErrorsView();
        errorsView.removeErrorForPath(errorPath);
        var start = System.currentTimeMillis();
        var result = project.getCache().recompile(codeEditor.getKey(), textArea.getText().getBytes());
        parseResult.setParseTime(System.currentTimeMillis() - start);
        for (var pair : result.getErrors()) {
            var error = pair.getValue();
            try {
                var startOffset = getOffset(error.getRange().getStart());
                var endOffset = getOffset(error.getRange().getEnd());
                var line = textArea.getLineOfOffset(startOffset);
                parseResult.addNotice(new ErrorNotice(this, error.getMessage(), line, startOffset, endOffset));
            } catch (Throwable e) {
                log.warn("An error occurred while adding the compiling errors to the result", e);
            }
        }
        return parseResult;
    }

    /**
     * Calculates and returns the start offset for the specified {@link LineColumn} object.
     *
     * @param lineColumn the object which contains the line and column information.
     */
    private int getOffset(LineColumn lineColumn) throws BadLocationException {
        return codeEditor.getTextArea().getLineStartOffset(lineColumn.getLine() - 1) + lineColumn.getColumn() - 1;
    }
}

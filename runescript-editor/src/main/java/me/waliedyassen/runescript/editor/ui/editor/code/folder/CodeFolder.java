/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor.code.folder;

import lombok.extern.slf4j.Slf4j;
import me.waliedyassen.runescript.editor.ui.editor.code.tokenMaker.CodeTokens;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.folding.FoldParser;
import org.fife.ui.rsyntaxtextarea.folding.FoldType;

import javax.swing.text.BadLocationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the fold parser of the RuneScript code editor.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class CodeFolder implements FoldParser {

    /**
     * The multiline comment ending characters sequence.
     */
    private static final char[] COMMENT_END = {'*', '/'};

    /**
     * The script declaration code folding type.
     */
    private static final int DECLARATION = FoldType.FOLD_TYPE_USER_DEFINED_MIN + 1;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Fold> getFolds(RSyntaxTextArea textArea) {
        var folds = new ArrayList<Fold>();
        var lineCount = textArea.getLineCount();
        var currentDeclarationStart = -1;
        var lastCommentStart = -1;
        var lastCurlyBraceLine = -1;
        var current = (Fold) null;
        var previous = (Fold) null;
        try {
            for (var line = 0; line < lineCount; line++) {
                var token = textArea.getTokenListForLine(line);
                while (token != null && token.isPaintable()) {
                    if (token.getType() == CodeTokens.DECLARATION) {
                        if (currentDeclarationStart != -1 && current != null) {
                            current.setEndOffset(token.getOffset() - 1);
                            if (current.isOnSingleLine()) {
                                folds.remove(folds.size() - 1);
                            }
                        }
                        currentDeclarationStart = token.getOffset();
                        current = new Fold(DECLARATION, textArea, currentDeclarationStart);
                        folds.add(current);
                    } else if (token.isLeftCurly()) {
                        if (previous != null && line == lastCurlyBraceLine) {
                            current = previous;
                            previous = null;
                            lastCurlyBraceLine = -1;
                        } else if (current == null) {
                            current = new Fold(FoldType.CODE, textArea, token.getOffset());
                            folds.add(current);
                        } else {
                            current = current.createChild(FoldType.CODE, token.getOffset());
                        }
                    } else if (token.isRightCurly()) {
                        if (current != null) {
                            current.setEndOffset(token.getOffset() - 1);
                            Fold parentFold = current.getParent();
                            if (current.isOnSingleLine()) {
                                if (!current.removeFromParent()) {
                                    folds.remove(folds.size() - 1);
                                }
                            } else {
                                lastCurlyBraceLine = line;
                                previous = current;
                            }
                            current = parentFold;
                        }
                    } else if (token.getType() == CodeTokens.MULTILINE_COMMENT) {
                        if (token.endsWith(COMMENT_END)) {
                            int commentEnd = token.getEndOffset() - 1;
                            if (current == null) {
                                current = new Fold(FoldType.COMMENT, textArea, lastCommentStart);
                                current.setEndOffset(commentEnd);
                                folds.add(current);
                                current = null;
                            } else {
                                current = current.createChild(FoldType.COMMENT, lastCommentStart);
                                current.setEndOffset(commentEnd);
                                current = current.getParent();
                            }
                            lastCommentStart = -1;
                        } else if (lastCommentStart == -1) {
                            lastCommentStart = token.getOffset();
                        }
                    }
                    token = token.getNextToken();
                }
            }
            if (currentDeclarationStart != -1 && current != null) {
                current.setEndOffset(textArea.getLastVisibleOffset());
                if (current.isOnSingleLine()) {
                    folds.remove(folds.size() - 1);
                }
            }
        } catch (BadLocationException e) {
            log.error("An error occurred while calculating the folds", e);
            return Collections.emptyList();
        }
        return folds;
    }
}

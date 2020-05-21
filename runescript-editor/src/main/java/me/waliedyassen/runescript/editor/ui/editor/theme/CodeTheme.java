/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor.theme;

import me.waliedyassen.runescript.editor.ui.editor.CodeArea;
import me.waliedyassen.runescript.editor.ui.editor.tokenMaker.CodeTokens;
import org.fife.ui.rsyntaxtextarea.*;

import java.awt.*;
import java.util.Arrays;

import static me.waliedyassen.runescript.editor.ui.editor.tokenMaker.CodeTokens.*;

/**
 * Represents the {@link CodeArea} theme object.
 *
 * @author Walied K. Yassen
 */
public final class CodeTheme extends Theme {

    /**
     * Constructs a new {@link CodeTheme} type object instance.
     *
     * @param textArea the text area to make the theme based on.
     */
    public CodeTheme(RSyntaxTextArea textArea) {
        super(textArea);
        selectionRoundedEdges = true;
        currentLineHighlight = new Color(127, 255, 255);
        fadeCurrentLineHighlight = false;
        matchedBracketAnimate = true;
        matchedBracketHighlightBoth = true;
        scheme = new CodeScheme();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(RSyntaxTextArea textArea) {
        super.apply(textArea);
        var gutter = RSyntaxUtilities.getGutter(textArea);
        if (gutter != null) {
            gutter.setBookmarkingEnabled(true);
        }
    }

    /**
     * Represents the color scheme of the code area theme.
     *
     * @author Walied K. Yassen
     */
    private static class CodeScheme extends SyntaxScheme {

        /**
         * Constructs a new {@link CodeScheme} type object instance.
         */
        public CodeScheme() {
            super(true);
            var styles = Arrays.copyOf(getStyles(), CodeTokens.NUM_TOKENS);
            styles[UNDEFINED] = styles[NULL];
            styles[WHITESPACE] = styles[NULL];
            styles[IDENTIFIER] = styles[NULL];
            styles[DECLARATION] = new Style(new Color(255, 0, 0));
            styles[LOCAL_VARIABLE] = new Style(new Color(255, 127, 0));
            styles[NUMBER_LITERAL] = new Style(new Color(255, 0, 71));
            styles[GLOBAL_VARIABLE] = new Style(new Color(255, 127, 0));
            styles[CONSTANT] = new Style(new Color(255, 127, 0));
            styles[STRING_LITERAL] = new Style(new Color(128, 128, 128));
            styles[STRING_INTERPOLATE] = new Style(new Color(0, 0, 128));
            styles[LINE_COMMENT] = new Style(new Color(0, 127, 174));
            styles[MULTILINE_COMMENT] = styles[LINE_COMMENT];
            styles[IDENTIFIER] = new Style(new Color(77, 164, 178));
            styles[KEYWORD] = new Style(new Color(0, 0, 255));
            styles[TYPE_NAME] = new Style(new Color(0, 128, 6));
            styles[COMMAND] = new Style(new Color(0, 128, 6));
            setStyles(styles);
        }
    }
}

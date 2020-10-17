/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor.code.completion.parameter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import me.waliedyassen.runescript.commons.document.LineColumn;
import me.waliedyassen.runescript.compiler.syntax.ParameterSyntax;
import me.waliedyassen.runescript.compiler.syntax.ScriptSyntax;
import me.waliedyassen.runescript.editor.ui.editor.code.completion.CodeCompletionProvider;
import me.waliedyassen.runescript.editor.ui.editor.code.parser.ParserManager;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.ParameterChoicesProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.fife.ui.autocomplete.ParameterizedCompletion.*;

/**
 * An auto completion provider for command and script parameters.
 *
 * @author Walied K. Yassen
 */
@Slf4j
@RequiredArgsConstructor
public final class CodeParameterCompletionProvider implements ParameterChoicesProvider {

    /**
     * The owner completion provider of this provider.
     */
    private final CodeCompletionProvider provider;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Completion> getParameterChoices(JTextComponent tc, Parameter param) {
        var script = getScriptAtOffset(tc, tc.getCaretPosition());
        if (script == null) {
            return Collections.emptyList();
        }
        var completions = new ArrayList<Completion>();
        addParameters(completions, param, script.getParameters());
        return completions;
    }

    private void addParameters(List<Completion> completions, Parameter requested, ParameterSyntax[] parameters) {
        for (ParameterSyntax parameter : parameters) {
            if (Objects.equals(parameter.getType(), requested.getTypeObject())) {
                completions.add(new BasicCompletion(provider, "$" + parameter.getName().getText()));
            }
        }
    }

    private ScriptSyntax getScriptAtOffset(JTextComponent tc, int offset) {
        var textArea = (RSyntaxTextArea) tc;
        var parser = ParserManager.getCodeParser(textArea);
        if (parser == null || parser.getScripts() == null || parser.getScripts().length == 0) {
            return null;
        }
        for (var script : parser.getScripts()) {
            if (script.getRange().contains(offset)) {
                return script;
            }
        }
        return null;
    }
}

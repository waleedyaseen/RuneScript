/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor.code.completion.parameter;

import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.editor.ui.editor.code.completion.CodeCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.ParameterChoicesProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;

import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.List;

/**
 * An auto completion provider for command and script parameters.
 *
 * @author Walied K. Yassen
 */
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
    public List<Completion> getParameterChoices(JTextComponent tc, ParameterizedCompletion.Parameter param) {
        var completions = new ArrayList<Completion>();
        return completions;
    }
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor.code.completion.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.editor.ui.editor.code.completion.impl.CodeCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A cache of {@link CodeCompletion} objects that are related.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class AutoCompleteCache {

    /**
     * A list of all the cached {@link CodeCompletion} objects.
     */
    @Getter
    private final List<Completion> completions = new ArrayList<>();

    /**
     * The provider which instantiated this completion cache.
     */
    @Getter
    private final CompletionProvider provider;

    /**
     * Adds a new {@link Completion} to the completions list in the appropriate place.
     *
     * @param completion the completion that we want to add to the completions list.
     */
    public void addSorted(Completion completion) {
        var index = Collections.binarySearch(completions, completion);
        if (index < 0) {
            index = -(index + 1);
        }
        completions.add(index, completion);
    }
}

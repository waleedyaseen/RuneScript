/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.editor.ui.editor;

import com.alee.extended.tab.DocumentData;
import com.alee.laf.scroll.WebScrollBar;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.scroll.WebScrollPaneBar;
import lombok.Getter;

import javax.swing.*;

/**
 * The {@link DocumentData} implementation for the {@link EditorView}.
 *
 * @author Walied K. Yassen
 */
public final class EditorData extends DocumentData<WebScrollPane> {

    /**
     * The code area which this editor data is for.
     */
    @Getter
    private final CodeArea codeArea;

    /**
     * Constructs a new {@link EditorData} type object instance.
     *
     * @param id
     *         {@inheritDoc}
     * @param title
     *         {@inheritDoc}
     * @param codeArea
     *         the {@link CodeArea} component of the code view.
     */
    public EditorData(String id, String title, CodeArea codeArea) {
        super(id, title, new WebScrollPane(codeArea));
        this.codeArea = codeArea;
    }
}

/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor;

import com.alee.extended.tab.DocumentAdapter;
import com.alee.extended.tab.PaneData;
import com.alee.extended.tab.WebDocumentPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.toolbar.WebToolBar;
import com.alee.managers.style.StyleId;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The editor main-view component.
 *
 * @author Walied K. Yassen
 */
public final class EditorView extends WebPanel {

    /**
     * The tool bar for the editor view.
     */
    private final WebToolBar toolBar = new WebToolBar(StyleId.toolbarAttachedNorth);

    /**
     * The documents pane for the code areas.
     */
    private final WebDocumentPane<EditorData> documents = new WebDocumentPane<>();

    /**
     * A map of all the existing {@link EditorData} in the current system by their {@code id}.
     */
    private final Map<String, EditorData> cachedData = new HashMap<>();

    /**
     * Constructs a new {@link EditorView} type object instance.
     */
    public EditorView() {
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(documents, BorderLayout.CENTER);
        setup();
        documents.openDocument(createEditorData("Test"));
    }

    /**
     * Sets-up the editor view document pane.
     */
    private void setup() {
        documents.setTabbedPaneCustomizer(tabbedPane -> tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT));
        documents.addDocumentListener(new DocumentAdapter<>() {
            @Override
            public void closed(EditorData document, PaneData<EditorData> pane, int index) {
                cachedData.remove(document.getId());
            }
        });
    }


    /**
     * Creates a new {@link EditorData} object with the specified {@code title} and cache it in the local map.
     *
     * @param title
     *         the title of the tab or the name of the file.
     *
     * @return the created {@link EditorData} object.
     */
    private EditorData createEditorData(String title) {
        var id = "editorview.codearea." + title.toLowerCase().replace(" ", "_") + System.currentTimeMillis();
        var data = new EditorData(id, title, new CodeArea());
        cachedData.put(id, data);
        return data;
    }
}

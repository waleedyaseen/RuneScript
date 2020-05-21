/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.shortcut.ShortcutManager;
import me.waliedyassen.runescript.editor.shortcut.common.CommonGroups;
import me.waliedyassen.runescript.editor.shortcut.common.CommonShortcuts;
import me.waliedyassen.runescript.editor.ui.dialog.DialogManager;
import me.waliedyassen.runescript.editor.ui.dialog.DialogResult;
import me.waliedyassen.runescript.editor.ui.menu.action.ActionSource;
import me.waliedyassen.runescript.editor.ui.menu.action.list.ActionList;
import me.waliedyassen.runescript.editor.ui.util.DelegatingMouseListener;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

/**
 * The editor main-view component.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class EditorView extends JPanel implements ActionSource {

    /**
     * The docking {@code ID} for the editor docking component.
     */
    public static final String DOCK_ID = "editor.dock";

    /**
     * The documents pane for the code areas.
     */
    private final JTabbedPane tabbedPane = new JTabbedPane();

    /**
     * The tabs that are currently in the editor view.
     */
    private final Map<Path, EditorTab> tabsByPath = new HashMap<>();

    /**
     * The tabs that are currently in the editor view.
     */
    private final Map<Component, EditorTab> tabsByComponent = new HashMap<>();

    /**
     * Constructs a new {@link EditorView} type object instance.
     */
    public EditorView() {
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        // We replace the original mouse listener of the tabbed pane.
        var listener = tabbedPane.getMouseListeners()[0];
        tabbedPane.removeMouseListener(listener);
        tabbedPane.addMouseListener(new DelegatingMouseListener(listener) {

            /**
             * {@inheritDoc}
             */
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    super.mousePressed(e);
                }
                var tabIndex = tabbedPane.indexAtLocation(e.getX(), e.getY());
                if (tabIndex == -1) {
                    return;
                }
                var component = tabbedPane.getComponentAt(tabIndex);
                var tab = tabsByComponent.get(component);
                if (tab == null) {
                    log.warn("Failed to find an EditorTab object for Component: {}", component);
                    return;
                }
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    closeTab(tab.getPath());
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    var actionList = Api.getApi().getActionManager().createList(tab);
                    tab.populateActions(actionList);
                    populateActions(actionList);
                    if (actionList.isEmpty()) {
                        return;
                    }
                    var popup = actionList.createPopupMenu();
                    popup.show(tabbedPane, e.getX(), e.getY());
                }
            }
        });
    }

    /**
     * Adds a new editor tab for the specified {@link Path path}.
     *
     * @param path the file path which the tab will be editing.
     */
    public void addTab(Path path) {
        EditorTab tab = tabsByPath.get(path);
        if (tab != null) {
            throw new IllegalStateException("The specified file path is already opened by another editor tab");
        }
        try {
            tab = new EditorTab(path);
            var component = tab.getComponent();
            tabsByPath.put(path, tab);
            tabsByComponent.put(component, tab);
            tabbedPane.addTab(path.getFileName().toString(), component);
            selectTab(path);
        } catch (IOException e) {
            log.error("Failed to create a new editor tab for path: {}", path, e);
        }
    }

    /**
     * Selects the tab with the specified {@link Path path}.
     *
     * @param path the file path on the local disk to select it's editor tab.
     * @return <code>true</code> if the tab was selected or <code>false</code> if it was not.
     */
    public boolean selectTab(Path path) {
        EditorTab tab = tabsByPath.get(path);
        if (tab == null) {
            return false;
        }
        tabbedPane.setSelectedComponent(tab.getComponent());
        return true;
    }

    /**
     * Closes all of the tabs that are open in the editor.
     */
    private void closeAllTabs() {
        for (var path : tabsByPath.keySet().toArray(new Path[0])) {
            var tab = tabsByPath.get(path);
            if (tab == null) {
                continue;
            }
            tab.requestClose();
        }
    }

    /**
     * Closes the tab with the specified {@link Path path} if it is opened in the editor.
     *
     * @param path the path of the tab that we want to close.
     */
    private void closeTab(Path path) {
        var tab = tabsByPath.get(path);
        if (tab == null) {
            return;
        }
        tabsByComponent.remove(tab.getComponent());
        tabsByPath.remove(path);
        tabbedPane.remove(tab.getComponent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateActions(ActionList actionList) {
        actionList.addSeparator();
        actionList.addAction("Close All", (source) -> closeAllTabs());
    }

    /**
     * A single code editor tab in the editor view.
     *
     * @author Walied K. Yassen
     */
    public static final class EditorTab implements ActionSource {

        /**
         * The code area which contains the content of the file.
         */
        @Getter
        private final CodeArea codeArea = new CodeArea();

        /**
         * The file path which the editor tab is editing.
         */
        @Getter
        private final Path path;

        /**
         * The cached component of the editor view.
         */
        private JComponent component;

        /**
         * Whether or not the tab has been modified.
         */
        @Getter
        private boolean modified;

        /**
         * Constructs a new {@link EditorTab} type object instance.
         *
         * @param path the file path on the local disk which this tab is for.
         * @throws IOException if anything occurs during the loading of the editor tab content.
         */
        public EditorTab(Path path) throws IOException {
            this.path = path;
            ShortcutManager.getInstance().bindShortcuts(CommonGroups.EDITOR, codeArea, this);
            ShortcutManager.getInstance().bindShortcuts(CommonGroups.EDITOR, getComponent(), this);
            reload();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void populateActions(ActionList actionList) {
            actionList.addAction("Close", CommonGroups.EDITOR.lookup(CommonShortcuts.EDITOR_CLOSE_FILE));
            actionList.addAction("Close", CommonGroups.EDITOR.lookup(CommonShortcuts.EDITOR_SAVE_FILE));
        }

        /**
         * Requests a close action for this tab.
         */
        public void requestClose() {
            try {
                if (checkModifySave()) {
                    return;
                }
                Api.getApi().getEditorView().closeTab(path);
            } catch (IOException e) {
                log.error("An error occurred while requesting an editor tab to close", e);
            }
        }

        /**
         * @return
         * @throws IOException
         */
        public boolean checkModifySave() throws IOException {
            if (modified) {
                var result = DialogManager.showCloseDialog("This file has unsaved changes. Do you want to save your changes before closing?");
                if (result == DialogResult.CANCEL) {
                    return true;
                }
                if (result == DialogResult.YES) {
                    save();
                }
            }
            return false;
        }

        /**
         * Reloads the content of the tab from the file from the local disk.
         *
         * @throws IOException if anything occurs during reading the content of the file from the local disk.
         */
        public void reload() throws IOException {
            try (var reader = Files.newBufferedReader(path)) {
                codeArea.read(reader, null);
            }
        }

        /**
         * Saves the content of this file to the disk.
         */
        @SneakyThrows
        public void save() {
            Files.writeString(path, codeArea.getText(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            modified = false;
        }

        /**
         * Returns the cached component of this editor tab or create a new one if there is none cached.
         *
         * @return the cached or created {@link JComponent} object.
         */
        public JComponent getComponent() {
            if (component != null) {
                return component;
            }
            component = new RTextScrollPane(codeArea);
            return component;
        }
    }

    static {
        ShortcutManager.getInstance().addShortcut(CommonGroups.EDITOR, CommonShortcuts.EDITOR_CLOSE_FILE, KeyStroke.getKeyStroke("ctrl W"), (source) -> {
            var editorTab = (EditorTab) source;
            editorTab.requestClose();
        });
        ShortcutManager.getInstance().addShortcut(CommonGroups.EDITOR, CommonShortcuts.EDITOR_SAVE_FILE, KeyStroke.getKeyStroke("ctrl S"), (source) -> {
            var editorTab = (EditorTab) source;
            editorTab.save();
        });
    }
}

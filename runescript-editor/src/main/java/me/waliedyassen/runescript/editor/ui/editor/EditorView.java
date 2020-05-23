/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor;

import lombok.extern.slf4j.Slf4j;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.shortcut.ShortcutManager;
import me.waliedyassen.runescript.editor.shortcut.common.CommonGroups;
import me.waliedyassen.runescript.editor.shortcut.common.CommonShortcuts;
import me.waliedyassen.runescript.editor.ui.menu.action.ActionSource;
import me.waliedyassen.runescript.editor.ui.menu.action.list.ActionList;
import me.waliedyassen.runescript.editor.ui.tabbedpane.TabbedPane;
import me.waliedyassen.runescript.editor.ui.util.DelegatingMouseListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
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
    private final TabbedPane tabbedPane = new TabbedPane();

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
     * {@inheritDoc}
     */
    @Override
    public void populateActions(ActionList actionList) {
        var tab = (EditorTab) actionList.getSource();
        actionList.addAction("Close Others", source -> closeAllBut(tabbedPane.indexOfComponent(tab.getViewComponent())))
                .withPredicate(action -> tabbedPane.getTabCount() > 1);
        actionList.addAction("Close All", source -> closeAllTabs());
        var tabIndex = tabbedPane.indexOfComponent(tab.getViewComponent());
        actionList.addAction("Close All to the Left", source -> closeAllToLeft(tabIndex))
                .withPredicate(action -> tabIndex > 0);
        actionList.addAction("Close All to the Right", source -> closeAllToRight(tabIndex))
                .withPredicate(action -> tabIndex < tabbedPane.getTabCount() - 1);
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
            var component = tab.getViewComponent();
            tabsByPath.put(path, tab);
            tabsByComponent.put(component, tab);
            tabbedPane.addTab(path.getFileName().toString(), component);
            tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(component), new EditorTabComponent(tab));
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
        tabbedPane.setSelectedComponent(tab.getViewComponent());
        return true;
    }

    /**
     * Closes all of the tabs that are open in the editor.
     *
     * @return <code>true</code> if the operation was cancelled otherwise <code>false</code>.
     */
    public boolean closeAllTabs() {
        for (var path : tabsByPath.keySet().toArray(new Path[0])) {
            var tab = tabsByPath.get(path);
            if (tab == null) {
                continue;
            }
            if (!tab.requestClose()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Closes the tab with the specified {@link Path path} if it is opened in the editor.
     *
     * @param path the path of the tab that we want to close.
     */
    void closeTab(Path path) {
        var tab = tabsByPath.get(path);
        if (tab == null) {
            return;
        }
        tabsByComponent.remove(tab.getViewComponent());
        tabsByPath.remove(path);
        tabbedPane.remove(tab.getViewComponent());
    }

    /**
     * Closes all of the opened tabs but the specified {@code index}.
     *
     * @param index the index of the specified index.
     */
    private void closeAllBut(int index) {
        var components = new ArrayList<Component>();
        for (var tabIndex = 0; tabIndex < tabbedPane.getTabCount(); tabIndex++) {
            if (tabIndex == index) {
                continue;
            }
            components.add(tabbedPane.getComponentAt(tabIndex));
        }
        for (var component : components) {
            var editorTab = tabsByComponent.get(component);
            if (!editorTab.requestClose()) {
                break;
            }
        }
    }

    /**
     * Closes all of the tabs to the left of the selected tab {@code index}.
     *
     * @param index the index of the tab.
     */
    private void closeAllToLeft(int index) {
        var components = new ArrayList<Component>();
        for (var tabIndex = index - 1; tabIndex >= 0; tabIndex--) {
            components.add(tabbedPane.getComponentAt(tabIndex));
        }
        for (var component : components) {
            var editorTab = tabsByComponent.get(component);
            if (!editorTab.requestClose()) {
                break;
            }
        }
    }

    /**
     * Closes all of the tabs to the right of the selected tab {@code index}.
     *
     * @param index the index of the tab.
     */
    private void closeAllToRight(int index) {
        var components = new ArrayList<Component>();
        for (var tabIndex = index + 1; tabIndex < tabbedPane.getTabCount(); tabIndex++) {
            components.add(tabbedPane.getComponentAt(tabIndex));
        }
        for (var component : components) {
            var editorTab = tabsByComponent.get(component);
            if (!editorTab.requestClose()) {
                break;
            }
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

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor.area;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.shortcut.ShortcutManager;
import me.waliedyassen.runescript.editor.shortcut.common.CommonGroups;
import me.waliedyassen.runescript.editor.shortcut.common.CommonShortcuts;
import me.waliedyassen.runescript.editor.ui.editor.Editor;
import me.waliedyassen.runescript.editor.ui.editor.tab.EditorTab;
import me.waliedyassen.runescript.editor.ui.menu.action.ActionSource;
import me.waliedyassen.runescript.editor.ui.menu.action.list.ActionList;
import me.waliedyassen.runescript.editor.ui.tabbedpane.TabbedPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

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
    private final Map<Object, EditorTab> tabsByKey = new HashMap<>();

    /**
     * The tabs that are currently in the editor view.
     */
    private final Map<Component, EditorTab> tabsByComponent = new HashMap<>();

    /**
     * The editor tab events handler.
     */
    //private final EditorTabHandler editorTabHandler;

    /**
     * Constructs a new {@link EditorView} type object instance.
     */
    public EditorView() {
        setLayout(new BorderLayout());
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSABLE, true);
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_CALLBACK, (BiConsumer<JTabbedPane, Integer>) (tabPane, tabIndex) -> {
            var component = tabPane.getComponentAt(tabIndex);
            if (component == null){
                return;
            }
            var tab = tabsByComponent.get(component);
            if (tab == null){
                return;
            }
            tab.requestClose();
        });
        add(tabbedPane, BorderLayout.CENTER);
        // We replace the original mouse listener of the tabbed pane.
        var delegateListener = tabbedPane.getMouseListeners()[0];
        tabbedPane.removeMouseListener(delegateListener);
        var editorTabHandler = new EditorTabHandler(delegateListener);
        tabbedPane.addMouseListener(editorTabHandler);
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
     * Adds a new editor tab to the editor view.
     *
     * @param editor the editor which we want to add as a tab.
     */
    public void addTab(Editor<?> editor) {
        var tab = tabsByKey.get(editor.getKey());
        if (tab != null) {
            throw new IllegalStateException("The specified editor is already opened by another editor tab");
        }
        var key = editor.getKey();
        tab = new EditorTab(editor);
        var component = editor.getViewComponent();
        tabsByKey.put(key, tab);
        tabsByComponent.put(component, tab);
        tabbedPane.addTab(editor.getTitle(),editor.getIcon(),component);
        selectTab(key);


    }

    /**
     * Selects the tab with the specified {@link Object key}.
     *
     * @param key the key which we want to select it's associated tab
     * @return <code>true</code> if the tab was selected or <code>false</code> if it was not.
     */
    public boolean selectTab(Object key) {
        var tab = tabsByKey.get(key);
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
        for (var key : tabsByKey.keySet().toArray(new Object[0])) {
            var tab = tabsByKey.get(key);
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
     * Requests the tab with the specified {@link Object key} to be closed.
     *
     * @param key <code>true</code> if the tab was closed otherwise <code>false</code>.
     */
    public boolean requestClose(Object key) {
        var tab = tabsByKey.get(key);
        if (tab == null) {
            return true;
        }
        return tab.requestClose();
    }

    /**
     * Removes the tab with the specified {@link Object key} if it is opened in the editor.
     *
     * @param key the key which we want to close it's associated tab.
     */
    public void removeTab(Object key) {
        var tab = tabsByKey.get(key);
        if (tab == null) {
            return;
        }
        tabsByComponent.remove(tab.getViewComponent());
        tabsByKey.remove(key);
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

    /**
     * Returns the {@link EditorTab} with the specified {@code key}.
     *
     * @param key the key of the editor tab that we want.
     * @return the the  {@link EditorTab} if it was present otherwise {@code null}.
     */
    public EditorTab getTab(Object key) {
        return tabsByKey.get(key);
    }

    /**
     * Represents the handler of the editor tab mouse listener.
     *
     * @author Walied K. Yasen
     */
    @RequiredArgsConstructor
    private final class EditorTabHandler implements MouseListener {

        private final MouseListener delegateHandler;

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            delegateHandler.mouseClicked(translateMouseEvent(e));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mousePressed(MouseEvent e) {
            e = translateMouseEvent(e);
            if (SwingUtilities.isLeftMouseButton(e)) {
                delegateHandler.mousePressed(e);
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
                requestClose(tab.getEditor().getKey());
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

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            delegateHandler.mouseReleased(translateMouseEvent(e));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseEntered(MouseEvent e) {
            delegateHandler.mouseEntered(translateMouseEvent(e));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseExited(MouseEvent e) {
            delegateHandler.mouseExited(translateMouseEvent(e));
        }

        /**
         * Translates the specified {@link MouseEvent} to be for the {@link #tabbedPane} object.
         *
         * @param event the event that we want to translate.
         * @return the translated {@link MouseEvent} if the parent needs changing or the same {@link MouseEvent event}
         * that was passed as a parameter..
         */
        private MouseEvent translateMouseEvent(MouseEvent event) {
            if (event.getComponent() == tabbedPane) {
                return event;
            } else {
                return SwingUtilities.convertMouseEvent(event.getComponent(), event, tabbedPane);
            }
        }
    }
}

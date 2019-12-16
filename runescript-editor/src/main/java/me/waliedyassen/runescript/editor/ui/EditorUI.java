/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui;

import com.alee.api.data.CompassDirection;
import com.alee.extended.dock.SidebarButtonVisibility;
import com.alee.extended.dock.WebDockablePane;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.menu.WebMenu;
import com.alee.laf.menu.WebMenuBar;
import com.alee.managers.style.StyleId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.editor.RuneScriptEditor;
import me.waliedyassen.runescript.editor.property.impl.StringProperty;
import me.waliedyassen.runescript.editor.ui.editor.EditorView;
import me.waliedyassen.runescript.editor.ui.explorer.ExplorerView;
import me.waliedyassen.runescript.editor.ui.status.StatusBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * The user-interface of the RuneScript Editor.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class EditorUI implements WindowListener {

    /**
     * The main frame of the user-interface.
     */
    private final JFrame frame = new JFrame();

    /**
     * The view component of the explorer.
     */
    @Getter
    private final ExplorerView explorerView = new ExplorerView();

    /**
     * The view component of the code editor.
     */
    @Getter
    private final EditorView editorView = new EditorView();

    /**
     * The current frame title property.
     */
    @Getter
    private final StringProperty title = new StringProperty();

    /**
     * The status bar of the editor.
     */
    @Getter
    private final StatusBar statusBar = new StatusBar();

    /**
     * The owner {@link RuneScriptEditor} object.
     */
    private final RuneScriptEditor editor;

    /**
     * Initialises the user-interface.
     */
    public void initialise() {
        initialiseComponents();
        initialiseProperties();
    }

    /**
     * Initialises the components of the editor.
     */
    private void initialiseComponents() {
        initialiseFrame();
        initialiseDocks();
        initialiseMenu();
        frame.add(statusBar, BorderLayout.SOUTH);
    }

    /**
     * Initialises the main frame component.
     */
    private void initialiseFrame() {
        // Setup the initial frame properties.
        title.bind(frame::setTitle);
        frame.setSize(1270, 800);
        frame.setLocationRelativeTo(null);
        // Add the window listener to the frame.
        frame.addWindowListener(this);
        // This makes it so it won't style the frame using the WebLAF style.
        frame.getRootPane().putClientProperty(StyleId.STYLE_PROPERTY, StyleId.frame);
    }

    /**
     * Initialise the docking system and docking views.
     */
    private void initialiseDocks() {
        // Setup the docking pane properties.
        var pane = new WebDockablePane(StyleId.dockablepaneCompact);
        pane.setSidebarButtonVisibility(SidebarButtonVisibility.anyMinimized);
        frame.add(pane, BorderLayout.CENTER);
        // Setup the docking pane content.
        pane.setContent(editorView);
        // Setup the explorer view.
        explorerView.setPosition(CompassDirection.west);
        explorerView.setPreferredSize(230, 600);
        explorerView.setClosable(false);
        pane.addFrame(explorerView);
    }

    /**
     * Initialises the menu bar of the editor.
     */
    private void initialiseMenu() {
        var bar = new WebMenuBar();
        var fileMenu = new WebMenu("File");
        {
            var menuItem = new JMenuItem("Open");
            menuItem.setPreferredSize(new Dimension(100, 20));
            menuItem.addActionListener((evt) -> {
                if (!editor.getProjectManager().getCurrentProject().isEmpty()) {
                    editor.getProjectManager().close();
                }
                var chooser = new WebFileChooser(StyleId.filechooser, editor.getSettings().getCachedPath("open-project").toFile());
                chooser.setDialogTitle("Choose a project directory");
                if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                var result = chooser.getSelectedFile().toPath();
                editor.getSettings().setCachedPath("open-project", result);
                editor.getProjectManager().open(result);
            });
            fileMenu.add(menuItem);
            editor.getProjectManager().getInactiveProperty().bind(menuItem::setEnabled);

            menuItem = new JMenuItem("Close");
            menuItem.setPreferredSize(new Dimension(100, 20));
            menuItem.addActionListener((evt) -> editor.getProjectManager().close());
            fileMenu.add(menuItem);
            editor.getProjectManager().getActiveProperty().bind(menuItem::setEnabled);

            fileMenu.addSeparator();

            menuItem = new JMenuItem("Create");
            menuItem.setPreferredSize(new Dimension(100, 20));
            menuItem.addActionListener((evt) -> editor.getProjectManager().close());
            fileMenu.add(menuItem);
            editor.getProjectManager().getInactiveProperty().bind(menuItem::setEnabled);

            fileMenu.addSeparator();

            menuItem = new JMenuItem("Exit");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
            menuItem.setPreferredSize(new Dimension(100, 20));
            menuItem.addActionListener((evt) -> windowClosing(null));
            fileMenu.add(menuItem);

        }
        bar.add(fileMenu);
        frame.setJMenuBar(bar);
    }

    /**
     * Initialises the properties of the editor.
     */
    private void initialiseProperties() {
        title.set("RuneScript Editor");
        statusBar.getText().set("Ready");
    }

    /**
     * Shows the user-interface if it is not visible.
     */
    public void show() {
        if (frame.isVisible()) {
            return;
        }
        frame.setVisible(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowOpened(WindowEvent e) {
        // NOOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowClosing(WindowEvent e) {
        if (!frame.isVisible()) {
            return;
        }
        frame.dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowClosed(WindowEvent e) {
        // NOOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowIconified(WindowEvent e) {
        // NOOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowDeiconified(WindowEvent e) {
        // NOOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowActivated(WindowEvent e) {
        // NOOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowDeactivated(WindowEvent e) {
        // NOOP
    }
}

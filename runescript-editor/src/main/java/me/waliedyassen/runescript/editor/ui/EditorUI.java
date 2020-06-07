/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.editor.RuneScriptEditor;
import me.waliedyassen.runescript.editor.ui.editor.area.EditorView;
import me.waliedyassen.runescript.editor.ui.errors.ErrorsView;
import me.waliedyassen.runescript.editor.ui.explorer.ExplorerView;
import me.waliedyassen.runescript.editor.ui.explorer.tree.node.ProjectNode;
import me.waliedyassen.runescript.editor.ui.frame.TitledFrame;
import me.waliedyassen.runescript.editor.ui.frame.top.TitleBar;
import me.waliedyassen.runescript.editor.ui.frame.top.ToolBar;
import me.waliedyassen.runescript.editor.ui.frame.top.TopUi;
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
    @Getter
    private final TitledFrame frame = new TitledFrame();

    /**
     * The title bar of the main frame.
     */
    @Getter
    private final TopUi topUi = new TopUi(frame.getTitleBar(), new ToolBar());

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
     * The errors component of the code editor.
     */
    @Getter
    private final ErrorsView errorsView = new ErrorsView();

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
        editor.getProjectManager().getCurrentProject().addListener(project -> {
            var tree = explorerView.getTree();
            tree.clear();
            if (project != null) {
                tree.getRoot().add(new ProjectNode(tree, project));
            }
            tree.getModel().nodeStructureChanged(tree.getRoot());
        });
    }

    /**
     * Initialises the components of the editor.
     */
    private void initialiseComponents() {
        initialiseFrame();
        initialiseMenu();
        initialiseDocks();
        statusBar.setOpaque(false);
        frame.add(statusBar, BorderLayout.SOUTH);
        frame.pack();
    }

    /**
     * Initialises the main frame component.
     */
    private void initialiseFrame() {
        frame.setPreferredSize(new Dimension(1270, 768));
        frame.setMinimumSize(new Dimension(600, 400));
        frame.addWindowListener(this);
        frame.add(topUi, BorderLayout.NORTH);
    }

    /**
     * Initialise the docking system and docking views.
     */
    private void initialiseDocks() {
        var control = new CControl(frame);
        frame.add(control.getContentArea());
        var grid = new CGrid(control);

        var explorerArea = new DefaultSingleCDockable(ExplorerView.DOCK_ID, "Explorer", explorerView);
        var editorArea = new DefaultSingleCDockable(EditorView.DOCK_ID, "Editor", this.editorView);
        var errorsArea = new DefaultSingleCDockable(ErrorsView.DOCK_ID, "Errors", errorsView);
        editorArea.setCloseable(false);
        editorArea.setMaximizable(false);
        editorArea.setMinimizable(false);
        editorArea.setExternalizable(false);
        editorArea.setStackable(false);
        grid.add(0, 0, 0.2, 1, explorerArea);
        grid.add(0.2, 0, 0.8, 0.8, editorArea);
        grid.add(0.2, 0.8, 0.8, 0.2, errorsArea);
        control.getContentArea().deploy(grid);
    }

    /**
     * Initialises the menu bar of the editor.
     */
    private void initialiseMenu() {
        var bar = topUi.getTitleBar().getMenuBar();
        var fileMenu = new JMenu("File");
        {
            var menuItem = new JMenuItem("Open");
            menuItem.addActionListener((evt) -> {
                if (!editor.getProjectManager().getCurrentProject().isEmpty()) {
                    editor.getProjectManager().close();
                }
                var chooser = new JFileChooser(editor.getSettings().getCachedPath("open-project").toFile());
                chooser.setDialogTitle("Choose a project directory");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
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
            menuItem.addActionListener((evt) -> editor.getProjectManager().close());
            fileMenu.add(menuItem);
            editor.getProjectManager().getActiveProperty().bind(menuItem::setEnabled);
            fileMenu.addSeparator();

            menuItem = new JMenuItem("Create");
            menuItem.addActionListener((evt) -> editor.getProjectManager().close());
            fileMenu.add(menuItem);
            editor.getProjectManager().getInactiveProperty().bind(menuItem::setEnabled);

            fileMenu.addSeparator();

            menuItem = new JMenuItem("Exit");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
            menuItem.addActionListener((evt) -> windowClosing(null));
            fileMenu.add(menuItem);

        }
        bar.add(fileMenu);
        var editMenu = new JMenu("Edit");
        editMenu.add(new JMenuItem("Test"));
        bar.add(editMenu);
        var windowMenu = new JMenu("Window");
        windowMenu.add(new JMenuItem("Test"));
        bar.add(windowMenu);
        var helpMenu = new JMenu("Help");
        helpMenu.add(new JMenuItem("Test"));
        bar.add(helpMenu);
    }

    /**
     * Initialises the properties of the editor.
     */
    private void initialiseProperties() {
        editor.getProjectManager().getActiveProperty().bind((val) -> refreshTitle());
        statusBar.getText().set("Ready");
    }

    /**
     * Refreshes the title of the top bar.
     */
    private void refreshTitle() {
        var activeProject = editor.getProjectManager().getCurrentProject();
        topUi.getTitleBar().getText().set("RuneScript Editor" + (activeProject.isEmpty() ? "" : " - " + activeProject.get().getName()));
    }

    /**
     * Shows the user-interface if it is not visible.
     */
    public void show() {
        if (frame.isVisible()) {
            return;
        }
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.toFront();
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

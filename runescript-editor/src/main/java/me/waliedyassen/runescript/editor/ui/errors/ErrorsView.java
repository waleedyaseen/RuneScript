/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.errors;

import me.waliedyassen.runescript.editor.shortcut.ShortcutManager;
import me.waliedyassen.runescript.editor.shortcut.common.CommonGroups;
import me.waliedyassen.runescript.editor.ui.menu.action.ActionSource;
import me.waliedyassen.runescript.editor.ui.menu.action.list.ActionList;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Represents the errors view of the editor.
 *
 * @author Walied K. Yassen
 */
public final class ErrorsView extends JPanel implements ActionSource {

    /**
     * The Dock ID of the errors view.
     */
    public static final String DOCK_ID = "editor.errors";

    /**
     * The table model of the errors view.
     */
    private final ErrorsTableModel model = new ErrorsTableModel();

    /**
     * The table of the errors view.
     */
    private final JTable table = new JTable(model);

    /**
     * Constructs a new {@link ErrorsView} type object instance.
     */
    public ErrorsView() {
        setLayout(new MigLayout("fill, ins 0", "[fill]"));
        var view = new JScrollPane(table);
        add(view);
        ShortcutManager.getInstance().bindShortcuts(CommonGroups.ERRORS, view, table);
    }


    /**
     * Adds a new error to the errors view.
     *
     * @param description the description of the error.
     * @param path    the path which leads to the error.
     * @param line    the line which leads to the error.
     */
    public void addError(String description, String path, int line) {
        SwingUtilities.invokeLater(() -> {
            model.addRow(new Object[]{description, path, "line " + line});
        });
    }

    public void removeErrorForPath(String targetPath) {
        SwingUtilities.invokeLater(() -> {
            for (var row = model.getRowCount() - 1; row >= 0; row--) {
                var path = (String) model.getValueAt(row, 1);
                if (path != null && path.contentEquals(targetPath)) {
                    model.removeRow(row);
                }
            }
        });
    }

    /**
     * Clears all of the errors currently in the view.
     */
    public void clearErrors() {
        table.clearSelection();
        model.setRowCount(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateActions(ActionList actionList) {
        // NOOP
    }

    /**
     * A table model implementation for the errors view.
     *
     * @author Walied K. Yassen
     */
    public static final class ErrorsTableModel extends DefaultTableModel {

        /**
         * Constructs a new {@link ErrorsTableModel} type object instance.
         */
        public ErrorsTableModel() {
            addColumn("Description");
            addColumn("Path");
            addColumn("Location");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

}

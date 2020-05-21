/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.explorer.tree.node;

import me.waliedyassen.runescript.CompilerError;
import me.waliedyassen.runescript.compiler.CompilerErrors;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.ui.dialog.DialogManager;
import me.waliedyassen.runescript.editor.ui.explorer.tree.ExplorerNode;
import me.waliedyassen.runescript.editor.ui.menu.action.list.ActionList;
import me.waliedyassen.runescript.parser.SyntaxError;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A file node in the project explorer tree.
 *
 * @author Walied K. Yassen
 */
public final class FileNode extends ExplorerNode<Path> {

    /**
     * Constructs a new {@link FileNode} type object instance.
     *
     * @param file the path which leads to the file.
     */
    public FileNode(Path file) {
        super(file);
        setUserObject(file.getFileName());
        setAllowsChildren(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateActions(ActionList actionList) {
        if (!isSourceFile()) {
            return;
        }
        actionList.addAction("Open", (source) -> openFile());
        actionList.addSeparator();
        actionList.addAction("Pack", (source) -> packFile());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActionClick() {
        openFile();
    }

    /**
     * Opens this file in the editor.
     */
    private void openFile() {
        var editorView = Api.getApi().getEditorView();
        if (editorView.selectTab(getValue())) {
            return;
        }
        Api.getApi().getEditorView().addTab(getValue());
    }

    /**
     * Packs the file.
     */
    private void packFile() {
        var projectManager = Api.getApi().getProjectManager();
        var project = projectManager.getCurrentProject().get();
        var compiler = Api.getApi().getCompiler();
        Api.getApi().getUi().getErrorsView().clearErrors();
        try {
            var scripts = compiler.compile(Files.readAllBytes(getValue()));
            for (var script : scripts) {
                project.getPackManager().pack(getValue(), script.getName(), script.getData());
            }
        } catch (IOException e) {
            DialogManager.showErrorDialog("Pack Error", "An I/O error occurred while trying to read the file from the disk.");
        } catch (CompilerErrors errors) {
            for (var error : errors.getErrors()) {
                Api.getApi().getUi().getErrorsView().addError("Unknown", error.getRange().getStart().getColumn(), error.getRange().getStart().getColumn(), error.getMessage());
            }
            DialogManager.showErrorDialog("Pack Error", "The file you tried to pack contains compile errors.\nPlease fix them before trying to pack again.");
        }
    }

    /**
     * Checks whether or not the file node is for a source file.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isSourceFile() {
        return getValue().getFileName().toString().endsWith(".rs2");
    }
}

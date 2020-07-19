/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.ScriptCompiler;
import me.waliedyassen.runescript.config.compiler.ConfigCompiler;
import me.waliedyassen.runescript.editor.project.ProjectManager;
import me.waliedyassen.runescript.editor.ui.EditorUI;
import me.waliedyassen.runescript.editor.ui.editor.area.EditorView;
import me.waliedyassen.runescript.editor.ui.explorer.ExplorerView;
import me.waliedyassen.runescript.editor.ui.explorer.tree.ExplorerTree;
import me.waliedyassen.runescript.editor.ui.menu.action.ActionManager;

/**
 * A class which is responsible for making things easier to access in the editor api.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class Api {

    /**
     * The current supplied {@link Api} instance.
     */
    private static Api api;

    /**
     * The RuneScript editor object.
     */
    private final RuneScriptEditor editor;

    /**
     * Returns the {@link EditorUI} object instance.
     *
     * @return the {@link EditorUI} object instance
     */
    public EditorUI getUi() {
        return editor.getUi();
    }

    /**
     * Returns the {@link ExplorerView} object instance.
     *
     * @return the {@link ExplorerView} object instance.
     */
    public ExplorerView getExplorerView() {
        return editor.getUi().getExplorerView();
    }

    /**
     * Returns the {@link ExplorerTree} object instance.
     *
     * @return the {@link ExplorerTree} object instance.
     */
    public ExplorerTree getExplorerTree() {
        return editor.getUi().getExplorerView().getTree();
    }

    /**
     * Returns the {@link EditorView} object instance.
     *
     * @return the {@link EditorView} object instance.
     */
    public EditorView getEditorView() {
        return editor.getUi().getEditorView();
    }

    /**
     * Returns the {@link ActionManager} object instance.
     *
     * @return the {@link ActionManager} object instance.
     */
    public ActionManager getActionManager() {
        return ActionManager.getInstance();
    }

    /**
     * Returns the {@link ProjectManager} object instance.
     *
     * @return the {@link ProjectManager} object instance.
     */
    public ProjectManager getProjectManager() {
        return editor.getProjectManager();
    }

    /**
     * Returns the {@link ScriptCompiler} object instance.
     *
     * @return the {@link ScriptCompiler} object instance.
     */
    public ScriptCompiler getScriptCompiler() {
        return getProjectManager().getCurrentProject().get().getScriptsCompiler();
    }

    /**
     * Returns the {@link ConfigCompiler} object instance.
     *
     * @return the {@link ConfigCompiler} object instance.
     */
    public ConfigCompiler getConfigCompiler() {
        return getProjectManager().getCurrentProject().get().getConfigsCompiler();
    }

    /**
     * Returns the supplied {@link Api} object instance.
     *
     * @return the supplied {@link Api} object instance.
     */
    public static Api getApi() {
        if (api == null) {
            throw new IllegalStateException("getApi() was called before setApi()");
        }
        return api;
    }

    /**
     * Sets the {@link Api} object to use for static access.
     *
     * @param api
     *         the {@link Api} object to set.
     */
    static void setApi(Api api) {
        if (Api.api != null) {
            throw new IllegalStateException("An api object was already assigned");
        }
        Api.api = api;
    }
}

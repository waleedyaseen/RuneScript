/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.explorer;

import com.alee.extended.dock.WebDockableFrame;

/**
 * The explorer file tree docking view.
 *
 * @author Walied K. Yassen
 */
public final class ExplorerView extends WebDockableFrame {

    /**
     * The docking view id of the component.
     */
    private static final String DOCK_ID = "explorer.view";

    /**
     * Constructs a new {@link ExplorerView} type object instance.
     */
    public ExplorerView() {
        super(DOCK_ID, "Explorer");
    }
}

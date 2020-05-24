/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.job;

import lombok.Getter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * A static-class which contains the common executors we are using in the IDE.
 *
 * @author Walied K. Yassen
 */
public final class WorkExecutor {

    /**
     * A scheduled executor which uses only one thread to schedule all of it's jobs, it's used for low priority tasks.
     */
    @Getter
    private static final ScheduledExecutorService singleThreadScheduler = Executors.newScheduledThreadPool(1);

    private WorkExecutor() {
        // NOOP
    }
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.editor.project.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.commons.document.Range;

/**
 * A cached error that is stored in a cached file.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class CachedError {

    /**
     * The range of the error in the source code.
     */
    @Getter
    private final Range range;

    /**
     * The message of the error.
     */
    @Getter
    private final String message;
}

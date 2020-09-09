/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.error;

import me.waliedyassen.runescript.compiler.CompilerError;

public final class ThrowingErrorReporter extends ErrorReporter {

    @Override
    public void addError(CompilerError error) {
        throw error;
    }
}
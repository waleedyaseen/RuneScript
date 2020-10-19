/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.sw;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.codegen.block.Label;

/**
 * A code generated switch case.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class SwitchCase {

    /**
     * The keys of the switch case.
     */
    @Getter
    public final Object[] keys;

    /**
     * The label which the switch case will jump to.
     */
    @Getter
    private final Label label;
}

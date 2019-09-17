/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.var;

import lombok.Data;
import lombok.Getter;

/**
 * Represents a configuration variable array properties.
 *
 * @author Walied K. Yassen
 */
@Data
public final class ConfigVarArray {

    /**
     * The maximum size of the array.
     */
    @Getter
    public final int size;
}

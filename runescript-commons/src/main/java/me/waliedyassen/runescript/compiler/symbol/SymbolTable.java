/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.symbol;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.symbol.impl.RuntimeConstantInfo;
import me.waliedyassen.runescript.type.primitive.PrimitiveType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a compile-time symbol table, it contains various information about different symbol types such as
 * constants, commands, scripts, and global variables.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public class SymbolTable {

    /**
     * The defined configurations map.
     */
    @Getter
    private final Map<PrimitiveType<?>, SymbolList<?>> configs = new HashMap<>();

    /**
     * The defined runtime constants.
     */
    @Getter
    private final SymbolList<RuntimeConstantInfo> runtimeConstants = new SymbolList<>();

    /**
     * The parent symbol table.
     */
    @Getter
    private final SymbolTable parent;

    /**
     * Whether to allow the un-defining of symbols from this table.
     */
    @Getter
    protected final boolean allowRemoving;

    /**
     * Constructs a new {@link SymbolTable} type object instance.
     *
     * @param allowRemoving whether to allow removing symbols from this table.
     */
    public SymbolTable(boolean allowRemoving) {
        this(null, allowRemoving);
        try {
            Class.forName(PrimitiveType.class.getName(), true, PrimitiveType.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Symbol> void read(PrimitiveType<T> type, Path file) throws IOException {
        var loader = type.getLoader();
        if (loader == null) {
            throw new IllegalStateException();
        }
        Files.lines(file).forEach(line -> defineConfig(type, loader.load(line)));
    }

    public <T extends Symbol> void write(PrimitiveType<T> type, Path file) throws IOException {
        var loader = type.getLoader();
        if (loader == null) {
            throw new IllegalStateException();
        }

        Files.lines(file).forEach(line -> defineConfig(type, loader.load(line)));
    }

    public <T extends Symbol> void defineConfig(PrimitiveType<T> type, T info) {
        if (lookupConfig(type, info.getName()) != null) {
            throw new IllegalArgumentException("The configuration '" + info.getName() + "' is already defined.");
        }
        //noinspection unchecked
        var list = (SymbolList<T>) configs.get(type);
        if (list == null) {
            list = new SymbolList<>();
            configs.put(type, list);
        }
        list.add(info);
    }

    public <T extends Symbol> T lookupConfig(PrimitiveType<T> type, String name) {
        //noinspection unchecked
        var list = (SymbolList<T>) configs.get(type);
        T info = null;
        if (list != null) {
            info = list.lookupByName(name);
        }
        if (info == null && parent != null) {
            info = parent.lookupConfig(type, name);
        }
        return info;
    }

    public Symbol lookupVariable(String name) {
        var varConfig = lookupConfig(PrimitiveType.VARP.INSTANCE, name);
        if (varConfig != null) {
            return varConfig;
        }
        var varBitConfig = lookupConfig(PrimitiveType.VARBIT.INSTANCE, name);
        if (varBitConfig != null) {
            return varBitConfig;
        }
        var varcConfig = lookupConfig(PrimitiveType.VARC.INSTANCE, name);
        return varcConfig;
    }

    public PrimitiveType<?> lookupVariableType(String name) {
        var varConfig = lookupConfig(PrimitiveType.VARP.INSTANCE, name);
        if (varConfig != null) {
            return varConfig.getType();
        }
        var varBitConfig = lookupConfig(PrimitiveType.VARBIT.INSTANCE, name);
        if (varBitConfig != null) {
            return PrimitiveType.INT.INSTANCE;
        }
        var varcConfig = lookupConfig(PrimitiveType.VARC.INSTANCE, name);
        if (varcConfig != null) {
            return varcConfig.getType();
        }
        return null;
    }

    public PrimitiveType<?> lookupVariableDomain(String name) {
        var varConfig = lookupConfig(PrimitiveType.VARP.INSTANCE, name);
        if (varConfig != null) {
            return PrimitiveType.VARP.INSTANCE;
        }
        var varBitConfig = lookupConfig(PrimitiveType.VARBIT.INSTANCE, name);
        if (varBitConfig != null) {
            return PrimitiveType.VARBIT.INSTANCE;
        }
        var varcConfig = lookupConfig(PrimitiveType.VARC.INSTANCE, name);
        if (varcConfig != null) {
            return PrimitiveType.VARC.INSTANCE;
        }
        return null;
    }

    /**
     * Defines a new runtime constant symbol in this table.
     *
     * @param name  the name of the runtime constant.
     * @param type  the type of the runtime constant.
     * @param value the value of the runtime constant.
     */
    public void defineRuntimeConstant(String name, int id, PrimitiveType<?> type, Object value) {
        if (lookupRuntimeConstant(name) != null) {
            throw new IllegalArgumentException("The runtime constant '" + name + "' is already defined.");
        }
        runtimeConstants.add(new RuntimeConstantInfo(name, id, type, value));
    }

    /**
     * Looks-up for the {@link RuntimeConstantInfo} with the specified {@code name}.
     *
     * @param name the name of the runtime constant.
     * @return the {@link RuntimeConstantInfo} if it was present otherwise {@code null}.
     */
    public RuntimeConstantInfo lookupRuntimeConstant(String name) {
        var info = runtimeConstants.lookupByName(name);
        if (info == null && parent != null) {
            info = parent.lookupRuntimeConstant(name);
        }
        return info;
    }

    /**
     * Creates a nested sub symbol table.
     *
     * @return the created {@link SymbolTable} object.
     */
    public SymbolTable createSubTable() {
        return new SymbolTable(this, true);
    }
}

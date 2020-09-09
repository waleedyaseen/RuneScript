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
import lombok.var;
import me.waliedyassen.runescript.compiler.symbol.impl.*;
import me.waliedyassen.runescript.type.PrimitiveType;
import me.waliedyassen.runescript.type.Type;

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
     * The parent symbol table.
     */
    @Getter
    private final SymbolTable parent;

    /**
     * The defined constants map.
     */
    @Getter
    private final Map<String, ConstantInfo> constants = new HashMap<>();

    /**
     * The defined configurations map.
     */
    @Getter
    private final Map<String, ConfigInfo> configs = new HashMap<>();

    /**
     * The defined graphics map.
     */
    @Getter
    private final Map<String, GraphicInfo> graphics = new HashMap<>();

    /**
     * The defined runtime constants.
     */
    @Getter
    private final Map<String, RuntimeConstantInfo> runtimeConstants = new HashMap<>();

    /**
     * Constructs a new {@link SymbolTable} type object instance.
     */
    public SymbolTable() {
        this(null);
    }

    /**
     * Defines a new constant symbol in this table.
     *
     * @param name
     *         the name of the constant.
     * @param type
     *         the type of the constant.
     * @param value
     *         the value of the constant.
     */
    public void defineConstant(String name, Type type, Object value) {
        if (lookupConfig(name) != null) {
            throw new IllegalArgumentException("The constant '" + name + "' is already defined.");
        }
        constants.put(name, new ConstantInfo(name, type, value));
    }

    /**
     * Looks-up for the {@link ConstantInfo constant information} with the specified {@code name}.
     *
     * @param name
     *         the name of the constant.
     *
     * @return the {@link ConstantInfo} if it was present otherwise {@code null}.
     */
    public ConstantInfo lookupConstant(String name) {
        var info = constants.get(name);
        if (info == null && parent != null) {
            info = parent.lookupConstant(name);
        }
        return info;
    }

    /**
     * Defines the specified {@link ConfigInfo} in the symbol table.
     *
     * @param info
     *         the configuration info object to define.
     */
    public void defineConfig(ConfigInfo info) {
        if (lookupConfig(info.getName()) != null) {
            throw new IllegalArgumentException("The configuration '" + info.getName() + "' is already defined.");
        }
        configs.put(info.getName(), info);
    }

    /**
     * Defines a new configuration type value symbol in this table.
     *
     * @param name
     *         the name of the configuration.
     * @param type
     *         the type of the configuration.
     * @param contentType
     *         the content type of the configuration.
     *
     * @return the created {@link ConfigInfo} object.
     */
    public ConfigInfo defineConfig(String name, Type type, Type contentType) {
        if (lookupConfig(name) != null) {
            throw new IllegalArgumentException("The configuration '" + name + "' is already defined.");
        }
        var config = new ConfigInfo(name, type, contentType);
        configs.put(name, config);
        return config;
    }

    /**
     * Undefines the configuration with the specified {@code name}.
     *
     * @param name
     *         the name of the configuration.
     */
    public void undefineConfig(String name) {
        configs.remove(name);
    }

    /**
     * Looks-up for the {@link ConfigInfo configuration information} with the specified {@code name}.
     *
     * @param name
     *         the name of the configuration type value.
     *
     * @return the {@link ConfigInfo} if it was present otherwise {@code null}.
     */
    public ConfigInfo lookupConfig(String name) {
        var info = configs.get(name);
        if (info == null && parent != null) {
            info = parent.lookupConfig(name);
        }
        return info;
    }


    /**
     * Looks-up for the {@link ConfigInfo variable information} with the specified {@code name}.
     *
     * @param name
     *         the name of the variable configuration type value.
     *
     * @return the {@link ConfigInfo} if it was present otherwise {@code null}.
     */
    public ConfigInfo lookupVariable(String name) {
        var config = lookupConfig(name);
        if (config == null) {
            return null;
        }
        switch ((PrimitiveType) config.getType()) {
            case VAR:
            case VARBIT:
            case VARCINT:
            case VARCSTR:
                return config;
        }
        return null;
    }

    /**
     * Defines a new graphic symbol in this table.
     *
     * @param name
     *         the name of the graphic.
     * @param id
     *         the id of the graphic.
     */
    public void defineGraphic(String name, int id) {
        if (lookupGraphic(name) != null) {
            throw new IllegalArgumentException("The graphic '" + name + "' is already defined.");
        }
        graphics.put(name, new GraphicInfo(name, id));
    }

    /**
     * Looks-up for the {@link GraphicInfo} with the specified {@code name}.
     *
     * @param name
     *         the name of the graphic.
     *
     * @return the {@link GraphicInfo} if it was present otherwise {@code null}.
     */
    public GraphicInfo lookupGraphic(String name) {
        var info = graphics.get(name);
        if (info == null && parent != null) {
            info = parent.lookupGraphic(name);
        }
        return info;
    }

    /**
     * Defines a new runtime constant symbol in this table.
     *
     * @param name
     *         the name of the runtime constant.
     * @param type
     *         the type of the runtime constant.
     * @param value
     *         the value of the runtime constant.
     */
    public void defineRuntimeConstant(String name, PrimitiveType type, Object value) {
        if (lookupRuntimeConstant(name) != null) {
            throw new IllegalArgumentException("The runtime constant '" + name + "' is already defined.");
        }
        runtimeConstants.put(name, new RuntimeConstantInfo(name, type, value));
    }

    /**
     * Looks-up for the {@link RuntimeConstantInfo} with the specified {@code name}.
     *
     * @param name
     *         the name of the runtime constant.
     *
     * @return the {@link RuntimeConstantInfo} if it was present otherwise {@code null}.
     */
    public RuntimeConstantInfo lookupRuntimeConstant(String name) {
        var info = runtimeConstants.get(name);
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
        return new SymbolTable(this);
    }
}

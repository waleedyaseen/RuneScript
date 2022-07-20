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
import me.waliedyassen.runescript.compiler.symbol.impl.ConfigInfo;
import me.waliedyassen.runescript.compiler.symbol.impl.ConstantInfo;
import me.waliedyassen.runescript.compiler.symbol.impl.GraphicInfo;
import me.waliedyassen.runescript.compiler.symbol.impl.RuntimeConstantInfo;
import me.waliedyassen.runescript.type.Type;
import me.waliedyassen.runescript.type.primitive.PrimitiveType;

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
     * The defined constants map.
     */
    @Getter
    private final SymbolList<ConstantInfo> constants = new SymbolList<>();

    /**
     * The defined configurations map.
     */
    @Getter
    private final Map<Type, SymbolList<ConfigInfo>> configs = new HashMap<>();

    /**
     * The defined graphics map.
     */
    @Getter
    private final SymbolList<GraphicInfo> graphics = new SymbolList<>();

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
    }

    /**
     * Defines a new constant symbol in this table.
     *
     * @param name  the name of the constant.
     * @param type  the type of the constant.
     * @param value the value of the constant.
     */
    public void defineConstant(String name, Type type, Object value) {
        if (lookupConstant(name) != null) {
            throw new IllegalArgumentException("The constant '" + name + "' is already defined.");
        }
        constants.add(new ConstantInfo(name, type, value));
    }

    /**
     * Looks-up for the {@link ConstantInfo constant information} with the specified {@code name}.
     *
     * @param name the name of the constant.
     * @return the {@link ConstantInfo} if it was present otherwise {@code null}.
     */
    public ConstantInfo lookupConstant(String name) {
        var info = constants.lookupByName(name);
        if (info == null && parent != null) {
            info = parent.lookupConstant(name);
        }
        return info;
    }

    /**
     * Defines the specified {@link ConfigInfo} in the symbol table.
     *
     * @param info the configuration info object to define.
     */
    public void defineConfig(ConfigInfo info) {
        if (lookupConfig(info.getType(), info.getName()) != null) {
            throw new IllegalArgumentException("The configuration '" + info.getName() + "' is already defined.");
        }
        var list = configs.get(info.getType());
        if (list == null) {
            list = new SymbolList<>();
            configs.put(info.getType(), list);
        }
        list.add(info);
    }

    /**
     * Defines a new configuration type value symbol in this table.
     *
     * @param name        the name of the configuration.
     * @param type        the type of the configuration.
     * @param contentType the content type of the configuration.
     * @return the created {@link ConfigInfo} object.
     */
    public ConfigInfo defineConfig(String name, Type type, Type contentType) {
        var configInfo = new ConfigInfo(name, type, contentType);
        defineConfig(configInfo);
        return configInfo;
    }

    /**
     * Un-defines the configuration with the specified {@code name}.
     *
     * @param name the name of the configuration.
     */
    public void undefineConfig(String name) {
        if (!allowRemoving) {
            return;
        }
        configs.remove(name);
    }

    /**
     * Looks-up for the {@link ConfigInfo configuration information} with the specified {@code name}.
     *
     * @param name the name of the configuration type value.
     * @return the {@link ConfigInfo} if it was present otherwise {@code null}.
     */
    public ConfigInfo lookupConfig(Type type, String name) {
        var list = configs.get(type);
        ConfigInfo info = null;
        if (list != null) {
            info = list.lookupByName(name);
        }
        if (info == null && parent != null) {
            info = parent.lookupConfig(type, name);
        }
        return info;
    }


    /**
     * Looks-up for the {@link ConfigInfo variable information} with the specified {@code name}.
     *
     * @param name the name of the variable configuration type value.
     * @return the {@link ConfigInfo} if it was present otherwise {@code null}.
     */
    public ConfigInfo lookupVariable(String name) {
        var varConfig = lookupConfig(PrimitiveType.VAR, name);
        if (varConfig != null) {
            return varConfig;
        }
        var varBitConfig = lookupConfig(PrimitiveType.VARBIT, name);
        if (varBitConfig != null) {
            return varBitConfig;
        }
        var varcIntConfig = lookupConfig(PrimitiveType.VARCINT, name);
        if (varcIntConfig != null) {
            return varcIntConfig;
        }
        var varcStrConfig = lookupConfig(PrimitiveType.VARCSTR, name);
        if (varcStrConfig != null) {
            return varcStrConfig;
        }
        return null;
    }

    /**
     * Defines a new graphic symbol in this table.
     *
     * @param name the name of the graphic.
     * @param id   the id of the graphic.
     */
    public void defineGraphic(String name, int id) {
        if (lookupGraphic(name) != null) {
            throw new IllegalArgumentException("The graphic '" + name + "' is already defined.");
        }
        graphics.add(new GraphicInfo(name, id));
    }

    /**
     * Looks-up for the {@link GraphicInfo} with the specified {@code name}.
     *
     * @param name the name of the graphic.
     * @return the {@link GraphicInfo} if it was present otherwise {@code null}.
     */
    public GraphicInfo lookupGraphic(String name) {
        var info = graphics.lookupByName(name);
        if (info == null && parent != null) {
            info = parent.lookupGraphic(name);
        }
        return info;
    }

    /**
     * Defines a new runtime constant symbol in this table.
     *
     * @param name  the name of the runtime constant.
     * @param type  the type of the runtime constant.
     * @param value the value of the runtime constant.
     */
    public void defineRuntimeConstant(String name, PrimitiveType type, Object value) {
        if (lookupRuntimeConstant(name) != null) {
            throw new IllegalArgumentException("The runtime constant '" + name + "' is already defined.");
        }
        runtimeConstants.add(new RuntimeConstantInfo(name, type, value));
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

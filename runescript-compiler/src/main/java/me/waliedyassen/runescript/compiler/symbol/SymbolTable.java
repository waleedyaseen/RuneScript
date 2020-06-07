/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.symbol;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.compiler.codegen.opcode.Opcode;
import me.waliedyassen.runescript.compiler.symbol.impl.*;
import me.waliedyassen.runescript.compiler.symbol.impl.script.Annotation;
import me.waliedyassen.runescript.compiler.symbol.impl.script.ScriptInfo;
import me.waliedyassen.runescript.compiler.symbol.impl.variable.VariableDomain;
import me.waliedyassen.runescript.compiler.symbol.impl.variable.VariableInfo;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;
import me.waliedyassen.runescript.type.PrimitiveType;
import me.waliedyassen.runescript.type.Type;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a compile-time symbol table, it contains various information about different symbol taypes such as
 * constants, commands, scripts, and global variables.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class SymbolTable {

    /**
     * The script name template.
     */
    private static final String SCRIPT_NAME_TEMPLATE = "[%s,%s]";

    /**
     * The parent symbol table.
     */
    private final SymbolTable parent;

    /**
     * The defined constants map.
     */
    @Getter
    private final Map<String, ConstantInfo> constants = new HashMap<>();

    /**
     * The defined commands map.
     */
    @Getter
    private final Map<String, CommandInfo> commands = new HashMap<>();

    /**
     * The defined configurations map.
     */
    @Getter
    private final Map<String, ConfigInfo> configs = new HashMap<>();

    /**
     * The defined scripts map.
     */
    @Getter
    private final Map<String, ScriptInfo> scripts = new HashMap<>();

    /**
     * The defined variables map.
     */
    @Getter
    private final Map<String, VariableInfo> variables = new HashMap<>();

    /**
     * The defined components map.
     */
    @Getter
    private final Map<String, InterfaceInfo> interfaces = new HashMap<>();

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
     * @param name  the name of the constant.
     * @param type  the type of the constant.
     * @param value the value of the constant.
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
     * @param name the name of the constant.
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
     * Defines a new command symbol in this table.
     *
     * @param opcode      the opcode of the command.
     * @param name        the name of the command.
     * @param type        the type of the command.
     * @param arguments   the arguments of hte command.
     * @param hook        whether or not this command is a hook command.
     * @param hookType    the type of transmits the hook must have if the hook is present.
     * @param alternative whether or not this command supports alternative calls.
     */
    public void defineCommand(Opcode opcode, String name, Type type, Type[] arguments, boolean hook, Type hookType, boolean alternative) {
        if (lookupCommand(name) != null) {
            throw new IllegalArgumentException("The command '" + name + "' is already defined.");
        }
        commands.put(name, new CommandInfo(opcode, name, type, arguments, hook, hookType, alternative));
    }

    /**
     * Looks-up for the {@link ConstantInfo command information} with the specified {@code name}.
     *
     * @param name the name of the command.
     * @return the {@link CommandInfo} if it was present otherwise {@code null}.
     */
    public CommandInfo lookupCommand(String name) {
        var info = commands.get(name);
        if (info == null && parent != null) {
            info = parent.lookupCommand(name);
        }
        return info;
    }

    /**
     * Defines a new configuration type value symbol in this table.
     *
     * @param id   the id of the configuration.
     * @param name the name of the configuration.
     * @param type the type of the configuration.
     */
    public void defineConfig(int id, String name, Type type) {
        if (lookupConfig(name) != null) {
            throw new IllegalArgumentException("The configuration '" + name + "' is already defined.");
        }
        configs.put(name, new ConfigInfo(id, name, type));
    }

    /**
     * Looks-up for the {@link ConfigInfo configuration information} with the specified {@code name}.
     *
     * @param name the name of the configuration type value.
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
     * Defines a new script symbol information in this table.
     *
     * @param info the script info that we want to define.
     */
    public void defineScript(ScriptInfo info) {
        defineScript(info.getAnnotations(), info.getTrigger(), info.getName(), info.getType(), info.getArguments());
    }

    /**
     * Defines a new script symbol information in this table.
     *
     * @param annotations the annotations of the script.
     * @param trigger     the trigger of the script.
     * @param name        the name of the script.
     * @param type        the type of the script.
     * @param arguments   the arguments type which the script takes.
     */
    public void defineScript(Map<String, Annotation> annotations, TriggerType trigger, String name, Type type, Type[] arguments) {
        defineScript(annotations, trigger, name, type, arguments, null);
    }

    /**
     * Defines a new script symbol information in this table.
     *
     * @param annotations  the annotations of the script.
     * @param trigger      the trigger of the script.
     * @param name         the name of the script.
     * @param type         the type of the script.
     * @param arguments    the arguments type which the script takes.
     * @param predefinedId the predefined id of the script.
     */
    public void defineScript(Map<String, Annotation> annotations, TriggerType trigger, String name, Type type, Type[] arguments, Integer predefinedId) {
        if (lookupScript(trigger, name) != null) {
            throw new IllegalArgumentException("The script '" + name + "' is already defined.");
        }
        scripts.put(String.format(SCRIPT_NAME_TEMPLATE, trigger.getRepresentation(), name), new ScriptInfo(annotations, name, trigger, type, arguments, predefinedId));
    }

    /**
     * Undefines the script with the specified {@link TriggerType trigger} and {@code name}.
     *
     * @param trigger the trigger type of the script that we want to undefine.
     * @param name    the name of the script of the script that we want to undefine.
     */
    public void undefineScript(TriggerType trigger, String name) {
        var fullName = String.format(SCRIPT_NAME_TEMPLATE, trigger.getRepresentation(), name);
        scripts.remove(fullName);
    }

    /**
     * Looks-up for the {@link ScriptInfo script information} with the specified {@code trigger} and {@code name}.
     *
     * @param trigger the trigger type of the script to lookup for.
     * @param name    the name of the script to lookup for.
     * @return the {@link ScriptInfo} if it was present otherwise {@code null}.
     */
    public ScriptInfo lookupScript(TriggerType trigger, String name) {
        var info = scripts.get(String.format(SCRIPT_NAME_TEMPLATE, trigger.getRepresentation(), name));
        if (info == null && parent != null) {
            info = parent.lookupScript(trigger, name);
        }
        return info;
    }

    /**
     * Defines a new variable symbol information in this table.
     *
     * @param domain the domain of the variable.
     * @param name   the name of the variable.
     * @param type   the type of the variable.
     */
    public void defineVariable(VariableDomain domain, String name, Type type) {
        if (lookupVariable(name) != null) {
            throw new IllegalArgumentException("The variable '" + name + "' is already defined.");
        }
        variables.put(name, new VariableInfo(domain, name, type));
    }

    /**
     * Looks-up for the {@link VariableInfo variable information} with the specified {@code name}.
     *
     * @param name the name of the variable to lookup for.
     * @return the {@link VariableInfo} if it was present otherwise {@code null}.
     */
    public VariableInfo lookupVariable(String name) {
        var info = variables.get(name);
        if (info == null && parent != null) {
            info = parent.lookupVariable(name);
        }
        return info;
    }

    /**
     * Defines a new interface symbol in this table.
     *
     * @param name the name of the interface.
     * @param id   the id of the interface.
     */
    public void defineInterface(String name, int id) {
        if (lookupInterface(name) != null) {
            throw new IllegalArgumentException("The constant '" + name + "' is already defined.");
        }
        interfaces.put(name, new InterfaceInfo(name, id));
    }

    /**
     * Looks-up for the {@link InterfaceInfo} with the specified {@code name}.
     *
     * @param name the name of the interface.
     * @return the {@link InterfaceInfo} if it was present otherwise {@code null}.
     */
    public InterfaceInfo lookupInterface(String name) {
        var info = interfaces.get(name);
        if (info == null && parent != null) {
            info = parent.lookupInterface(name);
        }
        return info;
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
        graphics.put(name, new GraphicInfo(name, id));
    }

    /**
     * Looks-up for the {@link GraphicInfo} with the specified {@code name}.
     *
     * @param name the name of the graphic.
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
     * @param name  the name of the runtime constant.
     * @param type  the type of the runtime constant.
     * @param value the value of the runtime constant.
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
     * @param name the name of the runtime constant.
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

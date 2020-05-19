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
import me.waliedyassen.runescript.compiler.codegen.opcode.Opcode;
import me.waliedyassen.runescript.compiler.symbol.impl.CommandInfo;
import me.waliedyassen.runescript.compiler.symbol.impl.ConfigInfo;
import me.waliedyassen.runescript.compiler.symbol.impl.ConstantInfo;
import me.waliedyassen.runescript.compiler.symbol.impl.script.Annotation;
import me.waliedyassen.runescript.compiler.symbol.impl.script.ScriptInfo;
import me.waliedyassen.runescript.compiler.symbol.impl.variable.VariableDomain;
import me.waliedyassen.runescript.compiler.symbol.impl.variable.VariableInfo;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;
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
    private final Map<String, ConstantInfo> constants = new HashMap<>();

    /**
     * The defined commands map.
     */
    private final Map<String, CommandInfo> commands = new HashMap<>();

    /**
     * The defined configurations map.
     */
    private final Map<String, ConfigInfo> configs = new HashMap<>();

    /**
     * The defined scripts map.
     */
    @Getter
    private final Map<String, ScriptInfo> scripts = new HashMap<>();

    /**
     * The defined variables map.
     */
    private final Map<String, VariableInfo> variables = new HashMap<>();

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
        if (constants.containsKey(name)) {
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
     * @param alternative whether or not this command supports alternative calls.
     */
    public void defineCommand(Opcode opcode, String name, Type type, Type[] arguments, boolean hook, boolean alternative) {
        if (commands.containsKey(name)) {
            throw new IllegalArgumentException("The command '" + name + "' is already defined.");
        }
        commands.put(name, new CommandInfo(opcode, name, type, arguments, hook, alternative));
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
        if (configs.containsKey(name)) {
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
     * @param annotations the annotations of the script.
     * @param trigger     the trigger of the script.
     * @param name        the name of the script.
     * @param type        the type of the script.
     * @param arguments   the arguments type which the script takes.
     */
    public void defineScript(Map<String, Annotation> annotations, TriggerType trigger, String name, Type type, Type[] arguments) {
        if (scripts.containsKey(name)) {
            throw new IllegalArgumentException("The script '" + name + "' is already defined.");
        }
        scripts.put(String.format(SCRIPT_NAME_TEMPLATE, trigger.getRepresentation(), name), new ScriptInfo(annotations, name, trigger, type, arguments));
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
        if (variables.containsKey(name)) {
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
     * Creates a nested sub symbol table.
     *
     * @return the created {@link SymbolTable} object.
     */
    public SymbolTable createSubTable() {
        return new SymbolTable(this);
    }
}

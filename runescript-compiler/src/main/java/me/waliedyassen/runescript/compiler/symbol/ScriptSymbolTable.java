/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.symbol;

import lombok.Getter;
import lombok.var;
import me.waliedyassen.runescript.compiler.codegen.opcode.Opcode;
import me.waliedyassen.runescript.compiler.symbol.impl.CommandInfo;
import me.waliedyassen.runescript.compiler.symbol.impl.ConstantInfo;
import me.waliedyassen.runescript.compiler.symbol.impl.script.Annotation;
import me.waliedyassen.runescript.compiler.symbol.impl.script.ScriptInfo;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;
import me.waliedyassen.runescript.type.Type;

import java.util.HashMap;
import java.util.Map;

public final class ScriptSymbolTable extends SymbolTable {

    /**
     * The script name template.
     */
    private static final String SCRIPT_NAME_TEMPLATE = "[%s,%s]";

    /**
     * The defined scripts map.
     */
    @Getter
    private final Map<String, ScriptInfo> scripts = new HashMap<>();

    /**
     * The defined commands map.
     */
    @Getter
    private final Map<String, CommandInfo> commands = new HashMap<>();

    /**
     * Constructs a new {@link SymbolTable} type object instance.
     */
    public ScriptSymbolTable() {
        super();
    }

    /**
     * Constructs a new {@link SymbolTable} type object instance.
     *
     * @param parent
     *         the parent symbol table object.
     */
    public ScriptSymbolTable(SymbolTable parent) {
        super(parent);
    }

    /**
     * Defines a new command symbol in this table.
     *
     * @param opcode
     *         the opcode of the command.
     * @param name
     *         the name of the command.
     * @param type
     *         the type of the command.
     * @param arguments
     *         the arguments of hte command.
     * @param hook
     *         whether or not this command is a hook command.
     * @param hookType
     *         the type of transmits the hook must have if the hook is present.
     * @param alternative
     *         whether or not this command supports alternative calls.
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
     * @param name
     *         the name of the command.
     *
     * @return the {@link CommandInfo} if it was present otherwise {@code null}.
     */
    public CommandInfo lookupCommand(String name) {
        var info = commands.get(name);
        if (info == null && getParent() != null) {
            info = getParent().lookupCommand(name);
        }
        return info;
    }

    /**
     * Defines a new script symbol information in this table.
     *
     * @param info
     *         the script info that we want to define.
     */
    public void defineScript(ScriptInfo info) {
        defineScript(info.getAnnotations(), info.getTrigger(), info.getName(), info.getType(), info.getArguments());
    }

    /**
     * Defines a new script symbol information in this table.
     *
     * @param annotations
     *         the annotations of the script.
     * @param trigger
     *         the trigger of the script.
     * @param name
     *         the name of the script.
     * @param type
     *         the type of the script.
     * @param arguments
     *         the arguments type which the script takes.
     */
    public void defineScript(Map<String, Annotation> annotations, TriggerType trigger, String name, Type type, Type[] arguments) {
        defineScript(annotations, trigger, name, type, arguments, null);
    }

    /**
     * Defines a new script symbol information in this table.
     *
     * @param annotations
     *         the annotations of the script.
     * @param trigger
     *         the trigger of the script.
     * @param name
     *         the name of the script.
     * @param type
     *         the type of the script.
     * @param arguments
     *         the arguments type which the script takes.
     * @param predefinedId
     *         the predefined id of the script.
     */
    public void defineScript(Map<String, Annotation> annotations, TriggerType trigger, String name, Type type, Type[] arguments, Integer predefinedId) {
        if (lookupScript(trigger, name) != null) {
            throw new IllegalArgumentException(String.format("The script '[%s,%s]' is already defined.", trigger.getRepresentation(), name));
        }
        scripts.put(String.format(SCRIPT_NAME_TEMPLATE, trigger.getRepresentation(), name), new ScriptInfo(annotations, name, trigger, type, arguments, predefinedId));
    }

    /**
     * Undefines the script with the specified {@link TriggerType trigger} and {@code name}.
     *
     * @param trigger
     *         the trigger type of the script that we want to undefine.
     * @param name
     *         the name of the script of the script that we want to undefine.
     */
    public void undefineScript(TriggerType trigger, String name) {
        var fullName = String.format(SCRIPT_NAME_TEMPLATE, trigger.getRepresentation(), name);
        scripts.remove(fullName);
    }

    /**
     * Looks-up for the {@link ScriptInfo script information} with the specified {@code trigger} and {@code name}.
     *
     * @param trigger
     *         the trigger type of the script to lookup for.
     * @param name
     *         the name of the script to lookup for.
     *
     * @return the {@link ScriptInfo} if it was present otherwise {@code null}.
     */
    public ScriptInfo lookupScript(TriggerType trigger, String name) {
        var info = scripts.get(String.format(SCRIPT_NAME_TEMPLATE, trigger.getRepresentation(), name));
        if (info == null && getParent() != null) {
            info = getParent().lookupScript(trigger, name);
        }
        return info;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScriptSymbolTable getParent() {
        return (ScriptSymbolTable) super.getParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScriptSymbolTable createSubTable() {
        return new ScriptSymbolTable(this);
    }
}

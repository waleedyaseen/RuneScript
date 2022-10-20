/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.symbol;

import lombok.Getter;
import me.waliedyassen.runescript.compiler.codegen.opcode.Opcode;
import me.waliedyassen.runescript.compiler.symbol.impl.CommandInfo;
import me.waliedyassen.runescript.compiler.symbol.impl.script.ScriptInfo;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;
import me.waliedyassen.runescript.type.Type;

import java.util.HashMap;
import java.util.Map;

public final class ScriptSymbolTable extends SymbolTable {

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
     *
     * @param allowRemoving whether or not to allow the removing of symbols.
     */
    public ScriptSymbolTable(boolean allowRemoving) {
        super(allowRemoving);
    }

    /**
     * Constructs a new {@link SymbolTable} type object instance.
     *
     * @param parent        the parent symbol table object.
     * @param allowRemoving whether or not to allow the removing of symbols.
     */
    public ScriptSymbolTable(SymbolTable parent, boolean allowRemoving) {
        super(parent, allowRemoving);
    }

    public void defineCommand(Opcode opcode, String name, Type[] args, Type returns, Type transmits, boolean dot) {
        if (lookupCommand(name) != null) {
            throw new IllegalArgumentException("The command '" + name + "' is already defined.");
        }
        commands.put(name, new CommandInfo(opcode, name, args, returns, transmits, dot));
    }

    /**
     * Looks-up for the {@link CommandInfo command information} with the specified {@code name}.
     *
     * @param name the name of the command.
     * @return the {@link CommandInfo} if it was present otherwise {@code null}.
     */
    public CommandInfo lookupCommand(String name) {
        var info = commands.get(name);
        if (info == null && getParent() != null) {
            info = getParent().lookupCommand(name);
        }
        return info;
    }

    public void defineScript(ScriptInfo scriptInfo) {
        if (lookupScript(scriptInfo.getFullName()) != null) {
            System.out.printf("The script '%s' is already defined.%n", scriptInfo.getFullName());
            return;
        }
        scripts.put(scriptInfo.getFullName(), scriptInfo);
    }


    /**
     * Builds the full script name from the specified trigger and name.
     *
     * @param trigger the trigger of the script.
     * @param name    the name of the script.
     * @return the full name of the script.
     */
    private String makeScriptName(TriggerType trigger, String name) {
        if (name == null) {
            return String.format("[%s]", trigger.getRepresentation());
        } else {
            return String.format("[%s,%s]", trigger.getRepresentation(), name);
        }
    }

    /**
     * Un-defines the script with the specified {@link TriggerType trigger} and {@code name}.
     *
     * @param trigger the trigger type of the script that we want to undefine.
     * @param name    the name of the script of the script that we want to undefine.
     */
    public void undefineScript(TriggerType trigger, String name) {
        if (!allowRemoving) {
            return;
        }
        var fullName = makeScriptName(trigger, name);
        scripts.remove(fullName);
    }

    /**
     * Looks-up for the {@link ScriptInfo script information} with the specified {@code name}.
     *
     * @param name the name of the script to lookup for.
     * @return the {@link ScriptInfo} if it was present otherwise {@code null}.
     */
    public ScriptInfo lookupScript(String name) {
        var info = scripts.get(name);
        if (info == null && getParent() != null) {
            info = getParent().lookupScript(name);
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
        return new ScriptSymbolTable(this, true);
    }
}

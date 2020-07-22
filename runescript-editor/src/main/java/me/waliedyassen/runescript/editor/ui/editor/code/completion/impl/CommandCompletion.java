/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor.code.completion.impl;

import lombok.var;
import me.waliedyassen.runescript.compiler.symbol.impl.CommandInfo;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;

import java.util.ArrayList;

/**
 * Represents a {@link CodeCompletion} implementation for a command.
 *
 * @author Walied K. Yassen
 */
public final class CommandCompletion extends FunctionCompletion implements CodeCompletion {

    /**
     * The command that we are auto completing.
     */
    private final CommandInfo commandInfo;

    /**
     * Constructs a new {@link  CommandCompletion} type object instance.
     *
     * @param provider    the provider of the auto completion.
     * @param commandInfo the command that we are auto completing.
     */
    public CommandCompletion(CompletionProvider provider, CommandInfo commandInfo) {
        super(provider, commandInfo.getName(), commandInfo.getType().getRepresentation());
        this.commandInfo = commandInfo;
        setRelevance(2);
        var parameters = new ArrayList<Parameter>(commandInfo.getArguments().length);
        for (var index = 0; index < commandInfo.getArguments().length; index++) {
            var argument = commandInfo.getArguments()[index];
            var parameter = new Parameter(argument.getRepresentation(), "$arg" + (index + 1));
            parameter.setDescription("");
            parameters.add(parameter);
        }
        setParams(parameters);
        setReturnValueDescription(commandInfo.getType().getRepresentation());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        return commandInfo.equals(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return commandInfo.hashCode();
    }
}

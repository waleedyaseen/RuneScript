package me.waliedyassen.runescript.editor.ui.editor.code.completion.impl;

import me.waliedyassen.runescript.compiler.symbol.impl.CommandInfo;
import org.fife.ui.autocomplete.CompletionProvider;

/**
 * Represents a {@link CodeCompletion} implementation for a command.
 *
 * @author Walied K. Yassen
 */
public final class CommandCompletion extends CodeCompletion {

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
        super(provider, commandInfo.getName(), "", "");
        this.commandInfo = commandInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append(commandInfo.getName());
        builder.append("(");
        for (var index = 0; index < commandInfo.getArguments().length; index++) {
            var argument = commandInfo.getArguments()[index];
            if (index != 0) {
                builder.append(", ");
            }
            builder.append(argument.getRepresentation());
        }
        builder.append(") : ");
        builder.append(commandInfo.getType().getRepresentation());
        return builder.toString();
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

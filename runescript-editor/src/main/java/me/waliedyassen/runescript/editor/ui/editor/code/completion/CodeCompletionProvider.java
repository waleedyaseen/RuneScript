package me.waliedyassen.runescript.editor.ui.editor.code.completion;

import lombok.var;
import me.waliedyassen.runescript.compiler.lexer.table.LexicalTable;
import me.waliedyassen.runescript.compiler.symbol.ScriptSymbolTable;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.ui.editor.code.completion.cache.AutoCompleteCache;
import me.waliedyassen.runescript.editor.ui.editor.code.completion.impl.CodeCompletion;
import me.waliedyassen.runescript.editor.ui.editor.code.completion.impl.CommandCompletion;
import me.waliedyassen.runescript.editor.ui.editor.code.completion.impl.KeywordCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * @author Walied K. Yassen
 */
public final class CodeCompletionProvider extends DefaultCompletionProvider {

    /**
     * The auto completion cache for keywords.
     */
    private final AutoCompleteCache keywordsCache = new AutoCompleteCache(this);

    /**
     * The auto completion cache for commands.
     */
    private final AutoCompleteCache commandsCache = new AutoCompleteCache(this);

    /**
     * Constructs a new {@link CodeCompletion} type object instance.
     */
    public CodeCompletionProvider() {
        setParameterizedCompletionParams('(', ", ", ')');
        setAutoActivationRules(true, null);
        refreshCache();
    }

    /**
     * Refreshes the code completions cache.
     */
    private void refreshCache() {
        var compiler = Api.getApi().getScriptCompiler();
        refreshKeywords(compiler.getLexicalTable());
        refreshCommands(compiler.getSymbolTable());
    }

    /**
     * Refreshes the keywords code completions cache.
     *
     * @param table
     *         the lexical table to grab the keywords from.
     */
    private void refreshKeywords(LexicalTable<?> table) {
        keywordsCache.getCompletions().clear();
        for (var keyword : table.getKeywords().keySet()) {
            keywordsCache.addSorted(new KeywordCompletion(this, keyword));
        }
    }

    /**
     * Refreshes the commands code completions cache.
     *
     * @param table
     *         the symbol table to grab the commands from.
     */
    private void refreshCommands(ScriptSymbolTable table) {
        commandsCache.getCompletions().clear();
        for (var commandInfo : table.getCommands().values()) {
            commandsCache.addSorted(new CommandCompletion(this, commandInfo));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Completion> getCompletionsImpl(JTextComponent comp) {
        var previousCursor = comp.getCursor();
        var text = getAlreadyEnteredText(comp);
        try {
            comp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            var completions = new TreeSet<>(keywordsCache.getCompletions());
            completions.addAll(commandsCache.getCompletions());
            return makePresentCompletions(completions, text);
        } finally {
            comp.setCursor(previousCursor);
        }
    }

    /**
     * Selects all of the {@link CodeCompletion} that we need to present from the specified {@link Set} based
     * on the specified {@code text}.
     *
     * @param all
     *         a set of all the code completion objects.
     * @param text
     *         the text that we have already entered.
     *
     * @return a {@link List} of all the completions that should be presented.
     */
    private List<Completion> makePresentCompletions(Set<Completion> all, String text) {
        completions = new ArrayList<>(all);
        Collections.sort(completions);
        int start = Collections.binarySearch(completions, text, comparator);
        if (start < 0) {
            start = -(start + 1);
        } else {
            while (start > 0 && comparator.compare(completions.get(start - 1), text) == 0) {
                start--;
            }
        }
        int end = Collections.binarySearch(completions, text + '{', comparator);
        end = -(end + 1);
        return completions.subList(start, end);
    }
}

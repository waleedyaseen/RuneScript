package me.waliedyassen.runescript.editor.ui.editor.code.completion.impl;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

/**
 * Represents a {@link CodeCompletion} implementation for a keyword.
 *
 * @author Walied K. Yassen
 */
public final class KeywordCompletion extends BasicCompletion implements CodeCompletion {

    /**
     * The keyword that we are auto completing.
     */
    private final String keyword;

    /**
     * Constructs a new {@link  KeywordCompletion} type object instance.
     *
     * @param provider the provider of the auto completion.
     * @param keyword  the keyword that we are auto completing.
     */
    public KeywordCompletion(CompletionProvider provider, String keyword) {
        super(provider, keyword, "Insert " + keyword + " keyword.", "Inserts a '" + keyword + "' at keyword at the current position.");
        this.keyword = keyword;
        setRelevance(5);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        return keyword.equals(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return keyword.hashCode();
    }
}

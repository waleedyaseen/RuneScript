package me.waliedyassen.runescript.editor.ui.editor.code.completion.impl;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

/**
 * The base class for all of our completion implementation.
 *
 * @author Walied K. Yassen
 */
public abstract class CodeCompletion extends BasicCompletion {

    /**
     * {@inheritDoc}
     */
    public CodeCompletion(CompletionProvider provider, String replacementText) {
        super(provider, replacementText);
    }

    /**
     * {@inheritDoc}
     */
    public CodeCompletion(CompletionProvider provider, String replacementText, String shortDesc) {
        super(provider, replacementText, shortDesc);
    }

    /**
     * {@inheritDoc}
     */
    public CodeCompletion(CompletionProvider provider, String replacementText, String shortDesc, String summary) {
        super(provider, replacementText, shortDesc, summary);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean equals(Object o);

    /**
     * {@inheritDoc}
     */
    public abstract int hashCode();
}

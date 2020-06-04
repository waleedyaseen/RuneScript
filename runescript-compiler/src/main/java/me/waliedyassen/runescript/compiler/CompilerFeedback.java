package me.waliedyassen.runescript.compiler;

import me.waliedyassen.runescript.compiler.ast.AstScript;

/**
 * An interface which acts like a bridge between the compiler and the client that is using the compiler, it provides
 * data back to the client such as the script that was just parsed.
 *
 * @author Walied K. Yassen
 */
public interface CompilerFeedback {

    // TODO: Turn this into compiler messaging system so we can communicate back and forth between the compiler
    // and the tools that are using the compiler.

    /**
     * Gets called when the parser have finished their work.
     *
     * @param key    the key that was passed to the compile call.
     * @param script the script that we just parsed.
     */
    void onParserDone(Object key, AstScript script);
}

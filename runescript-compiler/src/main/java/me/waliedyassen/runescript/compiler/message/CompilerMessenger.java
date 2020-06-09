package me.waliedyassen.runescript.compiler.message;

/**
 * An interface which acts like a bridge between the compiler and the client that is using the compiler, it provides
 * data back to the client such as the script that was just parsed.
 *
 * @author Walied K. Yassen
 */
public interface CompilerMessenger {

    /**
     * Handles the specified {@link CompilerMessage message}.
     *
     * @param message the message that we want to handle.
     */
    void handleCompilerMessage(CompilerMessage message);
}

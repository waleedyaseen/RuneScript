package me.waliedyassen.runescript.compiler;

import java.io.IOException;

/**
 * The base class for all the compiler implementations we use for RuneScript.
 *
 * @param <I> the input type of the compile calls.
 * @param <R> the result type of the compile calls.
 * @author Walied K. Yassen
 */
public abstract class CompilerBase<I, R> {

    /**
     * Attempts to compile all of the source code specified in the {@link I input} object
     * and produce a {@link R result} object which contains the compiled form of the object
     * and the associated errors produced during that compilation process.
     *
     * @param input the input object which contains the all of the source code that we want to compile.
     * @return the {@link R result} object instance.
     * @throws IOException if somehow a problem occurred while writing or reading from the temporary streams.
     */
    public abstract R compile(I input) throws IOException;
}

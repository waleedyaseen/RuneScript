package me.waliedyassen.runescript.compiler.idmapping;

import me.waliedyassen.runescript.type.Type;

/**
 * An interface which is responsible for providing ids for configs or scripts that are being code generated.
 *
 * @author Walied K. Yassen
 */
public interface IdProvider {

    /**
     * Attempts to find the id for the script with the specified {@code name}.
     *
     * @param name
     *         the name of the script that we want to find the id for.
     *
     * @return the id of the script that we found.
     *
     * @throws IllegalArgumentException
     *         if we failed to find an id for the specified {@code name}.
     */
    int findScript(String name) throws IllegalArgumentException;

    /**
     * Attempts to find the id for the config with the specified {@code name} and {@link Type type}.
     *
     * @param type
     *         the type of the config that we want to find the id for.
     * @param name
     *         the name of the config that we want to find the id for.
     *
     * @return the id of the config that we found.
     *
     * @throws IllegalArgumentException
     *         if we failed to find an id for the config specified {@code name}.
     */
    int findConfig(Type type, String name) throws IllegalArgumentException;
}

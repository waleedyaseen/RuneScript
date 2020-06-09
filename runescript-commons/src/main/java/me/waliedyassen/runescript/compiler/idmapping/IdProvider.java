package me.waliedyassen.runescript.compiler.idmapping;

/**
 * An interface which is responsible for providing ids for entities are compiling
 * such as scripts.
 *
 * @author Walied K. Yassen
 */
public interface IdProvider {

    /**
     * Attempts to find the id for the entity with the specified {@code name}.
     *
     * @param name the name of the entity that we want to find the id for.
     * @return the id of the entity that we found.
     * @throws IllegalArgumentException if we failed to find an id for the specified {@code name}.
     */
    int find(String name) throws IllegalArgumentException;
}

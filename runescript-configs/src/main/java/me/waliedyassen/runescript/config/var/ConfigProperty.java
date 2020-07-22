package me.waliedyassen.runescript.config.var;

import me.waliedyassen.runescript.type.PrimitiveType;

/**
 * The base class for all of the properties.
 *
 * @author Walied K. Yassen
 */
public interface ConfigProperty {

    /**
     * Returns the name of the property.
     *
     * @return the name of the property.
     */
    String getName();

    /**
     * Returns the components that make up the property.
     *
     * @return the components that make up the property.
     */
    PrimitiveType[] getComponents();

    /**
     * Whether or not the property is required to be present in all of the configurations.
     *
     * @return <code>true</code> if it is required otherwise <code>false</code>.
     */
    default boolean isRequired() {
        return false;
    }
}

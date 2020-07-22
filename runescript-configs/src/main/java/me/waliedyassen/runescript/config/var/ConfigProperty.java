package me.waliedyassen.runescript.config.var;

import me.waliedyassen.runescript.config.var.rule.ConfigRule;
import me.waliedyassen.runescript.type.PrimitiveType;

import java.util.Collections;
import java.util.List;

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

    /**
     * Checks whether or not this configuration property allow duplicates of the same property.
     *
     * @return <code>true</code> if it allows otherwise <code>false</code>.
     */
    default boolean isAllowDuplicates() {
        return false;
    }

    /**
     * Returns a list of all the rules that apply to this property.
     *
     * @return a list of all the  rules that apply to this property.
     */
    default List<ConfigRule> getRules() {
        return Collections.emptyList();
    }
}

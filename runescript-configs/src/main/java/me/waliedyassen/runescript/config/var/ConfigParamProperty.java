package me.waliedyassen.runescript.config.var;

import lombok.Data;
import me.waliedyassen.runescript.type.PrimitiveType;

/**
 * A configuration param property.
 *
 * @author Walied K. Yassen
 */
@Data
public final class ConfigParamProperty implements ConfigProperty {

    /**
     * The name of the param property.
     */
    private final String name;

    /**
     * The code of the param property.
     */
    private final int code;

    /**
     * {@inheritDoc}
     */
    @Override
    public PrimitiveType[] getComponents() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAllowDuplicates() {
        return true;
    }
}

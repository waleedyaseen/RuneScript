package me.waliedyassen.runescript.compiler.symbol.impl;

import lombok.Data;
import me.waliedyassen.runescript.compiler.symbol.Symbol;

/**
 * A graphic symbol information in the symbol table.
 *
 * @author Walied K. Yassen
 */
@Data
public final class GraphicInfo extends Symbol {

    /**
     * The name of the graphic.
     */
    private final String name;

    /**
     * The id of the graphic.
     */
    private final int id;
}

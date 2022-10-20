package me.waliedyassen.runescript.type.primitive

import me.waliedyassen.runescript.compiler.symbol.BasicSymbol
import me.waliedyassen.runescript.compiler.symbol.BasicSymbolLoader
import me.waliedyassen.runescript.compiler.symbol.ConfigSymbol
import me.waliedyassen.runescript.compiler.symbol.ConfigSymbolLoader
import me.waliedyassen.runescript.compiler.symbol.ConstantSymbol
import me.waliedyassen.runescript.compiler.symbol.ConstantSymbolLoader
import me.waliedyassen.runescript.compiler.symbol.Symbol
import me.waliedyassen.runescript.compiler.symbol.SymbolLoader
import me.waliedyassen.runescript.compiler.symbol.TypedSymbol
import me.waliedyassen.runescript.compiler.symbol.TypedSymbolLoader
import me.waliedyassen.runescript.type.Type
import me.waliedyassen.runescript.type.stack.StackType

open class PrimitiveType<T : Symbol>(
    override val code: Char,
    override val representation: String?,
    override val stackType: StackType?,
    override val defaultValue: Any?,
    val loader: SymbolLoader<T>?,
) : Type {


    object UNDEFINED : PrimitiveType<BasicSymbol>('\ufff0', "undefined", null, null, null)
    object HOOK : PrimitiveType<BasicSymbol>('\ufff1', "hook", null, null, null)
    object VOID : PrimitiveType<BasicSymbol>('\ufff2', "void", null, null, null)
    object CONSTANT : PrimitiveType<ConstantSymbol>('\ufff3', "constant", null, null, ConstantSymbolLoader)
    object TYPE : PrimitiveType<BasicSymbol>('\ufff6', "type", null, null, null)
    object PARAM : PrimitiveType<ConfigSymbol>('\uffd0', "param", null, null, ConfigSymbolLoader)
    object FLO : PrimitiveType<BasicSymbol>('\uffd1', "flo", null, null, null)
    object FLU : PrimitiveType<BasicSymbol>('\uffd2', "flu", null, null, null)
    object VARP : PrimitiveType<TypedSymbol>('\uffd3', "varp", null, null, TypedSymbolLoader)
    object VARBIT : PrimitiveType<BasicSymbol>('\uffd4', "varbit", null, null, BasicSymbolLoader)
    object VARC : PrimitiveType<TypedSymbol>('\uffd6', "varc", null, null, TypedSymbolLoader)
    object NULL : PrimitiveType<BasicSymbol>('\uffd7', null, null, null, null)
    object INT : PrimitiveType<BasicSymbol>('i', "int", StackType.INT, 0, null)
    object STRING : PrimitiveType<BasicSymbol>('s', "string", StackType.STRING, "", null)
    object SPOTANIM : PrimitiveType<BasicSymbol>('t', "spotanim", StackType.INT, -1, BasicSymbolLoader)
    object SEQ : PrimitiveType<BasicSymbol>('A', "seq", StackType.INT, -1, BasicSymbolLoader)
    object STAT : PrimitiveType<BasicSymbol>('S', "stat", StackType.INT, -1, BasicSymbolLoader)
    object SYNTH : PrimitiveType<BasicSymbol>('P', "synth", StackType.INT, -1, BasicSymbolLoader)
    object COORDGRID : PrimitiveType<BasicSymbol>('c', "coord", StackType.INT, -1, BasicSymbolLoader)
    object CHAR : PrimitiveType<BasicSymbol>('z', "char", StackType.INT, -1, BasicSymbolLoader)
    object FONTMETRICS : PrimitiveType<BasicSymbol>('f', "fontmetrics", StackType.INT, -1, BasicSymbolLoader)
    object MAPAREA : PrimitiveType<BasicSymbol>('`', "wma", StackType.INT, -1, BasicSymbolLoader)
    object ENUM : PrimitiveType<TypedSymbol>('g', "enum", StackType.INT, -1, TypedSymbolLoader)
    object NPC : PrimitiveType<BasicSymbol>('n', "npc", StackType.INT, -1, BasicSymbolLoader)
    object MODEL : PrimitiveType<BasicSymbol>('m', "model", StackType.INT, -1, BasicSymbolLoader)
    object TOPLEVELINTERFACE : PrimitiveType<BasicSymbol>('F', "toplevelinterface", StackType.INT, -1, BasicSymbolLoader)
    object OVERLAYINTERFACE : PrimitiveType<BasicSymbol>('L', "overlayinterface", StackType.INT, -1, BasicSymbolLoader)
    object CLIENTINTERFACE : PrimitiveType<BasicSymbol>('\u00a9', "clientinterface", StackType.INT, -1, BasicSymbolLoader)
    object INTERFACE : PrimitiveType<BasicSymbol>('a', "interface", StackType.INT, -1, BasicSymbolLoader)
    object COMPONENT : PrimitiveType<BasicSymbol>('I', "component", StackType.INT, -1, BasicSymbolLoader)
    object LONG : PrimitiveType<BasicSymbol>('\u00cf', "long", StackType.LONG, 0L, null)
    object BOOLEAN : PrimitiveType<BasicSymbol>('1', "boolean", StackType.INT, false, null)
    object CATEGORY : PrimitiveType<BasicSymbol>('y', "category", StackType.INT, -1, BasicSymbolLoader)
    object NAMEDOBJ : PrimitiveType<BasicSymbol>('O', "namedobj", StackType.INT, -1, BasicSymbolLoader)
    object OBJ : PrimitiveType<BasicSymbol>('o', "obj", StackType.INT, -1, BasicSymbolLoader)
    object INV : PrimitiveType<BasicSymbol>('v', "inv", StackType.INT, -1, BasicSymbolLoader)
    object TEXTURE : PrimitiveType<BasicSymbol>('x', "texture", StackType.INT, -1, BasicSymbolLoader)
    object MAPELEMENT : PrimitiveType<BasicSymbol>('\u00B5', "mapelement", StackType.INT, -1, BasicSymbolLoader)
    object GRAPHIC : PrimitiveType<BasicSymbol>('d', "graphic", StackType.INT, -1, BasicSymbolLoader)
    object STRUCT : PrimitiveType<BasicSymbol>('J', "struct", StackType.INT, -1, BasicSymbolLoader)
    object LOC : PrimitiveType<BasicSymbol>('l', "loc", StackType.INT, -1, BasicSymbolLoader)
    object COLOUR : PrimitiveType<BasicSymbol>('C', "colour", StackType.INT, -1, BasicSymbolLoader)
    object IDK : PrimitiveType<BasicSymbol>('K', "idkit", StackType.INT, -1, BasicSymbolLoader)
    object CHATPHRASE : PrimitiveType<BasicSymbol>('e', "chatphrase", StackType.INT, -1, BasicSymbolLoader)
    object BAS : PrimitiveType<BasicSymbol>('\u20ac', "bas", StackType.INT, -1, BasicSymbolLoader)
    object DBROW : PrimitiveType<BasicSymbol>('\u00D0', "dbrow", StackType.INT, -1, BasicSymbolLoader)
    object NEWVAR : PrimitiveType<BasicSymbol>('-', "newvar", StackType.INT, -1, BasicSymbolLoader)
    object NPC_UID : PrimitiveType<BasicSymbol>('u', "npc_uid", StackType.INT, -1, BasicSymbolLoader)
    object LOC_SHAPE : PrimitiveType<BasicSymbol>('H', "locshape", StackType.INT, -1, BasicSymbolLoader)

    override fun toString() = javaClass.simpleName

    val isReferencable: Boolean
        get() = when (this) {
            is TYPE -> false
            else -> representation != null
        }

    val isDeclarable: Boolean
        get() = stackType != null

    val isArrayable: Boolean
        get() = if (this == BOOLEAN) {
            false
        } else stackType == StackType.INT

    val isConfigType: Boolean
        get() = when (this) {
            is SEQ,
            is STAT,
            is MAPAREA,
            is ENUM,
            is NPC,
            is CATEGORY,
            is NAMEDOBJ,
            is OBJ,
            is INV,
            is MAPELEMENT,
            is VARP,
            is VARBIT,
            is VARC,
            is STRUCT,
            is LOC,
            is PARAM,
            is FLO,
            is FLU,
            is SPOTANIM -> true

            else -> false
        }

    val isNullable: Boolean
        get() = if (stackType != StackType.INT) {
            false
        } else when (this) {
            is NULL,
            is PARAM -> false

            else -> true
        }

    companion object {
        val values = listOf(
            UNDEFINED,
            HOOK,
            VOID,
            CONSTANT,
            TYPE,
            PARAM,
            FLO,
            FLU,
            VARP,
            VARBIT,
            VARC,
            NULL,
            INT,
            STRING,
            SPOTANIM,
            SEQ,
            STAT,
            SYNTH,
            COORDGRID,
            CHAR,
            FONTMETRICS,
            MAPAREA,
            ENUM,
            NPC,
            MODEL,
            TOPLEVELINTERFACE,
            OVERLAYINTERFACE,
            CLIENTINTERFACE,
            INTERFACE,
            COMPONENT,
            LONG,
            BOOLEAN,
            CATEGORY,
            NAMEDOBJ,
            OBJ,
            INV,
            TEXTURE,
            MAPELEMENT,
            GRAPHIC,
            STRUCT,
            LOC,
            COLOUR,
            IDK,
            CHATPHRASE,
            BAS,
            DBROW,
            NEWVAR,
            NPC_UID,
            LOC_SHAPE,
        )
        private val referencibleLookup = values
            .filter { it.isReferencable }
            .associateBy { it.representation!! }

        private val literalLookup = values
            .filter { it.representation != null }
            .associateBy { it.representation!! }

        @JvmStatic
        fun forRepresentation(representation: String): PrimitiveType<*>? {
            return referencibleLookup[representation]
        }
        @JvmStatic
        fun forLiteralOrNull(literal: String): PrimitiveType<*>? {
            return literalLookup[literal]
        }
        @JvmStatic
        fun forLiteral(literal: String): PrimitiveType<*> {
            return literalLookup[literal] ?: error("No type could be found for the literal '$literal'")
        }

    }
}

package me.waliedyassen.runescript.type.primitive

import me.waliedyassen.runescript.type.Type
import me.waliedyassen.runescript.type.stack.StackType

open class PrimitiveType(
    override val code: Char,
    override val representation: String?,
    override val stackType: StackType?,
    override val defaultValue: Any?
) : Type {


    object UNDEFINED : PrimitiveType('\ufff0', "undefined", null, null)
    object HOOK : PrimitiveType('\ufff1', "hook", null, null)
    object VOID : PrimitiveType('\ufff2', "void", null, null)
    object TYPE : PrimitiveType('\ufff6', "type", null, null)
    object PARAM : PrimitiveType('\uffd0', "param", null, null)
    object FLO : PrimitiveType('\uffd1', "flo", null, null)
    object FLU : PrimitiveType('\uffd2', "flu", null, null)
    object VARP : PrimitiveType('\uffd3', "varp", null, null)
    object VARBIT : PrimitiveType('\uffd4', "varbit", null, null)
    object VARC : PrimitiveType('\uffd6', "varc", null, null)
    object NULL : PrimitiveType('\uffd7', null, null, null)
    object INT : PrimitiveType('i', "int", StackType.INT, 0)
    object STRING : PrimitiveType('s', "string", StackType.STRING, "")
    object SPOTANIM : PrimitiveType('t', "spotanim", StackType.INT, -1)
    object SEQ : PrimitiveType('A', "seq", StackType.INT, -1)
    object STAT : PrimitiveType('S', "stat", StackType.INT, -1)
    object SYNTH : PrimitiveType('P', "synth", StackType.INT, -1)
    object COORDGRID : PrimitiveType('c', "coord", StackType.INT, -1)
    object CHAR : PrimitiveType('z', "char", StackType.INT, -1)
    object FONTMETRICS : PrimitiveType('f', "fontmetrics", StackType.INT, -1)
    object MAPAREA : PrimitiveType('`', "wma", StackType.INT, -1)
    object ENUM : PrimitiveType('g', "enum", StackType.INT, -1)
    object NPC : PrimitiveType('n', "npc", StackType.INT, -1)
    object MODEL : PrimitiveType('m', "model", StackType.INT, -1)
    object TOPLEVELINTERFACE : PrimitiveType('F', "toplevelinterface", StackType.INT, -1)
    object OVERLAYINTERFACE : PrimitiveType('L', "overlayinterface", StackType.INT, -1)
    object CLIENTINTERFACE : PrimitiveType('\u00a9', "clientinterface", StackType.INT, -1)
    object INTERFACE : PrimitiveType('a', "interface", StackType.INT, -1)
    object COMPONENT : PrimitiveType('I', "component", StackType.INT, -1)
    object LONG : PrimitiveType('\u00cf', "long", StackType.LONG, 0L)
    object BOOLEAN : PrimitiveType('1', "boolean", StackType.INT, false)
    object CATEGORY : PrimitiveType('y', "category", StackType.INT, -1)
    object NAMEDOBJ : PrimitiveType('O', "namedobj", StackType.INT, -1)
    object OBJ : PrimitiveType('o', "obj", StackType.INT, -1)
    object INV : PrimitiveType('v', "inv", StackType.INT, -1)
    object TEXTURE : PrimitiveType('x', "texture", StackType.INT, -1)
    object MAPELEMENT : PrimitiveType('\u00B5', "mapelement", StackType.INT, -1)
    object GRAPHIC : PrimitiveType('d', "graphic", StackType.INT, -1)
    object STRUCT : PrimitiveType('J', "struct", StackType.INT, -1)
    object LOC : PrimitiveType('l', "loc", StackType.INT, -1)
    object COLOUR : PrimitiveType('C', "colour", StackType.INT, -1)
    object IDK : PrimitiveType('K', "idkit", StackType.INT, -1)
    object CHATPHRASE : PrimitiveType('e', "chatphrase", StackType.INT, -1)
    object BAS : PrimitiveType('\u20ac', "bas", StackType.INT, -1)
    object DBROW : PrimitiveType('\u00D0', "dbrow", StackType.INT, -1)
    object NEWVAR : PrimitiveType('-', "newvar", StackType.INT, -1)
    object NPC_UID : PrimitiveType('u', "npc_uid", StackType.INT, -1)
    object LOC_SHAPE : PrimitiveType('H', "locshape", StackType.INT, -1)

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
        fun forRepresentation(representation: String): PrimitiveType? {
            return referencibleLookup[representation]
        }
        @JvmStatic
        fun forLiteralOrNull(literal: String): PrimitiveType? {
            return literalLookup[literal]
        }
        @JvmStatic
        fun forLiteral(literal: String): PrimitiveType {
            return literalLookup[literal] ?: error("No type could be found for the literal '$literal'")
        }

    }
}

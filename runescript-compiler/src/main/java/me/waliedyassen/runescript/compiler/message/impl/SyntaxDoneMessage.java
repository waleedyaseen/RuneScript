package me.waliedyassen.runescript.compiler.message.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.message.CompilerMessage;

/**
 * A message which is fired when a compiler have just finished parsing
 * the syntax of the input.
 *
 * @author Walied K. Yassen
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class SyntaxDoneMessage extends CompilerMessage {

    /**
     * The identifier of the script that we parsed.
     */
    private final Object identifier;

    /**
     * The AST of the script that we parsed.
     */
    private final AstScript script;
}

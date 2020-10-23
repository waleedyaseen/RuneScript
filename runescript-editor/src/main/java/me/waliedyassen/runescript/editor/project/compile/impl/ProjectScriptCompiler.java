/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project.compile.impl;

import lombok.Getter;
import lombok.var;
import me.waliedyassen.runescript.compiler.*;
import me.waliedyassen.runescript.compiler.symbol.ScriptSymbolTable;
import me.waliedyassen.runescript.compiler.symbol.impl.script.ScriptInfo;
import me.waliedyassen.runescript.compiler.syntax.ParameterSyntax;
import me.waliedyassen.runescript.compiler.syntax.ScriptSyntax;
import me.waliedyassen.runescript.editor.project.cache.unit.CacheUnit;
import me.waliedyassen.runescript.editor.project.compile.ProjectCompiler;
import me.waliedyassen.runescript.type.Type;
import me.waliedyassen.runescript.type.TypeUtil;
import me.waliedyassen.runescript.type.primitive.PrimitiveType;
import me.waliedyassen.runescript.type.tuple.TupleType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A {@link ProjectCompiler} implementation that is responsible for compiling either a client script
 * or a server script.
 *
 * @author Walied K. Yassen
 */
public final class ProjectScriptCompiler implements ProjectCompiler<ScriptSyntax, CompiledScriptUnit> {

    /**
     * The underlying compiler that we are going to use for this.
     */
    @Getter
    private final ScriptCompiler compiler;

    /**
     * Constructs a new {@link ProjectScriptCompiler} type object instance.
     *
     * @param compiler the underlying scripts compiler to use.
     */
    public ProjectScriptCompiler(ScriptCompiler compiler) {
        this.compiler = compiler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Output<ScriptSyntax, CompiledScriptUnit> compile(Input input) throws IOException {
        return compiler.compile(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CacheScriptUnit createUnit(String filePath, String fileName) {
        return new CacheScriptUnit(fileName, fileName);
    }

    /**
     * Represents a cached script unit.
     *
     * @author Walied K. Yassen
     */
    private final class CacheScriptUnit extends CacheUnit<CompiledScriptUnit> {

        /**
         * A list of all the {@link ScriptInfo} within this cache unit.
         */
        @Getter
        private final List<ScriptInfo> scripts = new ArrayList<>();

        /**
         * Constructs a new {@link CacheScriptUnit} type object instance.
         *
         * @param filePath the file path of the cache unit.
         * @param fileName the file name of the cache unit.
         */
        public CacheScriptUnit(String filePath, String fileName) {
            super(filePath, fileName);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void defineSymbols(ScriptSymbolTable symbolTable) {
            for (var script : scripts) {
                if (symbolTable.lookupScript(script.getTrigger(), script.getName()) == null) {
                    symbolTable.defineScript(script);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void undefineSymbols(ScriptSymbolTable symbolTable) {
            for (var script : scripts) {
                symbolTable.undefineScript(script.getTrigger(), script.getName());
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void update(CompiledFile<?, CompiledScriptUnit> compiledFile) {
            for (var compiledUnit : compiledFile.getUnits()) {
                var scriptNode = compiledUnit.getSyntax();
                var scriptName = compiledUnit.getSyntax().getName().getText();
                var triggerName = compiledUnit.getSyntax().getTrigger().getText();
                var info = new ScriptInfo(Collections.emptyMap(), scriptName,
                        compiler.getEnvironment().lookupTrigger(triggerName),
                        scriptNode.getType(),
                        Arrays.stream(scriptNode.getParameters()).map(ParameterSyntax::getType).toArray(Type[]::new),
                        null);
                scripts.add(info);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void writeImpl(DataOutputStream stream) throws IOException {
            stream.writeShort(scripts.size());
            for (var script : scripts) {
                stream.writeUTF(script.getName());
                stream.writeUTF(script.getTrigger().getRepresentation());
                stream.writeByte(script.getArguments().length);
                for (var argument : script.getArguments()) {
                    stream.writeUTF(argument.getRepresentation());
                }
                var returnTypes = TypeUtil.flatten(new Type[]{script.getType()});
                stream.writeByte(returnTypes.length);
                for (var returnType : returnTypes) {
                    stream.writeUTF(returnType.getRepresentation());
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void readImpl(DataInputStream stream) throws IOException {
            var environment = compiler.getEnvironment();
            var scriptsCount = stream.readUnsignedShort();
            for (var index = 0; index < scriptsCount; index++) {
                var name = stream.readUTF();
                var trigger = environment.lookupTrigger(stream.readUTF());
                var argumentsCount = stream.readUnsignedByte();
                var arguments = new Type[argumentsCount];
                for (var argumentIndex = 0; argumentIndex < argumentsCount; argumentIndex++) {
                    arguments[argumentIndex] = PrimitiveType.forRepresentation(stream.readUTF());
                }
                var returnsCount = stream.readUnsignedByte();
                var returns = new Type[returnsCount];
                for (var returnIndex = 0; returnIndex < returnsCount; returnIndex++) {
                    returns[returnIndex] = PrimitiveType.forRepresentation(stream.readUTF());
                }
                scripts.add(new ScriptInfo(Collections.emptyMap(), name, trigger, returnsCount == 0 ? PrimitiveType.VOID : returnsCount > 1 ? new TupleType(returns) : returns[0], arguments, null));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void clearImpl() {
            scripts.clear();
        }
    }
}

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
import me.waliedyassen.runescript.compiler.CompiledFile;
import me.waliedyassen.runescript.compiler.Input;
import me.waliedyassen.runescript.compiler.Output;
import me.waliedyassen.runescript.compiler.symbol.ScriptSymbolTable;
import me.waliedyassen.runescript.compiler.symbol.impl.ConfigInfo;
import me.waliedyassen.runescript.config.compiler.CompiledConfigUnit;
import me.waliedyassen.runescript.config.compiler.ConfigCompiler;
import me.waliedyassen.runescript.config.syntax.ConfigSyntax;
import me.waliedyassen.runescript.editor.project.cache.unit.CacheUnit;
import me.waliedyassen.runescript.editor.project.compile.ProjectCompiler;
import me.waliedyassen.runescript.type.primitive.PrimitiveType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ProjectCompiler} implementation that is responsible for compiling any type of configuration.
 *
 * @author Walied K. Yassen
 */
public final class ProjectConfigCompiler implements ProjectCompiler<ConfigSyntax, CompiledConfigUnit> {

    /**
     * The underlying compiler that we are going to use for this.
     */
    private final ConfigCompiler compiler;

    /**
     * Constructs a new {@link ProjectConfigCompiler} type object instance.
     *
     * @param compiler the underlying scripts compiler to use.
     */
    public ProjectConfigCompiler(ConfigCompiler compiler) {
        this.compiler = compiler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Output<ConfigSyntax, CompiledConfigUnit> compile(Input input) throws IOException {
        return compiler.compile(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CacheConfigUnit createUnit(String filePath, String fileName) {
        return new CacheConfigUnit(filePath, fileName);
    }

    /**
     * Represents a cached configuration unit.
     *
     * @author Walied K. Yassen
     */
    private static final class CacheConfigUnit extends CacheUnit<CompiledConfigUnit> {

        /**
         * A list of all the {@link ConfigInfo} within this cache unit.
         */
        @Getter
        private final List<ConfigInfo> configs = new ArrayList<>();

        /**
         * Constructs a new {@link CacheConfigUnit} type object instance.
         *
         * @param filePath the file path of the cache unit.
         * @param fileName the file name of the cache unit.
         */
        public CacheConfigUnit(String filePath, String fileName) {
            super(filePath, fileName);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void defineSymbols(ScriptSymbolTable symbolTable) {
            for (var config : configs) {
                if (symbolTable.lookupConfig(config.getName()) == null) {
                    symbolTable.defineConfig(config);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void undefineSymbols(ScriptSymbolTable symbolTable) {
            for (var script : configs) {
                symbolTable.undefineConfig(script.getName());
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void update(CompiledFile<?, CompiledConfigUnit> compiledFile) {
            for (var compiledUnit : compiledFile.getUnits()) {
                var info = new ConfigInfo(compiledUnit.getSyntax().getName().getText(), compiledUnit.getBinding().getGroup().getType(), compiledUnit.getSyntax().getContentType());
                configs.add(info);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void writeImpl(DataOutputStream stream) throws IOException {
            stream.writeShort(configs.size());
            for (var config : configs) {
                stream.writeUTF(config.getName());
                stream.writeUTF(config.getType().getRepresentation());
                stream.writeUTF(config.getContentType() == null ? "" : config.getContentType().getRepresentation());
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void readImpl(DataInputStream stream) throws IOException {
            var configsCount = stream.readUnsignedShort();
            for (var index = 0; index < configsCount; index++) {
                var name = stream.readUTF();
                var type = PrimitiveType.forRepresentation(stream.readUTF());
                var contentTypeRaw = stream.readUTF();
                var contentType = contentTypeRaw.isEmpty() ? null : PrimitiveType.forRepresentation(contentTypeRaw);
                configs.add(new ConfigInfo(name, type, contentType));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void clearImpl() {
            configs.clear();
        }
    }
}

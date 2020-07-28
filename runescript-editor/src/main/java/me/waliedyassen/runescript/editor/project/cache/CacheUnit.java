/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project.cache;

import lombok.Getter;
import lombok.Setter;
import lombok.var;
import me.waliedyassen.runescript.commons.document.LineColumn;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
import me.waliedyassen.runescript.compiler.symbol.ScriptSymbolTable;
import me.waliedyassen.runescript.compiler.symbol.impl.ConfigInfo;
import me.waliedyassen.runescript.compiler.symbol.impl.script.ScriptInfo;
import me.waliedyassen.runescript.type.PrimitiveType;
import me.waliedyassen.runescript.type.TupleType;
import me.waliedyassen.runescript.type.Type;
import me.waliedyassen.runescript.type.TypeUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A single unit that can be stored in the
 *
 * @author Walied K. Yassen
 */
public final class CacheUnit {

    /**
     * The path that leads to this cache unit (relative to the project directory).
     */
    @Getter
    @Setter
    private String path;

    /**
     * The name of the file (with the extension) that this cache unit is for.
     */
    @Getter
    @Setter
    private String name;

    /**
     * The CRC-32 hash of the file content this cache unit is for.
     */
    @Getter
    @Setter
    private int crc;

    /**
     * The last packed CRC-32 hash of the file content.
     */
    @Getter
    @Setter
    private int packCrc;

    /**
     * A list of all the errors that are in this cache unit.
     */
    @Getter
    private final List<CachedError> errors = new ArrayList<>();

    /**
     * A list of all the scripts that are in this cache unit.
     */
    @Getter
    private final List<ScriptInfo> scripts = new ArrayList<>();

    /**
     * A list of all the configs that are in this cache unit.
     */
    @Getter
    private final List<ConfigInfo> configs = new ArrayList<>();

    /**
     * Constructs a new {@link CacheUnit} type object instance.
     */
    public CacheUnit() {
        super();
    }

    /**
     * Constructs a new {@link CacheUnit} type object instance.
     *
     * @param path
     *         the path which leads to the cache unit.
     * @param name
     *         the name of the cache unit with the extension.
     */
    public CacheUnit(String path, String name) {
        this.path = path;
        this.name = name;
    }

    /**
     * Writes the cache unit data to the specified {@link DataOutputStream stream}.
     *
     * @param stream
     *         the stream that we want to write the cache unit data to.
     *
     * @throws IOException
     *         if anything occurs while writing the cache unit data to the stream.
     */
    public void write(DataOutputStream stream) throws IOException {
        stream.writeUTF(path);
        stream.writeUTF(name);
        stream.writeInt(crc);
        stream.writeInt(packCrc);
        stream.writeShort(errors.size());
        for (var error : errors) {
            stream.writeInt(error.getRange().getStart().getLine());
            stream.writeInt(error.getRange().getStart().getColumn());
            stream.writeInt(error.getRange().getEnd().getLine());
            stream.writeInt(error.getRange().getEnd().getColumn());
            stream.writeUTF(error.getMessage());
        }
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
        stream.writeShort(configs.size());
        for (var config : configs) {
            stream.writeUTF(config.getName());
            stream.writeUTF(config.getType().getRepresentation());
            stream.writeUTF(config.getContentType() == null ? "" : config.getContentType().getRepresentation());
        }
    }

    /**
     * Reads the cache unit data to the specified {@link DataInputStream stream}.
     *
     * @param stream
     *         the stream that we want to read the cache unit data from.
     * @param environment
     *         the compiler environment which we will try to resolve the triggers from.
     *
     * @throws IOException
     *         if anything occurs while reading the cache unit data from the stream.
     */
    public void deserialize(DataInputStream stream, CompilerEnvironment environment) throws IOException {
        path = stream.readUTF();
        name = stream.readUTF();
        crc = stream.readInt();
        packCrc = stream.readInt();
        var errorsCount = stream.readUnsignedShort();
        for (var index = 0; index < errorsCount; index++) {
            var start = new LineColumn(stream.readInt(), stream.readInt());
            var end = new LineColumn(stream.readInt(), stream.readInt());
            errors.add(new CachedError(new Range(start, end), stream.readUTF()));
        }
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
     * Defines all of the symbols of this cache unit in the specified {@link ScriptSymbolTable table}.
     *
     * @param symbolTable
     *         the symbol table to define all of the symbols in.
     */
    public void defineSymbols(ScriptSymbolTable symbolTable) {
        // TODO: Remove hte lookup calls and prevent registering duplicate configs in recompile.
        for (var info : configs) {
            if (symbolTable.lookupConfig(info.getName()) == null) {
                symbolTable.defineConfig(info);
            }
        }
        for (var info : scripts) {
            if (symbolTable.lookupScript(info.getTrigger(), info.getName()) == null) {
                symbolTable.defineScript(info);
            }
        }
    }

    /**
     * Undefines all of the symbols of this cache unit from the specified {@link ScriptSymbolTable table}.
     *
     * @param symbolTable
     *         the symbol table to undefine all of the symbols from.
     */
    public void undefineSymbols(ScriptSymbolTable symbolTable) {
        for (var info : configs) {
            symbolTable.undefineConfig(info.getName());
        }
        for (var info : scripts) {
            symbolTable.undefineScript(info.getTrigger(), info.getName());
        }
    }

    /**
     * Clears the cache unit from any cached data.
     */
    public void clear() {
        configs.clear();
        scripts.clear();
        errors.clear();
    }

    /**
     * Returns the full name with path of the cache unit.
     *
     * @return the full name with path of the cache unit.
     */
    public String getNameWithPath() {
        return path;
    }

    /**
     * Checks whether or not this cache unit is for server scripts.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isServerScript() {
        return name.endsWith(".rs2");
    }

    /**
     * Checks whether or not this cache unit is for client scripts.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isClientScript() {
        return name.endsWith(".cs2");
    }
}

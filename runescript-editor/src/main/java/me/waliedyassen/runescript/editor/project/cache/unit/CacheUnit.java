/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project.cache.unit;

import lombok.Getter;
import lombok.Setter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.CompiledFile;
import me.waliedyassen.runescript.compiler.CompiledUnit;
import me.waliedyassen.runescript.compiler.symbol.ScriptSymbolTable;
import me.waliedyassen.runescript.editor.project.cache.CachedError;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A single unit that can be stored in the
 *
 * @param <U> the type of the compilation unit this cache unit contains.
 * @author Walied K. Yassen
 */
public abstract class CacheUnit<U extends CompiledUnit<?>> {

    /**
     * A list of all the errors that are in this cache unit.
     */
    @Getter
    private final List<CachedError> errors = new ArrayList<>();

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
     * Constructs a new {@link CacheUnit} type object instance.
     *
     * @param path the path which leads to the cache unit.
     * @param name the name of the cache unit with the extension.
     */
    public CacheUnit(String path, String name) {
        this.path = path;
        this.name = name;
    }

    /**
     * Updates the content of this cache unit based from the specified compiled file. Calling this method
     * will not clear the existing content of the cache unit.
     *
     * @param compiledFile the compiled file object to update from.
     */
    public abstract void update(CompiledFile<?, U> compiledFile);

    /**
     * Writes the content of the implementation.
     *
     * @param stream the stream to write the data to.
     * @throws IOException if anything occurs while writing data to the stream.
     */
    public abstract void writeImpl(DataOutputStream stream) throws IOException;

    /**
     * Reads the content of the implementation.
     *
     * @param stream the stream to read the data from.
     * @throws IOException if anything occurs while reading data from the stream.
     */
    public abstract void readImpl(DataInputStream stream) throws IOException;

    /**
     * Clears the content of the implementation.
     */
    public abstract void clearImpl();

    /**
     * Defines all of the symbols of this cache unit in the specified {@link ScriptSymbolTable table}.
     *
     * @param symbolTable the symbol table to define all of the symbols in.
     */
    public abstract void defineSymbols(ScriptSymbolTable symbolTable);

    /**
     * Un-defines all of the symbols of this cache unit from the specified {@link ScriptSymbolTable table}.
     *
     * @param symbolTable the symbol table to undefine all of the symbols from.
     */
    public abstract void undefineSymbols(ScriptSymbolTable symbolTable);

    /**
     * Writes the cache unit data to the specified {@link DataOutputStream stream}.
     *
     * @param stream the stream that we want to write the cache unit data to.
     * @throws IOException if anything occurs while writing the cache unit data to the stream.
     */
    public void write(DataOutputStream stream) throws IOException {
        stream.writeUTF(path);
        stream.writeUTF(name);
        stream.writeInt(crc);
        stream.writeInt(packCrc);
        stream.writeShort(errors.size());
        for (var error : errors) {
            stream.writeInt(error.getRange().getStart());
            stream.writeInt(error.getRange().getWidth());
            stream.writeInt(error.getLine());
            stream.writeUTF(error.getMessage());
        }
        writeImpl(stream);
    }

    /**
     * Reads the cache unit data to the specified {@link DataInputStream stream}.
     *
     * @param stream the stream that we want to read the cache unit data from.
     * @throws IOException if anything occurs while reading the cache unit data from the stream.
     */
    public void read(DataInputStream stream) throws IOException {
        name = stream.readUTF();
        crc = stream.readInt();
        packCrc = stream.readInt();
        var errorsCount = stream.readUnsignedShort();
        for (var index = 0; index < errorsCount; index++) {
            errors.add(new CachedError(new Range(stream.readInt(), stream.readInt()), stream.readInt(), stream.readUTF()));
        }
        readImpl(stream);
    }

    /**
     * Clears the cache unit from any cached data.
     */
    public void clear() {
        errors.clear();
        clearImpl();
    }

    /**
     * Returns the full name with path of the cache unit.
     *
     * @return the full name with path of the cache unit.
     */
    public String getNameWithPath() {
        return path;
    }
}

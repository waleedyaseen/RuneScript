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
import me.waliedyassen.runescript.commons.document.LineColumn;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
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
 * The cached content of a specific file in the project, content can be things such as scripts it contains
 * or errors it currently has.
 *
 * @author Walied K . Yassen
 */
public final class CachedFile {

    /**
     * The path of the cached file containing directory.
     */
    @Getter
    @Setter
    private String path;

    /**
     * The name of the file with the extension.
     */
    @Getter
    @Setter
    private String name;

    /**
     * The CRC-32 of the file content this cache is for.
     */
    @Getter
    @Setter
    private int crc;

    /**
     * A list of all the cached errors.
     */
    @Getter
    private final List<CachedError> errors = new ArrayList<>();

    /**
     * A list of all the cached scripts.
     */
    @Getter
    private final List<ScriptInfo> scripts = new ArrayList<>();

    /**
     * Deserialises the content of the {@link CachedFile} from the specified {@link DataInputStream}.
     *
     * @param stream the stream to deserialise the content from.
     * @throws IOException if anything occurs while reading data from the specified stream.
     */
    protected void read(CompilerEnvironment environment, DataInputStream stream) throws IOException {
        path = stream.readUTF();
        name = stream.readUTF();
        crc = stream.readInt();
        var errorsCount = stream.readUnsignedShort();
        errors.clear();
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
    }

    /**
     * Serialises the content of the {@link CachedFile} into the specified {@link DataOutputStream}.
     *
     * @param stream the stream to serialise the content into.
     * @throws IOException if anything occurs while writing data to the specified stream.
     */
    protected void write(DataOutputStream stream) throws IOException {
        stream.writeUTF(path);
        stream.writeUTF(name);
        stream.writeInt(crc);
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
    }

    /**
     * Returns the full path of the cached file.
     *
     * @return the full path of the cached file.
     */
    public String getFullPath() {
        return path + "/" + name;
    }
}

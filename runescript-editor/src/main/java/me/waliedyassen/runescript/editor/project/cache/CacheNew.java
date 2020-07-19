package me.waliedyassen.runescript.editor.project.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.var;
import me.waliedyassen.runescript.compiler.Input;
import me.waliedyassen.runescript.compiler.SourceFile;
import me.waliedyassen.runescript.compiler.symbol.impl.ConfigInfo;
import me.waliedyassen.runescript.editor.project.Project;
import me.waliedyassen.runescript.editor.util.ex.PathEx;
import me.waliedyassen.runescript.type.PrimitiveType;

import javax.xml.transform.Source;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A cache system that is for a specific project.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class CacheNew {

    /**
     * A map of all the cache units that are stored in this
     */
    @Getter
    private final Map<String, CacheUnit> units = new HashMap<>();

    /**
     * The project which this cache is for.
     */
    private final Project project;

    /**
     * Reads the content of the cache from the specified {@link DataInputStream stream}.
     *
     * @param stream
     *         the stream to read the content of the cache from.
     *
     * @throws IOException
     *         if anything occurs while reading the data from the stream.
     */
    public void deserialize(DataInputStream stream) throws IOException {
        var unitsCount = stream.readInt();
        for (int index = 0; index < unitsCount; index++) {
            var unit = new CacheUnit();
            unit.deserialize(stream, project.getCompilerEnvironment());
            units.put(unit.getNameWithPath(), unit);
        }
    }

    /**
     * Writes the content of the cache to the specified {@link DataOutputStream stream}.
     *
     * @param stream
     *         the stream to write the content of the cache to.
     *
     * @throws IOException
     *         if anything occurs while writing the data to the stream.
     */
    public void serialize(DataOutputStream stream) throws IOException {
        stream.writeInt(units.size());
        for (var file : units.values()) {
            file.write(stream);
        }
    }


    public void performSaving() {

    }

    public void diff() throws IOException {
    }

    /**
     * @param path
     */
    @SneakyThrows
    public void recompile(Path path) {
        recompile(path, Files.readAllBytes(path));
    }

    /**
     * Re-compiles the content of the file at the specified {@link Path relative path}.
     *
     * @param path
     *         the relative path of the file that we want to recompile.
     */
    @SneakyThrows
    public void recompile(Path path, byte[] content) {
        var key = PathEx.normalizeRelative(project.getBuildPath().getSourceDirectory(), path);
        var unit = units.get(key);
        if (unit == null) {
            unit = createCacheUnit(path);
        } else {
            unit.getConfigs().forEach(config -> {
                System.out.print(config.getName() + ",");
            });
            unit.undefineSymbols(project.getSymbolTable());
            System.out.println(project.getSymbolTable().lookupConfig("test_obj"));
            unit.clear();
        }
        var input = new Input();
        input.addSourceFile(SourceFile.of(path, content));

        if (unit.isClientScript() || unit.isServerScript()) {

        } else {
            var type = PrimitiveType.forRepresentation(PathEx.getExtension(path));
            var output = project.getConfigsCompiler().compile(input);
            for (var compiledFile : output.getCompiledFiles()) {
                for (var binaryConfig : compiledFile.getUnits()) {
                    unit.getConfigs().add(new ConfigInfo(binaryConfig.getName(), type));
                    project.getSymbolTable().defineConfig(binaryConfig.getName(), type);
                }
                for (var error : compiledFile.getErrors()) {
                    unit.getErrors().add(new CachedError(error.getRange(), error.getMessage()));
                }
            }
        }
        project.updateErrors(unit);
    }

    /**
     *
     */
    public void pack() {

    }

    /**
     * Creates a new {@link CacheUnit} object for the specified {@link Path relative path}.
     *
     * @param relativePath
     *         the relative path of the cache unit.
     *
     * @return the created {@link CacheUnit} object.
     */
    private CacheUnit createCacheUnit(Path relativePath) {
        var fullPath = PathEx.normalizeRelative(project.getBuildPath().getSourceDirectory(), relativePath);
        var unit = new CacheUnit(fullPath, relativePath.getFileName().toString());
        units.put(fullPath, unit);
        return unit;
    }
}

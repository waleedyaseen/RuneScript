/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import me.waliedyassen.runescript.compiler.ScriptCompiler;
import me.waliedyassen.runescript.compiler.codegen.InstructionMap;
import me.waliedyassen.runescript.compiler.codegen.opcode.BasicOpcode;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
import me.waliedyassen.runescript.compiler.idmapping.IDManager;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.symbol.ScriptSymbolTable;
import me.waliedyassen.runescript.compiler.symbol.impl.script.ScriptInfo;
import me.waliedyassen.runescript.compiler.util.trigger.BasicTriggerType;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.pack.manager.PackManager;
import me.waliedyassen.runescript.editor.project.build.BuildPath;
import me.waliedyassen.runescript.editor.project.cache.Cache;
import me.waliedyassen.runescript.editor.project.cache.unit.CacheUnit;
import me.waliedyassen.runescript.editor.project.compile.ProjectCompiler;
import me.waliedyassen.runescript.editor.project.compile.ProjectCompilerProvider;
import me.waliedyassen.runescript.editor.project.compile.impl.ProjectScriptCompiler;
import me.waliedyassen.runescript.editor.util.JsonUtil;
import me.waliedyassen.runescript.editor.vfs.VFS;
import me.waliedyassen.runescript.type.Type;
import me.waliedyassen.runescript.type.TypeUtil;
import me.waliedyassen.runescript.type.primitive.PrimitiveType;
import me.waliedyassen.runescript.type.tuple.TupleType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A very basic project system that provides basic information such as the name and the build path directories.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class Project {

    /**
     * The project information file name.
     */
    static final String FILE_NAME = ".rsproj";

    /**
     * The ID manager of the project.
     */
    @Getter
    private final ProjectIDManager idManager = new ProjectIDManager(this);

    /**
     * The base directory {@link Path} of the project.
     */
    @Getter
    private final Path directory;

    /**
     * the name of the project.
     */
    @Getter
    @Setter
    private String name;

    /**
     * The build path of the project.
     */
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private BuildPath buildPath;

    /**
     * The pack manager of the project.
     */
    @Getter
    private PackManager packManager;

    /**
     * The virtual file system of the project.
     */
    @Getter
    private VFS vfs;

    /**
     * The provider object of the {@link ProjectCompiler} types for this project.
     */
    @Getter
    private ProjectCompilerProvider compilerProvider;

    /**
     * The instruction map of the project.
     */
    @Getter
    private InstructionMap instructionMap;

    /**
     * The compiler environment of the project.
     */
    @Getter
    private CompilerEnvironment compilerEnvironment;

    /**
     * The predefined symbols table for the compiler.
     */
    @Getter
    private ScriptSymbolTable predefinedTable;

    /**
     * The symbol table we are using for the compiler.
     */
    @Getter
    private ScriptSymbolTable symbolTable;

    /**
     * The cache of the project.
     */
    @Getter
    private Cache cache;

    /**
     * Whether or not the project supports long primitive type compilation.
     */
    @Getter
    @Setter
    private boolean supportsLongPrimitiveType;

    /**
     * Whether or not the project supports overriding already defined symbols.
     */
    @Getter
    @Setter
    private boolean overrideSymbols;

    /**
     * The commands configuration path.
     */
    @Getter
    @Setter
    private String commandsPath;

    /**
     * The triggers configuration path.
     */
    @Getter
    @Setter
    private String triggersPath;

    /**
     * The instructions configuration path.
     */
    @Getter
    @Setter
    private String instructionsPath;

    /**
     * The runtime constants configuration path.
     */
    @Getter
    @Setter
    private String runtimeConstantsPath;

    /**
     * The instructions configuration path.
     */
    @Getter
    @Setter
    private String predefinedScriptsPath;

    /**
     * The constants configuration path.
     */
    @Getter
    @Setter
    private String predefinedConstantsPath;

    /**
     * The pack type of the project.
     */
    @Getter
    @Setter
    private PackType packType;

    /**
     * Constructs a new {@link Project} type object instance.
     *
     * @param directory the root directory path of the project.
     */
    Project(Path directory) {
        this.directory = directory;
    }

    /**
     * Attempts to load the project information data from the local disk.
     *
     * @throws IOException if anything occurs during the loading procedure.
     */
    void loadData() throws IOException {
        // Read the node tree from the file.
        JsonNode root;
        try (var reader = Files.newBufferedReader(findProjectFile())) {
            root = JsonUtil.getMapper().reader().readTree(reader);
        }
        // Read the project general information.
        name = JsonUtil.getTextOrThrow(root, "name", "The project name cannot be null or empty");
        supportsLongPrimitiveType = JsonUtil.getBooleanOrDefault(root, "supportsLongPrimitiveType", false);
        overrideSymbols = JsonUtil.getBooleanOrDefault(root, "overrideSymbols", false);
        loadBuildPath(root);
        loadCompiler(root);
        postLoad();
    }

    /**
     * Attempts to load the {@link BuildPath} object from the specified {@link JsonNode} root object.
     *
     * @param root the root node which contains the build path node.
     * @throws IOException if anything occurs during the loading procedure.
     */
    void loadBuildPath(JsonNode root) throws IOException {
        var object = JsonUtil.getObjectOrThrow(root, "build_path", "The build path object cannot be null");
        var sourcePath = JsonUtil.getTextOrThrow(object, "source", "The source directory cannot be null or empty");
        var packPath = JsonUtil.getTextOrThrow(object, "pack", "The pack directory cannot be null or empty");
        buildPath = new BuildPath(directory.resolve(sourcePath), directory.resolve(packPath));
        buildPath.ensureExistence();
    }

    /**
     * Loads the compiler configuration of the project.
     *
     * @param root the root node to load the configuration from.
     */
    void loadCompiler(JsonNode root) {
        var compiler = JsonUtil.getObjectOrThrow(root, "compiler", "The compiler object cannot be null");
        instructionsPath = JsonUtil.getTextOrThrow(compiler, "instructions", "The instructions map cannot be null");
        triggersPath = JsonUtil.getTextOrThrow(compiler, "triggers", "The triggers cannot be null");
        commandsPath = JsonUtil.getTextOrThrow(compiler, "commands", "The commands cannot be null");
        runtimeConstantsPath = compiler.has("runtimeConstants") ? compiler.get("runtimeConstants").textValue() : "";
        predefinedScriptsPath = compiler.has("scripts") ? compiler.get("scripts").textValue() : "";
        predefinedConstantsPath = compiler.has("constants") ? compiler.get("constants").textValue() : "";
        packType = compiler.has("packType") ? PackType.valueOf(compiler.get("packType").textValue()) : PackType.SQLITE;
        var packer = root.get("packer");
        if (packer != null) {
            packType = PackType.valueOf(packer.get("type").textValue());
        } else {
            packType = PackType.FLATFILE;
        }
        reloadCompiler();
    }

    /**
     * Reloads the compiler configuration of the project.
     */
    public void reloadCompiler() {
        compilerEnvironment = new CompilerEnvironment();
        predefinedTable = new ScriptSymbolTable(false);
        symbolTable = predefinedTable.createSubTable();
        instructionMap = new InstructionMap();
        loadInstructions();
        loadTriggers();
        loadCommands();
        loadScripts();
        loadRuntimeConstants();
        compilerProvider = new ProjectCompilerProvider();
        registerScriptCompiler();
    }

    /**
     * Registers all of the script compiler(s) in the compiler provider.
     */
    private void registerScriptCompiler() {
        var scriptsCompiler = new ProjectScriptCompiler(ScriptCompiler.builder()
                .withEnvironment(compilerEnvironment)
                .withInstructionMap(instructionMap)
                .withSymbolTable(symbolTable)
                .withOverrideSymbols(overrideSymbols)
                .withSupportsLongPrimitiveType(supportsLongPrimitiveType)
                .withIdProvider(idManager)
                .build());
        compilerProvider.register("cs2", scriptsCompiler);
        compilerProvider.register("rs2", scriptsCompiler);
    }

    /**
     * Loads the instructions configuration of the project.
     */
    @SneakyThrows
    void loadInstructions() {
        var path = (Path) null;
        var pathRaw = instructionsPath;
        if (pathRaw.startsWith("*")) {
            pathRaw = pathRaw.substring(1);
            if ("osrs_default".equals(pathRaw)) {
                path = Paths.get(getClass().getResource("osrs_default_instructions.toml").toURI());
            } else {
                throw new IllegalStateException("Unrecognised macro: " + pathRaw);
            }
        } else {
            path = Paths.get(pathRaw);
        }
        if (!path.isAbsolute()) {
            path = directory.resolve(path);
        }
        if (!Files.exists(path)) {
            throw new IllegalStateException("The specified instructions file does not exist");
        }
        try (var config = CommentedFileConfig.of(path)) {
            config.load();
            for (var entry : config.entrySet()) {
                var key = entry.getKey();
                var value = (CommentedConfig) entry.getValue();
                var coreOpcode = CoreOpcode.valueOf(key.toUpperCase());
                var opcode = value.getInt("opcode");
                var large = value.getOrElse("large", false);
                instructionMap.registerCore(coreOpcode, opcode, large);
            }
        }
    }

    /**
     * Loads the triggers configuration of the project.
     */
    @SneakyThrows
    void loadTriggers() {
        var path = (Path) null;
        var pathRaw = triggersPath;
        if (pathRaw.startsWith("*")) {
            pathRaw = pathRaw.substring(1);
            if ("osrs_default".equals(pathRaw)) {
                path = Paths.get(getClass().getResource("osrs_default_triggers.toml").toURI());
            } else {
                throw new IllegalStateException("Unrecognised macro: " + pathRaw);
            }
        } else {
            path = Paths.get(pathRaw);
        }
        if (!path.isAbsolute()) {
            path = directory.resolve(path);
        }
        if (!Files.exists(path)) {
            throw new IllegalStateException("The specified trigger file does not exist");
        }
        try (var config = CommentedFileConfig.of(path)) {
            config.load();
            for (var entry : config.entrySet()) {
                var name = entry.getKey();
                var value = (CommentedConfig) entry.getValue();
                var operator = value.getOptionalEnum("operator", Kind.class).orElse(null);
                var opcode = value.getOptionalEnum("opcode", CoreOpcode.class).orElse(null);
                var supportArgument = value.getOrElse("support_arguments", false);
                var supportReturn = value.getOrElse("support_returns", false);
                var argumentTypes = value.contains("arguments") ? ProjectConfig.parseTypes(config, "arguments") : null;
                var returnTypes = value.contains("returns") ? ProjectConfig.parseTypes(config, "returns") : null;
                var hook = value.getOrElse("hook", false);
                var triggerType = new BasicTriggerType(name, operator, opcode, supportArgument, argumentTypes, supportReturn, returnTypes);
                if (hook) {
                    if (compilerEnvironment.getHookTriggerType() == null) {
                        compilerEnvironment.setHookTriggerType(triggerType);
                    } else {
                        log.warn("Multiple definition of hook triggers found: {} and {}", triggerType.getRepresentation(), compilerEnvironment.getHookTriggerType().getRepresentation());
                    }
                }
                compilerEnvironment.registerTrigger(triggerType);
            }
        }
    }

    /**
     * Loads the commands configuration of the project.
     */
    @SneakyThrows
    void loadCommands() {
        if (commandsPath == null || commandsPath.trim().isEmpty()) {
            return;
        }
        var path = (Path) null;
        if (commandsPath.startsWith("*")) {
            var name = commandsPath.substring(1);
            if ("osrs_default".equals(name)) {
                path = Paths.get(getClass().getResource("osrs_default_commands.sym").toURI());
            } else {
                throw new IllegalStateException("Unrecognised macro: " + name);
            }
        } else {
            path = Paths.get(commandsPath);
        }
        if (!path.isAbsolute()) {
            path = directory.resolve(commandsPath);
        }
        if (!Files.exists(path)) {
            throw new IllegalStateException("The specified commands file does not exist: " + path);
        }
        Pattern pattern = Pattern.compile("\\[(?<id>\\d+),(?<dot>true|false),(?<name>[\\w_]+)\\]\\((?<args>\\w+(,\\w+)*)?\\)\\((?<returns>\\w+(,\\w+)*)?\\)(?<transmits>\\{\\w+\\})?");
        Files.lines(path).forEach(line -> {
            var matcher = pattern.matcher(line);
            if (!matcher.matches()) {
                return;
            }
            var id = Integer.parseInt(matcher.group("id"));
            var dot = Boolean.parseBoolean(matcher.group("dot"));
            var name = matcher.group("name");
            var args = matcher.group("args") == null ? new Type[0] :
                    Arrays.stream(matcher.group("args").split(","))
                            .map(PrimitiveType::forLiteral)
                            .toArray(Type[]::new);
            var returns = matcher.group("returns") == null ? new Type[0] :
                    Arrays.stream(matcher.group("returns").split(","))
                            .map(PrimitiveType::forLiteral)
                            .toArray(Type[]::new);
            var transmitsRaw = matcher.group("transmits");
            var transmits = transmitsRaw != null
                    ? PrimitiveType.forLiteral(transmitsRaw.substring(1, transmitsRaw.length() - 1)) : null;
            var combinedReturns = returns.length > 1 ? new TupleType(returns) : returns.length == 0 ? TupleType.EMPTY : returns[0];
            symbolTable.defineCommand(new BasicOpcode(id, false), name, args, combinedReturns, transmits, dot);
        });
    }

    private static void extracted(String name, int opcode, boolean dot, Type[] arguments, Type hookType, Type returnTypes) {
        System.out.println("[" + opcode + "," + dot + "," + name + "]" +
                "(" + Arrays.stream(arguments).map(Type::getRepresentation).collect(Collectors.joining(",")) + ")" +
                "(" + Arrays.stream(TypeUtil.flatten(new Type[]{returnTypes})).map(Type::getRepresentation).collect(Collectors.joining(",")) + ")" +
                (hookType != null ? "{" + hookType.getRepresentation() + "}" : ""));
    }

    /**
     * Loads all the predefined scripts of the projects.
     */
    private void loadScripts() {
        if (predefinedScriptsPath == null || predefinedScriptsPath.trim().isEmpty()) {
            return;
        }
        var path = Paths.get(predefinedScriptsPath);
        if (!path.isAbsolute()) {
            path = directory.resolve(predefinedScriptsPath);
        }
        if (!Files.exists(path)) {
            log.info("The specified predefined scripts file does not exist: {}", predefinedScriptsPath);
            return;
        }
        try {
            try (var config = CommentedFileConfig.of(path.toFile())) {
                config.load();
                for (var entry : config.entrySet()) {
                    try {
                        var value = (CommentedConfig) entry.getValue();
                        var id = value.getInt("id");
                        var name = value.<String>get("name");
                        var trigger = compilerEnvironment.lookupTrigger(value.<String>get("trigger"));
                        var type = ProjectConfig.parseTypes(value, "type");
                        var arguments = ProjectConfig.parseTypes(value, "arguments");
                        var returnType = type.length < 1 ? TupleType.EMPTY : type.length == 1 ? type[0] : new TupleType(type);
                        predefinedTable.defineScript(new ScriptInfo(name, id, trigger, returnType, arguments));
                    } catch (Throwable e) {
                        log.error("An error occurred while loading the predefined script for key: {}", entry.getKey(), e);
                    }
                }
            }
        } catch (Throwable e) {
            log.error("An error occurred while loading the predefined scripts file for path: {}", predefinedScriptsPath, e);
        }
    }

    /**
     * Loads all of the predefined runtime constants of the project.
     */
    private void loadRuntimeConstants() {
        if (runtimeConstantsPath == null || runtimeConstantsPath.trim().isEmpty()) {
            return;
        }
        var path = Paths.get(runtimeConstantsPath);
        if (!path.isAbsolute()) {
            path = directory.resolve(runtimeConstantsPath);
        }
        if (!Files.exists(path)) {
            log.info("The specified runtime constants file does not exist: {}", runtimeConstantsPath);
            return;
        }
        try {
            try (var fileConfig = CommentedFileConfig.of(path.toFile())) {
                fileConfig.load();
                for (var entry : fileConfig.entrySet()) {
                    var config = (CommentedConfig) entry.getValue();
                    var name = entry.getKey();
                    var type = PrimitiveType.forLiteral(config.get("type"));
                    Object value;
                    switch (type.getStackType()) {
                        case INT:
                            value = config.getInt("value");
                            break;
                        case STRING:
                            value = config.<String>get("value");
                            break;
                        case LONG:
                            value = config.getLong("value");
                            break;
                        default:
                            throw new UnsupportedOperationException();
                    }
                    symbolTable.defineRuntimeConstant(name, name.hashCode(), type, value);
                }
            }
        } catch (Throwable e) {
            log.error("An error occurred while loading the runtime constants file for path: {}", runtimeConstantsPath, e);
        }
    }

    /**
     * Gets called after the project has been loaded.
     */
    private void postLoad() {
        packManager = new PackManager(packType.newInstance(buildPath.getPackDirectory().toAbsolutePath()));
        vfs = new VFS(directory);
        loadSym();
        loadCache();
    }

    /**
     * Loads the cache of the project.
     */
    private void loadCache() {
        var rootPath = resolveRsPath();
        var cacheFile = rootPath.resolve("cache.bin");
        cache = new Cache(this);
        if (Files.exists(cacheFile)) {
            try (var stream = new DataInputStream(Files.newInputStream(cacheFile))) {
                cache.deserialize(stream);
            } catch (IOException e) {
                log.error("An error occurred while loading the project cache", e);
            }
        }
        cache.getUnits().values().forEach(unit -> unit.defineSymbols(symbolTable));
        try {
            cache.diff();
        } catch (IOException e) {
            throw new ProjectException("Failed to generate the cache diff for the project cache", e);
        }
        updateErrors();
    }

    /**
     * Loads the index tables of the project.
     */
    private void loadSym() {
        var rootPath = directory;
        var symPath = rootPath.resolve("sym");
        var pattern = Pattern.compile("(\\w+)\\.sym");
        try {
            Files.list(symPath).forEach(file -> {
                var matcher = pattern.matcher(file.getFileName().toString());
                if (matcher.matches()) {
                    var literal = matcher.group(1);
                    PrimitiveType type = PrimitiveType.forRepresentation(literal);
                    if (type == null) {
                        return;
                    }
                    try {
                        symbolTable.read(type, file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates all of the errors of the project.
     */
    public void updateErrors() {
        var errorsView = Api.getApi().getUi().getErrorsView();
        errorsView.clearErrors();
        for (var unit : cache.getUnits().values()) {
            var path = unit.getNameWithPath();
            for (var cachedError : unit.getErrors()) {
                errorsView.addError(cachedError.getMessage(), path, cachedError.getLine());
            }
        }
    }

    /**
     * Updates the errors in the error view that belong to the specified {@link CacheUnit unit}.
     *
     * @param unit the cache unit to update the errors for.
     */
    public void updateErrors(CacheUnit<?> unit) {
        var errorsView = Api.getApi().getUi().getErrorsView();
        var path = unit.getNameWithPath();
        errorsView.removeErrorForPath(path);
        if (unit.getErrors().isEmpty()) {
            return;
        }
        for (var cachedError : unit.getErrors()) {
            errorsView.addError(cachedError.getMessage(), path, cachedError.getLine());
        }
    }

    /**
     * Saves the cache of the project.
     */
    public void saveCache() {
        var rootPath = resolveRsPath();
        var cacheFile = rootPath.resolve("cache.bin");
        try (var stream = new DataOutputStream(Files.newOutputStream(cacheFile, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE))) {
            cache.serialize(stream);
        } catch (IOException e) {
            log.error("An error occurred while writing the project cache", e);
        }
    }

    /**
     * Resolves the root .rs directory path and create it if it does not exist.
     *
     * @return the {@link Path} object of the .rs directory.
     * @throws ProjectException if the creation of the .rs directory failed.
     */
    private Path resolveRsPath() {
        var path = directory.resolve(".rs/");
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new ProjectException("Failed to create the .rs directory in the project root directory", e);
            }
        }
        return path;
    }

    /**
     * Saves the information data of the project to the local disk.
     *
     * @throws IOException if anything occurs during the saving procedure.
     */
    public void saveData() throws IOException {
        // Create the project root node.
        var root = JsonUtil.getMapper().createObjectNode();
        // Serialise the general information.
        root.put("name", name);
        // Serialise the build path information.
        var buildPath = root.putObject("build_path");
        buildPath.put("source", directory.relativize(this.buildPath.getSourceDirectory()).toString());
        buildPath.put("pack", directory.relativize(this.buildPath.getPackDirectory()).toString());
        var compiler = root.putObject("compiler");
        compiler.put("instructions", instructionsPath);
        compiler.put("triggers", triggersPath);
        compiler.put("commands", commandsPath);
        compiler.put("runtimeConstants", runtimeConstantsPath);
        compiler.put("scripts", predefinedScriptsPath);
        compiler.put("constants", predefinedConstantsPath);
        root.put("supportsLongPrimitiveType", supportsLongPrimitiveType);
        root.put("overrideSymbols", overrideSymbols);
        // Serialise the pack information.
        var packer = root.putObject("packer");
        packer.put("type", packType.name());
        // Write the serialised data into the project file.
        JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValue(findProjectFile().toFile(), root);
        // Save the cache of the project to the local disk.
        cache.performSaving();
    }

    /**
     * Closes the virtual file system of the project.
     */
    public void closeVfs() {
        try {
            vfs.close();
        } finally {
            vfs = null;
        }
    }

    /**
     * Finds the {@code .rspoj} file of the project.
     *
     * @return the {@link Path} object which leads to that file.
     */
    private Path findProjectFile() {
        return directory.resolve(FILE_NAME);
    }

    /**
     * Represents {@link IDManager} implementation for projects.
     *
     * @author Walied K. Yassen
     */
    @RequiredArgsConstructor
    public static final class ProjectIDManager implements IDManager {

        /**
         * The project this provider is for.
         */
        private final Project project;

        @Override
        public int findOrCreateScriptId(String name, String extension) {
            var symbol = project.symbolTable.lookupScript(name);
            if (symbol != null) {
                return symbol.getId();
            }
            return generateScriptId();
        }

        private int generateScriptId() {
            return project.symbolTable.getScripts()
                    .values()
                    .stream()
                    .max(Comparator.comparingInt(ScriptInfo::getId))
                    .map(ScriptInfo::getId)
                    .orElse(-1) + 1;
        }


        @Override
        public int findScript(String name, String extension) throws IllegalArgumentException {
            var existing = project.symbolTable.lookupScript(name);
            if (existing != null) {
                return existing.getId();
            }
            throw new IllegalArgumentException("Failed to find an id for script with name: " + name);
        }
    }
}

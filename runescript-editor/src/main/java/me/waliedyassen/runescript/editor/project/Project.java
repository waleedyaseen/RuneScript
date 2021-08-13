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
import me.waliedyassen.runescript.compiler.util.trigger.BasicTriggerType;
import me.waliedyassen.runescript.config.binding.ConfigBinding;
import me.waliedyassen.runescript.config.compiler.ConfigCompiler;
import me.waliedyassen.runescript.config.var.ConfigParamProperty;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.pack.manager.PackManager;
import me.waliedyassen.runescript.editor.project.build.BuildPath;
import me.waliedyassen.runescript.editor.project.cache.Cache;
import me.waliedyassen.runescript.editor.project.cache.unit.CacheUnit;
import me.waliedyassen.runescript.editor.project.compile.ProjectCompiler;
import me.waliedyassen.runescript.editor.project.compile.ProjectCompilerProvider;
import me.waliedyassen.runescript.editor.project.compile.impl.ProjectConfigCompiler;
import me.waliedyassen.runescript.editor.project.compile.impl.ProjectScriptCompiler;
import me.waliedyassen.runescript.editor.ui.editor.project.ProjectEditor;
import me.waliedyassen.runescript.editor.util.JsonUtil;
import me.waliedyassen.runescript.editor.vfs.VFS;
import me.waliedyassen.runescript.index.Index;
import me.waliedyassen.runescript.type.Type;
import me.waliedyassen.runescript.type.primitive.PrimitiveType;
import me.waliedyassen.runescript.type.tuple.TupleType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * The index table for the script files.
     */
    @Getter
    private Index<String> index;

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
     * A map which contains all of the predefined configuration paths.
     */
    @Getter
    private final Map<PrimitiveType, String> configsPath;

    /**
     * A map which contains all of the configuration bindings paths.
     */
    @Getter
    private final Map<PrimitiveType, String> bindingsPath;

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
        configsPath = new HashMap<>();
        bindingsPath = new HashMap<>();
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
        configsPath.clear();
        for (var type : PrimitiveType.values()) {
            if (!ProjectEditor.isPredefinable(type)) {
                continue;
            }
            var node = compiler.get("config_" + type.getRepresentation());
            if (node == null) {
                continue;
            }
            configsPath.put(type, node.textValue());
        }
        bindingsPath.clear();
        for (var type : PrimitiveType.values()) {
            if (!type.isConfigType()) {
                continue;
            }
            var node = compiler.get("binding_" + type.getRepresentation());
            if (node == null) {
                continue;
            }
            bindingsPath.put(type, node.textValue());
        }
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
        loadConfigs();
        loadScripts();
        loadConstants();
        loadRuntimeConstants();
        compilerProvider = new ProjectCompilerProvider();
        var configCompiler = new ConfigCompiler(new ProjectIDManager(this), symbolTable, overrideSymbols);
        loadBindings(configCompiler);
        registerScriptCompiler();
        registerConfigCompiler(configCompiler);
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
     * Registers all of the config compiler(s) in the compiler provider.
     *
     * @param configCompiler the underlying compiler tool for the project compiler.
     */
    private void registerConfigCompiler(ConfigCompiler configCompiler) {
        var projectCompiler = new ProjectConfigCompiler(configCompiler);
        configCompiler.getBindings().keySet().forEach(extension -> {
            compilerProvider.register(extension, projectCompiler);
        });
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
                path = Paths.get(getClass().getResource("osrs_default_commands.toml").toURI());
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
        try (var config = CommentedFileConfig.of(path)) {
            config.load();
            for (var entry : config.entrySet()) {
                var name = entry.getKey();
                var value = (CommentedConfig) entry.getValue();
                var opcode = value.getInt("opcode");
                var type = ProjectConfig.parseTypes(value, "type");
                var arguments = ProjectConfig.parseTypes(value, "arguments");
                var alternative = value.getOrElse("alternative", false);
                var hook = value.getOrElse("hook", false);
                var hookType = value.contains("hooktype") ? PrimitiveType.valueOf(value.get("hooktype")) : null;
                var tag = value.getOrElse("tag", (String) null);
                var returnTypes = type.length > 1 ? new TupleType(type) : type.length == 0 ? PrimitiveType.VOID : type[0];
                symbolTable.defineCommand(new BasicOpcode(opcode, false), name, returnTypes, arguments, hook, hookType, alternative, tag);
            }
        }
    }

    /**
     * Loads the config types configuration of the project.
     */
    void loadConfigs() {
        configsPath.forEach((type, pathRaw) -> {
            var path = Paths.get(pathRaw);
            if (!path.isAbsolute()) {
                path = directory.resolve(pathRaw);
            }
            if (!Files.exists(path)) {
                log.info("The specified configuration file does not exist for type {} and path: {}", type, pathRaw);
                return;
            }
            try {
                try (var config = CommentedFileConfig.of(path.toFile())) {
                    config.load();
                    for (var entry : config.entrySet()) {
                        var value = entry.getValue();
                        if (value instanceof Integer) {
                            defineConfig(type, (Integer) value, entry.getKey(), null);
                        } else if (value instanceof CommentedConfig) {
                            var commentedConfig = (CommentedConfig) value;
                            var id = commentedConfig.getInt("id");
                            var name = commentedConfig.contains("name") ? commentedConfig.<String>get("name") : entry.getKey();
                            var contentType = commentedConfig.contains("contentType") ? PrimitiveType.valueOf(commentedConfig.get("contentType")) : null;
                            defineConfig(type, id, name, contentType);
                        } else {
                            throw new IllegalArgumentException("Invalid preloaded config value: " + value.getClass().getName());
                        }
                    }
                }
            } catch (Throwable e) {
                log.error("An error occurred while loading the configuration file for type: {} and path: {}", type, pathRaw, e);
            }
        });
    }

    private void defineConfig(PrimitiveType type, int id, String name, PrimitiveType contentType) {
        if (type == PrimitiveType.GRAPHIC) {
            predefinedTable.defineGraphic(name, id);
        } else {
            var info = predefinedTable.defineConfig(name, type, contentType);
            info.setPredefinedId(id);
        }
    }

    /**
     * Loads all of the predefined scripts of the projects.
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
                    var value = (CommentedConfig) entry.getValue();
                    var id = value.getInt("id");
                    var name = value.<String>get("name");
                    var trigger = compilerEnvironment.lookupTrigger(value.<String>get("trigger"));
                    var type = ProjectConfig.parseTypes(value, "type");
                    var arguments = ProjectConfig.parseTypes(value, "arguments");
                    var returnType = type.length < 1 ? PrimitiveType.VOID : type.length == 1 ? type[0] : new TupleType(type);
                    predefinedTable.defineScript(Collections.emptyMap(), trigger, name, returnType, arguments, id);
                }
            }
        } catch (Throwable e) {
            log.error("An error occurred while loading the predefined scripts file for path: {}", predefinedScriptsPath, e);
        }
    }

    /**
     * Loads all of the predefined constants of the project.
     */
    private void loadConstants() {
        if (predefinedConstantsPath == null || predefinedConstantsPath.trim().isEmpty()) {
            return;
        }
        var path = Paths.get(predefinedConstantsPath);
        if (!path.isAbsolute()) {
            path = directory.resolve(predefinedConstantsPath);
        }
        if (!Files.exists(path)) {
            log.info("The specified constants file does not exist: {}", predefinedConstantsPath);
            return;
        }
        try {
            try (var fileConfig = CommentedFileConfig.of(path.toFile())) {
                fileConfig.load();
                for (var entry : fileConfig.entrySet()) {
                    var object = entry.getValue();
                    if (object instanceof Integer) {
                        symbolTable.defineConstant(entry.getKey(), PrimitiveType.INT, object);
                    } else if (object instanceof Long) {
                        symbolTable.defineConstant(entry.getKey(), PrimitiveType.LONG, object);
                    } else if (object instanceof String) {
                        symbolTable.defineConstant(entry.getKey(), PrimitiveType.STRING, object);
                    } else {
                        throw new IllegalArgumentException("Unrecognised value in the predefined constant(s) file for key: " + entry.getKey());
                    }
                }
            }
        } catch (Throwable e) {
            log.error("An error occurred while loading the predefined constants file for path: {}", predefinedConstantsPath, e);
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
                    var type = PrimitiveType.valueOf(config.get("type"));
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
                    symbolTable.defineRuntimeConstant(name, type, value);
                }
            }
        } catch (Throwable e) {
            log.error("An error occurred while loading the runtime constants file for path: {}", runtimeConstantsPath, e);
        }
    }

    /**
     * Loads all of the configuration bindings of the project.
     */
    void loadBindings(ConfigCompiler configCompiler) {
        bindingsPath.forEach((type, pathRaw) -> {
            var path = Paths.get(pathRaw);
            if (!path.isAbsolute()) {
                path = directory.resolve(pathRaw);
            }
            if (!Files.exists(path)) {
                log.info("The specified configuration binding file does not exist for type {} and path: {}", type, pathRaw);
                return;
            }
            try {
                try (var config = CommentedFileConfig.of(path.toFile())) {
                    config.load();
                    var binding = new ConfigBinding(() -> type);
                    configCompiler.registerBinding(type.getRepresentation(), binding);
                    for (var entry : config.entrySet()) {
                        if (entry.getKey().contentEquals("config")) {
                            continue;
                        }
                        var value = (CommentedConfig) entry.getValue();
                        var entryType = value.getOrElse("type", "BASIC");
                        switch (entryType) {
                            case "BASIC": {
                                var opcode = value.getInt("opcode");
                                var required = value.getOrElse("required", false);
                                var components = ProjectConfig.parsePrimitiveTypes(value, "components");
                                var rules = ProjectConfig.parseConfigRules(value, "rules");
                                binding.addBasicProperty(entry.getKey(), opcode, required, components, rules);
                                break;
                            }
                            case "BASIC_REPEAT": {
                                var opcode = value.getInt("opcode");
                                var required = value.getOrElse("required", false);
                                var components = ProjectConfig.parsePrimitiveTypes(value, "components");
                                var rules = ProjectConfig.parseConfigRules(value, "rules");
                                var count = value.getInt("count");
                                var format = value.getOrElse("format", entry.getKey() + "%d");
                                binding.addBasicProperty(format, opcode, required, components, rules, count);
                                break;
                            }
                            case "BASIC_DYNAMIC": {
                                var opcodes = value.<List<Integer>>get("opcodes");
                                if (opcodes.size() != 2) {
                                    throw new IllegalArgumentException("Expected 2 values (int opcode, string opcode) for opcodes field in property: " + entry.getKey());
                                }
                                var inferring = ProjectConfig.parseInferredVariable(config, value.get("typeProperty"));
                                binding.addBasicDynamicProperty(entry.getKey(), inferring, opcodes.stream().mapToInt(Integer::intValue).toArray());
                                break;
                            }
                            case "SPLIT_ARRAY": {
                                var opcode = value.getInt("opcode");
                                var required = value.getOrElse("required", false);
                                var components = ProjectConfig.parsePrimitiveTypes(value, "components");
                                var rules = ProjectConfig.parseConfigRules(value, "rules");
                                var sizeType = PrimitiveType.valueOf(value.get("sizeType"));
                                var maxSize = value.getInt("maxSize");
                                var names = value.<List<String>>get("names");
                                binding.addSplitArrayProperty(entry.getKey(), opcode, required, names.toArray(new String[0]), components, rules, sizeType, maxSize);
                                break;
                            }
                            case "MAP": {
                                var opcodes = value.<List<Integer>>get("opcodes");
                                if (opcodes.size() != 2) {
                                    throw new IllegalArgumentException("Expected 2 values (int opcode, string opcode) for opcodes field in property: " + entry.getKey());
                                }
                                var keyTypeProperty = ProjectConfig.parseInferredVariable(config, value.get("keyTypeProperty"));
                                var valueTypeProperty = ProjectConfig.parseInferredVariable(config, value.get("valueTypeProperty"));
                                binding.addMapProperty(entry.getKey(), opcodes.stream().mapToInt(Integer::intValue).toArray(), keyTypeProperty, valueTypeProperty);
                                break;
                            }
                            default: {
                                throw new IllegalArgumentException("The specified type is not recognised: " + entryType);
                            }
                        }
                    }
                    var contentTypeProperty = config.contains("config.content_type_property") ? config.<String>get("config.content_type_property") : null;
                    if (contentTypeProperty != null) {
                        binding.setContentTypeProperty(contentTypeProperty);
                        var prop = binding.findProperty(binding.getContentTypeProperty());
                        if (prop == null || prop.getComponents().length != 1 || prop.getComponents()[0] != PrimitiveType.TYPE) {
                            throw new IllegalArgumentException("Malformed content type property: " + binding.getContentTypeProperty());
                        }
                    }
                    if (config.getOrElse("config.add_param_property", false)) {
                        binding.addProperty("param", new ConfigParamProperty("param", 249));
                    }
                    if (config.getOrElse("config.add_transmit_property", false)) {
                        binding.addBasicProperty("transmit", 250, false, new PrimitiveType[]{PrimitiveType.BOOLEAN}, new List[]{Collections.emptyList()});
                    }
                }
            } catch (Throwable e) {
                log.error("An error occurred while loading the configuration binding file for type: {} and path: {}", type, pathRaw, e);
            }
        });
    }

    /**
     * Gets called after the project has been loaded.
     */
    private void postLoad() {
        packManager = new PackManager(packType.newInstance(buildPath.getPackDirectory().toAbsolutePath()));
        vfs = new VFS(directory);
        loadIndex();
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
    private void loadIndex() {
        var rootPath = resolveRsPath();
        var indexFile = rootPath.resolve("index.bin");
        index = new Index<>();
        if (Files.exists(indexFile)) {
            try (var stream = new DataInputStream(Files.newInputStream(indexFile))) {
                var key = stream.readUTF();
                var table = index.create(key);
                table.read(stream);
            } catch (IOException e) {
                log.error("An error occurred while loading the project cache", e);
            }
        } else {
            index.create(getPackName("rs2"));
            index.create(getPackName("cs2")).setCursor(10000);
            saveIndex();
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
     * Save the index tables of the project.
     */
    public void saveIndex() {
        var rootPath = resolveRsPath();
        var cacheFile = rootPath.resolve("index.bin");
        try (var stream = new DataOutputStream(Files.newOutputStream(cacheFile, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE))) {
            for (var entry : index.getTables().entrySet()) {
                stream.writeUTF(entry.getKey());
                entry.getValue().write(stream);
            }
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
        for (var type : PrimitiveType.values()) {
            if (!ProjectEditor.isPredefinable(type)) {
                continue;
            }
            var path = configsPath.get(type);
            if (path == null) {
                continue;
            }
            compiler.put("config_" + type.getRepresentation(), path);
        }
        for (var type : PrimitiveType.values()) {
            if (!type.isConfigType()) {
                continue;
            }
            var path = bindingsPath.get(type);
            if (path == null) {
                continue;
            }
            compiler.put("binding_" + type.getRepresentation(), path);
        }
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
     * Returns the pack database name for the file with the specified {@code extension}.
     *
     * @param extension the extension of the file the pack database name is for.
     * @return the name of the pack database.
     * @throws IllegalArgumentException if we failed to find a pack database name for the given extension.
     */
    public static String getPackName(String extension) {
        switch (extension) {
            case "cs2":
                return "clientscript";
            case "rs2":
                return "serverscript";
            default:
                PrimitiveType type = PrimitiveType.forRepresentation(extension);
                if (type != null && type.isConfigType()) {
                    return "config-" + type.getRepresentation();
                }
                throw new IllegalArgumentException("Failed to find a pack database name for extension: " + extension);
        }
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

        /**
         * {@inheritDoc}
         */
        @Override
        public int findOrCreateScript(String name, String extension) {
            var index = project.index.getOrCreate(getPackName(extension));
            return index.findOrCreate(name);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int findOrCreateConfig(Type type, String name) {
            if (!(type instanceof PrimitiveType)) {
                throw new IllegalArgumentException();
            }
            var config = project.symbolTable.lookupConfig(name);
            if (config != null && config.getPredefinedId() != null) {
                if (config.getType() != type) {
                    throw new IllegalStateException();
                }
                return config.getPredefinedId();
            }
            var packName = getPackName(type.getRepresentation());
            var index = project.index.getOrCreate(packName);
            return index.findOrCreate(name);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int findScript(String name, String extension) throws IllegalArgumentException {
            var index = project.index.get(getPackName(extension));
            var id = index.find(name);
            if (id == null) {
                throw new IllegalArgumentException("Failed to find an id for script with name: " + name);
            }
            return id;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int findConfig(Type type, String name) throws IllegalArgumentException {
            if (type instanceof PrimitiveType) {
                var config = project.symbolTable.lookupConfig(name);
                if (config != null && config.getPredefinedId() != null) {
                    if (config.getType() != type) {
                        throw new IllegalStateException();
                    }
                    return config.getPredefinedId();
                }
                var index = project.index.get(getPackName(type.getRepresentation()));
                if (index != null) {
                    var id = index.find(name);
                    if (id != null) {
                        return id;
                    }
                }
            }
            throw new IllegalArgumentException("Failed to find an id for config with name: " + name + " and type: " + type.getRepresentation());
        }
    }
}

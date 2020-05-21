/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.waliedyassen.runescript.compiler.Compiler;
import me.waliedyassen.runescript.compiler.codegen.InstructionMap;
import me.waliedyassen.runescript.compiler.codegen.opcode.BasicOpcode;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.util.trigger.BasicTriggerType;
import me.waliedyassen.runescript.editor.pack.manager.PackManager;
import me.waliedyassen.runescript.editor.pack.provider.impl.SQLitePackProvider;
import me.waliedyassen.runescript.editor.project.build.BuildPath;
import me.waliedyassen.runescript.editor.util.JsonUtil;
import me.waliedyassen.runescript.type.PrimitiveType;
import me.waliedyassen.runescript.type.TupleType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A very basic project system that provides basic information such as the name and the build path directories.
 *
 * @author Walied K. Yassen
 */
public final class Project {

    /**
     * The project information file name.
     */
    static final String FILE_NAME = ".rsproj";

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
     * The compiler we are going to use for this project.
     */
    @Getter
    private Compiler compiler;

    /**
     * The instruction map of the project.
     */
    private InstructionMap instructionMap;

    /**
     * The compiler environment of the project.
     */
    private CompilerEnvironment compilerEnvironment;

    /**
     * The commands configuration path.
     */
    private String commandsPath;

    /**
     * The triggers configuration path.
     */
    private String triggersPath;

    /**
     * The instructions configuration path.
     */
    private String instructionsPath;

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
     * @param root
     */
    void loadCompiler(JsonNode root) {
        var object = JsonUtil.getObjectOrThrow(root, "compiler", "The compiler object cannot be null");
        instructionsPath = JsonUtil.getTextOrThrow(object, "instructions", "The instructions map cannot be null");
        triggersPath = JsonUtil.getTextOrThrow(object, "commands", "The instructions map cannot be null");
        commandsPath = JsonUtil.getTextOrThrow(object, "triggers", "The instructions map cannot be null");
        compilerEnvironment = new CompilerEnvironment();
        instructionMap = new InstructionMap();
        loadInstructions(instructionsPath);
        loadTriggers(triggersPath);
        compiler = new Compiler(compilerEnvironment, instructionMap);
        loadCommands(commandsPath);
    }

    /**
     * Loads the instructiosn configuration of the project.
     *
     * @param path the path which leads to the instructions configuration.
     */
    @SneakyThrows
    void loadInstructions(String path) {
        var file = (File) null;
        if (path.startsWith("*")) {
            path = path.substring(1);
            if ("osrs_default".equals(path)) {
                file = new File(getClass().getResource("osrs_default_instructions.toml").toURI());
            } else {
                throw new IllegalStateException("Unrecognised macro: " + path);
            }
        } else {
            file = new File(path);
        }
        if (!file.exists()) {
            throw new IllegalStateException("The specified instructions file does not exist");
        }
        try (var config = CommentedFileConfig.of(file)) {
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
     *
     * @param path the path which leads to the triggers configuration.
     */
    @SneakyThrows
    void loadTriggers(String path) {
        var file = (File) null;
        if (path.startsWith("*")) {
            path = path.substring(1);
            if ("osrs_default".equals(path)) {
                file = new File(getClass().getResource("osrs_default_triggers.toml").toURI());
            } else {
                throw new IllegalStateException("Unrecognised macro: " + path);
            }
        } else {
            file = new File(path);
        }
        if (!file.exists()) {
            throw new IllegalStateException("The specified instructions file does not exist");
        }
        try (var config = CommentedFileConfig.of(file)) {
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
                compilerEnvironment.registerTrigger(new BasicTriggerType(name, operator, opcode, supportArgument, argumentTypes, supportReturn, returnTypes));
            }
        }
    }


    /**
     * Loads the commands configuration of the project.
     *
     * @param path the path which leads to the commands configuration.
     */
    @SneakyThrows
    void loadCommands(String path) {
        var file = (File) null;
        if (path.startsWith("*")) {
            path = path.substring(1);
            if ("osrs_default".equals(path)) {
                file = new File(getClass().getResource("osrs_default_commands.toml").toURI());
            } else {
                throw new IllegalStateException("Unrecognised macro: " + path);
            }
        } else {
            file = new File(path);
        }
        if (!file.exists()) {
            throw new IllegalStateException("The specified instructions file does not exist");
        }
        try (var config = CommentedFileConfig.of(file)) {
            config.load();
            for (var entry : config.entrySet()) {
                var name = entry.getKey();
                var value = (CommentedConfig) entry.getValue();
                var opcode = value.getInt("opcode");
                var type = ProjectConfig.parseTypes(value, "type");
                var arguments = ProjectConfig.parseTypes(value, "arguments");
                var alternative = value.getOrElse("alternative", false);
                var hook = value.getOrElse("hook", false);
                compiler.getSymbolTable().defineCommand(new BasicOpcode(opcode, false), name, type.length > 1 ? new TupleType(type) : type.length == 0 ? PrimitiveType.VOID : type[0], arguments, hook, alternative);
            }
        }
    }


    /**
     * Gets called after the project has been loaded.
     */
    private void postLoad() {
        packManager = new PackManager(new SQLitePackProvider(buildPath.getPackDirectory().toAbsolutePath()));
    }

    /**
     * Saves the information data of the project to the local disk.
     *
     * @throws IOException if anything occurs during the saving procedure.
     */
    void saveData() throws IOException {
        // Create the project root node.
        var root = JsonUtil.getMapper().createObjectNode();
        // Serialise the general information.
        root.put("name", name);
        // Serialise the build path information.
        var buildPath = root.putObject("build_path");
        buildPath.put("source", directory.relativize(this.buildPath.getSourceDirectory()).toString());
        buildPath.put("pack", directory.relativize(this.buildPath.getPackDirectory()).toString());
        var compiler = root.putObject("build_path");
        compiler.put("instructions", instructionsPath);
        compiler.put("triggers", triggersPath);
        compiler.put("commands", commandsPath);
        // Write the serialised data into the project file.
        JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValue(findProjectFile().toFile(), root);
    }

    /**
     * Finds the {@code .rspoj} file of the project.
     *
     * @return the {@link Path} object which leads to that file.
     */
    private Path findProjectFile() {
        return directory.resolve(FILE_NAME);
    }
}

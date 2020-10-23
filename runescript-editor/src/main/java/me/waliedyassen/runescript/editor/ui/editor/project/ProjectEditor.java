/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor.project;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import me.waliedyassen.runescript.editor.EditorIcons;
import me.waliedyassen.runescript.editor.file.impl.ProjectFileType;
import me.waliedyassen.runescript.editor.project.PackType;
import me.waliedyassen.runescript.editor.project.Project;
import me.waliedyassen.runescript.editor.ui.editor.Editor;
import me.waliedyassen.runescript.type.primitive.PrimitiveType;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents an {@link Editor} for the {@link ProjectFileType}.
 *
 * @author Walied K. Yassen
 */
@Slf4j
@RequiredArgsConstructor
public final class ProjectEditor extends Editor<Path> {

    /**
     * The view component of the editor.
     */
    private final ProjectEditorUI viewComponent = new ProjectEditorUI();

    /**
     * Th key of the editor.
     */
    @Getter
    private final Path key;

    /**
     * The project which we are editing.
     */
    private final Project project;

    /**
     * {@inheritDoc}
     */
    @Override
    public void reload() {
        viewComponent.instructionsField.setText(project.getInstructionsPath());
        viewComponent.commandsField.setText(project.getCommandsPath());
        viewComponent.triggersField.setText(project.getTriggersPath());
        viewComponent.runtimeConstantsField.setText(project.getRuntimeConstantsPath());
        viewComponent.predefinedConstantsField.setText(project.getPredefinedConstantsPath());
        viewComponent.predefinedScriptsField.setText(project.getPredefinedScriptsPath());
        viewComponent.supportsLongTypeCheckBox.setSelected(project.isSupportsLongPrimitiveType());
        viewComponent.overrideSymbolsCheckBox.setSelected(project.isOverrideSymbols());
        viewComponent.predefinedConfigs.forEach((type, field) -> {
            var path = project.getConfigsPath().get(type);
            if (path == null) {
                field.setText("");
            } else {
                field.setText(path);
            }
        });
        viewComponent.configBindings.forEach((type, field) -> {
            var path = project.getBindingsPath().get(type);
            if (path == null) {
                field.setText("");
            } else {
                field.setText(path);
            }
        });
        viewComponent.packTypeComboBox.setSelectedItem(project.getPackType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save() {
        project.setInstructionsPath(viewComponent.instructionsField.getText());
        project.setCommandsPath(viewComponent.commandsField.getText());
        project.setTriggersPath(viewComponent.triggersField.getText());
        project.setPredefinedScriptsPath(viewComponent.predefinedScriptsField.getText());
        project.setRuntimeConstantsPath(viewComponent.runtimeConstantsField.getText());
        project.setPredefinedConstantsPath(viewComponent.predefinedConstantsField.getText());
        project.setSupportsLongPrimitiveType(viewComponent.supportsLongTypeCheckBox.isSelected());
        project.setOverrideSymbols(viewComponent.overrideSymbolsCheckBox.isSelected());
        project.setPackType((PackType) viewComponent.packTypeComboBox.getSelectedItem());
        project.getConfigsPath().clear();
        project.getConfigsPath().putAll(getConfigPathMap());
        project.getBindingsPath().clear();
        project.getBindingsPath().putAll(getBindingPathMap());
        project.reloadCompiler();
        try {
            project.saveData();
        } catch (IOException e) {
            log.error("Failed to save the project file", e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent getViewComponent() {
        return viewComponent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Icon getIcon() {
        return EditorIcons.FILETYPE_FILE_ICON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return "Project Properties";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTooltip() {
        return "Project Properties";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isModified() {
        var modified = false;
        modified |= !viewComponent.instructionsField.getText().equals(project.getInstructionsPath());
        modified |= !viewComponent.commandsField.getText().equals(project.getCommandsPath());
        modified |= !viewComponent.triggersField.getText().equals(project.getTriggersPath());
        modified |= !viewComponent.runtimeConstantsField.getText().equals(project.getRuntimeConstantsPath());
        modified |= !viewComponent.predefinedScriptsField.getText().equals(project.getPredefinedScriptsPath());
        modified |= !viewComponent.predefinedConstantsField.getText().equals(project.getPredefinedConstantsPath());
        modified |= !getConfigPathMap().equals(project.getConfigsPath());
        modified |= !getBindingPathMap().equals(project.getBindingsPath());
        modified |= project.isSupportsLongPrimitiveType() != viewComponent.supportsLongTypeCheckBox.isSelected();
        modified |= project.isOverrideSymbols() != viewComponent.overrideSymbolsCheckBox.isSelected();
        modified |= project.getPackType() != viewComponent.packTypeComboBox.getSelectedItem();
        return modified;
    }

    /**
     * Returns a map of all the predefined configuration type file paths mapped by the associated config primitive
     * type.
     *
     * @return the {@link Map} object.
     */
    private Map<PrimitiveType, String> getConfigPathMap() {
        return viewComponent.predefinedConfigs.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getText().trim().length() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getText().trim()));
    }

    /**
     * Returns a map of all the configuration bindings file paths mapped by the associated config primitive type.
     *
     * @return the {@link Map} object.
     */
    private Map<PrimitiveType, String> getBindingPathMap() {
        return viewComponent.configBindings.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getText().trim().length() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getText().trim()));
    }

    /**
     * The {@link ProjectEditor} UI view component.
     *
     * @author Walied K. Yassen
     */
    private static final class ProjectEditorUI extends JPanel {

        /**
         * The supports long primitive type check box.
         */
        private final JCheckBox supportsLongTypeCheckBox = new JCheckBox();

        /**
         * The override symbols type check box.
         */
        private final JCheckBox overrideSymbolsCheckBox = new JCheckBox();

        /**
         * The commands configuration file path text field.
         */
        private final JTextField commandsField = new JTextField();

        /**
         * The triggers configuration file path text field.
         */
        private final JTextField triggersField = new JTextField();

        /**
         * The instructions configuration file path text field.
         */
        private final JTextField instructionsField = new JTextField();

        /**
         * The triggers configuration file path text field.
         */
        private final JTextField predefinedScriptsField = new JTextField();

        /**
         * The runtime constants file path text field.
         */
        private final JTextField runtimeConstantsField = new JTextField();

        /**
         * The constants file path text field.
         */
        private final JTextField predefinedConstantsField = new JTextField();

        /**
         * A map of all the text fields that lead to the predefined configs.
         */
        private final Map<PrimitiveType, JTextField> predefinedConfigs = new HashMap<>();

        /**
         * A map of all the text fields that lead to the configuration bindings.
         */
        private final Map<PrimitiveType, JTextField> configBindings = new HashMap<>();

        /**
         * A combo box that holds all of the possible pack type to use.
         */
        private final JComboBox<PackType> packTypeComboBox = new JComboBox<>(PackType.values());

        /**
         * Constructs a new {@link ProjectEditorUI} type object instance.
         */
        public ProjectEditorUI() {
            super(new MigLayout("", "[grow]", "[][][]"));
            var optionsPanel = new JPanel(new MigLayout());
            optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));
            {
                optionsPanel.add(new JLabel("Supports long type:"));
                optionsPanel.add(supportsLongTypeCheckBox);
                optionsPanel.add(new JLabel("Override symbols:"));
                optionsPanel.add(overrideSymbolsCheckBox);
                optionsPanel.add(new JLabel("Pack Type:"));
                optionsPanel.add(packTypeComboBox);
            }
            add(optionsPanel, "growx,wrap");
            initSymbolsPanel();
            initBindingsPanel();
        }

        private void initSymbolsPanel() {
            var symbolPanel = new JPanel(new MigLayout("", "[][grow][]"));
            {
                createBrowseSymbolRow(symbolPanel, "Instructions", instructionsField);
                createBrowseSymbolRow(symbolPanel, "Commands", commandsField);
                createBrowseSymbolRow(symbolPanel, "Triggers", triggersField);
                createBrowseSymbolRow(symbolPanel, "Runtime Constants", runtimeConstantsField);
                createBrowseSymbolRow(symbolPanel, "Predefined constant(s)", predefinedConstantsField);
                createBrowseSymbolRow(symbolPanel, "Predefined script(s)", predefinedScriptsField);
                for (var type : PrimitiveType.values()) {
                    if (!isPredefinable(type)) {
                        continue;
                    }
                    var textField = new JTextField();
                    predefinedConfigs.put(type, textField);
                    createBrowseSymbolRow(symbolPanel, "Predefined ." + type.getRepresentation() + "(s)", textField);
                }
            }

            var scrollPane = new JScrollPane(symbolPanel);
            scrollPane.setBorder(BorderFactory.createTitledBorder("Symbol"));
            add(scrollPane, "growx,wrap");
        }

        private void initBindingsPanel() {
            var bindingPanel = new JPanel(new MigLayout("", "[][grow][]"));
            {
                for (var type : PrimitiveType.values()) {
                    if (!type.isConfigType()) {
                        continue;
                    }
                    var textField = new JTextField();
                    configBindings.put(type, textField);
                    createBrowseSymbolRow(bindingPanel, "Binding ." + type.getRepresentation(), textField);
                }
            }

            var scrollPane = new JScrollPane(bindingPanel);
            scrollPane.setBorder(BorderFactory.createTitledBorder("Binding"));
            add(scrollPane, "growx,wrap");
        }

        /**
         * Creates a browse for a symbol row, contains a label, a text field for the symbol configuration file path, and a brwose
         * button for browsing for symbol configuration file.
         *
         * @param panel
         *         the panel which we will add the symbol row to.
         * @param name
         *         the name of the symbol we are browsing for.
         * @param textField
         *         the text field to place the symbols in.
         */
        private void createBrowseSymbolRow(JPanel panel, String name, JTextField textField) {
            panel.add(new JLabel(name + ":"));
            panel.add(textField, "grow");
            var browseButton = new JButton("Browse");
            browseButton.addActionListener(e -> {
                var fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select the " + name.toLowerCase() + " file you want");
                fileChooser.setFileFilter(new FileNameExtensionFilter("TOML Configuration File", "toml"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            });
            panel.add(browseButton, "wrap");
        }
    }

    public static void main(String[] args) {

    }
    /**
     * Checks whether or not the specified {@link PrimitiveType type} can be predefined.
     *
     * @param type
     *         the type to check if it can.
     *
     * @return <code>true</code> if it can otherwise <code>false</code>.
     */
    public static boolean isPredefinable(PrimitiveType type) {
        if (type.isConfigType()) {
            return true;
        }
        switch (type) {
            case GRAPHIC:
            case SYNTH:
            case INTERFACE:
            case COMPONENT:
            case FONTMETRICS:
            case TEXTURE:
                return true;
            default:
                return false;
        }
    }
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor.project;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.waliedyassen.runescript.editor.EditorIcons;
import me.waliedyassen.runescript.editor.file.impl.ProjectFileType;
import me.waliedyassen.runescript.editor.project.Project;
import me.waliedyassen.runescript.editor.ui.editor.Editor;
import me.waliedyassen.runescript.type.PrimitiveType;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
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
public final class ProjectEditor extends Editor<Project> {

    /**
     * The view component of the editor.
     */
    private final ProjectEditorUI viewComponent = new ProjectEditorUI();

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
        viewComponent.supportsLongTypeCheckBox.setSelected(project.isSupportsLongPrimitiveType());
        viewComponent.predefinedConfigs.forEach((type, field) -> {
            var path = project.getConfigsPath().get(type);
            if (path == null) {
                field.setText("");
            } else {
                field.setText(path);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save() {
        project.setInstructionsPath(viewComponent.instructionsField.getText());
        project.setCommandsPath(viewComponent.commandsField.getText());
        project.setTriggersPath(viewComponent.triggersField.getText());
        project.setSupportsLongPrimitiveType(viewComponent.supportsLongTypeCheckBox.isSelected());
        project.getConfigsPath().clear();
        project.getConfigsPath().putAll(getConfigPathMap());
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
        modified |= !project.getInstructionsPath().contentEquals(viewComponent.instructionsField.getText());
        modified |= !project.getCommandsPath().contentEquals(viewComponent.commandsField.getText());
        modified |= !project.getTriggersPath().contentEquals(viewComponent.triggersField.getText());
        modified |= project.isSupportsLongPrimitiveType() != viewComponent.supportsLongTypeCheckBox.isSelected();
        modified |= !project.getConfigsPath().equals(getConfigPathMap());
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
                .filter(entry -> entry.getValue().getText().strip().length() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getText().strip()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Project getKey() {
        return project;
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
         * A map of all the text fields that lead to the predefined configs.
         */
        private final Map<PrimitiveType, JTextField> predefinedConfigs = new HashMap<>();

        /**
         * Constructs a new {@link ProjectEditorUI} type object instance.
         */
        public ProjectEditorUI() {
            super(new MigLayout("", "[grow]", "[][]"));
            var optionsPanel = new JPanel(new MigLayout("", "[][grow][]"));
            optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));
            {
                optionsPanel.add(new JLabel("Supports long type:"));
                optionsPanel.add(supportsLongTypeCheckBox, "wrap");
            }
            add(optionsPanel, "grow, wrap");
            var symbolPanel = new JPanel(new MigLayout("", "[][grow][]"));
            {
                createBrowseSymbolRow(symbolPanel, "Instructions", instructionsField);
                createBrowseSymbolRow(symbolPanel, "Commands", commandsField);
                createBrowseSymbolRow(symbolPanel, "Triggers", triggersField);
                for (var type : PrimitiveType.values()) {
                    var textField = new JTextField();
                    predefinedConfigs.put(type, textField);
                    if (type.isConfigType()) {
                        createBrowseSymbolRow(symbolPanel, "Predefined " + type.getRepresentation() + "(s)", textField);
                    }
                }
            }
            var scrolPane = new JScrollPane(symbolPanel);
            scrolPane.setBorder(BorderFactory.createTitledBorder("Symbol"));
            add(scrolPane, "grow, wrap");
        }

        /**
         * Creates a browse for a symbol row, contains a label, a text field for the symbol configuration file path, and a brwose
         * button for browsing for symbol configuration file.
         *
         * @param panel     the panel which we will add the symbol row to.
         * @param name      the name of the symbol we are browsing for.
         * @param textField the text field to place the symbols in.
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
}

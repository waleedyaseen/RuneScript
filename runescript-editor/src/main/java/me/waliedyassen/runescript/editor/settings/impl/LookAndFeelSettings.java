/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.settings.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme;
import lombok.Data;
import me.waliedyassen.runescript.editor.settings.Settings;
import me.waliedyassen.runescript.editor.settings.state.SettingsState;
import me.waliedyassen.runescript.editor.util.LafUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The Look and Feel settings page.
 *
 * @author Walied K. Yassen
 */
public final class LookAndFeelSettings extends Settings<LookAndFeelSettings.LookAndFeelSettingsState> {

    /**
     * The Look and Feel settings name.
     */
    public static final String NAME = "lookAndFeel";

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent createComponent() {
        return new LookAndFeelSettingsUI();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(LookAndFeelSettingsState oldState, LookAndFeelSettingsState newState) {
        if (isModified(oldState, newState, LookAndFeelSettingsState::getLookAndFeelName)) {
            LafUtil.changeLaf(newState.getLookAndFeelName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LookAndFeelSettingsState createDefaultState() {
        return new LookAndFeelSettingsState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<LookAndFeelSettingsState> getStateClass() {
        return LookAndFeelSettingsState.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return "Look & Feel";
    }

    /**
     * Represents the Look And Feel settings UI component.
     *
     * @author Walied K. Yassen
     */
    private final class LookAndFeelSettingsUI extends JPanel {

        /**
         * The Look & Feel themes list component.
         */
        private final LookAndFeelList lookAndFeelList = new LookAndFeelList();

        /**
         * Constructs a new {@link LookAndFeelSettingsUI} type object instance.
         */
        public LookAndFeelSettingsUI() {
            super(new BorderLayout());
            add(createCenterComponent(), BorderLayout.CENTER);
            add(createThemeComponent(), BorderLayout.EAST);
            lookAndFeelList.addListSelectionListener(evt -> {
                temporaryState.setLookAndFeelName(lookAndFeelList.getSelectedValue().getClassName());
            });
        }

        /**
         * Creates a center component of the settings panel.
         *
         * @return the center component {@link JComponent} object.
         */
        private JComponent createCenterComponent() {
            JPanel panel = new JPanel();
            return panel;
        }

        /**
         * Creates the theme component of the settings panel.
         *
         * @return the theme component {@link JComponent} object.
         */
        private JComponent createThemeComponent() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JLabel("Look and Feels:"), BorderLayout.NORTH);
            panel.add(new JScrollPane(lookAndFeelList), BorderLayout.CENTER);
            return panel;
        }
    }

    /**
     * The Look and Feels list component.
     *
     * @author Walied K. Yassen
     */
    private static final class LookAndFeelList extends JList<UIManager.LookAndFeelInfo> {

        /**
         * Constructs a new {@link LookAndFeelList} type object instance.
         */
        public LookAndFeelList() {
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            DefaultListModel<UIManager.LookAndFeelInfo> model = new DefaultListModel<>();
            setModel(model);
            setCellRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    var info = (UIManager.LookAndFeelInfo) value;
                    var component = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    component.setText(info.getName());
                    return component;
                }
            });
            for (var info : FlatAllIJThemes.INFOS) {
                model.addElement(info);
            }
        }
    }

    /**
     * The settings state of the "Look & Feel" settings.
     *
     * @author Walied K. Yassen
     */
    @Data
    public static class LookAndFeelSettingsState implements SettingsState {

        /**
         * The Look and Feel class name.
         */
        @JsonProperty
        private String lookAndFeelName;

        /**
         * Whether or not the title bar is embed.
         */
        @JsonProperty
        private boolean embedTitleBar;

        /**
         * Constructs a new {@link LookAndFeelSettings} type object instance.
         */
        public LookAndFeelSettingsState() {
            lookAndFeelName = FlatDarkFlatIJTheme.class.getName();
            embedTitleBar = true;
        }
    }
}

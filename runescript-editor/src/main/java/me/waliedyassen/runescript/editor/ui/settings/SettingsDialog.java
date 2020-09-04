/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.formdev.flatlaf.util.UIScale;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import me.waliedyassen.runescript.editor.settings.Settings;
import me.waliedyassen.runescript.editor.settings.SettingsManager;
import me.waliedyassen.runescript.editor.util.JsonUtil;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * A dialog which contains a set of settings that can be modified by the user.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class SettingsDialog extends JDialog implements WindowListener {

    /**
     * The content pane of the settings dialog.
     */
    private final JPanel contentPane = new JPanel(new BorderLayout());

    /**
     * The model of the settings list component.
     */
    private final JTabbedPane tabbedPane = new JTabbedPane();

    /**
     * The OK button of the bottom panel.
     */
    private final JButton okButton = new JButton("OK");

    /**
     * The cancel button of the bottom panel.
     */
    private final JButton cancelButton = new JButton("Cancel");

    /**
     * The apply button of the bottom panel.
     */
    private final JButton applyButton = new JButton("Apply");

    /**
     * Constructs a new {@link SettingsDialog} type object instance.
     *
     * @param owner the owner frame of the dialog.
     */
    public SettingsDialog(JFrame owner) {
        super(owner, "Settings", true);
        setSize(UIScale.scale(800), UIScale.scale(600));
        setContentPane(contentPane);
        setLocationRelativeTo(owner);
        initContent();
        initState();
    }

    /**
     * Initialises the content of the dialog.
     */
    private void initContent() {
        var centerPanel = new JPanel(new MigLayout("insets dialog,fill"));
        {
            centerPanel.add(tabbedPane, "grow");
        }
        contentPane.add(centerPanel, BorderLayout.CENTER);
        var bottomPanel = new JPanel(new MigLayout("insets dialog", "[grow,fill][button,fill]", "[][bottom]"));
        {
            bottomPanel.add(new JSeparator(), "cell 0 0 2 1");
            bottomPanel.add(okButton, "cell 1 1");
            bottomPanel.add(cancelButton, "cell 1 1");
            bottomPanel.add(applyButton, "cell 1 1");
        }
        contentPane.add(bottomPanel, BorderLayout.SOUTH);
        // populate all of the settings.
        for (var settings : SettingsManager.collection()) {
            tabbedPane.addTab(settings.getTitle(), settings.createComponent());
        }
        okButton.addActionListener(evt -> onOk());
        cancelButton.addActionListener(evt -> onCancel());
        applyButton.addActionListener(evt -> onApply());
    }

    // TODO: Register listeners for the state UI components to call stateChange()

    /**
     * Gets fired when we press the OK button.
     */
    private void onOk() {
        onApply();
        dispose();
    }

    /**
     * Gets fired when we press the apply button.
     */
    private void onApply() {
        if (!isModified()) {
            return;
        }
        SettingsManager.stream().forEach(Settings::apply);
        SettingsManager.save();
    }

    /**
     * Gets fired when we press the cancel button.
     */
    private void onCancel() {
        dispose();
    }

    /**
     * Initializes the state of the settings.
     */
    @SuppressWarnings("unchecked")
    private void initState() {
        for (var settings : SettingsManager.collection()) {
            try {
                settings.setTemporaryState(JsonUtil.clone(settings.getPersistentState(), settings.getStateClass()));
            } catch (JsonProcessingException e) {
                log.error("Error while cloning a temporary state for settings: {}", settings.getName(), e);
            }
        }
        stateChanged();
    }

    /**
     * Gets fired when a settings property has changed.
     */
    private void stateChanged() {
        applyButton.setEnabled(isModified());
    }

    /**
     * Checks whether or not any of the settings was modified.
     *
     * @return <code>true</code> if any of the settings was modified otherwise <code>false</code>.
     */
    private boolean isModified() {
        return SettingsManager.collection().stream().anyMatch(Settings::isModified);
    }

    /**
     * Requests close of this settings dialog.
     */
    private void requestClose() {
        dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowOpened(WindowEvent e) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowClosing(WindowEvent e) {
        requestClose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowClosed(WindowEvent e) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowIconified(WindowEvent e) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowActivated(WindowEvent e) {

    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}

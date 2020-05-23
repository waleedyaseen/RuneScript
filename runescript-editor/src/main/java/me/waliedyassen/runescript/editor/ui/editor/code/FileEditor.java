/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor.code;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.waliedyassen.runescript.editor.file.FileType;
import me.waliedyassen.runescript.editor.ui.editor.Editor;
import me.waliedyassen.runescript.editor.util.MD5Util;
import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * Represents a plain file editor.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public class FileEditor extends Editor<Path> {

    /**
     * The main view component of the editor.
     */
    @Getter
    private final JComponent viewPane = new JPanel(new BorderLayout());

    /**
     * The text area which contains the file content.
     */
    @Getter
    protected final RSyntaxTextArea textArea = new RSyntaxTextArea();

    /**
     * The file type of the editor.
     */
    protected final FileType fileType;

    /**
     * The path which leads to the file we are editing.
     */
    protected final Path path;

    /**
     * The checksum of the editor content on the local disk.
     */
    private byte[] diskChecksum;

    /**
     * Constructs a new {@link FileType} type object instance.
     *
     * @param fileType the type of the file we are editing.
     * @param path     the path of the file we are editing.
     */
    public FileEditor(FileType fileType, Path path) {
        this.fileType = fileType;
        this.path = path;
        viewPane.add(new RTextScrollPane(textArea));
        viewPane.add(new ErrorStrip(textArea), BorderLayout.LINE_END);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reload() {
        try {
            var data = Files.readAllBytes(path);
            try (var reader = new InputStreamReader(new ByteArrayInputStream(data))) {
                textArea.read(reader, null);
            }
            diskChecksum = MD5Util.calculate(data);
        } catch (Throwable e) {
            log.error("An error occured while trying to save code editor content to disk", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save() {
        try {
            var content = textArea.getText().getBytes();
            Files.write(path, content, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            diskChecksum = MD5Util.calculate(content);
        } catch (Throwable e) {
            log.error("An error occured while trying to save code editor content to disk", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent getViewComponent() {
        return viewPane;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return path.getFileName().toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTooltip() {
        return path.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Icon getIcon() {
        return fileType.getIcon();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isModified() {
        return !Arrays.equals(MD5Util.calculate(textArea.getText().getBytes()), diskChecksum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Path getKey() {
        return path;
    }
}

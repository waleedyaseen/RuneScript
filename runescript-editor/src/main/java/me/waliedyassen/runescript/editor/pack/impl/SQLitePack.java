/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.pack.impl;

import lombok.SneakyThrows;
import me.waliedyassen.runescript.editor.pack.Pack;
import me.waliedyassen.runescript.editor.pack.PackFile;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Represents a {@link Pack} implementation that packs to a SQLite database.
 *
 * @author Walied K. Yassen
 */
public final class SQLitePack implements Pack {

    /**
     * THE SQL syntax for creating the table.
     */
    private static final String SQL_SYNTAX_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `content` (`ID`, `NAME` TEXT NOT NULL UNIQUE, `DATA` BLOB, PRIMARY KEY(`name`))";

    /**
     * The SQL syntax for creating the index.
     */
    private static final String SQL_SYNTAX_CREATE_INDEX = "CREATE UNIQUE INDEX IF NOT EXISTS `name_index` ON `content` (`name`)";

    /**
     * The SQL syntax for packing a file.
     */
    private static final String SQL_SYNTAX_PACK = "INSERT OR REPLACE INTO `content` (`name`, `data`) VALUES(?,?)";

    /**
     * The path which leads to the SQLite database.
     */
    private final String path;

    /**
     * The connection of the SQLite database.
     */
    private Connection connection;

    /**
     * Constructs a new {@link SQLitePack} type object instance.
     *
     * @param path the path which leads to the SQLite database path.
     */
    @SneakyThrows
    public SQLitePack(String path) {
        this.path = path;
        ensureConnectionAlive();
        try (var statement = connection.prepareStatement(String.format("%s;%s", SQL_SYNTAX_CREATE_TABLE, SQL_SYNTAX_CREATE_INDEX))) {
            statement.executeUpdate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SneakyThrows
    public void pack(PackFile file) {
        ensureConnectionAlive();
        try (var statement = connection.prepareStatement(SQL_SYNTAX_PACK)) {
            statement.setString(1, file.getName());
            statement.setBytes(2, file.getData());
            statement.executeUpdate();
        }
    }

    /**
     * Ensures the connection to the SQLite database is currently alive.
     */
    @SneakyThrows
    private void ensureConnectionAlive() {
        if (connection != null && !connection.isClosed()) {
            return;
        }
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
    }
}

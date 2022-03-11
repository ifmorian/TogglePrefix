package de.felix_kurz.toggleprefix.databases;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite {

    Connection conn;

    public void connect() {
        String url = "jdbc:sqlite:plugins/TogglePrefix/toggleprefix.db";
        try {
            conn = DriverManager.getConnection(url);
            Bukkit.getLogger().info("Connected to database");
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
    }

}

package de.felix_kurz.toggleprefix.databases;

import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQL {

    private String dbHost;
    private String dbPort;
    private String dbName;
    private String username;
    private String password;

    private Main plugin;

    private Connection conn;

    public MySQL(String dbHost, String dbPort, String dbName, String username, String password, Main plugin) {
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.username = username;
        this.password = password;
        this.plugin = plugin;
    }

    public void connect() {
        String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
        try {
            conn = DriverManager.getConnection(url, username, password);

            Bukkit.getLogger().info("Connected to database");
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return;
        }
        try {
            String sql = "CREATE TABLE IF NOT EXISTS prefixes(" +
                            "name varchar(64) PRIMARY KEY NOT NULL," +
                            "chat varchar(128)," +
                            "tablist varchar(128)," +
                            "item varchar(128)" +
                        ");";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.execute();

            Bukkit.getLogger().info("Setup table prefixes");
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        try {
            String sql = "CREATE TABLE IF NOT EXISTS players(" +
                            "id BINARY(16) PRIMARY KEY NOT NULL," +
                            "prefix varchar(64)" +
                        ");";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.execute();

            Bukkit.getLogger().info("Setup table players");
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
    }

    public void addPrefix(String name, String chat, String tablist, String item) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String sql = "INSERT INTO prefixes(name, chat, tablist, item) VALUES('?','?','?','?')";
                    PreparedStatement stmt = conn.prepareStatement(sql);

                    stmt.setString(1, name);
                    stmt.setString(2, chat);
                    stmt.setString(3, tablist);
                    stmt.setString(4, item);

                    stmt.execute();
                    syncConfig();
                } catch (SQLException e) {
                    Bukkit.getLogger().warning(e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void deletePrefix(String name) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String sql = "DELETE FROM prefixes WHERE name='?'";
                    PreparedStatement stmt = conn.prepareStatement(sql);

                    stmt.setString(1, name);

                    stmt.execute();
                    syncConfig();
                } catch (SQLException e) {
                    Bukkit.getLogger().warning(e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    private void syncConfig() {
        final ResultSet[] rs = {null};
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String sql = "SELECT * FROM prefixes";
                    PreparedStatement stmt = conn.prepareStatement(sql);

                    ResultSet rs = stmt.executeQuery();

                    List<String> prefixes = new ArrayList<>();
                    while(rs.next()) {
                        prefixes.add(rs.getString("name") + ";" + rs.getString("chat") + ";" +
                                rs.getString("tablist") + ";" + rs.getString("item"));
                    }
                    plugin.getCfgM().setPrefixes(prefixes);
                } catch (SQLException e) {
                    Bukkit.getLogger().info("Config.yml could not be updated.");
                    Bukkit.getLogger().warning(e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

}

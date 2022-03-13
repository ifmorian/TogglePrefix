package de.felix_kurz.toggleprefix.databases;

import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.UUID;

public class MySQL {

    private final String dbHost;
    private final String dbPort;
    private final String dbName;
    private final String username;
    private final String password;

    private final Main plugin;

    private Connection conn;

    private final ConsoleCommandSender c;

    public MySQL(String dbHost, String dbPort, String dbName, String username, String password, Main plugin) {
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.username = username;
        this.password = password;
        this.plugin = plugin;

        this.c = Bukkit.getConsoleSender();
    }

    public void connect() {
        String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
        try {
            conn = DriverManager.getConnection(url, username, password);

            c.sendMessage(Main.PRE + "Connected to database §6" + dbName);
        } catch (SQLException e) {
            c.sendMessage(Main.PRE + "Could not connect to database §6" + dbName);
            return;
        }
        try {
            String sql = "CREATE TABLE IF NOT EXISTS prefixes(" +
                            "name varchar(64) PRIMARY KEY NOT NULL," +
                            "chat varchar(128)," +
                            "tablist varchar(128)," +
                            "item varchar(128)," +
                            "priority varchar(3) NOT NULL" +
                        ");";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        try {
            String sql = "CREATE TABLE IF NOT EXISTS players(" +
                            "id BINARY(16) PRIMARY KEY NOT NULL," +
                            "rank varchar(64)," +
                            "prefix varchar(64)," +
                            "prefixes varchar(1024)" +
                        ");";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        try {
            String sql = "CREATE TABLE IF NOT EXISTS ranks(" +
                            "name varchar(64) PRIMARY KEY NOT NULL," +
                            "prefixes varchar(1024)," +
                            "priority int(3) NOT NULL" +
                        ");";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        try {
            String sql = "INSERT IGNORE INTO prefixes(name,chat,tablist,item,priority) " +
                    "VALUES('default','&7Player - &f%name% &7>>','&7Player | &f%name%','GREEN_STAINED_GLASS_PANE',0)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        try {
            String sql = "INSERT IGNORE INTO ranks(name,prefixes,priority) " +
                    "VALUES('default','default',0)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
    }

    public void disconnect() {
        if(conn != null) {
            try {
                conn.close();
                c.sendMessage(Main.PRE + "Disconnected from database §6" + dbName);
            } catch (SQLException e) {
                Bukkit.getLogger().warning(e.getMessage());
            }
        }
    }

    public void addPrefix(String name, String chat, String tablist, String item, String priority, Player p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String sql = "SELECT name FROM prefixes WHERE name=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);

                    stmt.setString(1, name);

                    if(stmt.executeQuery().next()) {
                        p.sendMessage(Main.PRE + "§cPrefix §6" + name + " §calready exists");
                        return;
                    }
                } catch (SQLException e) {
                    Bukkit.getLogger().warning(e.getMessage());
                    return;
                }
                try {
                    String sql = "INSERT INTO prefixes(name, chat, tablist, item, priority) VALUES(?,?,?,?,?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);

                    stmt.setString(1, name);
                    stmt.setString(2, chat);
                    stmt.setString(3, tablist);
                    stmt.setString(4, item);
                    stmt.setString(5, priority);

                    stmt.execute();
                    c.sendMessage(Main.PRE + "§aAdded prefix §6" + name);
                    p.sendMessage(Main.PRE + "§aAdded prefix §6" + name);
                } catch (SQLException e) {
                    Bukkit.getLogger().warning(e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void deletePrefix(String name, Player p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String sql = "SELECT name FROM prefixes WHERE name=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);

                    stmt.setString(1, name);

                    if(!stmt.executeQuery().next()) {
                        p.sendMessage(Main.PRE + "§cPrefix §6" + name + " §cdoes not exists");
                        return;
                    }
                } catch (SQLException e) {
                    Bukkit.getLogger().warning(e.getMessage());
                    return;
                }
                try {
                    String sql = "DELETE FROM prefixes WHERE name=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);

                    stmt.setString(1, name);

                    stmt.execute();
                    c.sendMessage(Main.PRE + "§aDeleted prefix §6" + name);
                    p.sendMessage(Main.PRE + "§aDeleted prefix §6" + name);
                } catch (SQLException e) {
                    Bukkit.getLogger().warning(e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void editPrefix(String name, String col, String value, Player p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String sql = "SELECT name FROM prefixes WHERE name=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);

                    stmt.setString(1, name);

                    if(!stmt.executeQuery().next()) {
                        p.sendMessage(Main.PRE + "§cPrefix §6" + name + " §cdoes not exists");
                        return;
                    }
                } catch (SQLException e) {
                    Bukkit.getLogger().warning(e.getMessage());
                    return;
                }
                try {
                    String sql = "UPDATE prefixes SET " + col + "=? WHERE name=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);

                    stmt.setString(1, value);
                    stmt.setString(2, name);

                    stmt.execute();

                    p.sendMessage(Main.PRE + "§aSet §b" + col + " §aof prefix §6" + name + " §ato §b" + value);
                    c.sendMessage(Main.PRE + "§aSet §b" + col + " §aof prefix §6" + name + " §ato §b" + value);
                } catch (SQLException e) {
                    Bukkit.getLogger().warning(e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public String getPlayerPrefix(Player p) {
        try {
            String sql = "SELECT prefix FROM players WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setBytes(1, UUIDtoByte(p.getUniqueId()));

            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getString("prefix");
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        return null;
    }

    public String getChatPrefix(Player p) {
        String prefix = getPlayerPrefix(p);
        try {
            String sql = "SELECT chat FROM prefixes WHERE name=?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, prefix);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getString("chat");
        } catch(SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        return null;
    }

    public void loadPlayer(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String sql = "SELECT id FROM players WHERE id=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);

                    stmt.setBytes(1, UUIDtoByte(player.getUniqueId()));

                    if(stmt.executeQuery().next()) {
                        return;
                    }
                } catch (SQLException e) {
                    Bukkit.getLogger().warning(e.getMessage());
                    return;
                }
                try {
                    String sql = "INSERT INTO players(id,rank,prefix) VALUES(?,'default','default')";
                    PreparedStatement stmt = conn.prepareStatement(sql);

                    stmt.setBytes(1, UUIDtoByte(player.getUniqueId()));

                    stmt.execute();
                } catch (SQLException e) {
                    Bukkit.getLogger().warning(e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public byte[] UUIDtoByte(UUID id) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(id.getMostSignificantBits());
        buffer.putLong(id.getLeastSignificantBits());
        return buffer.array();
    }

}

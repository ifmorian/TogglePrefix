package de.felix_kurz.toggleprefix.databases;

import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
                            "display varchar(64) NOT NULL," +
                            "chat varchar(128)," +
                            "tablist varchar(128)," +
                            "item varchar(128) NOT NULL," +
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
                            "rank varchar(64) NOT NULL," +
                            "prefix varchar(64) NOT NULL," +
                            "prefixes varchar(1024) NOT NULL" +
                        ");";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        try {
            String sql = "CREATE TABLE IF NOT EXISTS ranks(" +
                            "name varchar(64) PRIMARY KEY NOT NULL," +
                            "display varchar(64) NOT NULL," +
                            "prefixes varchar(1024) NOT NULL," +
                            "priority varchar(3) NOT NULL" +
                        ");";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        try {
            String sql = "INSERT IGNORE INTO prefixes(name,display,chat,tablist,item,priority) " +
                    "VALUES('default','Player','&7Player - &f%name% &7>>','&7Player | &f%name%','GREEN_STAINED_GLASS_PANE','100')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        try {
            String sql = "INSERT IGNORE INTO ranks(name,display,prefixes,priority) " +
                    "VALUES('default','Player','default','100')";
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

    public boolean exists(String table, String col, String value) {
        try {
            String sql = "SELECT " + col + " FROM " + table + " WHERE " + col + "=?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, value);

            return stmt.executeQuery().next();
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        return false;
    }

    public boolean playerExists(UUID id) {
        try {
            String sql = "SELECT id FROM players WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setBytes(1, UUIDtoByte(id));

            return stmt.executeQuery().next();
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        return false;
    }

    public boolean addPrefix(String name, String chat, String item, String priority) {
        try {
            String sql = "INSERT INTO prefixes(name,display,chat,tablist,item,priority) VALUES(?,?,?,?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, name);
            stmt.setString(2, name);
            stmt.setString(3, chat);
            stmt.setString(4, chat);
            stmt.setString(5, item);
            stmt.setString(6, priority);

            stmt.execute();

            c.sendMessage(Main.PRE + "§aAdded prefix §6" + name);
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return false;
        }
    }

    public boolean addRank(String name, String prefixes, String priority) {
        try {
            String sql = "INSERT INTO ranks(name,display,prefixes,priority) VALUES(?,?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, name);
            stmt.setString(2, name);
            stmt.setString(3, prefixes);
            stmt.setString(4, priority);

            stmt.execute();
            c.sendMessage(Main.PRE + "§aAdded rank §6" + name);
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return false;
        }
    }

    public boolean delete(String table, String key, String keyValue) {
        try {
            String sql = "DELETE FROM " + table + " WHERE " + key + "=?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, keyValue);

            stmt.execute();

            c.sendMessage(Main.PRE + "§aDeleted §6" + keyValue + " §afrom table §b" + table);
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return false;
        }
    }

    public boolean edit(String table, String key, String keyValue, String col, String value) {
        try {
            String sql = "UPDATE " + table + " SET " + col + "=? WHERE " + key + "=?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, value);
            stmt.setString(2, keyValue);

            stmt.execute();

            c.sendMessage(Main.PRE + "§aSet §3" + col + " §aof §6" + keyValue + " §ato §3" + value + " §ain table §b" + table);
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return false;
        }
    }

    public boolean editPlayer(UUID id, String col, String value) {
        try {
            String sql = "UPDATE players SET " + col +"=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, value);
            stmt.setBytes(2, UUIDtoByte(id));

            stmt.execute();

            c.sendMessage(Main.PRE + "§aSet §3" + col + " §aof Player with id §6" + id + " §ato §3" + value);
            if(col.equals("prefix")) {
                plugin.getSbM().updatePlayer(id);
            }
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return false;
        }
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

    public String loadPlayer(Player player) {
        try {
            String sql = "SELECT prefix FROM players WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setBytes(1, UUIDtoByte(player.getUniqueId()));

            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return rs.getString("prefix");
            }
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return null;
        }
        try {
            String sql = "INSERT INTO players(id,rank,prefix,prefixes) VALUES(?,'default','default','')";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setBytes(1, UUIDtoByte(player.getUniqueId()));

            stmt.execute();
            return "default";
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return null;
        }
    }

    public byte[] UUIDtoByte(UUID id) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(id.getMostSignificantBits());
        buffer.putLong(id.getLeastSignificantBits());
        return buffer.array();
    }

    public String getRankPrefixes(String name) {
        try {
            String sql = "SELECT prefixes FROM ranks WHERE name=?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, name);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getString("prefixes");
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return null;
        }
    }

    public String getPlayerPrefixes(UUID id) {
        try {
            String sql = "SELECT prefixes FROM players WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setBytes(1, UUIDtoByte(id));

            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getString("prefixes");
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return null;
        }
    }

    public String[] getTeams() {
        try {
            String sql = "SELECT name,priority FROM prefixes";
            PreparedStatement stmt = conn.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();
            List<String> teams = new ArrayList<>();
            while (rs.next()) {
                teams.add(rs.getString("priority") + rs.getString("name"));
            }
            return teams.toArray(new String[teams.size()]);
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return null;
        }
    }

    public String getTeam(Player p) {
        try {
            String sql = "SELECT name,priority FROM prefixes WHERE name=?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, getPlayerPrefix(p));

            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return rs.getString("priority") + rs.getString("name");
            }
            return null;
        } catch(SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return null;
        }
    }

}

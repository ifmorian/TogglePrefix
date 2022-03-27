package de.felix_kurz.toggleprefix.databases;

import de.felix_kurz.toggleprefix.items.PrefixItem;
import de.felix_kurz.toggleprefix.main.Main;
import de.felix_kurz.toggleprefix.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQL {

    private final String dbHost;
    private final String dbPort;
    private final String dbName;
    private final String username;
    private final String password;

    private final Main plugin;

    private Connection conn;

    private final ConsoleCommandSender c;

    public static final String playersTable = "tp_players";
    public static final String ranksTable = "tp_ranks";
    public static final String prefixesTable = "tp_prefixes";

    public MySQL(String dbHost, String dbPort, String dbName, String username, String password, Main plugin) {
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.username = username;
        this.password = password;
        this.plugin = plugin;

        this.c = Bukkit.getConsoleSender();
    }

    public Connection getConn() {
        return conn;
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
            String sql = "CREATE TABLE IF NOT EXISTS " + prefixesTable + "(" +
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
            return;
        }
        try {
            String sql = "CREATE TABLE IF NOT EXISTS " + playersTable + "(" +
                            "id BINARY(16) PRIMARY KEY NOT NULL," +
                            "rank varchar(64) NOT NULL," +
                            "prefix varchar(64) NOT NULL," +
                            "prefixes varchar(1024) NOT NULL" +
                        ");";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return;
        }
        try {
            String sql = "CREATE TABLE IF NOT EXISTS " + ranksTable + "(" +
                            "name varchar(64) PRIMARY KEY NOT NULL," +
                            "prefixes varchar(1024) NOT NULL" +
                        ");";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return;
        }
        try {
            String sql = "INSERT IGNORE INTO " + prefixesTable + "(name,display,chat,tablist,item,priority) " +
                    "VALUES('default','Player','&7Player - &f%name% &7>>','&7Player | &f%name%','GREEN_STAINED_GLASS_PANE','100')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return;
        }
        try {
            String sql = "INSERT IGNORE INTO " + ranksTable + "(name,prefixes) " +
                    "VALUES('default','default')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.execute();
        } catch (Exception e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
    }

    public void disconnect() {
        if(conn != null) {
            try {
                conn.close();
                c.sendMessage(Main.PRE + "Disconnected from database §6" + dbName);
            } catch (Exception e) {
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
        } catch (Exception e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        return false;
    }

    public boolean playerExists(Player p) {
        try {
            String sql = "SELECT id FROM " + playersTable + " WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setBytes(1, Utils.UUIDtoBytes(p.getUniqueId()));

            return stmt.executeQuery().next();
        } catch (Exception e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        return false;
    }

    public boolean addPrefix(String name, String chat, String item, String priority) {
        try {
            String sql = "INSERT INTO " + prefixesTable + "(name,display,chat,tablist,item,priority) VALUES(?,?,?,?,?,?)";
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
        } catch (Exception e) {
            Bukkit.getLogger().warning(e.getMessage());
            return false;
        }
    }

    public boolean addRank(String name, String prefixes) {
        try {
            String sql = "INSERT INTO " + ranksTable + "(name,prefixes) VALUES(?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, name);
            stmt.setString(2, prefixes);

            stmt.execute();
            c.sendMessage(Main.PRE + "§aAdded rank §6" + name);
            return true;
        } catch (Exception e) {
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
        } catch (Exception e) {
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

            if(table.equals(prefixesTable) && col.equals("tablist")) {
                plugin.getSbM().updatePlayers();
            }

            return true;
        } catch (Exception e) {
            Bukkit.getLogger().warning(e.getMessage());
            return false;
        }
    }

    public boolean editPlayer(OfflinePlayer p, String col, String value) {
        try {
            String sql = "UPDATE " + playersTable + " SET " + col +"=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, value);
            stmt.setBytes(2, Utils.UUIDtoBytes(p.getUniqueId()));

            stmt.execute();
            if(col.equals("prefix")) {
                if(p instanceof Player online) {
                    plugin.getSbM().updatePlayer(online);
                }
            }
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().warning(e.getMessage());
            return false;
        }
    }

    public String getFrom(String table, String key, String keyValue, String col) {
        try {
            String sql = "SELECT " + col + " FROM " + table + " WHERE " + key + "=?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, keyValue);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getString(col);
        } catch(Exception e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        return null;
    }

    public String getFromPlayer(OfflinePlayer p, String col) {
        try {
            String sql = "SELECT " + col + " FROM " + playersTable + " WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setBytes(1, Utils.UUIDtoBytes(p.getUniqueId()));

            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getString(col);
        } catch (Exception e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        return null;
    }

    public void loadPlayer(Player p) {
        if (playerExists(p)) return;
        try {
            String sql = "INSERT INTO " + playersTable + "(id,rank,prefix,prefixes) VALUES(?,'default','default','')";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setBytes(1, Utils.UUIDtoBytes(p.getUniqueId()));

            stmt.execute();
        } catch (Exception e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
    }

    public String[] getTeams() {
        try {
            String sql = "SELECT name,priority FROM " + prefixesTable;
            PreparedStatement stmt = conn.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();
            List<String> teams = new ArrayList<>();
            while (rs.next()) {
                teams.add(Utils.convertToLetters(rs.getString("priority")) + rs.getString("name"));
            }
            return teams.toArray(new String[0]);
        } catch (NullPointerException | SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return null;
        }
    }

    public String getTeam(Player p) {
        try {
            String sql = "SELECT name,priority FROM " + prefixesTable + " WHERE name=?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, getFromPlayer(p, "prefix"));

            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return Utils.convertToLetters(rs.getString("priority") + rs.getString("name"));
            }
        } catch(Exception e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        return null;
    }

    public PrefixItem getPrefix(String name) {
        try {
            String sql = "SELECT display,item FROM " + prefixesTable + " WHERE name=?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, name);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            return new PrefixItem(name, rs.getString("display"), Material.getMaterial(rs.getString("item")), false, plugin);
        } catch(Exception e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        return null;
    }

    public String[] getPlayers() {
        try {
            String sql = "SELECT id FROM " + playersTable;
            PreparedStatement stmt = conn.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();

            List<String> ids = new ArrayList<>();
            while(rs.next()) {
                ids.add(Bukkit.getOfflinePlayer(Utils.BytestoUUID(rs.getBytes("id"))).getName());
            }
            return ids.toArray(new String[0]);
        } catch(Exception e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        return null;
    }

    public String getPrefixes() {
        try {
            String sql = "SELECT name FROM " + prefixesTable;
            PreparedStatement stmt = conn.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();

            StringBuilder prefixes = new StringBuilder();
            while(rs.next()) {
                prefixes.append(rs.getString("name")).append(",");
            }
            return prefixes.deleteCharAt(prefixes.length() - 1).toString();
        } catch(Exception e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        return null;
    }

    public String[] getRanks() {
        try {
            String sql = "SELECT name FROM " + ranksTable;
            PreparedStatement stmt = conn.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();

            List<String> ranks = new ArrayList<>();
            while(rs.next()) {
                ranks.add(rs.getString("name"));
            }
            return ranks.toArray(new String[0]);
        } catch(Exception e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        return new String[0];
    }

}

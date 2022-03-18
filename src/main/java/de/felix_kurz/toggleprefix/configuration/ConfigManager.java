package de.felix_kurz.toggleprefix.configuration;

import de.felix_kurz.toggleprefix.databases.MySQL;
import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final Main plugin;
    private FileConfiguration cfg;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        cfg = plugin.getConfig();

        List<String> configHeader = new ArrayList<>();
        configHeader.add("TogglePrefix by Felix Kurz");
        configHeader.add("Version 1.0");
        configHeader.add("");
        configHeader.add("You may need to restart or reload the server for changes to take effect.");
        cfg.options().setHeader(configHeader);

        if (cfg.get("useColorTranslate") == null) cfg.set("useColorTranslate", true);
        if (cfg.get("autoUpdate") == null) cfg.set("autoUpdate", true);
        if (cfg.get("database") == null) {
            cfg.set("database.dbHost", "localhost");
            cfg.set("database.dbPort", "3306");
            cfg.set("database.dbName", "toggleprefix");
            cfg.set("database.username", "root");
            cfg.set("database.dbHost", "");
        }
        if (cfg.get("tablistHeader") == null) {
            List<String> defaultHeader = new ArrayList<>();
            defaultHeader.add("&bTogglePrefix\n&5Eine random Message");
            defaultHeader.add("&eTogglePrefix\n&cKeinen Plan, was hier stehen soll");
            defaultHeader.add("&aTogglePrefix\n&3Wirklich absolute keine Ahnung");
            cfg.set("tablistHeader", defaultHeader);
        }
        if (cfg.get("tablistFooter") == null) {
            List<String> defaultFooter = new ArrayList<>();
            defaultFooter.add("&bSpieler Online: &3%onlinePlayers%/%maxPlayers%");
            defaultFooter.add("&cSpieler Online: &4%onlinePlayers%/%maxPlayers%");
            defaultFooter.add("&aSpieler Online: &2%onlinePlayers%/%maxPlayers%");
            cfg.set("tablistFooter", defaultFooter);
        }
        save();
    }

    public MySQL loadDatabase() {
        String dbHost = cfg.getString("database.dbHost");
        String dbPort = cfg.getString("database.dbPort");
        String dbName = cfg.getString("database.dbName");
        String username = cfg.getString("database.username");
        String password = cfg.getString("database.password");

        return new MySQL(dbHost, dbPort, dbName, username, password, plugin);
    }

    public List<String> getTablistHeader() {
        return cfg.getStringList("tablistHeader");
    }

    public List<String> getTablistFooter() {
        return cfg.getStringList("tablistFooter");
    }

    public boolean useColorTranslate() {
        return cfg.getBoolean("useColorTranslate");
    }

    public void save() {
        plugin.saveConfig();
    }

    public boolean autoUpdate() {
        return cfg.getBoolean("autoUpdate");
    }
}

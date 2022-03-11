package de.felix_kurz.toggleprefix.configuration;

import de.felix_kurz.toggleprefix.databases.MySQL;
import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private Main plugin;
    private FileConfiguration cfg;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        cfg = plugin.getConfig();

        List<String> configHeader = new ArrayList<>();
        configHeader.add("TogglePrefix by Felix Kurz");
        configHeader.add("Version 1.0");
        cfg.options().setHeader(configHeader);

        List<String> configFooter = new ArrayList<>();
        configFooter.add("It is possible to handle permissions to the default group but it is highly not recommended. Every user without a LuckPerm group would be able to edit the prefix of the given groups.");
        configFooter.add("Version 1.0");

        if(cfg.get("useChatFormat") == null) cfg.set("useChatFormat", true);
        if(cfg.get("useColorTranslate") == null) cfg.set("useColorTranslate", true);
        if(cfg.get("chatFormat") == null) cfg.set("chatFormat", "%chatPrefix%%playerName% &8>> &7%message%");
        if(cfg.get("autoUpdate") == null) cfg.set("autoUpdate", true);
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

    public FileConfiguration getConfig() {
        return cfg;
    }

    public void save() {
        plugin.saveConfig();
    }

    public void setPrefixes(List<String> prefixes) {
        cfg.set("prefixes", prefixes);
        save();
    }
}

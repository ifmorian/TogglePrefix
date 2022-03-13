package de.felix_kurz.toggleprefix.main;

import de.felix_kurz.toggleprefix.commands.TogglePrefixCommand;
import de.felix_kurz.toggleprefix.configuration.ConfigManager;
import de.felix_kurz.toggleprefix.databases.MySQL;
import de.felix_kurz.toggleprefix.listeners.ChatListener;
import de.felix_kurz.toggleprefix.listeners.JoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static final String PRE = "§7[§dTogglePrefix§7] ";
    private MySQL mysql;
    private ConfigManager cfgM;

    public void onEnable() {
        cfgM = new ConfigManager(this);
        cfgM.loadConfig();

        mysql = cfgM.loadDatabase();
        mysql.connect();

        getCommand("toggleprefix").setExecutor(new TogglePrefixCommand(this));

        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
    }

    public void onDisable() {
        mysql.disconnect();
    }

    public ConfigManager getCfgM() {
        return cfgM;
    }

    public MySQL getMysql() {
        return mysql;
    }
}

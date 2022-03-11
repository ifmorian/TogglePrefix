package de.felix_kurz.toggleprefix.main;

import de.felix_kurz.toggleprefix.commands.ToggleCommand;
import de.felix_kurz.toggleprefix.configuration.ConfigManager;
import de.felix_kurz.toggleprefix.databases.SQLite;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static final String PRE = "§f[§9TogglePrefix§f] ";
    private SQLite sqlite;
    private ConfigManager cfgM;

    public void onEnable() {
        cfgM = new ConfigManager(this);
        cfgM.loadConfig();

        sqlite = new SQLite();
        sqlite.connect();

        getCommand("toggle").setExecutor(new ToggleCommand(this.cfgM));
    }

    public SQLite getSqlite() {
        return sqlite;
    }

    public ConfigManager getCfgM() {
        return cfgM;
    }
}

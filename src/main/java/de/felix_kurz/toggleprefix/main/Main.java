package de.felix_kurz.toggleprefix.main;

import de.felix_kurz.toggleprefix.commands.TogglePrefixCommand;
import de.felix_kurz.toggleprefix.configuration.ConfigManager;
import de.felix_kurz.toggleprefix.databases.MySQL;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static final String PRE = "§f[§9TogglePrefix§f] ";
    private MySQL mysql;
    private ConfigManager cfgM;

    public void onEnable() {
        cfgM = new ConfigManager(this);
        cfgM.loadConfig();

        mysql = cfgM.loadDatabase();
        mysql.connect();

        getCommand("toggleprefix").setExecutor(new TogglePrefixCommand(this.cfgM));
    }

    public ConfigManager getCfgM() {
        return cfgM;
    }
}

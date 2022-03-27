package de.felix_kurz.toggleprefix.main;

import de.felix_kurz.toggleprefix.commands.TogglePrefixCommand;
import de.felix_kurz.toggleprefix.commands.TogglePrefixTab;
import de.felix_kurz.toggleprefix.configuration.ConfigManager;
import de.felix_kurz.toggleprefix.databases.MySQL;
import de.felix_kurz.toggleprefix.inventories.PlayerInventory;
import de.felix_kurz.toggleprefix.listeners.ChatListener;
import de.felix_kurz.toggleprefix.listeners.InventoryClickListener;
import de.felix_kurz.toggleprefix.listeners.JoinListener;
import de.felix_kurz.toggleprefix.scoreboards.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin {

    public static final String PRE = "§7[§dTogglePrefix§7] ";
    private MySQL mysql;
    private ConfigManager cfgM;
    private ScoreboardManager sbM;

    public void onEnable() {
        cfgM = new ConfigManager(this);
        cfgM.loadConfig();

        mysql = cfgM.loadDatabase();
        mysql.connect();

        sbM = new ScoreboardManager(this, Bukkit.getScoreboardManager().getMainScoreboard());
        sbM.update();
        sbM.animateTabs();

        getCommand("toggleprefix").setExecutor(new TogglePrefixCommand(this));
        getCommand("toggleprefix").setTabCompleter(new TogglePrefixTab());

        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(this), this);
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

    public ScoreboardManager getSbM() {
        return sbM;
    }
}

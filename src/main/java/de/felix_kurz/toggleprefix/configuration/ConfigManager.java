package de.felix_kurz.toggleprefix.configuration;

import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private Main plugin;
    private FileConfiguration cfg;

    public void loadConfig() {
        cfg = plugin.getConfig();


    }

}

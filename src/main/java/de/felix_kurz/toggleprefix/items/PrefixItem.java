package de.felix_kurz.toggleprefix.items;

import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

public class PrefixItem extends InventoryItem {

    public String name;

    public PrefixItem(String name, String title, Material type, boolean enchanted, Main plugin) {
        super(title, type, enchanted, plugin);
        this.name = name;
    }

    public void setPrefix(OfflinePlayer p) {
        plugin.getMysql().editPlayer(p, "prefix", name);
    }

}

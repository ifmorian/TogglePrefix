package de.felix_kurz.toggleprefix.items;

import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlayerSkullItem extends SkullItem {

    public PlayerSkullItem(String ownerName, String title, boolean enchanted, Main plugin) {
        super(ownerName, title, enchanted, plugin);
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(ownerName);
    }

}

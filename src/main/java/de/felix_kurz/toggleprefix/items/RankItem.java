package de.felix_kurz.toggleprefix.items;

import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

public class RankItem extends InventoryItem {

    public RankItem(String title, boolean enchanted, Main plugin) {
        super(title, Material.EMERALD, enchanted, plugin);
    }

    public void setRank(OfflinePlayer p) {
        plugin.getMysql().editPlayer(p, "rank", title);
    }

}

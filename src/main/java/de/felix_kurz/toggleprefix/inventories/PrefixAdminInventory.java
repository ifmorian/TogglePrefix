package de.felix_kurz.toggleprefix.inventories;

import de.felix_kurz.toggleprefix.items.InventoryItem;
import de.felix_kurz.toggleprefix.items.PlayerSkullItem;
import de.felix_kurz.toggleprefix.items.SkullItem;
import de.felix_kurz.toggleprefix.main.Main;
import de.felix_kurz.toggleprefix.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PrefixAdminInventory extends PlayerInventory {

    private Collection<? extends Player> onlinePlayers;
    private String[] players;
    public int page;

    public static final String TITLE_PREFIX = "§dPräfixe verwalten";
    public static final String TITLE_RANK = "§dRänge verwalten";

    public PrefixAdminInventory(Main plugin, Player p, boolean prefix) {
        super(plugin, p, prefix ? TITLE_PREFIX : TITLE_RANK, 6 * 9);
        page = 0;
        openInventories.put(p.getUniqueId(), this);
    }

    @Override
    public void setupItems() {
        List<String> lore = new ArrayList<>();
        lore.add("§a§oSeite " + (page + 1));
        InventoryItem glassPane = new InventoryItem(title, lore, Material.GLASS_PANE, true, plugin);
        for (int i = 0; i < 9; i++) {
            items[i] = glassPane;
            items[i + 45] = glassPane;
        }
        for (int i = 1; i < 5; i++) {
            items[i * 9] = glassPane;
            items[i * 9 + 8] = glassPane;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                items[47] = new SkullItem("MHF_ArrowLeft", "§a§oVorherige Seite", false, plugin);
                items[51] = new SkullItem("MHF_ArrowRight", "§a§oNächste Seite", false, plugin);
                onlinePlayers = Bukkit.getOnlinePlayers();
                players = mySQL.getPlayers();
                if(players == null) {
                    Utils.error(p);
                    return;
                }
                Arrays.sort(players, String.CASE_INSENSITIVE_ORDER);
                int a = page * 28;
                for (int i = 1; i < 5; i++) {
                    for (int j = i * 9 + 1; j < i * 9 + 8; j++) {
                        if (players.length > a) {
                            OfflinePlayer player = Bukkit.getOfflinePlayer(players[a]);
                            items[j] = new PlayerSkullItem(players[a], players[a], onlinePlayers.contains(player), plugin);
                            a++;
                        } else
                            items[j] = null;
                    }
                }
                renderPage();
            }
        }.runTaskAsynchronously(plugin);
    }
    public void setPage(int page) {
        if(players.length > 28 * page && page >= 0) {
            this.page = page;
            setupItems();
        }
    }


}

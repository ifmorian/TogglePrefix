package de.felix_kurz.toggleprefix.inventories;

import de.felix_kurz.toggleprefix.databases.MySQL;
import de.felix_kurz.toggleprefix.items.InventoryItem;
import de.felix_kurz.toggleprefix.items.RankItem;
import de.felix_kurz.toggleprefix.items.SkullItem;
import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetrankInventory extends PlayerInventory{

    private String[] ranks;
    public int page;
    private OfflinePlayer changedPlayer;

    public SetrankInventory(Main plugin, Player p, OfflinePlayer changedPlayer) {
        super(plugin, p, "§dRang von §b" + changedPlayer.getName() + " §dändern", 5 * 9);
        ranks = mySQL.getRanks();
        Arrays.sort(ranks, String.CASE_INSENSITIVE_ORDER);
        this.changedPlayer = changedPlayer;
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
            items[i + 36] = glassPane;
        }
        for (int i = 1; i < 4; i++) {
            items[i * 9] = glassPane;
            items[i * 9 + 8] = glassPane;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                String rank = mySQL.getFromPlayer(changedPlayer, "rank");
                items[4] = new RankItem(rank, false, plugin);
                items[38] = new SkullItem("MHF_ArrowLeft", "§a§oVorherige Seite", false, plugin);
                items[42] = new SkullItem("MHF_ArrowRight", "§a§oNächste Seite", false, plugin);
                int a = page * 21;
                for (int i = 1; i < 4; i++) {
                    for (int j = i * 9 + 1; j < i * 9 + 8; j++) {
                        if (ranks.length > a) {
                            String title = mySQL.getFrom(MySQL.ranksTable, "name", ranks[a], "name");
                            items[j] = new RankItem(title, title.equals(rank), plugin);
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
        if(ranks.length > 21 * page && page >= 0) {
            this.page = page;
            setupItems();
        }
    }

}

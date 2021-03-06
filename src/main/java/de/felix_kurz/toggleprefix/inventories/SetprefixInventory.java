package de.felix_kurz.toggleprefix.inventories;

import de.felix_kurz.toggleprefix.databases.MySQL;
import de.felix_kurz.toggleprefix.items.InventoryItem;
import de.felix_kurz.toggleprefix.items.PrefixItem;
import de.felix_kurz.toggleprefix.items.SkullItem;
import de.felix_kurz.toggleprefix.main.Main;
import de.felix_kurz.toggleprefix.utils.Utils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetprefixInventory extends PlayerInventory {

    public static final String TITLE = "§dPräfix ändern";
    private String[] prefixes = new String[0];
    public int page;
    private boolean admin;
    private OfflinePlayer changedPlayer;

    public SetprefixInventory(Main plugin, Player p, OfflinePlayer changedPlayer, boolean admin) {
        super(plugin, p,admin ? "§dPräfix von §b" + changedPlayer.getName() + " §dändern" : TITLE, 5 * 9);
        this.admin = admin;
        String rank = mySQL.getFromPlayer(p, "rank");
        String pres = admin ? mySQL.getPrefixes() : Utils.joinPrefixes(mySQL.getFromPlayer(p, "prefixes"), plugin.getMysql().getFrom(MySQL.ranksTable, "name", rank, "prefixes"));
        if(pres != null) if(!pres.equals("")) {
            this.prefixes = pres.split(",");
            Arrays.sort(prefixes, String.CASE_INSENSITIVE_ORDER);
        }
        page = 0;
        this.changedPlayer = changedPlayer;
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
                String prefix = mySQL.getFromPlayer(changedPlayer, "prefix");
                items[4] = plugin.getMysql().getPrefix(prefix);
                items[38] = new SkullItem("MHF_ArrowLeft", "§a§oVorherige Seite", false, plugin);
                items[42] = new SkullItem("MHF_ArrowRight", "§a§oNächste Seite", false, plugin);
                int a = page * 21;
                for (int i = 1; i < 4; i++) {
                    for (int j = i * 9 + 1; j < i * 9 + 8; j++) {
                        if (prefixes.length > a) {
                            items[j] = mySQL.getPrefix(prefixes[a]);
                            if(((PrefixItem) items[j]).name.equals(prefix)) items[j].enchanted = true;
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
        if(prefixes.length > 21 * page && page >= 0) {
            this.page = page;
            setupItems();
        }
    }

}

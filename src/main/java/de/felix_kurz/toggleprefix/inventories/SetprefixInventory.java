package de.felix_kurz.toggleprefix.inventories;

import de.felix_kurz.toggleprefix.items.InventoryItem;
import de.felix_kurz.toggleprefix.items.PrefixItem;
import de.felix_kurz.toggleprefix.items.SkullItem;
import de.felix_kurz.toggleprefix.main.Main;
import de.felix_kurz.toggleprefix.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class SetprefixInventory extends PlayerInventory {

    public static final String TITLE = "§dPräfix ändern";
    private String[] prefixes = new String[0];
    public int page;
    private String prefix;

    public SetprefixInventory(Main plugin, Player p) {
        super(plugin, p,"§dPräfix ändern", 5 * 9);
        String rank = mySQL.getFromPlayer(p, "rank");
        String pres = Utils.joinPrefixes(mySQL.getFromPlayer(p, "prefixes"), plugin.getMysql().getFrom("ranks", "name", rank, "prefixes"));
        if(pres != null) if(!pres.equals("")) this.prefixes = pres.split(",");
        page = 0;
        inventory = Bukkit.createInventory(p, size, TITLE);
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
                prefix = mySQL.getFromPlayer(p, "prefix");
                items[4] = plugin.getMysql().getPrefix(prefix);
                items[38] = new SkullItem("MHF_ArrowLeft", "§a§oVorherige Seite", false, plugin);
                items[42] = new SkullItem("MHF_ArrowRight", "§a§oNächste Seite", false, plugin);
                int a = page * 21;
                for (int i = 1; i < 4; i++) {
                    for (int j = i * 9 + 1; j < i * 9 + 8; j++) {
                        if (prefixes.length > a) {
                            items[j] = plugin.getMysql().getPrefix(prefixes[a]);
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

    public void open() {
        setupItems();
        p.openInventory(inventory);
    }

    public void setPage(int page) {
        if(prefixes.length > 21 * page && page >= 0) {
            this.page = page;
            setupItems();
        }
    }

}

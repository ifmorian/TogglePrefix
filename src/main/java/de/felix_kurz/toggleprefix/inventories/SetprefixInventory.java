package de.felix_kurz.toggleprefix.inventories;

import de.felix_kurz.toggleprefix.items.InventoryItem;
import de.felix_kurz.toggleprefix.items.PrefixItem;
import de.felix_kurz.toggleprefix.items.SkullItem;
import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class SetprefixInventory extends PlayerInventory {

    public static final String TITLE = "§dPräfix ändern";
    private String[] prefixes = new String[0];
    public int page;
    private PrefixItem prefix;

    public SetprefixInventory(Main plugin, Player p) {
        super(plugin, p,"§dPräfix ändern", 5 * 9);
        String rank = mySQL.getFromPlayer(p, "rank");
        String pres = mySQL.getFromPlayer(p, "prefixes") + plugin.getMysql().getFrom("ranks", "name", rank, "prefixes");
        if(pres != null) if(!pres.equals("")) this.prefixes = pres.split(",");
        page = 0;
        inventory = Bukkit.createInventory(p, size, TITLE);
        openInventories.put(p.getUniqueId(), this);
    }

    @Override
    public void setupItems() {
        InventoryItem glassPane = new InventoryItem(page + "", Material.GLASS_PANE, true);
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
                items[4] = plugin.getMysql().getPrefix(mySQL.getFromPlayer(p, "prefix"));
                items[38] = new SkullItem("MHF_ArrowLeft", "§a§oVorherige Seite", false);
                items[42] = new SkullItem("MHF_ArrowRight", "§a§oNächste Seite", false);
            }
        }.runTaskAsynchronously(plugin);
        setupPage();
    }

    public void setupPage() {
        new BukkitRunnable() {
            @Override
            public void run() {
                int a = page * 21;
                for (int i = 1; i < 4; i++) {
                    for (int j = i * 9 + 1; j < i * 9 + 8; j++) {
                        if (prefixes.length > a) {
                            items[j] = plugin.getMysql().getPrefix(prefixes[a]);
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
            setupPage();
            renderPage();
        }
    }
}

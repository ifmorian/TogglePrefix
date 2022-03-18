package de.felix_kurz.toggleprefix.inventories;

import de.felix_kurz.toggleprefix.utils.Prefix;
import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class SetprefixInventory extends PlayerInventory {

    public static final String TITLE = "§dPräfix ändern";
    private String[] prefixes = new String[0];
    public int page;
    private Prefix prefix;

    public SetprefixInventory(Main plugin, Player p) {
        super(plugin, p,"§dPräfix ändern", 5 * 9);
        String rank = mySQL.getFromPlayer(p, "rank");
        String pres = mySQL.getFromPlayer(p, "prefixes") + plugin.getMysql().getFrom("ranks", "name", rank, "prefixes");
        if(pres != null) if(!pres.equals("")) this.prefixes = pres.split(",");
        page = 0;
        inventory = Bukkit.createInventory(p, size, TITLE);
        openInventories.put(p.getUniqueId(), this);
    }

    public void setup() {
        ItemStack glass = getTitledItem(Material.GLASS_PANE, TITLE, true);
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, glass);
            inventory.setItem(i + 36, glass);
        }
        for (int i = 1; i < 4; i++) {
            inventory.setItem(i * 9, glass);
            inventory.setItem(i * 9 + 8, glass);
        }
        inventory.setItem(38, new ItemStack(Material.ARROW));
        inventory.setItem(42, new ItemStack(Material.ARROW));

        p.openInventory(inventory);
        new BukkitRunnable() {
            @Override
            public void run() {
                prefix = plugin.getMysql().getPrefix(mySQL.getFromPlayer(p, "prefix"));
                inventory.setItem(4, getTitledItem(prefix.item, prefix.display, false));
                inventory.setItem(38, getPlayerHead("MHF_ArrowLeft", "Nächste Seite", false));
                inventory.setItem(42, getPlayerHead("MHF_ArrowRight", "Vorherige Seite", false));
                renderPage();
            }
        }.runTaskAsynchronously(plugin);
    }

    public void renderPage() {
        int a = page * 21;
        for (int i = 1; i < 4; i++) {
            for (int j = i * 9 + 1; j < i * 9 + 8; j++) {
                if (prefixes.length <= a) {
                    inventory.clear(j);
                } else {
                    Prefix prefix = plugin.getMysql().getPrefix(prefixes[a]);
                    inventory.setItem(j, getTitledItem(prefix.item, prefix.display, prefix.name.equals(this.prefix.name)));
                    a++;
                }
            }
        }
    }

    public void setPage(int page) {
        if(prefixes.length > 21 * page && page >= 0) {
            this.page = page;
            renderPage();
        }
    }

}

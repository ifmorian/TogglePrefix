package de.felix_kurz.toggleprefix.inventories;

import de.felix_kurz.toggleprefix.databases.MySQL;
import de.felix_kurz.toggleprefix.items.InventoryItem;
import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class PlayerInventory {

    public Main plugin;
    public MySQL mySQL;

    public Inventory inventory;
    public Player p;
    public String title;
    public int size;

    public InventoryItem[] items;

    public static Map<UUID, PlayerInventory> openInventories = new HashMap<>();

    public PlayerInventory(Main plugin, Player p, String title, int size) {
        this.plugin = plugin;
        this.p = p;
        this.title = title;
        this.size = size;
        mySQL = plugin.getMysql();
        this.items =  new InventoryItem[size];
        inventory = Bukkit.createInventory(p, size, title);
    }

    public void open() {
        setupItems();
        p.openInventory(inventory);
    }

    public abstract void setupItems();

    public void renderPage() {
        for (int i = 0; i < size; i++) {
            if(items[i] != null) inventory.setItem(i, items[i].getItem());
            else inventory.clear(i);
        }
    }

}

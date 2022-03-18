package de.felix_kurz.toggleprefix.inventories;

import de.felix_kurz.toggleprefix.databases.MySQL;
import de.felix_kurz.toggleprefix.main.Main;
import de.felix_kurz.toggleprefix.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerInventory {

    public Main plugin;
    public MySQL mySQL;

    public Inventory inventory;
    public Player p;
    public String title;
    public int size;

    public static Map<UUID, SetprefixInventory> openInventories = new HashMap<>();

    public PlayerInventory(Main plugin, Player p, String title, int size) {
        this.plugin = plugin;
        this.p = p;
        this.title = title;
        this.size = size;
        mySQL = plugin.getMysql();
    }

    public PlayerInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public ItemStack getPlayerHead(String name, String title, boolean enchanted) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(name));
        meta.setDisplayName(Utils.colorTranslate(title));
        if(enchanted) meta.addEnchant(Enchantment.LURE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getTitledItem(Material material, String title, boolean enchanted) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Utils.colorTranslate(title));
        if(enchanted) meta.addEnchant(Enchantment.LURE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

}

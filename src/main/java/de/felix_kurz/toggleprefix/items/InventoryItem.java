package de.felix_kurz.toggleprefix.items;

import de.felix_kurz.toggleprefix.main.Main;
import de.felix_kurz.toggleprefix.utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class InventoryItem {

    public final Main plugin;

    public String title;
    public Material type;
    public boolean enchanted;
    public List<String> lore;

    public InventoryItem(String title, List<String> lore, Material type, boolean enchanted, Main plugin) {
        this.title = title;
        this.type = type;
        this.enchanted = enchanted;
        this.lore = lore;
        this.plugin = plugin;
    }

    public InventoryItem(String title, Material type, boolean enchanted, Main plugin) {
        this.title = title;
        this.type = type;
        this.enchanted = enchanted;
        this.plugin = plugin;
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Utils.colorTranslate(title));
        if (enchanted) meta.addEnchant(Enchantment.LURE, 1, true);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

}

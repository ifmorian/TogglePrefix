package de.felix_kurz.toggleprefix.items;

import de.felix_kurz.toggleprefix.utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryItem {

    public String title;
    public Material type;
    public boolean enchanted;

    public InventoryItem(String title, Material type, boolean enchanted) {
        this.title = title;
        this.type = type;
        this.enchanted = enchanted;
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Utils.colorTranslate(title));
        if (enchanted) meta.addEnchant(Enchantment.LURE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

}

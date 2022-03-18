package de.felix_kurz.toggleprefix.items;

import de.felix_kurz.toggleprefix.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullItem extends InventoryItem {

    private String ownerName;

    public SkullItem(String ownerName, String title, Material type, boolean enchanted) {
        super(title, type, enchanted);
        this.ownerName = ownerName;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(ownerName));
        meta.setDisplayName(Utils.colorTranslate(title));
        if(enchanted) meta.addEnchant(Enchantment.LURE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

}

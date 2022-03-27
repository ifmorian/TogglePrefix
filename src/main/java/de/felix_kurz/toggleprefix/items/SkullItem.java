package de.felix_kurz.toggleprefix.items;

import de.felix_kurz.toggleprefix.main.Main;
import de.felix_kurz.toggleprefix.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class SkullItem extends InventoryItem {

    public String ownerName;

    public SkullItem(String ownerName, String title, boolean enchanted, Main plugin) {
        super(title, Material.PLAYER_HEAD, enchanted, plugin);
        this.ownerName = ownerName;
    }

    public SkullItem(UUID ownerID, String title, boolean enchanted, Main plugin) {
        super(title, Material.PLAYER_HEAD, enchanted, plugin);
        this.ownerName = Bukkit.getOfflinePlayer(ownerID).getName();
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(ownerName));
        meta.setDisplayName(Utils.colorTranslate(title));
        if(enchanted) meta.addEnchant(Enchantment.LURE, 1, true);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

}

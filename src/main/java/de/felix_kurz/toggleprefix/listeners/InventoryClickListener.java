package de.felix_kurz.toggleprefix.listeners;

import de.felix_kurz.toggleprefix.inventories.PlayerInventory;
import de.felix_kurz.toggleprefix.inventories.SetprefixInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(SetprefixInventory.TITLE)) {
            SetprefixInventory inventory = PlayerInventory.openInventories.get(event.getWhoClicked().getUniqueId());
            if (event.getSlot() == 38) inventory.setPage(inventory.page - 1);
            else if (event.getSlot() == 42) inventory.setPage(inventory.page + 1);
            event.setCancelled(true);
        }
    }

}

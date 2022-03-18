package de.felix_kurz.toggleprefix.listeners;

import de.felix_kurz.toggleprefix.inventories.PlayerInventory;
import de.felix_kurz.toggleprefix.inventories.SetprefixInventory;
import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

public record InventoryClickListener(Main plugin) implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(SetprefixInventory.TITLE)) {
            event.setCancelled(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    SetprefixInventory inventory = PlayerInventory.openInventories.get(event.getWhoClicked().getUniqueId());
                    if (event.getSlot() == 38) {
                        inventory.setPage(inventory.page - 1);
                        return;
                    } else if (event.getSlot() == 42) {
                        inventory.setPage(inventory.page + 1);
                        return;
                    }
                }
            }.runTaskAsynchronously(plugin);
        }
    }

}

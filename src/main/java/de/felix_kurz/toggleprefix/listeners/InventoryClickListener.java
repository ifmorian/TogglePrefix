package de.felix_kurz.toggleprefix.listeners;

import de.felix_kurz.toggleprefix.inventories.PlayerInventory;
import de.felix_kurz.toggleprefix.inventories.PrefixAdminInventory;
import de.felix_kurz.toggleprefix.inventories.SetprefixInventory;
import de.felix_kurz.toggleprefix.inventories.SetrankInventory;
import de.felix_kurz.toggleprefix.items.PlayerSkullItem;
import de.felix_kurz.toggleprefix.items.PrefixItem;
import de.felix_kurz.toggleprefix.items.RankItem;
import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

public record InventoryClickListener(Main plugin) implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (event.getSlot() == -999) return;
        if (title.equals(SetprefixInventory.TITLE)) {
            event.setCancelled(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    OfflinePlayer p = (OfflinePlayer) event.getWhoClicked();
                    handlePrefixInventory(p, p, event.getSlot());
                }
            }.runTaskAsynchronously(plugin);
        } else if (title.equals(PrefixAdminInventory.TITLE_PREFIX)) {
            event.setCancelled(true);
            Player p = (Player) event.getWhoClicked();
            PrefixAdminInventory inventory = (PrefixAdminInventory) PlayerInventory.openInventories.get(p.getUniqueId());
            if (event.getSlot() == 47) {
                inventory.setPage(inventory.page - 1);
                return;
            } else if (event.getSlot() == 51) {
                inventory.setPage(inventory.page + 1);
                return;
            }
            if (inventory.items[event.getSlot()] instanceof PlayerSkullItem player) {
                new SetprefixInventory(plugin, p, player.getPlayer(), true).open();
            }
        } else if (title.equals(PrefixAdminInventory.TITLE_RANK)) {
            event.setCancelled(true);
            Player p = (Player) event.getWhoClicked();
            PrefixAdminInventory inventory = (PrefixAdminInventory) PlayerInventory.openInventories.get(p.getUniqueId());
            if (event.getSlot() == 47) {
                inventory.setPage(inventory.page - 1);
                return;
            } else if (event.getSlot() == 51) {
                inventory.setPage(inventory.page + 1);
                return;
            }
            if (inventory.items[event.getSlot()] instanceof PlayerSkullItem player) {
                new SetrankInventory(plugin, p, player.getPlayer()).open();
            }
        } else if (title.contains("§dPräfix von") && title.contains("§dändern")) {
            event.setCancelled(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(title.substring(15, title.length() - 9));
                    handlePrefixInventory((OfflinePlayer) event.getWhoClicked(), p, event.getSlot());
                }
            }.runTaskAsynchronously(plugin);
        } else if (title.contains("§dRang von") && title.contains("§dändern")) {
            event.setCancelled(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(title.substring(13, title.length() - 9));
                    SetrankInventory inventory = (SetrankInventory) PlayerInventory.openInventories.get(event.getWhoClicked().getUniqueId());
                    int slot = event.getSlot();
                    if (slot == 38) {
                        inventory.setPage(inventory.page - 1);
                        return;
                    } else if (slot == 42) {
                        inventory.setPage(inventory.page + 1);
                        return;
                    }
                    if (slot == 4) return;
                    if (inventory.items[slot] instanceof RankItem rank) {
                        rank.setRank(p);
                        inventory.setupItems();
                    }
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    public void handlePrefixInventory(OfflinePlayer whoClicked, OfflinePlayer p, int slot) {
        SetprefixInventory inventory = (SetprefixInventory) PlayerInventory.openInventories.get(whoClicked.getUniqueId());
        if (slot == 38) {
            inventory.setPage(inventory.page - 1);
            return;
        } else if (slot == 42) {
            inventory.setPage(inventory.page + 1);
            return;
        }
        if (slot == 4) return;
        if (inventory.items[slot] instanceof PrefixItem prefix) {
            prefix.setPrefix(p);
            inventory.setupItems();
        }
    }

}

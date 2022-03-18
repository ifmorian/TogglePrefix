package de.felix_kurz.toggleprefix.listeners;

import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public record JoinListener(Main plugin) implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player p = event.getPlayer();
                plugin.getMysql().loadPlayer(p);
                plugin.getSbM().updatePlayer(p);
            }
        }.runTaskAsynchronously(plugin);
    }

}

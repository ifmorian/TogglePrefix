package de.felix_kurz.toggleprefix.listeners;

import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    Main plugin;

    public ChatListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String chatprefix = plugin.getMysql().getChatPrefix(event.getPlayer());
        chatprefix = chatprefix.replace("%name%", event.getPlayer().getDisplayName());
        chatprefix = chatprefix.replace("&", "§");
        String msg = event.getMessage();
        if(plugin.getCfgM().useColorTranslate()) {
            msg = msg.replace("&", "§");
        }
        event.setFormat(chatprefix + " " + msg);
    }

}

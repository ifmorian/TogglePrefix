package de.felix_kurz.toggleprefix.listeners;

import de.felix_kurz.toggleprefix.databases.MySQL;
import de.felix_kurz.toggleprefix.main.Main;
import de.felix_kurz.toggleprefix.utils.Utils;
import org.bukkit.entity.Player;
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
        if(plugin.getMysql().getConn() == null) return;
        Player p = event.getPlayer();
        String prefix = plugin.getMysql().getFromPlayer(p, "prefix");
        String chatprefix = plugin.getMysql().getFrom(MySQL.prefixesTable, "name", prefix, "chat");
        if(chatprefix == null) {
            p.sendMessage(Main.PRE + "§cEtwas ist schiefgelaufen. Bitte versuche es erneut.");
            return;
        }
        chatprefix = chatprefix.replace("%name%", p.getDisplayName());
        chatprefix = Utils.colorTranslate(chatprefix);
        String msg = event.getMessage();
        if (plugin.getCfgM().useColorTranslate()) event.setMessage(Utils.colorTranslate((event.getMessage())));
        event.setFormat(chatprefix + " " + "%2$s");
    }

}

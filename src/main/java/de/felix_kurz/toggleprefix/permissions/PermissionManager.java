package de.felix_kurz.toggleprefix.permissions;

import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.entity.Player;

public class PermissionManager {

    public static boolean checkPermission(Player sender, String permission, boolean msg) {
        if(!sender.hasPermission(permission)) {
            if(msg) sender.sendMessage(Main.PRE + "§cYou do not have the permission to do that");
            return false;
        }
        return true;
    }

}

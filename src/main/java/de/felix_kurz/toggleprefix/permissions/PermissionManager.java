package de.felix_kurz.toggleprefix.permissions;

import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.command.CommandSender;

public class PermissionManager {

    public static boolean checkPermission(CommandSender sender, String permission) {
        if(!sender.hasPermission(permission)) {
            sender.sendMessage(Main.PRE + "§cYou don't have the required permission.");
            return false;
        }
        return true;
    }

}

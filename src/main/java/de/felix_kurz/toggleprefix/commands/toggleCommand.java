package de.felix_kurz.toggleprefix.commands;

import de.felix_kurz.toggleprefix.main.Main;
import de.felix_kurz.toggleprefix.permissions.PermissionManager;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class toggleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args[0].equals("new")) {
            if(PermissionManager.checkPermission(sender, "prefixtoggle.edit")) {

            }
        } else if (args[0].equals("rm") || args[0].equals("remove")) {
            if(PermissionManager.checkPermission(sender, "prefixtoggle.edit")) {

            }
        } else if (args[0].equals("edit")) {
            if(args.length < 3) {

            }
        } else {
            sender.sendMessage(Main.PRE + "§cUse §6/toggle help §cfor usage overview.");
        }
        return false;
    }

}

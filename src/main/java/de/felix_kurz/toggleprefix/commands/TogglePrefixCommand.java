package de.felix_kurz.toggleprefix.commands;

import de.felix_kurz.toggleprefix.main.Main;
import de.felix_kurz.toggleprefix.permissions.PermissionManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TogglePrefixCommand implements CommandExecutor {

    private Main plugin;

    public TogglePrefixCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Main.PRE + "You have to be a player to issue this command");
            return false;
        }
        Player p = (Player) sender;
        if (args.length == 0) {
            p.sendMessage(Main.PRE + "§9Use §6/toggleprefix help §9for more information");
            return false;
        }
        if (args[0].equals("gui")) {

        } else if (args[0].equals("set")) {
            if(args.length != 2) {
                p.sendMessage(Main.PRE + "§9Use §6/toggleprefix set <prefix>");
                return false;
            }

        } else if (args[0].equals("new")) {
            if(!PermissionManager.checkPermission(p, "toggleprefix.edit", true)) return false;
            if(args.length != 5) {
                sender.sendMessage(Main.PRE + "§9Use §6/toggleprefix new <name> <chat-prefix> <tablist-prefix> <itemstack>\n" +
                        " §7-> §bname §7- §9The name of the prefix\n" +
                        " §7-> §bchat-prefix §7- §9Prefix for chat messages. Use §6%name% §9where the player's name will be inserted\n" +
                        " §7-> §btablist-prefix §7- §9Prefix in the tablist. Use §6%name% §9for the player's name and $6%ping% $9for the ping\n" +
                        " §7-> §bitemstack §7- §9Icon in the gui interface §3(use spigot itemstack names)");
                return false;
            }
            if(Material.getMaterial(args[4]) == null) {
                p.sendMessage(Main.PRE + "§cEnter a valid itemstack");
                return false;
            }
            plugin.getMysql().addPrefix(args[1], args[2], args[3], args[4]);
            p.sendMessage(Main.PRE + "§aNew prefix §b" + args[1] + " §acreated");
        } else if (args[0].equals("delete")) {
            if(!PermissionManager.checkPermission(p, "toggleprefix.edit", true)) return false;
            if(args.length != 2) {
                sender.sendMessage(Main.PRE + "§9Use §6/toggleprefix delete <name>\n" +
                        " §7-> §bname §7- §9Name of the prefix");
                return false;
            }
            plugin.getMysql().deletePrefix(args[1]);
            p.sendMessage(Main.PRE + "§aPrefix §b" + args[1] + " §adeleted");
        } else if (args[0].equals("edit")) {
            if(!PermissionManager.checkPermission(p, "toggleprefix.edit", true)) return false;
            if(args.length != 4) {
                sender.sendMessage(Main.PRE + "§9Use §6/toggleprefix edit <name> <name/chat/tablist/item> <value>\n" +
                        " §7-> §bname §7- §9The name of the prefix\n");
                return false;
            }
            plugin.getMysql().editPrefix(args[1], args[2], args[3]);
            p.sendMessage(Main.PRE + "§aPrefix §b" + args[1] + " §aupdated");
        }
        return false;
    }
}

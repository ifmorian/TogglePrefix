package de.felix_kurz.toggleprefix.commands;

import de.felix_kurz.toggleprefix.main.Main;
import de.felix_kurz.toggleprefix.permissions.PermissionManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record TogglePrefixCommand(Main plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Main.PRE + "You have to be a player to issue this command");
            return false;
        }
        if (args.length == 0) {
            p.sendMessage(Main.PRE + "§9Use §6/toggleprefix help §9for more information");
            return false;
        }
        switch (args[0]) {
            case "gui":

                break;
            case "set":
                if (args.length != 2) {
                    p.sendMessage(Main.PRE + "§9Use §6/toggleprefix set <prefix>");
                    return false;
                }

                break;
            case "new":
                if (PermissionManager.isNotPermit(p, "toggleprefix.edit", true)) return false;
                if (args.length != 6) {
                    sender.sendMessage(Main.PRE + "§9Use §6/toggleprefix new <name> <chat-prefix> <tablist-prefix> <itemstack> <priority>\n" +
                            " §7-> §bname §7- §9The name of the prefix\n" +
                            " §7-> §bchat-prefix §7- §9Prefix for chat messages. Use §6%name% §9where the player's name will be inserted\n" +
                            " §7-> §btablist-prefix §7- §9Prefix for tablist. Use §6%name% §9for the player's name and $6%ping% $9for the ping\n" +
                            " §7-> §bitemstack §7- §9Icon in the gui interface §3(use spigot itemstack names\n)" +
                            " §7-> §bpriority §7- §9Priority for tablist");
                    return false;
                }
                if (Material.getMaterial(args[4]) == null) {
                    p.sendMessage(Main.PRE + "§cEnter a valid itemstack");
                    return false;
                }
                int prio;
                try {
                    prio = Integer.parseInt(args[5]);
                } catch(NumberFormatException e) {
                    p.sendMessage(Main.PRE + "§cPriority must be a number");
                    return false;
                }
                if(prio > 999 || prio < 1) {
                    p.sendMessage(Main.PRE + "§cPriority must be between 1 and 999");
                    return false;
                }
                plugin.getMysql().addPrefix(args[1], args[2], args[3], args[4], args[5], p);
                break;
            case "delete":
                if (PermissionManager.isNotPermit(p, "toggleprefix.edit", true)) return false;
                if (args.length != 2) {
                    p.sendMessage(Main.PRE + "§9Use §6/toggleprefix delete <name>\n" +
                            " §7-> §bname §7- §9Name of the prefix");
                    return false;
                }
                plugin.getMysql().deletePrefix(args[1], p);
                break;
            case "edit":
                if (PermissionManager.isNotPermit(p, "toggleprefix.edit", true)) return false;
                if (args.length < 4) {
                    p.sendMessage(Main.PRE + "§9Use §6/toggleprefix edit <name> <name/chat/tablist/item> <value>\n" +
                            " §7-> §bname §7- §9The name of the prefix\n");
                    return false;
                }
                StringBuilder value = new StringBuilder(args[3]);
                for(int i = 4; i < args.length; i++) {
                    value.append(" ").append(args[i]);
                }
                if (!(args[2].equals("name") || args[2].equals("chat") || args[2].equals("tablist") || args[2].equals("item") || args[2].equals("priority"))) {
                    p.sendMessage(Main.PRE + "§cUse §6name§c, §6chat§c, §6tablist§c, §6item §cor §6priority §cas third argument");
                    return false;
                }
                if (args[2].equals("item") && Material.getMaterial(args[3]) == null) {
                    p.sendMessage(Main.PRE + "§cEnter a valid itemstack");
                    return false;
                }
                if (args[2].equals("priority")) {
                    try {
                        Integer.parseInt(args[3]);
                    } catch(NumberFormatException e) {
                        p.sendMessage(Main.PRE + "§cPriority must be a number");
                    }
                    if(Integer.parseInt(args[3]) > 999 || Integer.parseInt(args[3]) < 1) {
                        p.sendMessage(Main.PRE + "§cPriority must be between 1 and 999");
                    }
                }
                plugin.getMysql().editPrefix(args[1], args[2], value.toString(), p);
                break;
        }
        return false;
    }
}

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
                if (args.length < 5) {
                    sender.sendMessage(Main.PRE + "§9Use §6/toggleprefix new <name> <prefix> <itemstack> <priority>\n" +
                            " §7-> §bname §7- §9The name of the prefix\n" +
                            " §7-> §bchat-prefix §7- §9Prefix for chat messages. Use §6%name% §9where the player's name will be inserted\n" +
                            " §7-> §bitemstack §7- §9Icon in the gui interface §3(use spigot itemstack names)\n" +
                            " §7-> §bpriority §7- §9Priority for tablist");
                    return false;
                }
                if (Material.getMaterial(args[args.length - 2]) == null) {
                    p.sendMessage(Main.PRE + "§cEnter a valid itemstack");
                    return false;
                }
                String chat = argsToString(args, 2, args.length - 3);
                if (isNoValidPriority(args[args.length - 1], p)) return false;
                plugin.getMysql().addPrefix(args[1], chat, args[args.length - 2], args[args.length - 1], p);
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
                    p.sendMessage(Main.PRE + "§9Use §6/toggleprefix edit <name> [name/display/chat/tablist/item/priority] <value>\n" +
                            " §7-> §bname §7- §9The name of the prefix\n");
                    return false;
                }
                if (!(args[2].equals("name") || args[2].equals("display") || args[2].equals("chat") || args[2].equals("tablist") || args[2].equals("item") || args[2].equals("priority"))) {
                    p.sendMessage(Main.PRE + "§cUse §6name§c, §6display§c, §6chat§c, §6tablist§c, §6item §cor §6priority §cas third argument");
                    return false;
                }
                String value = argsToString(args, 3, args.length - 1);
                if (args[2].equals("item") && Material.getMaterial(args[3]) == null) {
                    p.sendMessage(Main.PRE + "§cEnter a valid itemstack");
                    return false;
                }
                if(args[2].equals("priority")) if (isNoValidPriority(value, p)) return false;
                plugin.getMysql().editPrefix(args[1], args[2], value, p);
                break;
            case "rank":
                if (PermissionManager.isNotPermit(p, "toggleprefix.edit", true)) return false;
                if (args.length < 2) {
                    p.sendMessage(Main.PRE + "§9Use §6/toggleprefix rank [new/delete/addprefix/removeprefix/edit]\n" +
                            " §7-> §bnew §7- §9Create a new rank" +
                            " §7-> §bdelete §7- §9Delete a rank" +
                            " §7-> §baddprefix §7- §9CAdd a prefix to a rank" +
                            " §7-> §bremoveprefix §7- §9Removes a prefix from a rank" +
                            " §7-> §bedit §7- §9Edit a rank");
                    return false;
                }
                switch(args[1]) {
                    case "new":
                        if(args.length != 5) {
                            p.sendMessage(Main.PRE + "§9Use §6/toggleprefix rank new <name> <prefix1,prefix2,...> <priority>");
                            return false;
                        }
                        if (isNoValidPriority(args[4], p)) return false;
                        plugin.getMysql().addRank(args[2], args[3], args[4], p);
                        break;
                    case "delete":
                        if(args.length != 3) {
                            p.sendMessage(Main.PRE + "§9Use §6/toggleprefix rank delete <name>");
                            return false;
                        }
                        plugin.getMysql().deleteRank(args[2], p);
                        break;
                    case "addprefix":
                        if(args.length != 4) {
                            p.sendMessage(Main.PRE + "§9Use §6/toggleprefix rank addprefix <rank> <prefix1,prefix2,...>");
                            return false;
                        }
                        plugin.getMysql().addPrefixToRank(args[2], args[3], p);
                        break;
                    case "removeprefix":
                        break;
                    case "edit":
                        break;
                }
                break;
        }
        return false;
    }

    public boolean isNoValidPriority(String priority, Player p) {
        int prio;
        try {
            prio = Integer.parseInt(priority);
        } catch(NumberFormatException e) {
            p.sendMessage(Main.PRE + "§cPriority must be a number");
            return true;
        }
        if(prio > 999 || prio < 1) {
            p.sendMessage(Main.PRE + "§cPriority must be between 1 and 999");
            return true;
        }
        return false;
    }

    public String argsToString(String[] args, int start, int end) {
        StringBuilder value = new StringBuilder(args[start]);
        for(int i = start + 1; i <= end; i++) {
            value.append(" ").append(args[i]);
        }
        return value.toString();
    }
}

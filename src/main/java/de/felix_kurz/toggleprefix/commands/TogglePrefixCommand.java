package de.felix_kurz.toggleprefix.commands;

import de.felix_kurz.toggleprefix.databases.MySQL;
import de.felix_kurz.toggleprefix.main.Main;
import de.felix_kurz.toggleprefix.permissions.PermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public record TogglePrefixCommand(Main plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Main.PRE + "Du musst ein Spieler sein, um dieses Command auszuführen");
            return false;
        }
        if (args.length == 0) {
            p.sendMessage(Main.PRE + "§9Benutze §6/toggleprefix help §9für mehr Informationen");
            return false;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                MySQL mySQL = plugin.getMysql();
                switch (args[0]) {
                    case "gui":

                        break;
                    case "set":
                        if (args.length != 2) {
                            p.sendMessage(Main.PRE + "§9Use §6/toggleprefix set <prefix>");
                            return;
                        }

                        break;
                    case "new":
                        if (PermissionManager.isNotPermit(p, "toggleprefix.edit", true)) return;
                        if (args.length < 5) {
                            sender.sendMessage(Main.PRE + "§9Use §6/toggleprefix new <name> <prefix> <itemstack> <priority>\n" +
                                    " §7-> §bname §7- §9The name of the prefix\n" +
                                    " §7-> §bchat-prefix §7- §9Prefix for chat messages. Use §6%name% §9where the player's name will be inserted\n" +
                                    " §7-> §bitemstack §7- §9Icon in the gui interface §3(use spigot itemstack names)\n" +
                                    " §7-> §bpriority §7- §9Priority for tablist");
                            return;
                        }
                        if (Material.getMaterial(args[args.length - 2]) == null) {
                            p.sendMessage(Main.PRE + "§cEnter a valid itemstack\n" +
                                    "§3hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
                            return;
                        }
                        String chat = argsToString(args, 2, args.length - 3);
                        if (isNoValidPriority(args[args.length - 1], p)) return;
                        if (mySQL.exists("prefixes", "name", args[1])) {
                            p.sendMessage(Main.PRE + "§cPrefix §6" + args[1] + " §calready exists");
                            return;
                        }
                        if (mySQL.addPrefix(args[1], chat, args[args.length - 2], args[args.length - 1])) {
                            p.sendMessage(Main.PRE + "§aAdded prefix §6" + args[1]);
                        } else
                            error(p);
                        break;
                    case "delete":
                        if (PermissionManager.isNotPermit(p, "toggleprefix.edit", true)) return;
                        if (args.length != 2) {
                            p.sendMessage(Main.PRE + "§9Use §6/toggleprefix delete <name>\n" +
                                    " §7-> §bname §7- §9Name of the prefix");
                            return;
                        }
                        if (!mySQL.exists("prefixes", "name", args[1])) {
                            p.sendMessage(Main.PRE + "§cPrefix §6" + args[1] + " §cdoes not exists");
                            return;
                        }
                        if (mySQL.delete("prefixes", "name", args[1])) {
                            p.sendMessage(Main.PRE + "§aDeleted prefix §6" + args[1]);
                        } else
                            error(p);
                        break;
                    case "edit":
                        if (PermissionManager.isNotPermit(p, "toggleprefix.edit", true)) return;
                        if (args.length < 4) {
                            p.sendMessage(Main.PRE + "§9Use §6/toggleprefix edit <name> [name/display/chat/tablist/item/priority] <value>\n" +
                                    " §7-> §bname §7- §9The name of the prefix\n");
                            return;
                        }
                        if (!(args[2].equals("name") || args[2].equals("display") || args[2].equals("chat") || args[2].equals("tablist") || args[2].equals("item") || args[2].equals("priority"))) {
                            p.sendMessage(Main.PRE + "§cUse §6name§c, §6display§c, §6chat§c, §6tablist§c, §6item §cor §6priority §cas third argument");
                            return;
                        }
                        String value = argsToString(args, 3, args.length - 1);
                        if (args[2].equals("item") && Material.getMaterial(value) == null) {
                            p.sendMessage(Main.PRE + "§cEnter a valid itemstack\n" +
                                    "§3hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
                            return;
                        }
                        if(args[2].equals("priority")) if (isNoValidPriority(value, p)) return;
                        if(!mySQL.exists("prefixes", "name", args[1])) {
                            p.sendMessage(Main.PRE + "§cPrefix §6" + args[1] + " §cdoes not exists");
                            return;
                        }
                        if(mySQL.edit("prefixes", "name", args[1], args[2], value)) {
                            p.sendMessage(Main.PRE + "§aSet §3" + args[2] + " §aof prefix §6" + args[1] + " §ato §3" + value);
                        } else
                            error(p);
                        break;
                    case "rank":
                        if (PermissionManager.isNotPermit(p, "toggleprefix.edit", true)) return;
                        if (args.length < 2) {
                            p.sendMessage(Main.PRE + "§9Use §6/toggleprefix rank [new/delete/addprefix/removeprefix/edit]\n" +
                                    " §7-> §bnew §7- §9Erstellt einen neuen Rang" +
                                    " §7-> §bdelete §7- §9Delete a rank" +
                                    " §7-> §baddprefix §7- §9CAdd a prefix to a rank" +
                                    " §7-> §bremoveprefix §7- §9Removes a prefix from a rank" +
                                    " §7-> §bedit §7- §9Edit a rank");
                            return;
                        }
                        switch(args[1]) {
                            case "new":
                                if(args.length != 5) {
                                    p.sendMessage(Main.PRE + "§9Use §6/toggleprefix rank new <name> <prefix1,prefix2,...> <priority>");
                                    return;
                                }
                                if (isNoValidPriority(args[4], p)) return;
                                if (mySQL.exists("ranks", "name", args[2])) {
                                    p.sendMessage(Main.PRE + "§cRank §6" + args[2] + " §calready exists");
                                    return;
                                }
                                String notExistentPrefix = prefixNotExists(args[3]);
                                if(notExistentPrefix != null) {
                                    p.sendMessage(Main.PRE + "§cPrefix §6" + notExistentPrefix + " §cdoes not exists");
                                    return;
                                }
                                if(mySQL.addRank(args[2], args[3], args[4])) {
                                    p.sendMessage(Main.PRE + "§aAdded rank §6" + args[2]);
                                } else
                                    error(p);
                                break;
                            case "delete":
                                if (args.length != 3) {
                                    p.sendMessage(Main.PRE + "§9Use §6/toggleprefix rank delete <name>");
                                    return;
                                }
                                if (!mySQL.exists("ranks", "name", args[2])) {
                                    p.sendMessage(Main.PRE + "§cRank §6" + args[2] + " §cdoes not exists");
                                    return;
                                }
                                if (mySQL.delete("ranks", "name", args[2])) {
                                    p.sendMessage(Main.PRE + "§aDeleted rank §6" + args[2]);
                                } else
                                    error(p);
                                break;
                            case "addprefix":
                                if (args.length != 4) {
                                    p.sendMessage(Main.PRE + "§9Use §6/toggleprefix rank addprefix <rank> <prefix1,prefix2,...>");
                                    return;
                                }
                                if (!mySQL.exists("ranks", "name", args[2])) {
                                    p.sendMessage(Main.PRE + "§cRank §6" + args[2] + " §cdoes not exists");
                                    return;
                                }

                                String notExistentPrefix1 = prefixNotExists(args[3]);
                                if (notExistentPrefix1 != null) {
                                    p.sendMessage(Main.PRE + "§cPrefix §6" + notExistentPrefix1 + " §cdoes not exists");
                                    return;
                                }
                                String prefixes = joinPrefixes(args[3], mySQL.getRankPrefixes(args[2]));
                                if (mySQL.edit("ranks", "name", args[2], "prefixes", prefixes)) {
                                    p.sendMessage(Main.PRE + "§aAdded prefixes §3" + args[3] + " §ato rank §6" + args[2] + "\n" +
                                            "§3(" + prefixes + ")");
                                } else
                                    error(p);
                                break;
                            case "removeprefix":
                                if (args.length != 4) {
                                    p.sendMessage(Main.PRE + "§9Use §6/toggleprefix rank removeprefix <rank> <prefix1,prefix2,...>");
                                    return;
                                }
                                if (!mySQL.exists("ranks", "name", args[2])) {
                                    p.sendMessage(Main.PRE + "§cRank §6" + args[2] + " §cdoes not exists");
                                    return;
                                }
                                String[] prefixes1 =  mySQL.getRankPrefixes(args[2]).split(",");
                                for (int i = 0; i < prefixes1.length; i++) {
                                    for (String s : args[3].split(",")) {
                                        if (prefixes1[i].equals(s)) {
                                            prefixes1[i] = null;
                                            break;
                                        }
                                    }
                                }
                                StringBuilder prefixesR = new StringBuilder();
                                for (String s : prefixes1) {
                                    if (s != null) {
                                        prefixesR.append(",").append(s);
                                    }
                                }
                                prefixesR.delete(0, 1);
                                if (mySQL.edit("ranks", "name", args[2], "prefixes", prefixesR.toString())) {
                                p.sendMessage(Main.PRE + "§aPräfix(e) §3" + args[3] + " §avon Rang §6" + args[2] + " §aentfernt\n" +
                                        "§3(" + prefixesR + ")");
                                } else
                                    error(p);
                                break;
                            case "edit":
                                break;
                        }
                        break;
                }
            }
        }.runTaskAsynchronously(plugin);
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

    public void error(Player p) {
        p.sendMessage(Main.PRE + "§cSomething went wrong");
    }

    public String prefixNotExists(String prefixes) {
        String[] prefixesArray = prefixes.split(",");
        for (String prefix : prefixesArray) {
            if (!plugin.getMysql().exists("prefixes", "name", prefix)) {
                return prefix;
            }
        }
        return null;
    }

    public String joinPrefixes(String newPrefixes, String oldPrefixes) {
        StringBuilder prefixes = new StringBuilder(oldPrefixes);
        String[] oldPrefixesArray = oldPrefixes.split(",");
        String[] newPrefixesArray = newPrefixes.split(",");
        mainloop: for (String newPrefix : newPrefixesArray) {
            for (String oldPrefix : oldPrefixesArray) {
                if (newPrefix.equals(oldPrefix)) {
                    continue mainloop;
                }
            }
            prefixes.append(",").append(newPrefix);
        }
        return prefixes.toString();
    }

}

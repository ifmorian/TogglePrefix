package de.felix_kurz.toggleprefix.commands;

import de.felix_kurz.toggleprefix.databases.MySQL;
import de.felix_kurz.toggleprefix.main.Main;
import de.felix_kurz.toggleprefix.permissions.PermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public record TogglePrefixCommand(Main plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Main.PRE + "Du musst ein Spieler sein, um dieses Command auszuführen");
            return false;
        }
        if (args.length == 0) {
            p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix [§enew§6/§edelete§6/§eedit§6/§erank§6]\n" +
                    " §7-> §bnew §7- §9Erstellt einen neuen Präfix\n" +
                    " §7-> §bdelete §7- §9Entfertn einen Präfix\n" +
                    " §7-> §bedit §7- §9Bearbeiten eines Präfixes\n" +
                    " §7-> §brank §7- §9Ränge erstellen, entfernen oder bearbeiten");
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
                            sender.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix new <§ename§6> <§eprefix§6> <§eitemstack§6> <§epriority§6>\n" +
                                    " §7-> §bname §7- §9Name des Präfixes\n" +
                                    " §7-> §bchat-prefix §7- §9Präfix für Chat-Nachrichten. Benutze §6%name% §9für den Spielernamen\n" +
                                    " §7-> §bitemstack §7- §9Icon des Präfixes im GUI §3(Bitte benutze die Bezeichnungen für Spigot Itemstacks)\n" +
                                    " §7-> §bpriority §7- §9Die Priorität in der Tabliste");
                            return;
                        }
                        if (Material.getMaterial(args[args.length - 2]) == null) {
                            p.sendMessage(Main.PRE + "§cBitte gib einen richtigen Itemstack ein\n" +
                                    "§3hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
                            return;
                        }
                        String chat = argsToString(args, 2, args.length - 3);
                        if (isNoValidPriority(args[args.length - 1], p)) return;
                        if (mySQL.exists("prefixes", "name", args[1])) {
                            p.sendMessage(Main.PRE + "§cPräfix §6" + args[1] + " §cexistiert bereits");
                            return;
                        }
                        if (mySQL.addPrefix(args[1], chat, args[args.length - 2], args[args.length - 1])) {
                            p.sendMessage(Main.PRE + "§aPräfix §6" + args[1] + " §awurde hinzugefügt");
                        } else
                            error(p);
                        break;
                    case "delete":
                        if (PermissionManager.isNotPermit(p, "toggleprefix.edit", true)) return;
                        if (args.length != 2) {
                            p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix delete <§ename§6>\n" +
                                    " §7-> §bname §7- §9Name des Präfixes");
                            return;
                        }
                        if (!mySQL.exists("prefixes", "name", args[1])) {
                            p.sendMessage(Main.PRE + "§cPräfix §6" + args[1] + " §cexistiert nicht");
                            return;
                        }
                        if (mySQL.delete("prefixes", "name", args[1])) {
                            p.sendMessage(Main.PRE + "§aPräfix §6" + args[1] + " §awurde entfernt");
                        } else
                            error(p);
                        break;
                    case "edit":
                        if (PermissionManager.isNotPermit(p, "toggleprefix.edit", true)) return;
                        if (args.length < 4) {
                            p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix edit <§ename§6> [§ename§6/§edisplay§6/§echat§6/§etablist§6/§eitem§6/§epriority§6] <§evalue§6>\n" +
                                    " §7-> §bname §7- §9The name of the prefix\n");
                            return;
                        }
                        if (!(args[2].equals("name") || args[2].equals("display") || args[2].equals("chat") || args[2].equals("tablist") || args[2].equals("item") || args[2].equals("priority"))) {
                            p.sendMessage(Main.PRE + "§cBitte benutze §6name§c, §6display§c, §6chat§c, §6tablist§c, §6item §coder §6priority §cals drittes Argument");
                            return;
                        }
                        String value = argsToString(args, 3, args.length - 1);
                        if (args[2].equals("item") && Material.getMaterial(value) == null) {
                            p.sendMessage(Main.PRE + "§cBitte gib einen richtigen Itemstack ein\n" +
                                    "§3hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
                            return;
                        }
                        if(args[2].equals("priority")) if (isNoValidPriority(value, p)) return;
                        if(!mySQL.exists("prefixes", "name", args[1])) {
                            p.sendMessage(Main.PRE + "§cPräfix §6" + args[1] + " §cexistiert nicht");
                            return;
                        }
                        if(mySQL.edit("prefixes", "name", args[1], args[2], value)) {
                            p.sendMessage(Main.PRE + "§3" + args[2] + " §avon Präfix §6" + args[1] + " §awurde zu §3" + value + " §ageändert");
                        } else
                            error(p);
                        break;
                    case "rank":
                        if (PermissionManager.isNotPermit(p, "toggleprefix.edit", true)) return;
                        if (args.length < 2) {
                            p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix rank [§enew§6/§edelete§6/§eaddprefix§6/§eremoveprefix§6/§eedit§6]\n" +
                                    " §7-> §bnew §7- §9Erstellt einen neuen Rang\n" +
                                    " §7-> §bdelete §7- §9Entfertn einen Rang\n" +
                                    " §7-> §baddprefix §7- §9CFügt Präfixe zu einem Rang hinzu\n" +
                                    " §7-> §bremoveprefix §7- §9Entfernt Präfixe von einem Rang\n" +
                                    " §7-> §bedit §7- §9Bearbeiten eines Rangs");
                            return;
                        }
                        switch (args[1]) {
                            case "new" -> {
                                if (args.length != 5) {
                                    p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix rank new <§ename§6> <§eprefix1§6,§eprefix2§6,§e...§6> <§epriority§6>");
                                    return;
                                }
                                if (isNoValidPriority(args[4], p)) return;
                                if (mySQL.exists("ranks", "name", args[2])) {
                                    p.sendMessage(Main.PRE + "§cRang §6" + args[2] + " §cexistiert bereits");
                                    return;
                                }
                                String notExistentPrefix = prefixNotExists(args[3]);
                                if (notExistentPrefix != null) {
                                    p.sendMessage(Main.PRE + "§cPräfix §6" + notExistentPrefix + " §cexistiert nicht");
                                    return;
                                }
                                if (mySQL.addRank(args[2], args[3], args[4])) {
                                    p.sendMessage(Main.PRE + "§aRang§6" + args[2] + " §ahinzugefügt");
                                } else
                                    error(p);
                            }
                            case "delete" -> {
                                if (args.length != 3) {
                                    p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix rank delete <§ename§6>");
                                    return;
                                }
                                if (!mySQL.exists("ranks", "name", args[2])) {
                                    p.sendMessage(Main.PRE + "§cRang §6" + args[2] + " §cexistiert nicht");
                                    return;
                                }
                                if (mySQL.delete("ranks", "name", args[2])) {
                                    p.sendMessage(Main.PRE + "§aRang §6" + args[2] + " §aentfernt");
                                } else
                                    error(p);
                            }
                            case "addprefix" -> {
                                if (args.length != 4) {
                                    p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix rank addprefix <§erank§6> <§eprefix1§6,§eprefix2§6,§e...§6>");
                                    return;
                                }
                                if (!mySQL.exists("ranks", "name", args[2])) {
                                    p.sendMessage(Main.PRE + "§cRang §6" + args[2] + " §cexistiert nicht");
                                    return;
                                }
                                String notExistentPrefix1 = prefixNotExists(args[3]);
                                if (notExistentPrefix1 != null) {
                                    p.sendMessage(Main.PRE + "§cPräfix §6" + notExistentPrefix1 + " §cexistiert nicht");
                                    return;
                                }
                                String prefixes = joinPrefixes(args[3], mySQL.getRankPrefixes(args[2]));
                                if (mySQL.edit("ranks", "name", args[2], "prefixes", prefixes)) {
                                    p.sendMessage(Main.PRE + "§aPräfix(e) §3" + args[3] + " §azu Rang §6" + args[2] + " §ahinzugefügt\n" +
                                            "§3(" + prefixes + ")");
                                } else
                                    error(p);
                            }
                            case "removeprefix" -> {
                                if (args.length != 4) {
                                    p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix rank removeprefix <§erank§6> <§eprefix1§6,§eprefix2§6,§e...§6>");
                                    return;
                                }
                                if (!mySQL.exists("ranks", "name", args[2])) {
                                    p.sendMessage(Main.PRE + "§cRang §6" + args[2] + " §cexistiert nicht");
                                    return;
                                }
                                String[] prefixes1 = mySQL.getRankPrefixes(args[2]).split(",");
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
                            }
                            case "edit" -> {
                                if (args.length < 5) {
                                    p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix rank edit <§ename§6> [§ename§6/§edisplay§6/§epriority§6] <§evalue§6>\n" +
                                            " §7-> §bname §7- §9Der Name des Rangs\n");
                                    return;
                                }
                                if (!(args[3].equals("name") || args[3].equals("display") || args[3].equals("priority"))) {
                                    p.sendMessage(Main.PRE + "§cBitte benutze §6name§c, §6display §coder §6priority §cals viertes Argument");
                                    return;
                                }
                                if (!mySQL.exists("ranks", "name", args[2])) {
                                    p.sendMessage(Main.PRE + "§cRang §6" + args[2] + " §cexistiert nicht");
                                    return;
                                }
                                if (args[3].equals("name")) {
                                    if(args.length != 5) {
                                        p.sendMessage(Main.PRE + "§cDer Name des Rangs darf keine Leerzeichen enthalten");
                                        return;
                                    }
                                    if(mySQL.exists("ranks", "name", args[4])) {
                                        p.sendMessage(Main.PRE + "§cRang §6" + args[4] + " §cexistiert bereits");
                                        return;
                                    }
                                }

                                String v = argsToString(args, 4, args.length - 1);
                                if (args[3].equals("priority")) if (isNoValidPriority(v, p)) return;
                                if(mySQL.edit("ranks", "name", args[2], args[3], v)) {
                                    p.sendMessage(Main.PRE + "§3" + args[3] + " §avon Rang §6" + args[2] + " §awurde zu §3" + v + " §ageändert");
                                } else
                                    error(p);
                            }
                            default -> p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix rank [§enew§6/§edelete§6/§eaddprefix§6/§eremoveprefix§6/§eedit§6]\n" +
                                    " §7-> §bnew §7- §9Erstellt einen neuen Rang\n" +
                                    " §7-> §bdelete §7- §9Entfertn einen Rang\n" +
                                    " §7-> §baddprefix §7- §9CFügt Präfixe zu einem Rang hinzu\n" +
                                    " §7-> §bremoveprefix §7- §9Entfernt Präfixe von einem Rang\n" +
                                    " §7-> §bedit §7- §9Bearbeiten eines Rangs");
                        }
                        break;
                    case "player":
                        if (PermissionManager.isNotPermit(p, "toggleprefix.admin", true)) return;
                        if (args.length < 3) {
                            p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix player <§eplayer§6> [§esetrank§6/§esetprefix§6/§eaddprefix§6/§eremoveprefix§6]\n" +
                                    " §7-> §bsetrank §7- §9Setzt den Rang eines Spielers\n" +
                                    " §7-> §bsetprefix §7- §9Setzt den Präfix eines Spielers\n" +
                                    " §7-> §baddprefix §7- §9Fügt verfügbare Präfixe für den Spieler hinzu\n" +
                                    " §7-> §bremoveprefix §7- §9Entfernt verfügbare Präfixe für den Spieler");
                            return;
                        }
                        UUID id;
                        Player player = Bukkit.getPlayer(args[1]);
                        if(!player.isValid()) {
                            p.sendMessage(Main.PRE + "§cDer Spieler §b" + args[1] + " §cwurde nicht gefunden §3(Offline Spieler werden nicht erfasst)");
                            return;
                        }
                        id = player.getUniqueId();
                        if(!mySQL.playerExists(id)) {
                            p.sendMessage(Main.PRE + "§cDer Spieler ist nicht in der Datenbank erfasst");
                            return;
                        }
                        switch (args[2]) {
                            case "setrank" -> {
                                if (args.length != 4) {
                                    p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix player <§eplayer§6> setrank <§erank§6>");
                                    return;
                                }
                                if(!mySQL.exists("ranks", "name", args[3])) {
                                    p.sendMessage(Main.PRE + "§cRang §6" + args[3] + " §cexistiert nicht");
                                    return;
                                }
                                if(mySQL.editPlayer(id, "rank", args[3])) {
                                    p.sendMessage("§cRang des Spielers §b" + args[1] + " §awurde auf §6" + args[3] + " §agesetzt");
                                } else
                                    error(p);
                            }
                            case "setprefix" -> {
                                if (args.length != 4) {
                                    p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix player <§eplayer§6> setprefix <§erank§6>");
                                    return;
                                }
                                if(!mySQL.exists("prefixes", "name", args[3])) {
                                    p.sendMessage(Main.PRE + "§cPräfix §6" + args[3] + " §cexistiert nicht");
                                    return;
                                }
                                if(mySQL.editPlayer(id, "prefix", args[3])) {
                                    p.sendMessage("§cPräfix des Spielers §b" + args[1] + " §awurde auf §6" + args[3] + " §agesetzt");
                                }
                                else {
                                    error(p);
                                }
                            }
                            case "addprefix" -> {

                            }
                            case "removeprefix" -> {

                            }
                            default -> {
                                p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix player [§esetrank§6/§esetprefix§6/§eaddprefix§6/§eremoveprefix§6]\n" +
                                        " §7-> §bsetrank §7- §9Setzt den Rang eines Spielers\n" +
                                        " §7-> §bsetprefix §7- §9Setzt den Präfix eines Spielers\n" +
                                        " §7-> §baddprefix §7- §9Fügt verfügbare Präfixe für den Spieler hinzu\n" +
                                        " §7-> §bremoveprefix §7- §9Entfernt verfügbare Präfixe für den Spieler");
                            }
                        }
                        break;
                    default:
                        p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix [§enew§6/§edelete§6/§eedit§6/§erank§6]\n" +
                                " §7-> §bnew §7- §9Erstellt einen neuen Präfix\n" +
                                " §7-> §bdelete §7- §9Entfertn einen Präfix\n" +
                                " §7-> §bedit §7- §9Bearbeiten eines Präfixes\n" +
                                " §7-> §brank §7- §9Ränge erstellen, entfernen oder bearbeiten");
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
        p.sendMessage(Main.PRE + "§cEtwas ist schiefgelaufen");
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

package de.felix_kurz.toggleprefix.commands;

import de.felix_kurz.toggleprefix.databases.MySQL;
import de.felix_kurz.toggleprefix.inventories.SetprefixInventory;
import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TogglePrefixCommand implements CommandExecutor {

    private Main plugin;
    private MySQL mySQL;

    public TogglePrefixCommand(Main plugin) {
        this.plugin = plugin;
        mySQL = plugin.getMysql();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Main.PRE + "Du musst ein Spieler sein, um dieses Command auszuführen");
            return false;
        }
        if (args.length == 0) {
            SetprefixInventory inv = new SetprefixInventory(plugin, p);
            inv.setup();
            return false;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                switch (args[0]) {
                    case "prefix" -> {
                        if (isNotPermit(p, "toggleprefix.edit", true)) return;
                        if(args.length < 2) {
                            p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix prefix [§enew§6/§edelete§6/§eedit§6]\n" +
                                    " §7-> §bnew §7- §9Erstellt einen neuen Präfix\n" +
                                    " §7-> §bdelete §7- §9Entfertn einen Präfix\n" +
                                    " §7-> §bedit §7- §9Bearbeiten eines Präfixes\n");
                            return;
                        }
                        prefix(p, args);
                    }
                    case "rank" -> {
                        if (isNotPermit(p, "toggleprefix.edit", true)) return;
                        if (args.length < 2) {
                            p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix rank [§enew§6/§edelete§6/§eaddprefix§6/§eremoveprefix§6/§eedit§6]\n" +
                                    " §7-> §bnew §7- §9Erstellt einen neuen Rang\n" +
                                    " §7-> §bdelete §7- §9Entfertn einen Rang\n" +
                                    " §7-> §baddprefix §7- §9CFügt Präfixe zu einem Rang hinzu\n" +
                                    " §7-> §bremoveprefix §7- §9Entfernt Präfixe von einem Rang\n" +
                                    " §7-> §bedit §7- §9Bearbeiten eines Rangs");
                            return;
                        }
                        rank(p, args);
                    }
                    case "player" -> {
                        if (isNotPermit(p, "toggleprefix.admin", true)) return;
                        if (args.length < 3) {
                            p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix player <§eplayer§6> [§esetrank§6/§esetprefix§6/§eaddprefix§6/§eremoveprefix§6]\n" +
                                    " §7-> §bsetrank §7- §9Setzt den Rang eines Spielers\n" +
                                    " §7-> §bsetprefix §7- §9Setzt den Präfix eines Spielers\n" +
                                    " §7-> §baddprefix §7- §9Fügt verfügbare Präfixe für den Spieler hinzu\n" +
                                    " §7-> §bremoveprefix §7- §9Entfernt verfügbare Präfixe für den Spieler");
                            return;
                        }
                        Player player = Bukkit.getPlayer(args[1]);
                        if(player == null) {
                            p.sendMessage(Main.PRE + "§cSpieler*in§b" + args[1] + " §cwurde nicht gefunden §3(Offline Spieler werden nicht erfasst)");
                            return;
                        }
                        if(!mySQL.playerExists(player)) {
                            p.sendMessage(Main.PRE + "§cSpieler*in ist nicht in der Datenbank erfasst");
                            return;
                        }
                        player(p, args, player);
                    }
                    default -> {

                    }
                }
            }
        }.runTaskAsynchronously(plugin);
        return false;
    }

    private void prefix(Player p, String[] args) {
        switch(args[1]) {
            case "new" -> {
                if (args.length < 6) {
                    p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix prefix new <§ename§6> <§eprefix§6> <§eitemstack§6> <§epriority§6>\n" +
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
                String chat = argsToString(args, 3, args.length - 3);
                if (isNoValidPriority(args[args.length - 1], p)) return;
                if (mySQL.exists("prefixes", "name", args[2])) {
                    p.sendMessage(Main.PRE + "§cPräfix §6" + args[2] + " §cexistiert bereits");
                    return;
                }
                if (mySQL.addPrefix(args[2], chat, args[args.length - 2], args[args.length - 1])) {
                    p.sendMessage(Main.PRE + "§aPräfix §6" + args[2] + " §awurde hinzugefügt");
                } else
                    error(p);
            }
            case "delete" -> {
                if (args.length != 3) {
                    p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix prefix delete <§ename§6>\n" +
                            " §7-> §bname §7- §9Name des Präfixes");
                    return;
                }
                if (!mySQL.exists("prefixes", "name", args[2])) {
                    p.sendMessage(Main.PRE + "§cPräfix §6" + args[2] + " §cexistiert nicht");
                    return;
                }
                if (mySQL.delete("prefixes", "name", args[2])) {
                    p.sendMessage(Main.PRE + "§aPräfix §6" + args[2] + " §awurde entfernt");
                } else
                    error(p);
            }
            case "edit" -> {
                if (args.length < 5) {
                    p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix prefix edit <§ename§6> [§ename§6/§edisplay§6/§echat§6/§etablist§6/§eitem§6/§epriority§6] <§evalue§6>\n" +
                            " §7-> §bname §7- §9The name of the prefix\n");
                    return;
                }
                if (!(args[3].equals("name") || args[3].equals("display") || args[3].equals("chat") || args[3].equals("tablist") || args[3].equals("item") || args[3].equals("priority"))) {
                    p.sendMessage(Main.PRE + "§cBitte benutze §6name§c, §6display§c, §6chat§c, §6tablist§c, §6item §coder §6priority §cals drittes Argument");
                    return;
                }
                String value = argsToString(args, 4, args.length - 1);
                if (args[3].equals("item") && Material.getMaterial(value) == null) {
                    p.sendMessage(Main.PRE + "§cBitte gib einen richtigen Itemstack ein\n" +
                            "§3hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
                    return;
                }
                if(args[2].equals("priority")) if (isNoValidPriority(value, p)) return;
                if(!mySQL.exists("prefixes", "name", args[2])) {
                    p.sendMessage(Main.PRE + "§cPräfix §6" + args[2] + " §cexistiert nicht");
                    return;
                }
                if(mySQL.edit("prefixes", "name", args[2], args[3], value)) {
                    p.sendMessage(Main.PRE + "§3" + args[3] + " §avon Präfix §6" + args[2] + " §awurde zu §3" + value + " §ageändert");
                } else
                    error(p);
            }
            default -> p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix prefix [§enew§6/§edelete§6/§eedit§6]\n" +
                    " §7-> §bnew §7- §9Erstellt einen neuen Präfix\n" +
                    " §7-> §bdelete §7- §9Entfertn einen Präfix\n" +
                    " §7-> §bedit §7- §9Bearbeiten eines Präfixes");
        }
    }

    private void rank(Player p, String[] args) {
        switch(args[1]) {
            case "new" -> {
                if (args.length != 5) {
                    p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix rank new <§ename§6> <§eprefix1§6,§eprefix2§6,§e...§6> <§epriority§6>" +
                            "§3(Der Name des Ranges darf keine Leerzeichen enthalten)");
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
                    p.sendMessage(Main.PRE + "§aRang §6" + args[2] + " §ahinzugefügt");
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
                String oldPrefixes =  mySQL.getFrom("ranks", "name", args[2], "prefixes");
                if(oldPrefixes == null) {
                    error(p);
                    return;
                }
                String prefixes = joinPrefixes(args[3], oldPrefixes);
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
                String oldPrefixes =  mySQL.getFrom("rank", "name", args[2], "prefixes");
                if(oldPrefixes == null) {
                    error(p);
                    return;
                }
                String[] prefixes = oldPrefixes.split(",");
                for (int i = 0; i < prefixes.length; i++) {
                    for (String s : args[3].split(",")) {
                        if (prefixes[i].equals(s)) {
                            prefixes[i] = null;
                            break;
                        }
                    }
                }
                StringBuilder prefixesR = new StringBuilder();
                for (String s : prefixes) {
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
            default -> p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix rank [§enew§6/§edelete§6/§eaddprefix§6/§eremoveprefix§6/§eedit§6]\n" +
                    " §7-> §bnew §7- §9Erstellt einen neuen Rang\n" +
                    " §7-> §bdelete §7- §9Entfertn einen Rang\n" +
                    " §7-> §baddprefix §7- §9CFügt Präfixe zu einem Rang hinzu\n" +
                    " §7-> §bremoveprefix §7- §9Entfernt Präfixe von einem Rang\n" +
                    " §7-> §bedit §7- §9Bearbeiten eines Rangs");
        }
    }

    private void player(Player p, String[] args, Player player) {
        switch(args[2]) {
            case "setprefix" -> {
                if (args.length != 4) {
                    p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix player <§eplayer§6> setprefix <§erank§6>");
                    return;
                }
                if(!mySQL.exists("prefixes", "name", args[3])) {
                    p.sendMessage(Main.PRE + "§cPräfix §6" + args[3] + " §cexistiert nicht");
                    return;
                }
                if(mySQL.editPlayer(player, "prefix", args[3])) {
                    p.sendMessage("§aPräfix von§b" + args[1] + " §awurde auf §6" + args[3] + " §agesetzt");
                }
                else
                    error(p);
            }
            case "setrank" -> {
                if (args.length != 4) {
                    p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix player <§eplayer§6> setrank <§erank§6>");
                    return;
                }
                if(!mySQL.exists("ranks", "name", args[3])) {
                    p.sendMessage(Main.PRE + "§cRang §6" + args[3] + " §cexistiert nicht");
                    return;
                }
                if(mySQL.editPlayer(player, "rank", args[3])) {
                    p.sendMessage("§aRang von§b" + args[1] + " §awurde auf §6" + args[3] + " §agesetzt");
                } else
                    error(p);
            }
            case "addprefix" -> {
                if (args.length != 4) {
                    p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix player <§ename§6> addprefix <§eprefix1§6,§eprefix2§6,§e...§6>");
                    return;
                }
                String notExistentPrefix1 = prefixNotExists(args[3]);
                if (notExistentPrefix1 != null) {
                    p.sendMessage(Main.PRE + "§cPräfix §6" + notExistentPrefix1 + " §cexistiert nicht");
                    return;
                }
                String oldPrefixes =  mySQL.getFromPlayer(player, "prefixes");
                if(oldPrefixes == null) {
                    error(p);
                    return;
                }
                String prefixes = joinPrefixes(args[3], oldPrefixes);
                if (mySQL.editPlayer(player, "prefixes", prefixes)) {
                    p.sendMessage(Main.PRE + "§aPräfix(e) §3" + args[3] + " §azu Spieler*in §6" + args[1] + " §ahinzugefügt\n" +
                            "§3(" + prefixes + ")");
                } else
                    error(p);
            }
            case "removeprefix" -> {
                if (args.length != 4) {
                    p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix player <§ename§6> removeprefix <§eprefix1§6,§eprefix2§6,§e...§6>");
                    return;
                }
                String oldPrefixes =  mySQL.getFromPlayer(player, "prefixes");
                if(oldPrefixes == null) {
                    error(p);
                    return;
                }
                String[] prefixes = oldPrefixes.split(",");
                for (int i = 0; i < prefixes.length; i++) {
                    for (String s : args[3].split(",")) {
                        if (prefixes[i].equals(s)) {
                            prefixes[i] = null;
                            break;
                        }
                    }
                }
                StringBuilder prefixesR = new StringBuilder();
                for (String s : prefixes) {
                    if (s != null) {
                        prefixesR.append(",").append(s);
                    }
                }
                prefixesR.delete(0, 1);
                if (mySQL.editPlayer(player, "prefixes", prefixesR.toString())) {
                    p.sendMessage(Main.PRE + "§aPräfix(e) §3" + args[3] + " §avon Spieler*in §6" + args[1] + " §aentfernt\n" +
                            "§3(" + prefixesR + ")");
                } else
                    error(p);

            }
            default -> p.sendMessage(Main.PRE + "§9Bitte benutze §6/toggleprefix player [§esetrank§6/§esetprefix§6/§eaddprefix§6/§eremoveprefix§6]\n" +
                    " §7-> §bsetrank §7- §9Setzt den Rang eines Spielers\n" +
                    " §7-> §bsetprefix §7- §9Setzt den Präfix eines Spielers\n" +
                    " §7-> §baddprefix §7- §9Fügt verfügbare Präfixe für den Spieler hinzu\n" +
                    " §7-> §bremoveprefix §7- §9Entfernt verfügbare Präfixe für den Spieler");
        }
    }

    public boolean isNoValidPriority(String priority, Player p) {
        int prio;
        try {
            prio = Integer.parseInt(priority);
        } catch(NumberFormatException e) {
            p.sendMessage(Main.PRE + "§cPriorität muss eine Zahl sein");
            return true;
        }
        if(prio > 999 || prio < 101) {
            p.sendMessage(Main.PRE + "§cPriorität muss zwischen 101 und 999 sein");
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

    public boolean isNotPermit(Player sender, String permission, boolean msg) {
        if (!sender.hasPermission(permission)) {
            if (msg) sender.sendMessage(Main.PRE + "§cDas darfst du nicht");
            return true;
        }
        return false;
    }

}

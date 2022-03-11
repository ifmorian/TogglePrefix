package de.felix_kurz.toggleprefix.commands;

import de.felix_kurz.toggleprefix.configuration.ConfigManager;
import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TogglePrefixCommand implements CommandExecutor {

    private ConfigManager cfgM;

    public TogglePrefixCommand(ConfigManager cfgM) {
        this.cfgM = cfgM;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Main.PRE + "§9Use §6/toggleprefix help §9for more information.");
            return false;
        }
        if (args[0].equals("gui")) {

        } else if (args[0].equals("set")) {
            if(args.length != 2) {
                sender.sendMessage(Main.PRE + "§9Use §6/toggleprefix set <prefix>");
                return false;
            }

        } else if (args[0].equals("new")) {
            if(args.length != 5) {
                sender.sendMessage(Main.PRE + "§9Use §6/toggleprefix new <name> <chat-prefix> <tablist-prefix> <itemstack>\n" +
                        " §7- §bname§9: The name of the prefix\n" +
                        " §7- §bchat-prefix§9: The prefix for chat messages. Use §6%name% where the player's name will be inserted" +
                        " §7- §btablist-prefix§9: The prefix in the tablist. User §6%name% for the player's name  and %ping% for the ping" +
                        " $7- §bitemstack§9: The icon in the gui interface (use spigot itemstacks names)");
                return false;
            }
        }
        return false;
    }
}

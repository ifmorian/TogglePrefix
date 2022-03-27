package de.felix_kurz.toggleprefix.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TogglePrefixTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("prefix");
            arguments.add("rank");
            arguments.add("player");
            arguments.add("manageprefixes");
            arguments.add("manageranks");
            for (String s : arguments) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) result.add(s);
            }
            return result;
        } else if (args.length == 2) {
            switch (args[0]) {
                case "prefix" -> {
                    List<String> arguments = new ArrayList<>();
                    arguments.add("new"); arguments.add("delete"); arguments.add("edit");
                    for (String s : arguments) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) result.add(s);
                    }
                    return arguments;
                }
                case "rank" -> {
                    List<String> arguments = new ArrayList<>();
                    arguments.add("new"); arguments.add("delete"); arguments.add("editname"); arguments.add("addprefix"); arguments.add("removeprefix");
                    for (String s : arguments) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) result.add(s);
                    }
                    return arguments;
                }
            }
        } else if (args.length == 3) {
            if (args[0].equals("player")) {
                List<String> arguments = new ArrayList<>();
                arguments.add("setrank"); arguments.add("setprefix"); arguments.add("addprefix"); arguments.add("removeprefix");
                for (String s : arguments) {
                    if (s.toLowerCase().startsWith(args[1].toLowerCase())) result.add(s);
                }
                return arguments;
            }
        } else if (args.length == 4) {
            if (args[0].equals("prefix") && args[1].equals("edit")) {
                List<String> arguments = new ArrayList<>();
                arguments.add("name"); arguments.add("display"); arguments.add("chat"); arguments.add("tablist"); arguments.add("item"); arguments.add("priority");
                for (String s : arguments) {
                    if (s.toLowerCase().startsWith(args[1].toLowerCase())) result.add(s);
                }
                return arguments;
            }
        }

        return null;
    }

}

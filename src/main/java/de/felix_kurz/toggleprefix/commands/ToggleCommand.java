package de.felix_kurz.toggleprefix.commands;

import de.felix_kurz.toggleprefix.configuration.ConfigManager;
import de.felix_kurz.toggleprefix.main.Main;
import de.felix_kurz.toggleprefix.permissions.PermissionManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ToggleCommand implements CommandExecutor {

    private ConfigManager cfgM;
    private LuckPerms luckPerms;

    public ToggleCommand(ConfigManager cfgM) {
        this.cfgM = cfgM;
        this.luckPerms = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Main.PRE + "§cOnly for players");
            return false;
        }

        if(args.length != 1) {
            sender.sendMessage(Main.PRE + "§cUse §6/toggle <group;chatprefix;tabprefix>");
            return false;
        }

        String group = args[0].split(";")[0];
        String permission = "toggleprefix." + group;

        PermissionNode node = PermissionNode.builder(permission).build();

        if (!luckPerms.getPlayerAdapter(Player.class).getUser((Player) sender).getNodes().contains(node)) {
            sender.sendMessage(Main.PRE + "§cYou do not have the permission to edit the prefix of §b" + group + "§c.");
            return false;
        }
        luckPerms.getGroupManager().getGroup("test").data().add(Node.builder("").build());
        if(luckPerms.getGroupManager().getGroup(group).equals(null)) {
            sender.sendMessage(Main.PRE + "§5The group §b" + group + " §5 does not exist. Prefix was created regardless.");
        }

        List<String> prefixes = cfgM.getConfig().getStringList("prefixes");
        prefixes.add(String.join("", args));
        cfgM.getConfig().set("prefixes", prefixes);
        cfgM.save();

        return false;
    }

}

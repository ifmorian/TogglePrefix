package de.felix_kurz.toggleprefix.scoreboards;

import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.UUID;

public class ScoreboardManager {

    private Main plugin;

    private String[] teams;
    private Scoreboard sb;

    public ScoreboardManager(Main plugin, Scoreboard sb) {
        this.plugin = plugin;
        this.sb = sb;
    }

    public void update() {
        teams = plugin.getMysql().getTeams();
        for (Team team : sb.getTeams()) {
            team.unregister();
        }
        for (String t : teams) {
            sb.registerNewTeam(t);
        }
    }

    public void updatePlayer(UUID id) {
        Player p = Bukkit.getPlayer(id);
        for (String t : teams) {
            p.sendMessage(t);
        }
        p.getScoreboard().getTeam(plugin.getMysql().getTeam(p)).addEntry(p.getName());
    }

    public void animateTabs() {
        final int[] count1 = {0};
        final int[] count2 = {0};
        final List<String> headers = plugin.getCfgM().getTablistHeader();
        final List<String> footers = plugin.getCfgM().getTablistFooter();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if(count1[0] >= headers.size()) {
                count1[0] = 0;
            }
            if(count2[0] >= footers.size()) {
                count2[0] = 0;
            }
            String header = headers.get(count1[0])
                                .replace("&", "§")
                                .replace("%onlinePlayers%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                                .replace("%maxPlayers%", String.valueOf(Bukkit.getMaxPlayers()));
            String footer = footers.get(count2[0])
                    .replace("&", "§")
                    .replace("%onlinePlayers%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                    .replace("%maxPlayers%", String.valueOf(Bukkit.getMaxPlayers()));

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.setPlayerListHeaderFooter(header, footer);
            }
            count1[0]++;
            count2[0]++;
        }, 0L, 60L);
    }

}

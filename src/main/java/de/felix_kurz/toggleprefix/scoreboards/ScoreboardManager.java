package de.felix_kurz.toggleprefix.scoreboards;

import de.felix_kurz.toggleprefix.databases.MySQL;
import de.felix_kurz.toggleprefix.main.Main;
import de.felix_kurz.toggleprefix.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

public class ScoreboardManager {

    private final Main plugin;
    private final MySQL mySQL;

    private final Scoreboard sb;

    public ScoreboardManager(Main plugin, Scoreboard sb) {
        this.plugin = plugin;
        this.sb = sb;
        mySQL = plugin.getMysql();
    }

    public void update() {
        String[] teams = plugin.getMysql().getTeams();
        if(teams == null) return;
        for (Team team : sb.getTeams()) {
            team.unregister();
        }
        for (String t : teams) {
            sb.registerNewTeam(t);
        }
    }

    public void updatePlayer(Player p) {
        p.getScoreboard().getTeam(Utils.convertToLetters(mySQL.getTeam(p))).addEntry(p.getName());
        String prefix = mySQL.getFromPlayer(p, "prefix");
        p.setPlayerListName(mySQL.getFrom("prefixes", "name", prefix, "tablist").replace("&", "§").replace("%name%", p.getName()));
    }

    public void updatePlayers() {
        if (plugin.getCfgM().autoUpdate()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                updatePlayer(p);
            }
        }
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

package de.felix_kurz.toggleprefix.scoreboards;

import de.felix_kurz.toggleprefix.databases.MySQL;
import de.felix_kurz.toggleprefix.main.Main;
import de.felix_kurz.toggleprefix.utils.Utils;
import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.api.statistic.types.DoubleStatistic;
import me.lucko.spark.api.statistic.types.GenericStatistic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

public class ScoreboardManager {

    private final Main plugin;
    private final MySQL mySQL;

    private final Scoreboard sb;

    private final Spark spark;

    public ScoreboardManager(Main plugin, Scoreboard sb) {
        this.plugin = plugin;
        this.sb = sb;
        mySQL = plugin.getMysql();
        this.spark = SparkProvider.get();
        runUpdate();
    }

    public void update() {
        if(mySQL.getConn() == null) return;
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
        p.setPlayerListName(mySQL.getFrom(MySQL.prefixesTable, "name", mySQL.getFromPlayer(p, "prefix"), "tablist").replace("&", "§").replace("%name%", p.getName()).replace("%ping%", String.valueOf(p.getPing())));
    }

    public void updatePlayers() {
        if (plugin.getCfgM().autoUpdate()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                updatePlayer(p);
            }
        }
    }

    public void runUpdate() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updatePlayers();
            }
        }.runTaskTimerAsynchronously(plugin, 0, 150L);
    }

    public void animateTabs() {
        if(mySQL.getConn() == null) return;
        final int[] count1 = {0};
        final int[] count2 = {0};
        final List<String> headers = plugin.getCfgM().getTablistHeader();
        final List<String> footers = plugin.getCfgM().getTablistFooter();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            DoubleStatistic<StatisticWindow.TicksPerSecond> tps = spark.tps();
            double tpsLast10Secs = ((double) Math.round(tps.poll(StatisticWindow.TicksPerSecond.SECONDS_10) * 10)) / 10;
            DoubleStatistic<StatisticWindow.CpuUsage> cpuUsage = spark.cpuSystem();
            double usagelastMin = ((double) Math.round(cpuUsage.poll(StatisticWindow.CpuUsage.MINUTES_1) * 1000)) / 10;
            if(count1[0] >= headers.size()) {
                count1[0] = 0;
            }
            if(count2[0] >= footers.size()) {
                count2[0] = 0;
            }
            String header = headers.get(count1[0])
                                .replace("&", "§")
                                .replace("%onlinePlayers%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                                .replace("%maxPlayers%", String.valueOf(Bukkit.getMaxPlayers()))
                                .replace("%tps%", String.valueOf(tpsLast10Secs))
                                .replace("%cpu%", String.valueOf(usagelastMin));
            String footer = footers.get(count2[0])
                    .replace("&", "§")
                    .replace("%onlinePlayers%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                    .replace("%maxPlayers%", String.valueOf(Bukkit.getMaxPlayers()))
                    .replace("%tps%", String.valueOf(tpsLast10Secs))
                    .replace("%cpu%", String.valueOf(usagelastMin));

            for (Player p : Bukkit.getOnlinePlayers()) {
                String s = header.replace("%ping%", String.valueOf(p.getPing()));
                String t = footer.replace("%ping%", String.valueOf(p.getPing()));
                p.setPlayerListHeaderFooter(s, t);
            }
            count1[0]++;
            count2[0]++;
        }, 0L, 60L);
    }

}

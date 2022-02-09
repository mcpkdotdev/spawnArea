package com.mcpkdot.spawnarea;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.mcpkdot.spawnarea.TextHandler.*;
import static java.lang.Integer.parseInt;

public class SpawnArea extends JavaPlugin implements Listener {
    private final HashMap<String, Area> AreaMap = new HashMap<>();
    private final HashMap<UUID, PosPlayer> PlayerMap = new HashMap<>();



    @Override
    public void onEnable() {
        System.out.println("Starting plugin spawnArea!");
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new Area(Bukkit.getWorlds().get(0).getSpawnLocation(), Bukkit.getWorlds().get(0).getSpawnLocation(), null, 0, 0),this);

        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
                    BufferedReader br = null;
                    try {

                        // create file object
                        File file = new File(this.getDataFolder() + File.separator + "areas.yml");

                        // create BufferedReader object from the File
                        br = new BufferedReader(new FileReader(file));

                        String line;

                        // read file line by line
                        while ((line = br.readLine()) != null) {

                            // split the line by :
                            String[] parts = line.split(":");

                            String name = parts[0].trim();
                            String area = parts[1].trim();

                            // put name, number in HashMap if they are
                            // not empty
                            if (!name.equals("") && !area.equals("")) {
                                AreaMap.put(name, getAreaFromText(area));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {

                        // Always close the BufferedReader
                        if (br != null) {
                            try {
                                br.close();
                            } catch (Exception ignored) {
                            }
                        }
                    }
                },1000L);



        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for(Area temp : AreaMap.values()) {
                temp.spawnEntity();
            }
        }, 0L, 400L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        try (BufferedWriter bf = new BufferedWriter(new FileWriter(this.getDataFolder() + File.separator + "areas.yml"))) {

            // create new BufferedWriter for the output file

            // iterate map entries
            for (Map.Entry<String, Area> entry : AreaMap.entrySet()) {

                // put key and value separated by a colon
                bf.write(entry.getKey() + ":"
                        + getStringFromArea(entry.getValue()));

                // new line
                bf.newLine();
            }
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // always close the writer

        AreaMap.clear();
        PlayerMap.clear();


    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        PlayerMap.put(e.getPlayer().getUniqueId(), new PosPlayer(e.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        PlayerMap.remove(e.getPlayer().getUniqueId());
    }

    public void newArea(String regionName, Area area){
        AreaMap.put(regionName, area);
    }

    public void removeArea(String regionName){ AreaMap.remove(regionName); }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e){
        String message = e.getMessage();
        String[] array = message.split(" ");
        array[0] = array[0].toLowerCase(Locale.ROOT);
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        PosPlayer posP = PlayerMap.get(uuid);
        Location loc = p.getLocation();
        if(p.isOp()){
            switch(array[0]) {
                case "/point1":
                    posP.setLoc1(loc);
                    p.sendMessage(String.format("Set Point 1 to %s, %s, %s", loc.getX(), loc.getY(), loc.getZ()));
                    e.setCancelled(true);
                    break;
                case "/point2":
                    posP.setLoc2(loc);
                    p.sendMessage(String.format("Set Point 2 to %s, %s, %s", loc.getX(), loc.getY(), loc.getZ()));
                    e.setCancelled(true);
                    break;

                case "/setspawnregion":
                    e.setCancelled(true);
                    if(posP.getLoc1() != null && posP.getLoc2() != null) {
                        if (array.length == 5) {
                            if (!AreaMap.containsKey(array[1])) {
                                try {
                                    int spawnSpeed = parseInt(array[3]);
                                    int mobLimit = parseInt(array[4]);
                                    newArea(array[1], new Area(posP.getLoc1(), posP.getLoc2(), getEntityList(array[2]), spawnSpeed, mobLimit));
                                    Area temp1 = AreaMap.get(array[1]);
                                    p.sendMessage("Area created in " + posP.getLoc1() + " point 1, " + posP.getLoc2() + " point 2 with spawn speed " + spawnSpeed + " with spawning mobs " + Arrays.toString(temp1.getEt()) + " with mob limit " + mobLimit);
                                } catch (NumberFormatException exception) {
                                    p.sendMessage("put only number in speed and limit.");
                                }
                            } else {
                                p.sendMessage("The region is already created. create another!");
                            }
                        } else {
                            p.sendMessage("/setspawnregion <areaname> <entityList, separate with commas & no space> <speed> <limit>");
                        }
                    } else{
                        p.sendMessage("Set two locations with /point1 and /point2 first.");
                    }
                    break;
                case "/removespawnregion":
                    e.setCancelled(true);
                    if (array.length == 2){
                        if(AreaMap.containsKey(array[1])){
                            removeArea(array[1]);
                        } else{
                            p.sendMessage("That area doesn't exist. /regionList");
                        }
                    } else{
                        p.sendMessage("/removespwanregion <areaname>");
                    }
                    break;
                case "/regionlist":
                    e.setCancelled(true);
                    StringBuilder templongstring = new StringBuilder();
                    for(String temp : AreaMap.keySet()){
                        templongstring.append(temp).append(", ");
                    }
                    p.sendMessage(templongstring.toString());

            }
        } else{
            p.sendMessage("No Perms. Contact someone to fix this if you are an moderator. If you are not a moderator, this command is very important since it changes the mob's spawn area and it creates region so please don't try to touch this. Seriously, this command is kinda dangerous to give to normal players because you can just set the spawn mob to ender dragon and the server will be two bee two tea and yea. Sorry for long text but it is worth it ig but like please don't try to run this command if you are not OP. It is not cool lol. Should this text be removed? ask owner. Contact mcpkdot for more infos, goodbye! Basically banana is the best food no cap.");
        }
    }
}

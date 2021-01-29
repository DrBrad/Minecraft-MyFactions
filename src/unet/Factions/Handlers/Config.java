package unet.Factions.Handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static unet.Factions.Main.plugin;

public class Config {

    private static Location spawn;
    private static HashMap<String, Location> warps = new HashMap<>();
    private static HashMap<Player, Player> playerTeleport = new HashMap<>();
    public static HashMap<Player, Long> wildDelayed = new HashMap<>();

    private static int teleportDelay = 2,
            wildRadius = 5000,
            wildDelay = 600,
            createPower = 10,
            claimCost = 2,
            createWarpCost = 2,
            deathToll = 1,
            joinPower = 10,
            periodicIncrease = 1,
            periodicTime = 1800;

    private static boolean wildTeleport = true,
            backTeleport = true,
            homeTeleport = true,
            factionHome = true,
            factionWarp = true;

    public Config(){
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdirs();
        }

        try{
            File configFile = new File(plugin.getDataFolder()+File.separator+"config.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

            if(configFile.exists()){
                teleportDelay = config.getInt("teleportation.delay");
                wildTeleport = config.getBoolean("teleportation.wild");
                backTeleport = config.getBoolean("teleportation.back");
                homeTeleport = config.getBoolean("teleportation.home");
                wildRadius = config.getInt("teleportation.wild-radius");
                wildDelay = config.getInt("teleportation.wild-delay");

                createPower = config.getInt("faction.create-power");
                claimCost = config.getInt("faction.claim-cost");
                createWarpCost = config.getInt("faction.create-warp-cost");
                deathToll = config.getInt("faction.death-toll");
                joinPower = config.getInt("faction.join-power");
                periodicIncrease = config.getInt("faction.periodic-power-increase");
                periodicTime = config.getInt("faction.periodic-increase-time");
                factionHome = config.getBoolean("faction.home");
                factionWarp = config.getBoolean("faction.warp");

                if(config.contains("spawn")){
                    spawn = new Location(Bukkit.getWorld(config.getString("spawn.world")),
                            config.getDouble("spawn.x"),
                            config.getDouble("spawn.y"),
                            config.getDouble("spawn.z"),
                            (float)config.getDouble("spawn.yaw"),
                            (float)config.getDouble("spawn.pitch"));
                }

            }else{
                config.options().header("teleportation.delay teleportation.wild-delay and faction.periodic-increase-time are in seconds.");
                config.set("teleportation.delay", 2);
                config.set("teleportation.wild", true);
                config.set("teleportation.wild-delay", 600);
                config.set("teleportation.back", true);
                config.set("teleportation.home", true);
                config.set("teleportation.wild-radius", 5000);

                config.set("faction.create-power", 10);
                config.set("faction.claim-cost", 2);
                config.set("faction.create-warp-cost", 2);
                config.set("faction.death-toll", 1);
                config.set("faction.join-power", 10);
                config.set("faction.periodic-power-increase", 1);
                config.set("faction.periodic-increase-time", 1800);
                config.set("faction.home", true);
                config.set("faction.warp", true);

                config.save(configFile);
            }

            File warpsFile = new File(plugin.getDataFolder()+File.separator+"warps.yml");
            config = YamlConfiguration.loadConfiguration(warpsFile);

            for(String warpKey : config.getKeys(false)){
                Location location = new Location(Bukkit.getWorld(config.getString(warpKey+".world")),
                        config.getDouble(warpKey+".x"),
                        config.getDouble(warpKey+".y"),
                        config.getDouble(warpKey+".z"),
                        (float)config.getDouble(warpKey+".yaw"),
                        (float)config.getDouble(warpKey+".pitch"));

                warps.put(warpKey, location);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static int getTeleportDelay(){
        return teleportDelay*20;
    }

    public static int getWildRadius(){
        return wildRadius;
    }

    public static int getCreatePower(){
        return createPower;
    }

    public static int getClaimCost(){
        return claimCost;
    }

    public static int getCreateWarpCost(){
        return createWarpCost;
    }

    public static int getDeathToll(){
        return deathToll;
    }

    public static int getJoinPower(){
        return joinPower;
    }

    public static int getPeriodicIncrease(){
        return periodicIncrease;
    }

    public static int getPeriodicTime(){
        return periodicTime*20;
    }

    public static int getWildDelay(){
        return wildDelay*1000;
    }

    public static boolean isWildTeleport(){
        return wildTeleport;
    }

    public static boolean isBackTeleport(){
        return backTeleport;
    }

    public static boolean isHomeTeleport(){
        return homeTeleport;
    }

    public static boolean isFactionHome(){
        return factionHome;
    }

    public static boolean isFactionWarp(){
        return factionWarp;
    }

    public static void setSpawn(Location location){
        spawn = location;
        writeConfig();
    }

    public static Location getSpawn(){
        return spawn;
    }

    public static Location getWarp(String name){
        if(warps.containsKey(name.toLowerCase())){
            return warps.get(name.toLowerCase());
        }
        return null;
    }

    public static ArrayList<String> getWarps(){
        return new ArrayList<>(warps.keySet());
    }

    public static void setWarp(String name, Location location){
        warps.put(name.toLowerCase(), location);
        writeWarps();
    }

    public static void removeWarp(String name){
        if(warps.containsKey(name.toLowerCase())){
            warps.remove(name.toLowerCase());
            writeWarps();
        }
    }

    public static boolean isWarp(String name){
        return warps.containsKey(name.toLowerCase());
    }

    public static HashMap<Player, Integer> delayedTask = new HashMap<>();

    public static void setPlayerTeleportTask(Player player, int task){
        if(delayedTask.containsKey(player)){
            plugin.getServer().getScheduler().cancelTask(delayedTask.get(player));
        }
        delayedTask.put(player, task);
    }

    public static void removePlayerTeleportTask(Player player){
        if(delayedTask.containsKey(player)){
            plugin.getServer().getScheduler().cancelTask(delayedTask.get(player));
            delayedTask.remove(player);
        }
    }

    public static void removePlayerTeleport(Player player){
        if(playerTeleport.containsKey(player)){
            playerTeleport.remove(player);
        }
    }

    public static void setPlayerTeleport(Player sender, Player receiver){
        //if(playerTeleport.containsKey(sender)){
            playerTeleport.put(receiver, sender);
        //}
    }

    public static boolean hasPlayerTeleport(Player player){
        return playerTeleport.containsKey(player);
    }

    public static Player getPlayerTeleport(Player player){
        if(playerTeleport.containsKey(player)){
            return playerTeleport.get(player);
        }
        return null;
    }

    public static void setWildDelayed(Player player){
        wildDelayed.put(player, new Date().getTime()+getWildDelay());
    }

    public static boolean isWildDelayed(Player player){
        if(wildDelayed.containsKey(player)){
            if(wildDelayed.get(player) > new Date().getTime()){
                return true;
            }
        }
        return false;
    }

    private static void writeConfig(){
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdirs();
        }

        try{
            File configFile = new File(plugin.getDataFolder()+File.separator+"config.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

            if(spawn != null){
                config.set("spawn.world", spawn.getWorld().getName());
                config.set("spawn.x", spawn.getX());
                config.set("spawn.y", spawn.getY());
                config.set("spawn.z", spawn.getZ());
                config.set("spawn.yaw", spawn.getYaw());
                config.set("spawn.pitch", spawn.getPitch());
            }

            config.save(configFile);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void writeWarps(){
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdirs();
        }

        try{
            File warpsFile = new File(plugin.getDataFolder()+File.separator+"warps.yml");
            if(warpsFile.exists()){
                OutputStream out = new FileOutputStream(warpsFile);
                out.flush();
                out.close();
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(warpsFile);

            for(String warpKey : warps.keySet()){
                config.set(warpKey+".world", warps.get(warpKey).getWorld().getName());
                config.set(warpKey+".x", warps.get(warpKey).getX());
                config.set(warpKey+".y", warps.get(warpKey).getY());
                config.set(warpKey+".z", warps.get(warpKey).getZ());
                config.set(warpKey+".yaw", warps.get(warpKey).getYaw());
                config.set(warpKey+".pitch", warps.get(warpKey).getPitch());
            }

            config.save(warpsFile);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

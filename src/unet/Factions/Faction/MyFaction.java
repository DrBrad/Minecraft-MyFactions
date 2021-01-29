package unet.Factions.Faction;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static unet.Factions.Faction.FactionHandler.*;
import static unet.Factions.Handlers.Config.*;
import static unet.Factions.Main.plugin;

public class MyFaction implements Faction {

    private HashMap<String, Location> warps = new HashMap<>();
    private JSONObject ranks = new JSONObject();
    private String name, description;
    private int power = 10, color = 12;
    private UUID key;
    private Location home;

    public void init(String key){
        read(key);
    }

    public MyFaction create(String name, UUID uuid){
        if(!isFaction(name)){
            if(!isPlayerInFaction(uuid)){
                this.name = name;
                ranks.put(uuid.toString(), 3);
                key = UUID.randomUUID();
                power = getCreatePower();
                writeData();
                writeRanks();
                return this;
            }
        }
        return null;
    }

    public boolean rename(String name, UUID uuid){
        if(!isFaction(name)){
            if(ranks.has(uuid.toString())){
                if(ranks.getInt(uuid.toString()) == 3){
                    this.name = name;
                    writeData();
                    return true;
                }
            }
        }
        return false;
    }

    public void changeOwnership(UUID sender, UUID receiver){
        if(ranks.has(sender.toString()) && ranks.has(receiver.toString())){
            if(ranks.getInt(sender.toString()) == 3){
                ranks.put(receiver.toString(), 3);
                ranks.put(sender.toString(), 2);
                writeRanks();
            }
        }
    }

    public boolean disband(UUID uuid){
        if(ranks.has(uuid.toString())){
            if(ranks.getInt(uuid.toString()) == 3){
                File factionsFolder = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+key);

                if(factionsFolder.exists()){
                    factionsFolder.delete();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canClaim(UUID uuid){
        if(ranks.has(uuid.toString())){
            if(ranks.getInt(uuid.toString()) > 1){
                return true;
            }
        }
        return false;
    }

    public boolean canInvite(UUID sender, UUID receiver){
        if(ranks.has(sender.toString()) && ranks.has(receiver.toString()) && !isPlayerInFaction(receiver)){
            if(ranks.getInt(sender.toString()) > 0){
                return true;
            }
        }
        return false;
    }

    public boolean canBuild(UUID uuid){
        if(ranks.has(uuid.toString())){
            if(ranks.getInt(uuid.toString()) > 0){
                return true;
            }
        }
        return false;
    }

    public void join(UUID uuid){
        ranks.put(uuid.toString(), 0);
        power += getJoinPower();
        writeRanks();
        writeData();
    }

    public boolean leave(UUID uuid){
        if(ranks.has(uuid.toString())){
            if(ranks.getInt(uuid.toString()) != 3){
                ranks.remove(uuid.toString());
                power -= getJoinPower();
                writeRanks();
                writeData();
                return true;
            }
        }
        return false;
    }

    public boolean remove(UUID sender, UUID receiver){
        if(ranks.has(sender.toString()) && ranks.has(receiver.toString())){
            if(ranks.getInt(sender.toString()) > ranks.getInt(receiver.toString()) && ranks.getInt(receiver.toString()) != 3){
                ranks.remove(receiver.toString());
                power -= getJoinPower();
                writeRanks();
                writeData();
                return true;
            }
        }
        return false;
    }

    public boolean promote(UUID sender, UUID receiver){
        if(ranks.has(sender.toString()) && ranks.has(receiver.toString())){
            if(ranks.getInt(sender.toString())-1 > ranks.getInt(receiver.toString()) && ranks.getInt(receiver.toString()) != 3){
                ranks.put(receiver.toString(), ((Long) ranks.get(receiver.toString())).intValue()+1);
                writeRanks();
                return true;
            }
        }
        return false;
    }

    public boolean demote(UUID sender, UUID receiver){
        if(ranks.has(sender.toString()) && ranks.has(receiver.toString())){
            if(ranks.getInt(sender.toString())-1 > ranks.getInt(receiver.toString()) && ranks.getInt(receiver.toString()) != 3){
                ranks.put(receiver.toString(), ((Long) ranks.get(receiver.toString())).intValue()-1);
                writeRanks();
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getPlayers(){
        return new ArrayList<>(ranks.keySet());
    }

    @Override
    public UUID getKey(){
        return key;
    }

    @Override
    public String getName(){
        return name;
    }

    public boolean setDescription(UUID uuid, String description){
        if(ranks.has(uuid.toString())){
            if(ranks.getInt(uuid.toString()) > 1){
                this.description = description;
                writeData();
                return true;
            }
        }
        return false;
    }

    public String getDescription(){
        return (description == null) ? "Has no description." : description;
    }

    @Override
    public int getType(){
        return 0;
    }

    public int getRank(UUID uuid){
        if(ranks.has(uuid.toString())){
            return ranks.getInt(uuid.toString());
        }
        return -1;
    }

    public void setPower(int power){
        this.power = power;
        writeData();
    }

    public int getPower(){
        return power;
    }

    public boolean setColor(UUID uuid, int color){
        if(ranks.has(uuid.toString())){
            if(ranks.getInt(uuid.toString()) > 1){
                this.color = color;
                writeData();
                return true;
            }
        }
        return false;
    }

    @Override
    public int getColor(){
        return color;
    }

    public void setHome(Location home){
        this.home = home;
        writeData();
    }

    public Location getHome(){
        return home;
    }

    public Location getWarp(String name){
        if(warps.containsKey(name.toLowerCase())){
            return warps.get(name.toLowerCase());
        }
        return null;
    }

    public ArrayList<String> getWarps(){
        return new ArrayList<>(warps.keySet());
    }

    public void setWarp(String name, Location location){
        warps.put(name.toLowerCase(), location);
        power -= getCreateWarpCost();
        writeWarps();
        writeData();
    }

    public void removeWarp(String name){
        if(warps.containsKey(name.toLowerCase())){
            warps.remove(name.toLowerCase());
            power += getCreateWarpCost();
            writeWarps();
            writeData();
        }
    }

    public boolean isWarp(String name){
        return warps.containsKey(name.toLowerCase());
    }

    public void read(String key){
        try{
            File factionsFolder = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+key);

            if(factionsFolder.exists()){
                File data = new File(factionsFolder.getPath()+File.separator+"data.yml");
                FileConfiguration config = YamlConfiguration.loadConfiguration(data);

                this.key = UUID.fromString(config.getString("key"));
                name = config.getString("name");
                color = config.getInt("color");
                power = config.getInt("power");
                description = config.getString("description");

                if(config.contains("home")){
                    String world = config.getString("home.world");
                    double x = config.getDouble("home.x");
                    double y = config.getDouble("home.y");
                    double z = config.getDouble("home.z");
                    float yaw = (float) config.getDouble("home.yaw");
                    float pitch = (float) config.getDouble("home.pitch");
                    home = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                }

                //READ ALL THE RANKS
                File ranksFile = new File(factionsFolder.getPath()+File.separator+"ranks.json");
                if(ranksFile.exists()){
                    ranks = new JSONObject(new JSONTokener(new FileInputStream(ranksFile)));
                }

                //READ ALL THE WARPS
                File warpsFile = new File(factionsFolder.getPath()+File.separator+"warps.yml");
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
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void writeData(){
        File factionsFolder = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+key);
        if(!factionsFolder.exists()){
            factionsFolder.mkdirs();
        }

        try{
            File data = new File(factionsFolder.getPath()+File.separator+"data.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(data);

            config.set("key", key.toString());
            config.set("name", name);
            config.set("color", color);
            config.set("power", power);
            config.set("description", description);

            if(home != null){
                config.set("home.world", home.getWorld().getName());
                config.set("home.x", home.getX());
                config.set("home.y", home.getY());
                config.set("home.z", home.getZ());
                config.set("home.yaw", home.getYaw());
                config.set("home.pitch", home.getPitch());
            }

            config.save(data);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void writeRanks(){
        File factionsFolder = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+key);
        if(!factionsFolder.exists()){
            factionsFolder.mkdirs();
        }

        try{
            FileWriter out = new FileWriter(new File(factionsFolder+File.separator+"ranks.json"));
            out.write(ranks.toString());
            out.flush();
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void writeWarps(){
        File factionsFolder = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+key);
        if(!factionsFolder.exists()){
            factionsFolder.mkdirs();
        }

        try{
            File warpsFile = new File(factionsFolder.getPath()+File.separator+"warps.yml");
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

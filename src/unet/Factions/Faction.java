package unet.Factions;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import static unet.Factions.Main.*;

public class Faction {

    private static HashMap<Player, String> players = new HashMap<>();
    private static HashMap<String, String> claims = new HashMap<>();

    //PERFECT
    public static String getFaction(Player player){
        if(players.containsKey(player)){
            return players.get(player);

        }else{
            File playersFolder = new File(plugin.getDataFolder()+File.separator+"players.yml");

            if(playersFolder.exists()){
                FileConfiguration config = YamlConfiguration.loadConfiguration(playersFolder);

                if(config.contains(player.getUniqueId().toString()) && !config.getString(player.getUniqueId().toString()).equalsIgnoreCase("null")){
                    players.put(player, config.getString(player.getUniqueId().toString()));
                    return config.getString(player.getUniqueId().toString());
                }
            }
            return null;
        }
    }

    public static void readAllClaims(){
        File factions = new File(plugin.getDataFolder()+File.separator+"factions");
        if(factions.exists()){
            for(File faction : factions.listFiles()){
                try{
                    File factionClaims = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+faction.getName()+File.separator+"claims.json");
                    if(factionClaims.exists()){
                        InputStream in = new FileInputStream(factionClaims);

                        String builder = "";
                        byte[] buffer = new byte[4096];
                        int length;
                        while((length = in.read(buffer)) > 0){
                            builder += new String(buffer, 0, length);
                        }

                        in.close();

                        JSONArray json = new JSONArray(builder);
                        for(int i = 0; i < json.length(); i++){
                            claims.put(json.getString(i), faction.getName());
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static String inClaim(Chunk chunk){
        if(claims.containsKey(chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ())){
            return claims.get(chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ());
        }
        return null;
    }

    public static int getFactionPower(String factionName){
        try{
            File factionData = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"data.yml");
            if(factionData.exists()){
                FileConfiguration config = YamlConfiguration.loadConfiguration(factionData);
                return config.getInt("power");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean setFactionPower(String factionName, int power){
        try{
            File factionData = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"data.yml");
            if(factionData.exists()){
                FileConfiguration config = YamlConfiguration.loadConfiguration(factionData);
                config.set("power", power);
                config.save(factionData);
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //PERFECT
    public static JSONObject getFactionPlayers(String factionName){
        try{
            File factionPlayers = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"players.json");
            if(factionPlayers.exists()){
                InputStream in = new FileInputStream(factionPlayers);

                String builder = "";
                byte[] buffer = new byte[4096];
                int length;
                while((length = in.read(buffer)) > 0){
                    builder += new String(buffer, 0, length);
                }

                in.close();

                return new JSONObject(builder);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    //PERFECT
    public static boolean createFaction(Player player, String factionName){
        File factionFolder = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName);
        File playersFolder = new File(plugin.getDataFolder()+File.separator+"players.yml");

        if(!factionFolder.exists()){
            factionFolder.mkdirs();
            try{
                File factionData = new File(factionFolder.getPath()+File.separator+"data.yml");
                FileConfiguration config = YamlConfiguration.loadConfiguration(factionData);
                config.set("power", 10);
                config.set("description", "§7No description set.");
                config.set("enter", "§7No entering text set.");
                config.set("leave", "§7No leaving text set.");
                config.save(factionData);

                File factionPlayers = new File(factionFolder.getPath()+File.separator+"players.json");
                JSONObject json = new JSONObject();

                JSONObject pson = new JSONObject();
                pson.put("p", 2);
                json.put(player.getUniqueId().toString(), pson);

                OutputStream out = new FileOutputStream(factionPlayers);
                out.write(json.toString().getBytes());
                out.flush();
                out.close();

                config = YamlConfiguration.loadConfiguration(playersFolder);
                config.set(player.getUniqueId().toString(), factionName);
                config.save(playersFolder);

                return true;
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return false;
    }

    //PERFECT
    public static void disbandFaction(String factionName){
        File playersFolder = new File(plugin.getDataFolder()+File.separator+"players.yml");

        if(playersFolder.exists()){
            FileConfiguration config = YamlConfiguration.loadConfiguration(playersFolder);
            JSONObject json = getFactionPlayers(factionName);

            Iterator<String> keys = json.keys();
            while(keys.hasNext()){
                String playerUUID = keys.next();
                config.set(playerUUID, "null");

                Player player = Bukkit.getPlayer(UUID.fromString(playerUUID));
                if(player != null && player.isOnline()){
                    player.sendMessage("§cYour faction was disbanded, you are no longer a part of a faction.");
                }

                if(players.containsKey(player)){
                    players.remove(player);
                }
            }

            try{
                config.save(playersFolder);
            }catch(Exception e){
                e.printStackTrace();
            }

            File factionsFolder = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName);
            if(factionsFolder.exists()){
                factionsFolder.delete();
            }
        }
    }

    //PERFECT
    public static void renameFaction(String factionName, String newFactionName){
        File playersFolder = new File(plugin.getDataFolder()+File.separator+"players.yml");

        if(playersFolder.exists()){
            FileConfiguration config = YamlConfiguration.loadConfiguration(playersFolder);
            JSONObject json = getFactionPlayers(factionName);

            Iterator<String> keys = json.keys();
            while(keys.hasNext()){
                String playerUUID = keys.next();
                config.set(playerUUID, newFactionName);

                Player player = Bukkit.getPlayer(UUID.fromString(playerUUID));
                if(player != null && player.isOnline()){
                    player.sendMessage("§7Your faction §c"+factionName+"§7 was renamed to §c"+newFactionName+"§7.");
                }

                if(players.containsKey(player)){
                    players.put(player, newFactionName);
                }
            }

            try{
                config.save(playersFolder);
            }catch(Exception e){
                e.printStackTrace();
            }

            File factionsFolder = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName);
            if(factionsFolder.exists()){
                factionsFolder.renameTo(new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+newFactionName));
            }

            for(String claim : claims.keySet()){
                if(claims.get(claim).equals(factionName)){
                    claims.put(claim, newFactionName);
                }
            }
        }
    }

    //PERFECT
    public static int getPlayerRank(Player player, String factionName){
        JSONObject players = getFactionPlayers(factionName);
        return players.getJSONObject(player.getUniqueId().toString()).getInt("p");
    }

    //PERFECT
    public static boolean addPlayerToFaction(Player player, String factionName){
        File playersFolder = new File(plugin.getDataFolder()+File.separator+"players.yml");
        File factionPlayers = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"players.json");

        if(factionPlayers.exists()){
            try{
                JSONObject json = getFactionPlayers(factionName);

                FileConfiguration config = YamlConfiguration.loadConfiguration(playersFolder);
                config.set(player.getUniqueId().toString(), factionName);
                config.save(playersFolder);

                JSONObject pson = new JSONObject();
                pson.put("p", 0);
                json.put(player.getUniqueId().toString(), pson);

                OutputStream out = new FileOutputStream(factionPlayers);
                out.write(json.toString().getBytes());
                out.flush();
                out.close();

                players.put(player, factionName);

                setFactionPower(factionName, getFactionPower(factionName)+10);

                return true;

            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    //PERFECT
    public static boolean removePlayerFromFaction(Player player, String factionName){
        File playersFolder = new File(plugin.getDataFolder()+File.separator+"players.yml");
        File factionPlayers = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"players.json");

        if(factionPlayers.exists()){
            try{
                JSONObject json = getFactionPlayers(factionName);

                FileConfiguration config = YamlConfiguration.loadConfiguration(playersFolder);
                config.set(player.getUniqueId().toString(), "null");
                config.save(playersFolder);

                json.remove(player.getUniqueId().toString());

                OutputStream out = new FileOutputStream(factionPlayers);
                out.write(json.toString().getBytes());
                out.flush();
                out.close();

                setFactionPower(factionName, getFactionPower(factionName)-10);

                if(players.containsKey(player)){
                    players.remove(player);
                }

                return true;

            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    //PERFECT
    public static boolean promotePlayer(Player player, String factionName){
        File factionPlayers = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"players.json");

        if(factionPlayers.exists()){
            try{
                InputStream in = new FileInputStream(factionPlayers);

                String builder = "";
                byte[] buffer = new byte[4096];
                int length;
                while((length = in.read(buffer)) > 0){
                    builder += new String(buffer, 0, length);
                }

                in.close();

                JSONObject json = new JSONObject(builder);
                json.getJSONObject(player.getUniqueId().toString()).put("p", json.getJSONObject(player.getUniqueId().toString()).getInt("p")+1);

                OutputStream out = new FileOutputStream(factionPlayers);
                out.write(json.toString().getBytes());
                out.flush();
                out.close();

                return true;

            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    //PERFECT
    public static boolean demotePlayer(Player player, String factionName){
        File factionPlayers = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"players.json");

        if(factionPlayers.exists()){
            try{
                InputStream in = new FileInputStream(factionPlayers);

                String builder = "";
                byte[] buffer = new byte[4096];
                int length;
                while((length = in.read(buffer)) > 0){
                    builder += new String(buffer, 0, length);
                }

                in.close();

                JSONObject json = new JSONObject(builder);
                json.getJSONObject(player.getUniqueId().toString()).put("p", json.getJSONObject(player.getUniqueId().toString()).getInt("p")-1);

                OutputStream out = new FileOutputStream(factionPlayers);
                out.write(json.toString().getBytes());
                out.flush();
                out.close();

                return true;

            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    //PERFECT
    public static boolean claimForFaction(String factionName, Chunk chunk, int power){
        try{
            File factionClaims = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"claims.json");
            JSONArray json;
            if(factionClaims.exists()){
                InputStream in = new FileInputStream(factionClaims);

                String builder = "";
                byte[] buffer = new byte[4096];
                int length;
                while((length = in.read(buffer)) > 0){
                    builder += new String(buffer, 0, length);
                }

                in.close();

                if(builder.contains(chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ())){
                    return true;
                }

                json = new JSONArray(builder);

            }else{
                json = new JSONArray();
            }

            json.put(chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ());

            OutputStream out = new FileOutputStream(factionClaims);
            out.write(json.toString().getBytes());
            out.flush();
            out.close();

            claims.put(chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ(), factionName);
            setFactionPower(factionName, power-2);

            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //PERFECT
    public static boolean unclaimForFaction(String factionName, Chunk chunk, int power){
        try{
            File factionClaims = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"claims.json");
            JSONArray json;
            if(factionClaims.exists()){
                InputStream in = new FileInputStream(factionClaims);

                String builder = "";
                byte[] buffer = new byte[4096];
                int length;
                while((length = in.read(buffer)) > 0){
                    builder += new String(buffer, 0, length);
                }

                in.close();

                if(!builder.contains(chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ())){
                    return true;
                }
                json = new JSONArray(builder);

            }else{
                json = new JSONArray();
            }

            for(int i = 0; i < json.length(); i++){
                if(json.getString(i).equals(chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ())){
                    json.remove(i);
                    break;
                }
            }

            OutputStream out = new FileOutputStream(factionClaims);
            out.write(json.toString().getBytes());
            out.flush();
            out.close();

            if(claims.containsKey(chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ())){
                claims.remove(chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ(), factionName);
            }

            setFactionPower(factionName, power+2);

            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //PERFECT
    public static void renewMappedLandscape(Player player, Chunk chunk){
        for(int x = 0; x < 16; x++){
            for(int y = 254; y > 0; y--){
                Block block = chunk.getBlock(x, y, 0);
                Block blockAbove = chunk.getBlock(x, y+1, 0);

                if(!transparentBlocks.contains(block.getType()) && transparentBlocks.contains(blockAbove.getType())){
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                    break;
                }
            }
        }

        for(int x = 0; x < 16; x++){
            for(int y = 254; y > 0; y--){
                Block block = chunk.getBlock(x, y, 15);
                Block blockAbove = chunk.getBlock(x, y+1, 15);

                if(!transparentBlocks.contains(block.getType()) && transparentBlocks.contains(blockAbove.getType())){
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                    break;
                }
            }
        }

        for(int z = 0; z < 16; z++){
            for(int y = 254; y > 0; y--){
                Block block = chunk.getBlock(0, y, z);
                Block blockAbove = chunk.getBlock(0, y+1, z);

                if(!transparentBlocks.contains(block.getType()) && transparentBlocks.contains(blockAbove.getType())){
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                    break;
                }
            }
        }

        for(int z = 0; z < 16; z++){
            for(int y = 254; y > 0; y--){
                Block block = chunk.getBlock(15, y, z);
                Block blockAbove = chunk.getBlock(15, y+1, z);

                if(!transparentBlocks.contains(block.getType()) && transparentBlocks.contains(blockAbove.getType())){
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                    break;
                }
            }
        }
    }
}

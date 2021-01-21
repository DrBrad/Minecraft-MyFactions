package unet.Factions;

import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
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

import static unet.Factions.Config.*;
import static unet.Factions.Main.*;

public class Faction {

    private static HashMap<UUID, String> players = new HashMap<>();
    private static HashMap<String, String> claims = new HashMap<>();

    //PERFECT
    public static String getFaction(UUID uuid){
        if(players.containsKey(uuid)){
            return players.get(uuid);

        }else{
            File playersFolder = new File(plugin.getDataFolder()+File.separator+"players.yml");

            if(playersFolder.exists()){
                FileConfiguration config = YamlConfiguration.loadConfiguration(playersFolder);

                if(config.contains(uuid.toString()) && !config.getString(uuid.toString()).equalsIgnoreCase("null")){
                    players.put(uuid, config.getString(uuid.toString()));
                    return config.getString(uuid.toString());
                }
            }
            return null;
        }
    }

    public static void readAllClaims(){
        try{
            File safeZone = new File(plugin.getDataFolder()+File.separator+"safe-zones.json");
            if(safeZone.exists()){
                InputStream in = new FileInputStream(safeZone);

                String builder = "";
                byte[] buffer = new byte[4096];
                int length;
                while((length = in.read(buffer)) > 0){
                    builder += new String(buffer, 0, length);
                }

                in.close();

                JSONArray json = new JSONArray(builder);
                for(int i = 0; i < json.length(); i++){
                    claims.put(json.getString(i), "Safe-Zone");
                }
            }

            File pvpZone = new File(plugin.getDataFolder()+File.separator+"pvp-zones.json");
            if(pvpZone.exists()){
                InputStream in = new FileInputStream(pvpZone);

                String builder = "";
                byte[] buffer = new byte[4096];
                int length;
                while((length = in.read(buffer)) > 0){
                    builder += new String(buffer, 0, length);
                }

                in.close();

                JSONArray json = new JSONArray(builder);
                for(int i = 0; i < json.length(); i++){
                    claims.put(json.getString(i), "Pvp-Zone");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

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
    public static boolean createFaction(UUID uuid, String factionName){
        File factionFolder = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName);
        File playersFolder = new File(plugin.getDataFolder()+File.separator+"players.yml");

        if(!factionFolder.exists()){
            factionFolder.mkdirs();
            try{
                File factionData = new File(factionFolder.getPath()+File.separator+"data.yml");
                FileConfiguration config = YamlConfiguration.loadConfiguration(factionData);
                config.set("power", factionCreatePower);
                config.set("description", "§7No description set.");
                config.set("enter", "§7No entering text set.");
                config.set("leave", "§7No leaving text set.");
                config.save(factionData);

                File factionPlayers = new File(factionFolder.getPath()+File.separator+"players.json");
                JSONObject json = new JSONObject();

                JSONObject pson = new JSONObject();
                pson.put("p", 3);
                json.put(uuid.toString(), pson);

                OutputStream out = new FileOutputStream(factionPlayers);
                out.write(json.toString().getBytes());
                out.flush();
                out.close();

                config = YamlConfiguration.loadConfiguration(playersFolder);
                config.set(uuid.toString(), factionName);
                config.save(playersFolder);

                players.put(uuid, factionName);

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

                OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(playerUUID));
                if(player != null && player.isOnline()){
                    player.getPlayer().sendMessage("§cYour faction was disbanded, you are no longer a part of a faction.");
                }

                if(players.containsKey(player.getUniqueId())){
                    players.remove(player.getUniqueId());
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

                OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(playerUUID));
                if(player != null && player.isOnline()){
                    player.getPlayer().sendMessage("§7Your faction §c"+factionName+"§7 was renamed to §c"+newFactionName+"§7.");
                }

                if(players.containsKey(player.getUniqueId())){
                    players.put(player.getUniqueId(), newFactionName);
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
    public static int getPlayerRank(UUID uuid, String factionName){
        JSONObject players = getFactionPlayers(factionName);
        return players.getJSONObject(uuid.toString()).getInt("p");
    }

    //PERFECT
    public static boolean addPlayerToFaction(UUID uuid, String factionName){
        File playersFolder = new File(plugin.getDataFolder()+File.separator+"players.yml");
        File factionPlayers = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"players.json");

        if(factionPlayers.exists()){
            try{
                JSONObject json = getFactionPlayers(factionName);

                FileConfiguration config = YamlConfiguration.loadConfiguration(playersFolder);
                config.set(uuid.toString(), factionName);
                config.save(playersFolder);

                JSONObject pson = new JSONObject();
                pson.put("p", 0);
                json.put(uuid.toString(), pson);

                OutputStream out = new FileOutputStream(factionPlayers);
                out.write(json.toString().getBytes());
                out.flush();
                out.close();

                players.put(uuid, factionName);

                setFactionPower(factionName, getFactionPower(factionName)+10);

                return true;

            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    //PERFECT
    public static boolean removePlayerFromFaction(UUID uuid, String factionName){
        File playersFolder = new File(plugin.getDataFolder()+File.separator+"players.yml");
        File factionPlayers = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"players.json");

        if(factionPlayers.exists()){
            try{
                JSONObject json = getFactionPlayers(factionName);

                FileConfiguration config = YamlConfiguration.loadConfiguration(playersFolder);
                config.set(uuid.toString(), "null");
                config.save(playersFolder);

                json.remove(uuid.toString());

                OutputStream out = new FileOutputStream(factionPlayers);
                out.write(json.toString().getBytes());
                out.flush();
                out.close();

                setFactionPower(factionName, getFactionPower(factionName)-10);

                if(players.containsKey(uuid)){
                    players.remove(uuid);
                }

                return true;

            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    //PERFECT
    public static boolean promotePlayer(UUID uuid, String factionName){
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
                json.getJSONObject(uuid.toString()).put("p", json.getJSONObject(uuid.toString()).getInt("p")+1);

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
    public static boolean demotePlayer(UUID uuid, String factionName){
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
                json.getJSONObject(uuid.toString()).put("p", json.getJSONObject(uuid.toString()).getInt("p")-1);

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
    public static boolean changeFactionOwnership(UUID uuid, UUID uuid1, String factionName){
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
                json.getJSONObject(uuid.toString()).put("p", 2);
                json.getJSONObject(uuid1.toString()).put("p", 3);

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
            setFactionPower(factionName, power-claimPower);

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
                claims.remove(chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ());
            }

            setFactionPower(factionName, power+claimPower);

            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //PERFECT
    public static boolean claimSafeZone(Chunk chunk){
        try{
            File safeZone = new File(plugin.getDataFolder()+File.separator+"safe-zones.json");
            JSONArray json;
            if(safeZone.exists()){
                InputStream in = new FileInputStream(safeZone);

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

            OutputStream out = new FileOutputStream(safeZone);
            out.write(json.toString().getBytes());
            out.flush();
            out.close();

            claims.put(chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ(), "Safe-Zone");

            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //PERFECT
    public static boolean unclaimSafeZone(Chunk chunk){
        try{
            File safeZone = new File(plugin.getDataFolder()+File.separator+"safe-zones.json");
            JSONArray json;
            if(safeZone.exists()){
                InputStream in = new FileInputStream(safeZone);

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

            OutputStream out = new FileOutputStream(safeZone);
            out.write(json.toString().getBytes());
            out.flush();
            out.close();

            if(claims.containsKey(chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ())){
                claims.remove(chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ());
            }

            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //PERFECT
    public static boolean claimPvpZone(Chunk chunk){
        try{
            File safeZone = new File(plugin.getDataFolder()+File.separator+"pvp-zones.json");
            JSONArray json;
            if(safeZone.exists()){
                InputStream in = new FileInputStream(safeZone);

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

            OutputStream out = new FileOutputStream(safeZone);
            out.write(json.toString().getBytes());
            out.flush();
            out.close();

            claims.put(chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ(), "Pvp-Zone");

            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //PERFECT
    public static boolean unclaimPvpZone(Chunk chunk){
        try{
            File safeZone = new File(plugin.getDataFolder()+File.separator+"pvp-zones.json");
            JSONArray json;
            if(safeZone.exists()){
                InputStream in = new FileInputStream(safeZone);

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

            OutputStream out = new FileOutputStream(safeZone);
            out.write(json.toString().getBytes());
            out.flush();
            out.close();

            if(claims.containsKey(chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ())){
                claims.remove(chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ());
            }

            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //PERFECT
    public static void renewMappedLandscape(Player player, Chunk chunk){
        for(int x = 0; x < 16; x++){
            for(int y = 254; y > -1; y--){
                Block block = chunk.getBlock(x, y, 0);
                Block blockAbove = chunk.getBlock(x, y+1, 0);

                if((!transparentBlocks.contains(block.getType()) && transparentBlocks.contains(blockAbove.getType())) || y == 0){
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                    break;
                }
            }
        }

        for(int x = 0; x < 16; x++){
            for(int y = 254; y > -1; y--){
                Block block = chunk.getBlock(x, y, 15);
                Block blockAbove = chunk.getBlock(x, y+1, 15);

                if((!transparentBlocks.contains(block.getType()) && transparentBlocks.contains(blockAbove.getType())) || y == 0){
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                    break;
                }
            }
        }

        for(int z = 0; z < 16; z++){
            for(int y = 254; y > -1; y--){
                Block block = chunk.getBlock(0, y, z);
                Block blockAbove = chunk.getBlock(0, y+1, z);

                if((!transparentBlocks.contains(block.getType()) && transparentBlocks.contains(blockAbove.getType())) || y == 0){
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                    break;
                }
            }
        }

        for(int z = 0; z < 16; z++){
            for(int y = 254; y > -1; y--){
                Block block = chunk.getBlock(15, y, z);
                Block blockAbove = chunk.getBlock(15, y+1, z);

                if((!transparentBlocks.contains(block.getType()) && transparentBlocks.contains(blockAbove.getType())) || y == 0){
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                    break;
                }
            }
        }
    }
}

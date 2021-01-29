package unet.Factions.Handlers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.JSONTokener;
import org.json.JSONObject;

import java.io.*;
import java.util.UUID;

import static unet.Factions.Main.plugin;

public class PlayerResolver {

    private static JSONObject players = new JSONObject();

    public PlayerResolver(){
        if(plugin.getDataFolder().exists()){
            try{
                File playersFile = new File(plugin.getDataFolder()+File.separator+"player_resolver.json");
                if(playersFile.exists()){
                    players = new JSONObject(new JSONTokener(new FileInputStream(playersFile)));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void setPlayer(String name, UUID uuid){
        players.put(name, uuid.toString());
        write();
    }

    public static OfflinePlayer getPlayer(String name){
        if(players.has(name)){
            return Bukkit.getOfflinePlayer(UUID.fromString(players.getString(name)));
        }
        return null;
    }

    private static void write(){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
            @Override
            public void run(){
                try{
                    if(!plugin.getDataFolder().exists()){
                        plugin.getDataFolder().mkdirs();
                    }

                    FileWriter out = new FileWriter(new File(plugin.getDataFolder()+File.separator+"player_resolver.json"));
                    out.write(players.toString());
                    out.flush();
                    out.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}

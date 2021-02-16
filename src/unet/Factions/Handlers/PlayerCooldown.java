package unet.Factions.Handlers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Date;
import java.util.UUID;

import static unet.Factions.Main.plugin;

public class PlayerCooldown {

    private static JSONObject players = new JSONObject();

    public PlayerCooldown(){
        if(plugin.getDataFolder().exists()){
            try{
                File playersFile = new File(plugin.getDataFolder()+File.separator+"player_cooldown.json");
                if(playersFile.exists()){
                    players = new JSONObject(new JSONTokener(new FileInputStream(playersFile)));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void setPlayerCooldown(UUID uuid){
        players.put(uuid.toString(), new Date().getTime());
        write();
    }

    public static long getPlayerCooldown(UUID uuid){
        if(players.has(uuid.toString())){
            return players.getLong(uuid.toString());
        }
        return 0;
    }

    private static void write(){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
            @Override
            public void run(){
                try{
                    if(!plugin.getDataFolder().exists()){
                        plugin.getDataFolder().mkdirs();
                    }

                    FileWriter out = new FileWriter(new File(plugin.getDataFolder()+File.separator+"player_cooldown.json"));
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

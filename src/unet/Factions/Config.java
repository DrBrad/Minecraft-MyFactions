package unet.Factions;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static unet.Factions.Main.*;

public class Config {

    public static int teleportDelay = 30, factionCreatePower = 10, claimPower = 2, warpCost = 2, playerDeathCost = 1, wildRadius = 5000;
    public static boolean warps = true, factionHome = true, sameFactionPvp = false, safeZonePvp = false, wild = true;

    public Config(){
        try{
            File data = new File(plugin.getDataFolder().getPath()+File.separator+"config.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(data);

            if(data.exists()){
                teleportDelay = config.getInt("teleport-delay");
                factionCreatePower = config.getInt("faction-create-power");
                claimPower = config.getInt("faction-claim-power-cost");
                warps = config.getBoolean("faction-warp-toggle");
                warpCost = config.getInt("faction-warp-power-cost");
                factionHome = config.getBoolean("faction-home-toggle");
                playerDeathCost = config.getInt("player-death-power-cost");
                sameFactionPvp = config.getBoolean("same-faction-pvp");
                safeZonePvp = config.getBoolean("safe-zone-pvp");
                wild = config.getBoolean("wild-teleport-toggle");
                wildRadius = config.getInt("wild-teleport-radius");

            }else{
                config.set("teleport-delay", 30);
                config.set("faction-create-power", 10);
                config.set("faction-claim-power-cost", 2);
                config.set("faction-warp-toggle", true);
                config.set("faction-warp-power-cost", 2);
                config.set("faction-home-toggle", true);
                config.set("player-death-power-cost", 1);
                config.set("same-faction-pvp", false);
                config.set("safe-zone-pvp", false);
                config.set("wild-teleport-toggle", true);
                config.set("wild-teleport-radius", 5000);
                config.save(data);
            }
            config.save(data);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

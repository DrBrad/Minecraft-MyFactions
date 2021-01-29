package unet.Factions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import unet.Factions.Claim.ClaimHandler;
import unet.Factions.Faction.FactionHandler;
import unet.Factions.Faction.MyFaction;
import unet.Factions.Handlers.PlayerResolver;
import unet.Factions.Handlers.Config;

import static unet.Factions.Handlers.Config.*;
import static unet.Factions.Handlers.MapHandler.*;
import static unet.Factions.Faction.FactionHandler.*;

public class Main extends JavaPlugin {

    public static Plugin plugin;

    //BETTER FAILURES - STATE EXACT REASON - SOME ARE OFF

    @Override
    public void onEnable(){
        plugin = this;
        Bukkit.getPluginManager().registerEvents(new MyEventHandler(), this);
        getCommand("f").setExecutor(new FactionCommands());

        getCommand("warp").setExecutor(new EssentialCommands());
        getCommand("warps").setExecutor(new EssentialCommands());
        getCommand("setwarp").setExecutor(new EssentialCommands());
        getCommand("delwarp").setExecutor(new EssentialCommands());
        getCommand("home").setExecutor(new EssentialCommands());
        getCommand("sethome").setExecutor(new EssentialCommands());
        getCommand("spawn").setExecutor(new EssentialCommands());
        getCommand("setspawn").setExecutor(new EssentialCommands());
        getCommand("tpa").setExecutor(new EssentialCommands());
        getCommand("tpaa").setExecutor(new EssentialCommands());
        getCommand("tpad").setExecutor(new EssentialCommands());
        getCommand("wild").setExecutor(new EssentialCommands());
        getCommand("back").setExecutor(new EssentialCommands());

        new Config();
        new PlayerResolver();
        new FactionHandler();
        new ClaimHandler();

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
            @Override
            public void run(){
                for(Player player : Bukkit.getOnlinePlayers()){
                    MyFaction faction = getPlayersFaction(player.getUniqueId());
                    if(faction != null){
                        faction.setPower(faction.getPower()+getPeriodicIncrease());
                    }
                }
            }
        }, getPeriodicTime(), getPeriodicTime());
    }

    @Override
    public void onDisable(){
        for(Player player : Bukkit.getOnlinePlayers()){
            if(isMapping(player.getUniqueId())){
                stopMapping(player);
            }
        }
    }
}

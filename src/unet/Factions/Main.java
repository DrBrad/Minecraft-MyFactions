package unet.Factions;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import unet.Factions.Claim.ClaimHandler;
import unet.Factions.Faction.FactionHandler;
import unet.Factions.Faction.MyFaction;
import unet.Factions.Handlers.PlayerCooldown;
import unet.Factions.Handlers.PlayerResolver;
import unet.Factions.Handlers.Config;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static unet.Factions.Handlers.Config.*;
import static unet.Factions.Handlers.MapHandler.*;
import static unet.Factions.Faction.FactionHandler.*;
import static unet.Factions.Handlers.PlayerCooldown.*;

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
        getCommand("msg").setExecutor(new EssentialCommands());
        getCommand("wild").setExecutor(new EssentialCommands());
        getCommand("gamemode").setExecutor(new EssentialCommands());
        getCommand("back").setExecutor(new EssentialCommands());

        new Config();
        new PlayerResolver();
        new FactionHandler();
        new ClaimHandler();
        new PlayerCooldown();

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
            @Override
            public void run(){
                long now = new Date().getTime();

                ArrayList<MyFaction> factions = getListOfFactions();
                if(factions != null){
                    for(MyFaction faction : factions){
                        int power = faction.getPower();
                        for(String uuid : faction.getPlayers()){
                            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));

                            if(player != null){
                                if(player.isOnline()){
                                    power += getPeriodicIncrease();

                                }else if(getPeriodicDecreaseCooldown()+getPlayerCooldown(player.getUniqueId()) < now){
                                    power -= getPeriodicDecrease();
                                }
                            }
                        }

                        faction.setPower(power);
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

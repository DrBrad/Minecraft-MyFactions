package unet.Factions;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;

import static unet.Factions.Handlers.*;
import static unet.Factions.Main.*;

public class EssentialCommands  implements CommandExecutor {

    private HashMap<Player, Player> playerTeleport = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args){
        if(commandSender instanceof Player){
            switch(command.getName()){
                case "warps":
                    listWarps(((Player) commandSender));
                    break;

                case "warp":
                    warp(((Player) commandSender), args);
                    break;

                case "setwarp":
                    setWarp(((Player) commandSender), args);
                    break;

                case "delwarp":
                    removeWarp(((Player) commandSender), args);
                    break;

                case "home":
                    home(((Player) commandSender));
                    break;

                case "sethome":
                    setHome(((Player) commandSender));
                    break;

                case "spawn":
                    spawn(((Player) commandSender));
                    break;

                case "setspawn":
                    setSpawn(((Player) commandSender));
                    break;

                case "tpaa":
                    tpaa(((Player) commandSender));
                    break;

                case "tpad":
                    tpad(((Player) commandSender));
                    break;

                case "tpa":
                    tpa(((Player) commandSender), args);
                    break;

                case "back":
                    back(((Player) commandSender));
                    break;
            }

        }

        return false;
    }

    private void warp(Player player, String[] args){
        if(args.length > 0){
            String warpName = args[0];

            File warp = new File(plugin.getDataFolder()+File.separator+"warps"+File.separator+warpName+".yml");
            if(warp.exists()){
                FileConfiguration config = YamlConfiguration.loadConfiguration(warp);

                teleport(player, new Location(plugin.getServer().getWorld(config.getString("world")), config.getDouble("x"),
                        config.getDouble("y"), config.getDouble("z")), warpName);

            }else{
                player.sendMessage("§cThe warp specified doesn't exist");
            }
        }else{
            player.sendMessage("§cPlease specify a warp you wish to go to.");
        }
    }

    private void listWarps(Player player){
        File warps = new File(plugin.getDataFolder()+File.separator+"warps");
        if(warps.exists() && warps.listFiles().length > 0){
            String builder = "";
            for(File warp : warps.listFiles()){
                builder += "§c"+warp.getName().substring(0, warp.getName().length()-4)+"§7, ";
            }
            builder = builder.substring(0, builder.length()-2);

            player.sendMessage("§Server warps: "+builder+".");
        }else{
            player.sendMessage("§cTheir doesn't seem to be any warps.");
        }
    }

    private void setWarp(Player player, String[] args){
        if(player.isOp()){
            if(args.length > 0){
                String warpName = args[0];
                if(warpName.length() < 13 && warpName.length() > 1){
                    try{
                        File warps = new File(plugin.getDataFolder()+File.separator+"warps");
                        if(!warps.exists()){
                            warps.mkdirs();
                        }

                        File warp = new File(warps.getPath()+File.separator+warpName+".yml");
                        FileConfiguration config = YamlConfiguration.loadConfiguration(warp);
                        config.set("world", player.getLocation().getWorld().getName());
                        config.set("x", player.getLocation().getX());
                        config.set("y", player.getLocation().getY());
                        config.set("z", player.getLocation().getZ());
                        config.save(warp);

                        player.sendMessage("§7You have set the warp §c"+warpName+"§7.");

                    }catch(Exception e){
                        e.printStackTrace();
                        player.sendMessage("§cError setting warp.");
                    }
                }else{
                    player.sendMessage("§cThe name exceeds character requirements.");
                }
            }else{
                player.sendMessage("§cPlease specify a warp name.");
            }
        }else{
            player.sendMessage("§cOnly server admins can set warps.");
        }
    }

    private void removeWarp(Player player, String[] args){
        if(player.isOp()){
            if(args.length > 0){
                String warpName = args[0];
                if(warpName.length() < 13 && warpName.length() > 1){
                    try{
                        File warp = new File(plugin.getDataFolder()+File.separator+"warps"+File.separator+warpName+".yml");
                        if(warp.exists()){
                            warp.delete();
                        }

                        player.sendMessage("§7You have removed the warp §c"+warpName+"§7.");
                    }catch(Exception e){
                        e.printStackTrace();
                        player.sendMessage("§cError removing warp.");
                    }
                }else{
                    player.sendMessage("§cThe name exceeds character requirements.");
                }
            }else{
                player.sendMessage("§cPlease specify a warp name.");
            }
        }else{
            player.sendMessage("§cOnly server admins can remove warps.");
        }
    }

    private void home(Player player){
        File home = new File(plugin.getDataFolder()+File.separator+"homes"+File.separator+player.getUniqueId().toString()+".yml");
        if(home.exists()){
            FileConfiguration config = YamlConfiguration.loadConfiguration(home);

            teleport(player, new Location(plugin.getServer().getWorld(config.getString("world")), config.getDouble("x"),
                    config.getDouble("y"), config.getDouble("z")), "Home");

        }else{
            player.sendMessage("§cYou don't seem to have a home set.");
        }
    }

    private void setHome(Player player){
        try{
            File homes = new File(plugin.getDataFolder()+File.separator+"homes");
            if(!homes.exists()){
                homes.mkdirs();
            }

            File warp = new File(homes.getPath()+File.separator+player.getUniqueId().toString()+".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(warp);
            config.set("world", player.getLocation().getWorld().getName());
            config.set("x", player.getLocation().getX());
            config.set("y", player.getLocation().getY());
            config.set("z", player.getLocation().getZ());
            config.save(warp);

            player.sendMessage("§7You have set your home.");

        }catch(Exception e){
            e.printStackTrace();
            player.sendMessage("§cError setting home.");
        }
    }

    private void spawn(Player player){
        File spawn = new File(plugin.getDataFolder()+File.separator+"spawn.yml");
        if(spawn.exists()){
            FileConfiguration config = YamlConfiguration.loadConfiguration(spawn);

            teleport(player, new Location(plugin.getServer().getWorld(config.getString("world")), config.getDouble("x"),
                    config.getDouble("y"), config.getDouble("z")), "Spawn");

        }else{
            player.sendMessage("§cTheir doesn't seem to be a spawn set.");
        }
    }

    private void setSpawn(Player player){
        if(player.isOp()){
            try{
                File spawn = new File(plugin.getDataFolder()+File.separator+"spawn.yml");
                FileConfiguration config = YamlConfiguration.loadConfiguration(spawn);
                config.set("world", player.getLocation().getWorld().getName());
                config.set("x", player.getLocation().getX());
                config.set("y", player.getLocation().getY());
                config.set("z", player.getLocation().getZ());
                config.save(spawn);

                player.sendMessage("§7You have set server spawn.");

            }catch(Exception e){
                e.printStackTrace();
                player.sendMessage("§cError setting home.");
            }
        }else{

        }
    }

    private void tpa(Player player, String[] args){
        if(args.length > 0){
            Player reqPlayer = plugin.getServer().getPlayer(args[1]);

            if(reqPlayer != null && reqPlayer.isOnline()){
                playerTeleport.put(reqPlayer, player);

                player.sendMessage("§7Teleport request sent to §c"+reqPlayer.getDisplayName()+"§7.");
                reqPlayer.sendMessage("§c"+player.getDisplayName()+"§7 wishes to teleport, please type §a/tpaa§7 to accept or §c/tpad§7to deny, this will expire in §c30s§7.");

                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                    @Override
                    public void run(){
                        if(playerTeleport.containsKey(reqPlayer)){
                            playerTeleport.remove(reqPlayer);
                            reqPlayer.sendMessage("§7Teleport request has expired!");
                        }
                    }
                }, 600);
            }else{
                player.sendMessage("§cThe player you are inviting doesn't exist or is not §aonline§c.");
            }
        }else{
            player.sendMessage("§cPlease specify a player you wish to teleport to.");
        }
    }

    private void tpaa(Player player){
        if(playerTeleport.containsKey(player)){
            Player reqPlayer = playerTeleport.get(player);
            player.sendMessage("§7You have accepted teleport for: §c"+reqPlayer.getDisplayName()+"§7.");
            reqPlayer.sendMessage("§c"+player.getDisplayName()+"§7 has accepted your teleport request.");
            teleport(reqPlayer, player.getLocation(), player.getDisplayName());
            playerTeleport.remove(player);

        }else{
            player.sendMessage("§cYou have no tp requests.");
        }
    }

    private void tpad(Player player){
        if(playerTeleport.containsKey(player)){
            Player reqPlayer = playerTeleport.get(player);
            player.sendMessage("§7You have denied teleport for §c"+reqPlayer.getDisplayName()+"§7.");
            reqPlayer.sendMessage("§c"+player.getDisplayName()+"§7 has denied your teleport request.");
            playerTeleport.remove(player);

        }else{
            player.sendMessage("§cYou have no tp requests.");
        }
    }

    private void back(Player player){
        if(lastTeleport.containsKey(player)){
            teleport(player, lastTeleport.get(player), "Back");

        }else{
            player.sendMessage("§cYou have no where to go back to.");
        }
    }
}

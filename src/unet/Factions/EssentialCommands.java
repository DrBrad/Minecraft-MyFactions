package unet.Factions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

import static unet.Factions.Config.*;
import static unet.Factions.Handlers.*;
import static unet.Factions.Main.*;
import static unet.Factions.Faction.*;

public class EssentialCommands  implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args){
        if(commandSender instanceof Player){
            switch(command.getName()){
                case "warps":
                    return listWarps(((Player) commandSender));

                case "warp":
                    return warp(((Player) commandSender), args);

                case "setwarp":
                    return setWarp(((Player) commandSender), args);

                case "delwarp":
                    return removeWarp(((Player) commandSender), args);

                case "home":
                    return home(((Player) commandSender));

                case "sethome":
                    return setHome(((Player) commandSender));

                case "spawn":
                    return spawn(((Player) commandSender));

                case "setspawn":
                    return setSpawn(((Player) commandSender));

                case "tpaa":
                    return tpaa(((Player) commandSender));

                case "tpad":
                    return tpad(((Player) commandSender));

                case "tpa":
                    return tpa(((Player) commandSender), args);

                case "wild":
                    return wild(((Player) commandSender), 0);

                case "back":
                    return back(((Player) commandSender));
            }
        }

        return false;
    }

    private boolean warp(Player player, String[] args){
        if(args.length > 0){
            String warpName = args[0];

            File warp = new File(plugin.getDataFolder()+File.separator+"warps"+File.separator+warpName+".yml");
            if(warp.exists()){
                FileConfiguration config = YamlConfiguration.loadConfiguration(warp);

                teleport(player, new Location(plugin.getServer().getWorld(config.getString("world")), config.getDouble("x"),
                        config.getDouble("y"), config.getDouble("z"), (float)config.getDouble("yaw"), (float)config.getDouble("pitch")), warpName);

            }else{
                player.sendMessage("§cThe warp specified doesn't exist");
            }
            return true;
        }else{
            player.sendMessage("§cPlease specify a warp you wish to go to.");
        }
        return false;
    }

    private boolean listWarps(Player player){
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
        return true;
    }

    private boolean setWarp(Player player, String[] args){
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
                        config.set("yaw", player.getLocation().getYaw());
                        config.set("pitch", player.getLocation().getPitch());
                        config.save(warp);

                        player.sendMessage("§7You have set the warp §c"+warpName+"§7.");

                    }catch(Exception e){
                        e.printStackTrace();
                        player.sendMessage("§cError setting warp.");
                    }
                }else{
                    player.sendMessage("§cThe name exceeds character requirements.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a warp name.");
                return false;
            }
        }else{
            player.sendMessage("§cOnly server admins can set warps.");
        }
        return true;
    }

    private boolean removeWarp(Player player, String[] args){
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
                return true;
            }else{
                player.sendMessage("§cPlease specify a warp name.");
            }
        }else{
            player.sendMessage("§cOnly server admins can remove warps.");
        }
        return false;
    }

    private boolean home(Player player){
        File home = new File(plugin.getDataFolder()+File.separator+"homes"+File.separator+player.getUniqueId().toString()+".yml");
        if(home.exists()){
            FileConfiguration config = YamlConfiguration.loadConfiguration(home);

            teleport(player, new Location(plugin.getServer().getWorld(config.getString("world")), config.getDouble("x"),
                    config.getDouble("y"), config.getDouble("z"), (float)config.getDouble("yaw"), (float)config.getDouble("pitch")), "Home");

        }else{
            player.sendMessage("§cYou don't seem to have a home set.");
        }
        return true;
    }

    private boolean setHome(Player player){
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
            config.set("yaw", player.getLocation().getYaw());
            config.set("pitch", player.getLocation().getPitch());
            config.save(warp);

            player.sendMessage("§7You have set your home.");

        }catch(Exception e){
            e.printStackTrace();
            player.sendMessage("§cError setting home.");
        }
        return true;
    }

    private boolean spawn(Player player){
        File spawn = new File(plugin.getDataFolder()+File.separator+"spawn.yml");
        if(spawn.exists()){
            FileConfiguration config = YamlConfiguration.loadConfiguration(spawn);

            teleport(player, new Location(plugin.getServer().getWorld(config.getString("world")), config.getDouble("x"),
                    config.getDouble("y"), config.getDouble("z"), (float)config.getDouble("yaw"), (float)config.getDouble("pitch")), "Spawn");

        }else{
            player.sendMessage("§cTheir doesn't seem to be a spawn set.");
        }
        return true;
    }

    private boolean setSpawn(Player player){
        if(player.isOp()){
            try{
                File spawn = new File(plugin.getDataFolder()+File.separator+"spawn.yml");
                FileConfiguration config = YamlConfiguration.loadConfiguration(spawn);
                config.set("world", player.getLocation().getWorld().getName());
                config.set("x", player.getLocation().getX());
                config.set("y", player.getLocation().getY());
                config.set("z", player.getLocation().getZ());
                config.set("yaw", player.getLocation().getYaw());
                config.set("pitch", player.getLocation().getPitch());
                config.save(spawn);

                player.sendMessage("§7You have set server spawn.");

            }catch(Exception e){
                e.printStackTrace();
                player.sendMessage("§cError setting home.");
            }
        }else{
            player.sendMessage("§cOnly server admins can set spawn.");
        }
        return true;
    }

    private boolean tpa(Player player, String[] args){
        if(args.length > 0){
            Player reqPlayer = plugin.getServer().getPlayer(args[0]);
            if(reqPlayer != null && reqPlayer.isOnline()){
                if(reqPlayer != player){
                    playerTeleport.put(reqPlayer, player);

                    player.sendMessage("§7Teleport request sent to §c"+reqPlayer.getDisplayName()+"§7.");
                    reqPlayer.sendMessage("§c"+player.getDisplayName()+"§7 wishes to teleport, please type §a/tpaa§7 to accept or §c/tpad§7to deny, this will expire in §c30s§7.");

                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                        @Override
                        public void run(){
                            if(playerTeleport.containsKey(reqPlayer)){
                                playerTeleport.remove(reqPlayer);
                                //TELL USER THE SAME
                                reqPlayer.sendMessage("§7Teleport request has expired!");
                            }
                        }
                    }, 600);
                }else{
                    player.sendMessage("§cYou cannot teleport to yourself...");
                }
            }else{
                player.sendMessage("§cThe player you are inviting doesn't exist or is not §aonline§c.");
            }
            return true;
        }else{
            player.sendMessage("§cPlease specify a player you wish to teleport to.");
        }
        return false;
    }

    private boolean tpaa(Player player){
        if(playerTeleport.containsKey(player)){
            Player reqPlayer = playerTeleport.get(player);

            if(reqPlayer.isOnline()){
                player.sendMessage("§7You have accepted teleport for: §c"+reqPlayer.getDisplayName()+"§7.");
                teleport(reqPlayer, player.getLocation(), player.getDisplayName());
                playerTeleport.remove(player);

            }else{
                player.sendMessage("§cPlayer is no longer online.");
            }
        }else{
            player.sendMessage("§cYou have no tp requests.");
        }
        return true;
    }

    private boolean tpad(Player player){
        if(playerTeleport.containsKey(player)){
            Player reqPlayer = playerTeleport.get(player);
            player.sendMessage("§7You have denied teleport for §c"+reqPlayer.getDisplayName()+"§7.");
            reqPlayer.sendMessage("§c"+player.getDisplayName()+"§7 has denied your teleport request.");
            playerTeleport.remove(player);

        }else{
            player.sendMessage("§cYou have no tp requests.");
        }
        return true;
    }

    private boolean wild(Player player, int attempts){
        if(wild){
            Location location = randomNotInClaim(player, 0);

            if(location != null){
                for(int y = 254; y > -1; y--){
                    Block block = player.getWorld().getBlockAt((int)location.getX(), y, (int)location.getZ());
                    Block blockAbove = player.getWorld().getBlockAt((int)location.getX(), y+1, (int)location.getZ());

                    if(nogoBlocks.contains(block.getType())){
                        if(attempts < 5){
                            wild(player, attempts+1);
                        }else{
                            player.sendMessage("§cCould not find a safe place to teleport, try again.");
                        }
                        break;
                    }

                    if(!transparentBlocks.contains(block.getType()) && transparentBlocks.contains(blockAbove.getType())){
                        location.setY(y+1);
                        teleport(player, location, "Wilderness");
                        break;
                    }

                    if(y == 0){
                        if(attempts < 5){
                            wild(player, attempts+1);
                        }else{
                            player.sendMessage("§cCould not find a safe place to teleport, try again.");
                        }
                        break;
                    }
                }
            }else{
                player.sendMessage("§cCould not find a wilderness location, try again.");
            }
        }else{
            player.sendMessage("§cWild teleport is not allowed in this server.");
        }
        return true;
    }

    private boolean back(Player player){
        if(lastTeleport.containsKey(player)){
            teleport(player, lastTeleport.get(player), "Back");

        }else{
            player.sendMessage("§cYou have no where to go back to.");
        }
        return true;
    }

    private Location randomNotInClaim(Player player, int attempts){
        int x = (int) (Math.random()*(wildRadius*2))-wildRadius;
        int z = (int) (Math.random()*(wildRadius*2))-wildRadius;

        Location location = new Location(player.getWorld(), x, 0, z);
        String claim = inClaim(location.getChunk());

        if(claim == null){
            return location;
        }else if(attempts < 5){
            return randomNotInClaim(player, attempts+1);
        }else{
            return null;
        }
    }
}

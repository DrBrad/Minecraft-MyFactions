package unet.Factions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import unet.Factions.Faction.MyFaction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static unet.Factions.Claim.ClaimHandler.*;
import static unet.Factions.Faction.FactionHandler.*;
import static unet.Factions.Handlers.BlockHandler.*;
import static unet.Factions.Handlers.Colors.*;
import static unet.Factions.Handlers.GeneralHandler.*;
import static unet.Factions.Handlers.Config.*;
import static unet.Factions.Main.plugin;

public class EssentialCommands implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args){
        if(commandSender instanceof Player){
            switch(command.getName()){
                case "warps":
                    return warps(((Player) commandSender), args);

                case "warp":
                    return warp(((Player) commandSender), args);

                case "setwarp":
                    return setWarpCMD(((Player) commandSender), args);

                case "delwarp":
                    return removeWarpCMD(((Player) commandSender), args);

                case "home":
                    return home(((Player) commandSender));

                case "sethome":
                    return setHome(((Player) commandSender));

                case "spawn":
                    return spawn(((Player) commandSender));

                case "setspawn":
                    return setSpawnCMD(((Player) commandSender));

                case "tpaa":
                    return tpaa(((Player) commandSender));

                case "tpad":
                    return tpad(((Player) commandSender));

                case "tpa":
                    return tpa(((Player) commandSender), args);

                case "wild":
                    return wild(((Player) commandSender));

                case "back":
                    return back(((Player) commandSender));
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args){
        if(commandSender instanceof Player){
            if(args.length > 0){
                String cmd = args[0].toLowerCase();
                ArrayList<String> tabComplete = new ArrayList<>();

                if(args.length > 1){
                    switch(cmd){
                        case "warp":
                            tabComplete.addAll(getWarps());
                            break;

                        case "setwarp":
                            tabComplete.addAll(getWarps());
                            break;

                        case "delwarp":
                            tabComplete.addAll(getWarps());
                            break;

                        case "tpa":
                            for(Player player : Bukkit.getOnlinePlayers()){
                                tabComplete.add(player.getName());
                            }
                            break;
                    }
                }else{
                    tabComplete.add("warps");
                    tabComplete.add("warp");
                    tabComplete.add("setwarp");
                    tabComplete.add("delwarp");
                    tabComplete.add("home");
                    tabComplete.add("sethome");
                    tabComplete.add("spawn");
                    tabComplete.add("setspawn");
                    tabComplete.add("tpa");
                    tabComplete.add("tpaa");
                    tabComplete.add("tpad");
                    tabComplete.add("wild");
                    tabComplete.add("back");
                }

                return tabComplete;
            }
        }

        return null;
    }

    private boolean home(Player player){
        if(isHomeTeleport()){
            File home = new File(plugin.getDataFolder()+File.separator+"homes"+File.separator+player.getUniqueId().toString()+".yml");
            if(home.exists()){
                FileConfiguration config = YamlConfiguration.loadConfiguration(home);

                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    teleport(player,
                            new Location(plugin.getServer().getWorld(config.getString("world")),
                                    config.getDouble("x"),
                                    config.getDouble("y"),
                                    config.getDouble("z"),
                                    (float)config.getDouble("yaw"),
                                    (float)config.getDouble("pitch")),
                            "Home",
                            getColorRGB(faction.getColor()));

                }else{
                    teleport(player,
                            new Location(plugin.getServer().getWorld(config.getString("world")),
                                    config.getDouble("x"),
                                    config.getDouble("y"),
                                    config.getDouble("z"),
                                    (float)config.getDouble("yaw"),
                                    (float)config.getDouble("pitch")),
                            "Home",
                            getColorRGB(5));
                }

            }else{
                player.sendMessage("§cYou don't seem to have a home set.");
            }
        }else{
            player.sendMessage("§cServer has player homes disabled.");
        }
        return true;
    }

    private boolean setHome(Player player){
        if(isHomeTeleport()){
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
                player.sendMessage("§cError setting your home.");
            }
        }else{
            player.sendMessage("§cServer has player homes disabled.");
        }
        return true;
    }

    public boolean warp(Player player, String[] args){
        if(args.length > 0){
            String warpName = args[0];

            if(isWarp(warpName)){
                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    teleport(player, getWarp(warpName), "warp "+warpName, getColorRGB(faction.getColor()));

                }else{
                    teleport(player, getWarp(warpName), "warp "+warpName, getColorRGB(5));
                }
                return true;
            }else{
                player.sendMessage("§cThe warp specified doesn't exist.");
            }
        }else{
            player.sendMessage("§cPlease specify a warp name.");
        }
        return false;
    }

    public boolean setWarpCMD(Player player, String[] args){
        if(player.isOp()){
            if(args.length > 0){

                String warpName = args[0];
                if(warpName.length() < 13 && warpName.length() > 1){
                    if(!isWarp(warpName)){
                        setWarp(warpName, player.getLocation());
                        player.sendMessage("§7You have set the warp: §a"+warpName+"§7.");
                        return true;
                    }else{
                        player.sendMessage("§cWarp already exists with this name.");
                    }
                }else{
                    player.sendMessage("§cWarp name exceeds character requirements.");
                }
            }else{
                player.sendMessage("§cPlease specify a warp name.");
            }
        }else{
            player.sendMessage("§cOnly server admins can set server warps.");
        }
        return false;
    }

    public boolean removeWarpCMD(Player player, String[] args){
        if(player.isOp()){
            if(args.length > 0){
                String warpName = args[0];
                if(isWarp(warpName)){
                    removeWarp(warpName);
                    player.sendMessage("§7You have removed the warp: §a"+warpName+"§7.");
                    return true;
                }else{
                    player.sendMessage("§cWarp specified doesn't exist.");
                }
            }else{
                player.sendMessage("§cPlease specify a warp name.");
            }
        }else{
            player.sendMessage("§cOnly server admins can remove server warps.");
        }
        return false;
    }

    public boolean warps(Player player, String[] args){
        int page = 0;
        if(args.length > 0){
            page = Integer.parseInt(args[0]);
        }

        ArrayList<String> warps = getWarps();

        if(warps != null && warps.size() > 0){
            player.sendMessage("§c------- §fList of Warps (1/"+(((warps.size()/9)*page)+1)+") §c-------");

            for(int i = page*9; i < (page+1)*9; i++){
                if(i < warps.size()){
                    Location warp = getWarp(warps.get(i));
                    player.sendMessage("§c"+warps.get(i)+"§7: Warp is located in the world: §c"+warp.getWorld().getName()+"§7.");
                }else{
                    break;
                }
            }
        }else{
            player.sendMessage("§cServer has no warps.");
        }
        return true;
    }

    private boolean spawn(Player player){
        Location spawn = getSpawn();
        if(spawn != null){
            MyFaction faction = getPlayersFaction(player.getUniqueId());
            if(faction != null){
                teleport(player, spawn, "Spawn", getColorRGB(faction.getColor()));

            }else{
                teleport(player, spawn, "Spawn", getColorRGB(5));
            }
            return true;
        }else{
            player.sendMessage("§cTheir doesn't seem to be a spawn set.");
        }
        return false;
    }

    private boolean setSpawnCMD(Player player){
        if(player.isOp()){
            setSpawn(player.getPlayer().getLocation());
            player.sendMessage("§7You have set server spawn.");
        }else{
            player.sendMessage("§cOnly server admins can set spawn.");
        }
        return true;
    }

    private boolean tpa(Player player, String[] args){
        if(args.length > 0){
            Player receiver = Bukkit.getPlayer(args[0]);

            if(receiver != null && receiver.isOnline()){
                if(!receiver.getUniqueId().equals(player.getUniqueId())){
                    setPlayerTeleport(player, receiver);

                    player.sendMessage("§7Teleport request sent to §c"+receiver.getDisplayName()+"§7.");
                    receiver.sendMessage("§c"+player.getDisplayName()+"§7 wishes to teleport, please type §a/tpaa§7 to accept or §c/tpad§7to deny, this will expire in §c30s§7.");

                    setPlayerTeleportTask(receiver, plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                        @Override
                        public void run(){
                            if(hasPlayerTeleport(receiver)){
                                removePlayerTeleport(receiver);
                                removePlayerTeleportTask(receiver);
                                player.sendMessage("§7Teleport request to §a"+receiver.getName()+"§7 has expired!");
                                receiver.sendMessage("§a"+receiver.getName()+"§7 teleport request has expired!");
                            }
                        }
                    }, 600));

                    return true;
                }else{
                    player.sendMessage("§cYou cannot teleport to yourself.");
                }
            }else{
                player.sendMessage("§cThe player specified doesn't exist or is not online.");
            }
        }else{
            player.sendMessage("§cPlease specify a player you wish to teleport to.");
        }
        return false;
    }

    private boolean tpaa(Player player){
        if(hasPlayerTeleport(player)){
            Player sender = getPlayerTeleport(player);
            removePlayerTeleport(player);

            if(sender.isOnline()){
                player.sendMessage("§7You have accepted teleport for: §c"+sender.getName()+"§7.");

                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    teleport(sender, player.getLocation(), "Back", getColorRGB(faction.getColor()));

                }else{
                    teleport(sender, player.getLocation(), "Back", getColorRGB(5));
                }
            }else{
                player.sendMessage("§cPlayer is no longer online.");
            }
        }else{
            player.sendMessage("§cYou have no tp requests.");
        }
        return true;
    }

    private boolean tpad(Player player){
        if(hasPlayerTeleport(player)){
            Player sender = getPlayerTeleport(player);
            removePlayerTeleport(player);

            if(sender.isOnline()){
                sender.sendMessage("§c"+player.getName()+"§7 has denied your teleport request.");
            }
            player.sendMessage("§7You have denied teleport for §c"+sender.getName()+"§7.");

        }else{
            player.sendMessage("§cYou have no tp requests.");
        }
        return true;
    }

    private boolean wild(Player player){
        if(isWildTeleport()){
            if(!isWildDelayed(player)){
                Location location = null;
                for(int i = 0; i < 6; i++){
                    int x = (int) (Math.random()*(getWildRadius()*2))-getWildRadius();
                    int z = (int) (Math.random()*(getWildRadius()*2))-getWildRadius();

                    location = new Location(player.getWorld(), x, 0, z);
                    if(!inClaim(location.getChunk())){
                        break;
                    }
                }

                if(inClaim(location.getChunk())){
                    player.sendMessage("§cCouldn't find a location outside a claim, try again.");
                    return false;
                }

                List<Material> dangerous = getDangerous();
                Block block = location.getWorld().getHighestBlockAt(location.getBlockX(), location.getBlockZ());

                if(dangerous.contains(block.getType()) || block.getY() < 0){
                    player.sendMessage("§cCould not find a safe place to teleport, try again.");
                    return false;
                }

                block = block.getWorld().getBlockAt(block.getX(), block.getY()+1, block.getZ());

                setWildDelayed(player);

                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    teleport(player, block.getLocation(), "Wild", getColorRGB(faction.getColor()));

                }else{
                    teleport(player, block.getLocation(), "Wild", getColorRGB(5));
                }
            }else{
                player.sendMessage("§cYou can only do /wild every "+(getWildDelay()/60000)+" minutes.");
            }
        }else{
            player.sendMessage("§cServer has wild teleports disabled.");
        }
        return true;
    }

    private boolean back(Player player){
        if(isBackTeleport()){
            if(hasLastTeleport(player)){
                Location location = getLastTeleport(player);

                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    teleport(player, location, "Back", getColorRGB(faction.getColor()));

                }else{
                    teleport(player, location, "Back", getColorRGB(5));
                }
            }else{
                player.sendMessage("§cYou have no where to go back to.");
            }
        }else{
            player.sendMessage("§cServer has back teleports disabled.");
        }
        return true;
    }
}

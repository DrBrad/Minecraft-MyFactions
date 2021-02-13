package unet.Factions;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import unet.Factions.Faction.MyFaction;
import unet.Factions.Handlers.Config;

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

                case "setendspawn":
                    return setSpawn(((Player) commandSender));

                case "tpaa":
                    return tpaa(((Player) commandSender));

                case "tpad":
                    return tpad(((Player) commandSender));

                case "msg":
                    return msg(((Player) commandSender), args);

                case "tpa":
                    return tpa(((Player) commandSender), args);

                case "wild":
                    return wild(((Player) commandSender));

                case "gamemode":
                    return gamemode(((Player) commandSender), args);

                case "back":
                    return back(((Player) commandSender));
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args){
        if(commandSender instanceof Player){
            String cmd = command.getName().toLowerCase();
            ArrayList<String> tabComplete = new ArrayList<>();

            if(args.length > 0){
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

                    case "msg":
                        if(args.length == 1){
                            for(Player player : Bukkit.getOnlinePlayers()){
                                tabComplete.add(player.getName());
                            }
                        }
                        break;

                    case "gamemode":
                        if(args.length == 2){
                            for(Player player : Bukkit.getOnlinePlayers()){
                                tabComplete.add(player.getName());
                            }
                        }else if(args.length == 1){
                            tabComplete.add("SURVIVAL");
                            tabComplete.add("CREATIVE");
                            tabComplete.add("ADVENTURE");
                            tabComplete.add("SPECTATOR");
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
                tabComplete.add("msg");
                tabComplete.add("wild");
                tabComplete.add("gamemode");
                tabComplete.add("back");
            }

            return tabComplete;
        }

        return null;
    }

    private boolean home(Player player){
        if(player.hasPermission("home")){
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
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean setHome(Player player){
        if(player.hasPermission("sethome")){
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
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    public boolean warp(Player player, String[] args){
        if(player.hasPermission("warp")){
            if(args.length > 0){
                String warpName = args[0];

                if(isWarp(warpName)){
                    MyFaction faction = getPlayersFaction(player.getUniqueId());
                    if(faction != null){
                        teleport(player, getWarp(warpName), "warp "+warpName, getColorRGB(faction.getColor()));

                    }else{
                        teleport(player, getWarp(warpName), "warp "+warpName, getColorRGB(5));
                    }
                }else{
                    player.sendMessage("§cThe warp specified doesn't exist.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a warp name.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    public boolean setWarp(Player player, String[] args){
        if(player.hasPermission("setwarp") || player.isOp()){
            if(args.length > 0){
                String warpName = args[0];
                if(warpName.length() < 13 && warpName.length() > 1){
                    if(!isWarp(warpName)){
                        Config.setWarp(warpName, player.getLocation());
                        player.sendMessage("§7You have set the warp: §a"+warpName+"§7.");
                    }else{
                        player.sendMessage("§cWarp already exists with this name.");
                    }
                }else{
                    player.sendMessage("§cWarp name exceeds character requirements.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a warp name.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    public boolean removeWarp(Player player, String[] args){
        if(player.hasPermission("delwarp") || player.isOp()){
            if(args.length > 0){
                String warpName = args[0];
                if(isWarp(warpName)){
                    Config.removeWarp(warpName);
                    player.sendMessage("§7You have removed the warp: §a"+warpName+"§7.");
                }else{
                    player.sendMessage("§cWarp specified doesn't exist.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a warp name.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    public boolean warps(Player player, String[] args){
        if(player.hasPermission("warps")){
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
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean spawn(Player player){
        if(player.hasPermission("spawn")){
            Location spawn = getSpawn();
            if(spawn != null){
                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    teleport(player, spawn, "Spawn", getColorRGB(faction.getColor()));

                }else{
                    teleport(player, spawn, "Spawn", getColorRGB(5));
                }
            }else{
                player.sendMessage("§cTheir doesn't seem to be a spawn set.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean setSpawn(Player player){
        if(player.hasPermission("setspawn") || player.isOp()){
            World.Environment environment = player.getLocation().getWorld().getEnvironment();
            if(environment == World.Environment.NORMAL){
                Config.setSpawn(player.getPlayer().getLocation());
                player.sendMessage("§7You have set server spawn.");

            }else if(environment == World.Environment.THE_END){
                Config.setEndSpawn(player.getPlayer().getLocation());
                player.sendMessage("§7You have set server end spawn.");

            }else{
                player.sendMessage("§7You cannot set the nethers spawn.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean tpa(Player player, String[] args){
        if(player.hasPermission("tpa")){
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

                    }else{
                        player.sendMessage("§cYou cannot teleport to yourself.");
                    }
                }else{
                    player.sendMessage("§cThe player specified doesn't exist or is not online.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a player you wish to teleport to.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean tpaa(Player player){
        if(player.hasPermission("tpa")){
            if(hasPlayerTeleport(player)){
                Player sender = getPlayerTeleport(player);
                removePlayerTeleport(player);

                if(sender.isOnline()){
                    player.sendMessage("§7You have accepted teleport for: §c"+sender.getName()+"§7.");

                    MyFaction faction = getPlayersFaction(player.getUniqueId());
                    if(faction != null){
                        teleport(sender, player.getLocation(), player.getName(), getColorRGB(faction.getColor()));

                    }else{
                        teleport(sender, player.getLocation(), player.getName(), getColorRGB(5));
                    }
                }else{
                    player.sendMessage("§cPlayer is no longer online.");
                }
            }else{
                player.sendMessage("§cYou have no tp requests.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean tpad(Player player){
        if(player.hasPermission("tpa")){
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
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean msg(Player player, String[] args){
        if(player.hasPermission("msg")){
            if(args.length > 1){
                Player receiver = Bukkit.getPlayer(args[0]);

                if(receiver != null && receiver.isOnline()){
                    if(!receiver.getUniqueId().equals(player.getUniqueId())){
                        String builder = "";
                        for(int i = 1; i < args.length; i++){
                            builder += args[i]+" ";
                        }

                        builder = builder.replaceAll("&", "§");
                        builder = builder.substring(0, builder.length()-1);


                        player.sendMessage("§o§7msg "+receiver.getName()+"§r§7: "+builder);
                        receiver.sendMessage("§o§7msg "+receiver.getName()+"§r§7: "+builder);

                    }else{
                        player.sendMessage("§cYou cannot message yourself.");
                    }
                }else{
                    player.sendMessage("§cThe player specified doesn't exist or is not online.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a player and a message you wish to send.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean wild(Player player){
        if(player.hasPermission("wild")){
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
                        return true;
                    }

                    List<Material> dangerous = getDangerous();
                    Block block = location.getWorld().getHighestBlockAt(location.getBlockX(), location.getBlockZ());

                    if(dangerous.contains(block.getType()) || block.getY() < 0){
                        player.sendMessage("§cCould not find a safe place to teleport, try again.");
                        return true;
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
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean back(Player player){
        if(player.hasPermission("back")){
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
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    public boolean gamemode(Player player, String[] args){
        if(player.hasPermission("gamemode") || player.isOp()){
            if(args.length > 0){
                String mode = args[0].toLowerCase();

                if(args.length > 1){
                    Player receiver = Bukkit.getPlayer(args[1]);

                    if(receiver != null && receiver.isOnline()){
                        if(mode.equals("survival") || mode.equals("0")){
                            receiver.setGameMode(GameMode.SURVIVAL);
                            player.sendMessage("§7You have changed §6"+receiver.getName()+"§7 gamemode to §aSURVIVAL§7 mode.");
                            receiver.sendMessage("§6"+player.getName()+"§7 has changed your gamemode to §aSURVIVAL§7 mode.");
                            //Bukkit.broadcastMessage("§6"+player.getName()+"§7 has changed §6"+receiver.getName()+"§7 gamemode to §aSURVIVAL§7 mode.");

                        }else if(mode.equals("creative") || mode.equals("1")){
                            receiver.setGameMode(GameMode.CREATIVE);
                            player.sendMessage("§7You have changed §6"+receiver.getName()+"§7 gamemode to §aCREATIVE§7 mode.");
                            receiver.sendMessage("§6"+player.getName()+"§7 has changed your gamemode to §aCREATIVE§7 mode.");
                            //Bukkit.broadcastMessage("§6"+player.getName()+"§7 has changed §6"+receiver.getName()+"§7 gamemode to §aCREATIVE§7 mode.");

                        }else if(mode.equals("adventure") || mode.equals("2")){
                            receiver.setGameMode(GameMode.ADVENTURE);
                            player.sendMessage("§7You have changed §6"+receiver.getName()+"§7 gamemode to §aADVENTURE§7 mode.");
                            receiver.sendMessage("§6"+player.getName()+"§7 has changed your gamemode to §aADVENTURE§7 mode.");
                            //Bukkit.broadcastMessage("§6"+player.getName()+"§7 has changed §6"+receiver.getName()+"§7 gamemode to §aADVENTURE§7 mode.");

                        }else if(mode.equals("spectator") || mode.equals("3")){
                            receiver.setGameMode(GameMode.SPECTATOR);
                            player.sendMessage("§7You have changed §6"+receiver.getName()+"§7 gamemode to §aSPECTATOR§7 mode.");
                            receiver.sendMessage("§6"+player.getName()+"§7 has changed your gamemode to §aSPECTATOR§7 mode.");
                            //Bukkit.broadcastMessage("§6"+player.getName()+"§7 has changed §6"+receiver.getName()+"§7 gamemode to §aSPECTATOR§7 mode.");
                        }
                    }else{
                        player.sendMessage("§cPlayer specified isn't online.");
                    }

                }else{
                    if(mode.equals("survival") || mode.equals("0")){
                        player.setGameMode(GameMode.SURVIVAL);
                        player.sendMessage("§7You have changed their gamemode to §aSURVIVAL§7 mode.");
                        //Bukkit.broadcastMessage("§6"+player.getName()+"§7 has changed their gamemode to §aSURVIVAL§7 mode.");

                    }else if(mode.equals("creative") || mode.equals("1")){
                        player.setGameMode(GameMode.CREATIVE);
                        player.sendMessage("§7You have changed their gamemode to §aCREATIVE§7 mode.");
                        //Bukkit.broadcastMessage("§6"+player.getName()+"§7 has changed their gamemode to §aCREATIVE§7 mode.");

                    }else if(mode.equals("adventure") || mode.equals("2")){
                        player.setGameMode(GameMode.ADVENTURE);
                        player.sendMessage("§7You have changed their gamemode to §aADVENTURE§7 mode.");
                        //Bukkit.broadcastMessage("§6"+player.getName()+"§7 has changed their gamemode to §aADVENTURE§7 mode.");

                    }else if(mode.equals("spectator") || mode.equals("3")){
                        player.setGameMode(GameMode.SPECTATOR);
                        player.sendMessage("§7You have changed their gamemode to §aSPECTATOR§7 mode.");
                        //Bukkit.broadcastMessage("§6"+player.getName()+"§7 has changed their gamemode to §aSPECTATOR§7 mode.");
                    }
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a gamemode.");
                return false;
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }
}

package unet.Factions;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import static unet.Factions.Handlers.teleport;
import static unet.Factions.Main.*;
import static unet.Factions.Faction.*;

public class FactionCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args){
        if(commandSender instanceof Player){
            if(args.length > 0){
                String cmd = args[0].toLowerCase();
                switch(cmd){
                    case "help":
                        help(((Player) commandSender), args);
                        break;

                    case "?":
                        help(((Player) commandSender), args);
                        break;

                    case "create":
                        create(((Player) commandSender), args);
                        break;

                    case "invite":
                        invite(((Player) commandSender), args);
                        break;

                    case "join":
                        join(((Player) commandSender));
                        break;

                    case "leave":
                        leave(((Player) commandSender));
                        break;

                    case "remove":
                        remove(((Player) commandSender), args);
                        break;

                    case "promote":
                        promote(((Player) commandSender), args);
                        break;

                    case "demote":
                        demote(((Player) commandSender), args);
                        break;

                    case "disband":
                        disband(((Player) commandSender));
                        break;

                    case "claim":
                        claim(((Player) commandSender));
                        break;

                    case "unclaim":
                        unclaim(((Player) commandSender));
                        break;

                    case "home":
                        home(((Player) commandSender));
                        break;

                    case "sethome":
                        setHome(((Player) commandSender));
                        break;

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

                    case "power":
                        power(((Player) commandSender), args);
                        break;

                    case "list":
                        listFactions(((Player) commandSender));
                        break;

                    case "rename":
                        rename(((Player) commandSender), args);
                        break;

                    case "chat":
                        factionChat(((Player) commandSender));
                        break;

                    case "map":
                        factionMap(((Player) commandSender));
                        break;

                    case "version":
                        commandSender.sendMessage("§7MyFaction version §c1.0 §7by DrBrad.");
                        break;
                }

            }else{
                commandSender.sendMessage("§7Type §c/f help§7 to see a list of commands.");
            }
        }

        return false;
    }

    private HashMap<Player, String> invites = new HashMap<>();

    //PERFECT
    private void help(Player player, String[] args){
        if(args.length > 1){
            if(args[1].equals("2")){
                player.sendMessage("§c------- §fFaction commands (2/4) §c-------");
                player.sendMessage("§c/f disband: §7Deletes your faction.");
                player.sendMessage("§c/f claim: §7Claim a chunk for your faction.");
                player.sendMessage("§c/f unclaim: §7Removes claim from your faction.");
                player.sendMessage("§c/f home: §7Teleport to your factions home.");
                player.sendMessage("§c/f sethome: §7Set your factions home.");
                player.sendMessage("§c/f warp: §7Teleport to one of your factions warps.");
                player.sendMessage("§c/f warps: §7Lists all warps for your faction.");
                return;

            }else if(args[1].equals("3")){
                player.sendMessage("§c------- §fFaction commands (3/4) §c-------");
                player.sendMessage("§c/f setwarp: §7Set a warp for your faction.");
                player.sendMessage("§c/f delwarp: §7Removes a warp from your faction.");
                player.sendMessage("§c/f power: §7Check yours or another factions power");
                player.sendMessage("§c/f list: §7List of all of the factions.");
                player.sendMessage("§c/f rename: §7Rename your faction something else.");
                player.sendMessage("§c/f chat: §7Chat with only faction members or globally.");
                player.sendMessage("§c/f map: §See all faction claims chunks visually.");
                return;

            }else if(args[1].equals("4")){
                player.sendMessage("§c------- §fFaction commands (4/4) §c-------");
                player.sendMessage("§c/f version: §7Get the version of this plugin.");
                return;

            }
        }

        player.sendMessage("§c------- §fFaction commands (1/4) §c-------");
        player.sendMessage("§c/f create: §7Creates a faction.");
        player.sendMessage("§c/f invite: §7Invites player to faction.");
        player.sendMessage("§c/f join: §7Join faction from invite.");
        player.sendMessage("§c/f leave: §7Leave your faction.");
        player.sendMessage("§c/f remove: §7Remove player from your faction.");
        player.sendMessage("§c/f promote: §7Promote player in faction.");
        player.sendMessage("§c/f demote: §7Demote player in faction.");
    }

    //MAKE WITH PAGES
    private void listFactions(Player player){
        File factions = new File(plugin.getDataFolder()+File.separator+"factions");
        if(factions.exists() && factions.listFiles().length > 0){
            String playersFaction = getFaction(player);

            for(File warps : factions.listFiles()){
                String factionName = warps.getName();
                if(playersFaction.equals(factionName)){
                    int power = getFactionPower(factionName);
                    player.sendMessage("§a"+factionName+"§7: with a power level of: "+((power > 0) ? "§a"+power : "§c"+power)+"§7.");

                }else{
                    int power = getFactionPower(factionName);
                    player.sendMessage("§c"+factionName+"§7: with a power level of: "+((power > 0) ? "§a"+power : "§c"+power)+"§7.");
                }
            }
        }else{
            player.sendMessage("§cThere are no factions currently.");
        }
    }

    //PERFECT
    private void create(Player player, String[] args){
        if(args.length > 1){
            String factionName = args[1];

            if(factionName.length() < 13 && factionName.length() > 1){
                if(!factionName.equalsIgnoreCase("null")){
                    File factionFolder = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName);

                    if(!factionFolder.exists()){
                        if(getFaction(player) == null){
                            if(createFaction(player, factionName)){
                                player.sendMessage("§7Faction §c"+factionName+"§7 was created.");

                            }else{
                                player.sendMessage("§cFailed to create faction.");
                            }
                        }else{
                            player.sendMessage("§cYou are already in a faction.");
                        }
                    }else{
                        player.sendMessage("§cFaction with that name already exists.");
                    }
                }else{
                    player.sendMessage("§cYou cannot name your faction this.");
                }
            }else{
                player.sendMessage("§cFaction name exceeds character requirements.");
            }
        }else{
            player.sendMessage("§cYou must include a name for your faction.");
        }
    }

    //PERFECT
    private void rename(Player player, String[] args){
        if(args.length > 1){
            String newFactionName = args[1];

            if(newFactionName.length() < 13 && newFactionName.length() > 1){
                if(!newFactionName.equalsIgnoreCase("null")){
                    File factionFolder = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+newFactionName);

                    if(!factionFolder.exists()){
                        String factionName = getFaction(player);
                        if(factionName != null){
                            if(getPlayerRank(player, factionName) == 3){
                                renameFaction(factionName, newFactionName);
                            }
                        }else{
                            player.sendMessage("§cYou aren't in a faction.");
                        }
                    }else{
                        player.sendMessage("§cFaction with that name already exists.");
                    }
                }else{
                    player.sendMessage("§cYou cannot name your faction this.");
                }
            }else{
                player.sendMessage("§cFaction name exceeds character requirements.");
            }
        }else{
            player.sendMessage("§cYou must include a name for your faction.");
        }
    }

    //PERFECT
    private void invite(Player player, String[] args){
        if(args.length > 1){
            String factionName = getFaction(player);
            if(factionName != null){
                Player reqPlayer = plugin.getServer().getPlayer(args[1]);

                if(reqPlayer != null && reqPlayer.isOnline()){
                    if(player.getUniqueId() != reqPlayer.getUniqueId()){
                        if(getFaction(reqPlayer) == null){
                            if(getPlayerRank(player, factionName) > 0){
                                invites.put(reqPlayer, factionName);
                                player.sendMessage("§7You have invited §c"+reqPlayer.getDisplayName()+"§7 to the faction, this invite will expire in §c30s§7.");
                                reqPlayer.sendMessage("§7"+player.getDisplayName()+" §7has invited you to join §c"+factionName+"§7!");

                                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                                    @Override
                                    public void run(){
                                        if(invites.containsKey(reqPlayer)){
                                            invites.remove(reqPlayer);
                                            reqPlayer.sendMessage("§7Faction invite to §c"+factionName+"§7 has expired!");
                                        }
                                    }
                                }, 600);

                            }else{
                                player.sendMessage("§cInsufficient permission in the faction, you cannot invite anyone.");
                            }

                        }else{
                            player.sendMessage("§cThe player you are inviting is already in the faction.");
                        }
                    }else{
                        player.sendMessage("§cYou cannot invite yourself to the faction.");
                    }
                }else{
                    player.sendMessage("§cThe player you are inviting doesn't exist or is not §aonline§c.");
                }
            }else{
                player.sendMessage("§cYou aren't in a faction.");
            }
        }else{
            player.sendMessage("§cYou must include a player name that you wish to invite.");
        }
    }

    //PERFECT
    private void join(Player player){
        if(getFaction(player) == null){
            if(invites.containsKey(player)){
                String factionName = invites.get(player);

                if(addPlayerToFaction(player, factionName)){
                    invites.remove(player);
                    player.sendMessage("§7You have joined §c"+factionName+"§7.");

                    JSONObject players = getFactionPlayers(factionName);
                    Iterator<String> keys = players.keys();
                    while(keys.hasNext()){
                        Player clansmen = Bukkit.getPlayer(UUID.fromString(keys.next()));
                        if(clansmen != null && clansmen.isOnline()){
                            player.sendMessage("§c"+player.getDisplayName()+"§7 has joined the clan.");
                        }
                    }

                }else{
                    player.sendMessage("§cFailed to join faction.");
                }
            }else{
                player.sendMessage("§cYou don't seem to have any faction invites.");
            }
        }else{
            player.sendMessage("§cYou must leave your faction before you can join this faction.");
        }
    }

    //PERFECT
    private void leave(Player player){
        String factionName = getFaction(player);
        if(factionName != null){
            if(getPlayerRank(player, factionName) != 3){
                if(removePlayerFromFaction(player, factionName)){
                    player.sendMessage("§7You have left §c"+factionName+"§7.");
                }else{
                    player.sendMessage("§cFailed to leave faction.");
                }
            }else{
                player.sendMessage("§cYou must transfer ownership or disband to leave this faction.");
            }
        }else{
            player.sendMessage("§cYou must be in a faction to leave one.");
        }
    }

    //PERFECT
    private void remove(Player player, String[] args){
        if(args.length > 1){
            String factionName = getFaction(player);
            if(factionName != null){
                Player reqPlayer = plugin.getServer().getPlayer(args[1]);

                if(reqPlayer != null){
                    String reqFaction = getFaction(reqPlayer);
                    if(reqFaction != null && reqFaction.equals(factionName)){
                        if(player.getUniqueId() != reqPlayer.getUniqueId()){
                            if(getPlayerRank(player, factionName) > getPlayerRank(reqPlayer, factionName)){
                                if(removePlayerFromFaction(reqPlayer, factionName)){
                                    player.sendMessage("§7You have removed §c"+reqPlayer.getDisplayName()+" from the faction.");

                                    if(reqPlayer.isOnline()){
                                        reqPlayer.sendMessage("§7You have been removed from the faction by §c"+player.getDisplayName()+"§7.");
                                    }
                                }else{
                                    player.sendMessage("§cFailed to remove player from faction.");
                                }

                            }else{
                                player.sendMessage("§cInsufficient permission in the faction, you cannot remove anyone.");
                            }
                        }else{
                            player.sendMessage("§cYou cannot remove yourself from the faction.");
                        }
                    }else{
                        player.sendMessage("§cThe player you wish to remove isn't in your faction.");
                    }
                }else{
                    player.sendMessage("§cThe player you wish to remove doesn't exist.");
                }
            }else{
                player.sendMessage("§cYou must be in a faction to remove someone.");
            }
        }else{
            player.sendMessage("§cYou must include a player name that you wish to remove.");
        }
    }

    //PERFECT
    private void promote(Player player, String[] args){
        if(args.length > 1){
            String factionName = getFaction(player);
            if(factionName != null){
                Player reqPlayer = plugin.getServer().getPlayer(args[1]);
                if(reqPlayer != null){
                    String reqFaction = getFaction(reqPlayer);
                    if(reqFaction != null && reqFaction.equals(factionName)){
                        if(player.getUniqueId() != reqPlayer.getUniqueId()){
                            if(getPlayerRank(player, factionName) == 3){
                                if(getPlayerRank(reqPlayer, factionName) < 3){
                                    if(promotePlayer(reqPlayer, factionName)){
                                        player.sendMessage("§7You have promoted §c"+reqPlayer.getDisplayName()+"§7.");

                                        if(reqPlayer.isOnline()){
                                            reqPlayer.sendMessage("§7You have been promoted to promoted §cAdmin§7 in faction.");
                                        }
                                    }else{
                                        player.sendMessage("§cFailed to promote player.");
                                    }
                                }else{
                                    player.sendMessage("§c"+reqPlayer.getDisplayName()+"§7 is already admin, you can transfer your ownership by typing §c/f chown§7.");
                                }
                            }else{
                                player.sendMessage("§cYou must be the owner of the faction to promote.");
                            }
                        }else{
                            player.sendMessage("§cYou cannot promote yourself in the faction.");
                        }
                    }else{
                        player.sendMessage("§cYou cannot remove yourself from the faction.");
                    }
                }else{
                    player.sendMessage("§cThe player specified doesn't exist.");
                }
            }else{
                player.sendMessage("§cYou aren't in a faction.");
            }
        }
    }

    //PERFECT
    private void demote(Player player, String[] args){
        if(args.length > 1){
            String factionName = getFaction(player);
            if(factionName != null){
                Player reqPlayer = plugin.getServer().getPlayer(args[1]);
                if(reqPlayer != null){
                    String reqFaction = getFaction(reqPlayer);
                    if(reqFaction != null && reqFaction.equals(factionName)){
                        if(player.getUniqueId() != reqPlayer.getUniqueId()){
                            if(getPlayerRank(player, factionName) == 3){
                                if(getPlayerRank(reqPlayer, factionName) > 0){
                                    if(demotePlayer(player, factionName)){
                                        player.sendMessage("§7You have demoted §c"+reqPlayer.getDisplayName()+"§7.");

                                        if(reqPlayer.isOnline()){
                                            reqPlayer.sendMessage("§7You have been demoted to promoted §cRecruit§7 in faction.");
                                        }
                                    }else{
                                        player.sendMessage("§cFailed to demote player.");
                                    }
                                }else{
                                    player.sendMessage("§cYou cannot demote a this person further.");
                                }
                            }else{
                                player.sendMessage("§cYou must be the owner of the faction to promote.");
                            }
                        }else{
                            player.sendMessage("§cYou cannot promote yourself in the faction.");
                        }
                    }else{
                        player.sendMessage("§cYou cannot remove yourself from the faction.");
                    }
                }else{
                    player.sendMessage("§cThe player specified doesn't exist.");
                }
            }else{
                player.sendMessage("§cYou aren't in a faction.");
            }
        }
    }

    //PERFECT
    private void disband(Player player){
        String factionName = getFaction(player);
        if(factionName != null){
            if(getPlayerRank(player, factionName) == 3){
                disbandFaction(factionName);
            }
        }else{
            player.sendMessage("§cYou aren't in a faction.");
        }
    }

    //PERFECT
    private void claim(Player player){
        String factionName = getFaction(player);
        if(factionName != null){
            if(getPlayerRank(player, factionName) > 1){
                String claim = inClaim(player.getLocation().getChunk());
                if(claim == null || getFactionPower(claim) < 0){
                    int power = getFactionPower(factionName);
                    if(power > 1){
                        if(claimForFaction(factionName, player.getLocation().getChunk(), power)){
                            player.sendMessage("§7You have claimed this chunk!");
                        }else{
                            player.sendMessage("§cFailed to claim chunk.");
                        }
                    }else{
                        player.sendMessage("§cYou don't have enough power to claim.");
                    }
                }else{
                    player.sendMessage("§cThis chunk is claimed by a faction with enough power to keep it.");
                }
            }else{
                player.sendMessage("§cInsufficient permission, you cannot claim.");
            }
        }else{
            player.sendMessage("§cYou must be in a faction to claim!");
        }
    }

    //PERFECT
    private void unclaim(Player player){
        String factionName = getFaction(player);
        if(factionName != null){
            if(getPlayerRank(player, factionName) > 1){
                if(inClaim(player.getLocation().getChunk()).equals(factionName)){
                    if(unclaimForFaction(factionName, player.getLocation().getChunk(), getFactionPower(factionName))){
                        player.sendMessage("§7You have unclaimed this chunk!");
                    }else{
                        player.sendMessage("§cFailed to claim chunk.");
                    }
                }else{
                    player.sendMessage("§cThis chunk is not your claim.");
                }
            }else{
                player.sendMessage("§cInsufficient permission, you cannot claim.");
            }
        }else{
            player.sendMessage("§cYou must be in a faction to claim!");
        }
    }

    //PERFECT
    private void power(Player player, String[] args){
        if(args.length > 1){
            File factionFolder = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+args[1]);
            if(factionFolder.exists()){
                player.sendMessage("§c"+args[1]+"§7 power level is: §c"+getFactionPower(args[1])+"§7.");
            }
        }else{
            String factionName = getFaction(player);
            if(factionName != null){
                player.sendMessage("§c"+factionName+"§7 power level is: §c"+getFactionPower(factionName)+"§7.");
            }else{
                player.sendMessage("§cPlease include a faction you wish to power check.");
            }
        }
    }

    //PERFECT
    private void home(Player player){
        String factionName = getFaction(player);
        if(factionName != null){
            File factionHome = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"home.yml");
            if(factionHome.exists()){
                FileConfiguration config = YamlConfiguration.loadConfiguration(factionHome);

                teleport(player, new Location(plugin.getServer().getWorld(config.getString("world")), config.getDouble("x"),
                        config.getDouble("y"), config.getDouble("z")), "Faction Home");

            }else{
                player.sendMessage("§cYour faction doesn't have a faction home.");
            }
        }else{
            player.sendMessage("§cYou aren't a part of any faction.");
        }
    }

    //PERFECT
    private void setHome(Player player){
        String factionName = getFaction(player);
        if(factionName != null){
            if(getPlayerRank(player, factionName) > 1){
                if(inClaim(player.getLocation().getChunk()).equals(factionName)){
                    try{
                        File factionHome = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"home.yml");
                        FileConfiguration config = YamlConfiguration.loadConfiguration(factionHome);
                        config.set("world", player.getLocation().getWorld().getName());
                        config.set("x", player.getLocation().getX());
                        config.set("y", player.getLocation().getY());
                        config.set("z", player.getLocation().getZ());
                        config.save(factionHome);

                        player.sendMessage("§7You have set the factions home.");
                    }catch(Exception e){
                        e.printStackTrace();
                        player.sendMessage("§cError setting your factions home.");
                    }
                }else{
                    player.sendMessage("§cYou can only set your factions home within your claim.");
                }
            }else{
                player.sendMessage("§cInsufficient permissions to set home.");
            }
        }else{
            player.sendMessage("§cYou aren't a part of any faction.");
        }
    }

    //PERFECT
    private void warp(Player player, String[] args){
        if(args.length > 1){
            String warpName = args[1];

            String factionName = getFaction(player);
            if(factionName != null){
                File factionWarp = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"warps"+File.separator+warpName+".yml");
                if(factionWarp.exists()){
                    FileConfiguration config = YamlConfiguration.loadConfiguration(factionWarp);

                    teleport(player, new Location(plugin.getServer().getWorld(config.getString("world")), config.getDouble("x"),
                            config.getDouble("y"), config.getDouble("z")), warpName);

                }else{
                    player.sendMessage("§cYour faction doesn't have that warp.");
                }
            }else{
                player.sendMessage("§cYou aren't a part of any faction.");
            }
        }else{
            player.sendMessage("§cPlease specify a warp name.");
        }
    }

    //PERFECT
    private void listWarps(Player player){
        String factionName = getFaction(player);
        if(factionName != null){
            File factionWarps = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"warps");
            if(factionWarps.exists() && factionWarps.listFiles().length > 0){
                String builder = "";
                for(File warp : factionWarps.listFiles()){
                    builder += "§c"+warp.getName().substring(0, warp.getName().length()-4)+"§7, ";
                }
                builder = builder.substring(0, builder.length()-2);

                player.sendMessage("§7Factions warps: "+builder+".");
            }else{
                player.sendMessage("§cYour faction has no warps.");
            }
        }else{
            player.sendMessage("§cYou aren't a part of any faction.");
        }
    }

    //PERFECT
    private void setWarp(Player player, String[] args){
        if(args.length > 1){
            String warpName = args[1];
            if(warpName.length() < 13 && warpName.length() > 1){
                String factionName = getFaction(player);
                if(factionName != null){
                    if(getPlayerRank(player, factionName) > 1){
                        if(inClaim(player.getLocation().getChunk()).equals(factionName)){
                            try{
                                File factionWarp = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"warps"+File.separator+warpName+".yml");
                                FileConfiguration config = YamlConfiguration.loadConfiguration(factionWarp);
                                config.set("world", player.getLocation().getWorld().getName());
                                config.set("x", player.getLocation().getX());
                                config.set("y", player.getLocation().getY());
                                config.set("z", player.getLocation().getZ());
                                config.save(factionWarp);

                                setFactionPower(factionName, getFactionPower(factionName)-2);

                                player.sendMessage("§7You have set faction warp §c"+warpName+"§7.");
                            }catch(Exception e){
                                e.printStackTrace();
                                player.sendMessage("§cError setting warp.");
                            }
                        }else{
                            player.sendMessage("§cYou can only set your factions home within your claim.");
                        }
                    }else{
                        player.sendMessage("§cInsufficient permissions to set warp.");
                    }
                }else{
                    player.sendMessage("§cYou aren't a part of any faction.");
                }
            }else{
                player.sendMessage("§cThe name exceeds character requirements.");
            }
        }else{
            player.sendMessage("§cPlease specify a warp name.");
        }
    }

    //PERFECT
    private void removeWarp(Player player, String[] args){
        if(args.length > 1){
            String warpName = args[1];
            if(warpName.length() < 13 && warpName.length() > 1){
                String factionName = getFaction(player);
                if(factionName != null){
                    if(getPlayerRank(player, factionName) > 1){
                        if(inClaim(player.getLocation().getChunk()).equals(factionName)){
                            try{
                                File factionWarp = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"warps"+File.separator+warpName+".yml");
                                if(factionWarp.exists()){
                                    factionWarp.delete();
                                }

                                setFactionPower(factionName, getFactionPower(factionName)+2);

                                player.sendMessage("§7You have removed faction warp §c"+warpName+"§7.");
                            }catch(Exception e){
                                e.printStackTrace();
                                player.sendMessage("§cError removing warp.");
                            }
                        }else{
                            player.sendMessage("§cYou can only set your factions home within your claim.");
                        }
                    }else{
                        player.sendMessage("§cInsufficient permissions to set warp.");
                    }
                }else{
                    player.sendMessage("§cYou aren't a part of any faction.");
                }
            }else{
                player.sendMessage("§cThe name exceeds character requirements.");
            }
        }else{
            player.sendMessage("§cPlease specify a warp name.");
        }
    }

    private void factionChat(Player player){
        String factionName = getFaction(player);
        if(factionName != null){
            if(factionChat.contains(player)){
                factionChat.remove(player);
                player.sendMessage("§7You have switched to §cglobal§7 chat.");
            }else{
                factionChat.add(player);
                player.sendMessage("§7You have switched to §cfaction§7 chat.");
            }
        }else{
            player.sendMessage("§cYou aren't a part of any faction.");
        }
    }

    //PERFECT
    private void factionMap(Player player){
        if(mapFactions.contains(player)){
            mapFactions.remove(player);

            if(mappedChunks.containsKey(player)){
                for(Location location : mappedChunks.get(player)){
                    renewMappedLandscape(player, location.getChunk());
                }
            }

            mappedChunks.remove(player);
            player.sendMessage("§7You have are no longer mapping factions around you.");

        }else{
            mapFactions.add(player);
            player.sendMessage("§7You have are now mapping factions around you.");
        }
    }
}

package unet.Factions;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import unet.Factions.Claim.Claim;
import unet.Factions.Faction.MyFaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static unet.Factions.Handlers.Config.*;
import static unet.Factions.Handlers.GeneralHandler.*;
import static unet.Factions.Claim.ClaimHandler.*;
import static unet.Factions.Faction.FactionHandler.*;
import static unet.Factions.Handlers.Colors.*;
import static unet.Factions.Handlers.MapHandler.*;
import static unet.Factions.Handlers.PlayerResolver.*;
import static unet.Factions.Main.plugin;

public class FactionCommands implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args){
        if(commandSender instanceof Player){
            if(args.length > 0){
                String cmd = args[0].toLowerCase();
                switch(cmd){
                    case "help":
                        return help(((Player) commandSender), args);

                    case "?":
                        return help(((Player) commandSender), args);

                    case "create":
                        return create(((Player) commandSender), args);

                    case "invite":
                        return invite(((Player) commandSender), args);

                    case "join":
                        return join(((Player) commandSender));

                    case "leave":
                        return leave(((Player) commandSender));

                    case "remove":
                        return remove(((Player) commandSender), args);

                    case "promote":
                        return promote(((Player) commandSender), args);

                    case "demote":
                        return demote(((Player) commandSender), args);

                    case "disband":
                        return disband(((Player) commandSender));

                    case "claim":
                        return claim(((Player) commandSender), args);

                    case "unclaim":
                        return unclaim(((Player) commandSender), args);

                    case "autoclaim":
                        return autoClaim(((Player) commandSender), args);

                    case "autounclaim":
                        return autoUnclaim(((Player) commandSender), args);

                    case "home":
                        return home(((Player) commandSender));

                    case "sethome":
                        return setHome(((Player) commandSender));

                    case "warps":
                        return warps(((Player) commandSender), args);

                    case "warp":
                        return warp(((Player) commandSender), args);

                    case "setwarp":
                        return setWarp(((Player) commandSender), args);

                    case "delwarp":
                        return removeWarp(((Player) commandSender), args);

                    case "power":
                        return power(((Player) commandSender));

                    case "list":
                        return list(((Player) commandSender), args);

                    case "rename":
                        return rename(((Player) commandSender), args);

                    case "chat":
                        return chat(((Player) commandSender));

                    case "setcolor":
                        return setColor(((Player) commandSender), args);

                    case "setdesc":
                        return setDescription(((Player) commandSender), args);

                    case "map":
                        return map(((Player) commandSender));

                    case "rank":
                        return rank(((Player) commandSender));

                    case "chown":
                        return changeOwnership(((Player) commandSender), args);

                    case "setpower":
                        return setPower(((Player) commandSender), args);

                    case "version":
                        commandSender.sendMessage("§7MyFaction version §c"+plugin.getDescription().getVersion()+"§7 by DrBrad.");
                        return true;
                }

            }else{
                commandSender.sendMessage("§7Type §c/f help§7 to see a list of commands.");
                return true;
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args){
        if(commandSender instanceof Player){
            if(args.length > 0){
                String cmd = args[0].toLowerCase();
                MyFaction faction = getPlayersFaction(((Player) commandSender).getUniqueId());
                ArrayList<String> tabComplete = new ArrayList<>();

                if(args.length > 1){
                    switch(cmd){
                        case "create":
                            tabComplete.add("FACTION_NAME");
                            break;

                        case "invite":
                            if(faction != null){
                                for(Player player : Bukkit.getOnlinePlayers()){
                                    tabComplete.add(player.getName());
                                }
                            }
                            break;

                        case "remove":
                            if(faction != null){
                                for(String uuid : faction.getPlayers()){
                                    tabComplete.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
                                }
                            }
                            break;

                        case "promote":
                            if(faction != null){
                                for(String uuid : faction.getPlayers()){
                                    tabComplete.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
                                }
                            }
                            break;

                        case "demote":
                            if(faction != null){
                                for(String uuid : faction.getPlayers()){
                                    tabComplete.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
                                }
                            }
                            break;

                        case "warp":
                            if(faction != null){
                                tabComplete.addAll(faction.getWarps());
                            }
                            break;

                        case "setwarp":
                            if(faction != null){
                                tabComplete.addAll(faction.getWarps());
                            }
                            break;

                        case "delwarp":
                            if(faction != null){
                                tabComplete.addAll(faction.getWarps());
                            }
                            break;

                        case "rename":
                            if(faction != null){
                                tabComplete.add(faction.getName());
                            }
                            break;

                        case "chown":
                            if(faction != null){
                                for(String uuid : faction.getPlayers()){
                                    tabComplete.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
                                }
                            }
                            break;

                        case "setcolor":
                            if(faction != null){
                                tabComplete.addAll(getAllColors());
                            }
                            break;

                        case "setdesc":
                            if(faction != null){
                                tabComplete.add(faction.getDescription());
                            }
                            break;

                        case "setpower":
                            if(args.length == 3){
                                if(isFaction(args[1].toLowerCase())){
                                    tabComplete.add(getFactionFromName(args[1].toLowerCase()).getPower()+"");
                                }

                            }else if(args.length == 2){
                                tabComplete.addAll(getListOfFactionNames());
                            }
                            break;

                        case "claim":
                            tabComplete.add("safezone");
                            tabComplete.add("pvpzone");
                            break;

                        case "unclaim":
                            tabComplete.add("safezone");
                            tabComplete.add("pvpzone");
                            break;

                        case "autoclaim":
                            tabComplete.add("safezone");
                            tabComplete.add("pvpzone");
                            break;

                        case "autounclaim":
                            tabComplete.add("safezone");
                            tabComplete.add("pvpzone");
                            break;
                    }
                }else{
                    tabComplete.add("help");
                    tabComplete.add("?");
                    tabComplete.add("create");
                    tabComplete.add("invite");
                    tabComplete.add("join");
                    tabComplete.add("leave");
                    tabComplete.add("remove");
                    tabComplete.add("promote");
                    tabComplete.add("demote");
                    tabComplete.add("disband");
                    tabComplete.add("claim");
                    tabComplete.add("unclaim");
                    tabComplete.add("autoclaim");
                    tabComplete.add("autounclaim");
                    tabComplete.add("home");
                    tabComplete.add("sethome");
                    tabComplete.add("warps");
                    tabComplete.add("warp");
                    tabComplete.add("setwarp");
                    tabComplete.add("delwarp");
                    tabComplete.add("power");
                    tabComplete.add("list");
                    tabComplete.add("rename");
                    tabComplete.add("chat");
                    tabComplete.add("setcolor");
                    tabComplete.add("setdesc");
                    tabComplete.add("map");
                    tabComplete.add("rank");
                    tabComplete.add("setpower");
                    tabComplete.add("version");
                }

                return tabComplete;
            }
        }

        return null;
    }

    private boolean help(Player player, String[] args){
        if(player.hasPermission("f.help")){
            if(args.length > 1){
                if(args[1].equals("2")){
                    player.sendMessage("§c------- §fFaction commands (2/5) §c-------");
                    player.sendMessage("§c/f unclaim: §7Removes claim from your faction.");
                    player.sendMessage("§c/f autoclaim: §7Automatically claim a chunk for your faction.");
                    player.sendMessage("§c/f unclaim: §7Automatically removes claim from your faction.");
                    player.sendMessage("§c/f home: §7Teleport to your factions home.");
                    player.sendMessage("§c/f sethome: §7Set your factions home.");
                    player.sendMessage("§c/f warp: §7Teleport to one of your factions warps.");
                    player.sendMessage("§c/f warps: §7Lists all warps for your faction.");
                    player.sendMessage("§c/f setwarp: §7Set a warp for your faction.");
                    player.sendMessage("§c/f delwarp: §7Removes a warp from your faction.");
                    return true;

                }else if(args[1].equals("3")){
                    player.sendMessage("§c------- §fFaction commands (3/5) §c-------");
                    player.sendMessage("§c/f power: §7Check yours or another factions power.");
                    player.sendMessage("§c/f list: §7List of all of the factions.");
                    player.sendMessage("§c/f rename: §7Rename your faction something else.");
                    player.sendMessage("§c/f chat: §7Chat with only faction members or globally.");
                    player.sendMessage("§c/f map: §7See all faction claims chunks visually.");
                    player.sendMessage("§c/f rank: §7Get your rank in faction.");
                    player.sendMessage("§c/f chown: §7Change faction ownership.");
                    player.sendMessage("§c/f setpower: §7Set factions power.");
                    player.sendMessage("§c/f claim safezone: §7Claim Safe-Zone for server.");
                    return true;

                }else if(args[1].equals("4")){
                    player.sendMessage("§c------- §fFaction commands (4/5) §c-------");
                    player.sendMessage("§c/f unclaim safezone: §7Unclaim Safe-Zone for server.");
                    player.sendMessage("§c/f autoclaim safezone: §7Automatically claims safezone.");
                    player.sendMessage("§c/f autounclaim safezone: §7Automatically removes safezone claims.");
                    player.sendMessage("§c/f claim pvpzone: §7Claim Pvp-Zone for server.");
                    player.sendMessage("§c/f unclaim pvpzone: §7Unclaim Pvp-Zone for server.");
                    player.sendMessage("§c/f autoclaim pvpzone: §7Automatically claims pvpzone.");
                    player.sendMessage("§c/f autounclaim pvpzone: §7Automatically removes pvpzone claims.");
                    player.sendMessage("§c/f setdesc: §7Set factions description.");
                    player.sendMessage("§c/f setcolor: §7Set factions color.");
                    return true;

                }else if(args[1].equals("5")){
                    player.sendMessage("§c------- §fFaction commands (5/5) §c-------");
                    player.sendMessage("§c/f version: §7Get the version of this plugin.");
                    return true;
                }
            }

            player.sendMessage("§c------- §fFaction commands (1/5) §c-------");
            player.sendMessage("§c/f create: §7Creates a faction.");
            player.sendMessage("§c/f invite: §7Invites player to faction.");
            player.sendMessage("§c/f join: §7Join faction from invite.");
            player.sendMessage("§c/f leave: §7Leave your faction.");
            player.sendMessage("§c/f remove: §7Remove player from your faction.");
            player.sendMessage("§c/f promote: §7Promote player in faction.");
            player.sendMessage("§c/f demote: §7Demote player in faction.");
            player.sendMessage("§c/f disband: §7Deletes your faction.");
            player.sendMessage("§c/f claim: §7Claim a chunk for your faction.");

        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean list(Player player, String[] args){
        if(player.hasPermission("f.list")){
            int page = 0;
            if(args.length > 1){
                page = Integer.parseInt(args[1]);
            }

            ArrayList<MyFaction> factions = getListOfFactions();

            if(factions != null && factions.size() > 0){
                player.sendMessage("§c------- §fList of Factions (1/"+(((factions.size()/9)*page)+1)+") §c-------");

                for(int i = page*9; i < (page+1)*9; i++){
                    if(i < factions.size()){
                        int power = factions.get(i).getPower();
                        if(power > 0){
                            player.sendMessage("§c"+factions.get(i).getName()+"§7 power §a"+power+"§7 "+factions.get(i).getDescription());
                        }else{
                            player.sendMessage("§c"+factions.get(i).getName()+"§7 power §c"+power+"§7 "+factions.get(i).getDescription());
                        }
                    }else{
                        break;
                    }
                }
            }else{
                player.sendMessage("§cThere are no factions currently.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean create(Player player, String[] args){
        if(player.hasPermission("f.create")){
            if(args.length > 1){
                String name = args[1];
                if(name.length() < 13 && name.length() > 1){
                    if(!isBannedName(name)){
                        MyFaction faction = new MyFaction().create(name, player.getUniqueId());
                        if(faction != null){
                            createFaction(player.getUniqueId(), faction);
                            player.sendMessage("§7You have successfully created the faction §c"+name+"§7.");
                        }else{
                            player.sendMessage("§cFailed to create faction.");
                        }
                    }else{
                        player.sendMessage("§cFaction name is not allowed.");
                    }
                }else{
                    player.sendMessage("§cFaction name exceeds character requirements.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a faction name.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean rename(Player player, String[] args){
        if(player.hasPermission("f.rename")){
            if(args.length > 1){
                String name = args[1];
                if(name.length() < 13 && name.length() > 1){
                    if(!isBannedName(name)){
                        MyFaction faction = getPlayersFaction(player.getUniqueId());
                        if(faction != null){
                            String oldName = faction.getName();
                            if(faction.rename(name, player.getUniqueId())){
                                renameFaction(oldName, name);
                                player.sendMessage("§7You have successfully created the faction §c"+name+"§7.");

                            }else{
                                player.sendMessage("§cFailed to rename faction.");
                            }
                        }else{
                            player.sendMessage("§cYou aren't a part of a faction.");
                        }
                    }else{
                        player.sendMessage("§cFaction name is not allowed.");
                    }
                }else{
                    player.sendMessage("§cFaction name exceeds character requirements.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a faction name.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean changeOwnership(Player player, String[] args){
        if(player.hasPermission("f.chown")){
            if(args.length > 1){
                String name = args[1];

                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    OfflinePlayer receiver = getPlayer(name);

                    if(receiver != null){
                        faction.changeOwnership(player.getUniqueId(), receiver.getUniqueId());
                    }else{
                        player.sendMessage("§cPlayer specified doesn't exist.");
                    }
                }else{
                    player.sendMessage("§cYou are not a part of a faction.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a player you wish to change faction ownership to.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean disband(Player player){
        if(player.hasPermission("f.disband")){
            MyFaction faction = getPlayersFaction(player.getUniqueId());
            if(faction != null){
                if(faction.disband(player.getUniqueId())){
                    deleteFaction(faction);
                    player.sendMessage("§7You have successfully disbanded the faction §c"+faction.getName()+"§7.");

                }else{
                    player.sendMessage("§cYou must be faction owner to disband faction.");
                }
            }else{
                player.sendMessage("§cYou are not a part of a faction.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean invite(Player player, String[] args){
        if(player.hasPermission("f.invite")){
            if(args.length > 1){
                String name = args[1];

                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    Player receiver = Bukkit.getPlayer(name);
                    if(receiver != null && player.isOnline()){
                        if(faction.canInvite(player.getUniqueId(), receiver.getUniqueId())){
                            inviteToFaction(receiver.getUniqueId(), faction.getKey());

                            player.sendMessage("§7You have invited §a"+receiver.getName()+"§7 to the faction.");
                            receiver.sendMessage("§a"+player.getName()+"§7 has invited you to the faction §c"+faction.getName()+"§7.");

                            setPlayerInviteTask(receiver, Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                                @Override
                                public void run(){
                                    if(hasInviteToFaction(receiver.getUniqueId())){
                                        removeInviteToFaction(receiver.getUniqueId());
                                        removePlayerInviteTask(receiver);

                                        player.getPlayer().sendMessage("§c"+receiver.getName()+"§7 invitation has expired.");
                                        player.getPlayer().sendMessage("§7Invite to the faction §c"+faction.getName()+"§7 has expired.");
                                    }
                                }
                            }, 600));

                        }else{
                            player.sendMessage("§cYou must be a faction recruit to invite players.");
                        }
                    }else{
                        player.sendMessage("§cThe player you specified ether doesn't exist or is not online.");
                    }
                }else{
                    player.sendMessage("§cYou are not a part of a faction.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a faction name.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean join(Player player){
        if(player.hasPermission("f.join")){
            MyFaction faction = getFactionInvite(player.getUniqueId());
            if(faction != null){
                faction.join(player.getUniqueId());
                addPlayerToFaction(player.getUniqueId(), faction.getKey());
                player.sendMessage("§7You have joined the faction §a"+faction.getName()+"§7.");

                for(String uuid : faction.getPlayers()){
                    OfflinePlayer member = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    if(member != null && member.isOnline() && !uuid.equals(player.getUniqueId())){
                        member.getPlayer().sendMessage("§a"+player.getName()+"§7 has joined the faction!");
                    }
                }
            }else{
                player.sendMessage("§cYou have no faction invites.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean remove(Player player, String[] args){
        if(player.hasPermission("f.remove")){
            if(args.length > 1){
                String name = args[1];

                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    OfflinePlayer receiver = getPlayer(name);

                    if(receiver != null){
                        if(faction.remove(player.getUniqueId(), receiver.getUniqueId())){
                            removePlayerFromFaction(receiver.getUniqueId());
                            player.sendMessage("§7You have removed §a"+receiver.getName()+" from the faction.");

                            for(String uuid : faction.getPlayers()){
                                OfflinePlayer member = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                                if(member != null && member.isOnline() && !uuid.equals(player.getUniqueId().toString())){
                                    member.getPlayer().sendMessage("§c"+receiver.getName()+"§7 has been kicked from the faction!");
                                }
                            }

                            if(receiver.isOnline()){
                                receiver.getPlayer().sendMessage("§cYou have been kicked from the faction!");
                            }
                        }else{
                            player.sendMessage("§cYou must be at least a faction admin to remove players.");
                        }
                    }else{
                        player.sendMessage("§cThe player specified doesn't exist.");
                    }
                }else{
                    player.sendMessage("§cYou are not a part of a faction.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a player you wish to remove from the faction.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean leave(Player player){
        if(player.hasPermission("f.leave")){
            MyFaction faction = getPlayersFaction(player.getUniqueId());
            if(faction != null){
                if(faction.leave(player.getUniqueId())){
                    removePlayerFromFaction(player.getUniqueId());
                    player.sendMessage("§7You have left the faction §c"+faction.getName()+"§7!");

                    for(String uuid : faction.getPlayers()){
                        OfflinePlayer member = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                        if(member != null && member.isOnline() && !uuid.equals(player.getUniqueId())){
                            member.getPlayer().sendMessage("§c"+player.getName()+"§7 has been left the faction!");
                        }
                    }
                }else{
                    player.sendMessage("§cFaction owners cannot leave the faction, only change ownership or disband.");
                }
            }else{
                player.sendMessage("§cYou are not a part of a faction.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean promote(Player player, String[] args){
        if(player.hasPermission("f.promote")){
            if(args.length > 1){
                String name = args[1];

                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    OfflinePlayer receiver = getPlayer(name);

                    if(receiver != null){
                        if(faction.promote(player.getUniqueId(), receiver.getUniqueId())){
                            String[] names = getRanks();
                            int rank = faction.getRank(receiver.getUniqueId());
                            player.sendMessage("§7You have promoted §a"+receiver.getName()+"§7 to §a"+names[rank]+"§7.");

                            for(String uuid : faction.getPlayers()){
                                OfflinePlayer member = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                                if(member != null && member.isOnline() && !uuid.equals(player.getUniqueId().toString())){
                                    member.getPlayer().sendMessage("§a"+receiver.getName()+"§7 has been promoted to §a"+names[rank]+"§7.");
                                }
                            }
                            return true;

                        }else{
                            player.sendMessage("§cYou must be at least a faction admin to promote players.");
                        }
                    }else{
                        player.sendMessage("§cThe player specified doesn't exist.");
                    }
                }else{
                    player.sendMessage("§cYou are not a part of a faction.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a player you wish to promote.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean demote(Player player, String[] args){
        if(player.hasPermission("f.demote")){
            if(args.length > 1){
                String name = args[1];

                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    OfflinePlayer receiver = getPlayer(name);

                    if(receiver != null){
                        if(faction.demote(player.getUniqueId(), receiver.getUniqueId())){
                            String[] names = getRanks();
                            int rank = faction.getRank(receiver.getUniqueId());
                            player.sendMessage("§7You have demoted §a"+receiver.getName()+"§7 to §a"+names[rank]+"§7.");

                            for(String uuid : faction.getPlayers()){
                                OfflinePlayer member = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                                if(member != null && member.isOnline() && !uuid.equals(player.getUniqueId().toString())){
                                    member.getPlayer().sendMessage("§a"+receiver.getName()+"§7 has been demoted to §a"+names[rank]+"§7.");
                                }
                            }
                        }else{
                            player.sendMessage("§cYou must be at least a faction admin to demote players.");
                        }
                    }else{
                        player.sendMessage("§cThe player specified doesn't exist.");
                    }
                }else{
                    player.sendMessage("§cYou are not a part of a faction.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a player you wish to demote.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean claim(Player player, String[] args){
        Chunk chunk = player.getLocation().getChunk();
        if(args.length > 1){
            if(player.hasPermission("f.admin") || player.isOp()){
                if(args[1].equalsIgnoreCase("safezone") || args[1].equalsIgnoreCase("safe-zone")){
                    claimChunk(player, chunk, getSafeZone());
                }else if(args[1].equalsIgnoreCase("pvpzone") || args[1].equalsIgnoreCase("pvp-zone")){
                    claimChunk(player, chunk, getPvpZone());
                }
            }else{
                player.sendMessage("§cYou don't have permission to perform this command.");
            }

        }else{
            if(player.hasPermission("f.claim")){
                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    claimChunk(player, chunk, faction);
                }else{
                    player.sendMessage("§cYou must be a part of a faction to claim.");
                }
            }else{
                player.sendMessage("§cYou don't have permission to perform this command.");
            }
        }
        return true;
    }

    private boolean autoClaim(Player player, String[] args){
        if(isAutoClaiming(player.getUniqueId())){
            if(getAutoClaim(player.getUniqueId()).isClaiming()){
                stopAutoClaiming(player.getUniqueId());
                player.sendMessage("§7You are no longer §cauto claiming§7.");
            }else{
                player.sendMessage("§7You must turn off §cauto unclaim§7 to auto claim.");
            }
            return true;
        }

        Chunk chunk = player.getLocation().getChunk();
        if(args.length > 1){
            if(player.hasPermission("f.admin") || player.isOp()){
                if(args[1].equalsIgnoreCase("safezone") || args[1].equalsIgnoreCase("safe-zone")){
                    startAutoClaiming(player.getUniqueId(), getSafeZone(), true);
                    claimChunk(player, chunk, getSafeZone());
                    player.sendMessage("§7You are now §aauto claiming§7 for Safe Zones.");

                }else if(args[1].equalsIgnoreCase("pvpzone") || args[1].equalsIgnoreCase("pvp-zone")){
                    startAutoClaiming(player.getUniqueId(), getPvpZone(), true);
                    claimChunk(player, chunk, getPvpZone());
                    player.sendMessage("§7You are now §aauto claiming§7 for PVP Zones.");
                }
            }else{
                player.sendMessage("§cYou don't have permission to perform this command.");
            }

        }else{
            if(player.hasPermission("f.autoclaim")){
                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    startAutoClaiming(player.getUniqueId(), faction, true);
                    claimChunk(player, chunk, faction);
                    player.sendMessage("§7You are now §aauto claiming§7 for "+faction.getName()+".");
                }else{
                    player.sendMessage("§cYou are not a part of a faction.");
                }
            }else{
                player.sendMessage("§cYou don't have permission to perform this command.");
            }
        }
        return true;
    }

    private boolean unclaim(Player player, String[] args){
        Chunk chunk = player.getLocation().getChunk();
        if(args.length > 1){
            if(player.hasPermission("f.admin") || player.isOp()){
                if(args[1].equalsIgnoreCase("safezone") || args[1].equalsIgnoreCase("safe-zone")){
                    unclaimChunk(player, chunk, getSafeZone());

                }else if(args[1].equalsIgnoreCase("pvpzone") || args[1].equalsIgnoreCase("pvp-zone")){
                    unclaimChunk(player, chunk, getPvpZone());
                }
            }else{
                player.sendMessage("§cYou don't have permission to perform this command.");
            }

        }else{
            if(player.hasPermission("f.unclaim")){
                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    unclaimChunk(player, chunk, faction);
                }else{
                    player.sendMessage("§cYou must be a part of a faction to unclaim.");
                }
            }else{
                player.sendMessage("§cYou don't have permission to perform this command.");
            }
        }
        return true;
    }

    private boolean autoUnclaim(Player player, String[] args){
        if(isAutoClaiming(player.getUniqueId())){
            if(!getAutoClaim(player.getUniqueId()).isClaiming()){
                stopAutoClaiming(player.getUniqueId());
                player.sendMessage("§7You are no longer §cauto unclaiming§7.");
            }else{
                player.sendMessage("§7You must turn off §cauto claim§7 to auto unclaim.");
            }
            return true;
        }

        Chunk chunk = player.getLocation().getChunk();
        if(args.length > 1){
            if(player.hasPermission("f.admin") || player.isOp()){
                if(args[1].equalsIgnoreCase("safezone") || args[1].equalsIgnoreCase("safe-zone")){
                    startAutoClaiming(player.getUniqueId(), getSafeZone(), false);
                    claimChunk(player, chunk, getSafeZone());
                    player.sendMessage("§7You are now §aauto unclaiming§7 for Safe Zones.");

                }else if(args[1].equalsIgnoreCase("pvpzone") || args[1].equalsIgnoreCase("pvp-zone")){
                    startAutoClaiming(player.getUniqueId(), getPvpZone(), false);
                    claimChunk(player, chunk, getPvpZone());
                    player.sendMessage("§7You are now §aauto unclaiming§7 for PVP Zones.");
                }
            }else{
                player.sendMessage("§cYou don't have permission to perform this command.");
            }

        }else{
            if(player.hasPermission("f.autounclaim")){
                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    startAutoClaiming(player.getUniqueId(), faction, false);
                    claimChunk(player, chunk, faction);
                    player.sendMessage("§7You are now §aauto unclaiming§7 for "+faction.getName()+".");
                }else{
                    player.sendMessage("§cYou are not a part of a faction.");
                }
            }else{
                player.sendMessage("§cYou don't have permission to perform this command.");
            }
        }
        return true;
    }

    private boolean rank(Player player){
        if(player.hasPermission("f.rank")){
            MyFaction faction = getPlayersFaction(player.getUniqueId());
            if(faction != null){
                String[] names = getRanks();
                player.sendMessage("§7Your rank in the faction is: §a"+names[faction.getRank(player.getUniqueId())]+"§7.");

            }else{
                player.sendMessage("§cYou are not a part of a faction.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean power(Player player){
        if(player.hasPermission("f.power")){
            MyFaction faction = getPlayersFaction(player.getUniqueId());
            if(faction != null){
                if(faction.getPower() > 0){
                    player.sendMessage("§7Your factions power level is: §a"+faction.getPower()+"§7.");
                }else{
                    player.sendMessage("§7Your factions power level is: §c"+faction.getPower()+"§7.");
                }

            }else{
                player.sendMessage("§cYou are not a part of a faction.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean setPower(Player player, String[] args){
        if(args.length > 2){
            if(player.hasPermission("f.admin") || player.isOp()){
                String name = args[1];

                MyFaction faction = getFactionFromName(name);
                if(faction != null){
                    int power = Integer.parseInt(args[2]);
                    faction.setPower(power);
                    player.sendMessage("§7You have set §c"+faction.getName()+"§7 power level to §a"+power+"§7.");

                }else{
                    player.sendMessage("§cFaction specified doesn't exist.");
                }
            }else{
                player.sendMessage("§cYou don't have permission to perform this command.");
            }
            return true;
        }else{
            player.sendMessage("§cPlease specify a faction name and a power level.");
        }
        return false;
    }

    private boolean setDescription(Player player, String[] args){
        if(player.hasPermission("f.setdesc")){
            if(args.length > 1){
                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    String builder = "";
                    for(int i = 1; i < args.length; i++){
                        builder += args[i]+" ";
                    }

                    builder = builder.replaceAll("&", "§");
                    if(faction.setDescription(player.getUniqueId(), builder.substring(0, builder.length()-1))){
                        player.sendMessage("§7You have set the factions description§7.");
                    }else{
                        player.sendMessage("§cYou must be at least a faction admin to set factions color.");
                    }
                }else{
                    player.sendMessage("§cYou are not in a faction.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease state a description for your faction.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean setColor(Player player, String[] args){
        if(player.hasPermission("f.setcolor")){
            if(args.length > 1){
                String color = args[1];

                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    int colorCode = getColorCode(color);
                    if(faction.setColor(player.getUniqueId(), colorCode)){
                        player.sendMessage("§7You have set the faction color to "+getChatColor(colorCode)+color+"§7.");

                    }else{
                        player.sendMessage("§cYou must be at least a faction admin to set factions color.");
                    }
                }else{
                    player.sendMessage("§cYou are not in a faction.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a color for your faction.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean chat(Player player){
        if(player.hasPermission("f.chat")){
            if(isChatting(player.getUniqueId())){
                stopChatting(player.getUniqueId());
                player.sendMessage("§7Your now chatting §aglobally§7.");

            }else if(isPlayerInFaction(player.getUniqueId())){
                startChatting(player.getUniqueId());
                player.sendMessage("§7Your now chatting with only §afaction§7 members of your faction.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean map(Player player){
        if(player.hasPermission("f.map")){
            if(isMapping(player.getUniqueId())){
                stopMapping(player);
                player.sendMessage("§7Your are no longer §cmapping§7 claimed chunks.");

            }else{
                startMapping(player);
                player.sendMessage("§7Your are now §amapping§7 claimed chunks.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean home(Player player){
        if(player.hasPermission("f.home")){
            if(isFactionHome()){
                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    Location home = faction.getHome();
                    if(home != null){
                        teleport(player, home, "faction Home", getColorRGB(faction.getColor()));

                    }else{
                        player.sendMessage("§cYour faction doesn't have a home set.");
                    }
                }else{
                    player.sendMessage("§cYour not a part of a faction.");
                }
            }else{
                player.sendMessage("§cServer has faction homes disabled.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean setHome(Player player){
        if(player.hasPermission("f.sethome")){
            if(isFactionHome()){
                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    Chunk chunk = player.getLocation().getChunk();
                    if(inClaim(chunk)){
                        Claim claim = getClaim(chunk);

                        if(faction.getKey().equals(claim.getKey())){
                            if(faction.canClaim(player.getUniqueId())){
                                faction.setHome(player.getLocation());
                                player.sendMessage("§7Your have set your factions §ahome§7.");

                            }else{
                                player.sendMessage("§cYou must be at least faction admin to set faction home.");
                            }
                        }else{
                            player.sendMessage("§cYou can only set factions home in your own claim.");
                        }
                    }else{
                        player.sendMessage("§cYou can only set factions home in your own claim.");
                    }
                }else{
                    player.sendMessage("§cYour not a part of a faction.");
                }
            }else{
                player.sendMessage("§cServer has faction homes disabled.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    public boolean warp(Player player, String[] args){
        if(player.hasPermission("f.warp")){
            if(isFactionWarp()){
                if(args.length > 1){
                    MyFaction faction = getPlayersFaction(player.getUniqueId());
                    if(faction != null){
                        String warpName = args[1];
                        Location warp = faction.getWarp(warpName);

                        if(warp != null){
                            teleport(player, warp, "faction warp "+warpName, getColorRGB(faction.getColor()));

                        }else{
                            player.sendMessage("§cYour faction doesn't have a warp set with the name specified.");
                        }
                    }else{
                        player.sendMessage("§cYou are not a part of a faction.");
                    }
                    return true;
                }else{
                    player.sendMessage("§cPlease specify a warp name.");
                }
            }else{
                player.sendMessage("§cServer has faction warps disabled.");
                return true;
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    public boolean setWarp(Player player, String[] args){
        if(player.hasPermission("f.setwarp")){
            if(isFactionWarp()){
                if(args.length > 1){
                    MyFaction faction = getPlayersFaction(player.getUniqueId());
                    if(faction != null){
                        String warpName = args[1];

                        if(warpName.length() < 13 && warpName.length() > 1){
                            if(faction.canClaim(player.getUniqueId())){
                                if(inClaim(player.getLocation().getChunk())){
                                    Claim claim = getClaim(player.getLocation().getChunk());
                                    if(claim.getKey().equals(faction.getKey())){
                                        if(faction.getPower() >= getCreateWarpCost()){
                                            if(!faction.isWarp(warpName)){
                                                faction.setWarp(warpName, player.getLocation());
                                                player.sendMessage("§7You have set the faction warp: §a"+warpName+"§7.");

                                            }else{
                                                player.sendMessage("§cYour faction already has warp with this name.");
                                            }
                                        }else{
                                            player.sendMessage("§cYour faction doesn't have enough power to set a warp.");
                                        }
                                    }else{
                                        player.sendMessage("§cYou can only set warps in your factions claims.");
                                    }
                                }else{
                                    player.sendMessage("§cYou can only set warps in your factions claims.");
                                }
                            }else{
                                player.sendMessage("§cYou must be at least a faction admin to set warps.");
                            }
                        }else{
                            player.sendMessage("§cThe warp name exceeds character requirements.");
                        }
                    }else{
                        player.sendMessage("§cYou are not a part of a faction.");
                    }
                    return true;
                }else{
                    player.sendMessage("§cPlease specify a warp name.");
                }
            }else{
                player.sendMessage("§cServer has faction warps disabled.");
                return true;
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    public boolean removeWarp(Player player, String[] args){
        if(player.hasPermission("f.delwarp")){
            if(isFactionWarp()){
                if(args.length > 1){
                    MyFaction faction = getPlayersFaction(player.getUniqueId());
                    if(faction != null){
                        String warpName = args[1];

                        if(faction.canClaim(player.getUniqueId())){
                            if(faction.isWarp(warpName)){
                                faction.removeWarp(warpName);
                                player.sendMessage("§7You have removed the faction warp: §a"+warpName+"§7.");

                            }else{
                                player.sendMessage("§cYour faction doesn't have a warp set with the name specified.");
                            }
                        }else{
                            player.sendMessage("§cYou must be at least a faction admin to set warps.");
                        }
                    }else{
                        player.sendMessage("§cYou are not a part of a faction.");
                    }
                    return true;
                }else{
                    player.sendMessage("§cPlease specify a warp name.");
                }
            }else{
                player.sendMessage("§cServer has faction warps disabled.");
                return true;
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    public boolean warps(Player player, String[] args){
        if(player.hasPermission("f.warps")){
            if(isFactionWarp()){
                int page = 0;
                if(args.length > 1){
                    page = Integer.parseInt(args[1]);
                }

                MyFaction faction = getPlayersFaction(player.getUniqueId());
                if(faction != null){
                    ArrayList<String> warps = faction.getWarps();

                    if(warps != null && warps.size() > 0){
                        player.sendMessage("§c------- §fList of Faction Warps (1/"+(((warps.size()/9)*page)+1)+") §c-------");

                        for(int i = page*9; i < (page+1)*9; i++){
                            if(i < warps.size()){
                                Location warp = faction.getWarp(warps.get(i));
                                player.sendMessage("§c"+warps.get(i)+"§7: Warp is located in the world: §c"+warp.getWorld().getName()+"§7.");
                            }else{
                                break;
                            }
                        }
                    }else{
                        player.sendMessage("§cYour faction has no warps.");
                    }
                }else{
                    player.sendMessage("§cYou are not in a faction.");
                }
            }else{
                player.sendMessage("§cServer has faction warps disabled.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }
}

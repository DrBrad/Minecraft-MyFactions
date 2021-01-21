package unet.Factions;

import org.bukkit.*;
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

import static unet.Factions.Config.*;
import static unet.Factions.Handlers.*;
import static unet.Factions.Main.*;
import static unet.Factions.Faction.*;

public class FactionCommands implements CommandExecutor {

    private HashMap<Player, String> invites = new HashMap<>();

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
                        return listWarps(((Player) commandSender));

                    case "warp":
                        return warp(((Player) commandSender), args);

                    case "setwarp":
                        return setWarp(((Player) commandSender), args);

                    case "delwarp":
                        return removeWarp(((Player) commandSender), args);

                    case "power":
                        return power(((Player) commandSender), args);

                    case "list":
                        return listFactions(((Player) commandSender));

                    case "rename":
                        return rename(((Player) commandSender), args);

                    case "chat":
                        return factionChat(((Player) commandSender));

                    case "map":
                        return factionMap(((Player) commandSender));

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

    //PERFECT
    private boolean help(Player player, String[] args){
        if(args.length > 1){
            if(args[1].equals("2")){
                player.sendMessage("§c------- §fFaction commands (2/4) §c-------");
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
                player.sendMessage("§c------- §fFaction commands (3/4) §c-------");
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
                player.sendMessage("§c------- §fFaction commands (4/4) §c-------");
                player.sendMessage("§c/f unclaim safezone: §7Unclaim Safe-Zone for server.");
                player.sendMessage("§c/f autoclaim safezone: §7Automatically claims safezone.");
                player.sendMessage("§c/f autounclaim safezone: §7Automatically removes safezone claims.");
                player.sendMessage("§c/f claim pvpzone: §7Claim Pvp-Zone for server.");
                player.sendMessage("§c/f unclaim pvpzone: §7Unclaim Pvp-Zone for server.");
                player.sendMessage("§c/f autoclaim pvpzone: §7Automatically claims pvpzone.");
                player.sendMessage("§c/f autounclaim pvpzone: §7Automatically removes pvpzone claims.");
                player.sendMessage("§c/f version: §7Get the version of this plugin.");
                return true;
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
        player.sendMessage("§c/f disband: §7Deletes your faction.");
        player.sendMessage("§c/f claim: §7Claim a chunk for your faction.");
        return true;
    }

    //MAKE WITH PAGES
    private boolean listFactions(Player player){
        File factions = new File(plugin.getDataFolder()+File.separator+"factions");
        if(factions.exists() && factions.listFiles().length > 0){
            String playersFaction = getFaction(player.getUniqueId());

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
        return true;
    }

    //PERFECT
    private boolean create(Player player, String[] args){
        if(args.length > 1){
            String factionName = args[1];

            if(factionName.length() < 13 && factionName.length() > 1){
                if(!factionName.equalsIgnoreCase("null") && !factionName.equalsIgnoreCase("Safe-Zone") &&
                        !factionName.equalsIgnoreCase("SafeZone") && !factionName.equalsIgnoreCase("Wilderness") &&
                        !factionName.equalsIgnoreCase("Pvp-Zone") && !factionName.equalsIgnoreCase("PvpZone")){
                    File factionFolder = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName);

                    if(!factionFolder.exists()){
                        if(getFaction(player.getUniqueId()) == null){
                            if(createFaction(player.getUniqueId(), factionName)){
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
            return true;
        }else{
            player.sendMessage("§cYou must include a name for your faction.");
        }
        return false;
    }

    //PERFECT
    private boolean rename(Player player, String[] args){
        if(args.length > 1){
            String newFactionName = args[1];

            if(newFactionName.length() < 13 && newFactionName.length() > 1){
                if(!newFactionName.equalsIgnoreCase("null")){
                    File factionFolder = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+newFactionName);

                    if(!factionFolder.exists()){
                        String factionName = getFaction(player.getUniqueId());
                        if(factionName != null){
                            if(getPlayerRank(player.getUniqueId(), factionName) == 3){
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
            return true;
        }else{
            player.sendMessage("§cYou must include a name for your faction.");
        }
        return false;
    }

    //PERFECT
    private boolean invite(Player player, String[] args){
        if(args.length > 1){
            String factionName = getFaction(player.getUniqueId());
            if(factionName != null){
                Player reqPlayer = plugin.getServer().getPlayer(args[1]);

                if(reqPlayer != null && reqPlayer.isOnline()){
                    if(player.getUniqueId() != reqPlayer.getUniqueId()){
                        if(getFaction(reqPlayer.getUniqueId()) == null){
                            if(getPlayerRank(player.getUniqueId(), factionName) > 0){
                                invites.put(reqPlayer.getPlayer(), factionName);
                                player.sendMessage("§7You have invited §c"+reqPlayer.getName()+"§7 to the faction.");
                                reqPlayer.getPlayer().sendMessage("§7"+player.getDisplayName()+" §7has invited you to join §c"+factionName+"§7, this invite will expire in §c30s§7.");

                                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                                    @Override
                                    public void run(){
                                        if(invites.containsKey(reqPlayer)){
                                            invites.remove(reqPlayer);
                                            reqPlayer.getPlayer().sendMessage("§7Faction invite to §c"+factionName+"§7 has expired!");
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
            return true;
        }else{
            player.sendMessage("§cYou must include a player name that you wish to invite.");
        }
        return false;
    }

    //PERFECT
    private boolean join(Player player){
        if(getFaction(player.getUniqueId()) == null){
            if(invites.containsKey(player)){
                String factionName = invites.get(player);

                if(addPlayerToFaction(player.getUniqueId(), factionName)){
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
        return true;
    }

    //PERFECT
    private boolean leave(Player player){
        String factionName = getFaction(player.getUniqueId());
        if(factionName != null){
            if(getPlayerRank(player.getUniqueId(), factionName) < 3){
                if(removePlayerFromFaction(player.getUniqueId(), factionName)){
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
        return true;
    }

    //PERFECT
    private boolean remove(Player player, String[] args){
        if(args.length > 1){
            String factionName = getFaction(player.getUniqueId());
            if(factionName != null){
                OfflinePlayer reqPlayer = resolvePlayer(args[1]);

                if(reqPlayer != null){
                    String reqFaction = getFaction(reqPlayer.getUniqueId());
                    if(reqFaction != null && reqFaction.equals(factionName)){
                        if(player.getUniqueId() != reqPlayer.getUniqueId()){
                            int rank = getPlayerRank(player.getUniqueId(), factionName);
                            if(rank > 1 && getPlayerRank(reqPlayer.getUniqueId(), factionName) < rank){
                                if(removePlayerFromFaction(reqPlayer.getUniqueId(), factionName)){
                                    player.sendMessage("§7You have removed §c"+reqPlayer.getName()+"§7 from the faction.");

                                    if(reqPlayer.isOnline()){
                                        reqPlayer.getPlayer().sendMessage("§7You have been removed from the faction by §c"+player.getDisplayName()+"§7.");
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
            return true;
        }else{
            player.sendMessage("§cYou must include a player name that you wish to remove.");
        }
        return false;
    }

    //PERFECT
    private boolean promote(Player player, String[] args){
        if(args.length > 1){
            String factionName = getFaction(player.getUniqueId());
            if(factionName != null){
                OfflinePlayer reqPlayer = resolvePlayer(args[1]);

                if(reqPlayer != null){
                    String reqFaction = getFaction(reqPlayer.getUniqueId());
                    if(reqFaction != null && reqFaction.equals(factionName)){
                        if(player.getUniqueId() != reqPlayer.getUniqueId()){
                            if(getPlayerRank(player.getUniqueId(), factionName) == 3){
                                int rank = getPlayerRank(reqPlayer.getUniqueId(), factionName);
                                if(rank < 2){
                                    if(promotePlayer(reqPlayer.getUniqueId(), factionName)){
                                        String[] names = { "Member", "Recruit", "Admin", "Owner" };
                                        player.sendMessage("§7You have promoted §c"+reqPlayer.getName()+"§7 to §c"+names[rank+1]+"§7.");

                                        if(reqPlayer.isOnline()){
                                            reqPlayer.getPlayer().sendMessage("§7You have been promoted to promoted to §c"+names[rank+1]+"§7 in faction.");
                                        }
                                    }else{
                                        player.sendMessage("§cFailed to promote player.");
                                    }
                                }else{
                                    player.sendMessage("§c"+reqPlayer.getName()+"§7 is already §cadmin§7, you can transfer your ownership by typing §c/f chown§7.");
                                }
                            }else{
                                player.sendMessage("§cYou must be the owner of the faction to promote.");
                            }
                        }else{
                            player.sendMessage("§cYou cannot promote yourself in the faction.");
                        }
                    }else{
                        player.sendMessage("§cYou cannot promote yourself from the faction.");
                    }
                }else{
                    player.sendMessage("§cThe player specified doesn't exist.");
                }
            }else{
                player.sendMessage("§cYou aren't in a faction.");
            }
            return true;
        }else{
            player.sendMessage("§cYou must include a player name that you wish to promote.");
        }
        return false;
    }

    //PERFECT
    private boolean demote(Player player, String[] args){
        if(args.length > 1){
            String factionName = getFaction(player.getUniqueId());
            if(factionName != null){
                OfflinePlayer reqPlayer = resolvePlayer(args[1]);

                if(reqPlayer != null){
                    String reqFaction = getFaction(reqPlayer.getUniqueId());
                    if(reqFaction != null && reqFaction.equals(factionName)){
                        if(player.getUniqueId() != reqPlayer.getUniqueId()){
                            if(getPlayerRank(player.getUniqueId(), factionName) == 3){
                                int rank = getPlayerRank(reqPlayer.getUniqueId(), factionName);
                                if(rank > 0){
                                    if(demotePlayer(reqPlayer.getUniqueId(), factionName)){
                                        String[] names = { "Member", "Recruit", "Admin", "Owner" };
                                        player.sendMessage("§7You have demoted §c"+reqPlayer.getName()+"§7 to §c"+names[rank-1]+"§7.");

                                        if(reqPlayer.isOnline()){
                                            reqPlayer.getPlayer().sendMessage("§7You have been demoted to §c"+names[rank-1]+"§7 in faction.");
                                        }
                                    }else{
                                        player.sendMessage("§cFailed to demote player.");
                                    }
                                }else{
                                    player.sendMessage("§cYou cannot demote a this person further.");
                                }
                            }else{
                                player.sendMessage("§cYou must be the owner of the faction to demote.");
                            }
                        }else{
                            player.sendMessage("§cYou cannot demote yourself in the faction.");
                        }
                    }else{
                        player.sendMessage("§cYou cannot demote yourself from the faction.");
                    }
                }else{
                    player.sendMessage("§cThe player specified doesn't exist.");
                }
            }else{
                player.sendMessage("§cYou aren't in a faction.");
            }
            return true;
        }else{
            player.sendMessage("§cYou must include a player name that you wish to demote.");
        }
        return false;
    }

    //PERFECT
    private boolean disband(Player player){
        String factionName = getFaction(player.getUniqueId());
        if(factionName != null){
            if(getPlayerRank(player.getUniqueId(), factionName) == 3){
                disbandFaction(factionName);
            }
        }else{
            player.sendMessage("§cYou aren't in a faction.");
        }
        return true;
    }

    //PERFECT
    private boolean claim(Player player, String[] args){
        if(args.length > 1){
            if(args[1].equalsIgnoreCase("safezone") || args[1].equalsIgnoreCase("safe-zone")){
                if(player.isOp()){
                    String claim = inClaim(player.getLocation().getChunk());
                    if(claim != null){
                        if(claim.equalsIgnoreCase("Safe-Zone")){
                            player.sendMessage("§cThis chunk is already claimed as Safe-Zone.");
                            return true;
                        }else if(claim.equalsIgnoreCase("Pvp-Zone")){
                            unclaimPvpZone(player.getLocation().getChunk());
                        }else{
                            unclaimForFaction(claim, player.getLocation().getChunk(), getFactionPower(claim));
                        }
                    }

                    if(claimSafeZone(player.getLocation().getChunk())){
                        player.sendMessage("§7You have claimed this chunk as a §aSafe-Zone§7!");

                        if(mappedChunks.containsKey(player)){
                            mappedChunks.get(player).remove(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());
                            viewClaims(player, player.getLocation().getChunk());
                        }
                    }else{
                        player.sendMessage("§cFailed to claim chunk as Safe-Zone.");
                    }
                }else{
                    player.sendMessage("§cOnly server admins can claim Safe-Zone.");
                }

            }else if(args[1].equalsIgnoreCase("pvpzone") || args[1].equalsIgnoreCase("pvp-zone")){
                if(player.isOp()){
                    String claim = inClaim(player.getLocation().getChunk());
                    if(claim != null){
                        if(claim.equalsIgnoreCase("Pvp-Zone")){
                            player.sendMessage("§cThis chunk is already claimed as Pvp-Zone.");
                            return true;
                        }else if(claim.equalsIgnoreCase("Safe-Zone")){
                            unclaimSafeZone(player.getLocation().getChunk());
                        }else{
                            unclaimForFaction(claim, player.getLocation().getChunk(), getFactionPower(claim));
                        }
                    }

                    if(claimPvpZone(player.getLocation().getChunk())){
                        player.sendMessage("§7You have claimed this chunk as a §aPvp-Zone§7!");

                        if(mappedChunks.containsKey(player)){
                            mappedChunks.get(player).remove(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());
                            viewClaims(player, player.getLocation().getChunk());
                        }
                    }else{
                        player.sendMessage("§cFailed to claim chunk as Pvp-Zone.");
                    }
                }else{
                    player.sendMessage("§cOnly server admins can claim Pvp-Zone.");
                }

            }else{
                player.sendMessage("§cIf you wish to claim a Safe-Zone or Pvp-Zone please specify that.");
            }

        }else{
            String factionName = getFaction(player.getUniqueId());
            if(factionName != null){
                if(getPlayerRank(player.getUniqueId(), factionName) > 1){
                    String claim = inClaim(player.getLocation().getChunk());
                    if(claim == null || getFactionPower(claim) < 0){
                        int power = getFactionPower(factionName);
                        if(power > claimPower-1){
                            if(claimForFaction(factionName, player.getLocation().getChunk(), power)){
                                player.sendMessage("§7You have claimed this chunk!");

                                if(mappedChunks.containsKey(player)){
                                    mappedChunks.get(player).remove(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());
                                    viewClaims(player, player.getLocation().getChunk());
                                }
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
        return true;
    }

    //PERFECT
    private boolean unclaim(Player player, String[] args){
        if(args.length > 1){
            if(args[1].equalsIgnoreCase("safezone") || args[1].equalsIgnoreCase("safe-zone")){
                if(player.isOp()){
                    String claim = inClaim(player.getLocation().getChunk());

                    if(claim != null && claim.equalsIgnoreCase("Safe-Zone")){
                        if(unclaimSafeZone(player.getLocation().getChunk())){
                            player.sendMessage("§7You have unclaimed §aSafe-Zone§7 chunk!");

                            if(mappedChunks.containsKey(player)){
                                mappedChunks.get(player).remove(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());
                                viewClaims(player, player.getLocation().getChunk());
                            }
                        }else{
                            player.sendMessage("§cFailed to unclaim Safe-Zone chunks.");
                        }
                    }else{
                        player.sendMessage("§cNo Safe-Zone chunk for you to unclaim.");
                    }
                }else{
                    player.sendMessage("§cOnly server admins can unclaim Safe-Zone.");
                }

            }else if(args[1].equalsIgnoreCase("pvpzone") || args[1].equalsIgnoreCase("pvp-zone")){
                if(player.isOp()){
                    String claim = inClaim(player.getLocation().getChunk());

                    if(claim != null && claim.equalsIgnoreCase("Pvp-Zone")){
                        if(unclaimPvpZone(player.getLocation().getChunk())){
                            player.sendMessage("§7You have unclaimed §aPvp-Zone§7 chunk!");

                            if(mappedChunks.containsKey(player)){
                                mappedChunks.get(player).remove(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());
                                viewClaims(player, player.getLocation().getChunk());
                            }
                        }else{
                            player.sendMessage("§cFailed to unclaim Pvp-Zone chunks.");
                        }
                    }else{
                        player.sendMessage("§cNo Pvp-Zone chunk for you to unclaim.");
                    }
                }else{
                    player.sendMessage("§cOnly server admins can unclaim Pvp-Zone.");
                }

            }else{
                player.sendMessage("§cIf you wish to claim a Safe-Zone or Pvp-Zone please specify that.");
            }

        }else{
            String factionName = getFaction(player.getUniqueId());
            if(factionName != null){
                if(getPlayerRank(player.getUniqueId(), factionName) > 1){
                    String claim = inClaim(player.getLocation().getChunk());
                    if(claim != null && claim.equals(factionName)){
                        if(unclaimForFaction(factionName, player.getLocation().getChunk(), getFactionPower(factionName))){
                            player.sendMessage("§7You have unclaimed this chunk!");

                            if(mappedChunks.containsKey(player)){
                                mappedChunks.get(player).remove(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());
                                viewClaims(player, player.getLocation().getChunk());
                            }
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
        return true;
    }

    //PERFECT
    private boolean autoClaim(Player player, String[] args){
        if(autoClaimList.containsKey(player)){
            if(autoClaimList.get(player).isClaiming()){
                autoClaimList.remove(player);
                player.sendMessage("§aAuto Claim§7 is no longer running!");
            }else{
                player.sendMessage("§cYou must turn off auto unclaim first.");
            }

        }else{
            if(args.length > 1){
                if(args[1].equalsIgnoreCase("safezone") || args[1].equalsIgnoreCase("safe-zone")){
                    if(player.isOp()){
                        String claim = inClaim(player.getLocation().getChunk());
                        if(claim != null){
                            if(claim.equalsIgnoreCase("Safe-Zone")){
                                player.sendMessage("§cThis chunk is already claimed as Safe-Zone.");
                                return true;
                            }else if(claim.equalsIgnoreCase("Pvp-Zone")){
                                unclaimPvpZone(player.getLocation().getChunk());
                            }else{
                                unclaimForFaction(claim, player.getLocation().getChunk(), getFactionPower(claim));
                            }
                        }

                        if(claimSafeZone(player.getLocation().getChunk())){
                            player.sendMessage("§7You have claimed this chunk as a §aSafe-Zone§7!");

                            if(mappedChunks.containsKey(player)){
                                mappedChunks.get(player).remove(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());
                                viewClaims(player, player.getLocation().getChunk());
                            }

                            autoClaimList.put(player, new AutoClaim(player, "Safe-Zone", player.getLocation().getChunk().getBlock(0, 0, 0).getLocation(), true));
                        }else{
                            player.sendMessage("§cFailed to claim chunk as Safe-Zone.");
                        }
                    }else{
                        player.sendMessage("§cOnly server admins can claim Safe-Zone.");
                    }

                }else if(args[1].equalsIgnoreCase("pvpzone") || args[1].equalsIgnoreCase("pvp-zone")){
                    if(player.isOp()){
                        String claim = inClaim(player.getLocation().getChunk());
                        if(claim != null){
                            if(claim.equalsIgnoreCase("Pvp-Zone")){
                                player.sendMessage("§cThis chunk is already claimed as Pvp-Zone.");
                                return true;
                            }else if(claim.equalsIgnoreCase("Safe-Zone")){
                                unclaimSafeZone(player.getLocation().getChunk());
                            }else{
                                unclaimForFaction(claim, player.getLocation().getChunk(), getFactionPower(claim));
                            }
                        }

                        if(claimPvpZone(player.getLocation().getChunk())){
                            player.sendMessage("§7You have claimed this chunk as a §aPvp-Zone§7!");

                            if(mappedChunks.containsKey(player)){
                                mappedChunks.get(player).remove(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());
                                viewClaims(player, player.getLocation().getChunk());
                            }

                            autoClaimList.put(player, new AutoClaim(player, "Pvp-Zone", player.getLocation().getChunk().getBlock(0, 0, 0).getLocation(), true));
                        }else{
                            player.sendMessage("§cFailed to claim chunk as Pvp-Zone.");
                        }
                    }else{
                        player.sendMessage("§cOnly server admins can claim Pvp-Zone.");
                    }

                }else{
                    player.sendMessage("§cIf you wish to claim a Safe-Zone or Pvp-Zone please specify that.");
                }

            }else{
                String factionName = getFaction(player.getUniqueId());
                if(factionName != null){
                    if(getPlayerRank(player.getUniqueId(), factionName) > 1){
                        String claim = inClaim(player.getLocation().getChunk());
                        if(claim == null || getFactionPower(claim) < 0){
                            int power = getFactionPower(factionName);
                            if(power > claimPower-1){
                                if(claimForFaction(factionName, player.getLocation().getChunk(), power)){
                                    player.sendMessage("§aAuto Claim§7 is now running!");
                                    player.sendMessage("§7You have claimed this chunk!");

                                    if(mappedChunks.containsKey(player)){
                                        mappedChunks.get(player).remove(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());
                                        viewClaims(player, player.getLocation().getChunk());
                                    }

                                    autoClaimList.put(player, new AutoClaim(player, factionName, player.getLocation().getChunk().getBlock(0, 0, 0).getLocation(), true));
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
        }
        return true;
    }

    //PERFECT
    private boolean autoUnclaim(Player player, String[] args){
        if(autoClaimList.containsKey(player)){
            if(!autoClaimList.get(player).isClaiming()){
                autoClaimList.remove(player);
                player.sendMessage("§aAuto Unclaim§7 is no longer running!");
            }else{
                player.sendMessage("§cYou must turn off auto claim first.");
            }

        }else{
            if(args.length > 1){
                if(args[1].equalsIgnoreCase("safezone") || args[1].equalsIgnoreCase("safe-zone")){
                    if(player.isOp()){
                        String claim = inClaim(player.getLocation().getChunk());

                        if(claim.equalsIgnoreCase("Safe-Zone")){
                            if(unclaimSafeZone(player.getLocation().getChunk())){
                                player.sendMessage("§7You have unclaimed §aSafe-Zone§7 chunk!");

                                if(mappedChunks.containsKey(player)){
                                    mappedChunks.get(player).remove(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());
                                    viewClaims(player, player.getLocation().getChunk());
                                }
                                autoClaimList.put(player, new AutoClaim(player, "Safe-Zone", player.getLocation().getChunk().getBlock(0, 0, 0).getLocation(), false));
                            }else{
                                player.sendMessage("§cFailed to unclaim Safe-Zone chunk.");
                            }
                        }else{
                            player.sendMessage("§cNo Safe-Zone chunk for you to unclaim.");
                        }
                    }else{
                        player.sendMessage("§cOnly server admins can unclaim Safe-Zone.");
                    }

                }else if(args[1].equalsIgnoreCase("pvpzone") || args[1].equalsIgnoreCase("pvp-zone")){
                    if(player.isOp()){
                        String claim = inClaim(player.getLocation().getChunk());

                        if(claim.equalsIgnoreCase("Pvp-Zone")){
                            if(unclaimPvpZone(player.getLocation().getChunk())){
                                player.sendMessage("§7You have unclaimed §aPvp-Zone§7 chunk!");

                                if(mappedChunks.containsKey(player)){
                                    mappedChunks.get(player).remove(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());
                                    viewClaims(player, player.getLocation().getChunk());
                                }
                                autoClaimList.put(player, new AutoClaim(player, "Pvp-Zone", player.getLocation().getChunk().getBlock(0, 0, 0).getLocation(), false));
                            }else{
                                player.sendMessage("§cFailed to unclaim Pvp-Zone chunk.");
                            }
                        }else{
                            player.sendMessage("§cNo Pvp-Zone chunk for you to unclaim.");
                        }
                    }else{
                        player.sendMessage("§cOnly server admins can unclaim Pvp-Zone.");
                    }

                }else{
                    player.sendMessage("§cIf you wish to claim a Safe-Zone or Pvp-Zone please specify that.");
                }

            }else{
                String factionName = getFaction(player.getUniqueId());
                if(factionName != null){
                    if(getPlayerRank(player.getUniqueId(), factionName) > 1){
                        if(inClaim(player.getLocation().getChunk()).equals(factionName)){
                            if(unclaimForFaction(factionName, player.getLocation().getChunk(), getFactionPower(factionName))){
                                player.sendMessage("§7You have unclaimed this chunk!");

                                if(mappedChunks.containsKey(player)){
                                    mappedChunks.get(player).remove(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());
                                    viewClaims(player, player.getLocation().getChunk());
                                }
                                autoClaimList.put(player, new AutoClaim(player, factionName, player.getLocation().getChunk().getBlock(0, 0, 0).getLocation(), false));
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
        }
        return true;
    }

    //PERFECT
    private boolean power(Player player, String[] args){
        if(args.length > 1){
            File factionFolder = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+args[1]);
            if(factionFolder.exists()){
                player.sendMessage("§c"+args[1]+"§7 power level is: §c"+getFactionPower(args[1])+"§7.");
            }
        }else{
            String factionName = getFaction(player.getUniqueId());
            if(factionName != null){
                player.sendMessage("§c"+factionName+"§7 power level is: §c"+getFactionPower(factionName)+"§7.");
            }else{
                player.sendMessage("§cPlease include a faction you wish to power check.");
            }
        }
        return true;
    }

    //PERFECT
    private boolean home(Player player){
        if(factionHome){
            String factionName = getFaction(player.getUniqueId());
            if(factionName != null){
                File factionHome = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"home.yml");
                if(factionHome.exists()){
                    FileConfiguration config = YamlConfiguration.loadConfiguration(factionHome);

                    teleport(player, new Location(plugin.getServer().getWorld(config.getString("world")), config.getDouble("x"),
                            config.getDouble("y"), config.getDouble("z"), (float)config.getDouble("yaw"), (float)config.getDouble("pitch")), "Faction Home");
                }else{
                    player.sendMessage("§cYour faction doesn't have a faction home.");
                }
            }else{
                player.sendMessage("§cYou aren't a part of any faction.");
            }
        }else{
            player.sendMessage("§Homes are not allowed for factions.");
        }
        return true;
    }

    //PERFECT
    private boolean setHome(Player player){
        if(factionHome){
            String factionName = getFaction(player.getUniqueId());
            if(factionName != null){
                if(getPlayerRank(player.getUniqueId(), factionName) > 1){
                    if(inClaim(player.getLocation().getChunk()).equals(factionName)){
                        try{
                            File factionHome = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"home.yml");
                            FileConfiguration config = YamlConfiguration.loadConfiguration(factionHome);
                            config.set("world", player.getLocation().getWorld().getName());
                            config.set("x", player.getLocation().getX());
                            config.set("y", player.getLocation().getY());
                            config.set("z", player.getLocation().getZ());
                            config.set("yaw", player.getLocation().getYaw());
                            config.set("pitch", player.getLocation().getPitch());
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
        }else{
            player.sendMessage("§Homes are not allowed for factions.");
        }
        return true;
    }

    //PERFECT
    private boolean warp(Player player, String[] args){
        if(warps){
            if(args.length > 1){
                String warpName = args[1];

                String factionName = getFaction(player.getUniqueId());
                if(factionName != null){
                    File factionWarp = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"warps"+File.separator+warpName+".yml");
                    if(factionWarp.exists()){
                        FileConfiguration config = YamlConfiguration.loadConfiguration(factionWarp);

                        teleport(player, new Location(plugin.getServer().getWorld(config.getString("world")), config.getDouble("x"),
                                config.getDouble("y"), config.getDouble("z"), (float)config.getDouble("yaw"), (float)config.getDouble("pitch")), warpName);
                    }else{
                        player.sendMessage("§cYour faction doesn't have that warp.");
                    }
                }else{
                    player.sendMessage("§cYou aren't a part of any faction.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a warp name.");
                return false;
            }
        }else{
            player.sendMessage("§cWarps are not allowed for factions.");
            return true;
        }
    }

    //PERFECT
    private boolean listWarps(Player player){
        if(warps){
            String factionName = getFaction(player.getUniqueId());
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
        }else{
            player.sendMessage("§cWarps are not allowed for factions.");
        }
        return true;
    }

    //PERFECT
    private boolean setWarp(Player player, String[] args){
        if(warps){
            if(args.length > 1){
                String warpName = args[1];
                if(warpName.length() < 13 && warpName.length() > 1){
                    String factionName = getFaction(player.getUniqueId());
                    if(factionName != null){
                        if(getPlayerRank(player.getUniqueId(), factionName) > 1){
                            if(inClaim(player.getLocation().getChunk()).equals(factionName)){
                                try{
                                    File factionWarp = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"warps"+File.separator+warpName+".yml");
                                    FileConfiguration config = YamlConfiguration.loadConfiguration(factionWarp);
                                    config.set("world", player.getLocation().getWorld().getName());
                                    config.set("x", player.getLocation().getX());
                                    config.set("y", player.getLocation().getY());
                                    config.set("z", player.getLocation().getZ());
                                    config.set("yaw", player.getLocation().getYaw());
                                    config.set("pitch", player.getLocation().getPitch());
                                    config.save(factionWarp);

                                    setFactionPower(factionName, getFactionPower(factionName)-warpCost);

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
                return true;
            }else{
                player.sendMessage("§cPlease specify a warp name.");
                return false;
            }
        }else{
            player.sendMessage("§cWarps are not allowed for factions.");
            return true;
        }
    }

    //PERFECT
    private boolean removeWarp(Player player, String[] args){
        if(warps){
            if(args.length > 1){
                String warpName = args[1];
                if(warpName.length() < 13 && warpName.length() > 1){
                    String factionName = getFaction(player.getUniqueId());
                    if(factionName != null){
                        if(getPlayerRank(player.getUniqueId(), factionName) > 1){
                            if(inClaim(player.getLocation().getChunk()).equals(factionName)){
                                try{
                                    File factionWarp = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+factionName+File.separator+"warps"+File.separator+warpName+".yml");
                                    if(factionWarp.exists()){
                                        factionWarp.delete();
                                    }

                                    setFactionPower(factionName, getFactionPower(factionName)+warpCost);

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
                return true;
            }else{
                player.sendMessage("§cPlease specify a warp name.");
                return false;
            }
        }else{
            player.sendMessage("§cWarps are not allowed for factions.");
            return true;
        }
    }

    //PERFECT
    private boolean factionChat(Player player){
        String factionName = getFaction(player.getUniqueId());
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
        return true;
    }

    //PERFECT
    private boolean factionMap(Player player){
        if(mapFactions.contains(player)){
            mapFactions.remove(player);

            if(mappedChunks.containsKey(player)){
                for(Location location : mappedChunks.get(player)){
                    renewMappedLandscape(player, location.getChunk());
                }
            }

            mappedChunks.remove(player);
            player.sendMessage("§7You are no longer mapping factions around you.");

        }else{
            mapFactions.add(player);
            viewClaims(player, player.getLocation().getChunk());
            player.sendMessage("§7You are now mapping factions around you.");
        }
        return true;
    }

    //PERFECT
    private boolean rank(Player player){
        String factionName = getFaction(player.getUniqueId());
        if(factionName != null){
            int rank = getPlayerRank(player.getUniqueId(), factionName);
            String[] names = { "Member", "Recruit", "Admin", "Owner" };
            player.sendMessage("§7Your rank in the faction is §c"+names[rank]+"§7.");

        }else{
            player.sendMessage("§cYou aren't a part of any faction.");
        }
        return true;
    }

    //PERFECT
    private boolean changeOwnership(Player player, String[] args){
        if(args.length > 1){
            String factionName = getFaction(player.getUniqueId());
            if(factionName != null){
                OfflinePlayer reqPlayer = resolvePlayer(args[1]);

                if(reqPlayer != null){
                    String reqFaction = getFaction(reqPlayer.getUniqueId());
                    if(reqFaction != null && reqFaction.equals(factionName)){
                        if(player.getUniqueId() != reqPlayer.getUniqueId()){
                            if(getPlayerRank(player.getUniqueId(), factionName) == 3){
                                if(changeFactionOwnership(player.getUniqueId(), reqPlayer.getUniqueId(), factionName)){
                                    player.sendMessage("§7You have changed faction ownership to §c"+reqPlayer.getName()+"§7.");

                                    if(reqPlayer.isOnline()){
                                        reqPlayer.getPlayer().sendMessage("§7You are now the faction §6Owner§7 in faction.");
                                    }
                                }else{
                                    player.sendMessage("§cFailed to change ownership to player.");
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
            return true;
        }else{
            player.sendMessage("§cYou must include a player name that you wish to demote.");
        }
        return false;
    }

    //PERFECT
    private boolean setPower(Player player, String[] args){
        if(args.length > 2){
            if(player.isOp()){
                File factionFolder = new File(plugin.getDataFolder()+File.separator+"factions"+File.separator+args[1]);

                if(factionFolder.exists()){
                    int power = Integer.parseInt(args[2]);

                    if(setFactionPower(args[1], power)){
                        player.sendMessage("§c"+args[1]+"§7 power has been set to §c"+power+"§7.");

                    }else{
                        player.sendMessage("§cFailed to set faction power.");
                    }
                }else{
                    player.sendMessage("§cFaction specified doesn't exist.");
                }
            }else{
                player.sendMessage("§cYou must be server admin to set faction power.");
            }
            return true;
        }else{
            player.sendMessage("§cYou must specify a faction and the power you wish to set.");
        }
        return false;
    }

    //PERFECT
    private OfflinePlayer resolvePlayer(String playerName){
        try{
            File pnr = new File(plugin.getDataFolder()+File.separator+"pnr.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(pnr);

            if(config.contains(playerName)){
                OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(UUID.fromString(config.getString(playerName)));
                return offlinePlayer;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}

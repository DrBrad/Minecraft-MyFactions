package unet.Factions.Faction;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.*;

import static unet.Factions.Main.plugin;
import static unet.Factions.Claim.ClaimHandler.*;

public class FactionHandler {

    private static ArrayList<UUID> chat = new ArrayList<>();

    private static HashMap<UUID, UUID> invites = new HashMap<>();
    private static JSONObject players = new JSONObject();
    private static HashMap<UUID, MyFaction> factionsByUUID = new HashMap<>();
    private static HashMap<String, UUID> factionsByName = new HashMap<>();
    private static Zone safeZone, pvpZone;

    public FactionHandler(){
        safeZone = new Zone(UUID.fromString("10b33fd4-ead7-4aa1-84d6-59aedfbd71ed"), "Safe Zone", 2, 1);
        pvpZone = new Zone(UUID.fromString("80b251bd-a999-4c0a-97eb-f2f631c9a7e3"), "PVP Zone", 1, 5);

        //READING TIME
        try{
            File playersFile = new File(plugin.getDataFolder()+File.separator+"players.json");
            if(playersFile.exists()){
                players = new JSONObject(new JSONTokener(new FileInputStream(playersFile)));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        File factionsFolder = new File(plugin.getDataFolder()+File.separator+"factions");
        if(factionsFolder.exists()){
            for(File factionFile : factionsFolder.listFiles()){
                MyFaction faction = new MyFaction();
                faction.init(factionFile.getName());
                factionsByUUID.put(faction.getKey(), faction);
                factionsByName.put(faction.getName().toLowerCase(), faction.getKey());
            }
        }
    }

    public static Zone getSafeZone(){
        return safeZone;
    }

    public static Zone getPvpZone(){
        return pvpZone;
    }

    public static Zone getZoneByUUID(UUID uuid){
        if(uuid.equals(UUID.fromString("10b33fd4-ead7-4aa1-84d6-59aedfbd71ed"))){
            return safeZone;

        }else if(uuid.equals(UUID.fromString("80b251bd-a999-4c0a-97eb-f2f631c9a7e3"))){
            return pvpZone;
        }
        return null;
    }

    public static ArrayList<MyFaction> getListOfFactions(){
        if(factionsByUUID.size() > 0){
            return new ArrayList<>(factionsByUUID.values());
        }
        return null;
    }

    public static ArrayList<String> getListOfFactionNames(){
        if(factionsByName.size() > 0){
            return new ArrayList<>(factionsByName.keySet());
        }
        return null;
    }

    public static MyFaction getFactionFromUUID(UUID uuid){
        if(factionsByUUID.containsKey(uuid)){
            return factionsByUUID.get(uuid);
        }
        return null;
    }

    public static MyFaction getFactionFromName(String name){
        if(factionsByName.containsKey(name.toLowerCase())){
            if(factionsByUUID.containsKey(factionsByName.get(name.toLowerCase()))){
                return factionsByUUID.get(factionsByName.get(name.toLowerCase()));
            }
        }
        return null;
    }

    public static MyFaction getPlayersFaction(UUID uuid){
        if(players.has(uuid.toString())){
            if(factionsByUUID.containsKey(UUID.fromString(players.getString(uuid.toString())))){
                return factionsByUUID.get(UUID.fromString(players.getString(uuid.toString())));
            }
        }
        return null;
    }

    public static boolean isPlayerInFaction(UUID uuid){
        return players.has(uuid.toString());
    }

    public static boolean isFaction(String name){
        if(factionsByName.containsKey(name.toLowerCase())){
            if(factionsByUUID.containsKey(factionsByName.get(name.toLowerCase()))){
                return true;
            }else{
                factionsByName.remove(name.toLowerCase());
            }
        }
        return false;
    }

    public static void createFaction(UUID uuid, MyFaction faction){
        //if(!factionsByUUID.containsKey(faction.getKey())){
            factionsByUUID.put(faction.getKey(), faction);
        //}

        //if(!factionsByName.containsKey(faction.getName())){
            factionsByName.put(faction.getName().toLowerCase(), faction.getKey());
        //}

        players.put(uuid.toString(), faction.getKey().toString());

        writePlayers();
    }

    public static void renameFaction(String oldName, String name){
        if(factionsByName.containsKey(oldName.toLowerCase())){
            UUID key = factionsByName.get(oldName.toLowerCase());
            factionsByName.remove(oldName.toLowerCase());
            factionsByName.put(name.toLowerCase(), key);
        }
    }

    public static void deleteFaction(MyFaction faction){
        if(factionsByUUID.containsKey(faction.getKey())){
            factionsByUUID.remove(faction.getKey());
        }

        if(factionsByName.containsKey(faction.getName().toLowerCase())){
            factionsByName.remove(faction.getName().toLowerCase());
        }

        for(String suuid : faction.getPlayers()){
            UUID uuid = UUID.fromString(suuid);
            if(players.has(uuid.toString())){
                players.remove(uuid.toString());
            }

            if(isAutoClaiming(uuid)){
                stopAutoClaiming(uuid);
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if(player != null && player.isOnline()){
                player.getPlayer().sendMessage("§cYour faction has been disbanded, you are no longer a part of a faction!");
            }
        }
        writePlayers();
    }

    public static void addPlayerToFaction(UUID uuid, UUID factionName){
        players.put(uuid.toString(), factionName.toString());
        writePlayers();
    }

    public static void removePlayerFromFaction(UUID uuid){
        if(players.has(uuid.toString())){
            players.remove(uuid.toString());

            if(isAutoClaiming(uuid)){
                stopAutoClaiming(uuid);
            }

            writePlayers();
        }
    }

    public static boolean hasInviteToFaction(UUID uuid){
        return invites.containsKey(uuid);
    }

    public static void removeInviteToFaction(UUID uuid){
        if(invites.containsKey(uuid)){
            invites.remove(uuid);
        }
    }

    public static void inviteToFaction(UUID uuid, UUID key){
        invites.put(uuid, key);
    }

    public static MyFaction getFactionInvite(UUID uuid){
        if(invites.containsKey(uuid)){
            UUID key = invites.get(uuid);
            if(factionsByUUID.containsKey(key)){
                invites.remove(uuid);
                return factionsByUUID.get(key);
            }
        }
        return null;
    }

    public static HashMap<Player, Integer> delayedTask = new HashMap<>();

    public static void setPlayerInviteTask(Player player, int task){
        if(delayedTask.containsKey(player)){
            plugin.getServer().getScheduler().cancelTask(delayedTask.get(player));
        }
        delayedTask.put(player, task);
    }

    public static void removePlayerInviteTask(Player player){
        if(delayedTask.containsKey(player)){
            plugin.getServer().getScheduler().cancelTask(delayedTask.get(player));
            delayedTask.remove(player);
        }
    }


    public static void startChatting(UUID uuid){
        if(!chat.contains(uuid)){
            chat.add(uuid);
        }
    }

    public static void stopChatting(UUID uuid){
        if(chat.contains(uuid)){
            chat.remove(uuid);
        }
    }

    public static boolean isChatting(UUID uuid){
        return chat.contains(uuid);
    }


    public static boolean isBannedName(String name){
        String[] tmp = {
                "safe-zone",
                "safezone",
                "pvp-zone",
                "pvpzone",
                "wilderness"
        };

        List<String> names = Arrays.asList(tmp);

        return names.contains(name.toLowerCase());
    }

    public static void writePlayers(){
        try{
            if(!plugin.getDataFolder().exists()){
                plugin.getDataFolder().mkdirs();
            }

            FileWriter out = new FileWriter(new File(plugin.getDataFolder()+File.separator+"players.json"));
            out.write(players.toString());
            out.flush();
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

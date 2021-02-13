package unet.Factions.Claim;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.json.JSONObject;
import org.json.JSONTokener;
import unet.Factions.Faction.Faction;
import unet.Factions.Faction.MyFaction;
import unet.Factions.Faction.Zone;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

import static unet.Factions.Handlers.Config.*;
import static unet.Factions.Handlers.MapHandler.*;
import static unet.Factions.Main.plugin;
import static unet.Factions.Faction.FactionHandler.*;

public class ClaimHandler {

    private static JSONObject claims = new JSONObject();
    private static HashMap<UUID, AutoClaim> autoClaiming = new HashMap<>();

    public ClaimHandler(){
        if(plugin.getDataFolder().exists()){
            try{
                File claimsFile = new File(plugin.getDataFolder()+File.separator+"claims.json");
                if(claimsFile.exists()){
                    claims = new JSONObject(new JSONTokener(new FileInputStream(claimsFile)));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static boolean claimChunk(Player player, Chunk chunk, Faction faction){
        if(faction.canClaim(player.getUniqueId())){
            String key = getChunkName(chunk);

            if(faction.getType() > 0){
                if(claimForZone(player, key, (Zone) faction)){
                    if(isMapping(player.getUniqueId())){
                        removeMappedChunk(player.getUniqueId(), chunk);
                        mapLandscape(player, chunk);
                    }
                    return true;
                }

            }else{
                if(claimForFaction(player, key, (MyFaction) faction)){
                    if(isMapping(player.getUniqueId())){
                        removeMappedChunk(player.getUniqueId(), chunk);
                        mapLandscape(player, chunk);
                    }
                    return true;
                }
            }
        }else{
            player.sendMessage("§cYou must be at least a faction admin to claim land.");
        }
        return false;
    }

    private static boolean claimForFaction(Player player, String key, MyFaction faction){
        if(faction.getPower() > 1){
            if(claims.has(key)){
                Claim claim = new Claim(UUID.fromString(claims.getJSONObject(key).getString("k")), claims.getJSONObject(key).getInt("t"));
                //Claim claim = claims.get(key);
                if(claim.getType() == 0){
                    Faction claimedFaction = getFactionFromUUID(claim.getKey());
                    if(claimedFaction != null){
                        if(claimedFaction.getType() == 0){
                            if(!claimedFaction.getKey().equals(faction.getKey())){
                                if(((MyFaction) claimedFaction).getPower() < 0){
                                    claim.setKey(faction.getKey());
                                    claim.setType(faction.getType());
                                    faction.setPower(faction.getPower()-getClaimCost());
                                    ((MyFaction) claimedFaction).setPower(((MyFaction) claimedFaction).getPower()+getClaimCost());
                                    write();
                                    player.sendMessage("§7You have over §aclaimed§7 the chunk from "+claimedFaction.getName()+".");
                                    return true;

                                }else{
                                    player.sendMessage("§cAnother faction owns this chunk and is stron enough to keep it.");
                                }
                            }else{
                                player.sendMessage("§cYour faction already claimed this chunk.");
                            }
                        }else{
                            player.sendMessage("§cThis chunk is claimed by a zone, you cannot over claim this chunk.");
                        }
                    }else{
                        claim.setKey(faction.getKey());
                        claim.setType(faction.getType());
                        faction.setPower(faction.getPower()-getClaimCost());
                        write();
                        player.sendMessage("§7You have §aclaimed§7 this chunk for "+faction.getName()+".");
                        return true;
                    }
                }else{
                    player.sendMessage("§cThis chunk is claimed by a zone, you cannot over claim this chunk.");
                }
            }else{
                JSONObject jclaim = new JSONObject();
                jclaim.put("k", faction.getKey().toString());
                jclaim.put("t", faction.getType());
                claims.put(key, jclaim);
                faction.setPower(faction.getPower()-getClaimCost());
                write();
                player.sendMessage("§7You have §aclaimed§7 this chunk for "+faction.getName()+".");
                return true;
            }
        }else{
            player.sendMessage("§cYour faction doesn't have enough power to claim.");
        }
        return false;
    }


    private static boolean claimForZone(Player player, String key, Zone zone){
        if(claims.has(key)){
            Claim claim = new Claim(UUID.fromString(claims.getJSONObject(key).getString("k")), claims.getJSONObject(key).getInt("t"));
            //Claim claim = claims.get(key);
            if(claim.getType() != zone.getType()){
                Faction claimedFaction = getFactionFromUUID(claim.getKey());
                if(claimedFaction != null){
                    if(claimedFaction.getType() == 0){
                        ((MyFaction) claimedFaction).setPower(((MyFaction) claimedFaction).getPower()+getClaimCost());
                    }
                }

                claim.setKey(zone.getKey());
                claim.setType(zone.getType());
                write();
                player.sendMessage("§7You have §aclaimed§7 this chunk for "+zone.getName()+".");
                return true;
            }
        }else{
            JSONObject jclaim = new JSONObject();
            jclaim.put("k", zone.getKey().toString());
            jclaim.put("t", zone.getType());
            claims.put(key, jclaim);
            write();
            player.sendMessage("§7You have §aclaimed§7 this chunk for "+zone.getName()+".");
            return true;
        }
        return false;
    }

    public static boolean autoClaimChunk(Player player, Chunk chunk){
        if(autoClaiming.containsKey(player.getUniqueId())){
            AutoClaim autoClaim = autoClaiming.get(player.getUniqueId());

            String key = getChunkName(chunk);
            if(autoClaim.getLastLocation() == null || !autoClaim.getLastLocation().equals(key)){
                autoClaim.setLastLocation(key);

                if(autoClaim.isClaiming()){
                    return claimChunk(player, chunk, autoClaim.getFaction());
                }else{
                    return unclaimChunk(player, chunk, autoClaim.getFaction());
                }
            }
        }
        return false;
    }

    public static boolean unclaimChunk(Player player, Chunk chunk, Faction faction){
        if(faction.canClaim(player.getUniqueId())){
            String key = getChunkName(chunk);

            if(claims.has(key)){
                Claim claim = new Claim(UUID.fromString(claims.getJSONObject(key).getString("k")), claims.getJSONObject(key).getInt("t"));
                //Claim claim = claims.get(key);
                if(claim.getType() == faction.getType() && claim.getKey().equals(faction.getKey())){
                    claims.remove(key);

                    if(faction.getType() == 0){
                        ((MyFaction) faction).setPower(((MyFaction) faction).getPower()+getClaimCost());
                    }
                    write();

                    if(isMapping(player.getUniqueId())){
                        removeMappedChunk(player.getUniqueId(), chunk);
                        mapLandscape(player, chunk);
                    }

                    player.sendMessage("§7You have §cunclaimed§7 this chunk for "+faction.getName()+".");
                    return true;

                }else{
                    player.sendMessage("§cYour faction doesn't own this chunk.");
                }
            }else{
                player.sendMessage("§cThis chunk isn't claimed by any faction.");
            }
        }else{
            player.sendMessage("§cYou must be at least a faction admin to unclaim land.");
        }
        return false;
    }

    public static boolean inClaim(Chunk chunk){
        String key = getChunkName(chunk);
        return claims.has(key);
    }

    public static Claim getClaim(Chunk chunk){
        String key = getChunkName(chunk);
        if(claims.has(key)){
            return new Claim(UUID.fromString(claims.getJSONObject(key).getString("k")), claims.getJSONObject(key).getInt("t"));
            //return claims.get(key);
        }
        return null;
    }

    public static void startAutoClaiming(UUID uuid, Faction faction, boolean claiming){
        if(!autoClaiming.containsKey(uuid)){
            autoClaiming.put(uuid, new AutoClaim(faction, claiming, null));
        }
    }

    public static AutoClaim getAutoClaim(UUID uuid){
        if(autoClaiming.containsKey(uuid)){
            return autoClaiming.get(uuid);
        }
        return null;
    }

    public static boolean isAutoClaiming(UUID uuid){
        return autoClaiming.containsKey(uuid);
    }

    public static void stopAutoClaiming(UUID uuid){
        if(autoClaiming.containsKey(uuid)){
            autoClaiming.remove(uuid);
        }
    }

    public static void unclaimAllForFaction(UUID uuid){
        if(claims.length() > 0){
            for(int i = claims.names().length()-1; i > -1; i--){
                String key = claims.names().getString(i);
                if(claims.getJSONObject(claims.names().getString(i)).getString("k").equals(uuid.toString())){
                    claims.remove(key);
                }
            }

            write();
        }
    }





    private static String getChunkName(Chunk chunk){
        /*
        try{
            String key = chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            return messageDigest.digest(key.getBytes());
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
        */
        return chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ();
        //return key.getBytes();
    }

    private static void write(){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
            @Override
            public void run(){
                if(!plugin.getDataFolder().exists()){
                    plugin.getDataFolder().mkdirs();
                }

                try{
                    FileWriter out = new FileWriter(new File(plugin.getDataFolder()+File.separator+"claims.json"));
                    out.write(claims.toString());
                    out.flush();
                    out.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}

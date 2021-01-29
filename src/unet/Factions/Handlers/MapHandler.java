package unet.Factions.Handlers;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import unet.Factions.Claim.Claim;
import unet.Factions.Faction.MyFaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static unet.Factions.Claim.ClaimHandler.*;
import static unet.Factions.Faction.FactionHandler.*;

public class MapHandler {

    private static HashMap<UUID, ArrayList<Location>> map = new HashMap<>();

    public static boolean isMapping(UUID uuid){
        return map.containsKey(uuid);
    }

    public static void startMapping(Player player){
        if(!map.containsKey(player.getUniqueId())){
            map.put(player.getUniqueId(), new ArrayList<>());
            mapLandscape(player, player.getLocation().getChunk());
        }
    }

    public static void stopMapping(Player player){
        if(map.containsKey(player.getUniqueId())){
            renewLandscape(player);
            map.remove(player.getUniqueId());
        }
    }

    public static void removeMappedChunk(UUID uuid, Chunk chunk){
        if(map.containsKey(uuid)){
            if(map.get(uuid).contains(chunk.getBlock(0, 0, 0).getLocation())){
                map.get(uuid).remove(chunk.getBlock(0, 0, 0).getLocation());
            }
        }
    }

    public static void mapLandscape(Player player, Chunk centerChunk){
        //UNDERSTANDING THE RENDER OF THE PLAYER
        if(map.containsKey(player.getUniqueId())){
            int size = map.get(player.getUniqueId()).size();
            if(size > 0){
                for(int i = size; i > 0; i--){
                    if(map.get(player.getUniqueId()).size() > i){
                        Location location = map.get(player.getUniqueId()).get(i);
                        if(player.getLocation().distance(location) > 300){
                            map.get(player.getUniqueId()).remove(i);
                        }
                    }
                }
            }
        }

        ArrayList<Chunk> chunks = new ArrayList<>();

        chunks.add(centerChunk);
        chunks.add(centerChunk.getWorld().getBlockAt(centerChunk.getBlock(0, 0, 0).getX()-1, 0, centerChunk.getBlock(0, 0, 0).getZ()).getChunk());
        chunks.add(centerChunk.getWorld().getBlockAt(centerChunk.getBlock(0, 0, 0).getX()-1, 0, centerChunk.getBlock(0, 0, 0).getZ()-1).getChunk());
        chunks.add(centerChunk.getWorld().getBlockAt(centerChunk.getBlock(0, 0, 0).getX()-1, 0, centerChunk.getBlock(15, 0, 15).getZ()+1).getChunk());

        chunks.add(centerChunk.getWorld().getBlockAt(centerChunk.getBlock(0, 0, 0).getX(), 0, centerChunk.getBlock(0, 0, 0).getZ()-1).getChunk());
        chunks.add(centerChunk.getWorld().getBlockAt(centerChunk.getBlock(0, 0, 0).getX(), 0, centerChunk.getBlock(15, 0, 15).getZ()+1).getChunk());

        chunks.add(centerChunk.getWorld().getBlockAt(centerChunk.getBlock(15, 0, 15).getX()+1, 0, centerChunk.getBlock(0, 0, 0).getZ()).getChunk());
        chunks.add(centerChunk.getWorld().getBlockAt(centerChunk.getBlock(15, 0, 15).getX()+1, 0, centerChunk.getBlock(0, 0, 0).getZ()-1).getChunk());
        chunks.add(centerChunk.getWorld().getBlockAt(centerChunk.getBlock(15, 0, 15).getX()+1, 0, centerChunk.getBlock(15, 0, 15).getZ()+1).getChunk());

        for(Chunk chunk : chunks){
            if(map.containsKey(player.getUniqueId())){
                if(!map.get(player.getUniqueId()).contains(chunk.getBlock(0, 0, 0).getLocation())){
                    map.get(player.getUniqueId()).add(chunk.getBlock(0, 0, 0).getLocation());
                }

                Material material = Material.WHITE_CONCRETE;
                if(inClaim(chunk)){
                    Claim claim = getClaim(chunk);

                    switch(claim.getType()){
                        case 0:
                            MyFaction faction = getPlayersFaction(player.getUniqueId());
                            if(faction != null){
                                if(claim.getKey().equals(faction.getKey())){
                                    material = Material.LIME_CONCRETE;
                                }else{
                                    material = Material.RED_CONCRETE;
                                }
                            }else{
                                material = Material.RED_CONCRETE;
                            }
                            break;

                        case 1:
                            material = Material.PURPLE_CONCRETE;
                            break;

                        case 2:
                            material = Material.BLUE_CONCRETE;
                            break;
                    }
                }

                for(int x = 0; x < 16; x++){
                    Block block = chunk.getBlock(x, 0, 0);
                    Location location = chunk.getWorld().getHighestBlockAt(block.getX(), block.getZ()).getLocation();
                    if(location.getY() < 0){
                        location.setY(0);
                    }
                    player.sendBlockChange(location, material.createBlockData());
                }

                for(int x = 0; x < 16; x++){
                    Block block = chunk.getBlock(x, 0, 15);
                    Location location = chunk.getWorld().getHighestBlockAt(block.getX(), block.getZ()).getLocation();
                    if(location.getY() < 0){
                        location.setY(0);
                    }
                    player.sendBlockChange(location, material.createBlockData());
                }

                for(int z = 0; z < 16; z++){
                    Block block = chunk.getBlock(0, 0, z);
                    Location location = chunk.getWorld().getHighestBlockAt(block.getX(), block.getZ()).getLocation();
                    if(location.getY() < 0){
                        location.setY(0);
                    }
                    player.sendBlockChange(location, material.createBlockData());
                }

                for(int z = 0; z < 16; z++){
                    Block block = chunk.getBlock(15, 0, z);
                    Location location = chunk.getWorld().getHighestBlockAt(block.getX(), block.getZ()).getLocation();
                    if(location.getY() < 0){
                        location.setY(0);
                    }
                    player.sendBlockChange(location, material.createBlockData());
                }
            }
        }

    }

    public static void renewLandscape(Player player){
        if(map.containsKey(player.getUniqueId())){
            for(Location location : map.get(player.getUniqueId())){
                renewChunk(player, location.getChunk());
            }
        }
    }

    private static void renewChunk(Player player, Chunk chunk){
        for(int x = 0; x < 16; x++){
            Block block = chunk.getBlock(x, 0, 0);
            block = chunk.getWorld().getHighestBlockAt(block.getX(), block.getZ());
            if(block.getY() < 0){
                block = chunk.getWorld().getBlockAt(block.getX(), 0, block.getZ());
            }
            player.sendBlockChange(block.getLocation(), block.getBlockData());
        }

        for(int x = 0; x < 16; x++){
            Block block = chunk.getBlock(x, 0, 15);
            block = chunk.getWorld().getHighestBlockAt(block.getX(), block.getZ());
            if(block.getY() < 0){
                block = chunk.getWorld().getBlockAt(block.getX(), 0, block.getZ());
            }
            player.sendBlockChange(block.getLocation(), block.getBlockData());
        }

        for(int z = 0; z < 16; z++){
            Block block = chunk.getBlock(0, 0, z);
            block = chunk.getWorld().getHighestBlockAt(block.getX(), block.getZ());
            if(block.getY() < 0){
                block = chunk.getWorld().getBlockAt(block.getX(), 0, block.getZ());
            }
            player.sendBlockChange(block.getLocation(), block.getBlockData());
        }

        for(int z = 0; z < 16; z++){
            Block block = chunk.getBlock(15, 0, z);
            block = chunk.getWorld().getHighestBlockAt(block.getX(), block.getZ());
            if(block.getY() < 0){
                block = chunk.getWorld().getBlockAt(block.getX(), 0, block.getZ());
            }
            player.sendBlockChange(block.getLocation(), block.getBlockData());
        }
    }
}

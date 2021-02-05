package unet.Factions;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import unet.Factions.Claim.Claim;
import unet.Factions.Faction.*;

import java.util.HashMap;
import java.util.UUID;

import static unet.Factions.Claim.ClaimHandler.*;
import static unet.Factions.Faction.FactionHandler.*;
import static unet.Factions.Handlers.Config.*;
import static unet.Factions.Handlers.GeneralHandler.*;
import static unet.Factions.Handlers.MapHandler.*;
import static unet.Factions.Handlers.PlayerResolver.*;
import static unet.Factions.Handlers.Colors.*;
import static unet.Factions.Handlers.BlockHandler.*;

public class MyEventHandler implements Listener {

    private static HashMap<Player, UUID> enteredClaim = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        setPlayer(event.getPlayer().getName(), event.getPlayer().getUniqueId());

        MyFaction faction = getPlayersFaction(event.getPlayer().getUniqueId());
        if(faction != null){
            String color = getChatColor(faction.getColor());
            String[] names = { "Member", "Recruit", "Admin", "Owner" };

            event.getPlayer().setPlayerListName("§6["+color+faction.getName()+"§6]["+color+names[faction.getRank(event.getPlayer().getUniqueId())]+"§6]["+color+event.getPlayer().getName()+"§6]");
            event.setJoinMessage(getChatColor(faction.getColor())+event.getPlayer().getName()+"§7 Has joined the server!");

        }else{
            event.getPlayer().setPlayerListName("§c"+event.getPlayer().getName());
            event.setJoinMessage("§c"+event.getPlayer().getName()+"§7 Has joined the server!");
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        MyFaction faction = getPlayersFaction(event.getPlayer().getUniqueId());
        if(faction != null){
            event.setQuitMessage(getChatColor(faction.getColor())+event.getPlayer().getName()+"§7 Has left the server!");

            if(isChatting(event.getPlayer().getUniqueId())){
                stopChatting(event.getPlayer().getUniqueId());
            }

            if(isAutoClaiming(event.getPlayer().getUniqueId())){
                stopAutoClaiming(event.getPlayer().getUniqueId());
            }
        }else{
            event.setQuitMessage("§c"+event.getPlayer().getName()+"§7 Has left the server!");
        }

        if(isMapping(event.getPlayer().getUniqueId())){
            stopMapping(event.getPlayer());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event){
        MyFaction faction = getPlayersFaction(event.getPlayer().getUniqueId());
        if(faction != null){
            spawnCircle(event.getPlayer(), event.getTo(), getColorRGB(faction.getColor()));
        }else{
            spawnCircle(event.getPlayer(), event.getTo(), Color.fromRGB(0, 0, 255));
        }
    }

    @EventHandler
    public static void onPortalTravel(PlayerPortalEvent event){
        if(event.getCause() == PlayerPortalEvent.TeleportCause.END_PORTAL){
            Location endSpawn = getEndSpawn();
            if(endSpawn != null){
                event.setTo(endSpawn);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        MyFaction faction = getPlayersFaction(event.getPlayer().getUniqueId());
        if(faction != null){
            String color = getChatColor(faction.getColor());
            String[] names = { "Member", "Recruit", "Admin", "Owner" };

            if(isChatting(event.getPlayer().getUniqueId())){
                for(String uuid : faction.getPlayers()){
                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    if(player != null && player.isOnline()){
                        player.getPlayer().sendMessage("§6["+color+faction.getName()+"§6]["+color+names[faction.getRank(event.getPlayer().getUniqueId())]+"§6]["+color+event.getPlayer().getName()+"§6]§7: §a"+event.getMessage());
                    }
                }
                event.setCancelled(true);
            }else{
                event.setFormat("§6["+color+faction.getName()+"§6]["+color+names[faction.getRank(event.getPlayer().getUniqueId())]+"§6]["+color+event.getPlayer().getName()+"§6]§7: "+event.getMessage());
            }
        }else{
            event.setFormat("§6[§c"+event.getPlayer().getName()+"§6]§7: "+event.getMessage());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        MyFaction faction = getPlayersFaction(event.getEntity().getUniqueId());
        if(faction != null){
            faction.setPower(faction.getPower()-getDeathToll());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if(event.getBlock().getBlockData() instanceof Waterlogged){
            if(((Waterlogged) event.getBlock().getBlockData()).isWaterlogged()){
                event.getPlayer().sendMessage("§cWater Logging blocks is not allowed.");
                event.setCancelled(true);
                return;
            }
        }

        Chunk chunk = event.getBlock().getChunk();

        if(inClaim(chunk)){
            Claim claim = getClaim(chunk);

            if(claim.getType() > 0){
                if(!event.getPlayer().isOp()){
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cOnly server admins can place blocks in zones.");
                }

            }else{
                MyFaction faction = getPlayersFaction(event.getPlayer().getUniqueId());
                if(faction != null){
                    if(!claim.getKey().equals(faction.getKey())){
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§cYou cannot place blocks in other factions claims.");

                    }else if(!faction.canBuild(event.getPlayer().getUniqueId())){
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§cYou cannot place blocks as a member.");
                    }
                }else{
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cYou cannot place blocks in other factions claims.");
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Chunk chunk = event.getBlock().getChunk();

        if(inClaim(chunk)){
            Claim claim = getClaim(chunk);

            if(claim.getType() > 0){
                if(!event.getPlayer().isOp()){
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cOnly server admins can break blocks in zones.");
                }

            }else{
                MyFaction faction = getPlayersFaction(event.getPlayer().getUniqueId());
                if(faction != null){
                    if(!claim.getKey().equals(faction.getKey())){
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§cYou cannot break blocks in other factions claims.");

                    }else if(!faction.canBuild(event.getPlayer().getUniqueId())){
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§cYou cannot break blocks as a member.");
                    }
                }else{
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cYou cannot break blocks in other factions claims.");
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Block block = event.getClickedBlock();

            Chunk chunk = block.getChunk();
            if(inClaim(chunk)){
                Claim claim = getClaim(chunk);
                if(claim.getType() > 0){
                    if(getSafeNoEdit().contains(block.getType())){
                        if(!event.getPlayer().isOp()){
                            event.setCancelled(true);
                            event.getPlayer().sendMessage("§cOnly server admins can interact with blocks in zones.");
                        }
                    }

                }else if(getNoEdit().contains(block.getType())){
                    MyFaction faction = getPlayersFaction(event.getPlayer().getUniqueId());
                    if(faction != null){
                        if(!faction.getKey().equals(claim.getKey())){
                            event.setCancelled(true);
                            event.getPlayer().sendMessage("§cYou cannot interact with blocks in other factions claims.");

                        }else if(!faction.canBuild(event.getPlayer().getUniqueId())){
                            event.setCancelled(true);
                            event.getPlayer().sendMessage("§cYou cannot interact with blocks as a member.");
                        }
                    }else{
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§cYou cannot interact with blocks in other factions claims.");
                    }
                }
            }

            /*
            if(getNoEdit().contains(block.getType())){
                Chunk chunk = block.getChunk();

                if(inClaim(chunk)){
                    Claim claim = getClaim(chunk);
                    if(claim.getType() > 0){
                        if(!event.getPlayer().isOp()){
                            event.setCancelled(true);
                            event.getPlayer().sendMessage("§cOnly server admins can interact with blocks in zones.");
                        }

                    }else{
                        MyFaction faction = getPlayersFaction(event.getPlayer().getUniqueId());
                        if(faction != null){
                            if(!faction.getKey().equals(claim.getKey())){
                                event.setCancelled(true);
                                event.getPlayer().sendMessage("§cYou cannot interact with blocks in other factions claims.");

                            }else if(!faction.canBuild(event.getPlayer().getUniqueId())){
                                event.setCancelled(true);
                                event.getPlayer().sendMessage("§cYou cannot interact with blocks as a member.");
                            }
                        }else{
                            event.setCancelled(true);
                            event.getPlayer().sendMessage("§cYou cannot interact with blocks in other factions claims.");
                        }
                    }
                }
            }
            */
        }
    }

    @EventHandler
    public void onDispenseWaterLog(BlockDispenseEvent event){
        Block block = event.getBlock().getWorld().getBlockAt(event.getBlock().getX()-1, event.getBlock().getY(), event.getBlock().getZ());
        if(event.getItem().getType() == Material.WATER_BUCKET && block.getBlockData() instanceof Waterlogged){
            event.setCancelled(true);
            return;
        }

        block = event.getBlock().getWorld().getBlockAt(event.getBlock().getX()+1, event.getBlock().getY(), event.getBlock().getZ());
        if(event.getItem().getType() == Material.WATER_BUCKET && block.getBlockData() instanceof Waterlogged){
            event.setCancelled(true);
            return;
        }

        block = event.getBlock().getWorld().getBlockAt(event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ()-1);
        if(event.getItem().getType() == Material.WATER_BUCKET && block.getBlockData() instanceof Waterlogged){
            event.setCancelled(true);
            return;
        }

        block = event.getBlock().getWorld().getBlockAt(event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ()+1);
        if(event.getItem().getType() == Material.WATER_BUCKET && block.getBlockData() instanceof Waterlogged){
            event.setCancelled(true);
            return;
        }

        block = event.getBlock().getWorld().getBlockAt(event.getBlock().getX(), event.getBlock().getY()-1, event.getBlock().getZ());
        if(event.getItem().getType() == Material.WATER_BUCKET && block.getBlockData() instanceof Waterlogged){
            event.setCancelled(true);
            return;
        }

        block = event.getBlock().getWorld().getBlockAt(event.getBlock().getX(), event.getBlock().getY()+1, event.getBlock().getZ());
        if(event.getItem().getType() == Material.WATER_BUCKET && block.getBlockData() instanceof Waterlogged){
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event){
        if(event.getBucket() == Material.WATER_BUCKET && event.getBlockClicked().getBlockData() instanceof Waterlogged){
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cWater Logging blocks is not allowed.");
        }
    }

    @EventHandler
    public void onHurt(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            Chunk chunk = player.getLocation().getChunk();

            if(inClaim(chunk)){
                Claim claim = getClaim(chunk);

                if(claim.getType() == 2){
                    event.setDamage(0.0F);
                    event.setCancelled(true);
                    if(event.getDamager() instanceof Player){
                        Player attacker = (Player) event.getDamager();
                        attacker.sendMessage("§cYou cannot attack players §aSafe Zones§7.");
                        return;
                    }
                }
            }

            if(event.getDamager() instanceof Player){
                Player attacker = (Player) event.getDamager();

                MyFaction victomsFaction = getFactionFromUUID(attacker.getUniqueId());
                MyFaction attackerFaction = getFactionFromUUID(attacker.getUniqueId());

                if(victomsFaction != null && attackerFaction != null){
                    if(victomsFaction.getKey().equals(attackerFaction.getKey())){
                        event.setDamage(0.0F);
                        event.setCancelled(true);
                        attacker.sendMessage("§cYou cannot attack players in your own faction.");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();

            if(event.getFoodLevel() < ((Player) event.getEntity()).getFoodLevel()){
                Chunk chunk = player.getLocation().getChunk();

                if(inClaim(chunk)){
                    Claim claim = getClaim(chunk);
                    if(claim.getType() == 2){
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            Chunk chunk = player.getLocation().getChunk();

            if(inClaim(chunk)){
                Claim claim = getClaim(chunk);
                if(claim.getType() == 2){
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event){
        Chunk chunk = event.getLocation().getChunk();

        if(inClaim(chunk)){
            Claim claim = getClaim(chunk);
            if(claim.getType() > 0){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event){
        Chunk chunk = event.getLocation().getChunk();

        if(inClaim(chunk)){
            Claim claim = getClaim(chunk);
            if(claim.getType() > 0){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent event){
        Chunk chunk = event.getEntity().getLocation().getChunk();

        if(inClaim(chunk)){
            Claim claim = getClaim(chunk);
            if(claim.getType() > 0){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onChangeBlock(EntityChangeBlockEvent event){
        if(event.getEntity().getType().equals(EntityType.ENDERMAN)){
            Chunk chunk = event.getBlock().getLocation().getChunk();

            if(inClaim(chunk)){
                Claim claim = getClaim(chunk);
                if(claim.getType() > 0){
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Chunk chunk = event.getPlayer().getLocation().getChunk();

        if(isAutoClaiming(event.getPlayer().getUniqueId())){
            if(!autoClaimChunk(event.getPlayer(), chunk) && isMapping(event.getPlayer().getUniqueId())){
                mapLandscape(event.getPlayer(), chunk);
            }
        }else if(isMapping(event.getPlayer().getUniqueId())){
            mapLandscape(event.getPlayer(), chunk);
        }

        UUID key = null;
        if(enteredClaim.containsKey(event.getPlayer())){
            key = enteredClaim.get(event.getPlayer());
        }

        if(inClaim(chunk)){
            Claim claim = getClaim(chunk);

            if(key == null || !key.equals(claim.getKey())){
                enteredClaim.put(event.getPlayer(), claim.getKey());

                if(claim.getType() > 0){
                    Zone zone = getZoneByUUID(claim.getKey());

                    if(zone != null){
                        sendTitle(event.getPlayer(), getChatColor(zone.getColor())+zone.getName(), "", 0, 1, 0);
                    }

                }else{
                    MyFaction faction = getFactionFromUUID(claim.getKey());
                    if(faction != null){
                        sendTitle(event.getPlayer(), getChatColor(faction.getColor())+faction.getName(), "", 0, 1, 0);
                    }
                }
            }

        }else if(key != null){
            enteredClaim.put(event.getPlayer(), null);
            sendTitle(event.getPlayer(), "§2Wilderness", "", 0, 1, 0);
        }
    }
}

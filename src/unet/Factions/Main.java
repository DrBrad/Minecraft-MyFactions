package unet.Factions;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.util.*;

import static unet.Factions.Faction.*;
import static unet.Factions.Handlers.sendTitle;

public class Main extends JavaPlugin implements Listener {

    public static Plugin plugin;
    private HashMap<Player, String> enteredClaim = new HashMap<>();
    private ArrayList<Material> noeditting = new ArrayList<>();
    public static ArrayList<Material> transparentBlocks = new ArrayList<>();
    public static ArrayList<Player> mapFactions = new ArrayList<>();
    public static HashMap<Player, ArrayList<Location>> mappedChunks = new HashMap<>();
    public static ArrayList<Player> teleport = new ArrayList<>();
    public static ArrayList<Player> factionChat = new ArrayList<>();
    public static HashMap<Player, Location> lastTeleport = new HashMap<>();
    public static int teleportDelay = 30;

    //SPAWNER ONLY OBTAIN EGG IF SILK
    //NO CREEPER SPAWN IN CLAIM
    //CREATE SAFE ZONES

    @Override
    public void onEnable(){
        plugin = this;
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("f").setExecutor(new FactionCommands());
        getCommand("warp").setExecutor(new EssentialCommands());
        getCommand("warps").setExecutor(new EssentialCommands());
        getCommand("setwarp").setExecutor(new EssentialCommands());
        getCommand("delwarp").setExecutor(new EssentialCommands());
        getCommand("home").setExecutor(new EssentialCommands());
        getCommand("sethome").setExecutor(new EssentialCommands());
        getCommand("spawn").setExecutor(new EssentialCommands());
        getCommand("setspawn").setExecutor(new EssentialCommands());
        getCommand("tpa").setExecutor(new EssentialCommands());
        getCommand("tpaa").setExecutor(new EssentialCommands());
        getCommand("tpad").setExecutor(new EssentialCommands());
        getCommand("back").setExecutor(new EssentialCommands());

        Material[] noedit = {
                Material.CHEST,
                Material.TRAPPED_CHEST,
                Material.SHULKER_BOX,
                Material.ANVIL,
                Material.CHIPPED_ANVIL,
                Material.DAMAGED_ANVIL,
                Material.BLACK_SHULKER_BOX,
                Material.BLUE_SHULKER_BOX,
                Material.BROWN_SHULKER_BOX,
                Material.CYAN_SHULKER_BOX,
                Material.GRAY_SHULKER_BOX,
                Material.GREEN_SHULKER_BOX,
                Material.LIGHT_BLUE_SHULKER_BOX,
                Material.LIGHT_GRAY_SHULKER_BOX,
                Material.LIME_SHULKER_BOX,
                Material.MAGENTA_SHULKER_BOX,
                Material.ORANGE_SHULKER_BOX,
                Material.PINK_SHULKER_BOX,
                Material.PURPLE_SHULKER_BOX,
                Material.RED_SHULKER_BOX,
                Material.WHITE_SHULKER_BOX,
                Material.YELLOW_SHULKER_BOX,
                Material.FURNACE,
                Material.FURNACE_MINECART,
                Material.CHEST_MINECART,
                Material.HOPPER,
                Material.HOPPER_MINECART,
                Material.TNT,
                Material.TNT_MINECART,
                Material.COMMAND_BLOCK,
                Material.COMMAND_BLOCK_MINECART,
                Material.SPAWNER,
                Material.BIRCH_BOAT,
                Material.ACACIA_BOAT,
                Material.DARK_OAK_BOAT,
                Material.JUNGLE_BOAT,
                Material.OAK_BOAT,
                Material.SPRUCE_BOAT,
                Material.MINECART,
                Material.DARK_OAK_DOOR,
                Material.ACACIA_DOOR,
                Material.BIRCH_DOOR,
                Material.CRIMSON_DOOR,
                Material.IRON_DOOR,
                Material.JUNGLE_DOOR,
                Material.OAK_DOOR,
                Material.SPRUCE_DOOR,
                Material.WARPED_DOOR,
                Material.ACACIA_TRAPDOOR,
                Material.BIRCH_TRAPDOOR,
                Material.CRIMSON_TRAPDOOR,
                Material.DARK_OAK_TRAPDOOR,
                Material.IRON_TRAPDOOR,
                Material.JUNGLE_TRAPDOOR,
                Material.OAK_TRAPDOOR,
                Material.SPRUCE_TRAPDOOR,
                Material.WARPED_TRAPDOOR
        };

        Collections.addAll(noeditting, noedit);


        Material[] transparent = {
                Material.AIR,
                Material.CAVE_AIR,
                Material.VOID_AIR
        };

        Collections.addAll(transparentBlocks, transparent);

        readAllClaims();

        //FIRST IS DELAY SECOND IS REPEAT
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
            @Override
            public void run(){
                for(Player player : getServer().getOnlinePlayers()){
                    String factionName = getFaction(player);
                    if(factionName != null){
                        setFactionPower(factionName, getFactionPower(factionName)+1);
                    }
                }

            }
        }, 36000, 36000);
    }

    @Override
    public void onDisable(){
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event){
        lastTeleport.put(event.getPlayer(), event.getFrom());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        String factionName = getFaction(event.getPlayer());
        if(factionName != null){
            if(factionChat.contains(event.getPlayer())){
                event.getMessage();

                JSONObject players = getFactionPlayers(factionName);

                Iterator<String> keys = players.keys();
                while(keys.hasNext()){
                    String playerUUID = keys.next();
                    Player player = Bukkit.getPlayer(UUID.fromString(playerUUID));

                    if(player != null && player.isOnline()){
                        player.sendMessage("§6[§c"+factionName+"§6][§c"+event.getPlayer().getDisplayName()+"§6]§a: "+event.getMessage());
                    }
                }

                event.getPlayer().sendMessage("§6[§c"+factionName+"§6][§c"+event.getPlayer().getDisplayName()+"§6]§a: "+event.getMessage());
                event.setCancelled(true);

            }else{
                event.setMessage("§6[§c"+factionName+"§6][§c"+event.getPlayer().getDisplayName()+"§6]§7: "+event.getMessage());
            }
        }else{
            event.setMessage("[§c"+event.getPlayer().getDisplayName()+"§6]§7: "+event.getMessage());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        String factionName = getFaction(event.getEntity());
        if(factionName != null){
            setFactionPower(factionName, getFactionPower(factionName)-1);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        String claim = inClaim(event.getPlayer().getLocation().getChunk());

        if(claim != null){
            if(claim.equals(getFaction(event.getPlayer()))){
                if(getPlayerRank(event.getPlayer(), claim) > 0){
                }else{
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cYou cannot place blocks as a recruit.");
                }
            }else{
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cYou cannot place blocks within another factions claim.");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        String claim = inClaim(event.getPlayer().getLocation().getChunk());

        if(claim != null){
            if(claim.equals(getFaction(event.getPlayer()))){
                if(getPlayerRank(event.getPlayer(), claim) > 0){
                }else{
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cYou cannot break blocks as a recruit.");
                }
            }else{
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cYou cannot break blocks within another factions claim.");
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Block block = event.getClickedBlock();

            if(noeditting.contains(block.getType())){
                String claim = inClaim(block.getLocation().getChunk());

                if(claim != null){
                    if(claim.equals(getFaction(event.getPlayer()))){
                        if(getPlayerRank(event.getPlayer(), claim) > 0){
                        }else{
                            event.setCancelled(true);
                            event.getPlayer().sendMessage("§cYou cannot interact with blocks as a recruit.");
                        }
                    }else{
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§cYou cannot interact with blocks within another factions claim.");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHurt(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();

            String claim = inClaim(player.getLocation().getChunk());
            if(claim != null){
                if(claim.equals("Safe-Zone")){
                    event.setDamage(0.0F);
                    event.setCancelled(true);
                    player.sendMessage("§cYou cannot attack players a §aSafe Zone§7.");
                }
            }

            if(event.getDamager() instanceof Player){
                Player attacker = (Player) event.getDamager();
                if(getFaction(player).equals(getFaction(attacker))){
                    event.setDamage(0.0F);
                    event.setCancelled(true);
                    attacker.sendMessage("§cYou cannot attack players in your own faction.");
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        if(teleport.contains(event.getPlayer())){
            if((int)event.getFrom().getX() > (int)event.getTo().getX() || (int)event.getFrom().getX() < (int)event.getTo().getX() ||
                    (int)event.getFrom().getZ() > (int)event.getTo().getZ() || (int)event.getFrom().getZ() > (int)event.getTo().getZ()){
                teleport.remove(event.getPlayer());
                event.getPlayer().sendMessage("§cTeleport cancelled due to movement.");
            }
        }

        String claim = inClaim(event.getPlayer().getLocation().getChunk());
        String claimText = "§c"+claim;

        if(claim == null){
            claimText = "§2Wilderness";
            claim = "";
        }

        if(enteredClaim.containsKey(event.getPlayer())){
            if(!enteredClaim.get(event.getPlayer()).equals(claim)){
                sendTitle(event.getPlayer(), claimText, "", 0, 1, 0);
            }
        }else{
            sendTitle(event.getPlayer(), claimText, "", 0, 1, 0);
        }

        enteredClaim.put(event.getPlayer(), claim);

        if(mapFactions.contains(event.getPlayer())){
            viewClaims(event.getPlayer(), event.getPlayer().getLocation().getChunk());
        }
    }

    private void viewClaims(Player player, Chunk centerChunk){
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

        String factionName = getFaction(player);

        if(!mappedChunks.containsKey(player)){
            mappedChunks.put(player, new ArrayList<>());
        }

        for(Chunk chunk : chunks){
            String claim = inClaim(chunk);
            if(!mappedChunks.get(player).contains(chunk.getBlock(0, 0, 0).getLocation())){
                mappedChunks.get(player).add(chunk.getBlock(0, 0, 0).getLocation());

                for(int x = 0; x < 16; x++){
                    for(int y = 254; y > 0; y--){
                        Block block = chunk.getBlock(x, y, 0);
                        Block blockAbove = chunk.getBlock(x, y+1, 0);

                        if(!transparentBlocks.contains(block.getType()) && transparentBlocks.contains(blockAbove.getType())){
                            if(claim == null){
                                player.sendBlockChange(block.getLocation(), Material.WHITE_CONCRETE.createBlockData());
                            }else if(factionName.equals(claim)){
                                player.sendBlockChange(block.getLocation(), Material.LIME_CONCRETE.createBlockData());
                            }else{
                                player.sendBlockChange(block.getLocation(), Material.RED_CONCRETE.createBlockData());
                            }
                            break;
                        }
                    }
                }

                for(int x = 0; x < 16; x++){
                    for(int y = 254; y > 0; y--){
                        Block block = chunk.getBlock(x, y, 15);
                        Block blockAbove = chunk.getBlock(x, y+1, 15);

                        if(!transparentBlocks.contains(block.getType()) && transparentBlocks.contains(blockAbove.getType())){
                            if(claim == null){
                                player.sendBlockChange(block.getLocation(), Material.WHITE_CONCRETE.createBlockData());
                            }else if(factionName.equals(claim)){
                                player.sendBlockChange(block.getLocation(), Material.LIME_CONCRETE.createBlockData());
                            }else{
                                player.sendBlockChange(block.getLocation(), Material.RED_CONCRETE.createBlockData());
                            }
                            break;
                        }
                    }
                }

                for(int z = 0; z < 16; z++){
                    for(int y = 254; y > 0; y--){
                        Block block = chunk.getBlock(0, y, z);
                        Block blockAbove = chunk.getBlock(0, y+1, z);

                        if(!transparentBlocks.contains(block.getType()) && transparentBlocks.contains(blockAbove.getType())){
                            if(claim == null){
                                player.sendBlockChange(block.getLocation(), Material.WHITE_CONCRETE.createBlockData());
                            }else if(factionName.equals(claim)){
                                player.sendBlockChange(block.getLocation(), Material.LIME_CONCRETE.createBlockData());
                            }else{
                                player.sendBlockChange(block.getLocation(), Material.RED_CONCRETE.createBlockData());
                            }
                            break;
                        }
                    }
                }

                for(int z = 0; z < 16; z++){
                    for(int y = 254; y > 0; y--){
                        Block block = chunk.getBlock(15, y, z);
                        Block blockAbove = chunk.getBlock(15, y+1, z);

                        if(!transparentBlocks.contains(block.getType()) && transparentBlocks.contains(blockAbove.getType())){
                            if(claim == null){
                                player.sendBlockChange(block.getLocation(), Material.WHITE_CONCRETE.createBlockData());
                            }else if(factionName.equals(claim)){
                                player.sendBlockChange(block.getLocation(), Material.LIME_CONCRETE.createBlockData());
                            }else{
                                player.sendBlockChange(block.getLocation(), Material.RED_CONCRETE.createBlockData());
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}

package unet.Factions;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.File;
import java.util.*;

import static unet.Factions.Config.*;
import static unet.Factions.Faction.*;
import static unet.Factions.Handlers.*;

public class Main extends JavaPlugin implements Listener {

    public static Plugin plugin;
    private HashMap<Player, String> enteredClaim = new HashMap<>();
    private ArrayList<Material> noeditting = new ArrayList<>();
    public static ArrayList<Material> transparentBlocks = new ArrayList<>();
    public static ArrayList<Material> nogoBlocks = new ArrayList<>();
    public static ArrayList<Player> mapFactions = new ArrayList<>();
    public static HashMap<Player, ArrayList<Location>> mappedChunks = new HashMap<>();
    public static ArrayList<Player> teleport = new ArrayList<>();
    public static ArrayList<Player> factionChat = new ArrayList<>();
    public static HashMap<Player, Location> lastTeleport = new HashMap<>();
    public static HashMap<Player, Player> playerTeleport = new HashMap<>();
    public static HashMap<Player, AutoClaim> autoClaimList = new HashMap<>();

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
        getCommand("wild").setExecutor(new EssentialCommands());
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

        Material[] nogo = {
                Material.LAVA,
                Material.WATER,
                Material.MAGMA_BLOCK,
                Material.CACTUS,
                Material.FIRE,
                Material.SOUL_FIRE,
                Material.CAMPFIRE,
                Material.SOUL_CAMPFIRE
        };

        Collections.addAll(nogoBlocks, nogo);

        readAllClaims();
        new Config();

        //FIRST IS DELAY SECOND IS REPEAT
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
            @Override
            public void run(){
                for(Player player : getServer().getOnlinePlayers()){
                    String factionName = getFaction(player.getUniqueId());
                    if(factionName != null){
                        setFactionPower(factionName, getFactionPower(factionName)+1);
                    }
                }

            }
        }, 36000, 36000);
    }

    @Override
    public void onDisable(){
        if(mappedChunks.size() > 0){
            for(Player player : mappedChunks.keySet()){
                for(Location location : mappedChunks.get(player)){
                    renewMappedLandscape(player, location.getChunk());
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        event.setJoinMessage("§c"+event.getPlayer().getDisplayName()+"§7 Has joined the server!");

        if(!event.getPlayer().hasPlayedBefore()){
            File spawn = new File(plugin.getDataFolder()+File.separator+"spawn.yml");
            if(spawn.exists()){
                FileConfiguration config = YamlConfiguration.loadConfiguration(spawn);

                event.getPlayer().teleport(new Location(plugin.getServer().getWorld(config.getString("world")), config.getDouble("x"),
                        config.getDouble("y"), config.getDouble("z"), (float)config.getDouble("yaw"), (float)config.getDouble("pitch")));
            }

            event.getPlayer().getInventory().getItemInMainHand().setType(Material.IRON_SWORD);
            event.getPlayer().getInventory().getItemInOffHand().setType(Material.SHIELD);
            event.getPlayer().getInventory().getHelmet().setType(Material.IRON_HELMET);
            event.getPlayer().getInventory().getChestplate().setType(Material.IRON_CHESTPLATE);
            event.getPlayer().getInventory().getLeggings().setType(Material.IRON_LEGGINGS);
            event.getPlayer().getInventory().getBoots().setType(Material.IRON_BOOTS);
        }

        try{
            File pnr = new File(plugin.getDataFolder()+File.separator+"pnr.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(pnr);
            if(!config.contains(event.getPlayer().getUniqueId().toString())){
                config.set(event.getPlayer().getDisplayName(), event.getPlayer().getUniqueId().toString());
                config.save(pnr);

            }else if(!config.getString(event.getPlayer().getUniqueId().toString()).equals(event.getPlayer().getDisplayName())){
                config.set(event.getPlayer().getDisplayName(), event.getPlayer().getUniqueId().toString());
                config.save(pnr);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        event.setQuitMessage("§c"+event.getPlayer().getDisplayName()+"§7 Has left the game!");
        if(mappedChunks.containsKey(event.getPlayer())){
            mappedChunks.remove(event.getPlayer());
        }

        if(autoClaimList.containsKey(event.getPlayer())){
            autoClaimList.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        File spawn = new File(plugin.getDataFolder()+File.separator+"spawn.yml");
        if(spawn.exists()){
            FileConfiguration config = YamlConfiguration.loadConfiguration(spawn);

            event.setRespawnLocation(new Location(plugin.getServer().getWorld(config.getString("world")), config.getDouble("x"),
                    config.getDouble("y"), config.getDouble("z"), (float)config.getDouble("yaw"), (float)config.getDouble("pitch")));
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event){
        lastTeleport.put(event.getPlayer(), event.getFrom());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        String factionName = getFaction(event.getPlayer().getUniqueId());
        if(factionName != null){
            if(factionChat.contains(event.getPlayer())){
                event.getMessage();

                JSONObject players = getFactionPlayers(factionName);

                Iterator<String> keys = players.keys();
                while(keys.hasNext()){
                    String playerUUID = keys.next();
                    Player player = Bukkit.getPlayer(UUID.fromString(playerUUID));

                    if(player != null && player.isOnline()){
                        int rank = getPlayerRank(event.getPlayer().getUniqueId(), factionName);
                        String[] names = { "Member", "Recruit", "Admin", "Owner" };
                        player.sendMessage("§6[§c"+factionName+"§6][§c"+names[rank]+"§6][§c"+event.getPlayer().getDisplayName()+"§6]§7: §a"+event.getMessage());
                    }
                }

                event.setCancelled(true);

            }else{
                int rank = getPlayerRank(event.getPlayer().getUniqueId(), factionName);
                String[] names = { "Member", "Recruit", "Admin", "Owner" };
                event.setFormat("§6[§c"+factionName+"§6][§c"+names[rank]+"§6][§c"+event.getPlayer().getDisplayName()+"§6]§7: "+event.getMessage());
            }
        }else{
            event.setFormat("§6[§c"+event.getPlayer().getDisplayName()+"§6]§7: "+event.getMessage());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        String factionName = getFaction(event.getEntity().getUniqueId());
        if(factionName != null){
            setFactionPower(factionName, getFactionPower(factionName)-playerDeathCost);
        }
        if(autoClaimList.containsKey(event.getEntity())){
            autoClaimList.remove(event.getEntity());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        String claim = inClaim(event.getBlock().getLocation().getChunk());

        if(claim != null){
            if((claim.equals("Safe-Zone") || claim.equals("Pvp-Zone")) && event.getPlayer().isOp()){
            }else if(claim.equals(getFaction(event.getPlayer().getUniqueId()))){
                if(getPlayerRank(event.getPlayer().getUniqueId(), claim) > 0){
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
        String claim = inClaim(event.getBlock().getLocation().getChunk());

        if(claim != null){
            if((claim.equals("Safe-Zone") || claim.equals("Pvp-Zone")) && event.getPlayer().isOp()){
            }else if(claim.equals(getFaction(event.getPlayer().getUniqueId()))){
                if(getPlayerRank(event.getPlayer().getUniqueId(), claim) > 0){
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
                    if((claim.equals("Safe-Zone") || claim.equals("Pvp-Zone")) && event.getPlayer().isOp()){
                    }else if(claim.equals(getFaction(event.getPlayer().getUniqueId()))){
                        if(getPlayerRank(event.getPlayer().getUniqueId(), claim) > 0){
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
                if(claim.equals("Safe-Zone") && !safeZonePvp){
                    event.setDamage(0.0F);
                    event.setCancelled(true);
                    player.sendMessage("§cYou cannot attack players a §aSafe Zone§7.");
                }
            }

            if(event.getDamager() instanceof Player){
                Player attacker = (Player) event.getDamager();
                if(getFaction(player.getUniqueId()).equals(getFaction(attacker.getUniqueId())) && !sameFactionPvp){
                    event.setDamage(0.0F);
                    event.setCancelled(true);
                    attacker.sendMessage("§cYou cannot attack players in your own faction.");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();

            String claim = inClaim(player.getLocation().getChunk());
            if(claim != null){
                if(claim.equals("Safe-Zone") && !safeZonePvp){
                    event.setDamage(0.0f);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event){
        String claim = inClaim(event.getLocation().getChunk());
        if(claim != null && (claim.equals("Safe-Zone") || claim.equals("Pvp-Zone"))){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event){
        String claim = inClaim(event.getLocation().getChunk());
        if(claim != null && (claim.equals("Safe-Zone") || claim.equals("Pvp-Zone"))){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChangeBlock(EntityChangeBlockEvent event){
        String claim = inClaim(event.getBlock().getLocation().getChunk());
        if(event.getEntity().getType().equals(EntityType.ENDERMAN)){
            if(claim != null && (claim.equals("Safe-Zone") || claim.equals("Pvp-Zone"))){
                event.setCancelled(true);
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

        if(autoClaimList.containsKey(event.getPlayer())){
            autoClaim(event.getPlayer());
        }

        String claim = inClaim(event.getPlayer().getLocation().getChunk());
        String claimText;
        if(claim != null && (claim.equals("Safe-Zone") || claim.equals(getFaction(event.getPlayer().getUniqueId())))){
            claimText = "§a"+claim;
        }else{
            claimText = "§c"+claim;
        }

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

    public void autoClaim(Player player){
        AutoClaim autoClaim = autoClaimList.get(player);
        if(!player.getLocation().getChunk().getBlock(0, 0, 0).getLocation().equals(autoClaim.getLocation())){
            String claim = inClaim(player.getLocation().getChunk());

            if(autoClaim.getFactionName().equals("Safe-Zone")){
                if(player.isOp()){
                    if(autoClaim.isClaiming()){
                        if(claim != null){
                            if(claim.equalsIgnoreCase("Safe-Zone")){
                                return;
                            }else if(claim.equalsIgnoreCase("Pvp-Zone")){
                                unclaimPvpZone(player.getLocation().getChunk());
                            }else{
                                unclaimForFaction(claim, player.getLocation().getChunk(), getFactionPower(claim));
                            }
                        }

                        if(claimSafeZone(player.getLocation().getChunk())){
                            player.sendMessage("§7You have claimed this chunk as a §aSafe-Zone§7!");
                            autoClaim.setLocation(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());

                            if(mappedChunks.containsKey(player)){
                                mappedChunks.get(player).remove(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());
                                viewClaims(player, player.getLocation().getChunk());
                            }
                        }else{
                            player.sendMessage("§cFailed to claim chunk as Safe-Zone.");
                        }

                    }else if(claim != null && claim.equalsIgnoreCase("Safe-Zone")){
                        if(unclaimSafeZone(player.getLocation().getChunk())){
                            player.sendMessage("§7You have unclaimed §aSafe-Zone§7 chunk!");
                            autoClaim.setLocation(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());

                            if(mappedChunks.containsKey(player)){
                                mappedChunks.get(player).remove(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());
                                viewClaims(player, player.getLocation().getChunk());
                            }
                        }else{
                            player.sendMessage("§cFailed to unclaim Safe-Zone chunk.");
                        }
                    }
                }else{
                    autoClaimList.remove(player);
                    player.sendMessage("§cOnly server admins can auto unclaim Safe-Zone, turning off auto claim.");
                }

            }else if(autoClaim.getFactionName().equals("Pvp-Zone")){
                if(player.isOp()){
                    if(autoClaim.isClaiming()){
                        if(claim != null){
                            if(claim.equalsIgnoreCase("Pvp-Zone")){
                                return;
                            }else if(claim.equalsIgnoreCase("Safe-Zone")){
                                unclaimPvpZone(player.getLocation().getChunk());
                            }else{
                                unclaimForFaction(claim, player.getLocation().getChunk(), getFactionPower(claim));
                            }
                        }

                        if(claimPvpZone(player.getLocation().getChunk())){
                            player.sendMessage("§7You have claimed this chunk as a §aPvp-Zone§7!");
                            autoClaim.setLocation(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());

                            if(mappedChunks.containsKey(player)){
                                mappedChunks.get(player).remove(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());
                                viewClaims(player, player.getLocation().getChunk());
                            }
                        }else{
                            player.sendMessage("§cFailed to claim chunk as Pvp-Zone.");
                        }

                    }else if(claim != null && claim.equalsIgnoreCase("Pvp-Zone")){
                        if(unclaimPvpZone(player.getLocation().getChunk())){
                            player.sendMessage("§7You have unclaimed §aPvp-Zone§7 chunk!");
                            autoClaim.setLocation(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());

                            if(mappedChunks.containsKey(player)){
                                mappedChunks.get(player).remove(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());
                                viewClaims(player, player.getLocation().getChunk());
                            }
                        }else{
                            player.sendMessage("§cFailed to unclaim Pvp-Zone chunk.");
                        }
                    }
                }else{
                    autoClaimList.remove(player);
                    player.sendMessage("§cOnly server admins can auto unclaim SaPvp-Zone, turning off auto claim.");
                }

            }else{
                String factionName = getFaction(player.getUniqueId());
                if(factionName != null){
                    if(getPlayerRank(player.getUniqueId(), factionName) > 1){
                        if(getPlayerRank(player.getUniqueId(), factionName) > 1){
                            if(factionName.equals(autoClaim.getFactionName())){
                                if(claim == null && autoClaim.isClaiming()){
                                    int power = getFactionPower(factionName);
                                    if(power > claimPower-1){
                                        if(claimForFaction(factionName, player.getLocation().getChunk(), power)){
                                            player.sendMessage("§7You have claimed this chunk!");
                                            autoClaim.setLocation(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());

                                            if(mappedChunks.containsKey(player)){
                                                mappedChunks.get(player).remove(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());
                                                viewClaims(player, player.getLocation().getChunk());
                                            }
                                        }else{
                                            player.sendMessage("§cFailed to claim chunk.");
                                        }
                                    }else{
                                        autoClaimList.remove(player);
                                        player.sendMessage("§cYour faction doesn't have enough power, turning off auto claim.");
                                    }
                                }else if(claim.equals(factionName) && !autoClaim.isClaiming()){
                                    if(unclaimForFaction(factionName, player.getLocation().getChunk(), getFactionPower(factionName))){
                                        player.sendMessage("§7You have unclaimed this chunk!");
                                        autoClaim.setLocation(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());

                                        if(mappedChunks.containsKey(player)){
                                            mappedChunks.get(player).remove(player.getLocation().getChunk().getBlock(0, 0, 0).getLocation());
                                            viewClaims(player, player.getLocation().getChunk());
                                        }
                                    }else{
                                        player.sendMessage("§cFailed to unclaim chunk.");
                                    }
                                }
                            }else{
                                autoClaimList.remove(player);
                                player.sendMessage("§cFaction name mismatch, turning off auto claim.");
                            }
                        }else{
                            autoClaimList.remove(player);
                            player.sendMessage("§cYour not a high enough rank in your faction to auto claim, turning off auto claim.");
                        }
                    }else{
                        autoClaimList.remove(player);
                        player.sendMessage("§cYour not a high enough rank in your faction to auto claim, turning off auto claim.");
                    }
                }else{
                    autoClaimList.remove(player);
                    player.sendMessage("§cYour not a part of a faction, turning off auto claim.");
                }
            }
        }
    }

    public static void viewClaims(Player player, Chunk centerChunk){
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

        String factionName = getFaction(player.getUniqueId());
        factionName = (factionName == null) ? "null" : factionName;

        if(!mappedChunks.containsKey(player)){
            mappedChunks.put(player, new ArrayList<>());
        }

        for(Chunk chunk : chunks){
            String claim = inClaim(chunk);
            if(!mappedChunks.get(player).contains(chunk.getBlock(0, 0, 0).getLocation())){
                mappedChunks.get(player).add(chunk.getBlock(0, 0, 0).getLocation());

                Material material;
                if(claim == null){
                    material = Material.WHITE_CONCRETE;
                }else if(factionName.equals(claim) || claim.equals("Safe-Zone")){
                    material = Material.LIME_CONCRETE;
                }else{
                    material = Material.RED_CONCRETE;
                }

                for(int x = 0; x < 16; x++){
                    for(int y = 254; y > -1; y--){
                        Block block = chunk.getBlock(x, y, 0);
                        Block blockAbove = chunk.getBlock(x, y+1, 0);

                        if((!transparentBlocks.contains(block.getType()) && transparentBlocks.contains(blockAbove.getType())) || y == 0){
                            player.sendBlockChange(block.getLocation(), material.createBlockData());
                            break;
                        }
                    }
                }

                for(int x = 0; x < 16; x++){
                    for(int y = 254; y > -1; y--){
                        Block block = chunk.getBlock(x, y, 15);
                        Block blockAbove = chunk.getBlock(x, y+1, 15);

                        if((!transparentBlocks.contains(block.getType()) && transparentBlocks.contains(blockAbove.getType())) || y == 0){
                            player.sendBlockChange(block.getLocation(), material.createBlockData());
                            break;
                        }
                    }
                }

                for(int z = 0; z < 16; z++){
                    for(int y = 254; y > -1; y--){
                        Block block = chunk.getBlock(0, y, z);
                        Block blockAbove = chunk.getBlock(0, y+1, z);

                        if((!transparentBlocks.contains(block.getType()) && transparentBlocks.contains(blockAbove.getType())) || y == 0){
                            player.sendBlockChange(block.getLocation(), material.createBlockData());
                            break;
                        }
                    }
                }

                for(int z = 0; z < 16; z++){
                    for(int y = 254; y > -1; y--){
                        Block block = chunk.getBlock(15, y, z);
                        Block blockAbove = chunk.getBlock(15, y+1, z);

                        if((!transparentBlocks.contains(block.getType()) && transparentBlocks.contains(blockAbove.getType())) || y == 0){
                            player.sendBlockChange(block.getLocation(), material.createBlockData());
                            break;
                        }
                    }
                }
            }
        }
    }
}

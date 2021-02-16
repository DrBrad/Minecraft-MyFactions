package unet.Factions.Handlers;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class BlockHandler {

    public static List<Material> getAir(){
        Material[] tmp = {
                Material.AIR,
                Material.CAVE_AIR,
                Material.VOID_AIR
        };

        return Arrays.asList(tmp);
    }

    public static List<Material> getNoEdit(){
        Material[] tmp = {
                Material.CHEST,
                Material.TRAPPED_CHEST,
                Material.SHULKER_BOX,
                Material.ANVIL,
                Material.ENDER_CHEST,
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
                Material.DROPPER,
                Material.DISPENSER,
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
                Material.WARPED_TRAPDOOR,
                Material.ITEM_FRAME,
                Material.ARMOR_STAND,
                Material.WATER,
                Material.LAVA,
                Material.POLISHED_BLACKSTONE_PRESSURE_PLATE,
                Material.ACACIA_PRESSURE_PLATE,
                Material.BIRCH_PRESSURE_PLATE,
                Material.CRIMSON_PRESSURE_PLATE,
                Material.DARK_OAK_PRESSURE_PLATE,
                Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
                Material.JUNGLE_PRESSURE_PLATE,
                Material.LIGHT_WEIGHTED_PRESSURE_PLATE,
                Material.OAK_PRESSURE_PLATE,
                Material.SPRUCE_PRESSURE_PLATE,
                Material.STONE_PRESSURE_PLATE,
                Material.WARPED_PRESSURE_PLATE,
                Material.BIRCH_BUTTON,
                Material.ACACIA_BUTTON,
                Material.CRIMSON_BUTTON,
                Material.DARK_OAK_BUTTON,
                Material.JUNGLE_BUTTON,
                Material.OAK_BUTTON,
                Material.POLISHED_BLACKSTONE_BUTTON,
                Material.SPRUCE_BUTTON,
                Material.STONE_BUTTON,
                Material.WARPED_BUTTON,
                Material.LEVER,
                Material.REPEATER,
                Material.COMPARATOR,
                Material.COMMAND_BLOCK,
                Material.COMMAND_BLOCK_MINECART,
                Material.CHAIN_COMMAND_BLOCK,
                Material.REPEATING_COMMAND_BLOCK,
                Material.BREWING_STAND,
                Material.CAULDRON,
                Material.BEACON
        };

        return Arrays.asList(tmp);
    }

    public static List<Material> getSafeNoEdit(){
        Material[] tmp = {
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
                Material.DROPPER,
                Material.DISPENSER,
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
                Material.ITEM_FRAME,
                Material.ARMOR_STAND,
                Material.WATER,
                Material.LAVA,
                Material.REPEATER,
                Material.COMPARATOR,
                Material.COMMAND_BLOCK,
                Material.COMMAND_BLOCK_MINECART,
                Material.CHAIN_COMMAND_BLOCK,
                Material.REPEATING_COMMAND_BLOCK
        };

        return Arrays.asList(tmp);
    }

    public static List<Material> getXRayBlocks(){
        Material[] tmp = {
                Material.DIAMOND_BLOCK,
                Material.IRON_BLOCK,
                Material.GOLD_BLOCK,
                Material.EMERALD_BLOCK,
                Material.NETHERITE_BLOCK,
                Material.LAPIS_BLOCK,
                Material.REDSTONE_BLOCK,
                Material.COAL_BLOCK,

                Material.DIAMOND_ORE,
                Material.IRON_ORE,
                Material.GOLD_ORE,
                Material.NETHER_GOLD_ORE,
                Material.EMERALD_ORE,
                Material.ANCIENT_DEBRIS,
                Material.LAPIS_ORE,
                Material.REDSTONE_ORE,
                Material.COAL_ORE,

                Material.SPAWNER,
                Material.CHEST,
                Material.SHULKER_BOX,
                Material.FURNACE,
                Material.HOPPER,
                Material.CRAFTING_TABLE

        };

        return Arrays.asList(tmp);
    }

    public static List<Material> getDangerous(){
        Material[] tmp = {
                Material.LAVA,
                Material.WATER,
                Material.MAGMA_BLOCK,
                Material.CACTUS,
                Material.FIRE,
                Material.SOUL_FIRE,
                Material.CAMPFIRE,
                Material.SOUL_CAMPFIRE
        };

        return Arrays.asList(tmp);
    }
}

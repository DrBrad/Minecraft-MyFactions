package unet.Factions.Handlers;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class BlockHandler {

    public static List<Material> getNoEdit(){
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
                Material.LAVA
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

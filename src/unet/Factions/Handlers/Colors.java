package unet.Factions.Handlers;

import org.bukkit.Color;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class Colors {

    public static List<String> getAllColors(){
        String[] tmp = {
                "BLACK",
                "DARK_BLUE",
                "DARK_GREEN",
                "DARK_AQUA",
                "DARK_RED",
                "DARK_PURPLE",
                "GOLD",
                "GRAY",
                "DARK_GRAY",
                "BLUE",
                "GREEN",
                "AQUA",
                "RED",
                "LIGHT_PURPLE",
                "YELLOW",
                "WHITE"
        };

        return Arrays.asList(tmp);
    }

    public static int getColorCode(String color){
        String[] tmp = {
                "BLACK",
                "DARK_BLUE",
                "DARK_GREEN",
                "DARK_AQUA",
                "DARK_RED",
                "DARK_PURPLE",
                "GOLD",
                "GRAY",
                "DARK_GRAY",
                "BLUE",
                "GREEN",
                "AQUA",
                "RED",
                "LIGHT_PURPLE",
                "YELLOW",
                "WHITE"
        };

        List<String> colors = Arrays.asList(tmp);

        if(colors.contains(color)){
            return colors.indexOf(color);
        }
        return -1;
    }

    public static Material getBlockFromColor(int color){
        Material[] blocks = {
                Material.BLACK_CONCRETE,
                Material.BLUE_CONCRETE,
                Material.GREEN_CONCRETE,
                Material.CYAN_CONCRETE,
                Material.RED_CONCRETE,
                Material.PURPLE_CONCRETE,
                Material.ORANGE_CONCRETE,
                Material.LIGHT_GRAY_CONCRETE,
                Material.GRAY_CONCRETE,
                Material.LIGHT_BLUE_CONCRETE,
                Material.LIME_CONCRETE,
                Material.CYAN_CONCRETE,   //NO OTHER CHOICE...
                Material.RED_CONCRETE,    //NO OTHER CHOICE
                Material.MAGENTA_CONCRETE,
                Material.YELLOW_CONCRETE,
                Material.WHITE_CONCRETE
        };

        return blocks[color];
    }

    public static String getChatColor(int color){
        String[] codes = {
                "§0",
                "§1",
                "§2",
                "§3",
                "§4",
                "§5",
                "§6",
                "§7",
                "§8",
                "§9",
                "§a",
                "§b",
                "§c",
                "§d",
                "§e",
                "§f"
        };

        return codes[color];
    }

    public static Color getColorRGB(int color){
        Color[] colors = {
                Color.fromRGB(0, 0, 0),
                Color.fromRGB(0, 0, 170),
                Color.fromRGB(0, 170, 0),
                Color.fromRGB(0, 170, 170),
                Color.fromRGB(170, 0, 0),
                Color.fromRGB(170, 0, 170),
                Color.fromRGB(255, 170, 0),
                Color.fromRGB(170, 170, 170),
                Color.fromRGB(85, 85, 85),
                Color.fromRGB(85, 85, 255),
                Color.fromRGB(85, 255, 85),
                Color.fromRGB(85, 255, 255),
                Color.fromRGB(255, 85, 85),
                Color.fromRGB(255, 85, 255),
                Color.fromRGB(255, 255, 85),
                Color.fromRGB(255, 255, 255)
        };

        return colors[color];
    }
}

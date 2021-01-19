package unet.Factions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

import static unet.Factions.Main.*;

public class Handlers {

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        try {
            Object enumTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
            //Object enumSubTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
            Object chat = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\""+title+"\"}");
            //Object subchat = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\""+subtitle+"\"}");
            Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
            Object packet = titleConstructor.newInstance(enumTitle, chat, fadeIn, stay, fadeOut);
            //Object packet2 = titleConstructor.newInstance(enumSubTitle, chat, fadeIn, stay, fadeOut);
            sendPacket(player, packet);
            //sendPacket(player, packet2);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static Class<?> getNMSClass(String name) {
        // org.bukkit.craftbukkit.v1_8_R3...
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        }

        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void teleport(Player player, Location location, String type){
        teleport.add(player);
        player.sendMessage("§7Preparing to teleport to §c"+type+"§7 don't move!");

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
            @Override
            public void run(){
                if(teleport.contains(player)){
                    teleport.remove(player);
                    player.teleport(location);
                }
            }
        }, teleportDelay);
    }
}

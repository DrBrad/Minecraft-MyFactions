package unet.Factions.Handlers;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

import static unet.Factions.Handlers.Config.*;
import static unet.Factions.Main.plugin;

public class GeneralHandler {

    private static ArrayList<Player> teleport = new ArrayList<>();
    private static HashMap<Player, Location> lastTeleport = new HashMap();

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

    public static void teleport(Player player, Location location, String type, Color color){
        teleport.add(player);
        spawnCircle(player, player.getLocation(), color);
        player.sendMessage("§7Preparing to teleport to §c"+type+"§7 don't move!");

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
            @Override
            public void run(){
                if(teleport.contains(player)){
                    teleport.remove(player);
                    lastTeleport.put(player, player.getLocation());
                    player.teleport(location);
                }
            }
        }, getTeleportDelay());
    }

    public static boolean hasLastTeleport(Player player){
        return lastTeleport.containsKey(player);
    }

    public static Location getLastTeleport(Player player){
        if(lastTeleport.containsKey(player)){
            return lastTeleport.get(player);
        }
        return null;
    }

    public static void spawnCircle(Player player, Location location, Color color){
        Particle.DustOptions dust = new Particle.DustOptions(color, 1);
        double size = 1;
        int points = 20;

        for(double y = 0; y < 2.2; y+=(0.1)){
            for(int i = 0; i < 360; i += 360/points){
                double angle = (i * Math.PI/180);
                double x = size * Math.cos(angle);
                double z = size * Math.sin(angle);
                Location loc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
                loc.add(x, y, z);

                player.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 1, 0, 0, 0, 1, dust);
                //player.spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 1, 0, 0, 0, 1, dust);
            }
        }
    }
}

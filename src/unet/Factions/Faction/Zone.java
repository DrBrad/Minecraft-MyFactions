package unet.Factions.Faction;

import org.bukkit.Bukkit;

import java.util.UUID;

public class Zone implements Faction {

    private int type, color;
    private String name;
    private UUID key;

    public Zone(UUID key, String name, int type, int color){
        this.key = key;
        this.name = name;
        this.type = type;
        this.color = color;
    }

    @Override
    public boolean canClaim(UUID uuid){
        return Bukkit.getPlayer(uuid).hasPermission("f.admin");
    }

    @Override
    public UUID getKey(){
        return key;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public int getType(){
        return type;
    }

    @Override
    public int getColor(){
        return color;
    }
}

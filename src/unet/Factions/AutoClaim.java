package unet.Factions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AutoClaim {

    private Player player;
    private String factionName;
    private Location location;
    private boolean claiming;

    public AutoClaim(Player player, String factionName, Location location, boolean claiming){
        this.player = player;
        this.factionName = factionName;
        this.location = location;
        this.claiming = claiming;
    }

    public String getFactionName(){
        return factionName;
    }

    public Location getLocation(){
        return location;
    }

    public boolean isClaiming(){
        return claiming;
    }

    public void setLocation(Location location){
        this.location = location;
    }
}

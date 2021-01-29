package unet.Factions.Claim;

import unet.Factions.Faction.Faction;

public class AutoClaim {

    private Faction faction;
    private boolean claiming;
    private String key;

    public AutoClaim(Faction faction, boolean claiming, String key){
        this.faction = faction;
        this.claiming = claiming;
        this.key = key;
    }

    public Faction getFaction(){
        return faction;
    }

    public boolean isClaiming(){
        return claiming;
    }

    public void setLastLocation(String key){
        this.key = key;
    }

    public String getLastLocation(){
        return key;
    }
}

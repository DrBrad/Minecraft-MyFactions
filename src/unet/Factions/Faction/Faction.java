package unet.Factions.Faction;

import java.util.UUID;

public interface Faction {

    boolean canClaim(UUID uuid);

    UUID getKey();

    String getName();

    int getType();

    int getColor();
}

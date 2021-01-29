package unet.Factions.Claim;

import java.io.Serializable;
import java.util.UUID;

public class Claim implements Serializable {

    private UUID key;
    private int type;

    public Claim(UUID key, int type){
        this.key = key;
        this.type = type;
    }

    public void setKey(UUID key){
        this.key = key;
    }

    public UUID getKey(){
        return key;
    }

    public void setType(int type){
        this.type = type;
    }

    public int getType(){
        return type;
    }
}

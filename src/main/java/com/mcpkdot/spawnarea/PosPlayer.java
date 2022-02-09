package com.mcpkdot.spawnarea;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

import static java.lang.Math.floor;


@Getter
@Setter
public class PosPlayer {
    private Location loc2;
    private Location loc1;
    private UUID uuid;


    public PosPlayer(UUID uuid){
        this.uuid = uuid;
    }

    public Location getLoc1(){
        return new Location(loc1.getWorld(), floor(loc1.getX()), floor(loc1.getY()), floor(loc1.getZ()));
    }

    public Location getLoc2() {
        return new Location(loc2.getWorld(), floor(loc2.getX()), floor(loc2.getY()), floor(loc2.getZ()));
    }
}

package com.mcpkdot.spawnarea;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class Area implements Listener {
    private Location loc2;
    private Location loc1;


    private EntityType[] et;
    private int spawnSpeed;
    private List<Entity> entityList;
    private int maxEntityCount;


    public Area(Location loc1, Location loc2, EntityType[] et, int spawnSpeed, int maxEntityCount){
        this.loc1 = loc1;
        this.loc2 = loc2;
        this.et = et;
        this.spawnSpeed = spawnSpeed;
        this.entityList = new ArrayList<>();
        this.maxEntityCount = maxEntityCount;
    }

    private Location getRandomLocation() {
        while(true){
            Preconditions.checkArgument(loc1.getWorld() == loc2.getWorld());
            double minX = Math.min(loc1.getX(), loc2.getX());
            double minY = Math.min(loc1.getY(), loc2.getY());
            double minZ = Math.min(loc1.getZ(), loc2.getZ());

            double maxX = Math.max(loc1.getX(), loc2.getX());
            double maxY = Math.max(loc1.getY(), loc2.getY());
            double maxZ = Math.max(loc1.getZ(), loc2.getZ());

            Location temp = new Location(loc1.getWorld(), randomDouble(minX, maxX), randomDouble(minY, maxY), randomDouble(minZ, maxZ));
            if(temp.getBlock().getBlockData().getMaterial().isAir()){
                return temp;
            }
        }


    }

    private double randomDouble(double min, double max) {
        return min + ThreadLocalRandom.current().nextDouble(Math.abs(max - min + 1));
    }

    public void spawnEntity(){
        int iteration = 0;
        while(iteration < spawnSpeed){
            if(entityList.size() >= maxEntityCount){
                break;
            }
            Entity entity = Objects.requireNonNull(loc1.getWorld()).spawnEntity(getRandomLocation(), et[(int) (Math.random() * (et.length))]);
            entityList.add(entity);
            iteration++;
        }
    }

    @EventHandler
    private void onEntityDeath(EntityDeathEvent e){
        entityList.remove(e.getEntity());
    }
}

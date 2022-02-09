package com.mcpkdot.spawnarea;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static org.bukkit.Bukkit.getWorld;
import static org.bukkit.Bukkit.getWorlds;

public class TextHandler {
    public static EntityType[] getEntityList(String str){
        List<EntityType> entityList = new ArrayList<>();
        String[] strlist = str.split(",");
        for (String s : strlist) {
            entityList.add(getEntityByName(s));
        }
        return entityList.toArray(new EntityType[0]);
    }

    private static EntityType getEntityByName(String name) {
        for (EntityType type : EntityType.values()) {
            if(type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }


    public static Area getAreaFromText(String text) {
        String[] textArray = text.split(" ");
        return new Area(getLocationFromString(textArray[0]), getLocationFromString(textArray[1]), getEntityList(textArray[2]), parseInt(textArray[3]), parseInt(textArray[4]));
    }

    public static String getStringFromArea(Area area) {
        StringBuilder text = new StringBuilder();
        text
                .append(getStringFromLocation(area.getLoc1()))
                .append(" ")
                .append(getStringFromLocation(area.getLoc2()))
                .append(" ");
        EntityType[] et = area.getEt();
        for (int i = 0, etLength = et.length; i < etLength; i++) {
            EntityType type = et[i];
            text.append(type);
            if(i < etLength-1) {
                text.append(",");
            }
        }
        text
                .append(" ")
                .append(area.getSpawnSpeed())
                .append(" ")
                .append(area.getMaxEntityCount());

        return text.toString();
    }

    public static String getStringFromLocation(Location loc){
        return(Objects.requireNonNull(loc.getWorld()).getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ());
    } //TODO: bug somehow

    public static Location getLocationFromString(String str){
        String[] textArray = str.split(",");
        return new Location(getWorld(textArray[0]), parseDouble(textArray[1]), parseDouble(textArray[2]), parseDouble(textArray[3]));
    }
}



















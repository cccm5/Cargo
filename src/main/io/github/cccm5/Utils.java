package io.github.cccm5;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.World;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.utils.MovecraftLocation;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
public class Utils
{
    private static final Material[] INVENTORY_MATERIALS = new Material[]{Material.CHEST,Material.TRAPPED_CHEST, Material.FURNACE, Material.HOPPER,Material.DROPPER,Material.DISPENSER, Material.BREWING_STAND};

    /**
     * Converts a movecraftLocation Object to a bukkit Location Object
     * 
     * @param movecraftLoc the movecraft location to be converted
     * @param world the world of the location
     * @return the converted location
     */
    public static Location movecraftLocationToBukkitLocation(MovecraftLocation movecraftLoc, World world){
        return new Location(world,movecraftLoc.getX(),movecraftLoc.getY(),movecraftLoc.getZ());
    }

    /**
     * Converts a list of movecraftLocation Object to a bukkit Location Object
     * 
     * @param movecraftLocations the movecraftLocations to be converted
     * @param world the world of the location
     * @return the converted location
     */
    public static ArrayList<Location> movecraftLocationToBukkitLocation(List<MovecraftLocation> movecraftLocations, World world){
        ArrayList<Location> locations = new ArrayList<Location>();
        for(MovecraftLocation movecraftLoc : movecraftLocations){
            locations.add(movecraftLocationToBukkitLocation(movecraftLoc,world));
        }
        return locations;
    }

    /**
     * Converts a list of movecraftLocation Object to a bukkit Location Object
     * 
     * @param movecraftLocations the movecraftLocations to be converted
     * @param world the world of the location
     * @return the converted location
     */
    public static ArrayList<Location> movecraftLocationToBukkitLocation(MovecraftLocation[] movecraftLocations, World world){
        ArrayList<Location> locations = new ArrayList<Location>();
        for(MovecraftLocation movecraftLoc : movecraftLocations){
            locations.add(movecraftLocationToBukkitLocation(movecraftLoc,world));
        }
        return locations;
    }

    public static ArrayList<NPC> getNPCsWithTrait(Class<? extends Trait> c){
        ArrayList<NPC> npcs = new ArrayList<NPC>();
        for(NPCRegistry registry : net.citizensnpcs.api.CitizensAPI.getNPCRegistries())
            for(NPC npc : registry)
                if(npc.hasTrait(c))
                    npcs.add(npc);
        return npcs;
    }

    /**
     * Gets the first inventory of a lookup material type on a craft holding a specific item, returns null if none found
     * an input of null for item searches withought checking inventory contents
     * an input of an ItemStack with type set to Material.AIR for searches for empty space in an inventory
     * 
     * @param craft the craft to scan
     * @param item the item to look for during the scan
     * @param lookup the materials to compare against while scaning
     * @return the first inventory matching a lookup material on the craft
     */
    public static Inventory firstInventory(Craft craft, ItemStack item, Material... lookup){
        boolean test=false;
        for(Material m : lookup){
            for(Material compare : INVENTORY_MATERIALS)
                if(compare == m){
                    test=true;
                    continue;
                }
            if(!test)
                throw new IllegalArgumentException(m + " is not an inventory type");
        }
        if(craft == null)
            throw new IllegalArgumentException("craft must not be null");

        for(Location loc : movecraftLocationToBukkitLocation(craft.getBlockList(),craft.getW()))
            for(Material m : lookup)
                if(loc.getBlock().getType() == m)
                {
                    Inventory inv = ((InventoryHolder)loc.getBlock().getState()).getInventory();
                    if(item==null)
                        return inv;
                    for(ItemStack i : inv)
                        if((item.getType()==Material.AIR  && (i==null || i.getType()==Material.AIR)) || (i!=null && i.isSimilar(item)))
                            return inv;
                }
        return null;
    }

    /**
     * Gets the first inventory of a lookup material type on a craft holding a specific item, returns null if none found
     * an input of null for item searches withought checking inventory contents
     * 
     * @param craft the craft to scan
     * @param item the item to look for during the scan
     * @param lookup the materials to compare against while scaning
     * @return the first inventory matching a lookup material on the craft
     */
    public static Inventory firstInventoryWithSpace(Craft craft, ItemStack item, Material... lookup){
        boolean test=false;
        for(Material m : lookup){
            for(Material compare : INVENTORY_MATERIALS)
                if(compare == m){
                    test=true;
                    continue;
                }
            if(!test)
                throw new IllegalArgumentException(m + " is not an inventory type");
        }
        if(craft == null)
            throw new IllegalArgumentException("craft must not be null");
        if(item.getType() == Material.AIR)
            throw new IllegalArgumentException("item must not have type Material.AIR");

        for(Location loc : movecraftLocationToBukkitLocation(craft.getBlockList(),craft.getW()))
            for(Material m : lookup)
                if(loc.getBlock().getType() == m)
                {
                    Inventory inv = ((InventoryHolder)loc.getBlock().getState()).getInventory();
                    if(item==null)
                        return inv;
                    for(ItemStack i : inv)
                        if(i==null || i.getType() == Material.AIR || (i.isSimilar(item) && i.getAmount() < item.getMaxStackSize() ))
                            return inv;
                }
        return null;
    }

}

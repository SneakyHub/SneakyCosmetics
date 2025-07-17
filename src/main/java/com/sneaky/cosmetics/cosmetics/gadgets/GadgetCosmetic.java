package com.sneaky.cosmetics.cosmetics.gadgets;

import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gadget cosmetic that gives players special interactive items
 */
public class GadgetCosmetic extends Cosmetic {
    
    private final Material gadgetMaterial;
    private final String gadgetName;
    private final List<String> gadgetLore;
    private final GadgetType gadgetType;
    
    private static final Map<Player, ItemStack> activeGadgets = new ConcurrentHashMap<>();
    
    public enum GadgetType {
        FIREWORK_LAUNCHER,
        SNOWBALL_CANNON,
        GRAPPLING_HOOK,
        TELEPORT_STICK,
        PARTY_POPPER,
        PAINT_BRUSH,
        WIND_CANNON,
        GRAVITY_GUN,
        LIGHTNING_WAND,
        FREEZE_RAY,
        JUMP_BOOTS,
        PORTAL_GUN
    }
    
    public GadgetCosmetic(String id, String displayName, int price, Material iconMaterial,
                         List<String> description, String permission, boolean requiresVIP,
                         boolean requiresPremium, Material gadgetMaterial, String gadgetName,
                         List<String> gadgetLore, GadgetType gadgetType) {
        super(id, displayName, CosmeticType.GADGET, price, iconMaterial, description, 
              permission, requiresVIP, requiresPremium);
        this.gadgetMaterial = gadgetMaterial;
        this.gadgetName = gadgetName;
        this.gadgetLore = gadgetLore;
        this.gadgetType = gadgetType;
    }
    
    @Override
    public void activate(Player player) {
        // Remove any existing gadget
        deactivate(player);
        
        // Create the gadget item
        ItemStack gadget = new ItemStack(gadgetMaterial);
        ItemMeta meta = gadget.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e" + gadgetName);
            meta.setLore(gadgetLore);
            gadget.setItemMeta(meta);
        }
        
        // Give to player
        player.getInventory().addItem(gadget);
        activeGadgets.put(player, gadget);
        
        player.sendMessage("§a✓ Activated gadget: " + getDisplayName());
    }
    
    @Override
    public void deactivate(Player player) {
        ItemStack existingGadget = activeGadgets.remove(player);
        if (existingGadget != null) {
            // Remove gadget from inventory
            player.getInventory().remove(existingGadget);
        }
        
        player.sendMessage("§7⊘ Deactivated gadget: " + getDisplayName());
    }
    
    @Override
    public boolean isActive(Player player) {
        return activeGadgets.containsKey(player);
    }
    
    @Override
    public void cleanup(Player player) {
        deactivate(player);
    }
    
    /**
     * Get the gadget type
     */
    public GadgetType getGadgetType() {
        return gadgetType;
    }
    
    /**
     * Get the gadget material
     */
    public Material getGadgetMaterial() {
        return gadgetMaterial;
    }
    
    /**
     * Get the gadget name
     */
    public String getGadgetName() {
        return gadgetName;
    }
    
    /**
     * Check if an item is a gadget
     */
    public static boolean isGadget(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName() && 
               meta.getDisplayName().startsWith("§e");
    }
    
    /**
     * Get active gadget for player
     */
    public static ItemStack getActiveGadget(Player player) {
        return activeGadgets.get(player);
    }
    
    /**
     * Clean up all gadgets (for plugin disable)
     */
    public static void cleanupAllGadgets() {
        for (Map.Entry<Player, ItemStack> entry : activeGadgets.entrySet()) {
            Player player = entry.getKey();
            ItemStack gadget = entry.getValue();
            if (player.isOnline()) {
                player.getInventory().remove(gadget);
            }
        }
        activeGadgets.clear();
    }
}
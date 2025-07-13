package com.sneaky.cosmetics.cosmetics.hats;

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
 * Hat cosmetic that places an item on the player's head
 */
public class HatCosmetic extends Cosmetic {
    
    private final Material hatMaterial;
    private final String hatDisplayName;
    private final List<String> hatLore;
    
    private static final Map<Player, ItemStack> originalHelmets = new ConcurrentHashMap<>();
    
    public HatCosmetic(String id, String displayName, int price, Material iconMaterial,
                      List<String> description, String permission, boolean requiresVIP,
                      boolean requiresPremium, Material hatMaterial, String hatDisplayName,
                      List<String> hatLore) {
        super(id, displayName, CosmeticType.HAT, price, iconMaterial, description, 
              permission, requiresVIP, requiresPremium);
        this.hatMaterial = hatMaterial;
        this.hatDisplayName = hatDisplayName;
        this.hatLore = hatLore;
    }
    
    @Override
    public void activate(Player player) {
        // Store the original helmet
        ItemStack originalHelmet = player.getInventory().getHelmet();
        if (originalHelmet != null) {
            originalHelmets.put(player, originalHelmet.clone());
        }
        
        // Create the hat item
        ItemStack hat = new ItemStack(hatMaterial);
        ItemMeta meta = hat.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6" + hatDisplayName);
            if (hatLore != null && !hatLore.isEmpty()) {
                meta.setLore(hatLore);
            }
            hat.setItemMeta(meta);
        }
        
        // Set the hat
        player.getInventory().setHelmet(hat);
    }
    
    @Override
    public void deactivate(Player player) {
        // Restore the original helmet
        ItemStack originalHelmet = originalHelmets.remove(player);
        player.getInventory().setHelmet(originalHelmet);
    }
    
    @Override
    public boolean isActive(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet == null || helmet.getType() != hatMaterial) {
            return false;
        }
        
        ItemMeta meta = helmet.getItemMeta();
        return meta != null && meta.hasDisplayName() && 
               meta.getDisplayName().equals("§6" + hatDisplayName);
    }
    
    @Override
    public void cleanup(Player player) {
        deactivate(player);
    }
}
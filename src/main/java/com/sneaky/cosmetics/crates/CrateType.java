package com.sneaky.cosmetics.crates;

import org.bukkit.Material;

/**
 * Enum representing different types of crates with their properties
 */
public enum CrateType {
    
    COMMON("Common Crate", "&#8C8C8C", Material.CHEST, 
           "A basic crate with common rewards", 50, 1.0),
    
    RARE("Rare Crate", "&#4169E1", Material.ENDER_CHEST, 
         "A special crate with rare cosmetics", 150, 0.3),
    
    EPIC("Epic Crate", "&#9932CC", Material.SHULKER_BOX, 
         "An epic crate with premium rewards", 300, 0.15),
    
    LEGENDARY("Legendary Crate", "&#FF8C00", Material.TRAPPED_CHEST, 
              "A legendary crate with exclusive cosmetics", 500, 0.05),
    
    MYTHIC("Mythic Crate", "&#FF1493", Material.DRAGON_EGG, 
           "The ultimate crate with mythical rewards", 1000, 0.01),
    
    SEASONAL("Seasonal Crate", "&#32CD32", Material.DECORATED_POT, 
             "Limited time seasonal rewards", 200, 0.2),
    
    EVENT("Event Crate", "&#FFD700", Material.BEACON, 
          "Special event exclusive crate", 0, 0.0); // Cannot be purchased
    
    private final String displayName;
    private final String colorCode;
    private final Material icon;
    private final String description;
    private final int price; // Price in credits to buy this crate
    private final double dropRate; // Base drop rate for this crate type
    
    CrateType(String displayName, String colorCode, Material icon, 
              String description, int price, double dropRate) {
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.icon = icon;
        this.description = description;
        this.price = price;
        this.dropRate = dropRate;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getColorCode() {
        return colorCode;
    }
    
    public Material getIcon() {
        return icon;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getPrice() {
        return price;
    }
    
    public double getDropRate() {
        return dropRate;
    }
    
    public boolean isPurchasable() {
        return price > 0;
    }
    
    /**
     * Get the formatted display name with color
     */
    public String getFormattedName() {
        return colorCode + displayName;
    }
    
    /**
     * Get rarity level (1-7, higher is rarer)
     */
    public int getRarityLevel() {
        return ordinal() + 1;
    }
    
    /**
     * Get crate type by name (case insensitive)
     */
    public static CrateType fromString(String name) {
        for (CrateType type : values()) {
            if (type.name().equalsIgnoreCase(name) || 
                type.displayName.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * Get a random crate type based on drop rates
     */
    public static CrateType getRandomCrateType() {
        double random = Math.random();
        double cumulative = 0.0;
        
        // Sort by rarity (most common first)
        CrateType[] types = {COMMON, RARE, SEASONAL, EPIC, LEGENDARY, MYTHIC};
        
        for (CrateType type : types) {
            cumulative += type.dropRate;
            if (random <= cumulative) {
                return type;
            }
        }
        
        return COMMON; // Fallback
    }
}
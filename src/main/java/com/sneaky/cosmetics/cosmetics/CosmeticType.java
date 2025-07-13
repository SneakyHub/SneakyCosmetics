package com.sneaky.cosmetics.cosmetics;

import org.bukkit.Material;

/**
 * Enumeration of all cosmetic types available in SneakyCosmetics
 * Each type represents a different category of cosmetic items
 */
public enum CosmeticType {
    PARTICLE("Particles", "&#9370DB", Material.BLAZE_POWDER, "Create beautiful particle effects around you"),
    HAT("Hats", "&#FFD700", Material.DIAMOND_HELMET, "Wear unique items on your head"),
    PET("Pets", "&#32CD32", Material.BONE, "Summon loyal companions to follow you"),
    TRAIL("Trails", "&#1E90FF", Material.FEATHER, "Leave magical trails as you move"),
    GADGET("Gadgets", "&#FF6347", Material.STICK, "Fun interactive items and tools"),
    WINGS("Wings", "&#F0F8FF", Material.ELYTRA, "Spread your wings and glide in style"),
    AURA("Auras", "&#8A2BE2", Material.NETHER_STAR, "Surround yourself with mystical auras");
    
    private final String displayName;
    private final String colorCode;
    private final Material icon;
    private final String description;
    
    CosmeticType(String displayName, String colorCode, Material icon, String description) {
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.icon = icon;
        this.description = description;
    }
    
    /**
     * Get the display name of this cosmetic type
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Get the color code for this cosmetic type
     */
    public String getColorCode() {
        return colorCode;
    }
    
    /**
     * Get the colored display name
     */
    public String getColoredDisplayName() {
        return colorCode + displayName;
    }
    
    /**
     * Get the icon material for this cosmetic type
     */
    public Material getIcon() {
        return icon;
    }
    
    /**
     * Get the description of this cosmetic type
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get the permission node for this cosmetic type
     */
    public String getPermission() {
        return "sneakycosmetics." + name().toLowerCase();
    }
    
    /**
     * Get cosmetic type by name (case insensitive)
     */
    public static CosmeticType fromString(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Get all cosmetic type names
     */
    public static String[] getNames() {
        CosmeticType[] types = values();
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].name();
        }
        return names;
    }
    
    /**
     * Get all display names
     */
    public static String[] getDisplayNames() {
        CosmeticType[] types = values();
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].getDisplayName();
        }
        return names;
    }
}
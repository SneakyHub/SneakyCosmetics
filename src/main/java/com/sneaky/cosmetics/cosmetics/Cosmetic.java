package com.sneaky.cosmetics.cosmetics;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Abstract base class for all cosmetic items
 * Defines the common structure and behavior for cosmetics
 */
public abstract class Cosmetic {
    
    protected final String id;
    protected final String displayName;
    protected final CosmeticType type;
    protected final int price;
    protected final Material iconMaterial;
    protected final List<String> description;
    protected final String permission;
    protected final String uniquePermission; // Auto-generated unique permission per cosmetic
    protected final boolean requiresVIP;
    protected final boolean requiresPremium;
    
    public Cosmetic(String id, String displayName, CosmeticType type, int price, 
                   Material iconMaterial, List<String> description, String permission,
                   boolean requiresVIP, boolean requiresPremium) {
        this.id = id;
        this.displayName = displayName;
        this.type = type;
        this.price = price;
        this.iconMaterial = iconMaterial;
        this.description = description;
        this.permission = permission;
        this.uniquePermission = generateUniquePermission(type, id);
        this.requiresVIP = requiresVIP;
        this.requiresPremium = requiresPremium;
    }
    
    /**
     * Get the unique identifier for this cosmetic
     */
    public String getId() {
        return id;
    }
    
    /**
     * Get the display name for this cosmetic
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Get the type of this cosmetic
     */
    public CosmeticType getType() {
        return type;
    }
    
    /**
     * Get the price in credits for this cosmetic
     */
    public int getPrice() {
        return price;
    }
    
    /**
     * Get the icon material for GUI display
     */
    public Material getIconMaterial() {
        return iconMaterial;
    }
    
    /**
     * Get the description lines for this cosmetic
     */
    public List<String> getDescription() {
        return description;
    }
    
    /**
     * Get the permission required for this cosmetic (if any)
     */
    public String getPermission() {
        return permission;
    }
    
    /**
     * Get the unique permission for this specific cosmetic
     * Format: sneakycosmetics.cosmetic.<type>.<id>
     */
    public String getUniquePermission() {
        return uniquePermission;
    }
    
    /**
     * Generate a unique permission string for a cosmetic
     * Format: sneakycosmetics.cosmetic.<type>.<id>
     */
    private static String generateUniquePermission(CosmeticType type, String id) {
        String typeString = type.name().toLowerCase();
        String cleanId = id.toLowerCase().replaceAll("[^a-z0-9_]", "_");
        return "sneakycosmetics.cosmetic." + typeString + "." + cleanId;
    }
    
    /**
     * Check if this cosmetic requires VIP status
     */
    public boolean requiresVIP() {
        return requiresVIP;
    }
    
    /**
     * Check if this cosmetic requires Premium status
     */
    public boolean requiresPremium() {
        return requiresPremium;
    }
    
    /**
     * Check if this cosmetic is free (price is 0)
     */
    public boolean isFree() {
        return price == 0;
    }
    
    /**
     * Check if a player can access this cosmetic
     */
    public boolean canPlayerAccess(Player player) {
        // Check if player has free access to all cosmetics
        if (player.hasPermission("sneakycosmetics.free")) {
            return true;
        }
        
        // Check VIP requirement
        if (requiresVIP && !player.hasPermission("sneakycosmetics.vip")) {
            return false;
        }
        
        // Check Premium requirement
        if (requiresPremium && !player.hasPermission("sneakycosmetics.premium")) {
            return false;
        }
        
        // Check unique cosmetic permission (highest priority)
        if (player.hasPermission(uniquePermission)) {
            return true;
        }
        
        // Check legacy specific permission
        if (permission != null && !permission.isEmpty() && !player.hasPermission(permission)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Get the reason why a player cannot access this cosmetic
     */
    public String getAccessDeniedReason(Player player) {
        if (requiresPremium && !player.hasPermission("sneakycosmetics.premium")) {
            return "Requires Premium";
        }
        
        if (requiresVIP && !player.hasPermission("sneakycosmetics.vip")) {
            return "Requires VIP";
        }
        
        if (!player.hasPermission(uniquePermission)) {
            return "Missing Permission: " + uniquePermission;
        }
        
        if (permission != null && !permission.isEmpty() && !player.hasPermission(permission)) {
            return "Missing Permission: " + permission;
        }
        
        return "Unknown";
    }
    
    /**
     * Activate this cosmetic for a player
     * Must be implemented by subclasses
     */
    public abstract void activate(Player player);
    
    /**
     * Deactivate this cosmetic for a player
     * Must be implemented by subclasses
     */
    public abstract void deactivate(Player player);
    
    /**
     * Check if this cosmetic is currently active for a player
     * Default implementation - can be overridden by subclasses
     */
    public boolean isActive(Player player) {
        // Default implementation - subclasses should override if they have specific logic
        return false;
    }
    
    /**
     * Update this cosmetic's effects for a player
     * Called periodically for animated cosmetics
     * Default implementation does nothing - override in subclasses that need updates
     */
    public void update(Player player) {
        // Default: no update needed
    }
    
    /**
     * Clean up resources when this cosmetic is removed
     * Default implementation does nothing - override in subclasses that need cleanup
     */
    public void cleanup(Player player) {
        // Default: no cleanup needed
    }
    
    /**
     * Check if a player has the unique permission for this cosmetic
     */
    public boolean hasUniquePermission(Player player) {
        return player.hasPermission(uniquePermission);
    }
    
    /**
     * Check if a player can use this cosmetic (combines access check with ownership)
     */
    public boolean canPlayerUse(Player player) {
        // Players with free access can use any cosmetic
        if (player.hasPermission("sneakycosmetics.free")) {
            return true;
        }
        
        // Check if player has unique permission for this cosmetic
        if (hasUniquePermission(player)) {
            return true;
        }
        
        // Otherwise, they need to own it and meet access requirements
        return canPlayerAccess(player);
    }
    
    /**
     * Get all applicable permissions for this cosmetic
     */
    public List<String> getAllPermissions() {
        List<String> permissions = new java.util.ArrayList<>();
        permissions.add(uniquePermission);
        
        if (permission != null && !permission.isEmpty()) {
            permissions.add(permission);
        }
        
        if (requiresVIP) {
            permissions.add("sneakycosmetics.vip");
        }
        
        if (requiresPremium) {
            permissions.add("sneakycosmetics.premium");
        }
        
        permissions.add("sneakycosmetics.free"); // Ultimate bypass permission
        
        return permissions;
    }
    
    /**
     * Get formatted display information for this cosmetic
     */
    public String getFormattedInfo() {
        StringBuilder info = new StringBuilder();
        info.append("§6").append(displayName).append(" §7(").append(type.getDisplayName()).append(")\n");
        info.append("§7Price: ").append(price == 0 ? "§aFree" : "§e" + price + " credits").append("\n");
        info.append("§7Permission: §e").append(uniquePermission).append("\n");
        
        if (requiresPremium) {
            info.append("§5Premium Required\n");
        } else if (requiresVIP) {
            info.append("§6VIP Required\n");
        }
        
        if (description != null && !description.isEmpty()) {
            for (String line : description) {
                info.append("§7").append(line).append("\n");
            }
        }
        
        return info.toString().trim();
    }
    
    @Override
    public String toString() {
        return "Cosmetic{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", type=" + type +
                ", price=" + price +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cosmetic cosmetic = (Cosmetic) obj;
        return id.equals(cosmetic.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
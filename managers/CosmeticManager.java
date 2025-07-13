package com.sneaky.cosmetics.managers;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central manager for all cosmetic types and operations
 * Coordinates between different cosmetic managers
 */
public class CosmeticManager {
    
    private final SneakyCosmetics plugin;
    private final Map<String, Cosmetic> cosmetics = new ConcurrentHashMap<>();
    private final Map<CosmeticType, List<Cosmetic>> cosmeticsByType = new ConcurrentHashMap<>();
    private final Map<UUID, Set<String>> activeCosmetics = new ConcurrentHashMap<>();
    
    public CosmeticManager(SneakyCosmetics plugin) {
        this.plugin = plugin;
        initializeCosmeticTypes();
    }
    
    private void initializeCosmeticTypes() {
        for (CosmeticType type : CosmeticType.values()) {
            cosmeticsByType.put(type, new ArrayList<>());
        }
    }
    
    /**
     * Register all cosmetics from all managers
     */
    public void registerCosmetics() {
        plugin.getLogger().info("Registering cosmetics...");
        
        // TODO: Register cosmetics from each manager
        // For now, just log that the system is ready
        plugin.getLogger().info("Cosmetic registration complete. Ready to load cosmetics!");
    }
    
    /**
     * Register a cosmetic
     */
    public void registerCosmetic(Cosmetic cosmetic) {
        cosmetics.put(cosmetic.getId(), cosmetic);
        cosmeticsByType.get(cosmetic.getType()).add(cosmetic);
        
        plugin.getLogger().info("Registered cosmetic: " + cosmetic.getId() + " (" + cosmetic.getType() + ")");
    }
    
    /**
     * Get a cosmetic by ID
     */
    public Cosmetic getCosmetic(String id) {
        return cosmetics.get(id);
    }
    
    /**
     * Get all cosmetics of a specific type
     */
    public List<Cosmetic> getCosmeticsByType(CosmeticType type) {
        return new ArrayList<>(cosmeticsByType.getOrDefault(type, new ArrayList<>()));
    }
    
    /**
     * Get all cosmetic types
     */
    public Set<CosmeticType> getCosmeticTypes() {
        return cosmeticsByType.keySet();
    }
    
    /**
     * Get total number of cosmetics
     */
    public int getTotalCosmetics() {
        return cosmetics.size();
    }
    
    /**
     * Get cosmetics by type count
     */
    public int getCosmeticCountByType(CosmeticType type) {
        return cosmeticsByType.getOrDefault(type, new ArrayList<>()).size();
    }
    
    /**
     * Check if a player owns a cosmetic
     */
    public boolean hasCosmetic(Player player, String cosmeticId) {
        // Check database
        try {
            return plugin.getDatabaseManager().hasCosmetic(player.getUniqueId(), cosmeticId).get();
        } catch (Exception e) {
            plugin.getLogger().warning("Error checking cosmetic ownership: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Give a cosmetic to a player
     */
    public void giveCosmetic(Player player, String cosmeticId) {
        plugin.getDatabaseManager().giveCosmetic(player.getUniqueId(), cosmeticId);
    }
    
    /**
     * Activate a cosmetic for a player
     */
    public boolean activateCosmetic(Player player, String cosmeticId) {
        Cosmetic cosmetic = getCosmetic(cosmeticId);
        if (cosmetic == null) {
            return false;
        }
        
        // Check if player owns the cosmetic
        if (!hasCosmetic(player, cosmeticId)) {
            return false;
        }
        
        // Check if player already has a cosmetic of this type active
        UUID uuid = player.getUniqueId();
        Set<String> playerCosmetics = activeCosmetics.getOrDefault(uuid, new HashSet<>());
        
        // Remove any existing cosmetic of the same type
        for (String activeId : new HashSet<>(playerCosmetics)) {
            Cosmetic activeCosmetic = getCosmetic(activeId);
            if (activeCosmetic != null && activeCosmetic.getType() == cosmetic.getType()) {
                deactivateCosmetic(player, activeId);
            }
        }
        
        // Activate the new cosmetic
        try {
            cosmetic.activate(player);
            playerCosmetics.add(cosmeticId);
            activeCosmetics.put(uuid, playerCosmetics);
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Error activating cosmetic " + cosmeticId + " for " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deactivate a cosmetic for a player
     */
    public boolean deactivateCosmetic(Player player, String cosmeticId) {
        Cosmetic cosmetic = getCosmetic(cosmeticId);
        if (cosmetic == null) {
            return false;
        }
        
        UUID uuid = player.getUniqueId();
        Set<String> playerCosmetics = activeCosmetics.getOrDefault(uuid, new HashSet<>());
        
        if (!playerCosmetics.contains(cosmeticId)) {
            return false; // Not active
        }
        
        try {
            cosmetic.deactivate(player);
            playerCosmetics.remove(cosmeticId);
            if (playerCosmetics.isEmpty()) {
                activeCosmetics.remove(uuid);
            } else {
                activeCosmetics.put(uuid, playerCosmetics);
            }
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Error deactivating cosmetic " + cosmeticId + " for " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Toggle a cosmetic for a player
     */
    public boolean toggleCosmetic(Player player, String cosmeticId) {
        UUID uuid = player.getUniqueId();
        Set<String> playerCosmetics = activeCosmetics.getOrDefault(uuid, new HashSet<>());
        
        if (playerCosmetics.contains(cosmeticId)) {
            return deactivateCosmetic(player, cosmeticId);
        } else {
            return activateCosmetic(player, cosmeticId);
        }
    }
    
    /**
     * Get all active cosmetics for a player
     */
    public Set<String> getActiveCosmetics(Player player) {
        return new HashSet<>(activeCosmetics.getOrDefault(player.getUniqueId(), new HashSet<>()));
    }
    
    /**
     * Clear all active cosmetics for a player
     */
    public void clearAllCosmetics(Player player) {
        UUID uuid = player.getUniqueId();
        Set<String> playerCosmetics = activeCosmetics.getOrDefault(uuid, new HashSet<>());
        
        for (String cosmeticId : new HashSet<>(playerCosmetics)) {
            deactivateCosmetic(player, cosmeticId);
        }
    }
    
    /**
     * Check if a cosmetic is active for a player
     */
    public boolean isCosmeticActive(Player player, String cosmeticId) {
        return activeCosmetics.getOrDefault(player.getUniqueId(), new HashSet<>()).contains(cosmeticId);
    }
    
    /**
     * Cleanup inactive cosmetic entities (performance task)
     */
    public void cleanupInactiveCosmetics() {
        // TODO: Implement cleanup logic for cosmetic entities
        plugin.getLogger().info("Running cosmetic cleanup task...");
    }
    
    /**
     * Reload cosmetic system
     */
    public void reload() {
        plugin.getLogger().info("Reloading cosmetic system...");
        
        // Clear and re-register cosmetics
        cosmetics.clear();
        for (List<Cosmetic> typeList : cosmeticsByType.values()) {
            typeList.clear();
        }
        
        registerCosmetics();
    }
    
    /**
     * Get all cosmetics
     */
    public Collection<Cosmetic> getAllCosmetics() {
        return cosmetics.values();
    }
    
    /**
     * Get cosmetics that a player can access (owned + free)
     */
    public List<Cosmetic> getAccessibleCosmetics(Player player, CosmeticType type) {
        List<Cosmetic> accessible = new ArrayList<>();
        List<Cosmetic> typeCosmetics = getCosmeticsByType(type);
        
        for (Cosmetic cosmetic : typeCosmetics) {
            // Check if player owns it or if it's free
            if (cosmetic.getPrice() == 0 || hasCosmetic(player, cosmetic.getId())) {
                accessible.add(cosmetic);
            }
        }
        
        return accessible;
    }
}
package com.sneaky.cosmetics.managers;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic;
import com.sneaky.cosmetics.cosmetics.morphs.MorphManager;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages cosmetic activation, deactivation, and player state
 */
public class CosmeticActivationManager {
    
    private final SneakyCosmetics plugin;
    private final CosmeticRegistry registry;
    private final Map<UUID, Set<String>> activeCosmetics = new ConcurrentHashMap<>();
    private MorphManager morphManager;
    
    public CosmeticActivationManager(SneakyCosmetics plugin, CosmeticRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
        this.morphManager = new MorphManager(plugin);
    }
    
    /**
     * Get the morph manager instance
     */
    public MorphManager getMorphManager() {
        return morphManager;
    }
    
    /**
     * Activate a cosmetic for a player
     */
    public boolean activateCosmetic(Player player, String cosmeticId) {
        Cosmetic cosmetic = registry.getCosmetic(cosmeticId);
        if (cosmetic == null) {
            plugin.getLogger().warning("Attempted to activate unknown cosmetic: " + cosmeticId);
            return false;
        }
        
        // Check if player has permission to use this cosmetic
        if (!hasCosmetic(player, cosmeticId)) {
            plugin.getMessageManager().sendError(player, "You don't own this cosmetic!");
            return false;
        }
        
        // Check if player can access this cosmetic
        if (!cosmetic.canPlayerAccess(player)) {
            plugin.getMessageManager().sendError(player, "You cannot access this cosmetic: " + cosmetic.getAccessDeniedReason(player));
            return false;
        }
        
        // Deactivate other cosmetics of the same type first
        deactivateAllCosmetics(player, cosmetic.getType());
        
        // Activate the cosmetic
        try {
            // Special handling for morph cosmetics
            if (cosmetic instanceof MorphCosmetic) {
                MorphCosmetic morphCosmetic = (MorphCosmetic) cosmetic;
                morphManager.setActiveMorph(player, morphCosmetic);
            } else {
                cosmetic.activate(player);
            }
            
            activeCosmetics.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(cosmeticId);
            
            // Record statistics
            if (plugin.getStatisticsManager() != null) {
                plugin.getStatisticsManager().recordCosmeticActivation(player, cosmeticId);
            }
            
            plugin.getLogger().fine("Activated cosmetic " + cosmeticId + " for " + player.getName());
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to activate cosmetic " + cosmeticId + " for " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deactivate a specific cosmetic for a player
     */
    public boolean deactivateCosmetic(Player player, String cosmeticId) {
        Cosmetic cosmetic = registry.getCosmetic(cosmeticId);
        if (cosmetic == null) {
            return false;
        }
        
        Set<String> playerCosmetics = activeCosmetics.get(player.getUniqueId());
        if (playerCosmetics == null || !playerCosmetics.contains(cosmeticId)) {
            return false; // Not active
        }
        
        try {
            // Special handling for morph cosmetics
            if (cosmetic instanceof MorphCosmetic) {
                morphManager.removeMorph(player);
            } else {
                cosmetic.deactivate(player);
            }
            
            playerCosmetics.remove(cosmeticId);
            
            if (playerCosmetics.isEmpty()) {
                activeCosmetics.remove(player.getUniqueId());
            }
            
            plugin.getLogger().fine("Deactivated cosmetic " + cosmeticId + " for " + player.getName());
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to deactivate cosmetic " + cosmeticId + " for " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Toggle a cosmetic on/off
     */
    public boolean toggleCosmetic(Player player, String cosmeticId) {
        if (isCosmeticActive(player, cosmeticId)) {
            return deactivateCosmetic(player, cosmeticId);
        } else {
            return activateCosmetic(player, cosmeticId);
        }
    }
    
    /**
     * Deactivate all cosmetics of a specific type for a player
     */
    public void deactivateAllCosmetics(Player player, CosmeticType type) {
        Set<String> playerCosmetics = activeCosmetics.get(player.getUniqueId());
        if (playerCosmetics == null) return;
        
        Iterator<String> iterator = playerCosmetics.iterator();
        while (iterator.hasNext()) {
            String cosmeticId = iterator.next();
            Cosmetic cosmetic = registry.getCosmetic(cosmeticId);
            
            if (cosmetic != null && cosmetic.getType() == type) {
                try {
                    // Special handling for morph cosmetics
                    if (cosmetic instanceof MorphCosmetic) {
                        morphManager.removeMorph(player);
                    } else {
                        cosmetic.deactivate(player);
                    }
                    iterator.remove();
                    plugin.getLogger().fine("Deactivated " + type.name() + " cosmetic " + cosmeticId + " for " + player.getName());
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to deactivate cosmetic " + cosmeticId + " for " + player.getName());
                }
            }
        }
        
        if (playerCosmetics.isEmpty()) {
            activeCosmetics.remove(player.getUniqueId());
        }
    }
    
    /**
     * Clear all active cosmetics for a player
     */
    public void clearAllCosmetics(Player player) {
        Set<String> playerCosmetics = activeCosmetics.remove(player.getUniqueId());
        if (playerCosmetics == null) return;
        
        for (String cosmeticId : playerCosmetics) {
            Cosmetic cosmetic = registry.getCosmetic(cosmeticId);
            if (cosmetic != null) {
                try {
                    // Special handling for morph cosmetics
                    if (cosmetic instanceof MorphCosmetic) {
                        morphManager.removeMorph(player);
                    } else {
                        cosmetic.deactivate(player);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to deactivate cosmetic " + cosmeticId + " during clear all");
                }
            }
        }
    }
    
    /**
     * Check if a cosmetic is currently active for a player
     */
    public boolean isCosmeticActive(Player player, String cosmeticId) {
        return activeCosmetics.getOrDefault(player.getUniqueId(), new HashSet<>()).contains(cosmeticId);
    }
    
    /**
     * Get all active cosmetics for a player
     */
    public Set<String> getActiveCosmetics(Player player) {
        return new HashSet<>(activeCosmetics.getOrDefault(player.getUniqueId(), new HashSet<>()));
    }
    
    /**
     * Check if player has access to a cosmetic
     */
    public boolean hasCosmetic(Player player, String cosmeticId) {
        Cosmetic cosmetic = registry.getCosmetic(cosmeticId);
        if (cosmetic == null) return false;
        
        // Free cosmetics are available to everyone
        if (cosmetic.isFree()) return true;
        
        // Check if player has free access permission
        if (player.hasPermission("sneakycosmetics.free")) return true;
        
        // Check database ownership
        try {
            return plugin.getDatabaseManager().hasCosmetic(player.getUniqueId(), cosmeticId).get();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to check cosmetic ownership for " + player.getName());
            return false;
        }
    }
    
    /**
     * Get cosmetics accessible to a player for a specific type
     */
    public List<Cosmetic> getAccessibleCosmetics(Player player, CosmeticType type) {
        List<Cosmetic> accessible = new ArrayList<>();
        List<Cosmetic> typeCosmetics = registry.getCosmeticsByType(type);
        
        for (Cosmetic cosmetic : typeCosmetics) {
            if (cosmetic.canPlayerAccess(player) && hasCosmetic(player, cosmetic.getId())) {
                accessible.add(cosmetic);
            }
        }
        
        return accessible;
    }
    
    /**
     * Clean up cosmetics for offline players
     */
    public int cleanupOfflinePlayerCosmetics() {
        int cleanedUp = 0;
        
        for (Iterator<Map.Entry<UUID, Set<String>>> iterator = activeCosmetics.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<UUID, Set<String>> entry = iterator.next();
            UUID playerId = entry.getKey();
            Player player = plugin.getServer().getPlayer(playerId);
            
            if (player == null || !player.isOnline()) {
                // Player is offline, clean up their cosmetics
                Set<String> playerCosmetics = entry.getValue();
                for (String cosmeticId : playerCosmetics) {
                    Cosmetic cosmetic = registry.getCosmetic(cosmeticId);
                    if (cosmetic != null) {
                        try {
                            cosmetic.cleanup(plugin.getServer().getOfflinePlayer(playerId).getPlayer());
                            cleanedUp++;
                        } catch (Exception e) {
                            plugin.getLogger().warning("Failed to cleanup cosmetic " + cosmeticId + " for offline player " + playerId);
                        }
                    }
                }
                iterator.remove(); // Remove offline player from active cosmetics
                plugin.getLogger().fine("Cleaned up " + playerCosmetics.size() + " cosmetics for offline player " + playerId);
            }
        }
        
        return cleanedUp;
    }
    
    /**
     * Get the internal active cosmetics map (for database saving)
     */
    public Map<UUID, Set<String>> getActiveCosmetsMap() {
        return new HashMap<>(activeCosmetics);
    }
}
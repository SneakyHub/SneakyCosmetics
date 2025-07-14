package com.sneaky.cosmetics.managers;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Main cosmetic manager that coordinates between specialized managers
 * This is a facade that delegates to specialized managers
 */
public class CosmeticManager {
    
    private final SneakyCosmetics plugin;
    private final CosmeticRegistry registry;
    private final CosmeticActivationManager activationManager;
    private final CosmeticCleanupManager cleanupManager;
    
    public CosmeticManager(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.registry = new CosmeticRegistry(plugin);
        this.activationManager = new CosmeticActivationManager(plugin, registry);
        this.cleanupManager = new CosmeticCleanupManager(plugin, activationManager);
    }
    
    /**
     * Initialize the cosmetic system
     */
    public void initialize() {
        plugin.getLogger().info("Initializing cosmetic system...");
        
        // Register all cosmetics
        registry.registerAllCosmetics();
        
        // Start cleanup tasks
        cleanupManager.startCleanupTasks();
        
        plugin.getLogger().info("Cosmetic system initialized with " + getTotalCosmetics() + " cosmetics");
    }
    
    /**
     * Shutdown the cosmetic system
     */
    public void shutdown() {
        plugin.getLogger().info("Shutting down cosmetic system...");
        cleanupManager.forceCleanupAll();
    }
    
    // Delegation methods to CosmeticRegistry
    public void registerCosmetic(Cosmetic cosmetic) {
        registry.registerCosmetic(cosmetic);
    }
    
    public Cosmetic getCosmetic(String id) {
        return registry.getCosmetic(id);
    }
    
    public List<Cosmetic> getCosmeticsByType(CosmeticType type) {
        return registry.getCosmeticsByType(type);
    }
    
    public Collection<Cosmetic> getAllCosmetics() {
        return registry.getAllCosmetics();
    }
    
    public Set<CosmeticType> getCosmeticTypes() {
        return registry.getCosmeticTypes();
    }
    
    public int getTotalCosmetics() {
        return registry.getTotalCosmetics();
    }
    
    public int getCosmeticCountByType(CosmeticType type) {
        return registry.getCosmeticCountByType(type);
    }
    
    // Delegation methods to CosmeticActivationManager
    public boolean hasCosmetic(Player player, String cosmeticId) {
        return activationManager.hasCosmetic(player, cosmeticId);
    }
    
    public void giveCosmetic(Player player, String cosmeticId) {
        // Give the cosmetic in the database
        plugin.getDatabaseManager().giveCosmetic(player.getUniqueId(), cosmeticId);
        plugin.getMessageManager().sendSuccess(player, "You received: " + getCosmetic(cosmeticId).getDisplayName());
    }
    
    public boolean activateCosmetic(Player player, String cosmeticId) {
        return activationManager.activateCosmetic(player, cosmeticId);
    }
    
    public boolean deactivateCosmetic(Player player, String cosmeticId) {
        return activationManager.deactivateCosmetic(player, cosmeticId);
    }
    
    public boolean toggleCosmetic(Player player, String cosmeticId) {
        return activationManager.toggleCosmetic(player, cosmeticId);
    }
    
    public void deactivateAllCosmetics(Player player, CosmeticType type) {
        activationManager.deactivateAllCosmetics(player, type);
    }
    
    public void clearAllCosmetics(Player player) {
        activationManager.clearAllCosmetics(player);
    }
    
    public boolean isCosmeticActive(Player player, String cosmeticId) {
        return activationManager.isCosmeticActive(player, cosmeticId);
    }
    
    public Set<String> getActiveCosmetics(Player player) {
        return activationManager.getActiveCosmetics(player);
    }
    
    public List<Cosmetic> getAccessibleCosmetics(Player player, CosmeticType type) {
        return activationManager.getAccessibleCosmetics(player, type);
    }
    
    // Delegation methods to CosmeticCleanupManager
    public void cleanupInactiveCosmetics() {
        cleanupManager.cleanupInactiveCosmetics();
    }
    
    /**
     * Reload the cosmetic system
     */
    public void reload() {
        plugin.getLogger().info("Reloading cosmetic system...");
        
        // Clear current cosmetics
        cleanupManager.forceCleanupAll();
        registry.clear();
        
        // Re-register cosmetics
        registry.registerAllCosmetics();
        
        plugin.getLogger().info("Cosmetic system reloaded with " + getTotalCosmetics() + " cosmetics");
    }
    
    // Getters for the specialized managers (if needed for advanced operations)
    public CosmeticRegistry getRegistry() {
        return registry;
    }
    
    public CosmeticActivationManager getActivationManager() {
        return activationManager;
    }
    
    public CosmeticCleanupManager getCleanupManager() {
        return cleanupManager;
    }
}
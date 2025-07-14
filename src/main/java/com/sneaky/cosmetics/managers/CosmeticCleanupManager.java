package com.sneaky.cosmetics.managers;

import com.sneaky.cosmetics.SneakyCosmetics;

/**
 * Handles cleanup and maintenance tasks for cosmetics
 */
public class CosmeticCleanupManager {
    
    private final SneakyCosmetics plugin;
    private final CosmeticActivationManager activationManager;
    
    public CosmeticCleanupManager(SneakyCosmetics plugin, CosmeticActivationManager activationManager) {
        this.plugin = plugin;
        this.activationManager = activationManager;
    }
    
    /**
     * Run comprehensive cleanup of inactive cosmetic entities and effects
     */
    public void cleanupInactiveCosmetics() {
        plugin.getLogger().info("Running cosmetic cleanup task...");
        
        plugin.getSchedulerAdapter().runTaskAsynchronously(() -> {
            int cleanedUp = 0;
            
            try {
                // Clean up cosmetics for offline players
                cleanedUp += activationManager.cleanupOfflinePlayerCosmetics();
                
                // Clean up particle effects for offline players
                if (plugin.getParticleManager() != null) {
                    plugin.getParticleManager().cleanupOfflinePlayerParticles();
                }
                
                // Clean up database cache for offline players
                if (plugin.getDatabaseManager() != null) {
                    // Database manager has its own cleanup task, but we can trigger cache cleanup
                    for (org.bukkit.entity.Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                        // Keep online players in cache, database manager handles offline cleanup
                    }
                }
                
                plugin.getLogger().info("Cosmetic cleanup completed. Cleaned up " + cleanedUp + " entities.");
                
            } catch (Exception e) {
                plugin.getLogger().warning("Error during cosmetic cleanup: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Schedule regular cleanup tasks
     */
    public void startCleanupTasks() {
        // Schedule cleanup every 30 minutes
        plugin.getSchedulerAdapter().runTaskTimerAsynchronously(() -> {
            cleanupInactiveCosmetics();
        }, 36000L, 36000L); // 30 minutes in ticks
        
        plugin.getLogger().info("Scheduled cosmetic cleanup tasks");
    }
    
    /**
     * Force cleanup of all cosmetic effects (for plugin shutdown)
     */
    public void forceCleanupAll() {
        plugin.getLogger().info("Force cleaning up all cosmetic effects...");
        
        try {
            // Clean up all active cosmetics for all players
            for (org.bukkit.entity.Player player : plugin.getServer().getOnlinePlayers()) {
                activationManager.clearAllCosmetics(player);
            }
            
            // Clean up particle effects
            if (plugin.getParticleManager() != null) {
                plugin.getParticleManager().cleanupOfflinePlayerParticles();
            }
            
            plugin.getLogger().info("Force cleanup completed");
            
        } catch (Exception e) {
            plugin.getLogger().warning("Error during force cleanup: " + e.getMessage());
        }
    }
}
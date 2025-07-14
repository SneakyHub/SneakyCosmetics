package com.sneaky.cosmetics.managers;

import com.sneaky.cosmetics.SneakyCosmetics;
import org.bukkit.entity.Player;

/**
 * Manages particle effects and cleanup for the cosmetics plugin
 */
public class ParticleManager {
    private final SneakyCosmetics plugin;
    
    public ParticleManager(SneakyCosmetics plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Clean up particle effects for offline players
     */
    public void cleanupOfflinePlayerParticles() {
        try {
            // Clean up advanced particles if available
            Class<?> advancedParticleClass = Class.forName("com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic");
            advancedParticleClass.getMethod("cleanupOfflinePlayerParticles").invoke(null);
            plugin.getLogger().fine("Cleaned up advanced particle effects for offline players");
        } catch (ClassNotFoundException e) {
            plugin.getLogger().fine("AdvancedParticleCosmetic class not found, skipping cleanup");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to cleanup offline player particles: " + e.getMessage());
        }
    }
    
    /**
     * Stop all particle effects for a specific player
     */
    public void stopPlayerEffects(Player player) {
        // Stop basic particle effects
        if (plugin.getCosmeticManager() != null) {
            plugin.getCosmeticManager().deactivateAllCosmetics(player, com.sneaky.cosmetics.cosmetics.CosmeticType.PARTICLE);
        }
    }
    
    /**
     * Start particle cleanup task
     */
    public void startParticleTask() {
        // This is now handled by the CosmeticCleanupManager
        // No need to start separate tasks since the new architecture handles this
        plugin.getLogger().fine("Particle tasks are handled by CosmeticCleanupManager");
    }
    
    /**
     * Stop all particle tasks
     */
    public void stopAllTasks() {
        // This is now handled by the CosmeticCleanupManager
        // No need to stop separate tasks since the new architecture handles this
        plugin.getLogger().fine("Particle task stopping is handled by CosmeticCleanupManager");
    }
}

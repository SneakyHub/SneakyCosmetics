package com.sneaky.cosmetics.managers;

import com.sneaky.cosmetics.SneakyCosmetics;
import org.bukkit.entity.Player;

public class ParticleManager {
    private final SneakyCosmetics plugin;
    
    public ParticleManager(SneakyCosmetics plugin) {
        this.plugin = plugin;
    }
    
    public void startParticleTask() {}
    public void startTrailTask() {}
    public void startPetTask() {}
    public void startWingTask() {}
    public void startAuraTask() {}
    public void stopAllTasks() {}
    public void stopPlayerEffects(Player player) {}
    public void stopPlayerTrails(Player player) {}
    public void removePet(Player player) {}
    public void stopPlayerWings(Player player) {}
    public void stopPlayerAuras(Player player) {}
    
    /**
     * Clean up particle effects for offline players
     */
    public void cleanupOfflinePlayerParticles() {
        try {
            // Clean up advanced particles
            Class.forName("com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic")
                .getMethod("cleanupOfflinePlayerParticles")
                .invoke(null);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to cleanup offline player particles: " + e.getMessage());
        }
    }
}

package com.sneaky.cosmetics.cosmetics.particles;

import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Particle cosmetic that creates particle effects around the player
 */
public class ParticleCosmetic extends Cosmetic {
    
    private final Particle particle;
    private final int count;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final double speed;
    private final Plugin plugin;
    
    private static final Map<Player, BukkitRunnable> activeParticles = new ConcurrentHashMap<>();
    
    public ParticleCosmetic(String id, String displayName, int price, Material iconMaterial,
                           List<String> description, String permission, boolean requiresVIP,
                           boolean requiresPremium, Particle particle, int count,
                           double offsetX, double offsetY, double offsetZ, double speed, Plugin plugin) {
        super(id, displayName, CosmeticType.PARTICLE, price, iconMaterial, description, 
              permission, requiresVIP, requiresPremium);
        this.particle = particle;
        this.count = count;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.plugin = plugin;
    }
    
    @Override
    public void activate(Player player) {
        // Stop any existing particle effect
        deactivate(player);
        
        // Start new particle effect
        BukkitRunnable particleTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    activeParticles.remove(player);
                    return;
                }
                
                // Spawn particles around the player
                player.getWorld().spawnParticle(particle, 
                    player.getLocation().add(0, 1, 0), 
                    count, offsetX, offsetY, offsetZ, speed);
            }
        };
        
        particleTask.runTaskTimer(plugin, 0L, 10L); // Every 0.5 seconds
        activeParticles.put(player, particleTask);
    }
    
    @Override
    public void deactivate(Player player) {
        BukkitRunnable task = activeParticles.remove(player);
        if (task != null) {
            task.cancel();
        }
    }
    
    @Override
    public boolean isActive(Player player) {
        return activeParticles.containsKey(player);
    }
    
    @Override
    public void cleanup(Player player) {
        deactivate(player);
    }
}
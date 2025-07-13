package com.sneaky.cosmetics.cosmetics.particles;

import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Advanced particle cosmetic with special patterns and effects
 */
public class AdvancedParticleCosmetic extends Cosmetic {
    
    private final Particle particle;
    private final int count;
    private final double speed;
    private final Plugin plugin;
    private final ParticlePattern pattern;
    
    private static final Map<Player, BukkitRunnable> activeParticles = new ConcurrentHashMap<>();
    
    public enum ParticlePattern {
        CIRCLE, HELIX, WAVE, TORNADO, HEART, STAR, GALAXY, SPIRAL
    }
    
    public AdvancedParticleCosmetic(String id, String displayName, int price, Material iconMaterial,
                                   List<String> description, String permission, boolean requiresVIP,
                                   boolean requiresPremium, Particle particle, int count,
                                   double speed, ParticlePattern pattern, Plugin plugin) {
        super(id, displayName, CosmeticType.PARTICLE, price, iconMaterial, description, 
              permission, requiresVIP, requiresPremium);
        this.particle = particle;
        this.count = count;
        this.speed = speed;
        this.pattern = pattern;
        this.plugin = plugin;
    }
    
    @Override
    public void activate(Player player) {
        // Stop any existing particle effect
        deactivate(player);
        
        // Start new particle effect with pattern
        BukkitRunnable particleTask = new BukkitRunnable() {
            private int tick = 0;
            
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    activeParticles.remove(player);
                    return;
                }
                
                Location playerLoc = player.getLocation().add(0, 1, 0);
                
                switch (pattern) {
                    case CIRCLE:
                        spawnCirclePattern(player, playerLoc, tick);
                        break;
                    case HELIX:
                        spawnHelixPattern(player, playerLoc, tick);
                        break;
                    case WAVE:
                        spawnWavePattern(player, playerLoc, tick);
                        break;
                    case TORNADO:
                        spawnTornadoPattern(player, playerLoc, tick);
                        break;
                    case HEART:
                        spawnHeartPattern(player, playerLoc, tick);
                        break;
                    case STAR:
                        spawnStarPattern(player, playerLoc, tick);
                        break;
                    case GALAXY:
                        spawnGalaxyPattern(player, playerLoc, tick);
                        break;
                    case SPIRAL:
                        spawnSpiralPattern(player, playerLoc, tick);
                        break;
                }
                
                tick++;
            }
        };
        
        particleTask.runTaskTimer(plugin, 0L, 2L); // Every 0.1 seconds for smooth animation
        activeParticles.put(player, particleTask);
    }
    
    private void spawnCirclePattern(Player player, Location center, int tick) {
        double radius = 1.5;
        int points = 8;
        
        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI * i / points) + (tick * 0.1);
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            
            Location particleLoc = center.clone().add(x, 0, z);
            player.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, speed);
        }
    }
    
    private void spawnHelixPattern(Player player, Location center, int tick) {
        double radius = 1.0;
        double height = 2.0;
        int points = 6;
        
        for (int i = 0; i < points; i++) {
            double angle = (tick + i * 60) * 0.1;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            double y = (Math.sin(tick * 0.05) * height) - 0.5;
            
            Location particleLoc = center.clone().add(x, y, z);
            player.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, speed);
        }
    }
    
    private void spawnWavePattern(Player player, Location center, int tick) {
        double radius = 2.0;
        int points = 12;
        
        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI * i / points);
            double waveRadius = radius + Math.sin(tick * 0.1 + i * 0.5) * 0.5;
            double x = waveRadius * Math.cos(angle);
            double z = waveRadius * Math.sin(angle);
            
            Location particleLoc = center.clone().add(x, 0, z);
            player.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, speed);
        }
    }
    
    private void spawnTornadoPattern(Player player, Location center, int tick) {
        for (int i = 0; i < 3; i++) {
            double height = i * 0.5;
            double radius = 1.5 - (i * 0.3);
            double angle = (tick + i * 40) * 0.15;
            
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            
            Location particleLoc = center.clone().add(x, height, z);
            player.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, speed);
        }
    }
    
    private void spawnHeartPattern(Player player, Location center, int tick) {
        if (tick % 20 == 0) { // Show heart every second
            for (double t = 0; t <= 2 * Math.PI; t += 0.3) {
                double x = 16 * Math.pow(Math.sin(t), 3) / 16;
                double y = (13 * Math.cos(t) - 5 * Math.cos(2*t) - 2 * Math.cos(3*t) - Math.cos(4*t)) / 16;
                
                Location particleLoc = center.clone().add(x, y + 0.5, 0);
                player.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, speed);
            }
        }
    }
    
    private void spawnStarPattern(Player player, Location center, int tick) {
        if (tick % 15 == 0) { // Show star every 0.75 seconds
            int points = 5;
            double outerRadius = 1.5;
            double innerRadius = 0.7;
            
            for (int i = 0; i < points * 2; i++) {
                double angle = (Math.PI * i) / points;
                double radius = (i % 2 == 0) ? outerRadius : innerRadius;
                
                double x = radius * Math.cos(angle - Math.PI/2);
                double z = radius * Math.sin(angle - Math.PI/2);
                
                Location particleLoc = center.clone().add(x, 0, z);
                player.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, speed);
            }
        }
    }
    
    private void spawnGalaxyPattern(Player player, Location center, int tick) {
        double radius = 2.0;
        int arms = 3;
        int pointsPerArm = 4;
        
        for (int arm = 0; arm < arms; arm++) {
            for (int i = 0; i < pointsPerArm; i++) {
                double armAngle = (2 * Math.PI * arm) / arms;
                double spiralAngle = armAngle + (i * 0.5) + (tick * 0.05);
                double spiralRadius = radius * (i + 1) / pointsPerArm;
                
                double x = spiralRadius * Math.cos(spiralAngle);
                double z = spiralRadius * Math.sin(spiralAngle);
                
                Location particleLoc = center.clone().add(x, 0, z);
                player.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, speed);
            }
        }
    }
    
    private void spawnSpiralPattern(Player player, Location center, int tick) {
        for (int i = 0; i < 20; i++) {
            double angle = (tick + i * 10) * 0.1;
            double radius = i * 0.1;
            double height = Math.sin(angle) * 0.5;
            
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            
            Location particleLoc = center.clone().add(x, height, z);
            player.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, speed);
        }
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
    
    /**
     * Clean up particles for offline players
     */
    public static void cleanupOfflinePlayerParticles() {
        activeParticles.entrySet().removeIf(entry -> {
            Player player = entry.getKey();
            if (!player.isOnline()) {
                entry.getValue().cancel();
                return true;
            }
            return false;
        });
    }
}
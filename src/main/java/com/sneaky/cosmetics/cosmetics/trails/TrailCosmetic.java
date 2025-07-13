package com.sneaky.cosmetics.cosmetics.trails;

import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Trail cosmetic that creates particle trails behind the player as they move
 */
public class TrailCosmetic extends Cosmetic {
    
    private final Particle particle;
    private final int count;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final double speed;
    private final Plugin plugin;
    
    private static final Map<Player, BukkitRunnable> activeTrails = new ConcurrentHashMap<>();
    private static final Map<Player, Location> lastLocations = new ConcurrentHashMap<>();
    
    public TrailCosmetic(String id, String displayName, int price, Material iconMaterial,
                        List<String> description, String permission, boolean requiresVIP,
                        boolean requiresPremium, Particle particle, int count,
                        double offsetX, double offsetY, double offsetZ, double speed, Plugin plugin) {
        super(id, displayName, CosmeticType.TRAIL, price, iconMaterial, description, 
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
        // Stop any existing trail effect
        deactivate(player);
        
        // Initialize last location
        lastLocations.put(player, player.getLocation());
        
        // Start new trail effect
        BukkitRunnable trailTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    activeTrails.remove(player);
                    lastLocations.remove(player);
                    return;
                }
                
                Location currentLocation = player.getLocation();
                Location lastLocation = lastLocations.get(player);
                
                // Only spawn particles if the player has moved
                if (lastLocation != null && currentLocation.distance(lastLocation) > 0.1) {
                    // Spawn particles at the player's feet
                    Location trailLocation = currentLocation.clone().add(0, 0.1, 0);
                    player.getWorld().spawnParticle(particle, trailLocation, 
                        count, offsetX, offsetY, offsetZ, speed);
                }
                
                lastLocations.put(player, currentLocation);
            }
        };
        
        trailTask.runTaskTimer(plugin, 0L, 2L); // Every 0.1 seconds
        activeTrails.put(player, trailTask);
    }
    
    @Override
    public void deactivate(Player player) {
        BukkitRunnable task = activeTrails.remove(player);
        if (task != null) {
            task.cancel();
        }
        lastLocations.remove(player);
    }
    
    @Override
    public boolean isActive(Player player) {
        return activeTrails.containsKey(player);
    }
    
    @Override
    public void cleanup(Player player) {
        deactivate(player);
    }
}
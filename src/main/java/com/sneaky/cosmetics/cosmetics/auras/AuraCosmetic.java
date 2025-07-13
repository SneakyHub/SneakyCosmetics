package com.sneaky.cosmetics.cosmetics.auras;

import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Aura cosmetic that creates mystical circular particle effects around players
 */
public class AuraCosmetic extends Cosmetic {
    
    private final Particle auraParticle;
    private final int particleCount;
    private final double radius;
    private final double height;
    private final double speed;
    private final AuraType auraType;
    private final Plugin plugin;
    
    private static final Map<Player, BukkitRunnable> activeTasks = new ConcurrentHashMap<>();
    
    public enum AuraType {
        HOLY("Holy Aura", Particle.ENCHANT, 1.5, 0.0),
        DARK("Dark Aura", Particle.SMOKE, 1.2, 0.0),
        FIRE("Fire Aura", Particle.FLAME, 1.3, 0.2),
        WATER("Water Aura", Particle.DRIPPING_WATER, 1.4, 0.1),
        NATURE("Nature Aura", Particle.HAPPY_VILLAGER, 1.6, 0.3),
        LIGHTNING("Lightning Aura", Particle.CRIT, 1.1, 0.5),
        FROST("Frost Aura", Particle.SNOWFLAKE, 1.5, 0.0),
        VOID("Void Aura", Particle.PORTAL, 1.8, 0.4),
        RAINBOW("Rainbow Aura", Particle.NOTE, 2.0, 0.6);
        
        private final String displayName;
        private final Particle particle;
        private final double defaultRadius;
        private final double defaultHeight;
        
        AuraType(String displayName, Particle particle, double defaultRadius, double defaultHeight) {
            this.displayName = displayName;
            this.particle = particle;
            this.defaultRadius = defaultRadius;
            this.defaultHeight = defaultHeight;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public Particle getParticle() {
            return particle;
        }
        
        public double getDefaultRadius() {
            return defaultRadius;
        }
        
        public double getDefaultHeight() {
            return defaultHeight;
        }
    }
    
    public AuraCosmetic(String id, String displayName, int price, Material iconMaterial,
                       List<String> description, String permission, boolean requiresVIP,
                       boolean requiresPremium, Particle auraParticle, int particleCount,
                       double radius, double height, double speed, AuraType auraType, Plugin plugin) {
        super(id, displayName, CosmeticType.AURA, price, iconMaterial, description, 
              permission, requiresVIP, requiresPremium);
        this.auraParticle = auraParticle;
        this.particleCount = particleCount;
        this.radius = radius;
        this.height = height;
        this.speed = speed;
        this.auraType = auraType;
        this.plugin = plugin;
    }
    
    @Override
    public void activate(Player player) {
        // Remove any existing aura effect
        deactivate(player);
        
        // Start aura particle task
        BukkitRunnable auraTask = new BukkitRunnable() {
            private double angle = 0;
            
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    activeTasks.remove(player);
                    return;
                }
                
                createAuraEffect(player, angle);
                angle += 0.2; // Rotate the aura
                if (angle >= 2 * Math.PI) {
                    angle = 0;
                }
            }
        };
        
        auraTask.runTaskTimer(plugin, 0L, 3L); // Every 0.15 seconds
        activeTasks.put(player, auraTask);
        
        player.sendMessage("§a✓ Activated aura: " + getDisplayName());
    }
    
    @Override
    public void deactivate(Player player) {
        BukkitRunnable task = activeTasks.remove(player);
        if (task != null) {
            task.cancel();
        }
        
        player.sendMessage("§7⊘ Deactivated aura: " + getDisplayName());
    }
    
    @Override
    public boolean isActive(Player player) {
        return activeTasks.containsKey(player);
    }
    
    @Override
    public void cleanup(Player player) {
        deactivate(player);
    }
    
    private void createAuraEffect(Player player, double angle) {
        Location playerLoc = player.getLocation();
        
        // Create circular aura around player
        int points = 16; // Number of points in the circle
        for (int i = 0; i < points; i++) {
            double currentAngle = angle + (2 * Math.PI * i / points);
            
            double x = playerLoc.getX() + radius * Math.cos(currentAngle);
            double z = playerLoc.getZ() + radius * Math.sin(currentAngle);
            double y = playerLoc.getY() + 1.0 + height * Math.sin(angle * 2 + i * 0.5);
            
            Location particleLoc = new Location(playerLoc.getWorld(), x, y, z);
            
            // Special effects for different aura types
            switch (auraType) {
                case RAINBOW:
                    // Cycle through different colors for rainbow effect
                    Particle rainbowParticle = i % 3 == 0 ? Particle.NOTE : 
                                             i % 3 == 1 ? Particle.ENCHANT : Particle.HAPPY_VILLAGER;
                    player.getWorld().spawnParticle(rainbowParticle, particleLoc, 1, 0, 0, 0, speed);
                    break;
                case LIGHTNING:
                    // Random height variations for lightning effect
                    double lightningY = y + (Math.random() - 0.5) * 0.5;
                    Location lightningLoc = new Location(playerLoc.getWorld(), x, lightningY, z);
                    player.getWorld().spawnParticle(auraParticle, lightningLoc, particleCount, 0.1, 0.1, 0.1, speed);
                    break;
                case VOID:
                    // Spiral effect for void aura
                    double spiralRadius = radius * (0.5 + 0.5 * Math.sin(angle * 3 + i));
                    double spiralX = playerLoc.getX() + spiralRadius * Math.cos(currentAngle);
                    double spiralZ = playerLoc.getZ() + spiralRadius * Math.sin(currentAngle);
                    Location spiralLoc = new Location(playerLoc.getWorld(), spiralX, y, spiralZ);
                    player.getWorld().spawnParticle(auraParticle, spiralLoc, particleCount, 0.05, 0.05, 0.05, speed);
                    break;
                default:
                    // Standard circular aura
                    player.getWorld().spawnParticle(auraParticle, particleLoc, particleCount, 0.05, 0.05, 0.05, speed);
                    break;
            }
        }
        
        // Add central pillar effect for some aura types
        if (auraType == AuraType.HOLY || auraType == AuraType.VOID) {
            Location centerLoc = playerLoc.clone().add(0, 0.5, 0);
            player.getWorld().spawnParticle(auraParticle, centerLoc, 2, 0.1, 0.3, 0.1, 0.02);
        }
    }
    
    /**
     * Get the aura type
     */
    public AuraType getAuraType() {
        return auraType;
    }
    
    /**
     * Get the aura particle
     */
    public Particle getAuraParticle() {
        return auraParticle;
    }
    
    /**
     * Get the aura radius
     */
    public double getRadius() {
        return radius;
    }
    
    /**
     * Clean up all aura tasks (for plugin disable)
     */
    public static void cleanupAllAuras() {
        for (BukkitRunnable task : activeTasks.values()) {
            if (task != null) {
                task.cancel();
            }
        }
        activeTasks.clear();
    }
}
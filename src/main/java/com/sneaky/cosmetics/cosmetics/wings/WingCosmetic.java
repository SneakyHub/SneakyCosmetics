package com.sneaky.cosmetics.cosmetics.wings;

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
 * Wings cosmetic that creates particle wing effects behind players
 */
public class WingCosmetic extends Cosmetic {
    
    private final Particle wingParticle;
    private final int particleCount;
    private final double spreadX;
    private final double spreadY;
    private final double spreadZ;
    private final double speed;
    private final WingType wingType;
    private final Plugin plugin;
    
    private static final Map<Player, BukkitRunnable> activeTasks = new ConcurrentHashMap<>();
    
    public enum WingType {
        ANGEL("Angel Wings", Particle.CLOUD),
        DEMON("Demon Wings", Particle.SMOKE),
        FAIRY("Fairy Wings", Particle.ENCHANT),
        DRAGON("Dragon Wings", Particle.FLAME),
        BUTTERFLY("Butterfly Wings", Particle.NOTE),
        PHOENIX("Phoenix Wings", Particle.LAVA),
        ICE("Ice Wings", Particle.SNOWFLAKE),
        SHADOW("Shadow Wings", Particle.SQUID_INK);
        
        private final String displayName;
        private final Particle particle;
        
        WingType(String displayName, Particle particle) {
            this.displayName = displayName;
            this.particle = particle;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public Particle getParticle() {
            return particle;
        }
    }
    
    public WingCosmetic(String id, String displayName, int price, Material iconMaterial,
                       List<String> description, String permission, boolean requiresVIP,
                       boolean requiresPremium, Particle wingParticle, int particleCount,
                       double spreadX, double spreadY, double spreadZ, double speed,
                       WingType wingType, Plugin plugin) {
        super(id, displayName, CosmeticType.WINGS, price, iconMaterial, description, 
              permission, requiresVIP, requiresPremium);
        this.wingParticle = wingParticle;
        this.particleCount = particleCount;
        this.spreadX = spreadX;
        this.spreadY = spreadY;
        this.spreadZ = spreadZ;
        this.speed = speed;
        this.wingType = wingType;
        this.plugin = plugin;
    }
    
    @Override
    public void activate(Player player) {
        // Remove any existing wing effect
        deactivate(player);
        
        // Start wing particle task
        BukkitRunnable wingTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    activeTasks.remove(player);
                    return;
                }
                
                createWingEffect(player);
            }
        };
        
        wingTask.runTaskTimer(plugin, 0L, 2L); // Every 0.1 seconds
        activeTasks.put(player, wingTask);
        
        player.sendMessage("§a✓ Activated wings: " + getDisplayName());
    }
    
    @Override
    public void deactivate(Player player) {
        BukkitRunnable task = activeTasks.remove(player);
        if (task != null) {
            task.cancel();
        }
        
        player.sendMessage("§7⊘ Deactivated wings: " + getDisplayName());
    }
    
    @Override
    public boolean isActive(Player player) {
        return activeTasks.containsKey(player);
    }
    
    @Override
    public void cleanup(Player player) {
        deactivate(player);
    }
    
    private void createWingEffect(Player player) {
        Location playerLoc = player.getLocation();
        
        // Get player's direction
        float yaw = playerLoc.getYaw();
        double radians = Math.toRadians(yaw);
        
        // Calculate wing positions (left and right)
        double leftX = playerLoc.getX() + Math.cos(radians + Math.PI/2) * 1.2;
        double leftZ = playerLoc.getZ() + Math.sin(radians + Math.PI/2) * 1.2;
        
        double rightX = playerLoc.getX() + Math.cos(radians - Math.PI/2) * 1.2;
        double rightZ = playerLoc.getZ() + Math.sin(radians - Math.PI/2) * 1.2;
        
        double y = playerLoc.getY() + 1.5;
        
        // Create wing shapes
        for (int i = 0; i < 5; i++) {
            double offset = i * 0.3;
            
            // Left wing
            Location leftWing = new Location(playerLoc.getWorld(), 
                leftX - Math.cos(radians) * offset,
                y - offset * 0.2,
                leftZ - Math.sin(radians) * offset
            );
            
            // Right wing  
            Location rightWing = new Location(playerLoc.getWorld(),
                rightX - Math.cos(radians) * offset,
                y - offset * 0.2,
                rightZ - Math.sin(radians) * offset
            );
            
            // Spawn particles
            player.getWorld().spawnParticle(wingParticle, leftWing, particleCount, 
                spreadX, spreadY, spreadZ, speed);
            player.getWorld().spawnParticle(wingParticle, rightWing, particleCount, 
                spreadX, spreadY, spreadZ, speed);
        }
    }
    
    /**
     * Get the wing type
     */
    public WingType getWingType() {
        return wingType;
    }
    
    /**
     * Get the wing particle
     */
    public Particle getWingParticle() {
        return wingParticle;
    }
    
    /**
     * Clean up all wing tasks (for plugin disable)
     */
    public static void cleanupAllWings() {
        for (BukkitRunnable task : activeTasks.values()) {
            if (task != null) {
                task.cancel();
            }
        }
        activeTasks.clear();
    }
}
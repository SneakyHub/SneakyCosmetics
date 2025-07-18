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
        ANGEL("Angel Wings", Particle.ENCHANT),
        DEMON("Demon Wings", Particle.SMOKE),
        FAIRY("Fairy Wings", Particle.ENCHANT),
        DRAGON("Dragon Wings", Particle.FLAME),
        BUTTERFLY("Butterfly Wings", Particle.NOTE),
        PHOENIX("Phoenix Wings", Particle.FLAME),
        ICE("Ice Wings", Particle.SNOWFLAKE),
        SHADOW("Shadow Wings", Particle.SQUID_INK),
        LIGHTNING("Lightning Wings", Particle.ELECTRIC_SPARK),
        NATURE("Nature Wings", Particle.HAPPY_VILLAGER),
        COSMIC("Cosmic Wings", Particle.PORTAL),
        RAINBOW("Rainbow Wings", Particle.NOTE);
        
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
        
        // Get player's direction and position
        float yaw = playerLoc.getYaw();
        double radians = Math.toRadians(yaw);
        
        // Base position slightly behind and above player
        double baseX = playerLoc.getX() - Math.cos(radians) * 0.3;
        double baseY = playerLoc.getY() + 1.4;
        double baseZ = playerLoc.getZ() - Math.sin(radians) * 0.3;
        
        // Create wing animation based on time
        long time = System.currentTimeMillis();
        double wingBeat = Math.sin(time * 0.01) * 0.3; // Wing flapping animation
        
        // Create left and right wings with proper wing shape
        createSingleWing(player, baseX, baseY, baseZ, radians, true, wingBeat);   // Left wing
        createSingleWing(player, baseX, baseY, baseZ, radians, false, wingBeat);  // Right wing
    }
    
    private void createSingleWing(Player player, double baseX, double baseY, double baseZ, 
                                 double yaw, boolean isLeft, double wingBeat) {
        
        // Wing configuration
        int wingSegments = 12; // Number of wing sections
        double wingSpan = 2.5; // How wide the wings extend
        double wingHeight = 1.8; // How tall the wings are
        
        // Direction multiplier for left/right wing
        double sideMultiplier = isLeft ? 1.0 : -1.0;
        
        // Calculate wing shape points using a wing curve
        for (int i = 0; i < wingSegments; i++) {
            double progress = (double) i / (wingSegments - 1); // 0.0 to 1.0
            
            // Create a realistic wing curve (starts narrow, widens, then tapers)
            double wingCurve = Math.sin(progress * Math.PI) * wingSpan;
            double heightCurve = Math.cos(progress * Math.PI * 0.5) * wingHeight;
            
            // Add wing beat animation
            double animatedHeight = heightCurve + wingBeat * (1.0 - progress * 0.5);
            double animatedSpan = wingCurve * (1.0 + wingBeat * 0.2);
            
            // Calculate the actual world position
            double wingX = baseX + Math.cos(yaw + Math.PI/2) * animatedSpan * sideMultiplier;
            double wingY = baseY + animatedHeight * 0.5 - progress * 0.3;
            double wingZ = baseZ + Math.sin(yaw + Math.PI/2) * animatedSpan * sideMultiplier;
            
            // Add backward offset that increases with distance from body
            wingX -= Math.cos(yaw) * progress * 0.8;
            wingZ -= Math.sin(yaw) * progress * 0.8;
            
            Location wingPoint = new Location(player.getWorld(), wingX, wingY, wingZ);
            
            // Create wing membrane effect with multiple layers
            createWingMembrane(player, wingPoint, progress, isLeft);
        }
        
        // Add wing tips with special effects
        createWingTips(player, baseX, baseY, baseZ, yaw, isLeft, wingBeat);
    }
    
    private void createWingMembrane(Player player, Location wingPoint, double progress, boolean isLeft) {
        // Main wing structure
        player.getWorld().spawnParticle(wingParticle, wingPoint, 1, 0.05, 0.05, 0.05, 0.0);
        
        // Add wing membrane details for larger wings
        if (progress > 0.2 && progress < 0.8) {
            // Inner membrane with slight randomization
            Location innerPoint = wingPoint.clone().add(
                Math.random() * 0.3 - 0.15,
                Math.random() * 0.2 - 0.1,
                Math.random() * 0.3 - 0.15
            );
            player.getWorld().spawnParticle(wingParticle, innerPoint, 1, 0.02, 0.02, 0.02, 0.0);
        }
        
        // Wing-type specific membrane effects
        switch (wingType) {
            case ANGEL:
                // Feathery texture
                if (Math.random() < 0.3) {
                    Location featherPoint = wingPoint.clone().add(0, -0.1, 0);
                    player.getWorld().spawnParticle(Particle.CLOUD, featherPoint, 1, 0.05, 0.05, 0.05, 0.0);
                }
                break;
                
            case DEMON:
                // Dark, smoky texture
                if (Math.random() < 0.2) {
                    player.getWorld().spawnParticle(Particle.SMOKE, wingPoint, 1, 0.1, 0.1, 0.1, 0.0);
                }
                break;
                
            case BUTTERFLY:
                // Colorful patterns on wings
                if (Math.random() < 0.4 && progress > 0.3 && progress < 0.7) {
                    player.getWorld().spawnParticle(Particle.NOTE, wingPoint, 1, 0.0, 0.0, 0.0, 0.0);
                }
                break;
                
            case DRAGON:
                // Scales and ember effects
                if (Math.random() < 0.15) {
                    player.getWorld().spawnParticle(Particle.LAVA, wingPoint, 1, 0.0, 0.0, 0.0, 0.0);
                }
                break;
                
            case PHOENIX:
                // Fiery feathers
                if (Math.random() < 0.25) {
                    Location featherPoint = wingPoint.clone().add(0, -0.1, 0);
                    player.getWorld().spawnParticle(Particle.FLAME, featherPoint, 1, 0.05, 0.05, 0.05, 0.0);
                }
                break;
                
            case ICE:
                // Crystalline structure
                if (Math.random() < 0.3) {
                    player.getWorld().spawnParticle(Particle.SNOWFLAKE, wingPoint, 1, 0.05, 0.05, 0.05, 0.0);
                }
                break;
                
            case LIGHTNING:
                // Electric arcs
                if (Math.random() < 0.2) {
                    player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, wingPoint, 1, 0.1, 0.1, 0.1, 0.0);
                }
                break;
                
            case NATURE:
                // Living wing with growth particles
                if (Math.random() < 0.25) {
                    player.getWorld().spawnParticle(Particle.COMPOSTER, wingPoint, 1, 0.05, 0.05, 0.05, 0.0);
                }
                break;
                
            case COSMIC:
                // Portal energy effects
                if (Math.random() < 0.2) {
                    player.getWorld().spawnParticle(Particle.PORTAL, wingPoint, 1, 0.1, 0.1, 0.1, 0.0);
                }
                break;
                
            case FAIRY:
            case SHADOW:
            case RAINBOW:
            default:
                // Default wing membrane (already handled above)
                break;
        }
    }
    
    private void createWingTips(Player player, double baseX, double baseY, double baseZ, 
                               double yaw, boolean isLeft, double wingBeat) {
        
        double sideMultiplier = isLeft ? 1.0 : -1.0;
        
        // Wing tip position (furthest point of wing)
        double tipX = baseX + Math.cos(yaw + Math.PI/2) * (2.5 + wingBeat * 0.3) * sideMultiplier;
        double tipY = baseY + 0.5 + wingBeat * 0.2;
        double tipZ = baseZ + Math.sin(yaw + Math.PI/2) * (2.5 + wingBeat * 0.3) * sideMultiplier;
        
        // Add backward offset for wing tips
        tipX -= Math.cos(yaw) * 1.2;
        tipZ -= Math.sin(yaw) * 1.2;
        
        Location tipLocation = new Location(player.getWorld(), tipX, tipY, tipZ);
        
        // Wing tip effects based on wing type
        switch (wingType) {
            case ANGEL:
                player.getWorld().spawnParticle(Particle.ENCHANT, tipLocation, 3, 0.1, 0.1, 0.1, 0.1);
                player.getWorld().spawnParticle(Particle.CLOUD, tipLocation, 1, 0.05, 0.05, 0.05, 0.0);
                break;
            case DEMON:
                player.getWorld().spawnParticle(Particle.SMOKE, tipLocation, 2, 0.1, 0.1, 0.1, 0.05);
                if (Math.random() < 0.3) {
                    player.getWorld().spawnParticle(Particle.LAVA, tipLocation, 1, 0.0, 0.0, 0.0, 0.0);
                }
                break;
            case FAIRY:
                player.getWorld().spawnParticle(Particle.ENCHANT, tipLocation, 5, 0.2, 0.2, 0.2, 0.1);
                player.getWorld().spawnParticle(Particle.END_ROD, tipLocation, 1, 0.0, 0.0, 0.0, 0.0);
                break;
            case DRAGON:
                player.getWorld().spawnParticle(Particle.FLAME, tipLocation, 2, 0.1, 0.1, 0.1, 0.02);
                player.getWorld().spawnParticle(Particle.SMOKE, tipLocation, 1, 0.05, 0.05, 0.05, 0.0);
                break;
            case BUTTERFLY:
                // Create colorful butterfly wing tips
                player.getWorld().spawnParticle(Particle.NOTE, tipLocation, 1, 0.0, 0.0, 0.0, 0.0);
                player.getWorld().spawnParticle(Particle.ENCHANT, tipLocation, 2, 0.1, 0.1, 0.1, 0.0);
                break;
            case PHOENIX:
                player.getWorld().spawnParticle(Particle.FLAME, tipLocation, 3, 0.1, 0.1, 0.1, 0.02);
                player.getWorld().spawnParticle(Particle.LAVA, tipLocation, 1, 0.05, 0.05, 0.05, 0.0);
                if (Math.random() < 0.2) {
                    player.getWorld().spawnParticle(Particle.END_ROD, tipLocation, 1, 0.0, 0.0, 0.0, 0.0);
                }
                break;
            case ICE:
                player.getWorld().spawnParticle(Particle.SNOWFLAKE, tipLocation, 3, 0.1, 0.1, 0.1, 0.0);
                player.getWorld().spawnParticle(Particle.CLOUD, tipLocation, 1, 0.05, 0.05, 0.05, 0.0);
                break;
            case SHADOW:
                player.getWorld().spawnParticle(Particle.SQUID_INK, tipLocation, 2, 0.1, 0.1, 0.1, 0.0);
                player.getWorld().spawnParticle(Particle.SMOKE, tipLocation, 1, 0.05, 0.05, 0.05, 0.0);
                break;
            case LIGHTNING:
                player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, tipLocation, 3, 0.2, 0.2, 0.2, 0.1);
                if (Math.random() < 0.4) {
                    player.getWorld().spawnParticle(Particle.END_ROD, tipLocation, 1, 0.0, 0.0, 0.0, 0.0);
                }
                break;
            case NATURE:
                player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, tipLocation, 2, 0.1, 0.1, 0.1, 0.0);
                if (Math.random() < 0.3) {
                    player.getWorld().spawnParticle(Particle.COMPOSTER, tipLocation, 1, 0.05, 0.05, 0.05, 0.0);
                }
                break;
            case COSMIC:
                player.getWorld().spawnParticle(Particle.PORTAL, tipLocation, 4, 0.2, 0.2, 0.2, 0.1);
                player.getWorld().spawnParticle(Particle.ENCHANT, tipLocation, 2, 0.1, 0.1, 0.1, 0.0);
                break;
            case RAINBOW:
                // Create rainbow effect at wing tips
                long time = System.currentTimeMillis();
                int colorIndex = (int) ((time / 200) % 6); // Cycle through colors
                Particle rainbowParticle = getRainbowParticle(colorIndex);
                player.getWorld().spawnParticle(rainbowParticle, tipLocation, 2, 0.1, 0.1, 0.1, 0.0);
                player.getWorld().spawnParticle(Particle.NOTE, tipLocation, 1, 0.0, 0.0, 0.0, 0.0);
                break;
        }
    }
    
    private Particle getRainbowParticle(int colorIndex) {
        switch (colorIndex) {
            case 0: return Particle.FLAME; // Red
            case 1: return Particle.LAVA; // Orange
            case 2: return Particle.ENCHANT; // Yellow/Gold
            case 3: return Particle.HAPPY_VILLAGER; // Green
            case 4: return Particle.ENCHANT; // Blue/Cyan
            case 5: return Particle.PORTAL; // Purple
            default: return Particle.ENCHANT;
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
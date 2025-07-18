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
        // === ORIGINAL AURAS (9) ===
        HOLY("Holy Aura", Particle.ENCHANT, 1.5, 0.0),
        DARK("Dark Aura", Particle.SMOKE, 1.2, 0.0),
        FIRE("Fire Aura", Particle.FLAME, 1.3, 0.2),
        WATER("Water Aura", Particle.DRIPPING_WATER, 1.4, 0.1),
        NATURE("Nature Aura", Particle.HAPPY_VILLAGER, 1.6, 0.3),
        LIGHTNING("Lightning Aura", Particle.CRIT, 1.1, 0.5),
        FROST("Frost Aura", Particle.SNOWFLAKE, 1.5, 0.0),
        VOID("Void Aura", Particle.PORTAL, 1.8, 0.4),
        RAINBOW("Rainbow Aura", Particle.NOTE, 2.0, 0.6),
        
        // === ELEMENTAL AURAS (20) ===
        INFERNO("Inferno Aura", Particle.LAVA, 1.4, 0.3),
        BLIZZARD("Blizzard Aura", Particle.SNOWFLAKE, 1.8, 0.2),
        TORNADO("Tornado Aura", Particle.CLOUD, 1.2, 0.8),
        EARTHQUAKE("Earthquake Aura", Particle.BLOCK, 1.5, 0.0),
        PLASMA("Plasma Aura", Particle.ELECTRIC_SPARK, 1.3, 0.4),
        MAGMA("Magma Aura", Particle.DRIPPING_LAVA, 1.6, 0.1),
        ICE_STORM("Ice Storm Aura", Particle.SNOWFLAKE, 2.0, 0.5),
        THUNDER("Thunder Aura", Particle.ELECTRIC_SPARK, 1.7, 0.6),
        TSUNAMI("Tsunami Aura", Particle.SPLASH, 1.9, 0.3),
        VOLCANIC("Volcanic Aura", Particle.ASH, 1.4, 0.7),
        CRYSTAL("Crystal Aura", Particle.END_ROD, 1.5, 0.0),
        STEAM("Steam Aura", Particle.CLOUD, 1.3, 0.4),
        SOLAR("Solar Aura", Particle.FLAME, 2.1, 0.2),
        LUNAR("Lunar Aura", Particle.ENCHANT, 1.8, 0.0),
        WIND("Wind Aura", Particle.SWEEP_ATTACK, 1.6, 0.5),
        EARTH("Earth Aura", Particle.COMPOSTER, 1.4, 0.1),
        METAL("Metal Aura", Particle.CRIT, 1.2, 0.0),
        ACID("Acid Aura", Particle.HAPPY_VILLAGER, 1.3, 0.3),
        POISON("Poison Aura", Particle.HAPPY_VILLAGER, 1.5, 0.2),
        RADIANT("Radiant Aura", Particle.END_ROD, 1.7, 0.4),
        
        // === COSMIC AURAS (15) ===
        GALAXY("Galaxy Aura", Particle.PORTAL, 2.2, 0.8),
        NEBULA("Nebula Aura", Particle.ENCHANT, 2.0, 0.6),
        STARLIGHT("Starlight Aura", Particle.END_ROD, 1.8, 0.0),
        BLACKHOLE("Black Hole Aura", Particle.SQUID_INK, 1.5, 1.0),
        COMET("Comet Aura", Particle.FIREWORK, 1.9, 0.4),
        SUPERNOVA("Supernova Aura", Particle.EXPLOSION, 2.5, 0.7),
        ASTEROID("Asteroid Aura", Particle.BLOCK, 1.6, 0.2),
        QUASAR("Quasar Aura", Particle.ELECTRIC_SPARK, 2.1, 0.9),
        PULSAR("Pulsar Aura", Particle.END_ROD, 1.4, 0.3),
        METEOR("Meteor Aura", Particle.FLAME, 1.7, 0.5),
        CONSTELLATION("Constellation Aura", Particle.ENCHANT, 2.3, 0.1),
        COSMIC_DUST("Cosmic Dust Aura", Particle.ASH, 2.0, 0.4),
        AURORA("Aurora Aura", Particle.NOTE, 2.4, 0.6),
        ANTIMATTER("Antimatter Aura", Particle.PORTAL, 1.3, 0.8),
        WORMHOLE("Wormhole Aura", Particle.PORTAL, 1.1, 1.2),
        
        // === MYSTICAL AURAS (15) ===
        ARCANE("Arcane Aura", Particle.ENCHANT, 1.6, 0.5),
        DIVINE("Divine Aura", Particle.END_ROD, 1.8, 0.0),
        DEMONIC("Demonic Aura", Particle.SMOKE, 1.4, 0.3),
        ANGELIC("Angelic Aura", Particle.CLOUD, 2.0, 0.2),
        NECROMANTIC("Necromantic Aura", Particle.ASH, 1.5, 0.4),
        DRUIDIC("Druidic Aura", Particle.COMPOSTER, 1.7, 0.6),
        SHAMANIC("Shamanic Aura", Particle.SMOKE, 1.3, 0.7),
        ETHEREAL("Ethereal Aura", Particle.END_ROD, 1.9, 0.1),
        SPECTRAL("Spectral Aura", Particle.ENCHANT, 1.2, 0.8),
        RUNIC("Runic Aura", Particle.ENCHANT, 1.4, 0.0),
        CELESTIAL("Celestial Aura", Particle.FIREWORK, 2.1, 0.3),
        INFERNAL("Infernal Aura", Particle.LAVA, 1.6, 0.5),
        ELDRITCH("Eldritch Aura", Particle.PORTAL, 1.8, 0.9),
        PRIMORDIAL("Primordial Aura", Particle.ASH, 2.2, 0.4),
        TEMPORAL("Temporal Aura", Particle.PORTAL, 1.5, 1.0),
        
        // === NATURE AURAS (12) ===
        FOREST("Forest Aura", Particle.HAPPY_VILLAGER, 1.8, 0.3),
        OCEAN("Ocean Aura", Particle.SPLASH, 2.0, 0.2),
        DESERT("Desert Aura", Particle.ASH, 1.6, 0.0),
        JUNGLE("Jungle Aura", Particle.COMPOSTER, 1.9, 0.4),
        TUNDRA("Tundra Aura", Particle.SNOWFLAKE, 1.7, 0.1),
        SWAMP("Swamp Aura", Particle.HAPPY_VILLAGER, 1.4, 0.5),
        MOUNTAIN("Mountain Aura", Particle.BLOCK, 1.5, 0.0),
        FLOWER("Flower Aura", Particle.NOTE, 1.3, 0.2),
        MUSHROOM("Mushroom Aura", Particle.COMPOSTER, 1.2, 0.3),
        CORAL("Coral Aura", Particle.SPLASH, 1.6, 0.1),
        BAMBOO("Bamboo Aura", Particle.HAPPY_VILLAGER, 1.4, 0.6),
        SAKURA("Sakura Aura", Particle.NOTE, 1.7, 0.4),
        
        // === ANIMAL SPIRIT AURAS (13) ===
        WOLF("Wolf Spirit Aura", Particle.SMOKE, 1.5, 0.2),
        EAGLE("Eagle Spirit Aura", Particle.CLOUD, 2.0, 0.8),
        BEAR("Bear Spirit Aura", Particle.BLOCK, 1.8, 0.0),
        TIGER("Tiger Spirit Aura", Particle.FLAME, 1.6, 0.3),
        DRAGON_SPIRIT("Dragon Spirit Aura", Particle.FLAME, 2.2, 0.5),
        PHOENIX_SPIRIT("Phoenix Spirit Aura", Particle.LAVA, 2.1, 0.7),
        SERPENT("Serpent Spirit Aura", Particle.HAPPY_VILLAGER, 1.3, 0.1),
        SPIDER("Spider Spirit Aura", Particle.SQUID_INK, 1.4, 0.4),
        SHARK("Shark Spirit Aura", Particle.SPLASH, 1.7, 0.2),
        FALCON("Falcon Spirit Aura", Particle.SWEEP_ATTACK, 1.9, 0.6),
        PANTHER("Panther Spirit Aura", Particle.SMOKE, 1.5, 0.3),
        RAVEN("Raven Spirit Aura", Particle.ASH, 1.6, 0.5),
        FOX("Fox Spirit Aura", Particle.FLAME, 1.2, 0.1),
        
        // === GEMSTONE AURAS (10) ===
        DIAMOND("Diamond Aura", Particle.END_ROD, 1.8, 0.0),
        EMERALD("Emerald Aura", Particle.HAPPY_VILLAGER, 1.6, 0.2),
        RUBY("Ruby Aura", Particle.LAVA, 1.5, 0.1),
        SAPPHIRE("Sapphire Aura", Particle.ENCHANT, 1.7, 0.3),
        AMETHYST("Amethyst Aura", Particle.PORTAL, 1.4, 0.4),
        TOPAZ("Topaz Aura", Particle.FLAME, 1.3, 0.2),
        OPAL("Opal Aura", Particle.NOTE, 1.9, 0.5),
        JADE("Jade Aura", Particle.COMPOSTER, 1.6, 0.1),
        ONYX("Onyx Aura", Particle.SQUID_INK, 1.2, 0.0),
        PEARL("Pearl Aura", Particle.CLOUD, 1.5, 0.3),
        
        // === SPECIAL EFFECT AURAS (11) ===
        MATRIX("Matrix Aura", Particle.ENCHANT, 1.4, 0.8),
        NEON("Neon Aura", Particle.END_ROD, 1.6, 0.0),
        HOLOGRAM("Hologram Aura", Particle.ENCHANT, 1.3, 0.5),
        LASER("Laser Aura", Particle.END_ROD, 1.1, 0.9),
        PRISM("Prism Aura", Particle.NOTE, 1.8, 0.2),
        MIRROR("Mirror Aura", Particle.ENCHANT, 1.5, 0.0),
        GLASS("Glass Aura", Particle.END_ROD, 1.2, 0.1),
        CHROME("Chrome Aura", Particle.CRIT, 1.4, 0.0),
        DISCO("Disco Aura", Particle.NOTE, 2.0, 0.6),
        STROBE("Strobe Aura", Particle.END_ROD, 1.3, 0.7),
        PULSE("Pulse Aura", Particle.ENCHANT, 1.7, 0.3);
        
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
        
        // Get aura-specific parameters
        AuraPattern pattern = getAuraPattern(auraType);
        int points = pattern.getPoints();
        
        // Create the main aura effect based on pattern type
        switch (pattern.getPatternType()) {
            case CIRCULAR:
                createCircularAura(player, playerLoc, angle, points);
                break;
            case SPIRAL:
                createSpiralAura(player, playerLoc, angle, points);
                break;
            case HELIX:
                createHelixAura(player, playerLoc, angle, points);
                break;
            case WAVE:
                createWaveAura(player, playerLoc, angle, points);
                break;
            case ORBITAL:
                createOrbitalAura(player, playerLoc, angle, points);
                break;
            case PULSE:
                createPulseAura(player, playerLoc, angle, points);
                break;
            case TORNADO:
                createTornadoAura(player, playerLoc, angle, points);
                break;
            case GEOMETRIC:
                createGeometricAura(player, playerLoc, angle, points);
                break;
        }
        
        // Add special central effects
        addCentralEffects(player, playerLoc, angle);
    }
    
    private void createCircularAura(Player player, Location playerLoc, double angle, int points) {
        for (int i = 0; i < points; i++) {
            double currentAngle = angle + (2 * Math.PI * i / points);
            double x = playerLoc.getX() + radius * Math.cos(currentAngle);
            double z = playerLoc.getZ() + radius * Math.sin(currentAngle);
            double y = playerLoc.getY() + 1.0 + height * Math.sin(angle * 2 + i * 0.5);
            
            Location particleLoc = new Location(playerLoc.getWorld(), x, y, z);
            spawnAuraParticle(player, particleLoc, i);
        }
    }
    
    private void createSpiralAura(Player player, Location playerLoc, double angle, int points) {
        for (int i = 0; i < points; i++) {
            double currentAngle = angle + (2 * Math.PI * i / points);
            double spiralRadius = radius * (0.3 + 0.7 * Math.sin(angle * 2 + i * 0.3));
            double x = playerLoc.getX() + spiralRadius * Math.cos(currentAngle);
            double z = playerLoc.getZ() + spiralRadius * Math.sin(currentAngle);
            double y = playerLoc.getY() + 0.5 + (i * 0.1) % 3.0;
            
            Location particleLoc = new Location(playerLoc.getWorld(), x, y, z);
            spawnAuraParticle(player, particleLoc, i);
        }
    }
    
    private void createHelixAura(Player player, Location playerLoc, double angle, int points) {
        // Create double helix pattern
        for (int helix = 0; helix < 2; helix++) {
            for (int i = 0; i < points / 2; i++) {
                double currentAngle = angle + (2 * Math.PI * i / (points / 2)) + (helix * Math.PI);
                double x = playerLoc.getX() + radius * Math.cos(currentAngle);
                double z = playerLoc.getZ() + radius * Math.sin(currentAngle);
                double y = playerLoc.getY() + 0.5 + (i * 0.15) % 2.5;
                
                Location particleLoc = new Location(playerLoc.getWorld(), x, y, z);
                spawnAuraParticle(player, particleLoc, i + helix * points);
            }
        }
    }
    
    private void createWaveAura(Player player, Location playerLoc, double angle, int points) {
        for (int i = 0; i < points; i++) {
            double currentAngle = angle + (2 * Math.PI * i / points);
            double waveRadius = radius + 0.5 * Math.sin(angle * 4 + i * 0.8);
            double x = playerLoc.getX() + waveRadius * Math.cos(currentAngle);
            double z = playerLoc.getZ() + waveRadius * Math.sin(currentAngle);
            double y = playerLoc.getY() + 1.0 + 0.8 * Math.sin(angle * 3 + i * 0.6);
            
            Location particleLoc = new Location(playerLoc.getWorld(), x, y, z);
            spawnAuraParticle(player, particleLoc, i);
        }
    }
    
    private void createOrbitalAura(Player player, Location playerLoc, double angle, int points) {
        // Multiple orbital rings
        for (int ring = 0; ring < 3; ring++) {
            int ringPoints = points / 3;
            double ringRadius = radius * (0.5 + ring * 0.3);
            double ringSpeed = 1.0 + ring * 0.5;
            
            for (int i = 0; i < ringPoints; i++) {
                double currentAngle = angle * ringSpeed + (2 * Math.PI * i / ringPoints);
                double x = playerLoc.getX() + ringRadius * Math.cos(currentAngle);
                double z = playerLoc.getZ() + ringRadius * Math.sin(currentAngle);
                double y = playerLoc.getY() + 1.0 + ring * 0.4;
                
                Location particleLoc = new Location(playerLoc.getWorld(), x, y, z);
                spawnAuraParticle(player, particleLoc, i + ring * ringPoints);
            }
        }
    }
    
    private void createPulseAura(Player player, Location playerLoc, double angle, int points) {
        double pulseRadius = radius * (0.5 + 0.5 * Math.abs(Math.sin(angle * 3)));
        for (int i = 0; i < points; i++) {
            double currentAngle = (2 * Math.PI * i / points);
            double x = playerLoc.getX() + pulseRadius * Math.cos(currentAngle);
            double z = playerLoc.getZ() + pulseRadius * Math.sin(currentAngle);
            double y = playerLoc.getY() + 1.0;
            
            Location particleLoc = new Location(playerLoc.getWorld(), x, y, z);
            spawnAuraParticle(player, particleLoc, i);
        }
    }
    
    private void createTornadoAura(Player player, Location playerLoc, double angle, int points) {
        for (int i = 0; i < points; i++) {
            double height_level = (double) i / points * 3.0;
            double currentAngle = angle * 2 + height_level * 4;
            double tornadoRadius = radius * (1.0 - height_level / 4.0);
            
            double x = playerLoc.getX() + tornadoRadius * Math.cos(currentAngle);
            double z = playerLoc.getZ() + tornadoRadius * Math.sin(currentAngle);
            double y = playerLoc.getY() + 0.2 + height_level;
            
            Location particleLoc = new Location(playerLoc.getWorld(), x, y, z);
            spawnAuraParticle(player, particleLoc, i);
        }
    }
    
    private void createGeometricAura(Player player, Location playerLoc, double angle, int points) {
        // Create geometric shapes (hexagon, pentagon, etc.)
        int sides = getGeometricSides(auraType);
        for (int i = 0; i < sides; i++) {
            double currentAngle = angle + (2 * Math.PI * i / sides);
            double x = playerLoc.getX() + radius * Math.cos(currentAngle);
            double z = playerLoc.getZ() + radius * Math.sin(currentAngle);
            double y = playerLoc.getY() + 1.0;
            
            // Create line between points
            for (int j = 0; j < 8; j++) {
                double progress = (double) j / 7;
                double nextAngle = angle + (2 * Math.PI * (i + 1) / sides);
                double lineX = x + progress * (radius * Math.cos(nextAngle) - x);
                double lineZ = z + progress * (radius * Math.sin(nextAngle) - z);
                
                Location particleLoc = new Location(playerLoc.getWorld(), 
                    playerLoc.getX() + (lineX - playerLoc.getX()), y, 
                    playerLoc.getZ() + (lineZ - playerLoc.getZ()));
                spawnAuraParticle(player, particleLoc, i * 8 + j);
            }
        }
    }
    
    private void spawnAuraParticle(Player player, Location location, int index) {
        switch (auraType) {
            // Original auras with enhanced effects
            case RAINBOW:
                Particle rainbowParticle = getRainbowParticle(index);
                player.getWorld().spawnParticle(rainbowParticle, location, 1, 0, 0, 0, speed);
                break;
                
            case LIGHTNING:
            case THUNDER:
            case PLASMA:
                // Electric effects with random positioning
                double offsetY = (Math.random() - 0.5) * 0.5;
                Location electricLoc = location.clone().add(0, offsetY, 0);
                player.getWorld().spawnParticle(auraParticle, electricLoc, particleCount, 0.1, 0.1, 0.1, speed);
                break;
                
            case GALAXY:
            case NEBULA:
            case COSMIC_DUST:
                // Cosmic swirling effects
                for (int i = 0; i < 3; i++) {
                    Location cosmicLoc = location.clone().add(
                        (Math.random() - 0.5) * 0.3,
                        (Math.random() - 0.5) * 0.2,
                        (Math.random() - 0.5) * 0.3
                    );
                    player.getWorld().spawnParticle(auraParticle, cosmicLoc, 1, 0.02, 0.02, 0.02, speed);
                }
                break;
                
            case MATRIX:
            case NEON:
            case HOLOGRAM:
                // Digital/tech effects
                if (Math.random() < 0.7) {
                    player.getWorld().spawnParticle(auraParticle, location, 1, 0, 0, 0, 0);
                }
                break;
                
            case DISCO:
            case STROBE:
                // Flashing effects
                if ((System.currentTimeMillis() / 100 + index) % 3 == 0) {
                    player.getWorld().spawnParticle(auraParticle, location, particleCount, 0.05, 0.05, 0.05, speed);
                }
                break;
                
            default:
                // Standard particle effect
                player.getWorld().spawnParticle(auraParticle, location, particleCount, 0.05, 0.05, 0.05, speed);
                break;
        }
    }
    
    private void addCentralEffects(Player player, Location playerLoc, double angle) {
        Location centerLoc = playerLoc.clone().add(0, 1.0, 0);
        
        switch (auraType) {
            case HOLY:
            case DIVINE:
            case ANGELIC:
                // Pillar of light
                for (int i = 0; i < 5; i++) {
                    Location pillarLoc = centerLoc.clone().add(0, i * 0.3, 0);
                    player.getWorld().spawnParticle(auraParticle, pillarLoc, 1, 0.05, 0.05, 0.05, 0.02);
                }
                break;
                
            case BLACKHOLE:
            case WORMHOLE:
                // Swirling center
                for (int i = 0; i < 8; i++) {
                    double centerAngle = angle * 4 + (2 * Math.PI * i / 8);
                    double centerRadius = 0.3 * Math.sin(angle * 2);
                    double x = centerLoc.getX() + centerRadius * Math.cos(centerAngle);
                    double z = centerLoc.getZ() + centerRadius * Math.sin(centerAngle);
                    Location swirLoc = new Location(centerLoc.getWorld(), x, centerLoc.getY(), z);
                    player.getWorld().spawnParticle(auraParticle, swirLoc, 1, 0, 0, 0, 0);
                }
                break;
                
            case SUPERNOVA:
                // Pulsing center
                if (Math.sin(angle * 5) > 0.8) {
                    player.getWorld().spawnParticle(auraParticle, centerLoc, 10, 0.2, 0.2, 0.2, 0.1);
                }
                break;
        }
    }
    
    private Particle getRainbowParticle(int index) {
        Particle[] rainbowParticles = {
            Particle.FLAME, Particle.LAVA, Particle.ENCHANT, 
            Particle.HAPPY_VILLAGER, Particle.ENCHANT, Particle.PORTAL
        };
        return rainbowParticles[index % rainbowParticles.length];
    }
    
    private int getGeometricSides(AuraType type) {
        switch (type) {
            case CRYSTAL:
            case DIAMOND: return 6; // Hexagon
            case RUNIC: return 5;   // Pentagon
            case ARCANE: return 8;  // Octagon
            default: return 6;
        }
    }
    
    private AuraPattern getAuraPattern(AuraType type) {
        switch (type) {
            // Spiral patterns
            case TORNADO:
            case VOID:
            case WORMHOLE:
                return new AuraPattern(PatternType.TORNADO, 24);
                
            case GALAXY:
            case NEBULA:
            case COSMIC_DUST:
                return new AuraPattern(PatternType.SPIRAL, 20);
                
            // Helix patterns
            case TEMPORAL:
            case MATRIX:
                return new AuraPattern(PatternType.HELIX, 16);
                
            // Wave patterns
            case WATER:
            case OCEAN:
            case TSUNAMI:
                return new AuraPattern(PatternType.WAVE, 18);
                
            // Orbital patterns
            case SOLAR:
            case LUNAR:
                return new AuraPattern(PatternType.ORBITAL, 15);
                
            // Pulse patterns
            case PULSE:
            case STROBE:
                return new AuraPattern(PatternType.PULSE, 12);
                
            // Geometric patterns
            case CRYSTAL:
            case DIAMOND:
            case RUNIC:
            case ARCANE:
                return new AuraPattern(PatternType.GEOMETRIC, 24);
                
            // Default circular
            default:
                return new AuraPattern(PatternType.CIRCULAR, 16);
        }
    }
    
    private enum PatternType {
        CIRCULAR, SPIRAL, HELIX, WAVE, ORBITAL, PULSE, TORNADO, GEOMETRIC
    }
    
    private static class AuraPattern {
        private final PatternType patternType;
        private final int points;
        
        public AuraPattern(PatternType patternType, int points) {
            this.patternType = patternType;
            this.points = points;
        }
        
        public PatternType getPatternType() { return patternType; }
        public int getPoints() { return points; }
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
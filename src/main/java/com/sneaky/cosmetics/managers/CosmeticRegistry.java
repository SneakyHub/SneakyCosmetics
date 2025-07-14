package com.sneaky.cosmetics.managers;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles registration and storage of all cosmetics
 */
public class CosmeticRegistry {
    
    private final SneakyCosmetics plugin;
    private final Map<String, Cosmetic> cosmetics = new ConcurrentHashMap<>();
    private final Map<CosmeticType, List<Cosmetic>> cosmeticsByType = new ConcurrentHashMap<>();
    
    public CosmeticRegistry(SneakyCosmetics plugin) {
        this.plugin = plugin;
        initializeCosmeticTypes();
    }
    
    private void initializeCosmeticTypes() {
        for (CosmeticType type : CosmeticType.values()) {
            cosmeticsByType.put(type, new ArrayList<>());
        }
    }
    
    /**
     * Register all cosmetics from all types
     */
    public void registerAllCosmetics() {
        plugin.getLogger().info("Registering cosmetics...");
        
        registerParticleCosmetics();
        registerHatCosmetics();
        registerTrailCosmetics();
        registerPetCosmetics();
        registerGadgetCosmetics();
        registerWingCosmetics();
        registerAuraCosmetics();
        
        plugin.getLogger().info("Cosmetic registration complete. Registered " + getTotalCosmetics() + " cosmetics!");
    }
    
    /**
     * Register a single cosmetic
     */
    public void registerCosmetic(Cosmetic cosmetic) {
        cosmetics.put(cosmetic.getId(), cosmetic);
        cosmeticsByType.get(cosmetic.getType()).add(cosmetic);
        plugin.getLogger().fine("Registered cosmetic: " + cosmetic.getId());
    }
    
    // Getters
    public Cosmetic getCosmetic(String id) {
        return cosmetics.get(id);
    }
    
    public List<Cosmetic> getCosmeticsByType(CosmeticType type) {
        return new ArrayList<>(cosmeticsByType.getOrDefault(type, new ArrayList<>()));
    }
    
    public Collection<Cosmetic> getAllCosmetics() {
        return cosmetics.values();
    }
    
    public Set<CosmeticType> getCosmeticTypes() {
        return cosmeticsByType.keySet();
    }
    
    public int getTotalCosmetics() {
        return cosmetics.size();
    }
    
    public int getCosmeticCountByType(CosmeticType type) {
        return cosmeticsByType.getOrDefault(type, new ArrayList<>()).size();
    }
    
    public void clear() {
        cosmetics.clear();
        for (List<Cosmetic> list : cosmeticsByType.values()) {
            list.clear();
        }
    }
    
    // Registration methods for each cosmetic type
    private void registerParticleCosmetics() {
        // Import particle cosmetics
        try {
            Class.forName("com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic");
            
            // Hearts particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic heartsParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_hearts", "Heart Particles", 0, org.bukkit.Material.RED_DYE,
                java.util.Arrays.asList("§7Surround yourself with", "§7lovely heart particles!"),
                null, false, false,
                org.bukkit.Particle.HEART, 3, 0.5, 0.5, 0.5, 0.1, plugin
            );
            registerCosmetic(heartsParticle);
            
            // Flame particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic flameParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_flame", "Flame Particles", 100, org.bukkit.Material.BLAZE_POWDER,
                java.util.Arrays.asList("§7Burn bright with", "§7fiery flame particles!"),
                null, false, false,
                org.bukkit.Particle.FLAME, 5, 0.3, 0.3, 0.3, 0.05, plugin
            );
            registerCosmetic(flameParticle);
            
            // Magic particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic magicParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_magic", "Magic Particles", 250, org.bukkit.Material.NETHER_STAR,
                java.util.Arrays.asList("§7Cast spells with", "§7mystical magic particles!"),
                null, false, true,
                org.bukkit.Particle.ENCHANT, 8, 0.8, 0.8, 0.8, 0.2, plugin
            );
            registerCosmetic(magicParticle);
            
            // Note particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic noteParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_note", "Musical Notes", 150, org.bukkit.Material.NOTE_BLOCK,
                java.util.Arrays.asList("§7Dance to the rhythm with", "§7colorful musical notes!"),
                null, false, false,
                org.bukkit.Particle.NOTE, 4, 0.6, 0.6, 0.6, 0.1, plugin
            );
            registerCosmetic(noteParticle);
            
            // Villager happy particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic happyParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_happy", "Happy Vibes", 75, org.bukkit.Material.EMERALD,
                java.util.Arrays.asList("§7Spread joy with", "§7happy villager particles!"),
                null, false, false,
                org.bukkit.Particle.HAPPY_VILLAGER, 6, 0.4, 0.4, 0.4, 0.15, plugin
            );
            registerCosmetic(happyParticle);
            
            // Smoke particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic smokeParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_smoke", "Smoky Aura", 80, org.bukkit.Material.COAL,
                java.util.Arrays.asList("§7Mysterious and dark", "§7smoke surrounds you!"),
                null, false, false,
                org.bukkit.Particle.LARGE_SMOKE, 4, 0.3, 0.8, 0.3, 0.08, plugin
            );
            registerCosmetic(smokeParticle);
            
            // Water splash particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic splashParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_splash", "Water Splash", 120, org.bukkit.Material.WATER_BUCKET,
                java.util.Arrays.asList("§7Refreshing water splashes", "§7keep you cool!"),
                null, false, false,
                org.bukkit.Particle.SPLASH, 8, 0.5, 0.3, 0.5, 0.2, plugin
            );
            registerCosmetic(splashParticle);
            
            // Portal particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic portalParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_portal", "Portal Energy", 200, org.bukkit.Material.OBSIDIAN,
                java.util.Arrays.asList("§7Interdimensional portal", "§7energy flows around you!"),
                null, false, false,
                org.bukkit.Particle.PORTAL, 10, 0.6, 1.0, 0.6, 0.3, plugin
            );
            registerCosmetic(portalParticle);
            
            // Critical hit particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic critParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_crit", "Critical Strike", 180, org.bukkit.Material.IRON_SWORD,
                java.util.Arrays.asList("§7Show your combat prowess", "§7with critical hit particles!"),
                null, false, false,
                org.bukkit.Particle.CRIT, 6, 0.4, 0.4, 0.4, 0.1, plugin
            );
            registerCosmetic(critParticle);
            
            // Snow particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic snowParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_snow", "Snowflakes", 90, org.bukkit.Material.SNOWBALL,
                java.util.Arrays.asList("§7Gentle snowflakes fall", "§7around you gracefully!"),
                null, false, false,
                org.bukkit.Particle.ITEM_SNOWBALL, 5, 0.8, 1.2, 0.8, 0.1, plugin
            );
            registerCosmetic(snowParticle);
            
            // Slime particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic slimeParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_slime", "Slime Bounce", 110, org.bukkit.Material.SLIME_BALL,
                java.util.Arrays.asList("§7Bouncy slime particles", "§7add some fun to your step!"),
                null, false, false,
                org.bukkit.Particle.ITEM_SLIME, 4, 0.4, 0.4, 0.4, 0.1, plugin
            );
            registerCosmetic(slimeParticle);
            
            // Witch spell particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic witchParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_witch", "Witch Magic", 300, org.bukkit.Material.BREWING_STAND,
                java.util.Arrays.asList("§7Dark witch magic", "§7swirls around you!"),
                null, true, false,
                org.bukkit.Particle.SMOKE, 7, 0.5, 0.5, 0.5, 0.15, plugin
            );
            registerCosmetic(witchParticle);
            
            // Drip water particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic dripParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_drip", "Water Drips", 60, org.bukkit.Material.BLUE_DYE,
                java.util.Arrays.asList("§7Gentle water drops", "§7drip from above you!"),
                null, false, false,
                org.bukkit.Particle.DRIPPING_WATER, 3, 0.3, 1.5, 0.3, 0.05, plugin
            );
            registerCosmetic(dripParticle);
            
            // Firework particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic fireworkParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_firework", "Fireworks", 350, org.bukkit.Material.FIREWORK_ROCKET,
                java.util.Arrays.asList("§7Celebrate with", "§7spectacular firework bursts!"),
                null, true, false,
                org.bukkit.Particle.FIREWORK, 8, 0.6, 0.6, 0.6, 0.2, plugin
            );
            registerCosmetic(fireworkParticle);
            
            // Redstone particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic redstoneParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_redstone", "Redstone Power", 130, org.bukkit.Material.REDSTONE,
                java.util.Arrays.asList("§7Electric redstone energy", "§7sparks around you!"),
                null, false, false,
                org.bukkit.Particle.DUST, 6, 0.4, 0.4, 0.4, 0.1, plugin
            );
            registerCosmetic(redstoneParticle);
            
            // Cloud particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic cloudParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_cloud", "Fluffy Clouds", 140, org.bukkit.Material.WHITE_WOOL,
                java.util.Arrays.asList("§7Fluffy white clouds", "§7float around you peacefully!"),
                null, false, false,
                org.bukkit.Particle.CLOUD, 5, 0.7, 0.5, 0.7, 0.05, plugin
            );
            registerCosmetic(cloudParticle);
            
            // Lava drip particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic lavaParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_lava", "Lava Drips", 220, org.bukkit.Material.LAVA_BUCKET,
                java.util.Arrays.asList("§7Hot lava drips", "§7sizzle around you!"),
                null, false, true,
                org.bukkit.Particle.DRIPPING_LAVA, 4, 0.3, 1.2, 0.3, 0.08, plugin
            );
            registerCosmetic(lavaParticle);
            
            // Explosion particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic explosionParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_explosion", "Explosive Energy", 400, org.bukkit.Material.TNT,
                java.util.Arrays.asList("§7Controlled explosions", "§7burst around you!"),
                "sneakycosmetics.premium", true, true,
                org.bukkit.Particle.EXPLOSION, 2, 0.5, 0.5, 0.5, 0.1, plugin
            );
            registerCosmetic(explosionParticle);
            
            // Totem particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic totemParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_totem", "Totem Blessing", 500, org.bukkit.Material.TOTEM_OF_UNDYING,
                java.util.Arrays.asList("§7Divine totem energy", "§7protects and blesses you!"),
                "sneakycosmetics.premium", true, true,
                org.bukkit.Particle.TOTEM_OF_UNDYING, 6, 0.5, 0.8, 0.5, 0.2, plugin
            );
            registerCosmetic(totemParticle);
            
            // End rod particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic endRodParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_end_rod", "End Rod Energy", 280, org.bukkit.Material.END_ROD,
                java.util.Arrays.asList("§7Mystical End Rod energy", "§7glows around you!"),
                null, false, true,
                org.bukkit.Particle.END_ROD, 4, 0.4, 0.6, 0.4, 0.1, plugin
            );
            registerCosmetic(endRodParticle);
            
            // Damage indicator particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic damageParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_damage", "Damage Aura", 190, org.bukkit.Material.IRON_AXE,
                java.util.Arrays.asList("§7Show your battle scars", "§7with damage indicators!"),
                null, false, false,
                org.bukkit.Particle.DAMAGE_INDICATOR, 3, 0.3, 0.3, 0.3, 0.1, plugin
            );
            registerCosmetic(damageParticle);
            
            // Barrier particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic barrierParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_barrier", "Protective Barrier", 450, org.bukkit.Material.BARRIER,
                java.util.Arrays.asList("§7An invisible barrier", "§7protects you from harm!"),
                "sneakycosmetics.admin", true, true,
                org.bukkit.Particle.BLOCK_MARKER, 1, 0.2, 0.2, 0.2, 0.05, plugin
            );
            registerCosmetic(barrierParticle);
            
            // Nautilus particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic nautilusParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_nautilus", "Ocean Currents", 320, org.bukkit.Material.NAUTILUS_SHELL,
                java.util.Arrays.asList("§7Ocean currents flow", "§7gracefully around you!"),
                null, false, true,
                org.bukkit.Particle.NAUTILUS, 5, 0.6, 0.6, 0.6, 0.15, plugin
            );
            registerCosmetic(nautilusParticle);
            
            // Dolphin particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic dolphinParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_dolphin", "Dolphin Grace", 250, org.bukkit.Material.DOLPHIN_SPAWN_EGG,
                java.util.Arrays.asList("§7Playful dolphin energy", "§7swims around you!"),
                null, false, false,
                org.bukkit.Particle.DOLPHIN, 4, 0.5, 0.3, 0.5, 0.1, plugin
            );
            registerCosmetic(dolphinParticle);
            
            // Advanced Pattern Particles
            Class.forName("com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic");
            
            // Circle pattern
            com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic circleParticle = new com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic(
                "particle_circle", "Magic Circle", 600, org.bukkit.Material.ENDER_EYE,
                java.util.Arrays.asList("§7A mystical circle of", "§7enchanted particles orbits you!"),
                null, true, true,
                org.bukkit.Particle.ENCHANT, 1, 0.0, 
                com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic.ParticlePattern.CIRCLE, plugin
            );
            registerCosmetic(circleParticle);
            
            // Helix pattern
            com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic helixParticle = new com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic(
                "particle_helix", "DNA Helix", 800, org.bukkit.Material.PRISMARINE_CRYSTALS,
                java.util.Arrays.asList("§7A beautiful DNA helix", "§7spirals around your body!"),
                "sneakycosmetics.premium", true, true,
                org.bukkit.Particle.HAPPY_VILLAGER, 1, 0.0,
                com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic.ParticlePattern.HELIX, plugin
            );
            registerCosmetic(helixParticle);
            
            // Wave pattern
            com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic waveParticle = new com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic(
                "particle_wave", "Energy Waves", 700, org.bukkit.Material.CYAN_DYE,
                java.util.Arrays.asList("§7Pulsing energy waves", "§7ripple outward from you!"),
                null, true, false,
                org.bukkit.Particle.ENCHANTED_HIT, 1, 0.0,
                com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic.ParticlePattern.WAVE, plugin
            );
            registerCosmetic(waveParticle);
            
            // Tornado pattern
            com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic tornadoParticle = new com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic(
                "particle_tornado", "Wind Tornado", 900, org.bukkit.Material.FEATHER,
                java.util.Arrays.asList("§7A powerful tornado of", "§7wind swirls around you!"),
                "sneakycosmetics.premium", true, true,
                org.bukkit.Particle.CLOUD, 1, 0.1,
                com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic.ParticlePattern.TORNADO, plugin
            );
            registerCosmetic(tornadoParticle);
            
            // Heart pattern
            com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic heartParticle = new com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic(
                "particle_heart_shape", "Love Heart", 750, org.bukkit.Material.PINK_DYE,
                java.util.Arrays.asList("§7Show your love with", "§7a beautiful heart shape!"),
                null, false, true,
                org.bukkit.Particle.HEART, 1, 0.0,
                com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic.ParticlePattern.HEART, plugin
            );
            registerCosmetic(heartParticle);
            
            // Star pattern
            com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic starParticle = new com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic(
                "particle_star_shape", "Shining Star", 850, org.bukkit.Material.NETHER_STAR,
                java.util.Arrays.asList("§7Shine bright like", "§7a magnificent star!"),
                "sneakycosmetics.premium", true, true,
                org.bukkit.Particle.FIREWORK, 1, 0.0,
                com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic.ParticlePattern.STAR, plugin
            );
            registerCosmetic(starParticle);
            
            // Galaxy pattern
            com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic galaxyParticle = new com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic(
                "particle_galaxy", "Cosmic Galaxy", 1200, org.bukkit.Material.END_CRYSTAL,
                java.util.Arrays.asList("§7A miniature galaxy", "§7orbits around you majestically!"),
                "sneakycosmetics.premium", true, true,
                org.bukkit.Particle.PORTAL, 1, 0.05,
                com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic.ParticlePattern.GALAXY, plugin
            );
            registerCosmetic(galaxyParticle);
            
            // Spiral pattern
            com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic spiralParticle = new com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic(
                "particle_spiral", "Golden Spiral", 650, org.bukkit.Material.GOLD_INGOT,
                java.util.Arrays.asList("§7A golden spiral", "§7elegantly flows around you!"),
                null, false, true,
                org.bukkit.Particle.CRIT, 1, 0.0,
                com.sneaky.cosmetics.cosmetics.particles.AdvancedParticleCosmetic.ParticlePattern.SPIRAL, plugin
            );
            registerCosmetic(spiralParticle);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to register particle cosmetics: " + e.getMessage());
        }
    }
    
    private void registerHatCosmetics() {
        // TODO: Move hat registration logic here
        plugin.getLogger().fine("Hat cosmetics registration (to be implemented)");
    }
    
    private void registerTrailCosmetics() {
        // TODO: Move trail registration logic here
        plugin.getLogger().fine("Trail cosmetics registration (to be implemented)");
    }
    
    private void registerPetCosmetics() {
        // TODO: Move pet registration logic here  
        plugin.getLogger().fine("Pet cosmetics registration (to be implemented)");
    }
    
    private void registerGadgetCosmetics() {
        // TODO: Move gadget registration logic here
        plugin.getLogger().fine("Gadget cosmetics registration (to be implemented)");
    }
    
    private void registerWingCosmetics() {
        // TODO: Move wing registration logic here
        plugin.getLogger().fine("Wing cosmetics registration (to be implemented)");
    }
    
    private void registerAuraCosmetics() {
        // TODO: Move aura registration logic here
        plugin.getLogger().fine("Aura cosmetics registration (to be implemented)");
    }
}
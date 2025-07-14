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
        try {
            Class.forName("com.sneaky.cosmetics.cosmetics.hats.HatCosmetic");
            
            // Basic Hat
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic basicHat = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_basic", "Basic Hat", 0, Material.LEATHER_HELMET,
                java.util.Arrays.asList("§7A simple leather hat", "§7for everyday wear!"),
                null, false, false,
                Material.LEATHER_HELMET, "§7Basic Hat", java.util.Arrays.asList("§7Simple and comfortable")
            );
            registerCosmetic(basicHat);
            
            // Iron Helmet
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic ironHat = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_iron", "Iron Helmet", 150, Material.IRON_HELMET,
                java.util.Arrays.asList("§7A sturdy iron helmet", "§7for protection!"),
                null, false, false,
                Material.IRON_HELMET, "§8Iron Helmet", java.util.Arrays.asList("§8Strong and durable")
            );
            registerCosmetic(ironHat);
            
            // Diamond Crown
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic diamondCrown = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_diamond", "Diamond Crown", 500, Material.DIAMOND_HELMET,
                java.util.Arrays.asList("§7A magnificent diamond crown", "§7fit for royalty!"),
                null, false, true,
                Material.DIAMOND_HELMET, "§bDiamond Crown", java.util.Arrays.asList("§b✧ Royal Quality ✧")
            );
            registerCosmetic(diamondCrown);
            
            // Golden Cap
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic goldenCap = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_golden", "Golden Cap", 300, Material.GOLDEN_HELMET,
                java.util.Arrays.asList("§7A shiny golden cap", "§7that gleams in the light!"),
                null, false, false,
                Material.GOLDEN_HELMET, "§6Golden Cap", java.util.Arrays.asList("§6✦ Shimmering ✦")
            );
            registerCosmetic(goldenCap);
            
            // Netherite Helm
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic netheriteHelm = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_netherite", "Netherite Helm", 1000, Material.NETHERITE_HELMET,
                java.util.Arrays.asList("§7The ultimate helmet", "§7forged in the depths of the Nether!"),
                "sneakycosmetics.premium", true, true,
                Material.NETHERITE_HELMET, "§4Netherite Helm", java.util.Arrays.asList("§4❖ Legendary ❖")
            );
            registerCosmetic(netheriteHelm);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to register hat cosmetics: " + e.getMessage());
        }
    }
    
    private void registerTrailCosmetics() {
        try {
            Class.forName("com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic");
            
            // Fire Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic fireTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_fire", "Fire Trail", 200, Material.FIRE_CHARGE,
                java.util.Arrays.asList("§7Leave a blazing trail", "§7of fire behind you!"),
                null, false, false,
                Particle.FLAME, 3, 0.2, 0.2, 0.2, 0.05, plugin
            );
            registerCosmetic(fireTrail);
            
            // Water Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic waterTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_water", "Water Trail", 150, Material.WATER_BUCKET,
                java.util.Arrays.asList("§7Cool water droplets", "§7follow your path!"),
                null, false, false,
                Particle.SPLASH, 5, 0.3, 0.1, 0.3, 0.1, plugin
            );
            registerCosmetic(waterTrail);
            
            // Magic Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic magicTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_magic", "Magic Trail", 350, Material.ENCHANTING_TABLE,
                java.util.Arrays.asList("§7Mystical enchantment particles", "§7mark your journey!"),
                null, false, true,
                Particle.ENCHANT, 8, 0.4, 0.4, 0.4, 0.2, plugin
            );
            registerCosmetic(magicTrail);
            
            // Heart Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic heartTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_hearts", "Heart Trail", 250, Material.PINK_DYE,
                java.util.Arrays.asList("§7Spread love with every step", "§7you take!"),
                null, false, false,
                Particle.HEART, 2, 0.3, 0.2, 0.3, 0.1, plugin
            );
            registerCosmetic(heartTrail);
            
            // End Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic endTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_end", "End Portal Trail", 600, Material.END_PORTAL_FRAME,
                java.util.Arrays.asList("§7Mysterious End dimension", "§7energy follows you!"),
                "sneakycosmetics.premium", true, true,
                Particle.PORTAL, 6, 0.4, 0.6, 0.4, 0.3, plugin
            );
            registerCosmetic(endTrail);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to register trail cosmetics: " + e.getMessage());
        }
    }
    
    private void registerPetCosmetics() {
        try {
            Class.forName("com.sneaky.cosmetics.cosmetics.pets.PetCosmetic");
            
            // === FARM ANIMALS ===
            
            // Wolf Pets
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic wolfPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_wolf", "Loyal Wolf", 200, Material.BONE,
                java.util.Arrays.asList("§7A faithful wolf companion", "§7that follows you everywhere!"),
                null, false, false,
                org.bukkit.entity.EntityType.WOLF, false, "§6Loyal Wolf", plugin
            );
            registerCosmetic(wolfPet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic babyWolfPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_baby_wolf", "Wolf Pup", 300, Material.BONE,
                java.util.Arrays.asList("§7An adorable wolf pup", "§7that yips and follows you!"),
                null, false, false,
                org.bukkit.entity.EntityType.WOLF, true, "§6Wolf Pup", plugin
            );
            registerCosmetic(babyWolfPet);
            
            // Cat Pets
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic catPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_cat", "Cute Cat", 180, Material.TROPICAL_FISH,
                java.util.Arrays.asList("§7An adorable feline friend", "§7with a purr for you!"),
                null, false, false,
                org.bukkit.entity.EntityType.CAT, false, "§eSweet Cat", plugin
            );
            registerCosmetic(catPet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic kittentPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_kitten", "Playful Kitten", 250, Material.TROPICAL_FISH,
                java.util.Arrays.asList("§7A tiny kitten that", "§7meows and plays around you!"),
                null, false, false,
                org.bukkit.entity.EntityType.CAT, true, "§ePlayful Kitten", plugin
            );
            registerCosmetic(kittentPet);
            
            // Pig Pets
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic pigPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_pig", "Pink Pig", 120, Material.PORKCHOP,
                java.util.Arrays.asList("§7A cute pink pig", "§7that oinks happily!"),
                null, false, false,
                org.bukkit.entity.EntityType.PIG, false, "§dPinky", plugin
            );
            registerCosmetic(pigPet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic pigletPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_piglet", "Little Piglet", 180, Material.PORKCHOP,
                java.util.Arrays.asList("§7An adorable piglet", "§7that squeaks cutely!"),
                null, false, false,
                org.bukkit.entity.EntityType.PIG, true, "§dLittle Piglet", plugin
            );
            registerCosmetic(pigletPet);
            
            // Chicken Pets
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic chickenPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_chicken", "Clucky Chicken", 100, Material.EGG,
                java.util.Arrays.asList("§7A feathered friend that", "§7clucks and follows you around!"),
                null, false, false,
                org.bukkit.entity.EntityType.CHICKEN, false, "§fClucky", plugin
            );
            registerCosmetic(chickenPet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic chickPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_chick", "Baby Chick", 150, Material.EGG,
                java.util.Arrays.asList("§7A tiny fluffy chick", "§7that peeps adorably!"),
                null, false, false,
                org.bukkit.entity.EntityType.CHICKEN, true, "§fBaby Chick", plugin
            );
            registerCosmetic(chickPet);
            
            // Cow Pets
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic cowPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_cow", "Friendly Cow", 140, Material.MILK_BUCKET,
                java.util.Arrays.asList("§7A gentle cow that", "§7moos peacefully!"),
                null, false, false,
                org.bukkit.entity.EntityType.COW, false, "§fFriendly Cow", plugin
            );
            registerCosmetic(cowPet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic calfPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_calf", "Little Calf", 200, Material.MILK_BUCKET,
                java.util.Arrays.asList("§7An adorable calf", "§7that follows you around!"),
                null, false, false,
                org.bukkit.entity.EntityType.COW, true, "§fLittle Calf", plugin
            );
            registerCosmetic(calfPet);
            
            // Sheep Pets
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic sheepPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_sheep", "Fluffy Sheep", 120, Material.WHITE_WOOL,
                java.util.Arrays.asList("§7A fluffy sheep that", "§7bleats softly!"),
                null, false, false,
                org.bukkit.entity.EntityType.SHEEP, false, "§fFluffy Sheep", plugin
            );
            registerCosmetic(sheepPet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic lambPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_lamb", "Cute Lamb", 180, Material.WHITE_WOOL,
                java.util.Arrays.asList("§7A tiny lamb that", "§7hops around playfully!"),
                null, false, false,
                org.bukkit.entity.EntityType.SHEEP, true, "§fCute Lamb", plugin
            );
            registerCosmetic(lambPet);
            
            // === OCEAN PETS ===
            
            // Dolphin Pets
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic dolphinPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_dolphin", "Playful Dolphin", 400, Material.DOLPHIN_SPAWN_EGG,
                java.util.Arrays.asList("§7A intelligent dolphin", "§7that swims gracefully!"),
                null, false, false,
                org.bukkit.entity.EntityType.DOLPHIN, false, "§bPlayful Dolphin", plugin
            );
            registerCosmetic(dolphinPet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic babyDolphinPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_baby_dolphin", "Dolphin Calf", 500, Material.DOLPHIN_SPAWN_EGG,
                java.util.Arrays.asList("§7A young dolphin that", "§7clicks and splashes!"),
                null, false, false,
                org.bukkit.entity.EntityType.DOLPHIN, true, "§bDolphin Calf", plugin
            );
            registerCosmetic(babyDolphinPet);
            
            // Turtle Pets
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic turtlePet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_turtle", "Sea Turtle", 350, Material.TURTLE_EGG,
                java.util.Arrays.asList("§7A wise sea turtle", "§7that moves slowly but surely!"),
                null, false, false,
                org.bukkit.entity.EntityType.TURTLE, false, "§2Sea Turtle", plugin
            );
            registerCosmetic(turtlePet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic babyTurtlePet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_baby_turtle", "Baby Turtle", 450, Material.TURTLE_EGG,
                java.util.Arrays.asList("§7A tiny turtle that", "§7waddles adorably!"),
                null, false, false,
                org.bukkit.entity.EntityType.TURTLE, true, "§2Baby Turtle", plugin
            );
            registerCosmetic(babyTurtlePet);
            
            // Squid Pets
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic squidPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_squid", "Friendly Squid", 300, Material.INK_SAC,
                java.util.Arrays.asList("§7A mysterious squid", "§7that floats around you!"),
                null, false, false,
                org.bukkit.entity.EntityType.SQUID, false, "§8Friendly Squid", plugin
            );
            registerCosmetic(squidPet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic babySquidPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_baby_squid", "Little Squid", 400, Material.INK_SAC,
                java.util.Arrays.asList("§7A tiny squid that", "§7swims in circles!"),
                null, false, false,
                org.bukkit.entity.EntityType.SQUID, true, "§8Little Squid", plugin
            );
            registerCosmetic(babySquidPet);
            
            // === EXOTIC PETS ===
            
            // Panda Pets
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic pandaPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_panda", "Gentle Panda", 600, Material.BAMBOO,
                java.util.Arrays.asList("§7A peaceful panda", "§7that loves bamboo!"),
                null, false, true,
                org.bukkit.entity.EntityType.PANDA, false, "§f§lGentle Panda", plugin
            );
            registerCosmetic(pandaPet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic babyPandaPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_baby_panda", "Panda Cub", 750, Material.BAMBOO,
                java.util.Arrays.asList("§7An adorable panda cub", "§7that tumbles around!"),
                null, false, true,
                org.bukkit.entity.EntityType.PANDA, true, "§f§lPanda Cub", plugin
            );
            registerCosmetic(babyPandaPet);
            
            // Fox Pets
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic foxPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_fox", "Clever Fox", 450, Material.SWEET_BERRIES,
                java.util.Arrays.asList("§7A cunning fox", "§7that's quick and agile!"),
                null, false, false,
                org.bukkit.entity.EntityType.FOX, false, "§6Clever Fox", plugin
            );
            registerCosmetic(foxPet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic babyFoxPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_baby_fox", "Fox Kit", 550, Material.SWEET_BERRIES,
                java.util.Arrays.asList("§7A playful fox kit", "§7that pounces on everything!"),
                null, false, false,
                org.bukkit.entity.EntityType.FOX, true, "§6Fox Kit", plugin
            );
            registerCosmetic(babyFoxPet);
            
            // Bee Pets
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic beePet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_bee", "Busy Bee", 300, Material.HONEYCOMB,
                java.util.Arrays.asList("§7A hardworking bee", "§7that buzzes around you!"),
                null, false, false,
                org.bukkit.entity.EntityType.BEE, false, "§eBusy Bee", plugin
            );
            registerCosmetic(beePet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic babyBeePet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_baby_bee", "Little Bee", 400, Material.HONEYCOMB,
                java.util.Arrays.asList("§7A tiny bee that", "§7makes cute buzzing sounds!"),
                null, false, false,
                org.bukkit.entity.EntityType.BEE, true, "§eLittle Bee", plugin
            );
            registerCosmetic(babyBeePet);
            
            // === LEGENDARY PETS ===
            
            // Dragon Pet
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic dragonPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_dragon", "Ender Dragon", 2000, Material.DRAGON_EGG,
                java.util.Arrays.asList("§7A mighty Ender Dragon", "§7as your personal companion!"),
                "sneakycosmetics.premium", true, true,
                org.bukkit.entity.EntityType.ENDER_DRAGON, false, "§5§lEnder Dragon", plugin
            );
            registerCosmetic(dragonPet);
            
            // Wither Pet
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic witherPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_wither", "Tamed Wither", 2500, Material.WITHER_SKELETON_SKULL,
                java.util.Arrays.asList("§7A powerful Wither", "§7under your control!"),
                "sneakycosmetics.premium", true, true,
                org.bukkit.entity.EntityType.WITHER, false, "§8§lTamed Wither", plugin
            );
            registerCosmetic(witherPet);
            
            // Warden Pet
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic wardenPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_warden", "Guardian Warden", 3000, Material.SCULK_CATALYST,
                java.util.Arrays.asList("§7An ancient Warden", "§7protecting you from harm!"),
                "sneakycosmetics.premium", true, true,
                org.bukkit.entity.EntityType.WARDEN, false, "§1§lGuardian Warden", plugin
            );
            registerCosmetic(wardenPet);
            
            // Iron Golem Pet
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic golemPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_golem", "Iron Guardian", 800, Material.IRON_BLOCK,
                java.util.Arrays.asList("§7A loyal iron golem", "§7that protects you!"),
                null, false, true,
                org.bukkit.entity.EntityType.IRON_GOLEM, false, "§7§lIron Guardian", plugin
            );
            registerCosmetic(golemPet);
            
            // Snow Golem Pet
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic snowGolemPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_snow_golem", "Frosty Friend", 400, Material.SNOWBALL,
                java.util.Arrays.asList("§7A cheerful snow golem", "§7that throws snowballs!"),
                null, false, false,
                org.bukkit.entity.EntityType.SNOW_GOLEM, false, "§f§lFrosty Friend", plugin
            );
            registerCosmetic(snowGolemPet);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to register pet cosmetics: " + e.getMessage());
        }
    }
    
    private void registerGadgetCosmetics() {
        try {
            Class.forName("com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic");
            
            // Grappling Hook
            com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic grapplingHook = new com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic(
                "gadget_grappling", "Grappling Hook", 400, Material.FISHING_ROD,
                java.util.Arrays.asList("§7Swing through the air", "§7with this amazing grappling hook!"),
                null, false, false,
                Material.FISHING_ROD, "§2Grappling Hook", java.util.Arrays.asList("§2Right-click to swing!"),
                com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic.GadgetType.GRAPPLING_HOOK
            );
            registerCosmetic(grapplingHook);
            
            // Firework Launcher
            com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic fireworkLauncher = new com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic(
                "gadget_firework", "Firework Launcher", 600, Material.FIREWORK_ROCKET,
                java.util.Arrays.asList("§7Launch spectacular fireworks", "§7into the sky!"),
                null, false, true,
                Material.FIREWORK_ROCKET, "§cFirework Launcher", java.util.Arrays.asList("§cRight-click to launch!"),
                com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic.GadgetType.FIREWORK_LAUNCHER
            );
            registerCosmetic(fireworkLauncher);
            
            // Paint Brush
            com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic paintBrush = new com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic(
                "gadget_paint", "Paint Brush", 300, Material.BRUSH,
                java.util.Arrays.asList("§7Splash colorful paint", "§7everywhere you click!"),
                null, false, false,
                Material.BRUSH, "§9Paint Brush", java.util.Arrays.asList("§9Right-click to paint!"),
                com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic.GadgetType.PAINT_BRUSH
            );
            registerCosmetic(paintBrush);
            
            // Teleport Stick
            com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic teleportStick = new com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic(
                "gadget_teleport", "Teleport Stick", 800, Material.BLAZE_ROD,
                java.util.Arrays.asList("§7Instantly teleport", "§7to where you're looking!"),
                "sneakycosmetics.premium", true, true,
                Material.BLAZE_ROD, "§5Teleport Stick", java.util.Arrays.asList("§5Right-click to teleport!"),
                com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic.GadgetType.TELEPORT_STICK
            );
            registerCosmetic(teleportStick);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to register gadget cosmetics: " + e.getMessage());
        }
    }
    
    private void registerWingCosmetics() {
        try {
            Class.forName("com.sneaky.cosmetics.cosmetics.wings.WingCosmetic");
            
            // Angel Wings
            com.sneaky.cosmetics.cosmetics.wings.WingCosmetic angelWings = new com.sneaky.cosmetics.cosmetics.wings.WingCosmetic(
                "wings_angel", "Angel Wings", 500, Material.FEATHER,
                java.util.Arrays.asList("§7Pure white wings", "§7that shine with divine light!"),
                null, false, true,
                Particle.CLOUD, 6, 0.8, 1.5, 0.3, 0.1,
                com.sneaky.cosmetics.cosmetics.wings.WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(angelWings);
            
            // Demon Wings
            com.sneaky.cosmetics.cosmetics.wings.WingCosmetic demonWings = new com.sneaky.cosmetics.cosmetics.wings.WingCosmetic(
                "wings_demon", "Demon Wings", 600, Material.COAL,
                java.util.Arrays.asList("§7Dark wings that emanate", "§7an ominous presence!"),
                null, false, true,
                Particle.SMOKE, 8, 0.8, 1.5, 0.3, 0.15,
                com.sneaky.cosmetics.cosmetics.wings.WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(demonWings);
            
            // Butterfly Wings
            com.sneaky.cosmetics.cosmetics.wings.WingCosmetic butterflyWings = new com.sneaky.cosmetics.cosmetics.wings.WingCosmetic(
                "wings_butterfly", "Butterfly Wings", 350, Material.ORANGE_DYE,
                java.util.Arrays.asList("§7Delicate butterfly wings", "§7with beautiful colors!"),
                null, false, false,
                Particle.NOTE, 4, 0.6, 1.2, 0.3, 0.08,
                com.sneaky.cosmetics.cosmetics.wings.WingCosmetic.WingType.BUTTERFLY, plugin
            );
            registerCosmetic(butterflyWings);
            
            // Dragon Wings
            com.sneaky.cosmetics.cosmetics.wings.WingCosmetic dragonWings = new com.sneaky.cosmetics.cosmetics.wings.WingCosmetic(
                "wings_dragon", "Dragon Wings", 800, Material.DRAGON_BREATH,
                java.util.Arrays.asList("§7Mighty dragon wings", "§7with ancient power!"),
                "sneakycosmetics.premium", true, true,
                Particle.FLAME, 10, 1.0, 1.8, 0.4, 0.2,
                com.sneaky.cosmetics.cosmetics.wings.WingCosmetic.WingType.DRAGON, plugin
            );
            registerCosmetic(dragonWings);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to register wing cosmetics: " + e.getMessage());
        }
    }
    
    private void registerAuraCosmetics() {
        try {
            Class.forName("com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic");
            
            // Fire Aura
            com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic fireAura = new com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic(
                "aura_fire", "Fire Aura", 400, Material.FIRE_CHARGE,
                java.util.Arrays.asList("§7A blazing aura", "§7surrounds you with flames!"),
                null, false, false,
                Particle.FLAME, 12, 1.5, 0.2, 0.1,
                com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic.AuraType.FIRE, plugin
            );
            registerCosmetic(fireAura);
            
            // Water Aura
            com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic waterAura = new com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic(
                "aura_water", "Water Aura", 350, Material.WATER_BUCKET,
                java.util.Arrays.asList("§7Cool water droplets", "§7dance around you!"),
                null, false, false,
                Particle.DRIPPING_WATER, 15, 1.4, 0.1, 0.15,
                com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic.AuraType.WATER, plugin
            );
            registerCosmetic(waterAura);
            
            // Holy Aura
            com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic holyAura = new com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic(
                "aura_holy", "Holy Aura", 600, Material.ENCHANTING_TABLE,
                java.util.Arrays.asList("§7Mystical enchantments", "§7swirl around your form!"),
                null, false, true,
                Particle.ENCHANT, 20, 1.5, 0.0, 0.3,
                com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic.AuraType.HOLY, plugin
            );
            registerCosmetic(holyAura);
            
            // Dark Aura
            com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic darkAura = new com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic(
                "aura_dark", "Dark Aura", 700, Material.OBSIDIAN,
                java.util.Arrays.asList("§7Shadows and darkness", "§7emanate from your being!"),
                "sneakycosmetics.premium", true, true,
                Particle.SMOKE, 16, 1.2, 0.0, 0.2,
                com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic.AuraType.DARK, plugin
            );
            registerCosmetic(darkAura);
            
            // Void Aura
            com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic voidAura = new com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic(
                "aura_void", "Void Aura", 1000, Material.BEACON,
                java.util.Arrays.asList("§7A mysterious void aura", "§7marks you as otherworldly!"),
                "sneakycosmetics.premium", true, true,
                Particle.PORTAL, 25, 1.8, 0.4, 0.4,
                com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic.AuraType.VOID, plugin
            );
            registerCosmetic(voidAura);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to register aura cosmetics: " + e.getMessage());
        }
    }
}
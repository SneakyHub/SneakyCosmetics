package com.sneaky.cosmetics.managers;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central manager for all cosmetic types and operations
 * Coordinates between different cosmetic managers
 */
public class CosmeticManager {
    
    private final SneakyCosmetics plugin;
    private final Map<String, Cosmetic> cosmetics = new ConcurrentHashMap<>();
    private final Map<CosmeticType, List<Cosmetic>> cosmeticsByType = new ConcurrentHashMap<>();
    private final Map<UUID, Set<String>> activeCosmetics = new ConcurrentHashMap<>();
    
    public CosmeticManager(SneakyCosmetics plugin) {
        this.plugin = plugin;
        initializeCosmeticTypes();
    }
    
    private void initializeCosmeticTypes() {
        for (CosmeticType type : CosmeticType.values()) {
            cosmeticsByType.put(type, new ArrayList<>());
        }
    }
    
    /**
     * Register all cosmetics from all managers
     */
    public void registerCosmetics() {
        plugin.getLogger().info("Registering cosmetics...");
        
        registerParticleCosmetics();
        registerHatCosmetics();
        registerTrailCosmetics();
        registerPetCosmetics();
        registerGadgetCosmetics();
        registerWingCosmetics();
        registerAuraCosmetics();
        
        plugin.getLogger().info("Cosmetic registration complete. Ready to load cosmetics!");
    }
    
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
                org.bukkit.Particle.SNOWBALL, 5, 0.8, 1.2, 0.8, 0.1, plugin
            );
            registerCosmetic(snowParticle);
            
            // Slime particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic slimeParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_slime", "Slime Bounce", 110, org.bukkit.Material.SLIME_BALL,
                java.util.Arrays.asList("§7Bouncy slime particles", "§7add some fun to your step!"),
                null, false, false,
                org.bukkit.Particle.ITEM_CRACK, 4, 0.4, 0.4, 0.4, 0.1, plugin
            );
            registerCosmetic(slimeParticle);
            
            // Witch spell particle
            com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic witchParticle = new com.sneaky.cosmetics.cosmetics.particles.ParticleCosmetic(
                "particle_witch", "Witch Magic", 300, org.bukkit.Material.BREWING_STAND,
                java.util.Arrays.asList("§7Dark witch magic", "§7swirls around you!"),
                null, true, false,
                org.bukkit.Particle.SPELL, 7, 0.5, 0.5, 0.5, 0.15, plugin
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
        // Import hat cosmetics
        try {
            Class.forName("com.sneaky.cosmetics.cosmetics.hats.HatCosmetic");
            
            // Diamond hat
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic diamondHat = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_diamond", "Diamond Crown", 500, org.bukkit.Material.DIAMOND,
                java.util.Arrays.asList("§7Show your wealth with", "§7a sparkling diamond crown!"),
                null, false, false,
                org.bukkit.Material.DIAMOND, "Diamond Crown",
                java.util.Arrays.asList("§7A crown fit for royalty!")
            );
            registerCosmetic(diamondHat);
            
            // Pumpkin hat
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic pumpkinHat = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_pumpkin", "Pumpkin Head", 0, org.bukkit.Material.PUMPKIN,
                java.util.Arrays.asList("§7Spook your friends with", "§7a classic pumpkin head!"),
                null, false, false,
                org.bukkit.Material.PUMPKIN, "Pumpkin Head",
                java.util.Arrays.asList("§7Perfect for Halloween!")
            );
            registerCosmetic(pumpkinHat);
            
            // Gold hat
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic goldHat = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_gold", "Golden Crown", 300, org.bukkit.Material.GOLD_INGOT,
                java.util.Arrays.asList("§7Shine bright with", "§7a golden crown!"),
                null, true, false,
                org.bukkit.Material.GOLD_INGOT, "Golden Crown",
                java.util.Arrays.asList("§7VIP exclusive crown!")
            );
            registerCosmetic(goldHat);
            
            // Iron hat
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic ironHat = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_iron", "Iron Helmet", 50, org.bukkit.Material.IRON_HELMET,
                java.util.Arrays.asList("§7A sturdy iron helmet", "§7for protection and style!"),
                null, false, false,
                org.bukkit.Material.IRON_HELMET, "Iron Helmet",
                java.util.Arrays.asList("§7Protective and stylish!")
            );
            registerCosmetic(ironHat);
            
            // Cake hat
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic cakeHat = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_cake", "Birthday Cake", 200, org.bukkit.Material.CAKE,
                java.util.Arrays.asList("§7Celebrate with a", "§7delicious cake on your head!"),
                null, false, false,
                org.bukkit.Material.CAKE, "Birthday Cake",
                java.util.Arrays.asList("§7Sweet and tasty!")
            );
            registerCosmetic(cakeHat);
            
            // TNT hat
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic tntHat = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_tnt", "TNT Block", 300, org.bukkit.Material.TNT,
                java.util.Arrays.asList("§7Explosive fashion!", "§7Don't worry, it won't blow up!"),
                null, true, false,
                org.bukkit.Material.TNT, "TNT Block",
                java.util.Arrays.asList("§7Boom! VIP exclusive!")
            );
            registerCosmetic(tntHat);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to register hat cosmetics: " + e.getMessage());
        }
    }
    
    private void registerTrailCosmetics() {
        // Import trail cosmetics
        try {
            Class.forName("com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic");
            
            // Rainbow trail (using FLAME instead of REDSTONE for simplicity)
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic rainbowTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_rainbow", "Rainbow Trail", 0, org.bukkit.Material.REDSTONE,
                java.util.Arrays.asList("§7Leave a colorful rainbow", "§7trail wherever you go!"),
                null, false, false,
                org.bukkit.Particle.FLAME, 3, 0.2, 0.1, 0.2, 0.1, plugin
            );
            registerCosmetic(rainbowTrail);
            
            // Smoke trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic smokeTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_smoke", "Smoke Trail", 100, org.bukkit.Material.COAL,
                java.util.Arrays.asList("§7Leave a mysterious", "§7smoke trail behind you!"),
                null, false, false,
                org.bukkit.Particle.SMOKE, 2, 0.1, 0.1, 0.1, 0.05, plugin
            );
            registerCosmetic(smokeTrail);
            
            // Water trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic waterTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_water", "Water Droplets", 150, org.bukkit.Material.WATER_BUCKET,
                java.util.Arrays.asList("§7Create refreshing water", "§7droplets as you move!"),
                null, false, false,
                org.bukkit.Particle.DRIPPING_WATER, 4, 0.3, 0.1, 0.3, 0.1, plugin
            );
            registerCosmetic(waterTrail);
            
            // Ender trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic enderTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_ender", "Ender Portal", 400, org.bukkit.Material.ENDER_EYE,
                java.util.Arrays.asList("§7Channel the power of", "§7the End dimension!"),
                null, false, true,
                org.bukkit.Particle.PORTAL, 6, 0.4, 0.2, 0.4, 0.2, plugin
            );
            registerCosmetic(enderTrail);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to register trail cosmetics: " + e.getMessage());
        }
    }
    
    private void registerPetCosmetics() {
        // Import pet cosmetics
        try {
            Class.forName("com.sneaky.cosmetics.cosmetics.pets.PetCosmetic");
            
            // Common pets (free/cheap)
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic wolfPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_wolf", "Wolf Companion", 0, org.bukkit.Material.BONE,
                java.util.Arrays.asList("§7A loyal wolf that follows", "§7you everywhere you go!"),
                null, false, false,
                org.bukkit.entity.EntityType.WOLF, false, "Wolf", plugin
            );
            registerCosmetic(wolfPet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic catPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_cat", "Cat Companion", 50, org.bukkit.Material.COD,
                java.util.Arrays.asList("§7A cute cat that will be", "§7your faithful companion!"),
                null, false, false,
                org.bukkit.entity.EntityType.CAT, false, "Cat", plugin
            );
            registerCosmetic(catPet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic chickenPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_chicken", "Chicken Friend", 25, org.bukkit.Material.EGG,
                java.util.Arrays.asList("§7A friendly chicken that", "§7follows you around!"),
                null, false, false,
                org.bukkit.entity.EntityType.CHICKEN, false, "Chicken", plugin
            );
            registerCosmetic(chickenPet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic pigPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_pig", "Pig Buddy", 75, org.bukkit.Material.PORKCHOP,
                java.util.Arrays.asList("§7An adorable pig that loves", "§7to follow you everywhere!"),
                null, false, false,
                org.bukkit.entity.EntityType.PIG, false, "Pig", plugin
            );
            registerCosmetic(pigPet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic cowPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_cow", "Cow Companion", 100, org.bukkit.Material.MILK_BUCKET,
                java.util.Arrays.asList("§7A gentle cow that will", "§7be your farming buddy!"),
                null, false, false,
                org.bukkit.entity.EntityType.COW, false, "Cow", plugin
            );
            registerCosmetic(cowPet);
            
            // Baby pets (more expensive)
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic babyWolfPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_baby_wolf", "Wolf Puppy", 150, org.bukkit.Material.BONE,
                java.util.Arrays.asList("§7An adorable wolf puppy", "§7that will melt your heart!"),
                null, false, false,
                org.bukkit.entity.EntityType.WOLF, true, "Puppy", plugin
            );
            registerCosmetic(babyWolfPet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic babyCowPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_baby_cow", "Baby Cow", 125, org.bukkit.Material.MILK_BUCKET,
                java.util.Arrays.asList("§7A tiny baby cow that's", "§7absolutely adorable!"),
                null, false, false,
                org.bukkit.entity.EntityType.COW, true, "Calf", plugin
            );
            registerCosmetic(babyCowPet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic babyPigPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_baby_pig", "Piglet", 100, org.bukkit.Material.PORKCHOP,
                java.util.Arrays.asList("§7A tiny piglet that loves", "§7to play and follow you!"),
                null, false, false,
                org.bukkit.entity.EntityType.PIG, true, "Piglet", plugin
            );
            registerCosmetic(babyPigPet);
            
            // Special/Rare pets (VIP/Premium)
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic foxPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_fox", "Fox Companion", 300, org.bukkit.Material.SWEET_BERRIES,
                java.util.Arrays.asList("§7A clever fox with beautiful", "§7orange fur and bright eyes!"),
                null, true, false,
                org.bukkit.entity.EntityType.FOX, false, "Fox", plugin
            );
            registerCosmetic(foxPet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic pandaPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_panda", "Panda Friend", 500, org.bukkit.Material.BAMBOO,
                java.util.Arrays.asList("§7A rare and adorable panda", "§7that loves bamboo!"),
                null, false, true,
                org.bukkit.entity.EntityType.PANDA, false, "Panda", plugin
            );
            registerCosmetic(pandaPet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic llamaPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_llama", "Llama Buddy", 250, org.bukkit.Material.LEAD,
                java.util.Arrays.asList("§7A majestic llama that will", "§7carry your belongings!"),
                null, true, false,
                org.bukkit.entity.EntityType.LLAMA, false, "Llama", plugin
            );
            registerCosmetic(llamaPet);
            
            // Exotic pets (Premium only)
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic parrotPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_parrot", "Parrot Companion", 400, org.bukkit.Material.FEATHER,
                java.util.Arrays.asList("§7A colorful parrot that sits", "§7on your shoulder!"),
                null, false, true,
                org.bukkit.entity.EntityType.PARROT, false, "Parrot", plugin
            );
            registerCosmetic(parrotPet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic turtlePet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_turtle", "Sea Turtle", 350, org.bukkit.Material.TURTLE_EGG,
                java.util.Arrays.asList("§7A wise sea turtle that moves", "§7slowly but surely!"),
                null, false, true,
                org.bukkit.entity.EntityType.TURTLE, false, "Turtle", plugin
            );
            registerCosmetic(turtlePet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic rabbitPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_rabbit", "Bunny Friend", 200, org.bukkit.Material.CARROT,
                java.util.Arrays.asList("§7A fluffy bunny that hops", "§7around with joy!"),
                null, false, false,
                org.bukkit.entity.EntityType.RABBIT, false, "Bunny", plugin
            );
            registerCosmetic(rabbitPet);
            
            // Hostile-turned-friendly pets (Premium)
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic zombiePet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_zombie", "Friendly Zombie", 600, org.bukkit.Material.ROTTEN_FLESH,
                java.util.Arrays.asList("§7A reformed zombie that's now", "§7your loyal undead friend!"),
                null, false, true,
                org.bukkit.entity.EntityType.ZOMBIE, false, "Zombie", plugin
            );
            registerCosmetic(zombiePet);
            
            com.sneaky.cosmetics.cosmetics.pets.PetCosmetic skeletonPet = new com.sneaky.cosmetics.cosmetics.pets.PetCosmetic(
                "pet_skeleton", "Bone Buddy", 550, org.bukkit.Material.BONE,
                java.util.Arrays.asList("§7A friendly skeleton that", "§7won't shoot arrows at you!"),
                null, false, true,
                org.bukkit.entity.EntityType.SKELETON, false, "Skeleton", plugin
            );
            registerCosmetic(skeletonPet);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to register pet cosmetics: " + e.getMessage());
        }
    }
    
    private void registerGadgetCosmetics() {
        // Import gadget cosmetics
        try {
            Class.forName("com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic");
            
            // Firework Launcher (Free)
            com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic fireworkLauncher = new com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic(
                "gadget_firework", "Firework Launcher", 0, org.bukkit.Material.FIREWORK_ROCKET,
                java.util.Arrays.asList("§7Launch colorful fireworks", "§7into the sky!"),
                null, false, false,
                org.bukkit.Material.FIREWORK_ROCKET, "Firework Launcher",
                java.util.Arrays.asList("§7Right-click to launch fireworks!"),
                com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic.GadgetType.FIREWORK_LAUNCHER
            );
            registerCosmetic(fireworkLauncher);
            
            // Snowball Cannon
            com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic snowballCannon = new com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic(
                "gadget_snowball", "Snowball Cannon", 150, org.bukkit.Material.SNOWBALL,
                java.util.Arrays.asList("§7Launch snowballs at", "§7your friends!"),
                null, false, false,
                org.bukkit.Material.SNOWBALL, "Snowball Cannon",
                java.util.Arrays.asList("§7Right-click to shoot snowballs!"),
                com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic.GadgetType.SNOWBALL_CANNON
            );
            registerCosmetic(snowballCannon);
            
            // Paint Brush (VIP)
            com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic paintBrush = new com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic(
                "gadget_paint", "Paint Brush", 300, org.bukkit.Material.BRUSH,
                java.util.Arrays.asList("§7Paint colorful particles", "§7wherever you look!"),
                null, true, false,
                org.bukkit.Material.BRUSH, "Paint Brush",
                java.util.Arrays.asList("§7Right-click to paint particles!"),
                com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic.GadgetType.PAINT_BRUSH
            );
            registerCosmetic(paintBrush);
            
            // Party Popper (Premium)
            com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic partyPopper = new com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic(
                "gadget_party", "Party Popper", 500, org.bukkit.Material.POPPED_CHORUS_FRUIT,
                java.util.Arrays.asList("§7Create an instant party", "§7with confetti and sounds!"),
                null, false, true,
                org.bukkit.Material.POPPED_CHORUS_FRUIT, "Party Popper",
                java.util.Arrays.asList("§7Right-click to start a party!"),
                com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic.GadgetType.PARTY_POPPER
            );
            registerCosmetic(partyPopper);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to register gadget cosmetics: " + e.getMessage());
        }
    }
    
    private void registerWingCosmetics() {
        // Import wing cosmetics
        try {
            Class.forName("com.sneaky.cosmetics.cosmetics.wings.WingCosmetic");
            
            // Angel Wings (Free)
            com.sneaky.cosmetics.cosmetics.wings.WingCosmetic angelWings = new com.sneaky.cosmetics.cosmetics.wings.WingCosmetic(
                "wings_angel", "Angel Wings", 0, org.bukkit.Material.FEATHER,
                java.util.Arrays.asList("§7Pure white wings that", "§7make you look divine!"),
                null, false, false,
                org.bukkit.Particle.CLOUD, 3, 0.2, 0.2, 0.2, 0.02,
                com.sneaky.cosmetics.cosmetics.wings.WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(angelWings);
            
            // Fairy Wings
            com.sneaky.cosmetics.cosmetics.wings.WingCosmetic fairyWings = new com.sneaky.cosmetics.cosmetics.wings.WingCosmetic(
                "wings_fairy", "Fairy Wings", 200, org.bukkit.Material.GLOW_BERRIES,
                java.util.Arrays.asList("§7Delicate sparkling wings", "§7full of magic!"),
                null, false, false,
                org.bukkit.Particle.ENCHANT, 5, 0.3, 0.3, 0.3, 0.05,
                com.sneaky.cosmetics.cosmetics.wings.WingCosmetic.WingType.FAIRY, plugin
            );
            registerCosmetic(fairyWings);
            
            // Dragon Wings (VIP)
            com.sneaky.cosmetics.cosmetics.wings.WingCosmetic dragonWings = new com.sneaky.cosmetics.cosmetics.wings.WingCosmetic(
                "wings_dragon", "Dragon Wings", 400, org.bukkit.Material.DRAGON_HEAD,
                java.util.Arrays.asList("§7Powerful fiery wings", "§7of an ancient dragon!"),
                null, true, false,
                org.bukkit.Particle.FLAME, 8, 0.4, 0.3, 0.4, 0.1,
                com.sneaky.cosmetics.cosmetics.wings.WingCosmetic.WingType.DRAGON, plugin
            );
            registerCosmetic(dragonWings);
            
            // Phoenix Wings (Premium)
            com.sneaky.cosmetics.cosmetics.wings.WingCosmetic phoenixWings = new com.sneaky.cosmetics.cosmetics.wings.WingCosmetic(
                "wings_phoenix", "Phoenix Wings", 600, org.bukkit.Material.BLAZE_ROD,
                java.util.Arrays.asList("§7Legendary wings of", "§7rebirth and eternal fire!"),
                null, false, true,
                org.bukkit.Particle.LAVA, 10, 0.5, 0.4, 0.5, 0.15,
                com.sneaky.cosmetics.cosmetics.wings.WingCosmetic.WingType.PHOENIX, plugin
            );
            registerCosmetic(phoenixWings);
            
            // Shadow Wings (Premium)
            com.sneaky.cosmetics.cosmetics.wings.WingCosmetic shadowWings = new com.sneaky.cosmetics.cosmetics.wings.WingCosmetic(
                "wings_shadow", "Shadow Wings", 550, org.bukkit.Material.BLACK_DYE,
                java.util.Arrays.asList("§7Dark wings that blend", "§7with the shadows!"),
                null, false, true,
                org.bukkit.Particle.SQUID_INK, 6, 0.3, 0.3, 0.3, 0.08,
                com.sneaky.cosmetics.cosmetics.wings.WingCosmetic.WingType.SHADOW, plugin
            );
            registerCosmetic(shadowWings);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to register wing cosmetics: " + e.getMessage());
        }
    }
    
    private void registerAuraCosmetics() {
        // Import aura cosmetics
        try {
            Class.forName("com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic");
            
            // Holy Aura (Free)
            com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic holyAura = new com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic(
                "aura_holy", "Holy Aura", 0, org.bukkit.Material.NETHER_STAR,
                java.util.Arrays.asList("§7A divine aura of", "§7pure light and energy!"),
                null, false, false,
                org.bukkit.Particle.ENCHANT, 3, 1.5, 0.0, 0.05,
                com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic.AuraType.HOLY, plugin
            );
            registerCosmetic(holyAura);
            
            // Fire Aura
            com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic fireAura = new com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic(
                "aura_fire", "Fire Aura", 250, org.bukkit.Material.FIRE_CHARGE,
                java.util.Arrays.asList("§7Surround yourself with", "§7burning flames!"),
                null, false, false,
                org.bukkit.Particle.FLAME, 4, 1.3, 0.2, 0.08,
                com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic.AuraType.FIRE, plugin
            );
            registerCosmetic(fireAura);
            
            // Nature Aura
            com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic natureAura = new com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic(
                "aura_nature", "Nature Aura", 300, org.bukkit.Material.OAK_SAPLING,
                java.util.Arrays.asList("§7Connect with nature's", "§7life-giving energy!"),
                null, false, false,
                org.bukkit.Particle.HAPPY_VILLAGER, 5, 1.6, 0.3, 0.06,
                com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic.AuraType.NATURE, plugin
            );
            registerCosmetic(natureAura);
            
            // Lightning Aura (VIP)
            com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic lightningAura = new com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic(
                "aura_lightning", "Lightning Aura", 450, org.bukkit.Material.LIGHTNING_ROD,
                java.util.Arrays.asList("§7Harness the power of", "§7electricity and storms!"),
                null, true, false,
                org.bukkit.Particle.CRIT, 6, 1.1, 0.5, 0.12,
                com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic.AuraType.LIGHTNING, plugin
            );
            registerCosmetic(lightningAura);
            
            // Void Aura (Premium)
            com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic voidAura = new com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic(
                "aura_void", "Void Aura", 700, org.bukkit.Material.END_PORTAL_FRAME,
                java.util.Arrays.asList("§7Channel the mysterious", "§7power of the void!"),
                null, false, true,
                org.bukkit.Particle.PORTAL, 8, 1.8, 0.4, 0.15,
                com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic.AuraType.VOID, plugin
            );
            registerCosmetic(voidAura);
            
            // Rainbow Aura (Premium)
            com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic rainbowAura = new com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic(
                "aura_rainbow", "Rainbow Aura", 800, org.bukkit.Material.PRISMARINE_CRYSTALS,
                java.util.Arrays.asList("§7A spectacular multicolored", "§7aura of pure joy!"),
                null, false, true,
                org.bukkit.Particle.NOTE, 10, 2.0, 0.6, 0.2,
                com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic.AuraType.RAINBOW, plugin
            );
            registerCosmetic(rainbowAura);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to register aura cosmetics: " + e.getMessage());
        }
    }
    
    /**
     * Register a cosmetic
     */
    public void registerCosmetic(Cosmetic cosmetic) {
        cosmetics.put(cosmetic.getId(), cosmetic);
        cosmeticsByType.get(cosmetic.getType()).add(cosmetic);
        
        plugin.getLogger().info("Registered cosmetic: " + cosmetic.getId() + " (" + cosmetic.getType() + ")");
    }
    
    /**
     * Get a cosmetic by ID
     */
    public Cosmetic getCosmetic(String id) {
        return cosmetics.get(id);
    }
    
    /**
     * Get all cosmetics of a specific type
     */
    public List<Cosmetic> getCosmeticsByType(CosmeticType type) {
        return new ArrayList<>(cosmeticsByType.getOrDefault(type, new ArrayList<>()));
    }
    
    /**
     * Get all cosmetic types
     */
    public Set<CosmeticType> getCosmeticTypes() {
        return cosmeticsByType.keySet();
    }
    
    /**
     * Get total number of cosmetics
     */
    public int getTotalCosmetics() {
        return cosmetics.size();
    }
    
    /**
     * Get cosmetics by type count
     */
    public int getCosmeticCountByType(CosmeticType type) {
        return cosmeticsByType.getOrDefault(type, new ArrayList<>()).size();
    }
    
    /**
     * Check if a player owns a cosmetic
     */
    public boolean hasCosmetic(Player player, String cosmeticId) {
        // Check if player has free access to all cosmetics
        if (player.hasPermission("sneakycosmetics.free")) {
            return true;
        }
        
        // Check if cosmetic is free
        Cosmetic cosmetic = getCosmetic(cosmeticId);
        if (cosmetic != null && cosmetic.isFree()) {
            return true;
        }
        
        // Check database
        try {
            return plugin.getDatabaseManager().hasCosmetic(player.getUniqueId(), cosmeticId).get();
        } catch (Exception e) {
            plugin.getLogger().warning("Error checking cosmetic ownership: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Give a cosmetic to a player
     */
    public void giveCosmetic(Player player, String cosmeticId) {
        plugin.getDatabaseManager().giveCosmetic(player.getUniqueId(), cosmeticId);
    }
    
    /**
     * Activate a cosmetic for a player
     */
    public boolean activateCosmetic(Player player, String cosmeticId) {
        Cosmetic cosmetic = getCosmetic(cosmeticId);
        if (cosmetic == null) {
            return false;
        }
        
        // Check if player owns the cosmetic (this now includes free access check)
        if (!hasCosmetic(player, cosmeticId)) {
            return false;
        }
        
        // Check CMI integration restrictions
        if (plugin.getCMIIntegration() != null && plugin.getCMIIntegration().isAvailable()) {
            if (plugin.getCMIIntegration().shouldDisableCosmetics(player)) {
                // Send appropriate message based on CMI status
                if (plugin.getCMIIntegration().isVanished(player)) {
                    plugin.getMessageManager().sendConfigMessage(player, "cmi.status.vanished-disabled");
                } else if (plugin.getCMIIntegration().isAFK(player)) {
                    plugin.getMessageManager().sendConfigMessage(player, "cmi.status.afk-disabled");
                } else if (plugin.getCMIIntegration().isInGodMode(player)) {
                    plugin.getMessageManager().sendConfigMessage(player, "cmi.status.god-disabled");
                }
                return false;
            }
        }
        
        // Check if player already has a cosmetic of this type active
        UUID uuid = player.getUniqueId();
        Set<String> playerCosmetics = activeCosmetics.getOrDefault(uuid, new HashSet<>());
        
        // Remove any existing cosmetic of the same type
        for (String activeId : new HashSet<>(playerCosmetics)) {
            Cosmetic activeCosmetic = getCosmetic(activeId);
            if (activeCosmetic != null && activeCosmetic.getType() == cosmetic.getType()) {
                deactivateCosmetic(player, activeId);
            }
        }
        
        // Activate the new cosmetic
        try {
            cosmetic.activate(player);
            playerCosmetics.add(cosmeticId);
            activeCosmetics.put(uuid, playerCosmetics);
            
            // Record statistics
            if (plugin.getStatisticsManager() != null) {
                plugin.getStatisticsManager().recordCosmeticActivation(player, cosmeticId);
            }
            
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Error activating cosmetic " + cosmeticId + " for " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deactivate a cosmetic for a player
     */
    public boolean deactivateCosmetic(Player player, String cosmeticId) {
        Cosmetic cosmetic = getCosmetic(cosmeticId);
        if (cosmetic == null) {
            return false;
        }
        
        UUID uuid = player.getUniqueId();
        Set<String> playerCosmetics = activeCosmetics.getOrDefault(uuid, new HashSet<>());
        
        if (!playerCosmetics.contains(cosmeticId)) {
            return false; // Not active
        }
        
        try {
            cosmetic.deactivate(player);
            playerCosmetics.remove(cosmeticId);
            if (playerCosmetics.isEmpty()) {
                activeCosmetics.remove(uuid);
            } else {
                activeCosmetics.put(uuid, playerCosmetics);
            }
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Error deactivating cosmetic " + cosmeticId + " for " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Toggle a cosmetic for a player
     */
    public boolean toggleCosmetic(Player player, String cosmeticId) {
        UUID uuid = player.getUniqueId();
        Set<String> playerCosmetics = activeCosmetics.getOrDefault(uuid, new HashSet<>());
        
        if (playerCosmetics.contains(cosmeticId)) {
            return deactivateCosmetic(player, cosmeticId);
        } else {
            return activateCosmetic(player, cosmeticId);
        }
    }
    
    /**
     * Get all active cosmetics for a player
     */
    public Set<String> getActiveCosmetics(Player player) {
        return new HashSet<>(activeCosmetics.getOrDefault(player.getUniqueId(), new HashSet<>()));
    }
    
    /**
     * Clear all active cosmetics for a player
     */
    public void clearAllCosmetics(Player player) {
        UUID uuid = player.getUniqueId();
        Set<String> playerCosmetics = activeCosmetics.getOrDefault(uuid, new HashSet<>());
        
        for (String cosmeticId : new HashSet<>(playerCosmetics)) {
            deactivateCosmetic(player, cosmeticId);
        }
    }
    
    /**
     * Check if a cosmetic is active for a player
     */
    public boolean isCosmeticActive(Player player, String cosmeticId) {
        return activeCosmetics.getOrDefault(player.getUniqueId(), new HashSet<>()).contains(cosmeticId);
    }
    
    /**
     * Cleanup inactive cosmetic entities (performance task)
     */
    public void cleanupInactiveCosmetics() {
        plugin.getLogger().info("Running cosmetic cleanup task...");
        
        plugin.getSchedulerAdapter().runTaskAsynchronously(() -> {
            int cleanedUp = 0;
            
            try {
                // Clean up pet entities for offline players
                if (plugin.getPetManager() != null) {
                    // Stub cleanup - would clean up pet entities
                    plugin.getLogger().fine("Cleaning up pet entities (stub implementation)");
                }
                
                // Clean up trail particles
                if (plugin.getTrailManager() != null) {
                    // Stub cleanup - would clean up trail particles
                    plugin.getLogger().fine("Cleaning up trail particles (stub implementation)");
                }
                
                // Clean up aura effects for offline players
                if (plugin.getAuraManager() != null) {
                    // Stub cleanup - would clean up aura effects
                    plugin.getLogger().fine("Cleaning up aura effects (stub implementation)");
                }
                
                // Clean up wing effects for offline players
                if (plugin.getWingManager() != null) {
                    // Stub cleanup - would clean up wing effects
                    plugin.getLogger().fine("Cleaning up wing effects (stub implementation)");
                }
                
                // Clean up hat entities for offline players
                if (plugin.getHatManager() != null) {
                    // Stub cleanup - would clean up hat entities
                    plugin.getLogger().fine("Cleaning up hat entities (stub implementation)");
                }
                
                // Clean up gadget items
                if (plugin.getGadgetManager() != null) {
                    // Stub cleanup - would clean up gadget items
                    plugin.getLogger().fine("Cleaning up gadget items (stub implementation)");
                }
                
                // Clean up particle effects for offline players
                if (plugin.getParticleManager() != null) {
                    plugin.getParticleManager().cleanupOfflinePlayerParticles();
                }
                
                plugin.getLogger().info("Cosmetic cleanup completed. Cleaned up " + cleanedUp + " entities.");
                
            } catch (Exception e) {
                plugin.getLogger().warning("Error during cosmetic cleanup: " + e.getMessage());
            }
        });
    }
    
    /**
     * Reload cosmetic system
     */
    public void reload() {
        plugin.getLogger().info("Reloading cosmetic system...");
        
        // Clear and re-register cosmetics
        cosmetics.clear();
        for (List<Cosmetic> typeList : cosmeticsByType.values()) {
            typeList.clear();
        }
        
        registerCosmetics();
    }
    
    /**
     * Get all cosmetics
     */
    public Collection<Cosmetic> getAllCosmetics() {
        return cosmetics.values();
    }
    
    /**
     * Get cosmetics that a player can access (owned + free)
     */
    public List<Cosmetic> getAccessibleCosmetics(Player player, CosmeticType type) {
        List<Cosmetic> accessible = new ArrayList<>();
        List<Cosmetic> typeCosmetics = getCosmeticsByType(type);
        
        for (Cosmetic cosmetic : typeCosmetics) {
            // Check if player can access this cosmetic (includes permission checks and ownership)
            if (cosmetic.canPlayerAccess(player) && hasCosmetic(player, cosmetic.getId())) {
                accessible.add(cosmetic);
            }
        }
        
        return accessible;
    }
}
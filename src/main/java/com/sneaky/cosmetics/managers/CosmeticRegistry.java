package com.sneaky.cosmetics.managers;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import com.sneaky.cosmetics.cosmetics.wings.WingCosmetic;
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
        registerMorphCosmetics();
        
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
                Material.NETHERITE_HELMET, "§4Netherite Helm", java.util.Arrays.asList("§4Legendary")
            );
            registerCosmetic(netheriteHelm);
            
                        // Pumpkin Head
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic pumpkin = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                    "hat_pumpkin", "Pumpkin Head", 300, Material.PUMPKIN,
                    java.util.Arrays.asList("§7A tasty pumpkin", "§7A snowman died for this!"),
                    null, false, false,
                    Material.PUMPKIN, "§6Pumpkin Head", java.util.Arrays.asList("§7Simple and comfortable")
            );
            registerCosmetic(pumpkin);
            
            // Creative Hats - Unique Design Collection
            
            // Chef's Hat
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic chefHat = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_chef", "Chef's Hat", 200, Material.WHITE_WOOL,
                java.util.Arrays.asList("§7A tall white chef's hat", "§7for culinary adventures!"),
                null, false, false,
                Material.WHITE_WOOL, "§fChef's Hat", java.util.Arrays.asList("§7Perfect for cooking!")
            );
            registerCosmetic(chefHat);
            
            // Pirate Hat
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic pirateHat = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_pirate", "Pirate Hat", 250, Material.BLACK_WOOL,
                java.util.Arrays.asList("§7Ahoy matey! A classic", "§7pirate tricorn hat!"),
                null, false, false,
                Material.BLACK_WOOL, "§8Pirate Hat", java.util.Arrays.asList("§8Sail the seven seas!")
            );
            registerCosmetic(pirateHat);
            
            // Top Hat
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic topHat = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_top", "Gentleman's Top Hat", 400, Material.BLACK_CARPET,
                java.util.Arrays.asList("§7A sophisticated black top hat", "§7for the distinguished player!"),
                null, false, false,
                Material.BLACK_CARPET, "§8Top Hat", java.util.Arrays.asList("§8Quite distinguished!")
            );
            registerCosmetic(topHat);
            
            // Wizard Hat
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic wizardHat = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_wizard", "Wizard Hat", 350, Material.PURPLE_WOOL,
                java.util.Arrays.asList("§7A mystical pointed wizard hat", "§7filled with arcane power!"),
                null, false, false,
                Material.PURPLE_WOOL, "§5Wizard Hat", java.util.Arrays.asList("§5Magical powers!")
            );
            registerCosmetic(wizardHat);
            
            // Santa Hat
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic santaHat = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_santa", "Santa Hat", 300, Material.RED_WOOL,
                java.util.Arrays.asList("§7Ho ho ho! A festive", "§7red Santa hat!"),
                null, false, false,
                Material.RED_WOOL, "§cSanta Hat", java.util.Arrays.asList("§cSpread Christmas cheer!")
            );
            registerCosmetic(santaHat);
            
            // Party Hat
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic partyHat = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_party", "Party Hat", 180, Material.YELLOW_WOOL,
                java.util.Arrays.asList("§7A colorful cone party hat", "§7for celebrations!"),
                null, false, false,
                Material.YELLOW_WOOL, "§eParty Hat", java.util.Arrays.asList("§eLet's celebrate!")
            );
            registerCosmetic(partyHat);
            
            // Crown of Thorns
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic thornCrown = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_thorns", "Crown of Thorns", 450, Material.DEAD_BUSH,
                java.util.Arrays.asList("§7A dark crown made of", "§7twisted thorns and brambles!"),
                null, true, false,
                Material.DEAD_BUSH, "§8Crown of Thorns", java.util.Arrays.asList("§8Sharp and dangerous!")
            );
            registerCosmetic(thornCrown);
            
            // Ice Crown
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic iceCrown = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_ice", "Ice Crown", 380, Material.ICE,
                java.util.Arrays.asList("§7A crystalline crown", "§7made of pure ice!"),
                null, false, false,
                Material.ICE, "§bIce Crown", java.util.Arrays.asList("§bFrozen majesty!")
            );
            registerCosmetic(iceCrown);
            
            // Fire Crown
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic fireCrown = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_fire", "Crown of Flames", 420, Material.MAGMA_BLOCK,
                java.util.Arrays.asList("§7A blazing crown that", "§7burns with eternal fire!"),
                null, false, true,
                Material.MAGMA_BLOCK, "§cCrown of Flames", java.util.Arrays.asList("§cBurning with power!")
            );
            registerCosmetic(fireCrown);
            
            // Nature Crown
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic natureCrown = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_nature", "Nature's Crown", 320, Material.OAK_LEAVES,
                java.util.Arrays.asList("§7A living crown made", "§7of leaves and flowers!"),
                null, false, false,
                Material.OAK_LEAVES, "§aNature's Crown", java.util.Arrays.asList("§aOne with nature!")
            );
            registerCosmetic(natureCrown);
            
            // Viking Helmet
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic vikingHelmet = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_viking", "Viking Helmet", 280, Material.IRON_HELMET,
                java.util.Arrays.asList("§7A horned viking helmet", "§7for brave warriors!"),
                null, false, false,
                Material.IRON_HELMET, "§8Viking Helmet", java.util.Arrays.asList("§8For Valhalla!")
            );
            registerCosmetic(vikingHelmet);
            
            // Pharaoh's Headdress
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic pharaohHat = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_pharaoh", "Pharaoh's Headdress", 500, Material.GOLD_BLOCK,
                java.util.Arrays.asList("§7An ancient Egyptian headdress", "§7worn by pharaohs!"),
                null, false, true,
                Material.GOLD_BLOCK, "§6Pharaoh's Headdress", java.util.Arrays.asList("§6Rule like a pharaoh!")
            );
            registerCosmetic(pharaohHat);
            
            // Space Helmet
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic spaceHelmet = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_space", "Space Helmet", 600, Material.GLASS,
                java.util.Arrays.asList("§7A futuristic space helmet", "§7for cosmic adventures!"),
                null, false, true,
                Material.GLASS, "§fSpace Helmet", java.util.Arrays.asList("§fTo infinity and beyond!")
            );
            registerCosmetic(spaceHelmet);
            
            // Samurai Helmet
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic samuraiHelmet = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_samurai", "Samurai Helmet", 450, Material.IRON_HELMET,
                java.util.Arrays.asList("§7A traditional samurai kabuto", "§7for honorable warriors!"),
                null, true, false,
                Material.IRON_HELMET, "§8Samurai Helmet", java.util.Arrays.asList("§8Honor and duty!")
            );
            registerCosmetic(samuraiHelmet);
            
            // Jester Hat
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic jesterHat = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_jester", "Jester's Hat", 220, Material.PURPLE_WOOL,
                java.util.Arrays.asList("§7A colorful jester's cap", "§7with jingling bells!"),
                null, false, false,
                Material.PURPLE_WOOL, "§dJester's Hat", java.util.Arrays.asList("§dEntertain the court!")
            );
            registerCosmetic(jesterHat);
            
            // Beret
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic beret = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_beret", "French Beret", 150, Material.BLACK_WOOL,
                java.util.Arrays.asList("§7A stylish French beret", "§7très magnifique!"),
                null, false, false,
                Material.BLACK_WOOL, "§8French Beret", java.util.Arrays.asList("§8Très chic!")
            );
            registerCosmetic(beret);
            
            // Cowboy Hat
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic cowboyHat = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_cowboy", "Cowboy Hat", 240, Material.BROWN_WOOL,
                java.util.Arrays.asList("§7Yeehaw! A classic", "§7western cowboy hat!"),
                null, false, false,
                Material.BROWN_WOOL, "§6Cowboy Hat", java.util.Arrays.asList("§6Ride 'em cowboy!")
            );
            registerCosmetic(cowboyHat);
            
            // Fedora
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic fedora = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_fedora", "Classic Fedora", 190, Material.GRAY_WOOL,
                java.util.Arrays.asList("§7A classy fedora hat", "§7for the sophisticated player!"),
                null, false, false,
                Material.GRAY_WOOL, "§8Classic Fedora", java.util.Arrays.asList("§8Timeless style!")
            );
            registerCosmetic(fedora);
            
            // Baseball Cap
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic baseballCap = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_baseball", "Baseball Cap", 120, Material.BLUE_WOOL,
                java.util.Arrays.asList("§7A casual baseball cap", "§7perfect for sports!"),
                null, false, false,
                Material.BLUE_WOOL, "§9Baseball Cap", java.util.Arrays.asList("§9Play ball!")
            );
            registerCosmetic(baseballCap);
            
            // Premium Exclusive Hats
            
            // Dragon Scale Helmet
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic dragonHelmet = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_dragon", "Dragon Scale Helmet", 800, Material.DRAGON_HEAD,
                java.util.Arrays.asList("§7A helmet forged from", "§7ancient dragon scales!"),
                "sneakycosmetics.premium", true, true,
                Material.DRAGON_HEAD, "§4Dragon Scale Helmet", java.util.Arrays.asList("§4Legendary protection!")
            );
            registerCosmetic(dragonHelmet);
            
            // Angel Halo
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic angelHalo = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_halo", "Angel Halo", 750, Material.END_ROD,
                java.util.Arrays.asList("§7A divine halo that", "§7radiates holy light!"),
                "sneakycosmetics.premium", true, true,
                Material.END_ROD, "§fAngel Halo", java.util.Arrays.asList("§fDivine blessing!")
            );
            registerCosmetic(angelHalo);
            
            // Devil Horns
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic devilHorns = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_devil", "Devil Horns", 666, Material.BLACKSTONE,
                java.util.Arrays.asList("§7Sinister horns that", "§7mark you as diabolical!"),
                "sneakycosmetics.premium", true, true,
                Material.BLACKSTONE, "§cDevil Horns", java.util.Arrays.asList("§cEmbraced by darkness!")
            );
            registerCosmetic(devilHorns);
            
            // Crystal Tiara
            com.sneaky.cosmetics.cosmetics.hats.HatCosmetic crystalTiara = new com.sneaky.cosmetics.cosmetics.hats.HatCosmetic(
                "hat_crystal", "Crystal Tiara", 900, Material.AMETHYST_CLUSTER,
                java.util.Arrays.asList("§7A sparkling tiara made", "§7of pure crystal shards!"),
                "sneakycosmetics.premium", true, true,
                Material.AMETHYST_CLUSTER, "§dCrystal Tiara", java.util.Arrays.asList("§dShimmering beauty!")
            );
            registerCosmetic(crystalTiara);
            
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
            
            // === ELEMENTAL TRAILS ===
            
            // Lightning Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic lightningTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_lightning", "Lightning Trail", 400, Material.LIGHTNING_ROD,
                java.util.Arrays.asList("§7Electric sparks crackle", "§7behind your every move!"),
                null, false, true,
                Particle.ELECTRIC_SPARK, 4, 0.3, 0.3, 0.3, 0.1, plugin
            );
            registerCosmetic(lightningTrail);
            
            // Ice Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic iceTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_ice", "Frost Trail", 220, Material.ICE,
                java.util.Arrays.asList("§7Crystalline ice particles", "§7freeze your path!"),
                null, false, false,
                Particle.SNOWFLAKE, 6, 0.4, 0.2, 0.4, 0.05, plugin
            );
            registerCosmetic(iceTrail);
            
            // Wind Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic windTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_wind", "Wind Current", 180, Material.FEATHER,
                java.util.Arrays.asList("§7Gusts of wind swirl", "§7around your footsteps!"),
                null, false, false,
                Particle.SWEEP_ATTACK, 3, 0.5, 0.1, 0.5, 0.2, plugin
            );
            registerCosmetic(windTrail);
            
            // Earth Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic earthTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_earth", "Earth Shaker", 250, Material.DIRT,
                java.util.Arrays.asList("§7Rocky debris follows", "§7your powerful steps!"),
                null, false, false,
                Particle.BLOCK, 5, 0.3, 0.1, 0.3, 0.1, plugin
            );
            registerCosmetic(earthTrail);
            
            // === MAGICAL TRAILS ===
            
            // Fairy Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic fairyTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_fairy", "Fairy Dust", 300, Material.GLOWSTONE_DUST,
                java.util.Arrays.asList("§7Sparkling fairy dust", "§7dances behind you!"),
                null, false, false,
                Particle.ENCHANT, 8, 0.4, 0.4, 0.4, 0.3, plugin
            );
            registerCosmetic(fairyTrail);
            
            // Void Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic voidTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_void", "Void Walker", 500, Material.OBSIDIAN,
                java.util.Arrays.asList("§7Dark void energy", "§7emanates from your path!"),
                null, true, true,
                Particle.WITCH, 4, 0.3, 0.3, 0.3, 0.2, plugin
            );
            registerCosmetic(voidTrail);
            
            // Cosmic Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic cosmicTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_cosmic", "Cosmic Energy", 450, Material.NETHER_STAR,
                java.util.Arrays.asList("§7Stellar energy from", "§7distant galaxies!"),
                null, false, true,
                Particle.END_ROD, 3, 0.2, 0.5, 0.2, 0.1, plugin
            );
            registerCosmetic(cosmicTrail);
            
            // Soul Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic soulTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_soul", "Soul Walker", 320, Material.SOUL_SAND,
                java.util.Arrays.asList("§7Ancient souls whisper", "§7as you pass by!"),
                null, false, false,
                Particle.SOUL, 5, 0.3, 0.3, 0.3, 0.15, plugin
            );
            registerCosmetic(soulTrail);
            
            // === NATURE TRAILS ===
            
            // Flower Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic flowerTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_flower", "Flower Petals", 200, Material.POPPY,
                java.util.Arrays.asList("§7Beautiful flower petals", "§7bloom in your wake!"),
                null, false, false,
                Particle.CHERRY_LEAVES, 6, 0.4, 0.2, 0.4, 0.1, plugin
            );
            registerCosmetic(flowerTrail);
            
            // Autumn Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic autumnTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_autumn", "Autumn Leaves", 160, Material.OAK_LEAVES,
                java.util.Arrays.asList("§7Golden autumn leaves", "§7drift behind you!"),
                null, false, false,
                Particle.FALLING_DUST, 7, 0.4, 0.3, 0.4, 0.05, plugin
            );
            registerCosmetic(autumnTrail);
            
            // Honey Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic honeyTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_honey", "Honey Drops", 240, Material.HONEY_BOTTLE,
                java.util.Arrays.asList("§7Sweet honey droplets", "§7drip from your steps!"),
                null, false, false,
                Particle.DRIPPING_HONEY, 4, 0.3, 0.2, 0.3, 0.08, plugin
            );
            registerCosmetic(honeyTrail);
            
            // === SPECIAL EFFECTS TRAILS ===
            
            // Rainbow Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic rainbowTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_rainbow", "Rainbow Path", 380, Material.PRISMARINE_CRYSTALS,
                java.util.Arrays.asList("§7A beautiful rainbow", "§7follows your journey!"),
                null, false, true,
                Particle.DUST, 10, 0.5, 0.4, 0.5, 0.2, plugin
            );
            registerCosmetic(rainbowTrail);
            
            // Music Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic musicTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_music", "Musical Notes", 280, Material.NOTE_BLOCK,
                java.util.Arrays.asList("§7Musical notes dance", "§7to your rhythm!"),
                null, false, false,
                Particle.NOTE, 5, 0.4, 0.4, 0.4, 0.3, plugin
            );
            registerCosmetic(musicTrail);
            
            // Slime Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic slimeTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_slime", "Slime Trail", 150, Material.SLIME_BALL,
                java.util.Arrays.asList("§7Bouncy slime drops", "§7mark your path!"),
                null, false, false,
                Particle.ITEM, 3, 0.3, 0.1, 0.3, 0.1, plugin
            );
            registerCosmetic(slimeTrail);
            
            // Smoke Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic smokeTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_smoke", "Smoke Cloud", 120, Material.CAMPFIRE,
                java.util.Arrays.asList("§7Mysterious smoke", "§7billows behind you!"),
                null, false, false,
                Particle.CAMPFIRE_COSY_SMOKE, 4, 0.3, 0.3, 0.3, 0.1, plugin
            );
            registerCosmetic(smokeTrail);
            
            // === SEASONAL TRAILS ===
            
            // Snowflake Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic snowflakeTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_snowflake", "Winter Snowflakes", 200, Material.SNOW_BLOCK,
                java.util.Arrays.asList("§7Delicate snowflakes", "§7fall in your wake!"),
                null, false, false,
                Particle.SNOWFLAKE, 8, 0.4, 0.4, 0.4, 0.05, plugin
            );
            registerCosmetic(snowflakeTrail);
            
            // Spring Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic springTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_spring", "Spring Bloom", 220, Material.CHERRY_SAPLING,
                java.util.Arrays.asList("§7Fresh spring blossoms", "§7celebrate your presence!"),
                null, false, false,
                Particle.CHERRY_LEAVES, 6, 0.4, 0.3, 0.4, 0.1, plugin
            );
            registerCosmetic(springTrail);
            
            // Firework Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic fireworkTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_firework", "Celebration Trail", 350, Material.FIREWORK_ROCKET,
                java.util.Arrays.asList("§7Colorful firework sparks", "§7celebrate every step!"),
                null, false, true,
                Particle.FIREWORK, 4, 0.3, 0.3, 0.3, 0.2, plugin
            );
            registerCosmetic(fireworkTrail);
            
            // === ANIMAL TRAILS ===
            
            // Paw Print Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic pawTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_paw", "Paw Prints", 140, Material.BONE,
                java.util.Arrays.asList("§7Cute paw prints", "§7follow your path!"),
                null, false, false,
                Particle.BLOCK, 2, 0.2, 0.1, 0.2, 0.05, plugin
            );
            registerCosmetic(pawTrail);
            
            // Butterfly Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic butterflyTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_butterfly", "Butterfly Flutter", 260, Material.FLOWER_POT,
                java.util.Arrays.asList("§7Graceful butterflies", "§7flutter around you!"),
                null, false, false,
                Particle.ENCHANT, 5, 0.5, 0.4, 0.5, 0.2, plugin
            );
            registerCosmetic(butterflyTrail);
            
            // === OCEAN TRAILS ===
            
            // Bubble Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic bubbleTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_bubble", "Ocean Bubbles", 180, Material.WATER_BUCKET,
                java.util.Arrays.asList("§7Refreshing bubbles", "§7rise from your steps!"),
                null, false, false,
                Particle.BUBBLE, 6, 0.3, 0.3, 0.3, 0.1, plugin
            );
            registerCosmetic(bubbleTrail);
            
            // Coral Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic coralTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_coral", "Coral Reef", 300, Material.BRAIN_CORAL,
                java.util.Arrays.asList("§7Vibrant coral pieces", "§7follow your movement!"),
                null, false, false,
                Particle.FALLING_WATER, 4, 0.3, 0.2, 0.3, 0.1, plugin
            );
            registerCosmetic(coralTrail);
            
            // === PREMIUM EXCLUSIVE TRAILS ===
            
            // Dragon Breath Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic dragonBreathTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_dragon_breath", "Dragon's Breath", 700, Material.DRAGON_BREATH,
                java.util.Arrays.asList("§7Mystical dragon breath", "§7swirls behind you!"),
                "sneakycosmetics.premium", true, true,
                Particle.DRAGON_BREATH, 5, 0.4, 0.4, 0.4, 0.2, plugin
            );
            registerCosmetic(dragonBreathTrail);
            
            // Phoenix Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic phoenixTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_phoenix", "Phoenix Fire", 800, Material.BLAZE_POWDER,
                java.util.Arrays.asList("§7Legendary phoenix flames", "§7rise from your path!"),
                "sneakycosmetics.premium", true, true,
                Particle.FLAME, 8, 0.3, 0.5, 0.3, 0.3, plugin
            );
            registerCosmetic(phoenixTrail);
            
            // Ender Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic enderTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_ender", "Ender Magic", 650, Material.ENDER_PEARL,
                java.util.Arrays.asList("§7Teleportation magic", "§7ripples behind you!"),
                "sneakycosmetics.premium", true, true,
                Particle.PORTAL, 7, 0.4, 0.4, 0.4, 0.25, plugin
            );
            registerCosmetic(enderTrail);
            
            // Galactic Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic galacticTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_galactic", "Galactic Stardust", 900, Material.END_CRYSTAL,
                java.util.Arrays.asList("§7Cosmic stardust from", "§7distant galaxies!"),
                "sneakycosmetics.premium", true, true,
                Particle.END_ROD, 6, 0.5, 0.6, 0.5, 0.3, plugin
            );
            registerCosmetic(galacticTrail);
            
            // Divine Trail
            com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic divineTrail = new com.sneaky.cosmetics.cosmetics.trails.TrailCosmetic(
                "trail_divine", "Divine Light", 1000, Material.BEACON,
                java.util.Arrays.asList("§7Holy divine light", "§7blesses your journey!"),
                "sneakycosmetics.premium", true, true,
                Particle.ENCHANT, 10, 0.4, 0.6, 0.4, 0.4, plugin
            );
            registerCosmetic(divineTrail);
            
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
            WingCosmetic angelWings = new WingCosmetic(
                "wings_angel", "Angel Wings", 500, Material.FEATHER,
                Arrays.asList("§7Pure white wings", "§7that shine with divine light!"),
                null, false, true,
                Particle.CLOUD, 6, 0.8, 1.5, 0.3, 0.1,
                WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(angelWings);
            
            // Demon Wings
            WingCosmetic demonWings = new WingCosmetic(
                "wings_demon", "Demon Wings", 600, Material.COAL,
                Arrays.asList("§7Dark wings that emanate", "§7an ominous presence!"),
                null, false, true,
                Particle.SMOKE, 8, 0.8, 1.5, 0.3, 0.15,
                WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(demonWings);
            
            // Butterfly Wings
            WingCosmetic butterflyWings = new WingCosmetic(
                "wings_butterfly", "Butterfly Wings", 350, Material.ORANGE_DYE,
                Arrays.asList("§7Delicate butterfly wings", "§7with beautiful colors!"),
                null, false, false,
                Particle.NOTE, 4, 0.6, 1.2, 0.3, 0.08,
                WingCosmetic.WingType.BUTTERFLY, plugin
            );
            registerCosmetic(butterflyWings);
            
            // Dragon Wings
            WingCosmetic dragonWings = new WingCosmetic(
                "wings_dragon", "Dragon Wings", 800, Material.DRAGON_BREATH,
                Arrays.asList("§7Mighty dragon wings", "§7with ancient power!"),
                "sneakycosmetics.premium", true, true,
                Particle.FLAME, 10, 1.0, 1.8, 0.4, 0.2,
                WingCosmetic.WingType.DRAGON, plugin
            );
            registerCosmetic(dragonWings);

            // Phoenix Wings
            WingCosmetic phoenixWings = new WingCosmetic(
                    "wings_phoenix", "Phoenix Wings", 750, Material.BLAZE_POWDER,
                    Arrays.asList("§7Fiery wings of a", "§7legendary phoenix!"),
                    null, false, true,
                    Particle.LAVA, 10, 0.9, 1.6, 0.3, 0.15,
                    WingCosmetic.WingType.DRAGON, plugin
            );
            registerCosmetic(phoenixWings);


            // Ice Wings
            WingCosmetic iceWings = new WingCosmetic(
                    "wings_ice", "Ice Wings", 550, Material.ICE,
                    Arrays.asList("§7Chilling wings that", "§7freeze the air around you!"),
                    null, false, true,
                    Particle.SNOWFLAKE, 6, 0.7, 1.4, 0.3, 0.12,
                    WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(iceWings);

// Fairy Wings
            WingCosmetic fairyWings = new WingCosmetic(
                    "wings_fairy", "Fairy Wings", 300, Material.PINK_DYE,
                    Arrays.asList("§7Sparkly fairy wings", "§7for magical moments!"),
                    null, false, false,
                    Particle.WITCH, 5, 0.6, 1.3, 0.2, 0.1,
                    WingCosmetic.WingType.BUTTERFLY, plugin
            );
            registerCosmetic(fairyWings);


            // Fallen Wings
            WingCosmetic fallenWings = new WingCosmetic(
                    "wings_fallen", "Fallen Wings", 650, Material.BLACK_DYE,
                    Arrays.asList("§7Wings of a fallen angel", "§7shrouded in mystery."),
                    null, false, true,
                    Particle.DRIPPING_OBSIDIAN_TEAR, 7, 0.8, 1.5, 0.3, 0.13,
                    WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(fallenWings);


            // Leaf Wings
            WingCosmetic leafWings = new WingCosmetic(
                    "wings_leaf", "Leaf Wings", 280, Material.OAK_LEAVES,
                    Arrays.asList("§7Natural leafy wings", "§7fluttering in the breeze!"),
                    null, false, false,
                    Particle.FALLING_HONEY, 4, 0.5, 1.1, 0.2, 0.09,
                    WingCosmetic.WingType.BUTTERFLY, plugin
            );
            registerCosmetic(leafWings);


            // Crystal Wings
            WingCosmetic crystalWings = new WingCosmetic(
                    "wings_crystal", "Crystal Wings", 700, Material.AMETHYST_SHARD,
                    Arrays.asList("§7Gleaming wings made", "§7of pure crystal!"),
                    null, false, true,
                    Particle.END_ROD, 8, 0.85, 1.6, 0.3, 0.14,
                    WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(crystalWings);

// Bat Wings
            WingCosmetic batWings = new WingCosmetic(
                    "wings_bat", "Bat Wings", 420, Material.BAT_SPAWN_EGG,
                    Arrays.asList("§7Silent wings of the night", "§7like a lurking bat."),
                    null, false, false,
                    Particle.CAMPFIRE_COSY_SMOKE, 5, 0.7, 1.3, 0.25, 0.1,
                    WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(batWings);

// Mechanical Wings
            WingCosmetic mechanicalWings = new WingCosmetic(
                    "wings_mech", "Mechanical Wings", 900, Material.REDSTONE,
                    Arrays.asList("§7Powered by gears and", "§7redstone energy!"),
                    "sneakycosmetics.premium", true, true,
                    Particle.SMOKE, 9, 0.95, 1.7, 0.3, 0.18,
                    WingCosmetic.WingType.DRAGON, plugin
            );
            registerCosmetic(mechanicalWings);

// Spirit Wings
            WingCosmetic spiritWings = new WingCosmetic(
                    "wings_spirit", "Spirit Wings", 580, Material.SOUL_SAND,
                    Arrays.asList("§7Ethereal wings of spirit", "§7glowing from the beyond."),
                    null, true, true,
                    Particle.SOUL, 6, 0.8, 1.5, 0.3, 0.13,
                    WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(spiritWings);

// Storm Wings
            WingCosmetic stormWings = new WingCosmetic(
                    "wings_storm", "Storm Wings", 770, Material.LIGHTNING_ROD,
                    Arrays.asList("§7Electrified wings that", "§7crackle with thunder!"),
                    null, false, true,
                    Particle.ELECTRIC_SPARK, 10, 1.0, 1.8, 0.3, 0.2,
                    WingCosmetic.WingType.DRAGON, plugin
            );
            registerCosmetic(stormWings);

            // Nebula Wings
            WingCosmetic nebulaWings = new WingCosmetic(
                    "wings_nebula", "Nebula Wings", 780, Material.PURPLE_DYE,
                    Arrays.asList("§7Wings formed of cosmic clouds", "§7and stardust light."),
                    "sneakycosmetics.premium", true, true,
                    Particle.WITCH, 9, 0.9, 1.7, 0.4, 0.18,
                    WingCosmetic.WingType.DRAGON, plugin
            );
            registerCosmetic(nebulaWings);

// Verdant Wings
            WingCosmetic verdantWings = new WingCosmetic(
                    "wings_verdant", "Verdant Wings", 410, Material.FERN,
                    Arrays.asList("§7Lush green wings", "§7overflowing with nature's energy."),
                    null, false, false,
                    Particle.LARGE_SMOKE, 5, 0.7, 1.3, 0.3, 0.1,
                    WingCosmetic.WingType.BUTTERFLY, plugin
            );
            registerCosmetic(verdantWings);

// Eclipse Wings
            WingCosmetic eclipseWings = new WingCosmetic(
                    "wings_eclipse", "Eclipse Wings", 690, Material.BLACK_CONCRETE,
                    Arrays.asList("§7Darkness meets light", "§7in celestial balance."),
                    null, false, true,
                    Particle.SMOKE, 8, 0.85, 1.6, 0.3, 0.14,
                    WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(eclipseWings);

// Prism Wings
            WingCosmetic prismWings = new WingCosmetic(
                    "wings_prism", "Prism Wings", 530, Material.ARROW,
                    Arrays.asList("§7Wings refracting light", "§7into vibrant rainbows."),
                    null, false, true,
                    Particle.CRIT, 6, 0.8, 1.5, 0.3, 0.12,
                    WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(prismWings);

// Ember Wings
            WingCosmetic emberWings = new WingCosmetic(
                    "wings_ember", "Ember Wings", 620, Material.FIRE_CHARGE,
                    Arrays.asList("§7Glowing embers drift", "§7from your fiery wings."),
                    null, false, true,
                    Particle.LAVA, 8, 0.85, 1.6, 0.35, 0.15,
                    WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(emberWings);

// Aurora Wings
            WingCosmetic auroraWings = new WingCosmetic(
                    "wings_aurora", "Aurora Wings", 760, Material.LIGHT_BLUE_DYE,
                    Arrays.asList("§7Wings shimmering like", "§7northern lights."),
                    "sneakycosmetics.premium", true, true,
                    Particle.RAIN, 9, 0.9, 1.7, 0.4, 0.18,
                    WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(auroraWings);

// Venom Wings
            WingCosmetic venomWings = new WingCosmetic(
                    "wings_venom", "Venom Wings", 600, Material.SPIDER_EYE,
                    Arrays.asList("§7Toxic wings dripping", "§7with dark poison."),
                    null, false, true,
                    Particle.DRIPPING_LAVA, 7, 0.8, 1.5, 0.3, 0.14,
                    WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(venomWings);

// Windrider Wings
            WingCosmetic windriderWings = new WingCosmetic(
                    "wings_windrider", "Windrider Wings", 450, Material.WHITE_WOOL,
                    Arrays.asList("§7Light as the breeze", "§7swift as the storm."),
                    null, false, false,
                    Particle.CLOUD, 6, 0.75, 1.4, 0.3, 0.1,
                    WingCosmetic.WingType.BUTTERFLY, plugin
            );
            registerCosmetic(windriderWings);

// Obsidian Wings
            WingCosmetic obsidianWings = new WingCosmetic(
                    "wings_obsidian", "Obsidian Wings", 670, Material.OBSIDIAN,
                    Arrays.asList("§7Heavy black wings", "§7forged in the void."),
                    "sneakycosmetics.premium", true, true,
                    Particle.LARGE_SMOKE, 8, 0.9, 1.6, 0.35, 0.15,
                    WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(obsidianWings);

// Twilight Bloom Wings
            WingCosmetic twilightBloomWings = new WingCosmetic(
                    "wings_twilight_bloom", "Twilight Bloom Wings", 580, Material.AZALEA,
                    Arrays.asList("§7Petals glow softly", "§7in the fading light."),
                    null, false, false,
                    Particle.FALLING_SPORE_BLOSSOM, 6, 0.7, 1.4, 0.3, 0.11,
                    WingCosmetic.WingType.BUTTERFLY, plugin
            );
            registerCosmetic(twilightBloomWings);

// Celestial Feather Wings
            WingCosmetic celestialFeatherWings = new WingCosmetic(
                    "wings_celestial_feather", "Celestial Feather Wings", 540, Material.FEATHER,
                    Arrays.asList("§7Lightworked feathers", "§7with heavenly grace."),
                    null, false, true,
                    Particle.CLOUD, 7, 0.85, 1.6, 0.3, 0.12,
                    WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(celestialFeatherWings);

// Inferno Drake Wings
            WingCosmetic infernoDrakeWings = new WingCosmetic(
                    "wings_inferno_drake", "Inferno Drake Wings", 720, Material.DRAGON_BREATH,
                    Arrays.asList("§7Scales blazing", "§7with draconic fury."),
                    "sneakycosmetics.premium", true, true,
                    Particle.FLAME, 9, 0.9, 1.7, 0.35, 0.15,
                    WingCosmetic.WingType.DRAGON, plugin
            );
            registerCosmetic(infernoDrakeWings);

// Glacier Spirit Wings
            WingCosmetic glacierSpiritWings = new WingCosmetic(
                    "wings_glacier_spirit", "Glacier Spirit Wings", 560, Material.PACKED_ICE,
                    Arrays.asList("§7Haunting icy spirit", "§7echoes with chill."),
                    null, false, true,
                    Particle.SNOWFLAKE, 7, 0.8, 1.5, 0.3, 0.11,
                    WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(glacierSpiritWings);

// Arcane Prism Wings
            WingCosmetic arcanePrismWings = new WingCosmetic(
                    "wings_arcane_prism", "Arcane Prism Wings", 650, Material.ARROW,
                    Arrays.asList("§7Wings shimmering", "§7with arcane hues."),
                    null, false, true,
                    Particle.CRIT, 8, 0.9, 1.6, 0.35, 0.14,
                    WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(arcanePrismWings);

// Thorned Rose Wings
            WingCosmetic thornedRoseWings = new WingCosmetic(
                    "wings_thorned_rose", "Thorned Rose Wings", 440, Material.RED_DYE,
                    Arrays.asList("§7Petals and thorns", "§7in perfect bloom."),
                    null, false, false,
                    Particle.HEART, 6, 0.75, 1.4, 0.3, 0.1,
                    WingCosmetic.WingType.BUTTERFLY, plugin
            );
            registerCosmetic(thornedRoseWings);

// Emberstorm Wings
            WingCosmetic emberstormWings = new WingCosmetic(
                    "wings_emberstorm", "Emberstorm Wings", 760, Material.FIRE_CHARGE,
                    Arrays.asList("§7Sparks and embers swirl", "§7with each beat."),
                    "sneakycosmetics.premium", true, true,
                    Particle.FLAME, 10, 1.0, 1.8, 0.4, 0.2,
                    WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(emberstormWings);

// Ocean Mist Wings
            WingCosmetic oceanMistWings = new WingCosmetic(
                    "wings_ocean_mist", "Ocean Mist Wings", 500, Material.PRISMARINE_CRYSTALS,
                    Arrays.asList("§7Misty foam drifts", "§7on your ocean wings."),
                    null, false, true,
                    Particle.DRIPPING_WATER, 7, 0.8, 1.5, 0.3, 0.11,
                    WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(oceanMistWings);

// Starfall Wings
            WingCosmetic starfallWings = new WingCosmetic(
                    "wings_starfal", "Starfall Wings", 780, Material.GLOWSTONE_DUST,
                    Arrays.asList("§7Constellations sparkle", "§7as you glide."),
                    "sneakycosmetics.premium", true, true,
                    Particle.END_ROD, 9, 0.9, 1.7, 0.35, 0.15,
                    WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(starfallWings);

// Abyssal Depths Wings
            WingCosmetic abyssalDepthsWings = new WingCosmetic(
                    "wings_abyssal_depths", "Abyssal Depths Wings", 700, Material.BLACK_CONCRETE,
                    Arrays.asList("§7Echoes from the deep", "§7haunt your wings."),
                    null, false, true,
                    Particle.SMOKE, 8, 0.85, 1.6, 0.35, 0.14,
                    WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(abyssalDepthsWings);

// Solar Flare Wings
            WingCosmetic solarFlareWings = new WingCosmetic(
                    "wings_solar_flare", "Solar Flare Wings", 740, Material.GOLD_INGOT,
                    Arrays.asList("§7Wings blazing bright", "§7with solar power."),
                    null, false, true,
                    Particle.FLAME, 9, 0.9, 1.7, 0.35, 0.15,
                    WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(solarFlareWings);

// Jade Lotus Wings
            WingCosmetic jadeLotusWings = new WingCosmetic(
                    "wings_jade_lotus", "Jade Lotus Wings", 460, Material.EMERALD,
                    Arrays.asList("§7Leaf and lotus petals", "§7blending in beauty."),
                    null, false, false,
                    Particle.LARGE_SMOKE, 7, 0.75, 1.4, 0.3, 0.12,
                    WingCosmetic.WingType.BUTTERFLY, plugin
            );
            registerCosmetic(jadeLotusWings);

// Stormbreaker Wings
            WingCosmetic stormbreakerWings = new WingCosmetic(
                    "wings_stormbreaker", "Stormbreaker Wings", 800, Material.LEATHER,
                    Arrays.asList("§7Lightning arcs across", "§7your mighty wings."),
                    "sneakycosmetics.premium", true, true,
                    Particle.ELECTRIC_SPARK, 10, 1.0, 1.8, 0.4, 0.2,
                    WingCosmetic.WingType.DRAGON, plugin
            );
            registerCosmetic(stormbreakerWings);

// Moonshadow Wings
            WingCosmetic moonshadowWings = new WingCosmetic(
                    "wings_moonshadow", "Moonshadow Wings", 520, Material.SNOWBALL,
                    Arrays.asList("§7Pale light glimmers", "§7on your quiet wings."),
                    null, false, true,
                    Particle.ITEM_SNOWBALL, 7, 0.8, 1.5, 0.3, 0.11,
                    WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(moonshadowWings);

// Infernal Ember Wings
            WingCosmetic infernalEmberWings = new WingCosmetic(
                    "wings_infernal_ember", "Infernal Ember Wings", 780, Material.NETHER_BRICK,
                    Arrays.asList("§7Molten cracks glow", "§7through your wings."),
                    "sneakycosmetics.premium", true, true,
                    Particle.LAVA, 10, 1.0, 1.8, 0.4, 0.2,
                    WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(infernalEmberWings);

// Blossom Breeze Wings
            WingCosmetic blossomBreezeWings = new WingCosmetic(
                    "wings_blossom_breeze", "Blossom Breeze Wings", 480, Material.AZALEA,
                    Arrays.asList("§7Soft petals drift", "§7on a gentle breeze."),
                    null, false, false,
                    Particle.FALLING_SPORE_BLOSSOM, 6, 0.7, 1.4, 0.3, 0.11,
                    WingCosmetic.WingType.BUTTERFLY, plugin
            );
            registerCosmetic(blossomBreezeWings);

// Radiant Halo Wings
            WingCosmetic radiantHaloWings = new WingCosmetic(
                    "wings_radiant_halo", "Radiant Halo Wings", 720, Material.GLOWSTONE_DUST,
                    Arrays.asList("§7Glowing halos swirl", "§7as light trails behind."),
                    null, false, true,
                    Particle.END_ROD, 9, 0.9, 1.7, 0.35, 0.15,
                    WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(radiantHaloWings);

// Duskfire Wings
            WingCosmetic duskfireWings = new WingCosmetic(
                    "wings_duskfire", "Duskfire Wings", 680, Material.ORANGE_DYE,
                    Arrays.asList("§7Ash and embers swirl", "§7at dusk’s last light."),
                    null, false, true,
                    Particle.LAVA, 9, 0.85, 1.6, 0.35, 0.14,
                    WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(duskfireWings);

// Serene Petal Wings
            WingCosmetic serenePetalWings = new WingCosmetic(
                    "wings_serene_petal", "Serene Petal Wings", 460, Material.PINK_DYE,
                    Arrays.asList("§7Whispers of petals", "§7on peaceful wings."),
                    null, false, false,
                    Particle.HEART, 6, 0.7, 1.4, 0.3, 0.1,
                    WingCosmetic.WingType.BUTTERFLY, plugin
            );
            registerCosmetic(serenePetalWings);

// Astral Mirage Wings
            WingCosmetic astralMirageWings = new WingCosmetic(
                    "wings_astral_mirage", "Astral Mirage Wings", 720, Material.ENDER_PEARL,
                    Arrays.asList("§7Wings that shimmer", "§7like distant stars."),
                    null, false, true,
                    Particle.PORTAL, 8, 0.9, 1.6, 0.3, 0.14,
                    WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(astralMirageWings);

// Bloodthorn Wings
            WingCosmetic bloodthornWings = new WingCosmetic(
                    "wings_bloodthorn", "Bloodthorn Wings", 690, Material.NETHER_WART,
                    Arrays.asList("§7Crimson thorns wrap", "§7your sinister flight."),
                    null, false, true,
                    Particle.DRIPPING_LAVA, 7, 0.85, 1.5, 0.3, 0.12,
                    WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(bloodthornWings);

// Verdant Bloom Wings
            WingCosmetic verdantBloomWings = new WingCosmetic(
                    "wings_verdant_bloom", "Verdant Bloom Wings", 510, Material.LILY_PAD,
                    Arrays.asList("§7Nature blooms in", "§7every fluttering beat."),
                    null, false, false,
                    Particle.SPLASH, 5, 0.7, 1.3, 0.3, 0.1,
                    WingCosmetic.WingType.BUTTERFLY, plugin
            );
            registerCosmetic(verdantBloomWings);

// Phantom Veil Wings
            WingCosmetic phantomVeilWings = new WingCosmetic(
                    "wings_phantom_veil", "Phantom Veil Wings", 640, Material.PHANTOM_MEMBRANE,
                    Arrays.asList("§7Ethereal shadows cling", "§7as you vanish in air."),
                    null, false, true,
                    Particle.LARGE_SMOKE, 6, 0.85, 1.5, 0.3, 0.13,
                    WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(phantomVeilWings);

// Luminous Reef Wings
            WingCosmetic luminousReefWings = new WingCosmetic(
                    "wings_luminous_reef", "Luminous Reef Wings", 600, Material.SEA_PICKLE,
                    Arrays.asList("§7Bioluminescent glow", "§7from deep sea coral."),
                    null, false, true,
                    Particle.BUBBLE_COLUMN_UP, 6, 0.75, 1.4, 0.3, 0.12,
                    WingCosmetic.WingType.BUTTERFLY, plugin
            );
            registerCosmetic(luminousReefWings);

// Shadowmantle Wings
            WingCosmetic shadowmantleWings = new WingCosmetic(
                    "wings_shadowmantle", "Shadowmantle Wings", 710, Material.BLACK_DYE,
                    Arrays.asList("§7Darkness flows", "§7like ink behind you."),
                    null, true, true,
                    Particle.ASH, 7, 0.9, 1.6, 0.3, 0.14,
                    WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(shadowmantleWings);

// Honeydrip Wings
            WingCosmetic honeydripWings = new WingCosmetic(
                    "wings_honeydrip", "Honeydrip Wings", 460, Material.HONEYCOMB,
                    Arrays.asList("§7Sticky and sweet", "§7as buzzing bees."),
                    null, false, false,
                    Particle.FALLING_HONEY, 5, 0.7, 1.3, 0.25, 0.1,
                    WingCosmetic.WingType.BUTTERFLY, plugin
            );
            registerCosmetic(honeydripWings);

// Cursed Ember Wings
            WingCosmetic cursedEmberWings = new WingCosmetic(
                    "wings_cursed_ember", "Cursed Ember Wings", 770, Material.MAGMA_CREAM,
                    Arrays.asList("§7Burned by a curse", "§7and left to fly."),
                    "sneakycosmetics.premium", true, true,
                    Particle.DRIPPING_LAVA, 9, 0.95, 1.7, 0.35, 0.18,
                    WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(cursedEmberWings);

// Aurora Dance Wings
            WingCosmetic auroraDanceWings = new WingCosmetic(
                    "wings_aurora_dance", "Aurora Dance Wings", 720, Material.LIGHT_BLUE_DYE,
                    Arrays.asList("§7Light weaves colors", "§7in the night sky."),
                    null, false, true,
                    Particle.FIREWORK, 8, 0.9, 1.6, 0.3, 0.14,
                    WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(auroraDanceWings);

// Serpent Scale Wings
            WingCosmetic serpentScaleWings = new WingCosmetic(
                    "wings_serpent_scale", "Serpent Scale Wings", 680, Material.SCAFFOLDING,
                    Arrays.asList("§7Shed from ancient serpents", "§7in deep jungle tombs."),
                    null, false, true,
                    Particle.ITEM_SLIME, 7, 0.85, 1.6, 0.3, 0.13,
                    WingCosmetic.WingType.DRAGON, plugin
            );
            registerCosmetic(serpentScaleWings);

// Wispveil Wings
            WingCosmetic wispveilWings = new WingCosmetic(
                    "wings_wispveil", "Wispveil Wings", 630, Material.WHITE_DYE,
                    Arrays.asList("§7Gliding phantoms", "§7follow in your trail."),
                    null, false, true,
                    Particle.SPLASH, 6, 0.75, 1.4, 0.3, 0.12,
                    WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(wispveilWings);

// Duskleaf Wings
            WingCosmetic duskleafWings = new WingCosmetic(
                    "wings_duskleaf", "Duskleaf Wings", 490, Material.JUNGLE_LEAVES,
                    Arrays.asList("§7Faded leaves swirl", "§7in twilight wind."),
                    null, false, false,
                    Particle.FALLING_DUST, 5, 0.7, 1.3, 0.3, 0.1,
                    WingCosmetic.WingType.DRAGON, plugin
            );
            registerCosmetic(duskleafWings);

// Ironclad Wings
            WingCosmetic ironcladWings = new WingCosmetic(
                    "wings_ironclad", "Ironclad Wings", 800, Material.IRON_BLOCK,
                    Arrays.asList("§7Heavy as steel", "§7but lift you just the same."),
                    "sneakycosmetics.premium", true, true,
                    Particle.CRIT, 10, 1.0, 1.8, 0.4, 0.2,
                    WingCosmetic.WingType.DRAGON, plugin
            );
            registerCosmetic(ironcladWings);

// Astral Mirage Wings
            WingCosmetic astralMirageWing = new WingCosmetic(
                    "wings_astral_mirage", "Astral Mirage Wings", 720, Material.ENDER_PEARL,
                    Arrays.asList("§7Wings that shimmer", "§7like distant stars."),
                    null, false, true,
                    Particle.PORTAL, 8, 0.9, 1.6, 0.3, 0.14,
                    WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(astralMirageWings);

// Bloodthorn Wings
            WingCosmetic bloodthornWing = new WingCosmetic(
                    "wings_bloodthorn", "Bloodthorn Wings", 690, Material.NETHER_WART,
                    Arrays.asList("§7Crimson thorns wrap", "§7your sinister flight."),
                    null, false, true,
                    Particle.DRIPPING_LAVA, 7, 0.85, 1.5, 0.3, 0.12,
                    WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(bloodthornWings);

// Verdant Bloom Wings
            WingCosmetic verdantBloomWing = new WingCosmetic(
                    "wings_verdant_bloom", "Verdant Bloom Wings", 510, Material.LILY_PAD,
                    Arrays.asList("§7Nature blooms in", "§7every fluttering beat."),
                    null, false, false,
                    Particle.SPLASH, 5, 0.7, 1.3, 0.3, 0.1,
                    WingCosmetic.WingType.BUTTERFLY, plugin
            );
            registerCosmetic(verdantBloomWings);

// Phantom Veil Wings
            WingCosmetic phantomVeilWing = new WingCosmetic(
                    "wings_phantom_veil", "Phantom Veil Wings", 640, Material.PHANTOM_MEMBRANE,
                    Arrays.asList("§7Ethereal shadows cling", "§7as you vanish in air."),
                    null, false, true,
                    Particle.LARGE_SMOKE, 6, 0.85, 1.5, 0.3, 0.13,
                    WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(phantomVeilWings);

// Luminous Reef Wings
            WingCosmetic Luminous = new WingCosmetic(
                    "wings_luminous_reef", "Luminous Reef Wings", 600, Material.SEA_PICKLE,
                    Arrays.asList("§7Bioluminescent glow", "§7from deep sea coral."),
                    null, false, true,
                    Particle.BUBBLE_COLUMN_UP, 6, 0.75, 1.4, 0.3, 0.12,
                    WingCosmetic.WingType.BUTTERFLY, plugin
            );
            registerCosmetic(luminousReefWings);

// Shadowmantle Wings
            WingCosmetic shadowmantleWing = new WingCosmetic(
                    "wings_shadowmantle", "Shadowmantle Wings", 710, Material.BLACK_DYE,
                    Arrays.asList("§7Darkness flows", "§7like ink behind you."),
                    null, true, true,
                    Particle.ASH, 7, 0.9, 1.6, 0.3, 0.14,
                    WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(shadowmantleWings);

// Honeydrip Wings
            WingCosmetic honeydripWing = new WingCosmetic(
                    "wings_honeydrip", "Honeydrip Wings", 460, Material.HONEYCOMB,
                    Arrays.asList("§7Sticky and sweet", "§7as buzzing bees."),
                    null, false, false,
                    Particle.FALLING_HONEY, 5, 0.7, 1.3, 0.25, 0.1,
                    WingCosmetic.WingType.BUTTERFLY, plugin
            );
            registerCosmetic(honeydripWings);

// Cursed Ember Wings
            WingCosmetic cursedEmberWing = new WingCosmetic(
                    "wings_cursed_ember", "Cursed Ember Wings", 770, Material.MAGMA_CREAM,
                    Arrays.asList("§7Burned by a curse", "§7and left to fly."),
                    "sneakycosmetics.premium", true, true,
                    Particle.DRIPPING_LAVA, 9, 0.95, 1.7, 0.35, 0.18,
                    WingCosmetic.WingType.DEMON, plugin
            );
            registerCosmetic(cursedEmberWings);

// Aurora Dance Wings
            WingCosmetic auroraDanceWing = new WingCosmetic(
                    "wings_aurora_dance", "Aurora Dance Wings", 720, Material.LIGHT_BLUE_DYE,
                    Arrays.asList("§7Light weaves colors", "§7in the night sky."),
                    null, false, true,
                    Particle.SPLASH, 8, 0.9, 1.6, 0.3, 0.14,
                    WingCosmetic.WingType.ANGEL, plugin
            );
            registerCosmetic(auroraDanceWings);

// Serpent Scale Wings
            WingCosmetic serpentScaleWing = new WingCosmetic(
                    "wings_serpent_scale", "Serpent Scale Wings", 680, Material.SCAFFOLDING,
                    Arrays.asList("§7Shed from ancient serpents", "§7in deep jungle tombs."),
                    null, false, true,
                    Particle.ITEM_SLIME, 7, 0.85, 1.6, 0.3, 0.13,
                    WingCosmetic.WingType.DRAGON, plugin
            );
            registerCosmetic(serpentScaleWings);

// Wispveil Wings
            WingCosmetic wispveilWing = new WingCosmetic(
                    "wings_wispveil", "Wispveil Wings", 630, Material.WHITE_DYE,
                    Arrays.asList("§7Gliding phantoms", "§7follow in your trail."),
                    null, false, true,
                    Particle.INSTANT_EFFECT, 6, 0.75, 1.4, 0.3, 0.12,
                    WingCosmetic.WingType.DRAGON, plugin
            );
            registerCosmetic(wispveilWings);

// Duskleaf Wings
            WingCosmetic duskleafWing = new WingCosmetic(
                    "wings_duskleaf", "Duskleaf Wings", 490, Material.JUNGLE_LEAVES,
                    Arrays.asList("§7Faded leaves swirl", "§7in twilight wind."),
                    null, false, false,
                    Particle.FALLING_DUST, 5, 0.7, 1.3, 0.3, 0.1,
                    WingCosmetic.WingType.DRAGON, plugin
            );
            registerCosmetic(duskleafWings);

// Ironclad Wings
            WingCosmetic ironcladWing = new WingCosmetic(
                    "wings_ironclad", "Ironclad Wings", 800, Material.IRON_BLOCK,
                    Arrays.asList("§7Heavy as steel", "§7but lift you just the same."),
                    "sneakycosmetics.premium", true, true,
                    Particle.CRIT, 10, 1.0, 1.8, 0.4, 0.2,
                    WingCosmetic.WingType.DRAGON, plugin
            );
            registerCosmetic(ironcladWings);


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
    
    private void registerMorphCosmetics() {
        try {
            Class.forName("com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic");
            
            // === COMMON MORPHS ===
            
            // Wolf Morph
            com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic wolfMorph = new com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic(
                "morph_wolf", "Wolf", 500, Material.BONE,
                java.util.Arrays.asList("§7Transform into a loyal wolf companion", "§7with pack-based abilities!"),
                "sneakycosmetics.morph.wolf", false, false,
                org.bukkit.entity.EntityType.WOLF, false, true, 1.0,
                java.util.Arrays.asList("Pack Howl - Give speed boost to nearby players", "Enhanced senses - Detect nearby entities"),
                "ENTITY_WOLF_AMBIENT", "ENTITY_WOLF_WHINE"
            );
            registerCosmetic(wolfMorph);
            
            // Rabbit Morph
            com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic rabbitMorph = new com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic(
                "morph_rabbit", "Rabbit", 300, Material.CARROT,
                java.util.Arrays.asList("§7Hop around as an adorable rabbit", "§7with enhanced jumping abilities!"),
                "sneakycosmetics.morph.rabbit", false, false,
                org.bukkit.entity.EntityType.RABBIT, false, true, 1.0,
                java.util.Arrays.asList("Super Jump - Jump higher than normal", "Speed Boost - Move faster when hopping"),
                "ENTITY_RABBIT_AMBIENT", "ENTITY_RABBIT_HURT"
            );
            registerCosmetic(rabbitMorph);
            
            // Bat Morph
            com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic batMorph = new com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic(
                "morph_bat", "Bat", 400, Material.LEATHER,
                java.util.Arrays.asList("§7Transform into a night-flying bat", "§7with echolocation powers!"),
                "sneakycosmetics.morph.bat", false, false,
                org.bukkit.entity.EntityType.BAT, true, true, 0.5,
                java.util.Arrays.asList("Echolocation - Detect nearby entities", "Night Vision - See in darkness"),
                "ENTITY_BAT_AMBIENT", "ENTITY_BAT_HURT"
            );
            registerCosmetic(batMorph);
            
            // Squid Morph
            com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic squidMorph = new com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic(
                "morph_squid", "Squid", 350, Material.INK_SAC,
                java.util.Arrays.asList("§7Become a mysterious squid", "§7with underwater abilities!"),
                "sneakycosmetics.morph.squid", false, false,
                org.bukkit.entity.EntityType.SQUID, false, true, 1.0,
                java.util.Arrays.asList("Ink Cloud - Create defensive ink cloud", "Water Breathing - Breathe underwater"),
                "ENTITY_SQUID_AMBIENT", "ENTITY_SQUID_HURT"
            );
            registerCosmetic(squidMorph);
            
            // === RARE MORPHS ===
            
            // Horse Morph
            com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic horseMorph = new com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic(
                "morph_horse", "Horse", 600, Material.SADDLE,
                java.util.Arrays.asList("§7Transform into a majestic horse", "§7with incredible speed!"),
                "sneakycosmetics.morph.horse", false, false,
                org.bukkit.entity.EntityType.HORSE, false, true, 1.6,
                java.util.Arrays.asList("Gallop - Move at incredible speeds", "Jump Boost - Leap over obstacles"),
                "ENTITY_HORSE_AMBIENT", "ENTITY_HORSE_HURT"
            );
            registerCosmetic(horseMorph);
            
            // Creeper Morph
            com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic creeperMorph = new com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic(
                "morph_creeper", "Creeper", 550, Material.GUNPOWDER,
                java.util.Arrays.asList("§7Become the feared creeper", "§7with explosive abilities!"),
                "sneakycosmetics.morph.creeper", false, false,
                org.bukkit.entity.EntityType.CREEPER, false, true, 1.0,
                java.util.Arrays.asList("Explosion - Create harmless fireworks", "Sneak Walk - Move silently"),
                "ENTITY_CREEPER_PRIMED", "ENTITY_CREEPER_HURT"
            );
            registerCosmetic(creeperMorph);
            
            // === EPIC MORPHS ===
            
            // Spider Morph
            com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic spiderMorph = new com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic(
                "morph_spider", "Spider", 700, Material.STRING,
                java.util.Arrays.asList("§7Transform into a venomous spider", "§7with wall-climbing abilities!"),
                "sneakycosmetics.morph.spider", false, true,
                org.bukkit.entity.EntityType.SPIDER, false, true, 1.0,
                java.util.Arrays.asList("Wall Climb - Climb walls and ceilings", "Web Trap - Create web traps"),
                "ENTITY_SPIDER_AMBIENT", "ENTITY_SPIDER_HURT"
            );
            registerCosmetic(spiderMorph);
            
            // Enderman Morph
            com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic endermanMorph = new com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic(
                "morph_enderman", "Enderman", 800, Material.ENDER_PEARL,
                java.util.Arrays.asList("§7Become the mysterious enderman", "§7with teleportation powers!"),
                "sneakycosmetics.morph.enderman", false, true,
                org.bukkit.entity.EntityType.ENDERMAN, false, true, 2.9,
                java.util.Arrays.asList("Teleport - Instantly move to target location", "Block Pickup - Pick up blocks"),
                "ENTITY_ENDERMAN_AMBIENT", "ENTITY_ENDERMAN_HURT"
            );
            registerCosmetic(endermanMorph);
            
            // Blaze Morph
            com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic blazeMorph = new com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic(
                "morph_blaze", "Blaze", 750, Material.BLAZE_ROD,
                java.util.Arrays.asList("§7Transform into a fiery blaze", "§7with fire-based abilities!"),
                "sneakycosmetics.morph.blaze", false, true,
                org.bukkit.entity.EntityType.BLAZE, true, true, 1.8,
                java.util.Arrays.asList("Fireball - Launch fireballs", "Fire Immunity - Immune to fire damage"),
                "ENTITY_BLAZE_AMBIENT", "ENTITY_BLAZE_HURT"
            );
            registerCosmetic(blazeMorph);
            
            // === LEGENDARY MORPHS ===
            
            // Ender Dragon Morph
            com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic dragonMorph = new com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic(
                "morph_dragon", "Ender Dragon", 1500, Material.DRAGON_EGG,
                java.util.Arrays.asList("§7Become the legendary Ender Dragon", "§7with ultimate powers!"),
                "sneakycosmetics.morph.dragon", true, true,
                org.bukkit.entity.EntityType.ENDER_DRAGON, true, true, 8.0,
                java.util.Arrays.asList("Dragon Breath - Breathe deadly acid", "Flight - Soar through the skies"),
                "ENTITY_ENDER_DRAGON_AMBIENT", "ENTITY_ENDER_DRAGON_HURT"
            );
            registerCosmetic(dragonMorph);
            
            // Phoenix Morph
            com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic phoenixMorph = new com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic(
                "morph_phoenix", "Phoenix", 1200, Material.BLAZE_POWDER,
                java.util.Arrays.asList("§7Rise as the mythical phoenix", "§7with rebirth abilities!"),
                "sneakycosmetics.morph.phoenix", true, true,
                org.bukkit.entity.EntityType.BLAZE, true, true, 1.8,
                java.util.Arrays.asList("Rebirth - Resurrect after death", "Phoenix Fire - Create healing flames"),
                "ENTITY_BLAZE_AMBIENT", "ENTITY_BLAZE_DEATH"
            );
            registerCosmetic(phoenixMorph);
            
            // Wither Morph
            com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic witherMorph = new com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic(
                "morph_wither", "Wither", 1800, Material.NETHER_STAR,
                java.util.Arrays.asList("§7Transform into the devastating Wither", "§7with destructive powers!"),
                "sneakycosmetics.morph.wither", true, true,
                org.bukkit.entity.EntityType.WITHER, true, true, 3.5,
                java.util.Arrays.asList("Wither Skull - Launch explosive skulls", "Wither Effect - Apply wither to enemies"),
                "ENTITY_WITHER_AMBIENT", "ENTITY_WITHER_HURT"
            );
            registerCosmetic(witherMorph);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to register morph cosmetics: " + e.getMessage());
        }
    }
}
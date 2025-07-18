package com.sneaky.cosmetics.cosmetics.morphs;

import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Represents a morph cosmetic that transforms players into different entities
 * Handles disguise functionality and morph-specific behavior
 */
public class MorphCosmetic extends Cosmetic {
    
    private final EntityType entityType;
    private final boolean canFly;
    private final boolean hasSpecialAbilities;
    private final double sizeMultiplier;
    private final List<String> abilities;
    private final String morphSound;
    private final String demorphSound;
    
    public MorphCosmetic(String id, String displayName, int price, Material iconMaterial,
                        List<String> description, String permission, boolean requiresVIP,
                        boolean requiresPremium, EntityType entityType, boolean canFly,
                        boolean hasSpecialAbilities, double sizeMultiplier, List<String> abilities,
                        String morphSound, String demorphSound) {
        super(id, displayName, CosmeticType.MORPH, price, iconMaterial, description, 
              permission, requiresVIP, requiresPremium);
        this.entityType = entityType;
        this.canFly = canFly;
        this.hasSpecialAbilities = hasSpecialAbilities;
        this.sizeMultiplier = sizeMultiplier;
        this.abilities = abilities;
        this.morphSound = morphSound;
        this.demorphSound = demorphSound;
    }
    
    /**
     * Get the entity type this morph transforms into
     */
    public EntityType getEntityType() {
        return entityType;
    }
    
    /**
     * Check if this morph can fly
     */
    public boolean canFly() {
        return canFly;
    }
    
    /**
     * Check if this morph has special abilities
     */
    public boolean hasSpecialAbilities() {
        return hasSpecialAbilities;
    }
    
    /**
     * Get the size multiplier for this morph
     */
    public double getSizeMultiplier() {
        return sizeMultiplier;
    }
    
    /**
     * Get the list of abilities this morph provides
     */
    public List<String> getAbilities() {
        return abilities;
    }
    
    /**
     * Get the sound played when morphing
     */
    public String getMorphSound() {
        return morphSound;
    }
    
    /**
     * Get the sound played when demorphing
     */
    public String getDemorphSound() {
        return demorphSound;
    }
    
    /**
     * Apply the morph to a player
     */
    @Override
    public void activate(Player player) {
        // Set the disguise using LibsDisguises or similar
        applyDisguise(player);
        
        // Apply special abilities if any
        if (hasSpecialAbilities) {
            applySpecialAbilities(player);
            player.sendMessage("§e✨ Special abilities activated! Sneak + Right-click to use them.");
        }
        
        // Play morph sound
        if (morphSound != null && !morphSound.isEmpty()) {
            playMorphSound(player);
        }
        
        // Show ability information
        if (hasSpecialAbilities && !abilities.isEmpty()) {
            player.sendMessage("§6🔥 Available Abilities:");
            for (String ability : abilities) {
                player.sendMessage("§7• " + ability);
            }
        }
    }
    
    /**
     * Remove the morph from a player
     */
    @Override
    public void deactivate(Player player) {
        // Remove the disguise
        removeDisguise(player);
        
        // Remove special abilities
        if (hasSpecialAbilities) {
            removeSpecialAbilities(player);
        }
        
        // Play demorph sound
        if (demorphSound != null && !demorphSound.isEmpty()) {
            playDemorphSound(player);
        }
    }
    
    /**
     * Check if this cosmetic is currently active for the player
     */
    @Override
    public boolean isActive(Player player) {
        // Check if player is currently disguised as this entity type
        return isPlayerDisguised(player);
    }
    
    /**
     * Apply the disguise to the player
     * This would integrate with LibsDisguises or similar plugin
     */
    private void applyDisguise(Player player) {
        // Store morph state in player metadata
        player.setMetadata("sneaky_morph", new org.bukkit.metadata.FixedMetadataValue(
            org.bukkit.Bukkit.getPluginManager().getPlugin("SneakyCosmetics"), getId()));
        
        // Apply visual effects for morph transformation
        org.bukkit.Location loc = player.getLocation();
        player.getWorld().spawnParticle(org.bukkit.Particle.CLOUD, loc.clone().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
        player.getWorld().spawnParticle(org.bukkit.Particle.ENCHANT, loc.clone().add(0, 1, 0), 30, 0.8, 1.0, 0.8, 0.1);
        
        // Send messages to nearby players about the transformation
        String morphName = getDisplayName();
        for (Player nearby : player.getWorld().getPlayers()) {
            if (nearby.getLocation().distance(loc) <= 20) {
                if (nearby == player) {
                    nearby.sendMessage("§6🔄 You transformed into " + morphName + "!");
                } else {
                    nearby.sendMessage("§7" + player.getName() + " transformed into " + morphName + "!");
                }
            }
        }
    }
    
    /**
     * Remove the disguise from the player
     */
    private void removeDisguise(Player player) {
        // Apply visual effects for morph removal
        org.bukkit.Location loc = player.getLocation();
        player.getWorld().spawnParticle(org.bukkit.Particle.POOF, loc.clone().add(0, 1, 0), 15, 0.5, 0.5, 0.5, 0.1);
        
        // Send messages to nearby players about the transformation ending
        for (Player nearby : player.getWorld().getPlayers()) {
            if (nearby.getLocation().distance(loc) <= 20) {
                if (nearby == player) {
                    nearby.sendMessage("§c🔄 You returned to human form!");
                } else {
                    nearby.sendMessage("§7" + player.getName() + " returned to human form!");
                }
            }
        }
        
        // Remove morph state from player metadata
        if (player.hasMetadata("sneaky_morph")) {
            player.removeMetadata("sneaky_morph", 
                org.bukkit.Bukkit.getPluginManager().getPlugin("SneakyCosmetics"));
        }
    }
    
    /**
     * Check if player is currently disguised
     */
    private boolean isPlayerDisguised(Player player) {
        // Placeholder - would use LibsDisguises API
        // return DisguiseAPI.isDisguised(player);
        
        // For now, check metadata
        return player.hasMetadata("sneaky_morph") && 
               player.getMetadata("sneaky_morph").get(0).asString().equals(getId());
    }
    
    /**
     * Apply special abilities based on the morph type
     */
    private void applySpecialAbilities(Player player) {
        switch (entityType) {
            case BAT:
                // Allow flight
                player.setAllowFlight(true);
                break;
            case ENDERMAN:
                // Teleportation ability (could be implemented with right-click)
                break;
            case SPIDER:
                // Wall climbing (would need custom implementation)
                break;
            case SQUID:
                // Water breathing
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0, false, false));
                break;
            case BLAZE:
                // Fire resistance
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
                break;
            case RABBIT:
                // Jump boost
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.JUMP_BOOST, Integer.MAX_VALUE, 2, false, false));
                break;
            case HORSE:
                // Speed boost
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
                break;
            default:
                // No special abilities for this morph
                break;
        }
    }
    
    /**
     * Remove special abilities from the player
     */
    private void removeSpecialAbilities(Player player) {
        // Remove all morph-related effects
        player.setAllowFlight(false);
        player.setFlying(false);
        
        // Remove specific potion effects
        player.removePotionEffect(org.bukkit.potion.PotionEffectType.WATER_BREATHING);
        player.removePotionEffect(org.bukkit.potion.PotionEffectType.FIRE_RESISTANCE);
        player.removePotionEffect(org.bukkit.potion.PotionEffectType.JUMP_BOOST);
        player.removePotionEffect(org.bukkit.potion.PotionEffectType.SPEED);
    }
    
    /**
     * Play the morph transformation sound
     */
    private void playMorphSound(Player player) {
        try {
            // Use direct sound mapping instead of deprecated valueOf
            org.bukkit.Sound sound = getSoundFromString(morphSound);
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException | NullPointerException e) {
            // Invalid sound name or null, use default
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        }
    }
    
    /**
     * Play the demorph transformation sound
     */
    private void playDemorphSound(Player player) {
        try {
            // Use direct sound mapping instead of deprecated valueOf
            org.bukkit.Sound sound = getSoundFromString(demorphSound);
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException | NullPointerException e) {
            // Invalid sound name or null, use default
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
        }
    }
    
    /**
     * Get sound from string without using deprecated valueOf
     */
    private org.bukkit.Sound getSoundFromString(String soundName) {
        if (soundName == null || soundName.isEmpty()) {
            return org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT;
        }
        
        // Map common sound names to actual Sound enum values
        String normalizedName = soundName.toUpperCase().replace(" ", "_");
        
        switch (normalizedName) {
            case "ENTITY_ENDERMAN_TELEPORT":
            case "ENDERMAN_TELEPORT":
                return org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT;
            case "ENTITY_WOLF_HOWL":
            case "WOLF_HOWL":
                return org.bukkit.Sound.ENTITY_WOLF_AMBIENT;
            case "ENTITY_CAT_MEOW":
            case "CAT_MEOW":
                return org.bukkit.Sound.ENTITY_CAT_AMBIENT;
            case "ENTITY_COW_MILK":
            case "COW_MILK":
                return org.bukkit.Sound.ENTITY_COW_MILK;
            case "ENTITY_PIG_AMBIENT":
            case "PIG_AMBIENT":
                return org.bukkit.Sound.ENTITY_PIG_AMBIENT;
            case "ENTITY_CHICKEN_AMBIENT":
            case "CHICKEN_AMBIENT":
                return org.bukkit.Sound.ENTITY_CHICKEN_AMBIENT;
            case "ENTITY_SHEEP_AMBIENT":
            case "SHEEP_AMBIENT":
                return org.bukkit.Sound.ENTITY_SHEEP_AMBIENT;
            case "ENTITY_HORSE_AMBIENT":
            case "HORSE_AMBIENT":
                return org.bukkit.Sound.ENTITY_HORSE_AMBIENT;
            case "ENTITY_VILLAGER_AMBIENT":
            case "VILLAGER_AMBIENT":
                return org.bukkit.Sound.ENTITY_VILLAGER_AMBIENT;
            case "ENTITY_ZOMBIE_AMBIENT":
            case "ZOMBIE_AMBIENT":
                return org.bukkit.Sound.ENTITY_ZOMBIE_AMBIENT;
            case "ENTITY_SKELETON_AMBIENT":
            case "SKELETON_AMBIENT":
                return org.bukkit.Sound.ENTITY_SKELETON_AMBIENT;
            case "ENTITY_CREEPER_PRIMED":
            case "CREEPER_PRIMED":
                return org.bukkit.Sound.ENTITY_CREEPER_PRIMED;
            case "ENTITY_SPIDER_AMBIENT":
            case "SPIDER_AMBIENT":
                return org.bukkit.Sound.ENTITY_SPIDER_AMBIENT;
            case "ENTITY_BLAZE_AMBIENT":
            case "BLAZE_AMBIENT":
                return org.bukkit.Sound.ENTITY_BLAZE_AMBIENT;
            case "ENTITY_GHAST_AMBIENT":
            case "GHAST_AMBIENT":
                return org.bukkit.Sound.ENTITY_GHAST_AMBIENT;
            case "ENTITY_WITCH_AMBIENT":
            case "WITCH_AMBIENT":
                return org.bukkit.Sound.ENTITY_WITCH_AMBIENT;
            case "ENTITY_IRON_GOLEM_ATTACK":
            case "IRON_GOLEM_ATTACK":
                return org.bukkit.Sound.ENTITY_IRON_GOLEM_ATTACK;
            default:
                // Add more common sound mappings to avoid valueOf usage
                if (normalizedName.contains("AMBIENT")) {
                    return org.bukkit.Sound.ENTITY_VILLAGER_AMBIENT;
                } else if (normalizedName.contains("ATTACK")) {
                    return org.bukkit.Sound.ENTITY_PLAYER_ATTACK_STRONG;
                } else if (normalizedName.contains("HURT")) {
                    return org.bukkit.Sound.ENTITY_PLAYER_HURT;
                } else if (normalizedName.contains("DEATH")) {
                    return org.bukkit.Sound.ENTITY_PLAYER_DEATH;
                }
                // Default fallback
                return org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT;
        }
    }
    
    /**
     * Get a formatted description including abilities
     */
    public List<String> getFullDescription() {
        List<String> fullDesc = new java.util.ArrayList<>(getDescription());
        
        if (hasSpecialAbilities && !abilities.isEmpty()) {
            fullDesc.add("");
            fullDesc.add("§6Special Abilities:");
            for (String ability : abilities) {
                fullDesc.add("§7• " + ability);
            }
        }
        
        if (canFly) {
            fullDesc.add("§a✓ Flight enabled");
        }
        
        return fullDesc;
    }
}
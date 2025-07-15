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
        // Implementation would depend on disguise plugin integration
        // This is a placeholder for the actual morph logic
        
        // Set the disguise using LibsDisguises or similar
        applyDisguise(player);
        
        // Apply special abilities if any
        if (hasSpecialAbilities) {
            applySpecialAbilities(player);
        }
        
        // Play morph sound
        if (morphSound != null && !morphSound.isEmpty()) {
            playMorphSound(player);
        }
        
        // Send confirmation message
        player.sendMessage("§a✓ Transformed into " + getDisplayName() + "!");
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
        
        // Send confirmation message
        player.sendMessage("§c⊘ Returned to human form!");
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
        // Placeholder - would use LibsDisguises API
        // DisguiseAPI.disguiseToAll(player, new MobDisguise(entityType));
        
        // Store morph state in player metadata
        player.setMetadata("sneaky_morph", new org.bukkit.metadata.FixedMetadataValue(
            org.bukkit.Bukkit.getPluginManager().getPlugin("SneakyCosmetics"), getId()));
    }
    
    /**
     * Remove the disguise from the player
     */
    private void removeDisguise(Player player) {
        // Placeholder - would use LibsDisguises API
        // DisguiseAPI.undisguiseToAll(player);
        
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
            org.bukkit.Sound sound = org.bukkit.Sound.valueOf(morphSound);
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException e) {
            // Invalid sound name, use default
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        }
    }
    
    /**
     * Play the demorph transformation sound
     */
    private void playDemorphSound(Player player) {
        try {
            org.bukkit.Sound sound = org.bukkit.Sound.valueOf(demorphSound);
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException e) {
            // Invalid sound name, use default
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
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
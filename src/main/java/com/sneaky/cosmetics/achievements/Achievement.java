package com.sneaky.cosmetics.achievements;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Represents an achievement that players can earn
 */
public class Achievement {
    
    private final String id;
    private final String name;
    private final String description;
    private final Material icon;
    private final List<String> requirements;
    private final int creditReward;
    private final AchievementType type;
    private final Object targetValue;
    
    public enum AchievementType {
        FIRST_COSMETIC("Purchase your first cosmetic"),
        COSMETIC_COUNT("Own a certain number of cosmetics"),
        SPEND_CREDITS("Spend a total amount of credits"),
        DAILY_LOGIN("Log in for consecutive days"),
        TYPE_COLLECTOR("Own all cosmetics of a specific type"),
        PREMIUM_USER("Become a premium player"),
        VIP_USER("Become a VIP player"),
        CREDITS_EARNED("Earn a total amount of credits"),
        CREDITS_SPENT("Spend a total amount of credits"),
        DAILY_STREAK("Log in for consecutive days"),
        DAILY_CLAIMS("Claim daily rewards multiple times"),
        COSMETICS_ACTIVATED("Activate cosmetics multiple times"),
        BABY_PETS("Collect baby pet variants"),
        LEGENDARY_PET("Own a legendary tier pet"),
        PARTICLE_COUNT("Own multiple particle effects"),
        SIMULTANEOUS_COSMETICS("Have multiple cosmetics active"),
        EARLY_ADOPTER("Be an early user of the system"),
        COMPLETIONIST("Own every cosmetic available");
        
        private final String description;
        
        AchievementType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public Achievement(String id, String name, String description, Material icon,
                      List<String> requirements, int creditReward, AchievementType type,
                      Object targetValue) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.requirements = requirements;
        this.creditReward = creditReward;
        this.type = type;
        this.targetValue = targetValue;
    }
    
    /**
     * Check if a player has completed this achievement
     */
    public boolean isCompleted(Player player, com.sneaky.cosmetics.SneakyCosmetics plugin) {
        switch (type) {
            case FIRST_COSMETIC:
                // Check if player owns any cosmetic
                for (var cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
                    if (plugin.getCosmeticManager().hasCosmetic(player, cosmetic.getId())) {
                        return true;
                    }
                }
                return false;
                
            case COSMETIC_COUNT:
                // Check if player owns enough cosmetics
                int targetCount = (Integer) targetValue;
                int ownedCount = 0;
                for (var cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
                    if (plugin.getCosmeticManager().hasCosmetic(player, cosmetic.getId())) {
                        ownedCount++;
                    }
                }
                return ownedCount >= targetCount;
                
            case PREMIUM_USER:
                return player.hasPermission("sneakycosmetics.premium");
                
            case VIP_USER:
                return player.hasPermission("sneakycosmetics.vip");
                
            case TYPE_COLLECTOR:
                // Check if player owns all cosmetics of a specific type
                com.sneaky.cosmetics.cosmetics.CosmeticType targetType = (com.sneaky.cosmetics.cosmetics.CosmeticType) targetValue;
                var typeCosmetics = plugin.getCosmeticManager().getCosmeticsByType(targetType);
                for (var cosmetic : typeCosmetics) {
                    if (!plugin.getCosmeticManager().hasCosmetic(player, cosmetic.getId())) {
                        return false;
                    }
                }
                return !typeCosmetics.isEmpty();
                
            case CREDITS_EARNED:
                // Check if player has earned enough credits total
                long targetCredits = (Integer) targetValue;
                return plugin.getStatisticsManager().getPlayerStatistics(player.getUniqueId()).creditsEarned.get() >= targetCredits;
                
            case CREDITS_SPENT:
                // Check if player has spent enough credits total
                long targetSpent = (Integer) targetValue;
                return plugin.getStatisticsManager().getPlayerStatistics(player.getUniqueId()).creditsSpent.get() >= targetSpent;
                
            case DAILY_STREAK:
                // Check if player has maintained daily login streak
                int targetStreak = (Integer) targetValue;
                return plugin.getCreditManager().getDailyStreak(player.getUniqueId()) >= targetStreak;
                
            case DAILY_CLAIMS:
                // Check if player has claimed daily rewards enough times
                int targetClaims = (Integer) targetValue;
                return plugin.getCreditManager().getTotalDailyClaimed(player.getUniqueId()) >= targetClaims;
                
            case COSMETICS_ACTIVATED:
                // Check if player has activated cosmetics enough times
                long targetActivations = (Integer) targetValue;
                return plugin.getStatisticsManager().getCosmeticsActivated(player.getUniqueId()) >= targetActivations;
                
            case BABY_PETS:
                // Check if player owns enough baby pets
                int targetBabyPets = (Integer) targetValue;
                int babyPetCount = 0;
                for (var cosmetic : plugin.getCosmeticManager().getCosmeticsByType(com.sneaky.cosmetics.cosmetics.CosmeticType.PET)) {
                    if (cosmetic.getId().contains("baby") && plugin.getCosmeticManager().hasCosmetic(player, cosmetic.getId())) {
                        babyPetCount++;
                    }
                }
                return babyPetCount >= targetBabyPets;
                
            case LEGENDARY_PET:
                // Check if player owns any legendary pet
                String[] legendaryPets = {"pet_ender_dragon", "pet_wither", "pet_warden"};
                for (String legendaryId : legendaryPets) {
                    if (plugin.getCosmeticManager().hasCosmetic(player, legendaryId)) {
                        return true;
                    }
                }
                return false;
                
            case PARTICLE_COUNT:
                // Check if player owns enough particle effects
                int targetParticles = (Integer) targetValue;
                int particleCount = 0;
                for (var cosmetic : plugin.getCosmeticManager().getCosmeticsByType(com.sneaky.cosmetics.cosmetics.CosmeticType.PARTICLE)) {
                    if (plugin.getCosmeticManager().hasCosmetic(player, cosmetic.getId())) {
                        particleCount++;
                    }
                }
                return particleCount >= targetParticles;
                
            case SIMULTANEOUS_COSMETICS:
                // Check if player has enough cosmetics active at once
                int targetSimultaneous = (Integer) targetValue;
                return plugin.getCosmeticManager().getActiveCosmetics(player).size() >= targetSimultaneous;
                
            case EARLY_ADOPTER:
                // This would need to be tracked separately when the plugin first launches
                // For now, always return false (would be awarded manually or via special logic)
                return false;
                
            case COMPLETIONIST:
                // Check if player owns every single cosmetic
                for (var cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
                    if (!plugin.getCosmeticManager().hasCosmetic(player, cosmetic.getId())) {
                        return false;
                    }
                }
                return true;
                
            default:
                return false;
        }
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Material getIcon() {
        return icon;
    }
    
    public List<String> getRequirements() {
        return requirements;
    }
    
    public int getCreditReward() {
        return creditReward;
    }
    
    public AchievementType getType() {
        return type;
    }
    
    public Object getTargetValue() {
        return targetValue;
    }
}
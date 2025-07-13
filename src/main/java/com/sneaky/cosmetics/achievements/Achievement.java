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
        VIP_USER("Become a VIP player");
        
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
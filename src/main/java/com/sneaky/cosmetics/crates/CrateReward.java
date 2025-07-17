package com.sneaky.cosmetics.crates;

import org.bukkit.Material;

/**
 * Represents a reward that can be obtained from a crate
 */
public class CrateReward {
    
    public enum RewardType {
        COSMETIC("Cosmetic", Material.DIAMOND),
        CREDITS("Credits", Material.EMERALD),
        RENTAL("Rental", Material.CLOCK),
        CRATE("Crate", Material.CHEST),
        SPECIAL("Special", Material.NETHER_STAR);
        
        private final String displayName;
        private final Material icon;
        
        RewardType(String displayName, Material icon) {
            this.displayName = displayName;
            this.icon = icon;
        }
        
        public String getDisplayName() { return displayName; }
        public Material getIcon() { return icon; }
    }
    
    private final RewardType type;
    private final String rewardId; // Cosmetic ID, "credits", crate type name, etc.
    private final int amount; // Amount of credits, rental duration, etc.
    private final double weight; // Weight for random selection
    private final Rarity rarity;
    
    public CrateReward(RewardType type, String rewardId, int amount, double weight, Rarity rarity) {
        this.type = type;
        this.rewardId = rewardId;
        this.amount = amount;
        this.weight = weight;
        this.rarity = rarity;
    }
    
    public RewardType getType() { return type; }
    public String getRewardId() { return rewardId; }
    public int getAmount() { return amount; }
    public double getWeight() { return weight; }
    public Rarity getRarity() { return rarity; }
    
    /**
     * Get formatted reward description
     */
    public String getFormattedDescription() {
        switch (type) {
            case COSMETIC:
                return rarity.getColor() + rewardId; // Will be replaced with actual cosmetic name
            case CREDITS:
                return "§e" + amount + " Credits";
            case RENTAL:
                return "§b" + amount + "h Rental Token";
            case CRATE:
                return "§6" + rewardId + " Crate";
            case SPECIAL:
                return "§d" + rewardId;
            default:
                return "§7Unknown Reward";
        }
    }
    
    /**
     * Rarity levels for rewards
     */
    public enum Rarity {
        COMMON("§7", "Common", 60.0),
        UNCOMMON("§a", "Uncommon", 25.0),
        RARE("§9", "Rare", 10.0),
        EPIC("§5", "Epic", 4.0),
        LEGENDARY("§6", "Legendary", 0.9),
        MYTHIC("§c", "Mythic", 0.1);
        
        private final String color;
        private final String name;
        private final double baseWeight;
        
        Rarity(String color, String name, double baseWeight) {
            this.color = color;
            this.name = name;
            this.baseWeight = baseWeight;
        }
        
        public String getColor() { return color; }
        public String getName() { return name; }
        public double getBaseWeight() { return baseWeight; }
        
        public String getFormattedName() { return color + name; }
    }
    
    @Override
    public String toString() {
        return "CrateReward{" +
                "type=" + type +
                ", rewardId='" + rewardId + '\'' +
                ", amount=" + amount +
                ", rarity=" + rarity +
                '}';
    }
}
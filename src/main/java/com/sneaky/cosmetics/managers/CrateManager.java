package com.sneaky.cosmetics.managers;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.TimedCosmetic;
import com.sneaky.cosmetics.crates.CrateReward;
import com.sneaky.cosmetics.crates.CrateType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Manages the crate system including crate rewards, opening mechanics, and player crate inventories
 */
public class CrateManager {
    
    private final SneakyCosmetics plugin;
    private final Map<CrateType, List<CrateReward>> crateRewards = new EnumMap<>(CrateType.class);
    private final Map<UUID, Map<CrateType, Integer>> playerCrates = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    public CrateManager(SneakyCosmetics plugin) {
        this.plugin = plugin;
        loadPlayerCratesFromDatabase();
        initializeCrateRewards();
    }
    
    /**
     * Initialize default crate rewards for all crate types
     */
    private void initializeCrateRewards() {
        initializeCommonCrateRewards();
        initializeRareCrateRewards();
        initializeEpicCrateRewards();
        initializeLegendaryCrateRewards();
        initializeMythicCrateRewards();
        initializeSeasonalCrateRewards();
        initializeEventCrateRewards();
        
        plugin.getLogger().info("Initialized crate reward system with " + getTotalRewards() + " rewards");
    }
    
    private void initializeCommonCrateRewards() {
        List<CrateReward> rewards = new ArrayList<>();
        
        // Credits (most common)
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 25, 30.0, CrateReward.Rarity.COMMON));
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 50, 20.0, CrateReward.Rarity.COMMON));
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 75, 15.0, CrateReward.Rarity.UNCOMMON));
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 100, 10.0, CrateReward.Rarity.UNCOMMON));
        
        // Rental tokens
        rewards.add(new CrateReward(CrateReward.RewardType.RENTAL, "rental_1h", 1, 15.0, CrateReward.Rarity.COMMON));
        rewards.add(new CrateReward(CrateReward.RewardType.RENTAL, "rental_6h", 6, 8.0, CrateReward.Rarity.UNCOMMON));
        
        // Common cosmetics (add based on actual cosmetics)
        addCosmeticsToRewards(rewards, CrateReward.Rarity.COMMON, 1.0, false, false);
        
        crateRewards.put(CrateType.COMMON, rewards);
    }
    
    private void initializeRareCrateRewards() {
        List<CrateReward> rewards = new ArrayList<>();
        
        // Better credits
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 100, 25.0, CrateReward.Rarity.COMMON));
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 200, 20.0, CrateReward.Rarity.UNCOMMON));
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 300, 15.0, CrateReward.Rarity.RARE));
        
        // Better rental tokens
        rewards.add(new CrateReward(CrateReward.RewardType.RENTAL, "rental_12h", 12, 10.0, CrateReward.Rarity.UNCOMMON));
        rewards.add(new CrateReward(CrateReward.RewardType.RENTAL, "rental_24h", 24, 5.0, CrateReward.Rarity.RARE));
        
        // Rare cosmetics
        addCosmeticsToRewards(rewards, CrateReward.Rarity.UNCOMMON, 15.0, false, false);
        addCosmeticsToRewards(rewards, CrateReward.Rarity.RARE, 8.0, false, false);
        
        // Chance for common crate
        rewards.add(new CrateReward(CrateReward.RewardType.CRATE, CrateType.COMMON.name(), 1, 2.0, CrateReward.Rarity.UNCOMMON));
        
        crateRewards.put(CrateType.RARE, rewards);
    }
    
    private void initializeEpicCrateRewards() {
        List<CrateReward> rewards = new ArrayList<>();
        
        // Epic credits
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 300, 20.0, CrateReward.Rarity.UNCOMMON));
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 500, 15.0, CrateReward.Rarity.RARE));
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 750, 10.0, CrateReward.Rarity.EPIC));
        
        // Extended rentals
        rewards.add(new CrateReward(CrateReward.RewardType.RENTAL, "rental_48h", 48, 8.0, CrateReward.Rarity.RARE));
        rewards.add(new CrateReward(CrateReward.RewardType.RENTAL, "rental_72h", 72, 5.0, CrateReward.Rarity.EPIC));
        
        // Epic cosmetics including VIP ones
        addCosmeticsToRewards(rewards, CrateReward.Rarity.RARE, 20.0, false, false);
        addCosmeticsToRewards(rewards, CrateReward.Rarity.EPIC, 15.0, true, false);
        
        // Rare and common crates
        rewards.add(new CrateReward(CrateReward.RewardType.CRATE, CrateType.COMMON.name(), 2, 3.0, CrateReward.Rarity.UNCOMMON));
        rewards.add(new CrateReward(CrateReward.RewardType.CRATE, CrateType.RARE.name(), 1, 2.0, CrateReward.Rarity.RARE));
        
        crateRewards.put(CrateType.EPIC, rewards);
    }
    
    private void initializeLegendaryCrateRewards() {
        List<CrateReward> rewards = new ArrayList<>();
        
        // Legendary credits
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 500, 15.0, CrateReward.Rarity.RARE));
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 1000, 12.0, CrateReward.Rarity.EPIC));
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 1500, 8.0, CrateReward.Rarity.LEGENDARY));
        
        // Week-long rentals
        rewards.add(new CrateReward(CrateReward.RewardType.RENTAL, "rental_168h", 168, 6.0, CrateReward.Rarity.EPIC));
        
        // Premium cosmetics
        addCosmeticsToRewards(rewards, CrateReward.Rarity.EPIC, 20.0, true, false);
        addCosmeticsToRewards(rewards, CrateReward.Rarity.LEGENDARY, 15.0, true, true);
        
        // Multiple crates
        rewards.add(new CrateReward(CrateReward.RewardType.CRATE, CrateType.RARE.name(), 2, 5.0, CrateReward.Rarity.RARE));
        rewards.add(new CrateReward(CrateReward.RewardType.CRATE, CrateType.EPIC.name(), 1, 3.0, CrateReward.Rarity.EPIC));
        
        crateRewards.put(CrateType.LEGENDARY, rewards);
    }
    
    private void initializeMythicCrateRewards() {
        List<CrateReward> rewards = new ArrayList<>();
        
        // Massive credits
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 1000, 10.0, CrateReward.Rarity.EPIC));
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 2500, 8.0, CrateReward.Rarity.LEGENDARY));
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 5000, 5.0, CrateReward.Rarity.MYTHIC));
        
        // Permanent rentals (very long duration)
        rewards.add(new CrateReward(CrateReward.RewardType.RENTAL, "rental_720h", 720, 4.0, CrateReward.Rarity.LEGENDARY)); // 30 days
        
        // Only the best cosmetics
        addCosmeticsToRewards(rewards, CrateReward.Rarity.LEGENDARY, 25.0, true, true);
        addCosmeticsToRewards(rewards, CrateReward.Rarity.MYTHIC, 20.0, true, true);
        
        // Multiple high-tier crates
        rewards.add(new CrateReward(CrateReward.RewardType.CRATE, CrateType.EPIC.name(), 2, 8.0, CrateReward.Rarity.EPIC));
        rewards.add(new CrateReward(CrateReward.RewardType.CRATE, CrateType.LEGENDARY.name(), 1, 5.0, CrateReward.Rarity.LEGENDARY));
        
        // Special rewards
        rewards.add(new CrateReward(CrateReward.RewardType.SPECIAL, "VIP_UPGRADE", 1, 1.0, CrateReward.Rarity.MYTHIC));
        rewards.add(new CrateReward(CrateReward.RewardType.SPECIAL, "PREMIUM_UPGRADE", 1, 0.5, CrateReward.Rarity.MYTHIC));
        
        crateRewards.put(CrateType.MYTHIC, rewards);
    }
    
    private void initializeSeasonalCrateRewards() {
        List<CrateReward> rewards = new ArrayList<>();
        
        // Seasonal credits
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 150, 20.0, CrateReward.Rarity.COMMON));
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 300, 15.0, CrateReward.Rarity.UNCOMMON));
        
        // Seasonal cosmetics (would be filtered by season)
        addCosmeticsToRewards(rewards, CrateReward.Rarity.UNCOMMON, 25.0, false, false);
        addCosmeticsToRewards(rewards, CrateReward.Rarity.RARE, 20.0, false, false);
        addCosmeticsToRewards(rewards, CrateReward.Rarity.EPIC, 10.0, true, false);
        
        // Special seasonal items
        rewards.add(new CrateReward(CrateReward.RewardType.SPECIAL, "SEASONAL_TOKEN", 1, 5.0, CrateReward.Rarity.RARE));
        
        crateRewards.put(CrateType.SEASONAL, rewards);
    }
    
    private void initializeEventCrateRewards() {
        List<CrateReward> rewards = new ArrayList<>();
        
        // Event-exclusive rewards
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 500, 15.0, CrateReward.Rarity.RARE));
        rewards.add(new CrateReward(CrateReward.RewardType.CREDITS, "credits", 1000, 10.0, CrateReward.Rarity.EPIC));
        
        // Exclusive cosmetics
        addCosmeticsToRewards(rewards, CrateReward.Rarity.EPIC, 30.0, true, true);
        addCosmeticsToRewards(rewards, CrateReward.Rarity.LEGENDARY, 20.0, true, true);
        
        // Special event rewards
        rewards.add(new CrateReward(CrateReward.RewardType.SPECIAL, "EVENT_EXCLUSIVE", 1, 15.0, CrateReward.Rarity.LEGENDARY));
        
        crateRewards.put(CrateType.EVENT, rewards);
    }
    
    /**
     * Add cosmetics to rewards based on criteria
     */
    private void addCosmeticsToRewards(List<CrateReward> rewards, CrateReward.Rarity rarity, 
                                     double baseWeight, boolean includeVIP, boolean includePremium) {
        for (Cosmetic cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
            if (cosmetic.getPrice() == 0) continue; // Skip free cosmetics
            
            // Filter by requirements
            if (cosmetic.requiresVIP() && !includeVIP) continue;
            if (cosmetic.requiresPremium() && !includePremium) continue;
            
            // Adjust weight based on cosmetic price
            double weight = baseWeight * Math.max(0.1, (1000.0 / Math.max(cosmetic.getPrice(), 100)));
            
            rewards.add(new CrateReward(
                CrateReward.RewardType.COSMETIC, 
                cosmetic.getId(), 
                1, 
                weight, 
                rarity
            ));
        }
    }
    
    /**
     * Give a crate to a player
     */
    public void giveCrate(Player player, CrateType crateType, int quantity, String source) {
        UUID playerUUID = player.getUniqueId();
        
        playerCrates.computeIfAbsent(playerUUID, k -> new EnumMap<>(CrateType.class))
                   .merge(crateType, quantity, Integer::sum);
        
        // Save to database
        saveCrateToDatabase(player, crateType, quantity, source);
        
        String crateText = quantity == 1 ? "crate" : "crates";
        player.sendMessage("§a✓ Received " + quantity + "x " + crateType.getFormattedName() + " " + crateText + "!");
        
        plugin.getLogger().info(player.getName() + " received " + quantity + "x " + crateType.name() + " crate(s) from " + source);
    }
    
    /**
     * Open a crate for a player
     */
    public CrateReward openCrate(Player player, CrateType crateType) {
        if (!hasCrate(player, crateType)) {
            player.sendMessage("§c✗ You don't have any " + crateType.getFormattedName() + " crates!");
            return null;
        }
        
        // Remove one crate
        removeCrate(player, crateType, 1);
        
        // Select random reward
        CrateReward reward = selectRandomReward(crateType);
        
        // Give reward to player
        giveReward(player, reward);
        
        // Log opening
        logCrateOpening(player, crateType, reward);
        
        return reward;
    }
    
    /**
     * Select a random reward from a crate type
     */
    public CrateReward selectRandomReward(CrateType crateType) {
        List<CrateReward> rewards = crateRewards.get(crateType);
        if (rewards == null || rewards.isEmpty()) {
            // Fallback reward
            return new CrateReward(CrateReward.RewardType.CREDITS, "credits", 50, 1.0, CrateReward.Rarity.COMMON);
        }
        
        // Calculate total weight
        double totalWeight = rewards.stream().mapToDouble(CrateReward::getWeight).sum();
        
        // Select random reward based on weight
        double randomValue = random.nextDouble() * totalWeight;
        double currentWeight = 0.0;
        
        for (CrateReward reward : rewards) {
            currentWeight += reward.getWeight();
            if (randomValue <= currentWeight) {
                return reward;
            }
        }
        
        // Fallback to last reward
        return rewards.get(rewards.size() - 1);
    }
    
    /**
     * Give a reward to a player
     */
    private void giveReward(Player player, CrateReward reward) {
        switch (reward.getType()) {
            case CREDITS:
                plugin.getCreditManager().addCredits(player.getUniqueId(), reward.getAmount());
                player.sendMessage("§a✓ Received " + reward.getAmount() + " credits!");
                break;
                
            case COSMETIC:
                Cosmetic cosmetic = plugin.getCosmeticManager().getCosmetic(reward.getRewardId());
                if (cosmetic != null) {
                    if (plugin.getCosmeticManager().hasCosmetic(player, cosmetic.getId())) {
                        // Convert to credits if already owned
                        int credits = Math.max(10, cosmetic.getPrice() / 4);
                        plugin.getCreditManager().addCredits(player.getUniqueId(), credits);
                        player.sendMessage("§e⚠ Already owned " + cosmetic.getDisplayName() + "! Converted to " + credits + " credits.");
                    } else {
                        plugin.getCosmeticManager().giveCosmetic(player, cosmetic.getId());
                        player.sendMessage("§a✓ Unlocked " + reward.getRarity().getColor() + cosmetic.getDisplayName() + "§a!");
                    }
                } else {
                    // Fallback to credits
                    plugin.getCreditManager().addCredits(player.getUniqueId(), 100);
                    player.sendMessage("§e⚠ Cosmetic not found! Received 100 credits instead.");
                }
                break;
                
            case RENTAL:
                int hours = Integer.parseInt(reward.getRewardId().replace("rental_", "").replace("h", ""));
                // Give rental token (implementation depends on rental system)
                player.sendMessage("§b✓ Received a " + hours + "-hour rental token!");
                // TODO: Implement rental token system
                break;
                
            case CRATE:
                CrateType crateType = CrateType.fromString(reward.getRewardId());
                if (crateType != null) {
                    giveCrate(player, crateType, reward.getAmount(), "crate_reward");
                }
                break;
                
            case SPECIAL:
                handleSpecialReward(player, reward);
                break;
        }
    }
    
    /**
     * Handle special rewards
     */
    private void handleSpecialReward(Player player, CrateReward reward) {
        switch (reward.getRewardId()) {
            case "VIP_UPGRADE":
                // Grant VIP permission (implementation depends on permission system)
                player.sendMessage("§6✓ Congratulations! You've been upgraded to VIP status!");
                plugin.getLogger().info(player.getName() + " received VIP upgrade from crate");
                break;
                
            case "PREMIUM_UPGRADE":
                // Grant Premium permission
                player.sendMessage("§5✓ Congratulations! You've been upgraded to Premium status!");
                plugin.getLogger().info(player.getName() + " received Premium upgrade from crate");
                break;
                
            case "SEASONAL_TOKEN":
            case "EVENT_EXCLUSIVE":
                player.sendMessage("§d✓ Received " + reward.getRewardId().replace("_", " ") + "!");
                break;
                
            default:
                // Fallback to credits
                plugin.getCreditManager().addCredits(player.getUniqueId(), 500);
                player.sendMessage("§d✓ Received special reward! (500 credits)");
                break;
        }
    }
    
    /**
     * Check if player has a specific crate type
     */
    public boolean hasCrate(Player player, CrateType crateType) {
        return getCrateCount(player, crateType) > 0;
    }
    
    /**
     * Get the number of crates a player has of a specific type
     */
    public int getCrateCount(Player player, CrateType crateType) {
        Map<CrateType, Integer> crates = playerCrates.get(player.getUniqueId());
        return crates != null ? crates.getOrDefault(crateType, 0) : 0;
    }
    
    /**
     * Get all crates owned by a player
     */
    public Map<CrateType, Integer> getPlayerCrates(Player player) {
        return playerCrates.getOrDefault(player.getUniqueId(), new EnumMap<>(CrateType.class));
    }
    
    /**
     * Remove crates from a player
     */
    public boolean removeCrate(Player player, CrateType crateType, int quantity) {
        Map<CrateType, Integer> crates = playerCrates.get(player.getUniqueId());
        if (crates == null) return false;
        
        int currentCount = crates.getOrDefault(crateType, 0);
        if (currentCount < quantity) return false;
        
        int newCount = currentCount - quantity;
        if (newCount <= 0) {
            crates.remove(crateType);
        } else {
            crates.put(crateType, newCount);
        }
        
        // Update database
        updateCrateInDatabase(player, crateType, newCount);
        
        return true;
    }
    
    /**
     * Purchase a crate with credits
     */
    public boolean purchaseCrate(Player player, CrateType crateType, int quantity) {
        if (!crateType.isPurchasable()) {
            player.sendMessage("§c✗ This crate type cannot be purchased!");
            return false;
        }
        
        int totalCost = crateType.getPrice() * quantity;
        int playerCredits = plugin.getCreditManager().getCreditsSync(player.getUniqueId());
        
        if (playerCredits < totalCost) {
            player.sendMessage("§c✗ Insufficient credits! Need " + totalCost + " credits.");
            return false;
        }
        
        // Deduct credits
        plugin.getCreditManager().removeCredits(player.getUniqueId(), totalCost);
        
        // Give crates
        giveCrate(player, crateType, quantity, "purchase");
        
        return true;
    }
    
    /**
     * Get total number of rewards across all crate types
     */
    private int getTotalRewards() {
        return crateRewards.values().stream().mapToInt(List::size).sum();
    }
    
    // Database methods
    private void saveCrateToDatabase(Player player, CrateType crateType, int quantity, String source) {
        plugin.getSchedulerAdapter().runTaskAsynchronously(() -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection()) {
                String sql = "INSERT INTO player_crates (player_uuid, crate_type, quantity, obtained_from) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, player.getUniqueId().toString());
                    stmt.setString(2, crateType.name());
                    stmt.setInt(3, quantity);
                    stmt.setString(4, source);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to save crate to database: " + e.getMessage());
            }
        });
    }
    
    private void updateCrateInDatabase(Player player, CrateType crateType, int newQuantity) {
        plugin.getSchedulerAdapter().runTaskAsynchronously(() -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection()) {
                if (newQuantity <= 0) {
                    String sql = "DELETE FROM player_crates WHERE player_uuid = ? AND crate_type = ?";
                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setString(1, player.getUniqueId().toString());
                        stmt.setString(2, crateType.name());
                        stmt.executeUpdate();
                    }
                } else {
                    String sql = "UPDATE player_crates SET quantity = ? WHERE player_uuid = ? AND crate_type = ?";
                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setInt(1, newQuantity);
                        stmt.setString(2, player.getUniqueId().toString());
                        stmt.setString(3, crateType.name());
                        stmt.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to update crate in database: " + e.getMessage());
            }
        });
    }
    
    private void logCrateOpening(Player player, CrateType crateType, CrateReward reward) {
        plugin.getSchedulerAdapter().runTaskAsynchronously(() -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection()) {
                String sql = "INSERT INTO crate_openings (player_uuid, crate_type, reward_type, reward_id, reward_amount) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, player.getUniqueId().toString());
                    stmt.setString(2, crateType.name());
                    stmt.setString(3, reward.getType().name());
                    stmt.setString(4, reward.getRewardId());
                    stmt.setInt(5, reward.getAmount());
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to log crate opening: " + e.getMessage());
            }
        });
    }
    
    private void loadPlayerCratesFromDatabase() {
        plugin.getSchedulerAdapter().runTaskAsynchronously(() -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection()) {
                String sql = "SELECT player_uuid, crate_type, SUM(quantity) as total_quantity FROM player_crates GROUP BY player_uuid, crate_type";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    try (ResultSet rs = stmt.executeQuery()) {
                        int loadedCount = 0;
                        while (rs.next()) {
                            UUID playerUUID = UUID.fromString(rs.getString("player_uuid"));
                            CrateType crateType = CrateType.fromString(rs.getString("crate_type"));
                            int quantity = rs.getInt("total_quantity");
                            
                            if (crateType != null && quantity > 0) {
                                playerCrates.computeIfAbsent(playerUUID, k -> new EnumMap<>(CrateType.class))
                                           .put(crateType, quantity);
                                loadedCount++;
                            }
                        }
                        plugin.getLogger().info("Loaded " + loadedCount + " crate entries from database");
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to load crates from database: " + e.getMessage());
            }
        });
    }
    
    /**
     * Cleanup on plugin disable
     */
    public void shutdown() {
        plugin.getLogger().info("CrateManager shutdown complete");
    }
}
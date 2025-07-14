package com.sneaky.cosmetics.managers;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.database.DatabaseManager;
import com.sneaky.cosmetics.utils.MessageManager;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the credit system for purchasing cosmetics
 * Integrates with Vault economy and handles transactions
 */
public class CreditManager {
    
    private final SneakyCosmetics plugin;
    private final DatabaseManager databaseManager;
    private final MessageManager messageManager;
    
    // Cache for daily bonus tracking
    private final Map<UUID, Long> dailyBonusCache = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastPlaytimeReward = new ConcurrentHashMap<>();
    
    public CreditManager(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
        this.messageManager = plugin.getMessageManager();
        
        // Start playtime reward task
        startPlaytimeRewardTask();
    }
    
    /**
     * Get a player's credit balance
     */
    public CompletableFuture<Integer> getCredits(UUID uuid) {
        return databaseManager.getPlayerCredits(uuid);
    }
    
    /**
     * Get a player's credit balance (sync version for GUI)
     */
    public int getCreditsSync(UUID uuid) {
        try {
            return getCredits(uuid).get();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get credits for " + uuid + ": " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Set a player's credit balance
     */
    public CompletableFuture<Void> setCredits(UUID uuid, int credits) {
        int maxCredits = plugin.getConfig().getInt("credits.max-credits", 100000);
        int finalCredits = Math.min(Math.max(credits, 0), maxCredits);
        
        return databaseManager.setPlayerCredits(uuid, finalCredits);
    }
    
    /**
     * Add credits to a player's balance
     */
    public CompletableFuture<Boolean> addCredits(UUID uuid, int credits) {
        if (credits <= 0) {
            return CompletableFuture.completedFuture(false);
        }
        
        return getCredits(uuid).thenCompose(currentCredits -> {
            int maxCredits = plugin.getConfig().getInt("credits.max-credits", 100000);
            int newCredits = Math.min(currentCredits + credits, maxCredits);
            
            if (newCredits == currentCredits) {
                return CompletableFuture.completedFuture(false); // Already at max
            }
            
            return setCredits(uuid, newCredits).thenApply(v -> true);
        });
    }
    
    /**
     * Remove credits from a player's balance
     */
    public CompletableFuture<Boolean> removeCredits(UUID uuid, int credits) {
        if (credits <= 0) {
            return CompletableFuture.completedFuture(false);
        }
        
        return databaseManager.removePlayerCredits(uuid, credits);
    }
    
    /**
     * Check if a player has enough credits
     */
    public CompletableFuture<Boolean> hasCredits(UUID uuid, int amount) {
        return getCredits(uuid).thenApply(credits -> credits >= amount);
    }
    
    /**
     * Purchase credits using Vault economy
     */
    public CompletableFuture<Boolean> purchaseCredits(Player player, int amount) {
        if (plugin.getVaultIntegration() == null || !plugin.getVaultIntegration().isEnabled()) {
            messageManager.sendConfigMessage(player, "errors.vault-not-available");
            return CompletableFuture.completedFuture(false);
        }
        
        double costPerCredit = plugin.getConfig().getDouble("credits.cost-per-credit", 100.0);
        double totalCost = amount * costPerCredit;
        
        // Apply bulk discounts
        double discount = getBulkDiscount(amount);
        if (discount > 0) {
            totalCost *= (1.0 - discount);
        }
        
        // Check if player has enough money
        if (!plugin.getVaultIntegration().hasBalance(player, totalCost)) {
            Map<String, String> placeholders = messageManager.createPlaceholders(player);
            placeholders.put("amount", String.format("%.2f", totalCost - plugin.getVaultIntegration().getBalance(player)));
            messageManager.sendConfigMessage(player, "credits.insufficient-funds", placeholders);
            return CompletableFuture.completedFuture(false);
        }
        
        // Withdraw money and add credits
        final double finalCost = totalCost;
        final int finalAmount = amount;
        
        if (plugin.getVaultIntegration().withdraw(player, finalCost)) {
            return addCredits(player.getUniqueId(), finalAmount).thenApply(success -> {
                if (success) {
                    Map<String, String> placeholders = messageManager.createPlaceholders(player);
                    placeholders.put("amount", String.valueOf(finalAmount));
                    placeholders.put("cost", String.format("%.2f", finalCost));
                    messageManager.sendConfigMessage(player, "credits.purchased", placeholders);
                    
                    // Log transaction
                    logTransaction(player.getUniqueId(), finalAmount, "PURCHASE", 
                                 "Purchased " + finalAmount + " credits for $" + String.format("%.2f", finalCost));
                } else {
                    // Refund if credit addition failed
                    plugin.getVaultIntegration().deposit(player, finalCost);
                    messageManager.sendConfigMessage(player, "errors.database-error");
                }
                return success;
            });
        }
        
        return CompletableFuture.completedFuture(false);
    }
    
    /**
     * Get bulk discount percentage for an amount
     */
    private double getBulkDiscount(int amount) {
        if (!plugin.getConfig().getBoolean("credits.bulk-discounts.enabled", true)) {
            return 0.0;
        }
        
        Map<String, Object> discounts = plugin.getConfig().getConfigurationSection("credits.bulk-discounts.discounts").getValues(false);
        
        double bestDiscount = 0.0;
        for (Map.Entry<String, Object> entry : discounts.entrySet()) {
            try {
                int threshold = Integer.parseInt(entry.getKey());
                double discount = ((Number) entry.getValue()).doubleValue();
                
                if (amount >= threshold && discount > bestDiscount) {
                    bestDiscount = discount;
                }
            } catch (NumberFormatException e) {
                // Skip invalid entries
            }
        }
        
        return bestDiscount;
    }
    
    /**
     * Give welcome credits to a new player
     */
    public void giveWelcomeCredits(Player player) {
        int welcomeAmount = plugin.getConfig().getInt("credits.welcome-amount", 500);
        if (welcomeAmount > 0) {
            addCredits(player.getUniqueId(), welcomeAmount).thenAccept(success -> {
                if (success) {
                    Map<String, String> placeholders = messageManager.createPlaceholders(player);
                    placeholders.put("amount", String.valueOf(welcomeAmount));
                    messageManager.sendConfigMessage(player, "daily.welcome-credits", placeholders);
                    
                    logTransaction(player.getUniqueId(), welcomeAmount, "WELCOME", "Welcome bonus");
                }
            });
        }
    }
    
    /**
     * Check if a player can claim daily bonus
     */
    public boolean canClaimDailyBonus(UUID uuid) {
        if (!plugin.getConfig().getBoolean("credits.daily-bonus.enabled", true)) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        long lastClaim = dailyBonusCache.getOrDefault(uuid, 0L);
        
        // Check if 24 hours have passed
        return currentTime - lastClaim >= 86400000L; // 24 hours in milliseconds
    }
    
    /**
     * Get hours until next daily bonus claim
     */
    public long getHoursUntilNextClaim(UUID uuid) {
        if (!plugin.getConfig().getBoolean("credits.daily-bonus.enabled", true)) {
            return 0;
        }
        
        long currentTime = System.currentTimeMillis();
        long lastClaim = dailyBonusCache.getOrDefault(uuid, 0L);
        long timeLeft = 86400000L - (currentTime - lastClaim);
        
        if (timeLeft <= 0) {
            return 0;
        }
        
        return timeLeft / 3600000L; // Convert to hours
    }
    
    /**
     * Get daily streak for a player
     */
    public int getDailyStreak(UUID uuid) {
        // TODO: Implement proper streak tracking in database
        return 1; // Placeholder
    }
    
    /**
     * Get total daily rewards claimed by a player
     */
    public int getTotalDailyClaimed(UUID uuid) {
        // TODO: Implement proper tracking in database
        return 5; // Placeholder
    }
    
    /**
     * Claim daily bonus credits
     */
    public CompletableFuture<Boolean> claimDailyBonus(Player player) {
        if (!plugin.getConfig().getBoolean("credits.daily-bonus.enabled", true)) {
            return CompletableFuture.completedFuture(false);
        }
        
        UUID uuid = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long lastClaim = dailyBonusCache.getOrDefault(uuid, 0L);
        
        // Check if 24 hours have passed
        if (currentTime - lastClaim < 86400000L) { // 24 hours in milliseconds
            long timeLeft = 86400000L - (currentTime - lastClaim);
            long hoursLeft = timeLeft / 3600000L;
            long minutesLeft = (timeLeft % 3600000L) / 60000L;
            
            Map<String, String> placeholders = messageManager.createPlaceholders(player);
            placeholders.put("time", hoursLeft + "h " + minutesLeft + "m");
            messageManager.sendConfigMessage(player, "daily.daily-already-claimed", placeholders);
            return CompletableFuture.completedFuture(false);
        }
        
        int dailyAmount = plugin.getConfig().getInt("credits.daily-bonus.amount", 50);
        
        return addCredits(uuid, dailyAmount).thenApply(success -> {
            if (success) {
                dailyBonusCache.put(uuid, currentTime);
                
                Map<String, String> placeholders = messageManager.createPlaceholders(player);
                placeholders.put("amount", String.valueOf(dailyAmount));
                messageManager.sendConfigMessage(player, "daily.daily-reward", placeholders);
                
                logTransaction(uuid, dailyAmount, "DAILY_BONUS", "Daily login bonus");
            }
            return success;
        });
    }
    
    /**
     * Start the playtime reward task
     */
    private void startPlaytimeRewardTask() {
        if (!plugin.getConfig().getBoolean("credits.playtime-rewards.enabled", true)) {
            return;
        }
        
        int creditsPerHour = plugin.getConfig().getInt("credits.playtime-rewards.credits-per-hour", 25);
        
        // Check every hour (72000 ticks)
        plugin.getSchedulerAdapter().runTaskTimerAsynchronously(() -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();
                long currentTime = System.currentTimeMillis();
                long lastReward = lastPlaytimeReward.getOrDefault(uuid, currentTime);
                
                // If player has been online for at least an hour since last reward
                if (currentTime - lastReward >= 3600000L) { // 1 hour in milliseconds
                    int hours = (int) ((currentTime - lastReward) / 3600000L);
                    int creditsToGive = hours * creditsPerHour;
                    
                    addCredits(uuid, creditsToGive).thenAccept(success -> {
                        if (success) {
                            lastPlaytimeReward.put(uuid, currentTime);
                            
                            Map<String, String> placeholders = messageManager.createPlaceholders(player);
                            placeholders.put("amount", String.valueOf(creditsToGive));
                            placeholders.put("hours", String.valueOf(hours));
                            messageManager.sendConfigMessage(player, "daily.playtime-reward", placeholders);
                            
                            logTransaction(uuid, creditsToGive, "PLAYTIME", "Playtime reward for " + hours + " hours");
                        }
                    });
                }
            }
        }, 72000L, 72000L);
    }
    
    /**
     * Log a credit transaction
     */
    private void logTransaction(UUID uuid, int amount, String type, String description) {
        plugin.getSchedulerAdapter().runTaskAsynchronously(() -> {
            try {
                // This would be implemented in the database manager
                // For now, just log to console if debug is enabled
                if (plugin.getConfig().getBoolean("admin.debug", false)) {
                    plugin.getLogger().info("Credit transaction: " + uuid + " " + type + " " + amount + " - " + description);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to log transaction: " + e.getMessage());
            }
        });
    }
    
    /**
     * Save all player data (called on plugin disable)
     */
    public void saveAllPlayerData() {
        // Save daily bonus data to database
        for (Map.Entry<UUID, Long> entry : dailyBonusCache.entrySet()) {
            // This would save to the database - for now we'll just clear the cache
        }
        
        // Clear caches
        dailyBonusCache.clear();
        lastPlaytimeReward.clear();
    }
    
    /**
     * Handle player join for credit initialization
     */
    public void handlePlayerJoin(Player player) {
        UUID uuid = player.getUniqueId();
        
        // Initialize player data if needed
        databaseManager.createPlayerData(uuid, player.getName());
        
        // Load daily bonus data
        // This would load from database - for now we'll initialize
        if (!dailyBonusCache.containsKey(uuid)) {
            dailyBonusCache.put(uuid, 0L);
        }
        
        if (!lastPlaytimeReward.containsKey(uuid)) {
            lastPlaytimeReward.put(uuid, System.currentTimeMillis());
        }
    }
    
    /**
     * Handle player quit for cleanup
     */
    public void handlePlayerQuit(Player player) {
        UUID uuid = player.getUniqueId();
        
        // Save data to database before removing from cache
        // For now we'll just keep the data in cache
        
        // Note: We don't remove from cache immediately to allow for quick reconnects
    }
}
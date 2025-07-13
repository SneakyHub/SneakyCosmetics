package com.sneaky.cosmetics.api;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.achievements.Achievement;
import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import com.sneaky.cosmetics.managers.StatisticsManager;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Public API for SneakyCosmetics plugin
 * Allows other plugins to interact with cosmetics, credits, achievements, and statistics
 */
public class SneakyCosmeticsAPI {
    
    private static SneakyCosmetics plugin;
    
    /**
     * Initialize the API with the plugin instance
     * This should only be called by the SneakyCosmetics plugin
     */
    public static void initialize(SneakyCosmetics plugin) {
        SneakyCosmeticsAPI.plugin = plugin;
    }
    
    /**
     * Get the plugin instance
     */
    public static SneakyCosmetics getPlugin() {
        return plugin;
    }
    
    // === COSMETIC METHODS ===
    
    /**
     * Get all available cosmetics
     */
    public static Collection<Cosmetic> getAllCosmetics() {
        return plugin.getCosmeticManager().getAllCosmetics();
    }
    
    /**
     * Get cosmetics by type
     */
    public static Collection<Cosmetic> getCosmeticsByType(CosmeticType type) {
        return plugin.getCosmeticManager().getCosmeticsByType(type);
    }
    
    /**
     * Get a specific cosmetic by ID
     */
    public static Cosmetic getCosmetic(String cosmeticId) {
        return plugin.getCosmeticManager().getCosmetic(cosmeticId);
    }
    
    /**
     * Check if a player owns a cosmetic
     */
    public static boolean playerOwnsCosmetic(Player player, String cosmeticId) {
        return plugin.getCosmeticManager().hasCosmetic(player, cosmeticId);
    }
    
    /**
     * Get all cosmetics owned by a player
     */
    public static Set<String> getPlayerOwnedCosmetics(Player player) {
        Set<String> ownedCosmetics = new HashSet<>();
        for (Cosmetic cosmetic : getAllCosmetics()) {
            if (playerOwnsCosmetic(player, cosmetic.getId())) {
                ownedCosmetics.add(cosmetic.getId());
            }
        }
        return ownedCosmetics;
    }
    
    /**
     * Get all active cosmetics for a player
     */
    public static Set<String> getPlayerActiveCosmetics(Player player) {
        return plugin.getCosmeticManager().getActiveCosmetics(player);
    }
    
    /**
     * Check if a cosmetic is currently active for a player
     */
    public static boolean isCosmeticActive(Player player, String cosmeticId) {
        return plugin.getCosmeticManager().isCosmeticActive(player, cosmeticId);
    }
    
    /**
     * Activate a cosmetic for a player
     */
    public static boolean activateCosmetic(Player player, String cosmeticId) {
        return plugin.getCosmeticManager().activateCosmetic(player, cosmeticId);
    }
    
    /**
     * Deactivate a cosmetic for a player
     */
    public static boolean deactivateCosmetic(Player player, String cosmeticId) {
        return plugin.getCosmeticManager().deactivateCosmetic(player, cosmeticId);
    }
    
    /**
     * Give a cosmetic to a player
     */
    public static void giveCosmetic(Player player, String cosmeticId) {
        plugin.getCosmeticManager().giveCosmetic(player, cosmeticId);
    }
    
    /**
     * Clear all active cosmetics for a player
     */
    public static void clearAllCosmetics(Player player) {
        plugin.getCosmeticManager().clearAllCosmetics(player);
    }
    
    // === CREDIT METHODS ===
    
    /**
     * Get a player's credit balance (async)
     */
    public static CompletableFuture<Integer> getPlayerCredits(Player player) {
        return plugin.getCreditManager().getCredits(player.getUniqueId());
    }
    
    /**
     * Get a player's credit balance (sync)
     */
    public static int getPlayerCreditsSync(Player player) {
        return plugin.getCreditManager().getCreditsSync(player.getUniqueId());
    }
    
    /**
     * Add credits to a player (async)
     */
    public static CompletableFuture<Boolean> addCredits(Player player, int amount) {
        return plugin.getCreditManager().addCredits(player.getUniqueId(), amount);
    }
    
    /**
     * Remove credits from a player (async)
     */
    public static CompletableFuture<Boolean> removeCredits(Player player, int amount) {
        return plugin.getCreditManager().removeCredits(player.getUniqueId(), amount);
    }
    
    /**
     * Set a player's credit balance (async)
     */
    public static CompletableFuture<Void> setCredits(Player player, int amount) {
        return plugin.getCreditManager().setCredits(player.getUniqueId(), amount);
    }
    
    // === ACHIEVEMENT METHODS ===
    
    /**
     * Get all available achievements
     */
    public static Collection<Achievement> getAllAchievements() {
        return plugin.getAchievementManager().getAllAchievements();
    }
    
    /**
     * Check if a player has an achievement
     */
    public static boolean hasAchievement(Player player, String achievementId) {
        return plugin.getAchievementManager().hasAchievement(player, achievementId);
    }
    
    /**
     * Get all achievements a player has unlocked
     */
    public static Set<String> getPlayerAchievements(Player player) {
        return plugin.getAchievementManager().getPlayerAchievements(player);
    }
    
    /**
     * Get achievement completion percentage for a player
     */
    public static double getAchievementProgress(Player player) {
        return plugin.getAchievementManager().getCompletionPercentage(player);
    }
    
    /**
     * Get total credits earned from achievements
     */
    public static int getAchievementCreditsEarned(Player player) {
        return plugin.getAchievementManager().getTotalCreditsEarned(player);
    }
    
    /**
     * Award an achievement to a player
     */
    public static void awardAchievement(Player player, String achievementId) {
        plugin.getAchievementManager().awardAchievement(player, achievementId);
    }
    
    /**
     * Check achievements for a player (triggers any newly completed ones)
     */
    public static void checkAchievements(Player player) {
        plugin.getAchievementManager().checkAchievements(player);
    }
    
    // === STATISTICS METHODS ===
    
    /**
     * Get player statistics
     */
    public static StatisticsManager.PlayerStatistics getPlayerStatistics(Player player) {
        return plugin.getStatisticsManager().getPlayerStatistics(player.getUniqueId());
    }
    
    /**
     * Get server-wide statistics
     */
    public static Map<String, Object> getServerStatistics() {
        return plugin.getStatisticsManager().getServerStatistics();
    }
    
    /**
     * Get total credits earned server-wide
     */
    public static long getTotalCreditsEarned() {
        return plugin.getStatisticsManager().getTotalCreditsEarned();
    }
    
    /**
     * Get total credits spent server-wide
     */
    public static long getTotalCreditsSpent() {
        return plugin.getStatisticsManager().getTotalCreditsSpent();
    }
    
    /**
     * Get total cosmetics activated server-wide
     */
    public static long getTotalCosmeticsActivated() {
        return plugin.getStatisticsManager().getTotalCosmeticsActivated();
    }
    
    /**
     * Get total achievements unlocked server-wide
     */
    public static long getTotalAchievementsUnlocked() {
        return plugin.getStatisticsManager().getTotalAchievementsUnlocked();
    }
    
    /**
     * Get top players by a specific statistic
     */
    public static List<Map.Entry<UUID, Long>> getTopPlayersByStat(String statType, int limit) {
        return plugin.getStatisticsManager().getTopPlayersByStat(statType, limit);
    }
    
    /**
     * Get most popular cosmetics
     */
    public static List<Map.Entry<String, Long>> getMostPopularCosmetics(int limit) {
        return plugin.getStatisticsManager().getMostPopularCosmetics(limit);
    }
    
    /**
     * Get cosmetics with most usage time
     */
    public static List<Map.Entry<String, Long>> getCosmeticsWithMostUsageTime(int limit) {
        return plugin.getStatisticsManager().getCosmeticsWithMostUsageTime(limit);
    }
    
    /**
     * Record custom statistic
     */
    public static void recordCosmeticActivation(Player player, String cosmeticId) {
        plugin.getStatisticsManager().recordCosmeticActivation(player, cosmeticId);
    }
    
    /**
     * Record achievement unlock (for custom achievements)
     */
    public static void recordAchievementUnlock(Player player, String achievementId) {
        plugin.getStatisticsManager().recordAchievementUnlock(player, achievementId);
    }
    
    /**
     * Record credits earned
     */
    public static void recordCreditsEarned(Player player, int amount, String source) {
        plugin.getStatisticsManager().recordCreditsEarned(player, amount, source);
    }
    
    /**
     * Record credits spent
     */
    public static void recordCreditsSpent(Player player, int amount, String item) {
        plugin.getStatisticsManager().recordCreditsSpent(player, amount, item);
    }
    
    /**
     * Record pet interaction
     */
    public static void recordPetInteraction(Player player, String petId, String interaction) {
        plugin.getStatisticsManager().recordPetInteraction(player, petId, interaction);
    }
    
    // === UTILITY METHODS ===
    
    /**
     * Check if the plugin is fully loaded and ready
     */
    public static boolean isReady() {
        return plugin != null && plugin.isEnabled() && plugin.getCosmeticManager() != null;
    }
    
    /**
     * Get plugin version
     */
    public static String getVersion() {
        return plugin.getDescription().getVersion();
    }
    
    /**
     * Check if a specific integration is available
     */
    public static boolean isIntegrationAvailable(String integration) {
        switch (integration.toLowerCase()) {
            case "vault":
                return plugin.getVaultIntegration() != null;
            case "luckperms":
                return plugin.getLuckPermsIntegration() != null;
            case "essentialsx":
            case "essentials":
                return plugin.getEssentialsXIntegration() != null;
            case "placeholderapi":
                return plugin.getPlaceholderAPIIntegration() != null;
            case "cmi":
                return plugin.getCMIIntegration() != null && plugin.getCMIIntegration().isAvailable();
            default:
                return false;
        }
    }
    
    /**
     * Get formatted time string from milliseconds
     */
    public static String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days + "d " + (hours % 24) + "h " + (minutes % 60) + "m";
        } else if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m " + (seconds % 60) + "s";
        } else if (minutes > 0) {
            return minutes + "m " + (seconds % 60) + "s";
        } else {
            return seconds + "s";
        }
    }
    
    // === EVENT METHODS ===
    
    /**
     * Register a custom event listener for cosmetic events
     * This allows other plugins to listen for cosmetic activation/deactivation
     */
    public static void registerEventListener(Object listener) {
        plugin.getServer().getPluginManager().registerEvents((org.bukkit.event.Listener) listener, plugin);
    }
}
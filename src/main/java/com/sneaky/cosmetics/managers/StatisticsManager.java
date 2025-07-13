package com.sneaky.cosmetics.managers;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages comprehensive statistics for SneakyCosmetics
 * Tracks usage, achievements, credits, and performance metrics
 */
public class StatisticsManager {
    
    private final SneakyCosmetics plugin;
    
    // Global statistics
    private final AtomicLong totalCreditsEarned = new AtomicLong(0);
    private final AtomicLong totalCreditsSpent = new AtomicLong(0);
    private final AtomicLong totalCosmeticsActivated = new AtomicLong(0);
    private final AtomicLong totalAchievementsUnlocked = new AtomicLong(0);
    
    // Per-player statistics
    private final Map<UUID, PlayerStatistics> playerStats = new ConcurrentHashMap<>();
    
    // Cosmetic usage statistics
    private final Map<String, AtomicLong> cosmeticUsageCount = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> cosmeticUsageTime = new ConcurrentHashMap<>();
    
    // Type-based statistics
    private final Map<CosmeticType, AtomicLong> typeUsageCount = new ConcurrentHashMap<>();
    private final Map<CosmeticType, AtomicLong> typeUsageTime = new ConcurrentHashMap<>();
    
    // Achievement statistics
    private final Map<String, AtomicLong> achievementUnlockCount = new ConcurrentHashMap<>();
    
    // Daily/Weekly/Monthly statistics
    private final Map<String, Map<UUID, AtomicLong>> dailyStats = new ConcurrentHashMap<>();
    @SuppressWarnings("unused")
    private final Map<String, Map<UUID, AtomicLong>> weeklyStats = new ConcurrentHashMap<>();
    @SuppressWarnings("unused")
    private final Map<String, Map<UUID, AtomicLong>> monthlyStats = new ConcurrentHashMap<>();
    
    public StatisticsManager(SneakyCosmetics plugin) {
        this.plugin = plugin;
        initializeTypeStats();
        startStatisticsTasks();
    }
    
    private void initializeTypeStats() {
        for (CosmeticType type : CosmeticType.values()) {
            typeUsageCount.put(type, new AtomicLong(0));
            typeUsageTime.put(type, new AtomicLong(0));
        }
    }
    
    private void startStatisticsTasks() {
        // Auto-save statistics every 5 minutes
        plugin.getSchedulerAdapter().runTaskTimerAsynchronously(() -> {
            saveAllStatistics();
        }, 6000L, 6000L); // 5 minutes
        
        // Update usage time for active cosmetics every minute
        plugin.getSchedulerAdapter().runTaskTimer(() -> {
            updateActiveUsageTimes();
        }, 1200L, 1200L); // 1 minute
    }
    
    /**
     * Get or create player statistics
     */
    public PlayerStatistics getPlayerStatistics(UUID uuid) {
        return playerStats.computeIfAbsent(uuid, k -> new PlayerStatistics());
    }
    
    /**
     * Record cosmetic activation
     */
    public void recordCosmeticActivation(Player player, String cosmeticId) {
        totalCosmeticsActivated.incrementAndGet();
        cosmeticUsageCount.computeIfAbsent(cosmeticId, k -> new AtomicLong(0)).incrementAndGet();
        
        PlayerStatistics stats = getPlayerStatistics(player.getUniqueId());
        stats.cosmeticsActivated.incrementAndGet();
        stats.cosmeticUsage.computeIfAbsent(cosmeticId, k -> new AtomicLong(0)).incrementAndGet();
        stats.lastCosmeticUsed = cosmeticId;
        stats.lastActivity = System.currentTimeMillis();
        
        // Track by type
        Cosmetic cosmetic = plugin.getCosmeticManager().getCosmetic(cosmeticId);
        if (cosmetic != null) {
            typeUsageCount.get(cosmetic.getType()).incrementAndGet();
            stats.typeUsage.computeIfAbsent(cosmetic.getType(), k -> new AtomicLong(0)).incrementAndGet();
        }
        
        // Daily statistics
        recordDailyStatistic("cosmetics_activated", player.getUniqueId(), 1);
    }
    
    /**
     * Record achievement unlock
     */
    public void recordAchievementUnlock(Player player, String achievementId) {
        totalAchievementsUnlocked.incrementAndGet();
        achievementUnlockCount.computeIfAbsent(achievementId, k -> new AtomicLong(0)).incrementAndGet();
        
        PlayerStatistics stats = getPlayerStatistics(player.getUniqueId());
        stats.achievementsUnlocked.incrementAndGet();
        stats.achievementHistory.add(achievementId);
        stats.lastAchievement = achievementId;
        stats.lastActivity = System.currentTimeMillis();
        
        recordDailyStatistic("achievements_unlocked", player.getUniqueId(), 1);
    }
    
    /**
     * Record credits earned
     */
    public void recordCreditsEarned(Player player, int amount, String source) {
        totalCreditsEarned.addAndGet(amount);
        
        PlayerStatistics stats = getPlayerStatistics(player.getUniqueId());
        stats.creditsEarned.addAndGet(amount);
        stats.creditSources.computeIfAbsent(source, k -> new AtomicLong(0)).addAndGet(amount);
        stats.lastActivity = System.currentTimeMillis();
        
        recordDailyStatistic("credits_earned", player.getUniqueId(), amount);
    }
    
    /**
     * Record credits spent
     */
    public void recordCreditsSpent(Player player, int amount, String item) {
        totalCreditsSpent.addAndGet(amount);
        
        PlayerStatistics stats = getPlayerStatistics(player.getUniqueId());
        stats.creditsSpent.addAndGet(amount);
        stats.creditSpending.computeIfAbsent(item, k -> new AtomicLong(0)).addAndGet(amount);
        stats.lastPurchase = item;
        stats.lastActivity = System.currentTimeMillis();
        
        recordDailyStatistic("credits_spent", player.getUniqueId(), amount);
    }
    
    /**
     * Record pet interaction
     */
    public void recordPetInteraction(Player player, String petId, String interaction) {
        PlayerStatistics stats = getPlayerStatistics(player.getUniqueId());
        stats.petInteractions.computeIfAbsent(petId, k -> new AtomicLong(0)).incrementAndGet();
        stats.lastPetInteraction = petId + ":" + interaction;
        stats.lastActivity = System.currentTimeMillis();
        
        recordDailyStatistic("pet_interactions", player.getUniqueId(), 1);
    }
    
    /**
     * Update usage time for all active cosmetics
     */
    private void updateActiveUsageTimes() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Set<String> activeCosmetics = plugin.getCosmeticManager().getActiveCosmetics(player);
            PlayerStatistics stats = getPlayerStatistics(player.getUniqueId());
            
            for (String cosmeticId : activeCosmetics) {
                // Add 1 minute to usage time
                cosmeticUsageTime.computeIfAbsent(cosmeticId, k -> new AtomicLong(0)).addAndGet(60000);
                stats.cosmeticUsageTime.computeIfAbsent(cosmeticId, k -> new AtomicLong(0)).addAndGet(60000);
                
                // Track by type
                Cosmetic cosmetic = plugin.getCosmeticManager().getCosmetic(cosmeticId);
                if (cosmetic != null) {
                    typeUsageTime.get(cosmetic.getType()).addAndGet(60000);
                    stats.typeUsageTime.computeIfAbsent(cosmetic.getType(), k -> new AtomicLong(0)).addAndGet(60000);
                }
            }
        }
    }
    
    /**
     * Record daily statistic
     */
    private void recordDailyStatistic(String statType, UUID uuid, long value) {
        String today = java.time.LocalDate.now().toString();
        dailyStats.computeIfAbsent(today, k -> new ConcurrentHashMap<>())
                  .computeIfAbsent(uuid, k -> new AtomicLong(0))
                  .addAndGet(value);
    }
    
    /**
     * Get top players by specific statistic
     */
    public List<Map.Entry<UUID, Long>> getTopPlayersByStat(String statType, int limit) {
        Map<UUID, Long> statMap = new HashMap<>();
        
        for (Map.Entry<UUID, PlayerStatistics> entry : playerStats.entrySet()) {
            PlayerStatistics stats = entry.getValue();
            long value = 0;
            
            switch (statType.toLowerCase()) {
                case "cosmetics_activated":
                    value = stats.cosmeticsActivated.get();
                    break;
                case "achievements_unlocked":
                    value = stats.achievementsUnlocked.get();
                    break;
                case "credits_earned":
                    value = stats.creditsEarned.get();
                    break;
                case "credits_spent":
                    value = stats.creditsSpent.get();
                    break;
                case "total_usage_time":
                    value = stats.getTotalUsageTime();
                    break;
                default:
                    // Unknown stat type, value remains 0
                    break;
            }
            
            if (value > 0) {
                statMap.put(entry.getKey(), value);
            }
        }
        
        return statMap.entrySet().stream()
                .sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Get most popular cosmetics
     */
    public List<Map.Entry<String, Long>> getMostPopularCosmetics(int limit) {
        return cosmeticUsageCount.entrySet().stream()
                .sorted(Map.Entry.<String, AtomicLong>comparingByValue(Comparator.comparing(AtomicLong::get)).reversed())
                .limit(limit)
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue().get()))
                .collect(Collectors.toList());
    }
    
    /**
     * Get cosmetics with most usage time
     */
    public List<Map.Entry<String, Long>> getCosmeticsWithMostUsageTime(int limit) {
        return cosmeticUsageTime.entrySet().stream()
                .sorted(Map.Entry.<String, AtomicLong>comparingByValue(Comparator.comparing(AtomicLong::get)).reversed())
                .limit(limit)
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue().get()))
                .collect(Collectors.toList());
    }
    
    /**
     * Get server-wide statistics
     */
    public Map<String, Object> getServerStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("total_credits_earned", totalCreditsEarned.get());
        stats.put("total_credits_spent", totalCreditsSpent.get());
        stats.put("total_cosmetics_activated", totalCosmeticsActivated.get());
        stats.put("total_achievements_unlocked", totalAchievementsUnlocked.get());
        
        stats.put("unique_players", playerStats.size());
        stats.put("total_cosmetics", plugin.getCosmeticManager().getTotalCosmetics());
        stats.put("total_achievements", plugin.getAchievementManager().getAllAchievements().size());
        
        stats.put("most_popular_cosmetic", getMostPopularCosmetic());
        stats.put("most_used_cosmetic_type", getMostUsedCosmeticType());
        stats.put("most_unlocked_achievement", getMostUnlockedAchievement());
        
        return stats;
    }
    
    private String getMostPopularCosmetic() {
        return cosmeticUsageCount.entrySet().stream()
                .max(Map.Entry.comparingByValue(Comparator.comparing(AtomicLong::get)))
                .map(Map.Entry::getKey)
                .orElse("None");
    }
    
    private String getMostUsedCosmeticType() {
        return typeUsageCount.entrySet().stream()
                .max(Map.Entry.comparingByValue(Comparator.comparing(AtomicLong::get)))
                .map(e -> e.getKey().toString())
                .orElse("None");
    }
    
    private String getMostUnlockedAchievement() {
        return achievementUnlockCount.entrySet().stream()
                .max(Map.Entry.comparingByValue(Comparator.comparing(AtomicLong::get)))
                .map(Map.Entry::getKey)
                .orElse("None");
    }
    
    /**
     * Save all statistics to database
     */
    public void saveAllStatistics() {
        plugin.getSchedulerAdapter().runTaskAsynchronously(() -> {
            try {
                // Save global statistics
                plugin.getDatabaseManager().saveGlobalStatistics(
                    totalCreditsEarned.get(),
                    totalCreditsSpent.get(),
                    totalCosmeticsActivated.get(),
                    totalAchievementsUnlocked.get()
                );
                
                // Save player statistics
                for (Map.Entry<UUID, PlayerStatistics> entry : playerStats.entrySet()) {
                    UUID playerId = entry.getKey();
                    PlayerStatistics stats = entry.getValue();
                    
                    plugin.getDatabaseManager().savePlayerStatistics(playerId, stats);
                }
                
                // Save cosmetic usage statistics
                plugin.getDatabaseManager().saveCosmeticUsageStats(cosmeticUsageCount, cosmeticUsageTime);
                
                plugin.getLogger().fine("Statistics saved to database successfully");
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to save statistics: " + e.getMessage());
            }
        });
    }
    
    /**
     * Load statistics from database
     */
    public void loadStatistics() {
        plugin.getSchedulerAdapter().runTaskAsynchronously(() -> {
            try {
                // Load global statistics
                var globalStats = plugin.getDatabaseManager().loadGlobalStatistics();
                if (globalStats != null && globalStats.length >= 4) {
                    totalCreditsEarned.set(globalStats[0]);
                    totalCreditsSpent.set(globalStats[1]);
                    totalCosmeticsActivated.set(globalStats[2]);
                    totalAchievementsUnlocked.set(globalStats[3]);
                }

                // Load player statistics
                Map<UUID, Object> loadedPlayerStats = plugin.getDatabaseManager().loadAllPlayerStatistics();
                if (loadedPlayerStats != null) {
                    for (Map.Entry<UUID, Object> entry : loadedPlayerStats.entrySet()) {
                        if (entry.getValue() instanceof StatisticsManager.PlayerStatistics stats) {
                            playerStats.put(entry.getKey(), stats);
                        }
                    }
                }
                
                // Load cosmetic usage statistics
                var usageStats = plugin.getDatabaseManager().loadCosmeticUsageStats();
                if (usageStats != null && usageStats.length >= 2) {
                    @SuppressWarnings("unchecked")
                    Map<String, AtomicLong> loadedUsageCount = (Map<String, AtomicLong>) usageStats[0];
                    @SuppressWarnings("unchecked")
                    Map<String, AtomicLong> loadedUsageTime = (Map<String, AtomicLong>) usageStats[1];
                    
                    cosmeticUsageCount.putAll(loadedUsageCount);
                    cosmeticUsageTime.putAll(loadedUsageTime);
                }
                
                plugin.getLogger().info("Statistics loaded from database successfully");
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load statistics: " + e.getMessage());
            }
        });
    }
    
    // Getters for global statistics
    public long getTotalCreditsEarned() { return totalCreditsEarned.get(); }
    public long getTotalCreditsSpent() { return totalCreditsSpent.get(); }
    public long getTotalCosmeticsActivated() { return totalCosmeticsActivated.get(); }
    public long getTotalAchievementsUnlocked() { return totalAchievementsUnlocked.get(); }
    
    public Map<String, AtomicLong> getCosmeticUsageCount() { return cosmeticUsageCount; }
    public Map<String, AtomicLong> getCosmeticUsageTime() { return cosmeticUsageTime; }
    public Map<CosmeticType, AtomicLong> getTypeUsageCount() { return typeUsageCount; }
    public Map<String, AtomicLong> getAchievementUnlockCount() { return achievementUnlockCount; }
    
    /**
     * Inner class to hold per-player statistics
     */
    public static class PlayerStatistics {
        public final AtomicLong cosmeticsActivated = new AtomicLong(0);
        public final AtomicLong achievementsUnlocked = new AtomicLong(0);
        public final AtomicLong creditsEarned = new AtomicLong(0);
        public final AtomicLong creditsSpent = new AtomicLong(0);
        
        public final Map<String, AtomicLong> cosmeticUsage = new ConcurrentHashMap<>();
        public final Map<String, AtomicLong> cosmeticUsageTime = new ConcurrentHashMap<>();
        public final Map<CosmeticType, AtomicLong> typeUsage = new ConcurrentHashMap<>();
        public final Map<CosmeticType, AtomicLong> typeUsageTime = new ConcurrentHashMap<>();
        
        public final Map<String, AtomicLong> creditSources = new ConcurrentHashMap<>();
        public final Map<String, AtomicLong> creditSpending = new ConcurrentHashMap<>();
        public final Map<String, AtomicLong> petInteractions = new ConcurrentHashMap<>();
        
        public final Set<String> achievementHistory = ConcurrentHashMap.newKeySet();
        
        public String lastCosmeticUsed = "None";
        public String lastAchievement = "None";
        public String lastPurchase = "None";
        public String lastPetInteraction = "None";
        public long lastActivity = System.currentTimeMillis();
        
        public long getTotalUsageTime() {
            return cosmeticUsageTime.values().stream()
                    .mapToLong(AtomicLong::get)
                    .sum();
        }
        
        public String getFavoriteCosmetic() {
            return cosmeticUsageTime.entrySet().stream()
                    .max(Map.Entry.comparingByValue(Comparator.comparing(AtomicLong::get)))
                    .map(Map.Entry::getKey)
                    .orElse("None");
        }
        
        public CosmeticType getFavoriteType() {
            return typeUsageTime.entrySet().stream()
                    .max(Map.Entry.comparingByValue(Comparator.comparing(AtomicLong::get)))
                    .map(Map.Entry::getKey)
                    .orElse(null);
        }
    }
}
package com.sneaky.cosmetics.integrations;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * PlaceholderAPI integration for SneakyCosmetics
 * Provides placeholders for cosmetic information
 */
public class PlaceholderAPIIntegration extends PlaceholderExpansion {
    
    private final SneakyCosmetics plugin;
    
    public PlaceholderAPIIntegration(SneakyCosmetics plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public @NotNull String getIdentifier() {
        return "sneakycosmetics";
    }
    
    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }
    
    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }
    
    @Override
    public boolean persist() {
        return true; // Required to stay registered
    }
    
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null || !player.isOnline()) {
            return "";
        }
        
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null) {
            return "";
        }
        
        // %sneakycosmetics_credits%
        if (params.equals("credits")) {
            return String.valueOf(plugin.getCreditManager().getCreditsSync(player.getUniqueId()));
        }
        
        // %sneakycosmetics_total_owned%
        if (params.equals("total_owned")) {
            int count = 0;
            for (var cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
                if (plugin.getCosmeticManager().hasCosmetic(onlinePlayer, cosmetic.getId())) {
                    count++;
                }
            }
            return String.valueOf(count);
        }
        
        // %sneakycosmetics_total_active%
        if (params.equals("total_active")) {
            return String.valueOf(plugin.getCosmeticManager().getActiveCosmetics(onlinePlayer).size());
        }
        
        // %sneakycosmetics_has_free_access%
        if (params.equals("has_free_access")) {
            return onlinePlayer.hasPermission("sneakycosmetics.free") ? "true" : "false";
        }
        
        // %sneakycosmetics_owned_<type>%
        if (params.startsWith("owned_")) {
            String typeName = params.substring(6).toUpperCase();
            try {
                CosmeticType type = CosmeticType.valueOf(typeName);
                int count = 0;
                for (var cosmetic : plugin.getCosmeticManager().getCosmeticsByType(type)) {
                    if (plugin.getCosmeticManager().hasCosmetic(onlinePlayer, cosmetic.getId())) {
                        count++;
                    }
                }
                return String.valueOf(count);
            } catch (IllegalArgumentException e) {
                return "0";
            }
        }
        
        // %sneakycosmetics_active_<type>%
        if (params.startsWith("active_")) {
            String typeName = params.substring(7).toUpperCase();
            try {
                CosmeticType type = CosmeticType.valueOf(typeName);
                for (String cosmeticId : plugin.getCosmeticManager().getActiveCosmetics(onlinePlayer)) {
                    var cosmetic = plugin.getCosmeticManager().getCosmetic(cosmeticId);
                    if (cosmetic != null && cosmetic.getType() == type) {
                        return cosmetic.getDisplayName();
                    }
                }
                return "None";
            } catch (IllegalArgumentException e) {
                return "None";
            }
        }
        
        // %sneakycosmetics_has_<cosmetic_id>%
        if (params.startsWith("has_")) {
            String cosmeticId = params.substring(4);
            return plugin.getCosmeticManager().hasCosmetic(onlinePlayer, cosmeticId) ? "true" : "false";
        }
        
        // %sneakycosmetics_active_<cosmetic_id>%
        if (params.startsWith("using_")) {
            String cosmeticId = params.substring(6);
            return plugin.getCosmeticManager().isCosmeticActive(onlinePlayer, cosmeticId) ? "true" : "false";
        }
        
        // %sneakycosmetics_pet_name_<pet_id>%
        if (params.startsWith("pet_name_")) {
            String petId = params.substring(9);
            // Get custom name from database
            String customName = plugin.getDatabaseManager().getPetCustomName(player.getUniqueId(), petId);
            if (customName != null && !customName.isEmpty()) {
                return customName;
            }
            // Return default name
            var cosmetic = plugin.getCosmeticManager().getCosmetic(petId);
            return cosmetic != null ? cosmetic.getDisplayName() : "Unknown Pet";
        }
        
        // %sneakycosmetics_achievement_progress%
        if (params.equals("achievement_progress")) {
            return String.format("%.1f", plugin.getAchievementManager().getCompletionPercentage(onlinePlayer));
        }
        
        // %sneakycosmetics_achievement_credits%
        if (params.equals("achievement_credits")) {
            return String.valueOf(plugin.getAchievementManager().getTotalCreditsEarned(onlinePlayer));
        }
        
        // Statistics placeholders
        if (plugin.getStatisticsManager() != null) {
            var playerStats = plugin.getStatisticsManager().getPlayerStatistics(player.getUniqueId());
            
            // %sneakycosmetics_stats_cosmetics_activated%
            if (params.equals("stats_cosmetics_activated")) {
                return String.valueOf(playerStats.cosmeticsActivated.get());
            }
            
            // %sneakycosmetics_stats_achievements_unlocked%
            if (params.equals("stats_achievements_unlocked")) {
                return String.valueOf(playerStats.achievementsUnlocked.get());
            }
            
            // %sneakycosmetics_stats_credits_earned%
            if (params.equals("stats_credits_earned")) {
                return String.valueOf(playerStats.creditsEarned.get());
            }
            
            // %sneakycosmetics_stats_credits_spent%
            if (params.equals("stats_credits_spent")) {
                return String.valueOf(playerStats.creditsSpent.get());
            }
            
            // %sneakycosmetics_stats_total_usage_time%
            if (params.equals("stats_total_usage_time")) {
                return formatTime(playerStats.getTotalUsageTime());
            }
            
            // %sneakycosmetics_stats_favorite_cosmetic%
            if (params.equals("stats_favorite_cosmetic")) {
                return playerStats.getFavoriteCosmetic();
            }
            
            // %sneakycosmetics_stats_favorite_type%
            if (params.equals("stats_favorite_type")) {
                CosmeticType type = playerStats.getFavoriteType();
                return type != null ? type.getDisplayName() : "None";
            }
            
            // %sneakycosmetics_stats_last_cosmetic%
            if (params.equals("stats_last_cosmetic")) {
                return playerStats.lastCosmeticUsed;
            }
            
            // %sneakycosmetics_stats_last_achievement%
            if (params.equals("stats_last_achievement")) {
                return playerStats.lastAchievement;
            }
            
            // %sneakycosmetics_stats_last_purchase%
            if (params.equals("stats_last_purchase")) {
                return playerStats.lastPurchase;
            }
            
            // %sneakycosmetics_usage_count_<cosmetic_id>%
            if (params.startsWith("usage_count_")) {
                String cosmeticId = params.substring(13);
                return String.valueOf(playerStats.cosmeticUsage.getOrDefault(cosmeticId, new java.util.concurrent.atomic.AtomicLong(0)).get());
            }
            
            // %sneakycosmetics_usage_time_<cosmetic_id>%
            if (params.startsWith("usage_time_")) {
                String cosmeticId = params.substring(11);
                long time = playerStats.cosmeticUsageTime.getOrDefault(cosmeticId, new java.util.concurrent.atomic.AtomicLong(0)).get();
                return formatTime(time);
            }
            
            // %sneakycosmetics_type_usage_<type>%
            if (params.startsWith("type_usage_")) {
                String typeName = params.substring(11).toUpperCase();
                try {
                    CosmeticType type = CosmeticType.valueOf(typeName);
                    return String.valueOf(playerStats.typeUsage.getOrDefault(type, new java.util.concurrent.atomic.AtomicLong(0)).get());
                } catch (IllegalArgumentException e) {
                    return "0";
                }
            }
            
            // %sneakycosmetics_type_time_<type>%
            if (params.startsWith("type_time_")) {
                String typeName = params.substring(10).toUpperCase();
                try {
                    CosmeticType type = CosmeticType.valueOf(typeName);
                    long time = playerStats.typeUsageTime.getOrDefault(type, new java.util.concurrent.atomic.AtomicLong(0)).get();
                    return formatTime(time);
                } catch (IllegalArgumentException e) {
                    return "0m";
                }
            }
            
            // %sneakycosmetics_credit_source_<source>%
            if (params.startsWith("credit_source_")) {
                String source = params.substring(14);
                return String.valueOf(playerStats.creditSources.getOrDefault(source, new java.util.concurrent.atomic.AtomicLong(0)).get());
            }
        }
        
        // Server-wide statistics
        if (plugin.getStatisticsManager() != null) {
            // %sneakycosmetics_server_total_credits_earned%
            if (params.equals("server_total_credits_earned")) {
                return String.valueOf(plugin.getStatisticsManager().getTotalCreditsEarned());
            }
            
            // %sneakycosmetics_server_total_credits_spent%
            if (params.equals("server_total_credits_spent")) {
                return String.valueOf(plugin.getStatisticsManager().getTotalCreditsSpent());
            }
            
            // %sneakycosmetics_server_total_cosmetics_activated%
            if (params.equals("server_total_cosmetics_activated")) {
                return String.valueOf(plugin.getStatisticsManager().getTotalCosmeticsActivated());
            }
            
            // %sneakycosmetics_server_total_achievements_unlocked%
            if (params.equals("server_total_achievements_unlocked")) {
                return String.valueOf(plugin.getStatisticsManager().getTotalAchievementsUnlocked());
            }
            
            // %sneakycosmetics_server_most_popular_cosmetic%
            if (params.equals("server_most_popular_cosmetic")) {
                var serverStats = plugin.getStatisticsManager().getServerStatistics();
                return (String) serverStats.getOrDefault("most_popular_cosmetic", "None");
            }
            
            // %sneakycosmetics_server_most_used_type%
            if (params.equals("server_most_used_type")) {
                var serverStats = plugin.getStatisticsManager().getServerStatistics();
                return (String) serverStats.getOrDefault("most_used_cosmetic_type", "None");
            }
            
            // %sneakycosmetics_top_player_<stat>_<position>%
            if (params.startsWith("top_player_")) {
                String[] parts = params.substring(11).split("_");
                if (parts.length >= 3) {
                    String stat = parts[0] + "_" + parts[1];
                    try {
                        int position = Integer.parseInt(parts[2]);
                        var topPlayers = plugin.getStatisticsManager().getTopPlayersByStat(stat, position);
                        if (topPlayers.size() >= position) {
                            UUID topPlayerUUID = topPlayers.get(position - 1).getKey();
                            var topPlayer = plugin.getServer().getOfflinePlayer(topPlayerUUID);
                            return topPlayer.getName() != null ? topPlayer.getName() : "Unknown";
                        }
                    } catch (NumberFormatException e) {
                        return "Invalid";
                    }
                }
                return "None";
            }
            
            // %sneakycosmetics_top_value_<stat>_<position>%
            if (params.startsWith("top_value_")) {
                String[] parts = params.substring(10).split("_");
                if (parts.length >= 3) {
                    String stat = parts[0] + "_" + parts[1];
                    try {
                        int position = Integer.parseInt(parts[2]);
                        var topPlayers = plugin.getStatisticsManager().getTopPlayersByStat(stat, position);
                        if (topPlayers.size() >= position) {
                            return String.valueOf(topPlayers.get(position - 1).getValue());
                        }
                    } catch (NumberFormatException e) {
                        return "0";
                    }
                }
                return "0";
            }
        }
        
        // CMI Integration placeholders
        if (plugin.getCMIIntegration() != null && plugin.getCMIIntegration().isAvailable()) {
            // %sneakycosmetics_cmi_balance%
            if (params.equals("cmi_balance")) {
                return String.valueOf(plugin.getCMIIntegration().getBalance(onlinePlayer));
            }
            
            // %sneakycosmetics_cmi_nickname%
            if (params.equals("cmi_nickname")) {
                return plugin.getCMIIntegration().getNickName(onlinePlayer);
            }
            
            // %sneakycosmetics_cmi_playtime%
            if (params.equals("cmi_playtime")) {
                long playtime = plugin.getCMIIntegration().getTotalPlaytime(onlinePlayer);
                return formatTime(playtime);
            }
            
            // %sneakycosmetics_cmi_vanished%
            if (params.equals("cmi_vanished")) {
                return plugin.getCMIIntegration().isVanished(onlinePlayer) ? "true" : "false";
            }
            
            // %sneakycosmetics_cmi_afk%
            if (params.equals("cmi_afk")) {
                return plugin.getCMIIntegration().isAFK(onlinePlayer) ? "true" : "false";
            }
            
            // %sneakycosmetics_cmi_god%
            if (params.equals("cmi_god")) {
                return plugin.getCMIIntegration().isInGodMode(onlinePlayer) ? "true" : "false";
            }
        }
        
        return null; // Placeholder is unknown
    }
    
    /**
     * Format time in milliseconds to a readable format
     */
    private String formatTime(long milliseconds) {
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
}
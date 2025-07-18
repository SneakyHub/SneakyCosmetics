package com.sneaky.cosmetics.listeners;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.managers.CreditManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.Map;
import java.util.Set;

/**
 * Main player event listener for SneakyCosmetics
 * Handles player join/quit events and cosmetic-related interactions
 */
public class PlayerListener implements Listener {
    
    private final SneakyCosmetics plugin;
    private final CreditManager creditManager;
    
    public PlayerListener(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.creditManager = plugin.getCreditManager();
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Handle credit system initialization
        creditManager.handlePlayerJoin(player);
        
        // Check if this is a new player and give welcome credits
        if (!player.hasPlayedBefore()) {
            plugin.getSchedulerAdapter().runTaskLater(() -> {
                creditManager.giveWelcomeCredits(player);
            }, 20L); // Wait 1 second after join
        }
        
        // Check for updates if player is admin
        if (player.hasPermission("sneakycosmetics.admin") && 
            plugin.getConfig().getBoolean("admin.notify-updates", true) &&
            plugin.getUpdateChecker() != null &&
            plugin.getUpdateChecker().isUpdateAvailable()) {
            
            plugin.getSchedulerAdapter().runTaskLater(() -> {
                if (player.isOnline()) {
                    Map<String, String> placeholders = plugin.getMessageManager().createPlaceholders(player);
                    placeholders = plugin.getMessageManager().addPlaceholder(placeholders, "current", plugin.getUpdateChecker().getCurrentVersion());
                    placeholders = plugin.getMessageManager().addPlaceholder(placeholders, "latest", plugin.getUpdateChecker().getLatestVersion());
                    plugin.getMessageManager().sendConfigMessage(player, "updates.available", placeholders);
                }
            }, 60L); // Wait 3 seconds after join
        }
        
        // Load player's active cosmetics
        plugin.getSchedulerAdapter().runTaskAsynchronously(() -> {
            try {
                plugin.getLogger().info("Loading cosmetics for " + player.getName());
                
                // Load and reactivate player's previously active cosmetics
                Set<String> activeCosmetics = plugin.getCosmeticManager().getActiveCosmetics(player);
                for (String cosmeticId : activeCosmetics) {
                    plugin.getSchedulerAdapter().runTask(() -> {
                        if (player.isOnline()) {
                            plugin.getCosmeticManager().activateCosmetic(player, cosmeticId);
                        }
                    });
                }
                
                plugin.getLogger().fine("Loaded " + activeCosmetics.size() + " active cosmetics for " + player.getName());
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load cosmetics for " + player.getName() + ": " + e.getMessage());
            }
        });
        
        // Note: Achievement auto-check on join has been disabled
        // Players must now manually claim achievements via the GUI
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Handle credit system cleanup
        creditManager.handlePlayerQuit(player);
        
        // Stop any active cosmetic effects
        plugin.getSchedulerAdapter().runTask(() -> {
            // Stop particles
            if (plugin.getParticleManager() != null) {
                plugin.getParticleManager().stopPlayerEffects(player);
            }
            
            // Stop trails
            if (plugin.getTrailManager() != null) {
                plugin.getTrailManager().stopPlayerTrails(player);
            }
            
            // Remove pets
            if (plugin.getPetManager() != null) {
                plugin.getPetManager().removePet(player);
            }
            
            // Stop wing effects
            if (plugin.getWingManager() != null) {
                plugin.getWingManager().stopPlayerWings(player);
            }
            
            // Stop aura effects
            if (plugin.getAuraManager() != null) {
                plugin.getAuraManager().stopPlayerAuras(player);
            }
        });
        
        // Save player data asynchronously
        plugin.getSchedulerAdapter().runTaskAsynchronously(() -> {
            try {
                plugin.getLogger().info("Saving cosmetics data for " + player.getName());
                
                // Save current active cosmetics state to database
                Set<String> activeCosmetics = plugin.getCosmeticManager().getActiveCosmetics(player);
                plugin.getDatabaseManager().savePlayerActiveCosmetics(player.getUniqueId(), activeCosmetics);
                
                // Record session statistics
                if (plugin.getStatisticsManager() != null) {
                    // Update last activity time
                    var stats = plugin.getStatisticsManager().getPlayerStatistics(player.getUniqueId());
                    stats.lastActivity = System.currentTimeMillis();
                }
                
                plugin.getLogger().fine("Saved " + activeCosmetics.size() + " active cosmetics for " + player.getName());
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to save cosmetics data for " + player.getName() + ": " + e.getMessage());
            }
        });
        
        // Clean up any cached data after a delay
        plugin.getSchedulerAdapter().runTaskLater(() -> {
            plugin.getDatabaseManager().invalidateCache(player.getUniqueId());
        }, 200L); // Wait 10 seconds before cache cleanup
    }
}
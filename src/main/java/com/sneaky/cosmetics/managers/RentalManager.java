package com.sneaky.cosmetics.managers;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.TimedCosmetic;
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
 * Manages cosmetic rentals and timed cosmetics
 */
public class RentalManager {
    
    private final SneakyCosmetics plugin;
    private final Map<String, TimedCosmetic> availableRentals = new ConcurrentHashMap<>();
    private final Map<UUID, Set<String>> activeRentals = new ConcurrentHashMap<>();
    private final Map<String, Long> rentalExpirationTimes = new ConcurrentHashMap<>();
    
    public RentalManager(SneakyCosmetics plugin) {
        this.plugin = plugin;
        loadRentalsFromDatabase();
        startExpirationChecker();
        initializeDefaultRentals();
    }
    
    /**
     * Initialize default rental options for cosmetics
     */
    private void initializeDefaultRentals() {
        // Register rentals for all cosmetics
        for (Cosmetic cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
            registerDefaultRentalsForCosmetic(cosmetic);
        }
        
        plugin.getLogger().info("Initialized " + availableRentals.size() + " rental options");
    }
    
    /**
     * Register default rental options for a cosmetic
     */
    private void registerDefaultRentalsForCosmetic(Cosmetic cosmetic) {
        if (cosmetic.getPrice() == 0) {
            return; // Don't create rentals for free cosmetics
        }
        
        int basePrice = cosmetic.getPrice();
        
        // 1 hour rental - 20% of purchase price
        TimedCosmetic hourly = new TimedCosmetic(
            cosmetic, 1, TimeUnit.HOURS, 
            Math.max(1, (int)(basePrice * 0.20)), true
        );
        availableRentals.put(hourly.getRentalId(), hourly);
        
        // 1 day rental - 50% of purchase price
        TimedCosmetic daily = new TimedCosmetic(
            cosmetic, 1, TimeUnit.DAYS, 
            Math.max(5, (int)(basePrice * 0.50)), true
        );
        availableRentals.put(daily.getRentalId(), daily);
        
        // 1 week rental - 80% of purchase price
        TimedCosmetic weekly = new TimedCosmetic(
            cosmetic, 7, TimeUnit.DAYS, 
            Math.max(10, (int)(basePrice * 0.80)), true
        );
        availableRentals.put(weekly.getRentalId(), weekly);
        
        // For premium cosmetics, add shorter test rentals
        if (cosmetic.requiresPremium() || cosmetic.requiresVIP()) {
            TimedCosmetic trial = new TimedCosmetic(
                cosmetic, 30, TimeUnit.MINUTES, 
                Math.max(1, (int)(basePrice * 0.10)), false
            );
            availableRentals.put(trial.getRentalId(), trial);
        }
    }
    
    /**
     * Rent a cosmetic for a player
     */
    public boolean rentCosmetic(Player player, String rentalId) {
        TimedCosmetic rental = availableRentals.get(rentalId);
        if (rental == null) {
            player.sendMessage("§c✗ Rental not found!");
            return false;
        }
        
        // Check if player can rent
        if (!rental.canPlayerRent(player)) {
            player.sendMessage("§c✗ " + rental.getRentalDeniedReason(player));
            return false;
        }
        
        // Check if already rented
        if (hasActiveRental(player, rental.getBaseCosmetic().getId())) {
            player.sendMessage("§c✗ You already have this cosmetic rented!");
            return false;
        }
        
        // Check if player owns the cosmetic permanently
        if (plugin.getCosmeticManager().hasCosmetic(player, rental.getBaseCosmetic().getId())) {
            player.sendMessage("§c✗ You already own this cosmetic permanently!");
            return false;
        }
        
        // Check credits
        int playerCredits = plugin.getCreditManager().getCreditsSync(player.getUniqueId());
        if (playerCredits < rental.getRentalPrice()) {
            player.sendMessage("§c✗ Insufficient credits! Need " + rental.getRentalPrice() + " credits.");
            return false;
        }
        
        // Deduct credits
        plugin.getCreditManager().removeCredits(player.getUniqueId(), rental.getRentalPrice());
        
        // Add rental
        long expirationTime = System.currentTimeMillis() + rental.getDuration();
        String rentalKey = player.getUniqueId() + ":" + rental.getBaseCosmetic().getId();
        
        activeRentals.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(rental.getBaseCosmetic().getId());
        rentalExpirationTimes.put(rentalKey, expirationTime);
        
        // Save to database
        saveRentalToDatabase(player, rental, expirationTime);
        
        player.sendMessage("§a✓ Successfully rented " + rental.getBaseCosmetic().getDisplayName() + 
                          " for " + rental.getFormattedDuration() + "!");
        player.sendMessage("§7Rental expires: §e" + formatExpirationTime(expirationTime));
        
        return true;
    }
    
    /**
     * Extend an existing rental
     */
    public boolean extendRental(Player player, String cosmeticId, String rentalId) {
        TimedCosmetic rental = availableRentals.get(rentalId);
        if (rental == null || !rental.canExtend()) {
            player.sendMessage("§c✗ This rental cannot be extended!");
            return false;
        }
        
        if (!hasActiveRental(player, cosmeticId)) {
            player.sendMessage("§c✗ You don't have an active rental for this cosmetic!");
            return false;
        }
        
        // Check credits
        int playerCredits = plugin.getCreditManager().getCreditsSync(player.getUniqueId());
        if (playerCredits < rental.getRentalPrice()) {
            player.sendMessage("§c✗ Insufficient credits! Need " + rental.getRentalPrice() + " credits.");
            return false;
        }
        
        // Deduct credits
        plugin.getCreditManager().removeCredits(player.getUniqueId(), rental.getRentalPrice());
        
        // Extend rental
        String rentalKey = player.getUniqueId() + ":" + cosmeticId;
        long currentExpiration = rentalExpirationTimes.getOrDefault(rentalKey, System.currentTimeMillis());
        long newExpiration = Math.max(currentExpiration, System.currentTimeMillis()) + rental.getDuration();
        
        rentalExpirationTimes.put(rentalKey, newExpiration);
        updateRentalInDatabase(player, cosmeticId, newExpiration);
        
        player.sendMessage("§a✓ Extended rental for " + rental.getBaseCosmetic().getDisplayName() + 
                          " by " + rental.getFormattedDuration() + "!");
        player.sendMessage("§7New expiration: §e" + formatExpirationTime(newExpiration));
        
        return true;
    }
    
    /**
     * Check if a player has an active rental for a cosmetic
     */
    public boolean hasActiveRental(Player player, String cosmeticId) {
        Set<String> playerRentals = activeRentals.get(player.getUniqueId());
        if (playerRentals == null || !playerRentals.contains(cosmeticId)) {
            return false;
        }
        
        String rentalKey = player.getUniqueId() + ":" + cosmeticId;
        Long expirationTime = rentalExpirationTimes.get(rentalKey);
        
        return expirationTime != null && expirationTime > System.currentTimeMillis();
    }
    
    /**
     * Get rental expiration time for a player's cosmetic
     */
    public long getRentalExpiration(Player player, String cosmeticId) {
        String rentalKey = player.getUniqueId() + ":" + cosmeticId;
        return rentalExpirationTimes.getOrDefault(rentalKey, 0L);
    }
    
    /**
     * Get all active rentals for a player
     */
    public Map<String, Long> getPlayerRentals(Player player) {
        Map<String, Long> playerRentals = new HashMap<>();
        Set<String> rentedCosmetics = activeRentals.get(player.getUniqueId());
        
        if (rentedCosmetics != null) {
            for (String cosmeticId : rentedCosmetics) {
                String rentalKey = player.getUniqueId() + ":" + cosmeticId;
                Long expiration = rentalExpirationTimes.get(rentalKey);
                if (expiration != null && expiration > System.currentTimeMillis()) {
                    playerRentals.put(cosmeticId, expiration);
                }
            }
        }
        
        return playerRentals;
    }
    
    /**
     * Get available rentals for a cosmetic
     */
    public List<TimedCosmetic> getRentalsForCosmetic(String cosmeticId) {
        List<TimedCosmetic> rentals = new ArrayList<>();
        for (TimedCosmetic rental : availableRentals.values()) {
            if (rental.getBaseCosmetic().getId().equals(cosmeticId)) {
                rentals.add(rental);
            }
        }
        return rentals;
    }
    
    /**
     * Get all available rentals
     */
    public Collection<TimedCosmetic> getAllRentals() {
        return availableRentals.values();
    }
    
    /**
     * Start the expiration checker task
     */
    private void startExpirationChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkExpiredRentals();
            }
        }.runTaskTimerAsynchronously(plugin, 200L, 1200L); // Check every minute
    }
    
    /**
     * Check for expired rentals and remove them
     */
    private void checkExpiredRentals() {
        long currentTime = System.currentTimeMillis();
        List<String> expiredRentals = new ArrayList<>();
        
        for (Map.Entry<String, Long> entry : rentalExpirationTimes.entrySet()) {
            if (entry.getValue() <= currentTime) {
                expiredRentals.add(entry.getKey());
            }
        }
        
        for (String rentalKey : expiredRentals) {
            String[] parts = rentalKey.split(":");
            if (parts.length == 2) {
                UUID playerUUID = UUID.fromString(parts[0]);
                String cosmeticId = parts[1];
                
                // Remove from memory
                Set<String> playerRentals = activeRentals.get(playerUUID);
                if (playerRentals != null) {
                    playerRentals.remove(cosmeticId);
                    if (playerRentals.isEmpty()) {
                        activeRentals.remove(playerUUID);
                    }
                }
                rentalExpirationTimes.remove(rentalKey);
                
                // Remove from database
                removeRentalFromDatabase(playerUUID, cosmeticId);
                
                // Notify player if online
                Player player = plugin.getServer().getPlayer(playerUUID);
                if (player != null && player.isOnline()) {
                    Cosmetic cosmetic = plugin.getCosmeticManager().getCosmetic(cosmeticId);
                    if (cosmetic != null) {
                        player.sendMessage("§c⏰ Your rental for " + cosmetic.getDisplayName() + " has expired!");
                        
                        // Deactivate if currently active
                        if (cosmetic.isActive(player)) {
                            cosmetic.deactivate(player);
                            player.sendMessage("§7Cosmetic has been deactivated.");
                        }
                    }
                }
            }
        }
        
        if (!expiredRentals.isEmpty()) {
            plugin.getLogger().info("Cleaned up " + expiredRentals.size() + " expired rentals");
        }
    }
    
    /**
     * Format expiration time for display
     */
    private String formatExpirationTime(long expirationTime) {
        long remainingTime = expirationTime - System.currentTimeMillis();
        if (remainingTime <= 0) {
            return "Expired";
        }
        
        long days = TimeUnit.MILLISECONDS.toDays(remainingTime);
        long hours = TimeUnit.MILLISECONDS.toHours(remainingTime) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTime) % 60;
        
        if (days > 0) {
            return days + "d " + hours + "h " + minutes + "m";
        } else if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + "m";
        }
    }
    
    // Database methods
    private void saveRentalToDatabase(Player player, TimedCosmetic rental, long expirationTime) {
        plugin.getSchedulerAdapter().runTaskAsynchronously(() -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection()) {
                String sql = "INSERT OR IGNORE INTO cosmetic_rentals (player_uuid, cosmetic_id, rental_id, rented_at, expires_at, rental_price) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, player.getUniqueId().toString());
                    stmt.setString(2, rental.getBaseCosmetic().getId());
                    stmt.setString(3, rental.getRentalId());
                    stmt.setLong(4, System.currentTimeMillis());
                    stmt.setLong(5, expirationTime);
                    stmt.setInt(6, rental.getRentalPrice());
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to save rental to database: " + e.getMessage());
            }
        });
    }
    
    private void updateRentalInDatabase(Player player, String cosmeticId, long newExpirationTime) {
        plugin.getSchedulerAdapter().runTaskAsynchronously(() -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection()) {
                String sql = "UPDATE cosmetic_rentals SET expires_at = ? WHERE player_uuid = ? AND cosmetic_id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setLong(1, newExpirationTime);
                    stmt.setString(2, player.getUniqueId().toString());
                    stmt.setString(3, cosmeticId);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to update rental in database: " + e.getMessage());
            }
        });
    }
    
    private void removeRentalFromDatabase(UUID playerUUID, String cosmeticId) {
        plugin.getSchedulerAdapter().runTaskAsynchronously(() -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection()) {
                String sql = "DELETE FROM cosmetic_rentals WHERE player_uuid = ? AND cosmetic_id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, playerUUID.toString());
                    stmt.setString(2, cosmeticId);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to remove rental from database: " + e.getMessage());
            }
        });
    }
    
    private void loadRentalsFromDatabase() {
        plugin.getSchedulerAdapter().runTaskAsynchronously(() -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection()) {
                String sql = "SELECT player_uuid, cosmetic_id, expires_at FROM cosmetic_rentals WHERE expires_at > ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setLong(1, System.currentTimeMillis());
                    try (ResultSet rs = stmt.executeQuery()) {
                        int loadedCount = 0;
                        while (rs.next()) {
                            UUID playerUUID = UUID.fromString(rs.getString("player_uuid"));
                            String cosmeticId = rs.getString("cosmetic_id");
                            long expirationTime = rs.getLong("expires_at");
                            
                            activeRentals.computeIfAbsent(playerUUID, k -> new HashSet<>()).add(cosmeticId);
                            rentalExpirationTimes.put(playerUUID + ":" + cosmeticId, expirationTime);
                            loadedCount++;
                        }
                        plugin.getLogger().info("Loaded " + loadedCount + " active rentals from database");
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to load rentals from database: " + e.getMessage());
            }
        });
    }
    
    /**
     * Cleanup on plugin disable
     */
    public void shutdown() {
        // Save any remaining data if needed
        plugin.getLogger().info("RentalManager shutdown complete");
    }
}
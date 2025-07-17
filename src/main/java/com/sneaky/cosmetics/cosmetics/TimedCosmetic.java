package com.sneaky.cosmetics.cosmetics;

import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

/**
 * Represents a timed cosmetic that can be rented for a specific duration
 * Extends the base Cosmetic class with rental functionality
 */
public class TimedCosmetic {
    
    private final Cosmetic baseCosmetic;
    private final long duration; // Duration in milliseconds
    private final int rentalPrice; // Price to rent this cosmetic
    private final TimeUnit timeUnit;
    private final boolean canExtend; // Whether rental can be extended
    
    public TimedCosmetic(Cosmetic baseCosmetic, long duration, TimeUnit timeUnit, 
                        int rentalPrice, boolean canExtend) {
        this.baseCosmetic = baseCosmetic;
        this.duration = timeUnit.toMillis(duration);
        this.timeUnit = timeUnit;
        this.rentalPrice = rentalPrice;
        this.canExtend = canExtend;
    }
    
    /**
     * Get the base cosmetic this rental is for
     */
    public Cosmetic getBaseCosmetic() {
        return baseCosmetic;
    }
    
    /**
     * Get the rental duration in milliseconds
     */
    public long getDuration() {
        return duration;
    }
    
    /**
     * Get the rental duration in the original time unit
     */
    public long getDurationInOriginalUnit() {
        return timeUnit.convert(duration, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Get the time unit for this rental
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
    
    /**
     * Get the rental price
     */
    public int getRentalPrice() {
        return rentalPrice;
    }
    
    /**
     * Check if this rental can be extended
     */
    public boolean canExtend() {
        return canExtend;
    }
    
    /**
     * Get formatted duration string
     */
    public String getFormattedDuration() {
        long amount = getDurationInOriginalUnit();
        String unit = timeUnit.name().toLowerCase();
        
        if (amount == 1) {
            // Remove 's' from plural form
            if (unit.endsWith("s")) {
                unit = unit.substring(0, unit.length() - 1);
            }
        }
        
        return amount + " " + unit;
    }
    
    /**
     * Get the unique rental ID
     */
    public String getRentalId() {
        return "rental_" + baseCosmetic.getId() + "_" + getDurationInOriginalUnit() + timeUnit.name().toLowerCase();
    }
    
    /**
     * Check if a player can rent this cosmetic
     */
    public boolean canPlayerRent(Player player) {
        return baseCosmetic.canPlayerAccess(player);
    }
    
    /**
     * Get the reason why a player cannot rent this cosmetic
     */
    public String getRentalDeniedReason(Player player) {
        return baseCosmetic.getAccessDeniedReason(player);
    }
    
    /**
     * Get formatted rental information
     */
    public String getFormattedRentalInfo() {
        StringBuilder info = new StringBuilder();
        info.append("§6Rental: ").append(baseCosmetic.getDisplayName()).append("\n");
        info.append("§7Duration: §e").append(getFormattedDuration()).append("\n");
        info.append("§7Rental Price: ").append(rentalPrice == 0 ? "§aFree" : "§e" + rentalPrice + " credits").append("\n");
        info.append("§7Purchase Price: ").append(baseCosmetic.getPrice() == 0 ? "§aFree" : "§e" + baseCosmetic.getPrice() + " credits").append("\n");
        
        if (canExtend) {
            info.append("§aRental can be extended\n");
        } else {
            info.append("§cRental cannot be extended\n");
        }
        
        info.append("§8Base cosmetic: ").append(baseCosmetic.getId());
        
        return info.toString().trim();
    }
    
    @Override
    public String toString() {
        return "TimedCosmetic{" +
                "baseCosmetic=" + baseCosmetic.getId() +
                ", duration=" + getFormattedDuration() +
                ", rentalPrice=" + rentalPrice +
                ", canExtend=" + canExtend +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TimedCosmetic that = (TimedCosmetic) obj;
        return getRentalId().equals(that.getRentalId());
    }
    
    @Override
    public int hashCode() {
        return getRentalId().hashCode();
    }
}
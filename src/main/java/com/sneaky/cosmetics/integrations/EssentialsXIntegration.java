package com.sneaky.cosmetics.integrations;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.sneaky.cosmetics.SneakyCosmetics;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

/**
 * Integration with EssentialsX for additional economy features
 * Provides backup economy support if Vault is not available
 */
public class EssentialsXIntegration {
    
    private final SneakyCosmetics plugin;
    private boolean enabled;
    
    public EssentialsXIntegration(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.enabled = false;
    }
    
    /**
     * Initialize EssentialsX integration
     */
    public boolean initialize() {
        try {
            // Check if EssentialsX is available
            if (plugin.getServer().getPluginManager().getPlugin("Essentials") == null &&
                plugin.getServer().getPluginManager().getPlugin("EssentialsX") == null) {
                return false;
            }
            
            // Test if the API is available
            Class.forName("com.earth2me.essentials.api.Economy");
            
            this.enabled = true;
            plugin.getLogger().info("Successfully hooked into EssentialsX!");
            return true;
        } catch (ClassNotFoundException e) {
            plugin.getLogger().warning("EssentialsX API not found: " + e.getMessage());
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to initialize EssentialsX integration: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if EssentialsX integration is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Get a player's balance from EssentialsX
     */
    public double getBalance(Player player) {
        if (!isEnabled()) {
            return 0.0;
        }
        
        try {
            BigDecimal balance = Economy.getMoneyExact(player.getUniqueId());
            return balance.doubleValue();
        } catch (UserDoesNotExistException e) {
            plugin.getLogger().warning("Player " + player.getName() + " does not exist in EssentialsX database");
            return 0.0;
        } catch (Exception e) {
            plugin.getLogger().warning("Error getting balance for " + player.getName() + " from EssentialsX: " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Check if a player has enough money
     */
    public boolean hasBalance(Player player, double amount) {
        if (!isEnabled()) {
            return false;
        }
        
        try {
            return Economy.hasEnough(player.getUniqueId(), BigDecimal.valueOf(amount));
        } catch (UserDoesNotExistException e) {
            plugin.getLogger().warning("Player " + player.getName() + " does not exist in EssentialsX database");
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("Error checking balance for " + player.getName() + " in EssentialsX: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Withdraw money from a player's account
     */
    public boolean withdraw(Player player, double amount) {
        if (!isEnabled()) {
            return false;
        }
        
        if (amount <= 0) {
            return false;
        }
        
        try {
            Economy.subtract(player.getUniqueId(), BigDecimal.valueOf(amount));
            plugin.getLogger().info("Withdrew $" + String.format("%.2f", amount) + " from " + player.getName() + 
                                   " via EssentialsX. New balance: $" + String.format("%.2f", getBalance(player)));
            return true;
        } catch (UserDoesNotExistException e) {
            plugin.getLogger().warning("Player " + player.getName() + " does not exist in EssentialsX database");
            return false;
        } catch (NoLoanPermittedException e) {
            plugin.getLogger().info("Player " + player.getName() + " doesn't have enough money for withdrawal");
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("Error withdrawing money from " + player.getName() + " via EssentialsX: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deposit money to a player's account
     */
    public boolean deposit(Player player, double amount) {
        if (!isEnabled()) {
            return false;
        }
        
        if (amount <= 0) {
            return false;
        }
        
        try {
            Economy.add(player.getUniqueId(), BigDecimal.valueOf(amount));
            plugin.getLogger().info("Deposited $" + String.format("%.2f", amount) + " to " + player.getName() + 
                                   " via EssentialsX. New balance: $" + String.format("%.2f", getBalance(player)));
            return true;
        } catch (UserDoesNotExistException e) {
            plugin.getLogger().warning("Player " + player.getName() + " does not exist in EssentialsX database");
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("Error depositing money to " + player.getName() + " via EssentialsX: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Set a player's balance
     */
    public boolean setBalance(Player player, double amount) {
        if (!isEnabled()) {
            return false;
        }
        
        if (amount < 0) {
            return false;
        }
        
        try {
            Economy.setMoney(player.getUniqueId(), BigDecimal.valueOf(amount));
            plugin.getLogger().info("Set balance for " + player.getName() + " to $" + String.format("%.2f", amount) + " via EssentialsX");
            return true;
        } catch (UserDoesNotExistException e) {
            plugin.getLogger().warning("Player " + player.getName() + " does not exist in EssentialsX database");
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("Error setting balance for " + player.getName() + " via EssentialsX: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Format a monetary amount for display
     */
    public String formatAmount(double amount) {
        return "$" + String.format("%.2f", amount);
    }
    
    /**
     * Get the currency symbol/name
     */
    public String getCurrencySymbol() {
        return "$";
    }
    
    /**
     * Check if a player exists in EssentialsX database
     */
    public boolean playerExists(Player player) {
        if (!isEnabled()) {
            return false;
        }
        
        try {
            Economy.getMoneyExact(player.getUniqueId());
            return true;
        } catch (UserDoesNotExistException e) {
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("Error checking if player exists in EssentialsX: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Create a player account in EssentialsX (if they don't exist)
     */
    public boolean createPlayerAccount(Player player) {
        if (!isEnabled()) {
            return false;
        }
        
        try {
            if (!playerExists(player)) {
                // EssentialsX automatically creates accounts when setting money
                Economy.setMoney(player.getUniqueId(), BigDecimal.ZERO);
                plugin.getLogger().info("Created EssentialsX account for " + player.getName());
                return true;
            }
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Error creating EssentialsX account for " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Transfer money between players
     */
    public boolean transferMoney(Player from, Player to, double amount) {
        if (!isEnabled()) {
            return false;
        }
        
        if (amount <= 0) {
            return false;
        }
        
        if (!hasBalance(from, amount)) {
            return false;
        }
        
        try {
            if (withdraw(from, amount)) {
                if (deposit(to, amount)) {
                    plugin.getLogger().info("Transferred $" + String.format("%.2f", amount) + 
                                           " from " + from.getName() + " to " + to.getName() + " via EssentialsX");
                    return true;
                } else {
                    // Rollback the withdrawal
                    deposit(from, amount);
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("Error transferring money via EssentialsX: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get information about the EssentialsX integration
     */
    public String getIntegrationInfo() {
        if (!isEnabled()) {
            return "EssentialsX integration: Disabled";
        }
        
        StringBuilder info = new StringBuilder();
        info.append("EssentialsX integration: Enabled\n");
        info.append("Currency Symbol: ").append(getCurrencySymbol()).append("\n");
        info.append("Backup Economy: Available");
        
        return info.toString();
    }
    
    /**
     * Test the EssentialsX integration
     */
    public boolean testIntegration() {
        if (!isEnabled()) {
            return false;
        }
        
        try {
            // Test basic functionality by checking if API is accessible
            Economy.getMoneyExact(java.util.UUID.randomUUID());
            return true;
        } catch (UserDoesNotExistException e) {
            // This is expected for a random UUID
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("EssentialsX integration test failed: " + e.getMessage());
            return false;
        }
    }
}
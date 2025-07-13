package com.sneaky.cosmetics.integrations;

import com.sneaky.cosmetics.SneakyCosmetics;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Integration with Vault economy plugin
 * Handles money transactions for purchasing credits
 */
public class VaultIntegration {
    
    private final SneakyCosmetics plugin;
    private Economy economy;
    private boolean enabled;
    
    public VaultIntegration(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.enabled = false;
    }
    
    /**
     * Setup the economy provider
     */
    public boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().warning("Vault not found! Economy features will be disabled.");
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().warning("No economy provider found! Economy features will be disabled.");
            return false;
        }
        
        economy = rsp.getProvider();
        enabled = true;
        
        plugin.getLogger().info("Successfully hooked into economy provider: " + economy.getName());
        return true;
    }
    
    /**
     * Check if Vault integration is enabled
     */
    public boolean isEnabled() {
        return enabled && economy != null;
    }
    
    /**
     * Get the economy provider
     */
    public Economy getEconomy() {
        return economy;
    }
    
    /**
     * Get a player's balance
     */
    public double getBalance(Player player) {
        if (!isEnabled()) {
            return 0.0;
        }
        
        try {
            return economy.getBalance(player);
        } catch (Exception e) {
            plugin.getLogger().warning("Error getting balance for " + player.getName() + ": " + e.getMessage());
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
            return economy.has(player, amount);
        } catch (Exception e) {
            plugin.getLogger().warning("Error checking balance for " + player.getName() + ": " + e.getMessage());
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
            net.milkbowl.vault.economy.EconomyResponse response = economy.withdrawPlayer(player, amount);
            
            if (response.transactionSuccess()) {
                plugin.getLogger().info("Withdrew $" + String.format("%.2f", amount) + " from " + player.getName() + 
                                     " for credit purchase. New balance: $" + String.format("%.2f", response.balance));
                return true;
            } else {
                plugin.getLogger().warning("Failed to withdraw $" + amount + " from " + player.getName() + 
                                         ": " + response.errorMessage);
                return false;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error withdrawing money from " + player.getName() + ": " + e.getMessage());
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
            net.milkbowl.vault.economy.EconomyResponse response = economy.depositPlayer(player, amount);
            
            if (response.transactionSuccess()) {
                plugin.getLogger().info("Deposited $" + String.format("%.2f", amount) + " to " + player.getName() + 
                                     ". New balance: $" + String.format("%.2f", response.balance));
                return true;
            } else {
                plugin.getLogger().warning("Failed to deposit $" + amount + " to " + player.getName() + 
                                         ": " + response.errorMessage);
                return false;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error depositing money to " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Format a monetary amount for display
     */
    public String formatAmount(double amount) {
        if (!isEnabled()) {
            return String.format("%.2f", amount);
        }
        
        try {
            return economy.format(amount);
        } catch (Exception e) {
            return String.format("%.2f", amount);
        }
    }
    
    /**
     * Get the currency name (singular)
     */
    public String getCurrencyName() {
        if (!isEnabled()) {
            return "dollar";
        }
        
        try {
            return economy.currencyNameSingular();
        } catch (Exception e) {
            return "dollar";
        }
    }
    
    /**
     * Get the currency name (plural)
     */
    public String getCurrencyNamePlural() {
        if (!isEnabled()) {
            return "dollars";
        }
        
        try {
            return economy.currencyNamePlural();
        } catch (Exception e) {
            return "dollars";
        }
    }
    
    /**
     * Check if the economy supports banks
     */
    public boolean hasBankSupport() {
        if (!isEnabled()) {
            return false;
        }
        
        try {
            return economy.hasBankSupport();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get information about the economy provider
     */
    public String getEconomyInfo() {
        if (!isEnabled()) {
            return "Economy not available";
        }
        
        StringBuilder info = new StringBuilder();
        info.append("Economy Provider: ").append(economy.getName()).append("\n");
        info.append("Currency: ").append(getCurrencyName()).append(" (").append(getCurrencyNamePlural()).append(")\n");
        info.append("Bank Support: ").append(hasBankSupport() ? "Yes" : "No").append("\n");
        info.append("Fractional Digits: ").append(economy.fractionalDigits());
        
        return info.toString();
    }
    
    /**
     * Test the economy integration
     */
    public boolean testIntegration() {
        if (!isEnabled()) {
            return false;
        }
        
        try {
            // Test basic economy functions
            String name = economy.getName();
            String currency = economy.currencyNameSingular();
            boolean bankSupport = economy.hasBankSupport();
            
            plugin.getLogger().info("Economy integration test passed:");
            plugin.getLogger().info("- Provider: " + name);
            plugin.getLogger().info("- Currency: " + currency);
            plugin.getLogger().info("- Bank Support: " + bankSupport);
            
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Economy integration test failed: " + e.getMessage());
            return false;
        }
    }
}
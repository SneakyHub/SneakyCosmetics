package com.sneaky.cosmetics.integrations;

import com.sneaky.cosmetics.SneakyCosmetics;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Integration with CMI (Creative Multi Interface) plugin
 * Uses reflection to avoid compile-time dependencies
 * Provides enhanced economy support, user management, and messaging features
 */
public class CMIIntegration {
    
    private final SneakyCosmetics plugin;
    private boolean initialized = false;
    private Object cmiLib;
    private Plugin cmiPlugin;
    
    public CMIIntegration(SneakyCosmetics plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Initialize CMI integration using reflection
     */
    public boolean initialize() {
        try {
            this.cmiPlugin = plugin.getServer().getPluginManager().getPlugin("CMI");
            if (cmiPlugin == null || !cmiPlugin.isEnabled()) {
                plugin.getLogger().info("CMI not found, CMI integration disabled");
                return false;
            }
            
            // Try to get CMILib using reflection
            try {
                Class<?> cmiLibClass = Class.forName("net.Zrips.CMILib.CMILib");
                Method getInstance = cmiLibClass.getMethod("getInstance");
                this.cmiLib = getInstance.invoke(null);
                
                if (cmiLib == null) {
                    plugin.getLogger().warning("CMILib not available, CMI integration disabled");
                    return false;
                }
            } catch (ClassNotFoundException e) {
                plugin.getLogger().info("CMILib not found, using basic CMI integration");
                this.cmiLib = null; // Use basic integration without CMILib
            }
            
            this.initialized = true;
            plugin.getLogger().info("CMI integration initialized successfully (v" + cmiPlugin.getDescription().getVersion() + ")");
            
            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to initialize CMI integration", e);
            return false;
        }
    }
    
    /**
     * Check if CMI integration is available and initialized
     */
    public boolean isAvailable() {
        return initialized && cmiPlugin != null;
    }
    
    /**
     * Get CMI user data for a player using reflection
     */
    private Object getCMIUser(Player player) {
        if (!isAvailable() || cmiLib == null) {
            return null;
        }
        
        try {
            Method getUser = cmiLib.getClass().getMethod("getUser", Player.class);
            return getUser.invoke(cmiLib, player);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error getting CMI user for " + player.getName(), e);
            return null;
        }
    }
    
    /**
     * Get CMI user data by UUID using reflection
     */
    private Object getCMIUser(UUID uuid) {
        if (!isAvailable() || cmiLib == null) {
            return null;
        }
        
        try {
            Method getUser = cmiLib.getClass().getMethod("getUser", UUID.class);
            return getUser.invoke(cmiLib, uuid);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error getting CMI user for " + uuid, e);
            return null;
        }
    }
    
    /**
     * Check if player has CMI vanish enabled
     */
    public boolean isVanished(Player player) {
        if (!isAvailable()) {
            return false;
        }
        
        try {
            Object user = getCMIUser(player);
            if (user != null) {
                Method isVanished = user.getClass().getMethod("isVanished");
                return (Boolean) isVanished.invoke(user);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error checking vanish status for " + player.getName(), e);
        }
        return false;
    }
    
    /**
     * Check if player is in god mode
     */
    public boolean isInGodMode(Player player) {
        if (!isAvailable()) {
            return false;
        }
        
        try {
            Object user = getCMIUser(player);
            if (user != null) {
                Method isGod = user.getClass().getMethod("isGod");
                return (Boolean) isGod.invoke(user);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error checking god mode for " + player.getName(), e);
        }
        return false;
    }
    
    /**
     * Check if player is AFK
     */
    public boolean isAFK(Player player) {
        if (!isAvailable()) {
            return false;
        }
        
        try {
            Object user = getCMIUser(player);
            if (user != null) {
                Method isAfk = user.getClass().getMethod("isAfk");
                return (Boolean) isAfk.invoke(user);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error checking AFK status for " + player.getName(), e);
        }
        return false;
    }
    
    /**
     * Get player's total playtime in milliseconds
     */
    public long getTotalPlaytime(Player player) {
        if (!isAvailable()) {
            return 0L;
        }
        
        try {
            Object user = getCMIUser(player);
            if (user != null) {
                Method getTotalPlayTime = user.getClass().getMethod("getTotalPlayTime");
                return (Long) getTotalPlayTime.invoke(user);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error getting playtime for " + player.getName(), e);
        }
        return 0L;
    }
    
    /**
     * Get player's balance from CMI economy (if available)
     */
    public double getBalance(Player player) {
        if (!isAvailable()) {
            return 0.0;
        }
        
        try {
            Object user = getCMIUser(player);
            if (user != null) {
                Method getBalance = user.getClass().getMethod("getBalance");
                return (Double) getBalance.invoke(user);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error getting balance for " + player.getName(), e);
        }
        return 0.0;
    }
    
    /**
     * Add money to player's CMI economy balance
     */
    public boolean addMoney(Player player, double amount) {
        if (!isAvailable() || amount <= 0) {
            return false;
        }
        
        try {
            Object user = getCMIUser(player);
            if (user != null) {
                Method deposit = user.getClass().getMethod("deposit", double.class);
                deposit.invoke(user, amount);
                return true;
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error adding money to " + player.getName(), e);
        }
        return false;
    }
    
    /**
     * Remove money from player's CMI economy balance
     */
    public boolean removeMoney(Player player, double amount) {
        if (!isAvailable() || amount <= 0) {
            return false;
        }
        
        try {
            Object user = getCMIUser(player);
            if (user != null) {
                Method getBalance = user.getClass().getMethod("getBalance");
                double balance = (Double) getBalance.invoke(user);
                if (balance >= amount) {
                    Method withdraw = user.getClass().getMethod("withdraw", double.class);
                    withdraw.invoke(user, amount);
                    return true;
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error removing money from " + player.getName(), e);
        }
        return false;
    }
    
    /**
     * Send formatted message using CMI's messaging system
     */
    public void sendCMIMessage(Player player, String message) {
        if (!isAvailable()) {
            return;
        }
        
        try {
            Object user = getCMIUser(player);
            if (user != null) {
                Method sendMessage = user.getClass().getMethod("sendMessage", String.class);
                sendMessage.invoke(user, message);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error sending CMI message to " + player.getName(), e);
        }
    }
    
    /**
     * Send action bar message using CMI
     */
    public void sendActionBar(Player player, String message) {
        if (!isAvailable()) {
            return;
        }
        
        try {
            // Try to use CMI's RawMessages if available
            Class<?> rawMessagesClass = Class.forName("net.Zrips.CMILib.RawMessages");
            Method actionBarMsg = rawMessagesClass.getMethod("actionBarMsg", Player.class, String.class);
            actionBarMsg.invoke(null, player, message);
        } catch (Exception e) {
            // Fallback to basic action bar
            player.sendActionBar(message);
        }
    }
    
    /**
     * Send title message using CMI
     */
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (!isAvailable()) {
            return;
        }
        
        try {
            // Try to use CMI's RawMessages if available
            Class<?> rawMessagesClass = Class.forName("net.Zrips.CMILib.RawMessages");
            Method titleMsg = rawMessagesClass.getMethod("titleMsg", Player.class, String.class, String.class, int.class, int.class, int.class);
            titleMsg.invoke(null, player, title, subtitle, fadeIn, stay, fadeOut);
        } catch (Exception e) {
            // Fallback to basic title
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }
    
    /**
     * Get player's nick name from CMI
     */
    public String getNickName(Player player) {
        if (!isAvailable()) {
            return player.getName();
        }
        
        try {
            Object user = getCMIUser(player);
            if (user != null) {
                Method getNickName = user.getClass().getMethod("getNickName");
                String nickname = (String) getNickName.invoke(user);
                if (nickname != null && !nickname.isEmpty()) {
                    return nickname;
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error getting nickname for " + player.getName(), e);
        }
        return player.getName();
    }
    
    /**
     * Check if cosmetics should be disabled for this player based on CMI status
     */
    public boolean shouldDisableCosmetics(Player player) {
        if (!isAvailable()) {
            return false;
        }
        
        // Check configuration to see if cosmetics should be disabled
        boolean disableOnVanish = plugin.getConfig().getBoolean("integrations.cmi.disable-on-vanish", true);
        boolean disableOnAFK = plugin.getConfig().getBoolean("integrations.cmi.disable-on-afk", false);
        boolean disableOnGod = plugin.getConfig().getBoolean("integrations.cmi.disable-on-god", false);
        
        if (disableOnVanish && isVanished(player)) {
            return true;
        }
        
        if (disableOnAFK && isAFK(player)) {
            return true;
        }
        
        if (disableOnGod && isInGodMode(player)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Log transaction to CMI's logging system
     */
    public void logTransaction(Player player, String action, String details) {
        if (!isAvailable()) {
            return;
        }
        
        try {
            // Log to CMI's transaction system if available
            plugin.getLogger().info("[CMI Integration] " + player.getName() + " - " + action + ": " + details);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error logging transaction for " + player.getName(), e);
        }
    }
    
    /**
     * Check if player has a specific CMI permission or meta
     */
    public boolean hasCMIPermission(Player player, String permission) {
        if (!isAvailable()) {
            return false;
        }
        
        try {
            Object user = getCMIUser(player);
            return user != null && player.hasPermission(permission);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error checking CMI permission for " + player.getName(), e);
            return false;
        }
    }
    
    /**
     * Get CMI version
     */
    public String getCMIVersion() {
        if (!isAvailable()) {
            return "Unknown";
        }
        
        Plugin cmiPlugin = plugin.getServer().getPluginManager().getPlugin("CMI");
        return cmiPlugin != null ? cmiPlugin.getDescription().getVersion() : "Unknown";
    }
}
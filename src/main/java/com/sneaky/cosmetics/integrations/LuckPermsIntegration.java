package com.sneaky.cosmetics.integrations;

import com.sneaky.cosmetics.SneakyCosmetics;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

/**
 * Integration with LuckPerms for advanced permission management
 * Handles cosmetic permissions and group-based access
 */
public class LuckPermsIntegration {
    
    private final SneakyCosmetics plugin;
    private LuckPerms luckPerms;
    private boolean enabled;
    
    public LuckPermsIntegration(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.enabled = false;
    }
    
    /**
     * Initialize LuckPerms integration
     */
    public boolean initialize() {
        try {
            this.luckPerms = LuckPermsProvider.get();
            this.enabled = true;
            plugin.getLogger().info("Successfully hooked into LuckPerms!");
            return true;
        } catch (IllegalStateException e) {
            plugin.getLogger().warning("LuckPerms not available: " + e.getMessage());
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to initialize LuckPerms integration: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if LuckPerms integration is enabled
     */
    public boolean isEnabled() {
        return enabled && luckPerms != null;
    }
    
    /**
     * Get the LuckPerms API instance
     */
    public LuckPerms getLuckPerms() {
        return luckPerms;
    }
    
    /**
     * Grant a cosmetic permission to a player
     */
    public CompletableFuture<Boolean> grantCosmeticPermission(Player player, String cosmeticId) {
        if (!isEnabled()) {
            return CompletableFuture.completedFuture(false);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = luckPerms.getUserManager().getUser(player.getUniqueId());
                if (user == null) {
                    return false;
                }
                
                String permission = "sneakycosmetics.cosmetic." + cosmeticId.toLowerCase();
                Node node = Node.builder(permission).build();
                
                user.data().add(node);
                luckPerms.getUserManager().saveUser(user);
                
                plugin.getLogger().info("Granted cosmetic permission " + permission + " to " + player.getName());
                return true;
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to grant cosmetic permission to " + player.getName() + ": " + e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * Revoke a cosmetic permission from a player
     */
    public CompletableFuture<Boolean> revokeCosmeticPermission(Player player, String cosmeticId) {
        if (!isEnabled()) {
            return CompletableFuture.completedFuture(false);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = luckPerms.getUserManager().getUser(player.getUniqueId());
                if (user == null) {
                    return false;
                }
                
                String permission = "sneakycosmetics.cosmetic." + cosmeticId.toLowerCase();
                Node node = Node.builder(permission).build();
                
                user.data().remove(node);
                luckPerms.getUserManager().saveUser(user);
                
                plugin.getLogger().info("Revoked cosmetic permission " + permission + " from " + player.getName());
                return true;
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to revoke cosmetic permission from " + player.getName() + ": " + e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * Check if a player has a specific cosmetic permission
     */
    public boolean hasCosmeticPermission(Player player, String cosmeticId) {
        if (!isEnabled()) {
            return false;
        }
        
        try {
            String permission = "sneakycosmetics.cosmetic." + cosmeticId.toLowerCase();
            return player.hasPermission(permission);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to check cosmetic permission for " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get a player's primary group
     */
    public String getPrimaryGroup(Player player) {
        if (!isEnabled()) {
            return "default";
        }
        
        try {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) {
                return "default";
            }
            
            return user.getPrimaryGroup();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get primary group for " + player.getName() + ": " + e.getMessage());
            return "default";
        }
    }
    
    /**
     * Check if a player is in a specific group
     */
    public boolean isInGroup(Player player, String groupName) {
        if (!isEnabled()) {
            return false;
        }
        
        try {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) {
                return false;
            }
            
            return user.getInheritedGroups(user.getQueryOptions()).stream()
                    .anyMatch(group -> group.getName().equalsIgnoreCase(groupName));
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to check group membership for " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Grant temporary cosmetic access based on group
     */
    public CompletableFuture<Boolean> grantTemporaryCosmeticAccess(Player player, String cosmeticId, long durationMillis) {
        if (!isEnabled()) {
            return CompletableFuture.completedFuture(false);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = luckPerms.getUserManager().getUser(player.getUniqueId());
                if (user == null) {
                    return false;
                }
                
                String permission = "sneakycosmetics.cosmetic." + cosmeticId.toLowerCase();
                long expiry = System.currentTimeMillis() + durationMillis;
                
                Node node = Node.builder(permission)
                        .expiry(expiry / 1000) // LuckPerms uses seconds
                        .build();
                
                user.data().add(node);
                luckPerms.getUserManager().saveUser(user);
                
                plugin.getLogger().info("Granted temporary cosmetic permission " + permission + " to " + 
                                      player.getName() + " for " + (durationMillis / 1000) + " seconds");
                return true;
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to grant temporary cosmetic permission to " + 
                                         player.getName() + ": " + e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * Check if a player has VIP access
     */
    public boolean hasVIPAccess(Player player) {
        if (!isEnabled()) {
            return player.hasPermission("sneakycosmetics.vip");
        }
        
        // Check both permission and group membership
        return player.hasPermission("sneakycosmetics.vip") || 
               isInGroup(player, "vip") || 
               isInGroup(player, "premium") ||
               isInGroup(player, "donor");
    }
    
    /**
     * Check if a player has Premium access
     */
    public boolean hasPremiumAccess(Player player) {
        if (!isEnabled()) {
            return player.hasPermission("sneakycosmetics.premium");
        }
        
        // Check both permission and group membership
        return player.hasPermission("sneakycosmetics.premium") || 
               isInGroup(player, "premium") ||
               isInGroup(player, "elite") ||
               isInGroup(player, "ultimate");
    }
    
    /**
     * Check if a player has admin access
     */
    public boolean hasAdminAccess(Player player) {
        if (!isEnabled()) {
            return player.hasPermission("sneakycosmetics.admin");
        }
        
        return player.hasPermission("sneakycosmetics.admin") ||
               player.hasPermission("sneakycosmetics.*") ||
               isInGroup(player, "admin") ||
               isInGroup(player, "owner") ||
               isInGroup(player, "staff");
    }
    
    /**
     * Get cosmetic access level for a player
     */
    public String getCosmeticAccessLevel(Player player) {
        if (hasAdminAccess(player)) {
            return "admin";
        } else if (hasPremiumAccess(player)) {
            return "premium";
        } else if (hasVIPAccess(player)) {
            return "vip";
        } else {
            return "basic";
        }
    }
    
    /**
     * Add a player to a cosmetic group (for organized permissions)
     */
    public CompletableFuture<Boolean> addToCosmeticGroup(Player player, String groupName) {
        if (!isEnabled()) {
            return CompletableFuture.completedFuture(false);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = luckPerms.getUserManager().getUser(player.getUniqueId());
                if (user == null) {
                    return false;
                }
                
                String groupNode = "group." + groupName.toLowerCase();
                Node node = Node.builder(groupNode).build();
                
                user.data().add(node);
                luckPerms.getUserManager().saveUser(user);
                
                plugin.getLogger().info("Added " + player.getName() + " to cosmetic group: " + groupName);
                return true;
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to add " + player.getName() + " to group " + groupName + ": " + e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * Remove a player from a cosmetic group
     */
    public CompletableFuture<Boolean> removeFromCosmeticGroup(Player player, String groupName) {
        if (!isEnabled()) {
            return CompletableFuture.completedFuture(false);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = luckPerms.getUserManager().getUser(player.getUniqueId());
                if (user == null) {
                    return false;
                }
                
                String groupNode = "group." + groupName.toLowerCase();
                Node node = Node.builder(groupNode).build();
                
                user.data().remove(node);
                luckPerms.getUserManager().saveUser(user);
                
                plugin.getLogger().info("Removed " + player.getName() + " from cosmetic group: " + groupName);
                return true;
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to remove " + player.getName() + " from group " + groupName + ": " + e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * Get integration status information
     */
    public String getStatusInfo() {
        if (!isEnabled()) {
            return "LuckPerms integration: Disabled";
        }
        
        StringBuilder info = new StringBuilder();
        info.append("LuckPerms integration: Enabled\n");
        info.append("Version: ").append(luckPerms.getPluginMetadata().getVersion()).append("\n");
        info.append("Platform: ").append(luckPerms.getServerName());
        
        return info.toString();
    }
}
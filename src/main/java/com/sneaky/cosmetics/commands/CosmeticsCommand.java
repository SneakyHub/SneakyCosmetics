package com.sneaky.cosmetics.commands;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import com.sneaky.cosmetics.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Main command handler for /cosmetics command
 * Handles cosmetic management, GUI opening, and admin functions
 */
public class CosmeticsCommand implements CommandExecutor, TabCompleter {
    
    private final SneakyCosmetics plugin;
    private final MessageManager messageManager;
    
    public CosmeticsCommand(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Open GUI for players, show help for console
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("sneakycosmetics.use")) {
                    plugin.getGUIManager().openMainGUI(player);
                } else {
                    messageManager.sendConfigMessage(player, "general.no-permission");
                }
            } else {
                showHelp(sender);
            }
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "help":
                showHelp(sender);
                break;
                
            case "list":
                handleListCommand(sender, args);
                break;
                
            case "toggle":
                handleToggleCommand(sender, args);
                break;
                
            case "give":
                handleGiveCommand(sender, args);
                break;
                
            case "remove":
                handleRemoveCommand(sender, args);
                break;
                
            case "clear":
                handleClearCommand(sender, args);
                break;
                
            case "reload":
                handleReloadCommand(sender);
                break;
                
            case "update":
                handleUpdateCommand(sender);
                break;
                
            case "info":
                handleInfoCommand(sender);
                break;
                
            case "petname":
                handlePetNameCommand(sender, args);
                break;
                
            case "permissions":
                handlePermissionsCommand(sender, args);
                break;
                
            case "daily":
                if (sender instanceof Player) {
                    plugin.getGUIManager().openDailyRewardsGUI((Player) sender);
                } else {
                    messageManager.sendError(sender, "This command can only be used by players.");
                }
                break;
                
            case "achievements":
                if (sender instanceof Player) {
                    plugin.getGUIManager().openAchievementsGUI((Player) sender);
                } else {
                    messageManager.sendError(sender, "This command can only be used by players.");
                }
                break;
                
            case "pets":
                if (sender instanceof Player) {
                    plugin.getGUIManager().openTypeGUI((Player) sender, com.sneaky.cosmetics.cosmetics.CosmeticType.PET);
                } else {
                    messageManager.sendError(sender, "This command can only be used by players.");
                }
                break;
                
            default:
                messageManager.sendError(sender, "Unknown subcommand. Use /cosmetics help for help.");
                break;
        }
        
        return true;
    }
    
    private void showHelp(CommandSender sender) {
        // Enhanced help menu with orange-black gradient theme
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "&#FF8C00╔══════════════════════════════════════════════════╗");
        messageManager.sendInfo(sender, "&#FF8C00║            &#FFD700SneakyCosmetics Commands            &#FF8C00║");
        messageManager.sendInfo(sender, "&#FF8C00╠══════════════════════════════════════════════════╣");
        messageManager.sendInfo(sender, "");
        
        // Player Commands
        messageManager.sendInfo(sender, "&#FF8C00🎮 &#FFD700Player Commands:");
        messageManager.sendInfo(sender, "   &#32CD32/cosmetics &#808080- Open main cosmetics menu");
        messageManager.sendInfo(sender, "   &#32CD32/cosmetics list &#808080- List all your cosmetics");
        messageManager.sendInfo(sender, "   &#32CD32/cosmetics toggle <cosmetic> &#808080- Toggle a cosmetic");
        messageManager.sendInfo(sender, "   &#32CD32/cosmetics daily &#808080- Open daily rewards menu");
        messageManager.sendInfo(sender, "   &#32CD32/cosmetics achievements &#808080- Open achievements menu");
        messageManager.sendInfo(sender, "   &#32CD32/cosmetics pets &#808080- Open pet management menu");
        messageManager.sendInfo(sender, "   &#32CD32/cosmetics petname set <pet> <name> &#808080- Rename your pet");
        messageManager.sendInfo(sender, "");
        
        // Quick Access Commands
        messageManager.sendInfo(sender, "&#FF8C00⚡ &#FFD700Quick Access:");
        messageManager.sendInfo(sender, "   &#32CD32/daily &#808080- Daily rewards system");
        messageManager.sendInfo(sender, "   &#32CD32/achievements &#808080- Achievement system");
        messageManager.sendInfo(sender, "   &#32CD32/pet &#808080- Pet management system");
        messageManager.sendInfo(sender, "   &#32CD32/credits &#808080- Credit management");
        messageManager.sendInfo(sender, "");
        
        // Admin Commands (only show if has permission)
        if (sender.hasPermission("sneakycosmetics.admin")) {
            messageManager.sendInfo(sender, "&#FF8C00👑 &#FFD700Admin Commands:");
            messageManager.sendInfo(sender, "   &#32CD32/cosmetics give <player> <cosmetic> &#808080- Give a cosmetic");
            messageManager.sendInfo(sender, "   &#32CD32/cosmetics remove <player> <cosmetic> &#808080- Remove a cosmetic");
            messageManager.sendInfo(sender, "   &#32CD32/cosmetics clear <player> &#808080- Clear all cosmetics");
            messageManager.sendInfo(sender, "   &#32CD32/cosmetics permissions list &#808080- List all permissions");
            messageManager.sendInfo(sender, "   &#32CD32/cosmetics reload &#808080- Reload configuration");
            messageManager.sendInfo(sender, "   &#32CD32/cosmetics update &#808080- Check for updates");
            messageManager.sendInfo(sender, "   &#32CD32/cosmetics info &#808080- View plugin information");
            messageManager.sendInfo(sender, "");
        }
        
        // Feature Highlights
        messageManager.sendInfo(sender, "&#FF8C00✨ &#FFD700Features:");
        messageManager.sendInfo(sender, "   &#FFA500🎁 Daily Rewards &#808080- Claim credits daily with streak bonuses");
        messageManager.sendInfo(sender, "   &#FFA500🏆 Achievements &#808080- Complete challenges for extra rewards");
        messageManager.sendInfo(sender, "   &#FFA500🐾 Pet System &#808080- Level up pets through interactions");
        messageManager.sendInfo(sender, "   &#FFA500🛒 Credit Shop &#808080- Purchase cosmetics with credits");
        messageManager.sendInfo(sender, "   &#FFA500🎨 7 Cosmetic Types &#808080- Particles, Hats, Pets, Trails & more");
        messageManager.sendInfo(sender, "   &#FFA500🔐 Unique Permissions &#808080- Per-cosmetic permission system");
        messageManager.sendInfo(sender, "");
        
        // Support Information
        messageManager.sendInfo(sender, "&#FF8C00🛠️ &#FFD700Need Help?");
        messageManager.sendInfo(sender, "   &#FFA500Documentation: &#8A2BE2/cosmetics info");
        messageManager.sendInfo(sender, "   &#FFA500Permissions: &#8A2BE2/cosmetics permissions list");
        if (sender.hasPermission("sneakycosmetics.admin")) {
            messageManager.sendInfo(sender, "   &#FFA500Discord: &#8A2BE2https://discord.gg/sneakycosmetics");
            messageManager.sendInfo(sender, "   &#FFA500GitHub: &#8A2BE2https://github.com/sneaky/cosmetics");
        }
        
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "&#FF8C00╚══════════════════════════════════════════════════╝");
        messageManager.sendInfo(sender, "");
    }
    
    private void handleListCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        // List available cosmetics
        Player target = null;
        if (sender instanceof Player) {
            target = (Player) sender;
        }
        
        if (args.length > 1 && sender.hasPermission("sneakycosmetics.admin")) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                Map<String, String> placeholders = messageManager.createPlaceholders();
                placeholders.put("player", args[1]);
                messageManager.sendConfigMessage(sender, "general.player-not-found", placeholders);
                return;
            }
        }
        
        if (target == null) {
            messageManager.sendError(sender, "This command can only be used by players or specify a target player.");
            return;
        }
        
        // Show list of cosmetics with ownership status
        messageManager.sendInfo(sender, "§6=== Cosmetics for " + target.getName() + " ===");
        
        for (com.sneaky.cosmetics.cosmetics.CosmeticType type : com.sneaky.cosmetics.cosmetics.CosmeticType.values()) {
            java.util.List<com.sneaky.cosmetics.cosmetics.Cosmetic> typeCosmetics = plugin.getCosmeticManager().getCosmeticsByType(type);
            if (!typeCosmetics.isEmpty()) {
                messageManager.sendInfo(sender, "");
                messageManager.sendInfo(sender, type.getColorCode() + "§l" + type.getDisplayName() + ":");
                
                for (com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic : typeCosmetics) {
                    boolean hasCosmetic = plugin.getCosmeticManager().hasCosmetic(target, cosmetic.getId());
                    boolean hasUniquePermission = cosmetic.hasUniquePermission(target);
                    boolean isActive = plugin.getCosmeticManager().isCosmeticActive(target, cosmetic.getId());
                    
                    String status;
                    if (isActive) {
                        status = "§a[ACTIVE]";
                    } else if (hasCosmetic || hasUniquePermission) {
                        status = hasUniquePermission ? "§b[PERM]" : "§e[OWNED]";
                    } else {
                        status = "§c[LOCKED]";
                    }
                    
                    String price = cosmetic.getPrice() == 0 ? "§aFree" : "§e" + cosmetic.getPrice() + " credits";
                    String permission = "§7(" + cosmetic.getUniquePermission() + ")";
                    
                    messageManager.sendInfo(sender, "  " + status + " §f" + cosmetic.getDisplayName() + " §7(" + price + ") " + permission);
                }
            } else {
                messageManager.sendInfo(sender, type.getColorCode() + type.getDisplayName() + ": §7No cosmetics available");
            }
        }
    }
    
    private void handleToggleCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            messageManager.sendError(sender, "This command can only be used by players.");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(player, "general.no-permission");
            return;
        }
        
        if (args.length < 2) {
            messageManager.sendError(player, "Usage: /cosmetics toggle <cosmetic>");
            return;
        }
        
        String cosmeticId = args[1];
        
        // Check if cosmetic exists
        com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic = plugin.getCosmeticManager().getCosmetic(cosmeticId);
        if (cosmetic == null) {
            messageManager.sendError(player, "Cosmetic not found: " + cosmeticId);
            return;
        }
        
        // Check if player can use the cosmetic (includes permission checks)
        if (!cosmetic.canPlayerUse(player)) {
            messageManager.sendError(player, "You cannot use this cosmetic: " + cosmetic.getAccessDeniedReason(player));
            return;
        }
        
        // Check if player owns the cosmetic (unless it's free, has free access, or unique permission)
        boolean hasFreeAccess = player.hasPermission("sneakycosmetics.free");
        boolean hasUniquePermission = cosmetic.hasUniquePermission(player);
        
        if (!cosmetic.isFree() && !hasFreeAccess && !hasUniquePermission && !plugin.getCosmeticManager().hasCosmetic(player, cosmeticId)) {
            messageManager.sendError(player, "You don't own this cosmetic! Purchase it first for " + cosmetic.getPrice() + " credits, or get permission: " + cosmetic.getUniquePermission());
            return;
        }
        
        // Toggle the cosmetic
        boolean success = plugin.getCosmeticManager().toggleCosmetic(player, cosmeticId);
        if (success) {
            boolean isActive = plugin.getCosmeticManager().isCosmeticActive(player, cosmeticId);
            if (isActive) {
                messageManager.sendSuccess(player, "Activated cosmetic: " + cosmetic.getDisplayName());
            } else {
                messageManager.sendSuccess(player, "Deactivated cosmetic: " + cosmetic.getDisplayName());
            }
        } else {
            messageManager.sendError(player, "Failed to toggle cosmetic: " + cosmetic.getDisplayName());
        }
    }
    
    private void handleGiveCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("sneakycosmetics.admin")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        if (args.length < 3) {
            messageManager.sendError(sender, "Usage: /cosmetics give <player> <cosmetic>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            Map<String, String> placeholders = messageManager.createPlaceholders();
            placeholders.put("player", args[1]);
            messageManager.sendConfigMessage(sender, "general.player-not-found", placeholders);
            return;
        }
        
        String cosmeticId = args[2];
        
        // Check if cosmetic exists
        com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic = plugin.getCosmeticManager().getCosmetic(cosmeticId);
        if (cosmetic == null) {
            messageManager.sendError(sender, "Cosmetic not found: " + cosmeticId);
            return;
        }
        
        // Check if player already owns the cosmetic
        if (plugin.getCosmeticManager().hasCosmetic(target, cosmeticId)) {
            messageManager.sendError(sender, target.getName() + " already owns this cosmetic!");
            return;
        }
        
        // Give the cosmetic
        plugin.getCosmeticManager().giveCosmetic(target, cosmeticId);
        
        // Send success messages
        messageManager.sendSuccess(sender, "Gave " + cosmetic.getDisplayName() + " to " + target.getName());
        messageManager.sendSuccess(target, "You received a new cosmetic: " + cosmetic.getDisplayName());
    }
    
    private void handleRemoveCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("sneakycosmetics.admin")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        if (args.length < 3) {
            messageManager.sendError(sender, "Usage: /cosmetics remove <player> <cosmetic>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            Map<String, String> placeholders = messageManager.createPlaceholders();
            placeholders.put("player", args[1]);
            messageManager.sendConfigMessage(sender, "general.player-not-found", placeholders);
            return;
        }
        
        String cosmeticId = args[2];
        
        // Check if cosmetic exists
        com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic = plugin.getCosmeticManager().getCosmetic(cosmeticId);
        if (cosmetic == null) {
            messageManager.sendError(sender, "Cosmetic not found: " + cosmeticId);
            return;
        }
        
        // Check if player owns the cosmetic
        if (!plugin.getCosmeticManager().hasCosmetic(target, cosmeticId)) {
            messageManager.sendError(sender, target.getName() + " doesn't own this cosmetic!");
            return;
        }
        
        // Deactivate the cosmetic if it's active
        if (plugin.getCosmeticManager().isCosmeticActive(target, cosmeticId)) {
            plugin.getCosmeticManager().deactivateCosmetic(target, cosmeticId);
        }
        
        // Remove the cosmetic from database
        plugin.getDatabaseManager().removeCosmetic(target.getUniqueId(), cosmeticId);
        
        // Send success messages
        messageManager.sendSuccess(sender, "Removed " + cosmetic.getDisplayName() + " from " + target.getName());
        messageManager.sendInfo(target, "Cosmetic removed: " + cosmetic.getDisplayName());
    }
    
    private void handleClearCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("sneakycosmetics.admin")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        if (args.length < 2) {
            messageManager.sendError(sender, "Usage: /cosmetics clear <player>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            Map<String, String> placeholders = messageManager.createPlaceholders();
            placeholders.put("player", args[1]);
            messageManager.sendConfigMessage(sender, "general.player-not-found", placeholders);
            return;
        }
        
        // Clear all active cosmetics from player
        plugin.getCosmeticManager().clearAllCosmetics(target);
        
        // Send success messages
        messageManager.sendSuccess(sender, "Cleared all active cosmetics from " + target.getName());
        messageManager.sendInfo(target, "All your active cosmetics have been cleared.");
    }
    
    private void handleReloadCommand(CommandSender sender) {
        if (!sender.hasPermission("sneakycosmetics.admin")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        try {
            plugin.reload();
            messageManager.sendConfigMessage(sender, "general.reload-success");
        } catch (Exception e) {
            messageManager.sendConfigMessage(sender, "general.reload-error");
            plugin.getLogger().severe("Error reloading plugin: " + e.getMessage());
        }
    }
    
    private void handleUpdateCommand(CommandSender sender) {
        if (!sender.hasPermission("sneakycosmetics.admin")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        if (plugin.getUpdateChecker() == null) {
            messageManager.sendError(sender, "Update checker is disabled!");
            return;
        }
        
        if (sender instanceof Player) {
            plugin.getUpdateChecker().forceCheckForUpdates((Player) sender);
        } else {
            messageManager.sendInfo(sender, "Checking for updates...");
            plugin.getUpdateChecker().checkForUpdates().thenAccept(hasUpdate -> {
                if (hasUpdate) {
                    messageManager.sendInfo(sender, "Update available! Current: " + 
                            plugin.getUpdateChecker().getCurrentVersion() + " -> Latest: " + 
                            plugin.getUpdateChecker().getLatestVersion());
                } else {
                    messageManager.sendInfo(sender, "No updates available.");
                }
            });
        }
    }
    
    private void handleInfoCommand(CommandSender sender) {
        if (!sender.hasPermission("sneakycosmetics.admin")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        // Get plugin information
        String version = plugin.getDescription().getVersion();
        String serverType = plugin.getSchedulerAdapter().isFolia() ? "Folia" : "Paper/Spigot";
        int totalCosmetics = plugin.getCosmeticManager().getTotalCosmetics();
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int maxPlayers = Bukkit.getMaxPlayers();
        
        // Count cosmetics by type
        int particleCount = plugin.getCosmeticManager().getCosmeticCountByType(CosmeticType.PARTICLE);
        int hatCount = plugin.getCosmeticManager().getCosmeticCountByType(CosmeticType.HAT);
        int petCount = plugin.getCosmeticManager().getCosmeticCountByType(CosmeticType.PET);
        int trailCount = plugin.getCosmeticManager().getCosmeticCountByType(CosmeticType.TRAIL);
        int gadgetCount = plugin.getCosmeticManager().getCosmeticCountByType(CosmeticType.GADGET);
        int wingCount = plugin.getCosmeticManager().getCosmeticCountByType(CosmeticType.WINGS);
        int auraCount = plugin.getCosmeticManager().getCosmeticCountByType(CosmeticType.AURA);
        
        // Display enhanced info with orange-black gradient theme
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "&#FF8C00╔══════════════════════════════════════════════════╗");
        messageManager.sendInfo(sender, "&#FF8C00║              &#FFD700SneakyCosmetics Info              &#FF8C00║");
        messageManager.sendInfo(sender, "&#FF8C00╠══════════════════════════════════════════════════╣");
        messageManager.sendInfo(sender, "");
        
        // Core Information
        messageManager.sendInfo(sender, "&#FF8C00📦 &#FFD700Core Information:");
        messageManager.sendInfo(sender, "   &#FFA500Version: &#FFFFFF" + version);
        messageManager.sendInfo(sender, "   &#FFA500Server: &#FFFFFF" + serverType);
        messageManager.sendInfo(sender, "   &#FFA500Players: &#FFD700" + onlinePlayers + "&#808080/&#FFD700" + maxPlayers);
        messageManager.sendInfo(sender, "   &#FFA500Total Cosmetics: &#FFD700" + totalCosmetics);
        messageManager.sendInfo(sender, "");
        
        // Cosmetic Breakdown
        messageManager.sendInfo(sender, "&#FF8C00🎨 &#FFD700Cosmetic Breakdown:");
        messageManager.sendInfo(sender, "   &#FF6347✨ Particles: &#FFD700" + particleCount);
        messageManager.sendInfo(sender, "   &#FFD700👑 Hats: &#FFD700" + hatCount);
        messageManager.sendInfo(sender, "   &#FF8C00🐾 Pets: &#FFD700" + petCount);
        messageManager.sendInfo(sender, "   &#FF4500💫 Trails: &#FFD700" + trailCount);
        messageManager.sendInfo(sender, "   &#FF6347🎮 Gadgets: &#FFD700" + gadgetCount);
        messageManager.sendInfo(sender, "   &#FFD700🕊️ Wings: &#FFD700" + wingCount);
        messageManager.sendInfo(sender, "   &#FF8C00🌟 Auras: &#FFD700" + auraCount);
        messageManager.sendInfo(sender, "");
        
        // Integration Status
        messageManager.sendInfo(sender, "&#FF8C00🔗 &#FFD700Integration Status:");
        
        // Vault Integration
        if (plugin.getVaultIntegration() != null && plugin.getVaultIntegration().isEnabled()) {
            String economyName = plugin.getVaultIntegration().getEconomy().getName();
            messageManager.sendInfo(sender, "   &#32CD32✓ &#FFA500Vault: &#FFFFFF" + economyName);
        } else {
            messageManager.sendInfo(sender, "   &#FF4500✗ &#FFA500Vault: &#808080Disabled");
        }
        
        // LuckPerms Integration
        if (plugin.getLuckPermsIntegration() != null && plugin.getLuckPermsIntegration().isEnabled()) {
            messageManager.sendInfo(sender, "   &#32CD32✓ &#FFA500LuckPerms: &#FFFFFF Enabled");
        } else {
            messageManager.sendInfo(sender, "   &#FF4500✗ &#FFA500LuckPerms: &#808080Disabled");
        }
        
        // EssentialsX Integration
        if (plugin.getEssentialsXIntegration() != null && plugin.getEssentialsXIntegration().isEnabled()) {
            messageManager.sendInfo(sender, "   &#32CD32✓ &#FFA500EssentialsX: &#FFFFFF Enabled");
        } else {
            messageManager.sendInfo(sender, "   &#FF4500✗ &#FFA500EssentialsX: &#808080Disabled");
        }
        
        // PlaceholderAPI Integration
        if (plugin.getPlaceholderAPIIntegration() != null) {
            messageManager.sendInfo(sender, "   &#32CD32✓ &#FFA500PlaceholderAPI: &#FFFFFF Enabled");
        } else {
            messageManager.sendInfo(sender, "   &#FF4500✗ &#FFA500PlaceholderAPI: &#808080Disabled");
        }
        
        messageManager.sendInfo(sender, "");
        
        // Database Information
        messageManager.sendInfo(sender, "&#FF8C00💾 &#FFD700Database Information:");
        messageManager.sendInfo(sender, "   &#FFA500Type: &#FFFFFF" + plugin.getDatabaseManager().getDatabaseConfig().getDatabaseType().toUpperCase());
        messageManager.sendInfo(sender, "   &#32CD32✓ &#FFA500Status: &#FFFFFF" + plugin.getDatabaseManager().getConnectionInfo());
        
        // Performance Statistics
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "&#FF8C00⚡ &#FFD700Performance Statistics:");
        
        try {
            int totalAchievements = plugin.getAchievementManager().getAllAchievements().size();
            messageManager.sendInfo(sender, "   &#FFA500Total Achievements: &#FFD700" + totalAchievements);
        } catch (Exception e) {
            messageManager.sendInfo(sender, "   &#FFA500Total Achievements: &#808080N/A");
        }
        
        try {
            long totalCreditsInCirculation = plugin.getStatisticsManager().getTotalCreditsEarned();
            messageManager.sendInfo(sender, "   &#FFA500Credits in Circulation: &#FFD700" + totalCreditsInCirculation);
        } catch (Exception e) {
            messageManager.sendInfo(sender, "   &#FFA500Credits in Circulation: &#808080N/A");
        }
        
        messageManager.sendInfo(sender, "");
        
        // Support Information
        messageManager.sendInfo(sender, "&#FF8C00🛠️ &#FFD700Support & Links:");
        messageManager.sendInfo(sender, "   &#FFA500Documentation: &#8A2BE2https://docs.sneakycosmetics.com");
        messageManager.sendInfo(sender, "   &#FFA500Discord: &#8A2BE2https://discord.gg/sneakycosmetics");
        messageManager.sendInfo(sender, "   &#FFA500GitHub: &#8A2BE2https://github.com/sneaky/cosmetics");
        messageManager.sendInfo(sender, "   &#FFA500Bug Reports: &#8A2BE2https://github.com/sneaky/cosmetics/issues");
        
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "&#FF8C00╚══════════════════════════════════════════════════╝");
        messageManager.sendInfo(sender, "");
    }
    
    private void handlePetNameCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            messageManager.sendError(sender, "This command can only be used by players.");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(player, "general.no-permission");
            return;
        }
        
        if (args.length < 2) {
            messageManager.sendError(player, "Usage: /cosmetics petname <set|get> [pet_id] [name]");
            messageManager.sendInfo(player, "  set - Set a custom name for your pet");
            messageManager.sendInfo(player, "  get - Get the current name of your pet");
            return;
        }
        
        String action = args[1].toLowerCase();
        
        if (action.equals("set")) {
            if (args.length < 4) {
                messageManager.sendError(player, "Usage: /cosmetics petname set <pet_id> <name>");
                messageManager.sendInfo(player, "Example: /cosmetics petname set pet_wolf \"Fluffy\"");
                return;
            }
            
            String petId = args[2];
            
            // Check if cosmetic exists and is a pet
            com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic = plugin.getCosmeticManager().getCosmetic(petId);
            if (cosmetic == null) {
                messageManager.sendError(player, "Pet not found: " + petId);
                return;
            }
            
            if (cosmetic.getType() != com.sneaky.cosmetics.cosmetics.CosmeticType.PET) {
                messageManager.sendError(player, "This cosmetic is not a pet!");
                return;
            }
            
            // Check if player owns the pet
            if (!plugin.getCosmeticManager().hasCosmetic(player, petId)) {
                messageManager.sendError(player, "You don't own this pet!");
                return;
            }
            
            // Combine all remaining arguments as the pet name
            StringBuilder nameBuilder = new StringBuilder();
            for (int i = 3; i < args.length; i++) {
                if (i > 3) nameBuilder.append(" ");
                nameBuilder.append(args[i]);
            }
            String petName = nameBuilder.toString();
            
            // Remove quotes if present
            if (petName.startsWith("\"") && petName.endsWith("\"")) {
                petName = petName.substring(1, petName.length() - 1);
            }
            
            // Validate name length
            if (petName.length() > 32) {
                messageManager.sendError(player, "Pet name too long! Maximum 32 characters.");
                return;
            }
            
            if (petName.trim().isEmpty()) {
                messageManager.sendError(player, "Pet name cannot be empty!");
                return;
            }
            
            // Set the custom name
            plugin.getDatabaseManager().setPetCustomName(player.getUniqueId(), petId, petName);
            
            // Update the pet's name if it's currently active
            if (plugin.getCosmeticManager().isCosmeticActive(player, petId)) {
                com.sneaky.cosmetics.cosmetics.pets.PetCosmetic petCosmetic = (com.sneaky.cosmetics.cosmetics.pets.PetCosmetic) cosmetic;
                petCosmetic.updatePetName(player, petName);
            }
            
            messageManager.sendSuccess(player, "Set pet name to: §e" + petName);
            
        } else if (action.equals("get")) {
            if (args.length < 3) {
                messageManager.sendError(player, "Usage: /cosmetics petname get <pet_id>");
                return;
            }
            
            String petId = args[2];
            
            // Check if cosmetic exists and is a pet
            com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic = plugin.getCosmeticManager().getCosmetic(petId);
            if (cosmetic == null) {
                messageManager.sendError(player, "Pet not found: " + petId);
                return;
            }
            
            if (cosmetic.getType() != com.sneaky.cosmetics.cosmetics.CosmeticType.PET) {
                messageManager.sendError(player, "This cosmetic is not a pet!");
                return;
            }
            
            // Check if player owns the pet
            if (!plugin.getCosmeticManager().hasCosmetic(player, petId)) {
                messageManager.sendError(player, "You don't own this pet!");
                return;
            }
            
            // Get the custom name
            String customName = plugin.getDatabaseManager().getPetCustomName(player.getUniqueId(), petId);
            if (customName != null && !customName.isEmpty()) {
                messageManager.sendInfo(player, "Pet name: §e" + customName);
            } else {
                messageManager.sendInfo(player, "This pet has no custom name. Default: §e" + cosmetic.getDisplayName());
            }
            
        } else {
            messageManager.sendError(player, "Unknown action: " + action);
            messageManager.sendInfo(player, "Use 'set' or 'get'");
        }
    }
    
    private void handlePermissionsCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("sneakycosmetics.admin")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        if (args.length < 2) {
            messageManager.sendError(sender, "Usage: /cosmetics permissions <list|cosmetic>");
            messageManager.sendInfo(sender, "  list - List all cosmetic permissions");
            messageManager.sendInfo(sender, "  cosmetic <id> - Show permissions for specific cosmetic");
            return;
        }
        
        String action = args[1].toLowerCase();
        
        if (action.equals("list")) {
            messageManager.sendInfo(sender, "§6=== All Cosmetic Permissions ===");
            messageManager.sendInfo(sender, "§7Base permissions:");
            messageManager.sendInfo(sender, "  §esneakycosmetics.use §7- Basic cosmetics access");
            messageManager.sendInfo(sender, "  §esneakycosmetics.free §7- Free access to all cosmetics");
            messageManager.sendInfo(sender, "  §esneakycosmetics.premium §7- Premium cosmetics access");
            messageManager.sendInfo(sender, "  §esneakycosmetics.vip §7- VIP cosmetics access");
            messageManager.sendInfo(sender, "  §esneakycosmetics.admin §7- Admin commands");
            messageManager.sendInfo(sender, "");
            
            for (com.sneaky.cosmetics.cosmetics.CosmeticType type : com.sneaky.cosmetics.cosmetics.CosmeticType.values()) {
                java.util.List<com.sneaky.cosmetics.cosmetics.Cosmetic> typeCosmetics = plugin.getCosmeticManager().getCosmeticsByType(type);
                if (!typeCosmetics.isEmpty()) {
                    messageManager.sendInfo(sender, type.getColorCode() + "§l" + type.getDisplayName() + " Permissions:");
                    
                    for (com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic : typeCosmetics) {
                        messageManager.sendInfo(sender, "  §e" + cosmetic.getUniquePermission() + " §7- " + cosmetic.getDisplayName());
                    }
                    messageManager.sendInfo(sender, "");
                }
            }
            
        } else if (action.equals("cosmetic")) {
            if (args.length < 3) {
                messageManager.sendError(sender, "Usage: /cosmetics permissions cosmetic <cosmetic_id>");
                return;
            }
            
            String cosmeticId = args[2];
            com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic = plugin.getCosmeticManager().getCosmetic(cosmeticId);
            
            if (cosmetic == null) {
                messageManager.sendError(sender, "Cosmetic not found: " + cosmeticId);
                return;
            }
            
            messageManager.sendInfo(sender, "§6=== Permissions for " + cosmetic.getDisplayName() + " ===");
            messageManager.sendInfo(sender, "§7Unique Permission: §e" + cosmetic.getUniquePermission());
            
            java.util.List<String> allPerms = cosmetic.getAllPermissions();
            messageManager.sendInfo(sender, "§7All Applicable Permissions:");
            for (String perm : allPerms) {
                messageManager.sendInfo(sender, "  §e" + perm);
            }
            
            if (cosmetic.getPermission() != null && !cosmetic.getPermission().isEmpty()) {
                messageManager.sendInfo(sender, "§7Legacy Permission: §e" + cosmetic.getPermission());
            }
            
        } else {
            messageManager.sendError(sender, "Unknown action: " + action);
            messageManager.sendInfo(sender, "Use 'list' or 'cosmetic'");
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("help", "list", "toggle", "give", "remove", "clear", "reload", "update", "info", "petname", "permissions", "daily", "achievements", "pets");
            String partial = args[0].toLowerCase();
            
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(partial)) {
                    // Check permissions
                    if (subCommand.equals("give") || subCommand.equals("remove") || subCommand.equals("clear") || 
                        subCommand.equals("reload") || subCommand.equals("update") || subCommand.equals("info") || subCommand.equals("permissions")) {
                        if (sender.hasPermission("sneakycosmetics.admin")) {
                            completions.add(subCommand);
                        }
                    } else if (sender.hasPermission("sneakycosmetics.use")) {
                        completions.add(subCommand);
                    }
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            if (subCommand.equals("give") || subCommand.equals("remove") || subCommand.equals("clear") || subCommand.equals("list")) {
                if (sender.hasPermission("sneakycosmetics.admin")) {
                    // Complete with online player names
                    String partial = args[1].toLowerCase();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getName().toLowerCase().startsWith(partial)) {
                            completions.add(player.getName());
                        }
                    }
                }
            } else if (subCommand.equals("toggle")) {
                if (sender.hasPermission("sneakycosmetics.use")) {
                    // Complete with actual cosmetic IDs
                    String partial = args[1].toLowerCase();
                    for (com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
                        if (cosmetic.getId().toLowerCase().startsWith(partial)) {
                            completions.add(cosmetic.getId());
                        }
                    }
                }
            } else if (subCommand.equals("petname")) {
                if (sender.hasPermission("sneakycosmetics.use")) {
                    // Complete with set/get actions
                    String partial = args[1].toLowerCase();
                    if ("set".startsWith(partial)) completions.add("set");
                    if ("get".startsWith(partial)) completions.add("get");
                }
            } else if (subCommand.equals("permissions")) {
                if (sender.hasPermission("sneakycosmetics.admin")) {
                    // Complete with list/cosmetic actions
                    String partial = args[1].toLowerCase();
                    if ("list".startsWith(partial)) completions.add("list");
                    if ("cosmetic".startsWith(partial)) completions.add("cosmetic");
                }
            }
        } else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            
            if ((subCommand.equals("give") || subCommand.equals("remove")) && sender.hasPermission("sneakycosmetics.admin")) {
                // Complete with actual cosmetic IDs
                String partial = args[2].toLowerCase();
                for (com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
                    if (cosmetic.getId().toLowerCase().startsWith(partial)) {
                        completions.add(cosmetic.getId());
                    }
                }
            } else if (subCommand.equals("petname") && sender.hasPermission("sneakycosmetics.use")) {
                String action = args[1].toLowerCase();
                if (action.equals("set") || action.equals("get")) {
                    // Complete with pet IDs only
                    String partial = args[2].toLowerCase();
                    for (com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
                        if (cosmetic.getType() == com.sneaky.cosmetics.cosmetics.CosmeticType.PET && 
                            cosmetic.getId().toLowerCase().startsWith(partial)) {
                            completions.add(cosmetic.getId());
                        }
                    }
                }
            } else if (subCommand.equals("permissions") && sender.hasPermission("sneakycosmetics.admin")) {
                String action = args[1].toLowerCase();
                if (action.equals("cosmetic")) {
                    // Complete with cosmetic IDs
                    String partial = args[2].toLowerCase();
                    for (com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
                        if (cosmetic.getId().toLowerCase().startsWith(partial)) {
                            completions.add(cosmetic.getId());
                        }
                    }
                }
            }
        }
        
        return completions;
    }
}
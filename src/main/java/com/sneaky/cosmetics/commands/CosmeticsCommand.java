package com.sneaky.cosmetics.commands;

import com.sneaky.cosmetics.SneakyCosmetics;
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
                
            default:
                messageManager.sendError(sender, "Unknown subcommand. Use /cosmetics help for help.");
                break;
        }
        
        return true;
    }
    
    private void showHelp(CommandSender sender) {
        messageManager.sendConfigMessages(sender, "help.cosmetics");
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
                    boolean isActive = plugin.getCosmeticManager().isCosmeticActive(target, cosmetic.getId());
                    
                    String status = isActive ? "§a[ACTIVE]" : hasCosmetic ? "§e[OWNED]" : "§c[LOCKED]";
                    String price = cosmetic.getPrice() == 0 ? "§aFree" : "§e" + cosmetic.getPrice() + " credits";
                    
                    messageManager.sendInfo(sender, "  " + status + " §f" + cosmetic.getDisplayName() + " §7(" + price + ")");
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
        
        // Check if player can access the cosmetic
        if (!cosmetic.canPlayerAccess(player)) {
            messageManager.sendError(player, "You cannot access this cosmetic: " + cosmetic.getAccessDeniedReason(player));
            return;
        }
        
        // Check if player owns the cosmetic (unless it's free)
        if (!cosmetic.isFree() && !plugin.getCosmeticManager().hasCosmetic(player, cosmeticId)) {
            messageManager.sendError(player, "You don't own this cosmetic! Purchase it first for " + cosmetic.getPrice() + " credits.");
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
        
        messageManager.sendInfo(sender, "=== SneakyCosmetics Info ===");
        messageManager.sendInfo(sender, "Version: " + plugin.getDescription().getVersion());
        messageManager.sendInfo(sender, "Server: " + (plugin.getSchedulerAdapter().isFolia() ? "Folia" : "Paper/Spigot"));
        messageManager.sendInfo(sender, "Total Cosmetics: " + plugin.getCosmeticManager().getTotalCosmetics());
        messageManager.sendInfo(sender, "Online Players: " + Bukkit.getOnlinePlayers().size());
        
        if (plugin.getVaultIntegration() != null && plugin.getVaultIntegration().isEnabled()) {
            messageManager.sendInfo(sender, "Vault: Enabled (" + plugin.getVaultIntegration().getEconomy().getName() + ")");
        } else {
            messageManager.sendInfo(sender, "Vault: Disabled");
        }
        
        if (plugin.getLuckPermsIntegration() != null && plugin.getLuckPermsIntegration().isEnabled()) {
            messageManager.sendInfo(sender, "LuckPerms: Enabled");
        } else {
            messageManager.sendInfo(sender, "LuckPerms: Disabled");
        }
        
        if (plugin.getEssentialsXIntegration() != null && plugin.getEssentialsXIntegration().isEnabled()) {
            messageManager.sendInfo(sender, "EssentialsX: Enabled");
        } else {
            messageManager.sendInfo(sender, "EssentialsX: Disabled");
        }
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
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("help", "list", "toggle", "give", "remove", "clear", "reload", "update", "info", "petname");
            String partial = args[0].toLowerCase();
            
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(partial)) {
                    // Check permissions
                    if (subCommand.equals("give") || subCommand.equals("remove") || subCommand.equals("clear") || 
                        subCommand.equals("reload") || subCommand.equals("update") || subCommand.equals("info")) {
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
            }
        }
        
        return completions;
    }
}
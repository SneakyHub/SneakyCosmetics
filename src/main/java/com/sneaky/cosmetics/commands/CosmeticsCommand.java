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
        
        // TODO: Show list of cosmetics with ownership status
        messageManager.sendInfo(sender, "Cosmetic listing feature coming soon!");
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
        
        // TODO: Toggle cosmetic
        messageManager.sendInfo(player, "Cosmetic toggle feature coming soon for: " + cosmeticId);
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
        
        // TODO: Give cosmetic to player
        messageManager.sendInfo(sender, "Give cosmetic feature coming soon!");
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
        
        // TODO: Remove cosmetic from player
        messageManager.sendInfo(sender, "Remove cosmetic feature coming soon!");
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
        
        // TODO: Clear all active cosmetics from player
        messageManager.sendInfo(sender, "Clear cosmetics feature coming soon!");
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
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("help", "list", "toggle", "give", "remove", "clear", "reload", "update", "info");
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
                    // TODO: Complete with cosmetic IDs
                    completions.add("particle_hearts");
                    completions.add("hat_diamond");
                    completions.add("pet_wolf");
                }
            }
        } else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            
            if ((subCommand.equals("give") || subCommand.equals("remove")) && sender.hasPermission("sneakycosmetics.admin")) {
                // TODO: Complete with cosmetic IDs
                completions.add("particle_hearts");
                completions.add("hat_diamond");
                completions.add("pet_wolf");
            }
        }
        
        return completions;
    }
}
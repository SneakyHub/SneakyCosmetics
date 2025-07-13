package com.sneaky.cosmetics.commands;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.managers.CreditManager;
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
 * Command handler for /credits command
 * Handles credit management, purchases, and admin functions
 */
public class CreditsCommand implements CommandExecutor, TabCompleter {
    
    private final SneakyCosmetics plugin;
    private final MessageManager messageManager;
    private final CreditManager creditManager;
    
    public CreditsCommand(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
        this.creditManager = plugin.getCreditManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Show balance for players, help for console
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("sneakycosmetics.credits")) {
                    showBalance(player, player);
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
                
            case "balance":
            case "bal":
                handleBalanceCommand(sender, args);
                break;
                
            case "buy":
            case "purchase":
                handleBuyCommand(sender, args);
                break;
                
            case "give":
            case "add":
                handleGiveCommand(sender, args);
                break;
                
            case "remove":
            case "take":
                handleRemoveCommand(sender, args);
                break;
                
            case "set":
                handleSetCommand(sender, args);
                break;
                
            case "daily":
                handleDailyCommand(sender);
                break;
                
            case "top":
                handleTopCommand(sender);
                break;
                
            default:
                messageManager.sendError(sender, "Unknown subcommand. Use /credits help for help.");
                break;
        }
        
        return true;
    }
    
    private void showHelp(CommandSender sender) {
        messageManager.sendConfigMessages(sender, "help.credits");
    }
    
    private void showBalance(CommandSender sender, Player target) {
        creditManager.getCredits(target.getUniqueId()).thenAccept(credits -> {
            plugin.getSchedulerAdapter().runTask(() -> {
                Map<String, String> placeholders = messageManager.createPlaceholders(target);
                placeholders.put("credits", String.valueOf(credits));
                
                if (sender.equals(target)) {
                    messageManager.sendConfigMessage(sender, "credits.balance", placeholders);
                } else {
                    messageManager.sendConfigMessage(sender, "credits.balance-other", placeholders);
                }
            });
        });
    }
    
    private void handleBalanceCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("sneakycosmetics.credits")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        Player target = null;
        
        if (args.length > 1) {
            // Check balance of another player (admin only)
            if (!sender.hasPermission("sneakycosmetics.admin")) {
                messageManager.sendConfigMessage(sender, "general.no-permission");
                return;
            }
            
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                Map<String, String> placeholders = messageManager.createPlaceholders();
                placeholders.put("player", args[1]);
                messageManager.sendConfigMessage(sender, "general.player-not-found", placeholders);
                return;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            messageManager.sendError(sender, "Console must specify a player name.");
            return;
        }
        
        showBalance(sender, target);
    }
    
    private void handleBuyCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            messageManager.sendError(sender, "This command can only be used by players.");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("sneakycosmetics.credits")) {
            messageManager.sendConfigMessage(player, "general.no-permission");
            return;
        }
        
        if (args.length < 2) {
            messageManager.sendError(player, "Usage: /credits buy <amount>");
            return;
        }
        
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                messageManager.sendConfigMessage(player, "general.invalid-amount");
                return;
            }
        } catch (NumberFormatException e) {
            messageManager.sendConfigMessage(player, "general.invalid-amount");
            return;
        }
        
        // Purchase credits using Vault
        creditManager.purchaseCredits(player, amount);
    }
    
    private void handleGiveCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("sneakycosmetics.admin")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        if (args.length < 3) {
            messageManager.sendError(sender, "Usage: /credits give <player> <amount>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            Map<String, String> placeholders = messageManager.createPlaceholders();
            placeholders.put("player", args[1]);
            messageManager.sendConfigMessage(sender, "general.player-not-found", placeholders);
            return;
        }
        
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount <= 0) {
                messageManager.sendConfigMessage(sender, "general.invalid-amount");
                return;
            }
        } catch (NumberFormatException e) {
            messageManager.sendConfigMessage(sender, "general.invalid-amount");
            return;
        }
        
        creditManager.addCredits(target.getUniqueId(), amount).thenAccept(success -> {
            plugin.getSchedulerAdapter().runTask(() -> {
                if (success) {
                    Map<String, String> placeholders = messageManager.createPlaceholders(target);
                    placeholders.put("amount", String.valueOf(amount));
                    
                    messageManager.sendConfigMessage(sender, "credits.given-other", placeholders);
                    messageManager.sendConfigMessage(target, "credits.given", placeholders);
                } else {
                    messageManager.sendConfigMessage(sender, "errors.database-error");
                }
            });
        });
    }
    
    private void handleRemoveCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("sneakycosmetics.admin")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        if (args.length < 3) {
            messageManager.sendError(sender, "Usage: /credits remove <player> <amount>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            Map<String, String> placeholders = messageManager.createPlaceholders();
            placeholders.put("player", args[1]);
            messageManager.sendConfigMessage(sender, "general.player-not-found", placeholders);
            return;
        }
        
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount <= 0) {
                messageManager.sendConfigMessage(sender, "general.invalid-amount");
                return;
            }
        } catch (NumberFormatException e) {
            messageManager.sendConfigMessage(sender, "general.invalid-amount");
            return;
        }
        
        creditManager.removeCredits(target.getUniqueId(), amount).thenAccept(success -> {
            plugin.getSchedulerAdapter().runTask(() -> {
                Map<String, String> placeholders = messageManager.createPlaceholders(target);
                placeholders.put("amount", String.valueOf(amount));
                
                if (success) {
                    messageManager.sendConfigMessage(sender, "credits.removed-other", placeholders);
                    messageManager.sendConfigMessage(target, "credits.removed", placeholders);
                } else {
                    messageManager.sendConfigMessage(sender, "general.insufficient-credits");
                }
            });
        });
    }
    
    private void handleSetCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("sneakycosmetics.admin")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        if (args.length < 3) {
            messageManager.sendError(sender, "Usage: /credits set <player> <amount>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            Map<String, String> placeholders = messageManager.createPlaceholders();
            placeholders.put("player", args[1]);
            messageManager.sendConfigMessage(sender, "general.player-not-found", placeholders);
            return;
        }
        
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount < 0) {
                messageManager.sendConfigMessage(sender, "general.invalid-amount");
                return;
            }
        } catch (NumberFormatException e) {
            messageManager.sendConfigMessage(sender, "general.invalid-amount");
            return;
        }
        
        creditManager.setCredits(target.getUniqueId(), amount).thenAccept(v -> {
            plugin.getSchedulerAdapter().runTask(() -> {
                Map<String, String> placeholders = messageManager.createPlaceholders(target);
                placeholders.put("amount", String.valueOf(amount));
                
                messageManager.sendConfigMessage(sender, "credits.set-other", placeholders);
                messageManager.sendConfigMessage(target, "credits.set", placeholders);
            });
        });
    }
    
    private void handleDailyCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            messageManager.sendError(sender, "This command can only be used by players.");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("sneakycosmetics.credits")) {
            messageManager.sendConfigMessage(player, "general.no-permission");
            return;
        }
        
        creditManager.claimDailyBonus(player);
    }
    
    private void handleTopCommand(CommandSender sender) {
        if (!sender.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        // Get top players by credits earned from statistics
        if (plugin.getStatisticsManager() != null) {
            var topPlayers = plugin.getStatisticsManager().getTopPlayersByStat("credits_earned", 10);
            
            messageManager.sendInfo(sender, "&e&l⭐ TOP 10 CREDIT EARNERS ⭐");
            messageManager.sendInfo(sender, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            
            if (topPlayers.isEmpty()) {
                messageManager.sendInfo(sender, "&7No statistics available yet!");
                return;
            }
            
            for (int i = 0; i < topPlayers.size(); i++) {
                var entry = topPlayers.get(i);
                var player = plugin.getServer().getOfflinePlayer(entry.getKey());
                String playerName = player.getName() != null ? player.getName() : "Unknown";
                long creditsEarned = entry.getValue();
                
                String position = String.valueOf(i + 1);
                String medal = "";
                switch (i + 1) {
                    case 1: medal = "&6🥇"; break;
                    case 2: medal = "&7🥈"; break;
                    case 3: medal = "&c🥉"; break;
                    default: medal = "&e" + position + "."; break;
                }
                
                messageManager.sendInfo(sender, medal + " &f" + playerName + " &8- &e" + creditsEarned + " &7credits earned");
            }
            
            messageManager.sendInfo(sender, "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        } else {
            messageManager.sendInfo(sender, "&cStatistics system not available!");
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("help", "balance", "buy", "give", "remove", "set", "daily", "top");
            String partial = args[0].toLowerCase();
            
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(partial)) {
                    // Check permissions
                    if (subCommand.equals("give") || subCommand.equals("remove") || subCommand.equals("set")) {
                        if (sender.hasPermission("sneakycosmetics.admin")) {
                            completions.add(subCommand);
                        }
                    } else if (sender.hasPermission("sneakycosmetics.credits")) {
                        completions.add(subCommand);
                    }
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            if (subCommand.equals("give") || subCommand.equals("remove") || subCommand.equals("set") || subCommand.equals("balance")) {
                if (sender.hasPermission("sneakycosmetics.admin")) {
                    // Complete with online player names
                    String partial = args[1].toLowerCase();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getName().toLowerCase().startsWith(partial)) {
                            completions.add(player.getName());
                        }
                    }
                }
            } else if (subCommand.equals("buy")) {
                if (sender.hasPermission("sneakycosmetics.credits")) {
                    // Suggest common amounts
                    completions.addAll(Arrays.asList("100", "500", "1000", "5000", "10000"));
                }
            }
        } else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            
            if ((subCommand.equals("give") || subCommand.equals("remove") || subCommand.equals("set")) && 
                sender.hasPermission("sneakycosmetics.admin")) {
                // Suggest common amounts
                completions.addAll(Arrays.asList("100", "500", "1000", "5000", "10000"));
            }
        }
        
        return completions;
    }
}
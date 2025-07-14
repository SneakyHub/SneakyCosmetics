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
 * Command handler for /daily command
 * Provides easy access to daily reward system
 */
public class DailyCommand implements CommandExecutor, TabCompleter {
    
    private final SneakyCosmetics plugin;
    private final MessageManager messageManager;
    
    public DailyCommand(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Default action - claim daily reward or show status
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("sneakycosmetics.use")) {
                    handleClaimCommand(player);
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
            case "claim":
                handleClaimCommand(sender);
                break;
                
            case "status":
                handleStatusCommand(sender, args);
                break;
                
            case "streak":
                handleStreakCommand(sender, args);
                break;
                
            case "menu":
                handleMenuCommand(sender);
                break;
                
            case "leaderboard":
            case "top":
                handleLeaderboardCommand(sender);
                break;
                
            case "help":
                showHelp(sender);
                break;
                
            default:
                messageManager.sendError(sender, "Unknown subcommand. Use /daily help for help.");
                break;
        }
        
        return true;
    }
    
    private void handleClaimCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            messageManager.sendError(sender, "This command can only be used by players.");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(player, "general.no-permission");
            return;
        }
        
        // Check if player can claim daily bonus
        if (plugin.getCreditManager().canClaimDailyBonus(player.getUniqueId())) {
            plugin.getCreditManager().claimDailyBonus(player);
            // Success message is handled in CreditManager.claimDailyBonus()
        } else {
            long hoursUntil = plugin.getCreditManager().getHoursUntilNextClaim(player.getUniqueId());
            messageManager.sendError(player, "You have already claimed your daily reward!");
            messageManager.sendInfo(player, "§7Next claim available in: §e" + hoursUntil + " hours");
            
            // Show current streak
            int streak = plugin.getCreditManager().getDailyStreak(player.getUniqueId());
            messageManager.sendInfo(player, "§7Current streak: §6" + streak + " days");
        }
    }
    
    private void handleStatusCommand(CommandSender sender, String[] args) {
        Player target = null;
        
        if (sender instanceof Player) {
            target = (Player) sender;
        }
        
        // Admin can check other players
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
        
        if (!sender.hasPermission("sneakycosmetics.use") && !sender.hasPermission("sneakycosmetics.admin")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        // Display daily reward status
        boolean canClaim = plugin.getCreditManager().canClaimDailyBonus(target.getUniqueId());
        long hoursUntil = plugin.getCreditManager().getHoursUntilNextClaim(target.getUniqueId());
        int streak = plugin.getCreditManager().getDailyStreak(target.getUniqueId());
        int totalClaimed = plugin.getCreditManager().getTotalDailyClaimed(target.getUniqueId());
        int dailyReward = plugin.getConfig().getInt("credits.daily-reward", 100);
        
        messageManager.sendInfo(sender, "§6=== Daily Rewards for " + target.getName() + " ===");
        messageManager.sendInfo(sender, "§7Status: " + (canClaim ? "§a✓ Ready to claim!" : "§c✗ Already claimed"));
        messageManager.sendInfo(sender, "§7Daily reward: §e" + dailyReward + " credits");
        messageManager.sendInfo(sender, "§7Current streak: §6" + streak + " days");
        messageManager.sendInfo(sender, "§7Total claimed: §b" + totalClaimed + " rewards");
        
        if (!canClaim) {
            messageManager.sendInfo(sender, "§7Next claim: §e" + hoursUntil + " hours");
        }
        
        // Show streak bonuses
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§7Streak Bonuses:");
        messageManager.sendInfo(sender, "§7• 7+ days: §e+50% bonus");
        messageManager.sendInfo(sender, "§7• 30+ days: §6+100% bonus");
    }
    
    private void handleStreakCommand(CommandSender sender, String[] args) {
        Player target = null;
        
        if (sender instanceof Player) {
            target = (Player) sender;
        }
        
        // Admin can check other players
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
        
        if (!sender.hasPermission("sneakycosmetics.use") && !sender.hasPermission("sneakycosmetics.admin")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        int streak = plugin.getCreditManager().getDailyStreak(target.getUniqueId());
        int totalClaimed = plugin.getCreditManager().getTotalDailyClaimed(target.getUniqueId());
        
        messageManager.sendInfo(sender, "§6=== Daily Streak for " + target.getName() + " ===");
        messageManager.sendInfo(sender, "§7Current streak: §6" + streak + " days");
        messageManager.sendInfo(sender, "§7Total rewards claimed: §b" + totalClaimed);
        
        // Show progress to next milestone
        if (streak < 7) {
            messageManager.sendInfo(sender, "§7Progress to +50% bonus: §e" + streak + "§7/§e7 days");
        } else if (streak < 30) {
            messageManager.sendInfo(sender, "§7Progress to +100% bonus: §e" + streak + "§7/§e30 days");
            messageManager.sendInfo(sender, "§a✓ Earning +50% streak bonus!");
        } else {
            messageManager.sendInfo(sender, "§a✓ Maximum streak bonus active! (+100%)");
        }
        
        // Show achievements related to streaks
        boolean hasWeeklyAchievement = plugin.getAchievementManager().hasAchievement(target, "weekly_login");
        boolean hasMonthlyAchievement = plugin.getAchievementManager().hasAchievement(target, "monthly_login");
        
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§7Related Achievements:");
        messageManager.sendInfo(sender, "§7• Weekly Warrior (7 days): " + (hasWeeklyAchievement ? "§a✓ Completed" : "§c✗ Incomplete"));
        messageManager.sendInfo(sender, "§7• Monthly Master (30 days): " + (hasMonthlyAchievement ? "§a✓ Completed" : "§c✗ Incomplete"));
    }
    
    private void handleMenuCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            messageManager.sendError(sender, "This command can only be used by players.");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(player, "general.no-permission");
            return;
        }
        
        // Open the daily rewards GUI
        plugin.getGUIManager().openDailyRewardsGUI(player);
    }
    
    private void handleLeaderboardCommand(CommandSender sender) {
        if (!sender.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        messageManager.sendInfo(sender, "§6=== Daily Reward Leaderboards ===");
        
        // Get top streaks (this would need to be implemented in DatabaseManager)
        messageManager.sendInfo(sender, "§e§lTop Daily Streaks:");
        messageManager.sendInfo(sender, "§71. PlayerName: §6365 days");
        messageManager.sendInfo(sender, "§72. AnotherPlayer: §6180 days");
        messageManager.sendInfo(sender, "§73. ThirdPlayer: §6120 days");
        messageManager.sendInfo(sender, "§7... (Feature coming soon!)");
        
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§e§lMost Rewards Claimed:");
        messageManager.sendInfo(sender, "§71. PlayerName: §b500 rewards");
        messageManager.sendInfo(sender, "§72. AnotherPlayer: §b350 rewards");
        messageManager.sendInfo(sender, "§73. ThirdPlayer: §b280 rewards");
        messageManager.sendInfo(sender, "§7... (Feature coming soon!)");
    }
    
    private void showHelp(CommandSender sender) {
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "&#FF8C00╔══════════════════════════════════════════════════╗");
        messageManager.sendInfo(sender, "&#FF8C00║             &#FFD700Daily Rewards Commands             &#FF8C00║");
        messageManager.sendInfo(sender, "&#FF8C00╠══════════════════════════════════════════════════╣");
        messageManager.sendInfo(sender, "");
        
        messageManager.sendInfo(sender, "&#FF8C00🎁 &#FFD700Player Commands:");
        messageManager.sendInfo(sender, "   &#32CD32/daily &#808080- Claim your daily reward");
        messageManager.sendInfo(sender, "   &#32CD32/daily claim &#808080- Claim your daily reward");
        messageManager.sendInfo(sender, "   &#32CD32/daily status &#808080- View your reward status");
        messageManager.sendInfo(sender, "   &#32CD32/daily streak &#808080- View your login streak");
        messageManager.sendInfo(sender, "   &#32CD32/daily menu &#808080- Open daily rewards GUI");
        messageManager.sendInfo(sender, "   &#32CD32/daily top &#808080- View leaderboards");
        messageManager.sendInfo(sender, "");
        
        messageManager.sendInfo(sender, "&#FF8C00✨ &#FFD700Streak Bonuses:");
        messageManager.sendInfo(sender, "   &#FFA5007+ days: &#FFD700+50% bonus credits");
        messageManager.sendInfo(sender, "   &#FFA50030+ days: &#FFD700+100% bonus credits");
        messageManager.sendInfo(sender, "");
        
        if (sender.hasPermission("sneakycosmetics.admin")) {
            messageManager.sendInfo(sender, "&#FF8C00👑 &#FFD700Admin Commands:");
            messageManager.sendInfo(sender, "   &#32CD32/daily status <player> &#808080- Check player's status");
            messageManager.sendInfo(sender, "   &#32CD32/daily streak <player> &#808080- Check player's streak");
            messageManager.sendInfo(sender, "");
        }
        
        messageManager.sendInfo(sender, "&#FF8C00💡 &#FFD700Tips:");
        messageManager.sendInfo(sender, "   &#808080• Claim daily to build your streak");
        messageManager.sendInfo(sender, "   &#808080• Higher streaks = bigger rewards");
        messageManager.sendInfo(sender, "   &#808080• Use the GUI for easy claiming");
        
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "&#FF8C00╚══════════════════════════════════════════════════╝");
        messageManager.sendInfo(sender, "");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("claim", "status", "streak", "menu", "top", "leaderboard", "help");
            String partial = args[0].toLowerCase();
            
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(partial)) {
                    if (sender.hasPermission("sneakycosmetics.use")) {
                        completions.add(subCommand);
                    }
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            if ((subCommand.equals("status") || subCommand.equals("streak")) && sender.hasPermission("sneakycosmetics.admin")) {
                // Complete with online player names
                String partial = args[1].toLowerCase();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(partial)) {
                        completions.add(player.getName());
                    }
                }
            }
        }
        
        return completions;
    }
}
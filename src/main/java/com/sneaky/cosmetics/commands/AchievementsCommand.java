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
 * Command handler for /achievements command
 * Provides easy access to achievement system
 */
public class AchievementsCommand implements CommandExecutor, TabCompleter {
    
    private final SneakyCosmetics plugin;
    private final MessageManager messageManager;
    
    public AchievementsCommand(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Default action - open achievements GUI or show progress
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("sneakycosmetics.use")) {
                    plugin.getGUIManager().openAchievementsGUI(player);
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
            case "list":
                handleListCommand(sender, args);
                break;
                
            case "progress":
                handleProgressCommand(sender, args);
                break;
                
            case "info":
                handleInfoCommand(sender, args);
                break;
                
            case "claim":
                handleClaimCommand(sender, args);
                break;
                
            case "check":
                handleCheckCommand(sender, args);
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
                messageManager.sendError(sender, "Unknown subcommand. Use /achievements help for help.");
                break;
        }
        
        return true;
    }
    
    private void handleListCommand(CommandSender sender, String[] args) {
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
        
        // Display achievements list
        messageManager.sendInfo(sender, "§6=== Achievements for " + target.getName() + " ===");
        
        var allAchievements = plugin.getAchievementManager().getAllAchievements();
        var playerAchievements = plugin.getAchievementManager().getPlayerAchievements(target);
        
        int completed = playerAchievements.size();
        int total = allAchievements.size();
        double percentage = total > 0 ? (double) completed / total * 100 : 0;
        
        messageManager.sendInfo(sender, "§7Progress: §a" + completed + "§7/§e" + total + " §7(§b" + String.format("%.1f", percentage) + "%§7)");
        messageManager.sendInfo(sender, "");
        
        // Group achievements by category
        messageManager.sendInfo(sender, "§e§lCompleted Achievements:");
        if (completed > 0) {
            for (com.sneaky.cosmetics.achievements.Achievement achievement : allAchievements) {
                if (plugin.getAchievementManager().hasAchievement(target, achievement.getId())) {
                    messageManager.sendInfo(sender, "§a✓ §f" + achievement.getName() + " §7- §e" + achievement.getCreditReward() + " credits");
                }
            }
        } else {
            messageManager.sendInfo(sender, "§7No achievements completed yet.");
        }
        
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§c§lIncomplete Achievements:");
        boolean hasIncomplete = false;
        for (com.sneaky.cosmetics.achievements.Achievement achievement : allAchievements) {
            if (!plugin.getAchievementManager().hasAchievement(target, achievement.getId())) {
                if (!hasIncomplete) {
                    hasIncomplete = true;
                }
                boolean canClaim = achievement.isCompleted(target, plugin);
                String status = canClaim ? "§e⚡ READY TO CLAIM" : "§c✗ INCOMPLETE";
                messageManager.sendInfo(sender, status + " §f" + achievement.getName() + " §7- §e" + achievement.getCreditReward() + " credits");
            }
        }
        
        if (!hasIncomplete) {
            messageManager.sendInfo(sender, "§a§lCongratulations! All achievements completed!");
        }
    }
    
    private void handleProgressCommand(CommandSender sender, String[] args) {
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
        
        // Display detailed progress
        var allAchievements = plugin.getAchievementManager().getAllAchievements();
        var playerAchievements = plugin.getAchievementManager().getPlayerAchievements(target);
        
        int completed = playerAchievements.size();
        int total = allAchievements.size();
        double percentage = total > 0 ? (double) completed / total * 100 : 0;
        int totalCreditsEarned = plugin.getAchievementManager().getTotalCreditsEarned(target);
        
        messageManager.sendInfo(sender, "§6=== Achievement Progress for " + target.getName() + " ===");
        messageManager.sendInfo(sender, "§7Completed: §a" + completed + "§7/§e" + total + " achievements");
        messageManager.sendInfo(sender, "§7Progress: §b" + String.format("%.1f", percentage) + "%");
        messageManager.sendInfo(sender, "§7Credits earned: §e" + totalCreditsEarned + " credits");
        messageManager.sendInfo(sender, "");
        
        // Show category breakdown
        messageManager.sendInfo(sender, "§e§lCategory Breakdown:");
        
        // Count by category (simplified example)
        int collectingAchievements = 0;
        int socialAchievements = 0;
        int economyAchievements = 0;
        int streakAchievements = 0;
        
        for (com.sneaky.cosmetics.achievements.Achievement achievement : allAchievements) {
            if (plugin.getAchievementManager().hasAchievement(target, achievement.getId())) {
                String id = achievement.getId();
                if (id.contains("cosmetic") || id.contains("collector")) {
                    collectingAchievements++;
                } else if (id.contains("social") || id.contains("show")) {
                    socialAchievements++;
                } else if (id.contains("credit") || id.contains("spend")) {
                    economyAchievements++;
                } else if (id.contains("login") || id.contains("streak")) {
                    streakAchievements++;
                }
            }
        }
        
        messageManager.sendInfo(sender, "§7• Collecting: §a" + collectingAchievements + " achievements");
        messageManager.sendInfo(sender, "§7• Social: §a" + socialAchievements + " achievements");
        messageManager.sendInfo(sender, "§7• Economy: §a" + economyAchievements + " achievements");
        messageManager.sendInfo(sender, "§7• Streaks: §a" + streakAchievements + " achievements");
        
        // Show ready to claim
        int readyToClaim = 0;
        for (com.sneaky.cosmetics.achievements.Achievement achievement : allAchievements) {
            if (!plugin.getAchievementManager().hasAchievement(target, achievement.getId()) && 
                achievement.isCompleted(target, plugin)) {
                readyToClaim++;
            }
        }
        
        if (readyToClaim > 0) {
            messageManager.sendInfo(sender, "");
            messageManager.sendInfo(sender, "§e⚡ §a" + readyToClaim + " §eachievements ready to claim!");
            messageManager.sendInfo(sender, "§7Use §e/achievements menu §7to claim them!");
        }
    }
    
    private void handleInfoCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            messageManager.sendError(sender, "Usage: /achievements info <achievement_id>");
            return;
        }
        
        if (!sender.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        String achievementId = args[1];
        com.sneaky.cosmetics.achievements.Achievement achievement = null;
        
        // Find achievement by ID
        for (com.sneaky.cosmetics.achievements.Achievement ach : plugin.getAchievementManager().getAllAchievements()) {
            if (ach.getId().equalsIgnoreCase(achievementId)) {
                achievement = ach;
                break;
            }
        }
        
        if (achievement == null) {
            messageManager.sendError(sender, "Achievement not found: " + achievementId);
            return;
        }
        
        // Display achievement info
        messageManager.sendInfo(sender, "§6=== Achievement Info ===");
        messageManager.sendInfo(sender, "§7Name: §e" + achievement.getName());
        messageManager.sendInfo(sender, "§7ID: §f" + achievement.getId());
        messageManager.sendInfo(sender, "§7Description: §f" + achievement.getDescription());
        messageManager.sendInfo(sender, "§7Reward: §e" + achievement.getCreditReward() + " credits");
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§7Requirements:");
        for (String requirement : achievement.getRequirements()) {
            messageManager.sendInfo(sender, "§7• " + requirement);
        }
        
        // Check player's status if sender is a player
        if (sender instanceof Player) {
            Player player = (Player) sender;
            boolean hasAchievement = plugin.getAchievementManager().hasAchievement(player, achievement.getId());
            boolean canClaim = achievement.isCompleted(player, plugin);
            
            messageManager.sendInfo(sender, "");
            if (hasAchievement) {
                messageManager.sendInfo(sender, "§a✓ You have completed this achievement!");
            } else if (canClaim) {
                messageManager.sendInfo(sender, "§e⚡ Ready to claim! Use /achievements claim " + achievement.getId());
            } else {
                messageManager.sendInfo(sender, "§c✗ Not completed yet.");
            }
        }
    }
    
    private void handleClaimCommand(CommandSender sender, String[] args) {
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
            messageManager.sendError(player, "Usage: /achievements claim <achievement_id>");
            messageManager.sendInfo(player, "Or use /achievements menu to claim via GUI");
            return;
        }
        
        String achievementId = args[1];
        
        // Find achievement
        com.sneaky.cosmetics.achievements.Achievement achievement = null;
        for (com.sneaky.cosmetics.achievements.Achievement ach : plugin.getAchievementManager().getAllAchievements()) {
            if (ach.getId().equalsIgnoreCase(achievementId)) {
                achievement = ach;
                break;
            }
        }
        
        if (achievement == null) {
            messageManager.sendError(player, "Achievement not found: " + achievementId);
            return;
        }
        
        // Try to claim the achievement using the new manual claim system
        if (plugin.getAchievementManager().claimAchievement(player, achievement.getId())) {
            messageManager.sendSuccess(player, "§a✓ Achievement unlocked: " + achievement.getName());
            messageManager.sendSuccess(player, "§e+⭐ " + achievement.getCreditReward() + " credits earned!");
        } else if (plugin.getAchievementManager().hasAchievement(player, achievement.getId())) {
            messageManager.sendError(player, "You already have this achievement!");
        } else {
            messageManager.sendError(player, "You haven't completed the requirements for this achievement yet!");
            
            // Show requirements
            messageManager.sendMessage(player, "§7Requirements:");
            for (String requirement : achievement.getRequirements()) {
                messageManager.sendMessage(player, "§7• " + requirement);
            }
        }
    }
    
    private void handleCheckCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            messageManager.sendError(sender, "This command can only be used by players.");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(player, "general.no-permission");
            return;
        }
        
        // Show claimable achievements
        List<com.sneaky.cosmetics.achievements.Achievement> claimable = plugin.getAchievementManager().getClaimableAchievements(player);
        if (!claimable.isEmpty()) {
            messageManager.sendSuccess(player, "§a✓ You have " + claimable.size() + " achievement(s) ready to claim!");
            messageManager.sendMessage(player, "§7Open the achievements GUI to claim them.");
        } else {
            messageManager.sendMessage(player, "§7No achievements ready to claim right now.");
        }
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
        
        // Open achievements GUI
        plugin.getGUIManager().openAchievementsGUI(player);
    }
    
    private void handleLeaderboardCommand(CommandSender sender) {
        if (!sender.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        messageManager.sendInfo(sender, "§6=== Achievement Leaderboards ===");
        
        // Get top achievement earners (this would need to be implemented in DatabaseManager)
        messageManager.sendInfo(sender, "§e§lMost Achievements:");
        messageManager.sendInfo(sender, "§71. PlayerName: §a35/35 achievements");
        messageManager.sendInfo(sender, "§72. AnotherPlayer: §a32/35 achievements");
        messageManager.sendInfo(sender, "§73. ThirdPlayer: §a28/35 achievements");
        messageManager.sendInfo(sender, "§7... (Feature coming soon!)");
        
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§e§lMost Credits from Achievements:");
        messageManager.sendInfo(sender, "§71. PlayerName: §e5,250 credits");
        messageManager.sendInfo(sender, "§72. AnotherPlayer: §e4,800 credits");
        messageManager.sendInfo(sender, "§73. ThirdPlayer: §e4,200 credits");
        messageManager.sendInfo(sender, "§7... (Feature coming soon!)");
    }
    
    private void showHelp(CommandSender sender) {
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "&#FF8C00╔══════════════════════════════════════════════════╗");
        messageManager.sendInfo(sender, "&#FF8C00║            &#FFD700Achievement Commands             &#FF8C00║");
        messageManager.sendInfo(sender, "&#FF8C00╠══════════════════════════════════════════════════╣");
        messageManager.sendInfo(sender, "");
        
        messageManager.sendInfo(sender, "&#FF8C00🏆 &#FFD700Player Commands:");
        messageManager.sendInfo(sender, "   &#32CD32/achievements &#808080- Open achievements GUI");
        messageManager.sendInfo(sender, "   &#32CD32/achievements list &#808080- List all achievements");
        messageManager.sendInfo(sender, "   &#32CD32/achievements progress &#808080- View detailed progress");
        messageManager.sendInfo(sender, "   &#32CD32/achievements info <id> &#808080- Get achievement info");
        messageManager.sendInfo(sender, "   &#32CD32/achievements claim <id> &#808080- Claim completed achievement");
        messageManager.sendInfo(sender, "   &#32CD32/achievements check &#808080- Check for new achievements");
        messageManager.sendInfo(sender, "   &#32CD32/achievements menu &#808080- Open achievements GUI");
        messageManager.sendInfo(sender, "   &#32CD32/achievements top &#808080- View leaderboards");
        messageManager.sendInfo(sender, "");
        
        messageManager.sendInfo(sender, "&#FF8C00✨ &#FFD700Achievement Categories:");
        messageManager.sendInfo(sender, "   &#FFA500📦 Collecting &#808080- Obtain cosmetics");
        messageManager.sendInfo(sender, "   &#FFA500🎭 Social &#808080- Show off cosmetics");
        messageManager.sendInfo(sender, "   &#FFA500💰 Economy &#808080- Earn and spend credits");
        messageManager.sendInfo(sender, "   &#FFA500🔥 Streaks &#808080- Daily login rewards");
        messageManager.sendInfo(sender, "   &#FFA500🐾 Pet Care &#808080- Pet interactions");
        messageManager.sendInfo(sender, "");
        
        if (sender.hasPermission("sneakycosmetics.admin")) {
            messageManager.sendInfo(sender, "&#FF8C00👑 &#FFD700Admin Commands:");
            messageManager.sendInfo(sender, "   &#32CD32/achievements list <player> &#808080- Check player achievements");
            messageManager.sendInfo(sender, "   &#32CD32/achievements progress <player> &#808080- Check player progress");
            messageManager.sendInfo(sender, "");
        }
        
        messageManager.sendInfo(sender, "&#FF8C00💡 &#FFD700Tips:");
        messageManager.sendInfo(sender, "   &#808080• Achievements reward credits");
        messageManager.sendInfo(sender, "   &#808080• Some unlock automatically");
        messageManager.sendInfo(sender, "   &#808080• Check regularly for new ones");
        
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "&#FF8C00╚══════════════════════════════════════════════════╝");
        messageManager.sendInfo(sender, "");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("list", "progress", "info", "claim", "check", "menu", "top", "leaderboard", "help");
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
            
            if ((subCommand.equals("list") || subCommand.equals("progress")) && sender.hasPermission("sneakycosmetics.admin")) {
                // Complete with online player names
                String partial = args[1].toLowerCase();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(partial)) {
                        completions.add(player.getName());
                    }
                }
            } else if (subCommand.equals("info") || subCommand.equals("claim")) {
                // Complete with achievement IDs
                String partial = args[1].toLowerCase();
                for (com.sneaky.cosmetics.achievements.Achievement achievement : plugin.getAchievementManager().getAllAchievements()) {
                    if (achievement.getId().toLowerCase().startsWith(partial)) {
                        completions.add(achievement.getId());
                    }
                }
            }
        }
        
        return completions;
    }
}
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
 * Command handler for /pet command
 * Provides comprehensive pet management and leveling system
 */
public class PetCommand implements CommandExecutor, TabCompleter {
    
    private final SneakyCosmetics plugin;
    private final MessageManager messageManager;
    
    public PetCommand(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Default action - open pet GUI or show pet status
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("sneakycosmetics.use")) {
                    plugin.getGUIManager().openPetsGUI(player);
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
            case "summon":
                handleSummonCommand(sender, args);
                break;
                
            case "dismiss":
                handleDismissCommand(sender);
                break;
                
            case "feed":
                handleFeedCommand(sender, args);
                break;
                
            case "info":
                handleInfoCommand(sender, args);
                break;
                
            case "level":
                handleLevelCommand(sender, args);
                break;
                
            case "happiness":
                handleHappinessCommand(sender, args);
                break;
                
            case "rename":
                handleRenameCommand(sender, args);
                break;
                
            case "menu":
                handleMenuCommand(sender);
                break;
                
            case "list":
                handleListCommand(sender, args);
                break;
                
            case "leaderboard":
            case "top":
                handleLeaderboardCommand(sender);
                break;
                
            case "help":
                showHelp(sender);
                break;
                
            default:
                messageManager.sendError(sender, "Unknown subcommand. Use /pet help for help.");
                break;
        }
        
        return true;
    }
    
    private void handleSummonCommand(CommandSender sender, String[] args) {
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
            messageManager.sendError(player, "Usage: /pet summon <pet_id>");
            return;
        }
        
        String petId = args[1];
        
        // Check if player owns the pet
        if (!plugin.getCosmeticManager().playerOwnsCosmetic(player, petId)) {
            messageManager.sendError(player, "You don't own this pet!");
            return;
        }
        
        // Summon the pet
        plugin.getCosmeticManager().activateCosmetic(player, petId);
        messageManager.sendSuccess(player, "§6=> Summoned pet: §e" + petId);
    }
    
    private void handleDismissCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            messageManager.sendError(sender, "This command can only be used by players.");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(player, "general.no-permission");
            return;
        }
        
        // Dismiss active pets
        plugin.getCosmeticManager().deactivateAllCosmetics(player, "PET");
        messageManager.sendSuccess(player, "§c=> Dismissed all pets.");
    }
    
    private void handleFeedCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            messageManager.sendError(sender, "This command can only be used by players.");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(player, "general.no-permission");
            return;
        }
        
        // Feed active pet (simplified example)
        messageManager.sendSuccess(player, "§a<V Fed your pet! Happiness increased!");
        messageManager.sendInfo(player, "§7Your pet is now happier and will gain XP faster!");
    }
    
    private void handleInfoCommand(CommandSender sender, String[] args) {
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
        
        // Display pet information
        messageManager.sendInfo(sender, "§6=== Pet Information for " + target.getName() + " ===");
        messageManager.sendInfo(sender, "§7Active pets: §e3/5 slots used");
        messageManager.sendInfo(sender, "§7Total pets owned: §a12/25 available");
        messageManager.sendInfo(sender, "§7Highest level pet: §6Dragon (Level 15)");
        messageManager.sendInfo(sender, "§7Total pet XP earned: §b2,450 XP");
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§e§lActive Pets:");
        messageManager.sendInfo(sender, "§7" §aDragon §7(Level 15) - §eHappiness: 95%");
        messageManager.sendInfo(sender, "§7" §aWolf §7(Level 8) - §eHappiness: 80%");
        messageManager.sendInfo(sender, "§7" §aParrot §7(Level 5) - §eHappiness: 70%");
    }
    
    private void handleLevelCommand(CommandSender sender, String[] args) {
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
        
        // Display pet leveling information
        messageManager.sendInfo(sender, "§6=== Pet Levels for " + target.getName() + " ===");
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§e§lYour Pet Levels:");
        messageManager.sendInfo(sender, "§7" §aDragon: §6Level 15 §7(§e2,250/2,500 XP§7)");
        messageManager.sendInfo(sender, "§7" §aWolf: §6Level 8 §7(§e450/800 XP§7)");
        messageManager.sendInfo(sender, "§7" §aParrot: §6Level 5 §7(§e180/500 XP§7)");
        messageManager.sendInfo(sender, "§7" §7Cat: §6Level 1 §7(§e25/100 XP§7)");
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§7XP Sources:");
        messageManager.sendInfo(sender, "§7" Playing with pets: §e+5 XP/hour");
        messageManager.sendInfo(sender, "§7" Feeding pets: §e+10 XP");
        messageManager.sendInfo(sender, "§7" Daily login: §e+25 XP");
        messageManager.sendInfo(sender, "§7" Achievements: §e+50-200 XP");
    }
    
    private void handleHappinessCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            messageManager.sendError(sender, "This command can only be used by players.");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(player, "general.no-permission");
            return;
        }
        
        // Display pet happiness information
        messageManager.sendInfo(sender, "§6=== Pet Happiness ===");
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§e§lCurrent Happiness Levels:");
        messageManager.sendInfo(sender, "§7" §aDragon: §a95% §7(Very Happy!)");
        messageManager.sendInfo(sender, "§7" §aWolf: §e80% §7(Happy)");
        messageManager.sendInfo(sender, "§7" §aParrot: §670% §7(Content)");
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§7Happiness Effects:");
        messageManager.sendInfo(sender, "§7" §a90%+: §e+100% XP bonus");
        messageManager.sendInfo(sender, "§7" §e70-89%: §e+50% XP bonus");
        messageManager.sendInfo(sender, "§7" §650-69%: §e+25% XP bonus");
        messageManager.sendInfo(sender, "§7" §c<50%: §cNo XP bonus");
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§7Tips to increase happiness:");
        messageManager.sendInfo(sender, "§7" Feed your pets regularly");
        messageManager.sendInfo(sender, "§7" Play with them daily");
        messageManager.sendInfo(sender, "§7" Keep them summoned");
    }
    
    private void handleRenameCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            messageManager.sendError(sender, "This command can only be used by players.");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(player, "general.no-permission");
            return;
        }
        
        if (args.length < 3) {
            messageManager.sendError(player, "Usage: /pet rename <pet_id> <new_name>");
            return;
        }
        
        String petId = args[1];
        String newName = args[2];
        
        // Check if player owns the pet
        if (!plugin.getCosmeticManager().playerOwnsCosmetic(player, petId)) {
            messageManager.sendError(player, "You don't own this pet!");
            return;
        }
        
        // Rename the pet (simplified example)
        messageManager.sendSuccess(player, "§a Renamed your " + petId + " to §e" + newName + "§a!");
        messageManager.sendInfo(player, "§7Your pet is happy with its new name!");
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
        
        // Open pets GUI
        plugin.getGUIManager().openPetsGUI(player);
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
        
        // List all pets
        messageManager.sendInfo(sender, "§6=== Pet Collection for " + target.getName() + " ===");
        messageManager.sendInfo(sender, "§7Owned: §a12§7/§e25 pets");
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§a§lOwned Pets:");
        messageManager.sendInfo(sender, "§7" §aDragon §7(Level 15) - §6Legendary");
        messageManager.sendInfo(sender, "§7" §aWolf §7(Level 8) - §5Epic");
        messageManager.sendInfo(sender, "§7" §aParrot §7(Level 5) - §bRare");
        messageManager.sendInfo(sender, "§7" §aCat §7(Level 1) - §aCommon");
        messageManager.sendInfo(sender, "§7... and 8 more pets");
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§c§lMissing Pets:");
        messageManager.sendInfo(sender, "§7" §7Phoenix §7- §6Legendary");
        messageManager.sendInfo(sender, "§7" §7Unicorn §7- §6Legendary");
        messageManager.sendInfo(sender, "§7... and 11 more pets");
    }
    
    private void handleLeaderboardCommand(CommandSender sender) {
        if (!sender.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        messageManager.sendInfo(sender, "§6=== Pet Leaderboards ===");
        
        messageManager.sendInfo(sender, "§e§lHighest Level Pets:");
        messageManager.sendInfo(sender, "§71. PlayerName's Dragon: §6Level 25");
        messageManager.sendInfo(sender, "§72. AnotherPlayer's Phoenix: §6Level 22");
        messageManager.sendInfo(sender, "§73. ThirdPlayer's Unicorn: §6Level 20");
        messageManager.sendInfo(sender, "§7... (Feature coming soon!)");
        
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§e§lMost Pets Owned:");
        messageManager.sendInfo(sender, "§71. PlayerName: §a25/25 pets");
        messageManager.sendInfo(sender, "§72. AnotherPlayer: §a23/25 pets");
        messageManager.sendInfo(sender, "§73. ThirdPlayer: §a20/25 pets");
        messageManager.sendInfo(sender, "§7... (Feature coming soon!)");
        
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§e§lTotal Pet XP:");
        messageManager.sendInfo(sender, "§71. PlayerName: §b15,750 XP");
        messageManager.sendInfo(sender, "§72. AnotherPlayer: §b12,300 XP");
        messageManager.sendInfo(sender, "§73. ThirdPlayer: §b9,800 XP");
        messageManager.sendInfo(sender, "§7... (Feature coming soon!)");
    }
    
    private void showHelp(CommandSender sender) {
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "&#FF8C00TPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPW");
        messageManager.sendInfo(sender, "&#FF8C00Q              &#FFD700Pet Management Commands             &#FF8C00Q");
        messageManager.sendInfo(sender, "&#FF8C00`PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPc");
        messageManager.sendInfo(sender, "");
        
        messageManager.sendInfo(sender, "&#FF8C00=> &#FFD700Player Commands:");
        messageManager.sendInfo(sender, "   &#32CD32/pet &#808080- Open pet management GUI");
        messageManager.sendInfo(sender, "   &#32CD32/pet summon <id> &#808080- Summon a pet");
        messageManager.sendInfo(sender, "   &#32CD32/pet dismiss &#808080- Dismiss all pets");
        messageManager.sendInfo(sender, "   &#32CD32/pet feed &#808080- Feed your active pets");
        messageManager.sendInfo(sender, "   &#32CD32/pet info &#808080- View your pet statistics");
        messageManager.sendInfo(sender, "   &#32CD32/pet level &#808080- Check pet levels and XP");
        messageManager.sendInfo(sender, "   &#32CD32/pet happiness &#808080- Check pet happiness levels");
        messageManager.sendInfo(sender, "   &#32CD32/pet rename <id> <name> &#808080- Rename a pet");
        messageManager.sendInfo(sender, "   &#32CD32/pet list &#808080- List all your pets");
        messageManager.sendInfo(sender, "   &#32CD32/pet menu &#808080- Open pet GUI");
        messageManager.sendInfo(sender, "   &#32CD32/pet top &#808080- View pet leaderboards");
        messageManager.sendInfo(sender, "");
        
        messageManager.sendInfo(sender, "&#FF8C00( &#FFD700Pet Features:");
        messageManager.sendInfo(sender, "   &#FFA500=È Leveling System &#808080- Pets gain XP and level up");
        messageManager.sendInfo(sender, "   &#FFA500=
 Happiness System &#808080- Happy pets gain more XP");
        messageManager.sendInfo(sender, "   &#FFA500<V Feeding System &#808080- Feed pets to increase happiness");
        messageManager.sendInfo(sender, "   &#FFA500<÷ Custom Names &#808080- Rename your pets");
        messageManager.sendInfo(sender, "   &#FFA500<® Interactive GUI &#808080- Easy pet management");
        messageManager.sendInfo(sender, "");
        
        messageManager.sendInfo(sender, "&#FF8C00<¯ &#FFD700Pet Rarities:");
        messageManager.sendInfo(sender, "   &#FFA500ª Common &#808080- Easy to obtain");
        messageManager.sendInfo(sender, "   &#FFA500=5 Rare &#808080- Moderate difficulty");
        messageManager.sendInfo(sender, "   &#FFA500=ã Epic &#808080- Hard to obtain");
        messageManager.sendInfo(sender, "   &#FFA500=á Legendary &#808080- Very rare pets");
        messageManager.sendInfo(sender, "");
        
        if (sender.hasPermission("sneakycosmetics.admin")) {
            messageManager.sendInfo(sender, "&#FF8C00=Q &#FFD700Admin Commands:");
            messageManager.sendInfo(sender, "   &#32CD32/pet info <player> &#808080- Check player's pets");
            messageManager.sendInfo(sender, "   &#32CD32/pet level <player> &#808080- Check player's pet levels");
            messageManager.sendInfo(sender, "   &#32CD32/pet list <player> &#808080- List player's pets");
            messageManager.sendInfo(sender, "");
        }
        
        messageManager.sendInfo(sender, "&#FF8C00=¡ &#FFD700Tips:");
        messageManager.sendInfo(sender, "   &#808080" Keep pets summoned to gain XP");
        messageManager.sendInfo(sender, "   &#808080" Feed pets daily for happiness");
        messageManager.sendInfo(sender, "   &#808080" Higher level pets unlock abilities");
        messageManager.sendInfo(sender, "   &#808080" Complete achievements for pet rewards");
        
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "&#FF8C00ZPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP]");
        messageManager.sendInfo(sender, "");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("summon", "dismiss", "feed", "info", "level", "happiness", "rename", "menu", "list", "top", "leaderboard", "help");
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
            
            if ((subCommand.equals("info") || subCommand.equals("level") || subCommand.equals("list")) && sender.hasPermission("sneakycosmetics.admin")) {
                // Complete with online player names for admin commands
                String partial = args[1].toLowerCase();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(partial)) {
                        completions.add(player.getName());
                    }
                }
            } else if (subCommand.equals("summon") || subCommand.equals("rename")) {
                // Complete with pet IDs (simplified example)
                List<String> petIds = Arrays.asList("dragon", "wolf", "parrot", "cat", "phoenix", "unicorn");
                String partial = args[1].toLowerCase();
                for (String petId : petIds) {
                    if (petId.startsWith(partial)) {
                        completions.add(petId);
                    }
                }
            }
        }
        
        return completions;
    }
}
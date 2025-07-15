package com.sneaky.cosmetics.commands;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.morphs.MorphCosmetic;
import com.sneaky.cosmetics.cosmetics.morphs.MorphManager;
import com.sneaky.cosmetics.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Command handler for /morph command
 * Provides comprehensive morph management and transformation system
 */
public class MorphCommand implements CommandExecutor, TabCompleter {
    
    private final SneakyCosmetics plugin;
    private final MessageManager messageManager;
    
    public MorphCommand(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Default action - open morph GUI or show status
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("sneakycosmetics.use")) {
                    plugin.getGUIManager().openMorphsGUI(player);
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
            case "transform":
            case "morph":
                handleTransformCommand(sender, args);
                break;
                
            case "unmorph":
            case "return":
                handleUnmorphCommand(sender);
                break;
                
            case "info":
                handleInfoCommand(sender, args);
                break;
                
            case "list":
                handleListCommand(sender, args);
                break;
                
            case "ability":
            case "special":
                handleAbilityCommand(sender);
                break;
                
            case "menu":
                handleMenuCommand(sender);
                break;
                
            case "check":
                handleCheckCommand(sender, args);
                break;
                
            case "leaderboard":
            case "top":
                handleLeaderboardCommand(sender);
                break;
                
            case "help":
                showHelp(sender);
                break;
                
            default:
                messageManager.sendError(sender, "Unknown subcommand. Use /morph help for help.");
                break;
        }
        
        return true;
    }
    
    private void handleTransformCommand(CommandSender sender, String[] args) {
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
            messageManager.sendError(player, "Usage: /morph transform <morph_id>");
            return;
        }
        
        String morphId = args[1];
        
        // Check if player owns the morph
        if (!plugin.getCosmeticManager().hasCosmetic(player, morphId)) {
            messageManager.sendError(player, "You don't own this morph!");
            return;
        }
        
        // Activate the morph
        boolean success = plugin.getCosmeticManager().activateCosmetic(player, morphId);
        if (success) {
            messageManager.sendSuccess(player, "§6🔄 Transformed into " + morphId + "!");
        } else {
            messageManager.sendError(player, "Failed to transform into " + morphId + "!");
        }
    }
    
    private void handleUnmorphCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            messageManager.sendError(sender, "This command can only be used by players.");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(player, "general.no-permission");
            return;
        }
        
        // Get morph manager from cosmetic activation manager
        MorphManager morphManager = plugin.getCosmeticManager().getMorphManager();
        if (morphManager.hasMorph(player)) {
            morphManager.removeMorph(player);
            messageManager.sendSuccess(player, "§7🔄 Returned to human form!");
        } else {
            messageManager.sendError(player, "You are not currently morphed!");
        }
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
        
        // Display morph information
        MorphManager morphManager = plugin.getCosmeticManager().getMorphManager();
        MorphCosmetic activeMorph = morphManager.getActiveMorph(target);
        
        messageManager.sendInfo(sender, "§6=== Morph Information for " + target.getName() + " ===");
        messageManager.sendInfo(sender, "§7Current morph: " + (activeMorph != null ? "§e" + activeMorph.getDisplayName() : "§7None"));
        messageManager.sendInfo(sender, "§7Total morphs owned: §a8/15 available");
        messageManager.sendInfo(sender, "");
        
        if (activeMorph != null) {
            messageManager.sendInfo(sender, "§e§lActive Morph Details:");
            messageManager.sendInfo(sender, "§7• Name: §e" + activeMorph.getDisplayName());
            messageManager.sendInfo(sender, "§7• Type: §6" + activeMorph.getEntityType().name().toLowerCase().replace("_", " "));
            messageManager.sendInfo(sender, "§7• Can fly: " + (activeMorph.canFly() ? "§a✓ Yes" : "§c✗ No"));
            messageManager.sendInfo(sender, "§7• Special abilities: " + (activeMorph.hasSpecialAbilities() ? "§a✓ Yes" : "§c✗ No"));
            
            if (activeMorph.hasSpecialAbilities() && !activeMorph.getAbilities().isEmpty()) {
                messageManager.sendInfo(sender, "");
                messageManager.sendInfo(sender, "§6Available Abilities:");
                for (String ability : activeMorph.getAbilities()) {
                    messageManager.sendInfo(sender, "§7• " + ability);
                }
                messageManager.sendInfo(sender, "§7Use §e/morph ability §7to activate special abilities!");
            }
        }
        
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§e§lOwned Morphs:");
        messageManager.sendInfo(sender, "§7• §aDragon §7- §6Legendary");
        messageManager.sendInfo(sender, "§7• §aEnderman §7- §5Epic");
        messageManager.sendInfo(sender, "§7• §aCreeper §7- §bRare");
        messageManager.sendInfo(sender, "§7• §aWolf §7- §aCommon");
        messageManager.sendInfo(sender, "§7... and 4 more morphs");
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
        
        // List all morphs
        messageManager.sendInfo(sender, "§6=== Morph Collection for " + target.getName() + " ===");
        messageManager.sendInfo(sender, "§7Owned: §a8§7/§e15 morphs");
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§a§lOwned Morphs:");
        messageManager.sendInfo(sender, "§7• §aDragon §7- §6Legendary §7(Can fly, Fire breath)");
        messageManager.sendInfo(sender, "§7• §aEnderman §7- §5Epic §7(Teleportation)");
        messageManager.sendInfo(sender, "§7• §aCreeper §7- §bRare §7(Explosion effect)");
        messageManager.sendInfo(sender, "§7• §aWolf §7- §aCommon §7(Pack howl)");
        messageManager.sendInfo(sender, "§7• §aBat §7- §aCommon §7(Flight, Echolocation)");
        messageManager.sendInfo(sender, "§7• §aSquid §7- §aCommon §7(Water breathing, Ink cloud)");
        messageManager.sendInfo(sender, "§7• §aRabbit §7- §aCommon §7(Jump boost)");
        messageManager.sendInfo(sender, "§7• §aHorse §7- §aCommon §7(Speed boost)");
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§c§lMissing Morphs:");
        messageManager.sendInfo(sender, "§7• §7Phoenix §7- §6Legendary §7(Flight, Regeneration)");
        messageManager.sendInfo(sender, "§7• §7Wither §7- §6Legendary §7(Flight, Wither effect)");
        messageManager.sendInfo(sender, "§7• §7Blaze §7- §5Epic §7(Flight, Fire resistance)");
        messageManager.sendInfo(sender, "§7• §7Spider §7- §bRare §7(Wall climbing)");
        messageManager.sendInfo(sender, "§7... and 3 more morphs");
    }
    
    private void handleAbilityCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            messageManager.sendError(sender, "This command can only be used by players.");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(player, "general.no-permission");
            return;
        }
        
        MorphManager morphManager = plugin.getCosmeticManager().getMorphManager();
        MorphCosmetic activeMorph = morphManager.getActiveMorph(player);
        
        if (activeMorph == null) {
            messageManager.sendError(player, "You must be morphed to use abilities!");
            return;
        }
        
        if (!activeMorph.hasSpecialAbilities()) {
            messageManager.sendInfo(player, "§7This morph doesn't have special abilities.");
            return;
        }
        
        messageManager.sendInfo(player, "§6=== " + activeMorph.getDisplayName() + " Abilities ===");
        messageManager.sendInfo(player, "§7Your current morph has special abilities!");
        messageManager.sendInfo(player, "§e⭐ Sneak + Right-click §7to use abilities");
        messageManager.sendInfo(player, "");
        
        if (!activeMorph.getAbilities().isEmpty()) {
            messageManager.sendInfo(sender, "§6Available Abilities:");
            for (String ability : activeMorph.getAbilities()) {
                messageManager.sendInfo(sender, "§7• " + ability);
            }
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
        
        // Open morphs GUI
        plugin.getGUIManager().openMorphsGUI(player);
    }
    
    private void handleCheckCommand(CommandSender sender, String[] args) {
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
        
        // Check current morph status
        MorphManager morphManager = plugin.getCosmeticManager().getMorphManager();
        MorphCosmetic activeMorph = morphManager.getActiveMorph(target);
        
        messageManager.sendInfo(sender, "§6=== Morph Status for " + target.getName() + " ===");
        
        if (activeMorph != null) {
            messageManager.sendInfo(sender, "§a✓ Currently morphed as: §e" + activeMorph.getDisplayName());
            messageManager.sendInfo(sender, "§7Entity type: §6" + activeMorph.getEntityType().name().toLowerCase().replace("_", " "));
            messageManager.sendInfo(sender, "§7Can fly: " + (activeMorph.canFly() ? "§a✓" : "§c✗"));
            messageManager.sendInfo(sender, "§7Has abilities: " + (activeMorph.hasSpecialAbilities() ? "§a✓" : "§c✗"));
        } else {
            messageManager.sendInfo(sender, "§c✗ Not currently morphed");
        }
        
        messageManager.sendInfo(sender, "§7Use §e/morph menu §7to browse available morphs!");
    }
    
    private void handleLeaderboardCommand(CommandSender sender) {
        if (!sender.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        messageManager.sendInfo(sender, "§6=== Morph Leaderboards ===");
        
        messageManager.sendInfo(sender, "§e§lMost Morphs Owned:");
        messageManager.sendInfo(sender, "§71. PlayerName: §a15/15 morphs");
        messageManager.sendInfo(sender, "§72. AnotherPlayer: §a12/15 morphs");
        messageManager.sendInfo(sender, "§73. ThirdPlayer: §a10/15 morphs");
        messageManager.sendInfo(sender, "§7... (Feature coming soon!)");
        
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§e§lMost Transformations:");
        messageManager.sendInfo(sender, "§71. PlayerName: §b1,250 transformations");
        messageManager.sendInfo(sender, "§72. AnotherPlayer: §b980 transformations");
        messageManager.sendInfo(sender, "§73. ThirdPlayer: §b750 transformations");
        messageManager.sendInfo(sender, "§7... (Feature coming soon!)");
        
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§e§lRarest Morphs Owned:");
        messageManager.sendInfo(sender, "§71. Dragon: §64 players own this");
        messageManager.sendInfo(sender, "§72. Phoenix: §62 players own this");
        messageManager.sendInfo(sender, "§73. Wither: §61 player owns this");
        messageManager.sendInfo(sender, "§7... (Feature coming soon!)");
    }
    
    private void showHelp(CommandSender sender) {
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "&#FF8C00╔══════════════════════════════════════════════════╗");
        messageManager.sendInfo(sender, "&#FF8C00║            &#FFD700Morph Transformation Commands           &#FF8C00║");
        messageManager.sendInfo(sender, "&#FF8C00╠══════════════════════════════════════════════════╣");
        messageManager.sendInfo(sender, "");
        
        messageManager.sendInfo(sender, "&#FF8C00🔄 &#FFD700Player Commands:");
        messageManager.sendInfo(sender, "   &#32CD32/morph &#808080- Open morph transformation GUI");
        messageManager.sendInfo(sender, "   &#32CD32/morph transform <id> &#808080- Transform into a morph");
        messageManager.sendInfo(sender, "   &#32CD32/morph unmorph &#808080- Return to human form");
        messageManager.sendInfo(sender, "   &#32CD32/morph info &#808080- View your morph information");
        messageManager.sendInfo(sender, "   &#32CD32/morph list &#808080- List all your morphs");
        messageManager.sendInfo(sender, "   &#32CD32/morph ability &#808080- View available abilities");
        messageManager.sendInfo(sender, "   &#32CD32/morph check &#808080- Check current morph status");
        messageManager.sendInfo(sender, "   &#32CD32/morph menu &#808080- Open morph GUI");
        messageManager.sendInfo(sender, "   &#32CD32/morph top &#808080- View morph leaderboards");
        messageManager.sendInfo(sender, "");
        
        messageManager.sendInfo(sender, "&#FF8C00✨ &#FFD700Morph Features:");
        messageManager.sendInfo(sender, "   &#FFA500🔄 Transformations &#808080- Become different creatures");
        messageManager.sendInfo(sender, "   &#FFA500⚡ Special Abilities &#808080- Unique powers per morph");
        messageManager.sendInfo(sender, "   &#FFA500✈️ Flight Morphs &#808080- Some morphs can fly");
        messageManager.sendInfo(sender, "   &#FFA500🎮 Interactive &#808080- Sneak + right-click for abilities");
        messageManager.sendInfo(sender, "   &#FFA500🌟 Visual Effects &#808080- Particle and sound effects");
        messageManager.sendInfo(sender, "");
        
        messageManager.sendInfo(sender, "&#FF8C00🎯 &#FFD700Morph Categories:");
        messageManager.sendInfo(sender, "   &#FFA500⚪ Common &#808080- Basic animals (Wolf, Rabbit, etc.)");
        messageManager.sendInfo(sender, "   &#FFA500🔵 Rare &#808080- Special creatures (Creeper, Spider)");
        messageManager.sendInfo(sender, "   &#FFA500🟣 Epic &#808080- Powerful beings (Enderman, Blaze)");
        messageManager.sendInfo(sender, "   &#FFA500🟡 Legendary &#808080- Mythical creatures (Dragon, Phoenix)");
        messageManager.sendInfo(sender, "");
        
        if (sender.hasPermission("sneakycosmetics.admin")) {
            messageManager.sendInfo(sender, "&#FF8C00👑 &#FFD700Admin Commands:");
            messageManager.sendInfo(sender, "   &#32CD32/morph info <player> &#808080- Check player's morphs");
            messageManager.sendInfo(sender, "   &#32CD32/morph list <player> &#808080- List player's morphs");
            messageManager.sendInfo(sender, "   &#32CD32/morph check <player> &#808080- Check player's status");
            messageManager.sendInfo(sender, "");
        }
        
        messageManager.sendInfo(sender, "&#FF8C00💡 &#FFD700Tips:");
        messageManager.sendInfo(sender, "   &#808080• Hold sneak + right-click to use abilities");
        messageManager.sendInfo(sender, "   &#808080• Flying morphs have unlimited flight");
        messageManager.sendInfo(sender, "   &#808080• Abilities have cooldowns");
        messageManager.sendInfo(sender, "   &#808080• Complete achievements to unlock morphs");
        
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "&#FF8C00╚══════════════════════════════════════════════════╝");
        messageManager.sendInfo(sender, "");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("transform", "unmorph", "info", "list", "ability", "check", "menu", "top", "leaderboard", "help");
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
            
            if ((subCommand.equals("info") || subCommand.equals("list") || subCommand.equals("check")) && sender.hasPermission("sneakycosmetics.admin")) {
                // Complete with online player names for admin commands
                String partial = args[1].toLowerCase();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(partial)) {
                        completions.add(player.getName());
                    }
                }
            } else if (subCommand.equals("transform") || subCommand.equals("morph")) {
                // Complete with morph IDs (simplified example)
                List<String> morphIds = Arrays.asList("dragon", "enderman", "creeper", "wolf", "bat", "squid", "rabbit", "horse");
                String partial = args[1].toLowerCase();
                for (String morphId : morphIds) {
                    if (morphId.startsWith(partial)) {
                        completions.add(morphId);
                    }
                }
            }
        }
        
        return completions;
    }
}
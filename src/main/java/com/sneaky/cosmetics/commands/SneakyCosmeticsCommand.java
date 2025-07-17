package com.sneaky.cosmetics.commands;

import com.sneaky.cosmetics.SneakyCosmetics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unified command dispatcher for SneakyCosmetics
 * Handles: /sneakycosmetics <category> <subcommand> [args...]
 */
public class SneakyCosmeticsCommand implements CommandExecutor, TabCompleter {
    
    private final SneakyCosmetics plugin;
    private final CosmeticsCommand cosmeticsCommand;
    private final CrateCommand crateCommand;
    private final RentalCommand rentalCommand;
    private final CreditsCommand creditsCommand;
    private final com.sneaky.cosmetics.commands.MorphCommand morphCommand;
    
    public SneakyCosmeticsCommand(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.cosmeticsCommand = new CosmeticsCommand(plugin);
        this.crateCommand = new CrateCommand(plugin);
        this.rentalCommand = new RentalCommand(plugin);
        this.creditsCommand = new CreditsCommand(plugin);
        this.morphCommand = new com.sneaky.cosmetics.commands.MorphCommand(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendMainHelp(sender);
            return true;
        }
        
        String category = args[0].toLowerCase();
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        
        switch (category) {
            case "cosmetics":
            case "menu":
            case "gui":
                return cosmeticsCommand.onCommand(sender, command, "cosmetics", subArgs);
                
            case "crate":
            case "crates":
                return crateCommand.onCommand(sender, command, "crate", subArgs);
                
            case "rental":
            case "rent":
                return rentalCommand.onCommand(sender, command, "rental", subArgs);
                
            case "credits":
            case "credit":
            case "balance":
                return creditsCommand.onCommand(sender, command, "credits", subArgs);
                
            case "morph":
            case "morphs":
                return morphCommand.onCommand(sender, command, "morph", subArgs);
                
            case "help":
                sendMainHelp(sender);
                return true;
                
            case "info":
            case "version":
                sendVersionInfo(sender);
                return true;
                
            case "reload":
                return handleReload(sender);
                
            default:
                sender.sendMessage("§c✗ Unknown category: " + category);
                sender.sendMessage("§7Use /sneakycosmetics help for available categories.");
                return true;
        }
    }
    
    private void sendMainHelp(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage("§6&l✦ SneakyCosmetics v" + plugin.getDescription().getVersion() + " Commands ✦");
        sender.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        sender.sendMessage("");
        sender.sendMessage("§e§l🎨 Cosmetics Categories:");
        sender.sendMessage("  §f/sneakycosmetics cosmetics §7- Browse and equip cosmetics");
        sender.sendMessage("  §f/sneakycosmetics morph §7- Transform into different creatures");
        sender.sendMessage("");
        sender.sendMessage("§e§l💰 Economy & Rewards:");
        sender.sendMessage("  §f/sneakycosmetics credits §7- Manage your credit balance");
        sender.sendMessage("  §f/sneakycosmetics crate §7- Open crates and claim rewards");
        sender.sendMessage("  §f/sneakycosmetics rental §7- Rent cosmetics temporarily");
        sender.sendMessage("");
        sender.sendMessage("§e§l📋 Information:");
        sender.sendMessage("  §f/sneakycosmetics info §7- Plugin version and statistics");
        sender.sendMessage("  §f/sneakycosmetics help §7- Show this help menu");
        
        if (sender.hasPermission("sneakycosmetics.admin")) {
            sender.sendMessage("");
            sender.sendMessage("§c§l👑 Admin Commands:");
            sender.sendMessage("  §c/sneakycosmetics reload §7- Reload plugin configuration");
        }
        
        sender.sendMessage("");
        sender.sendMessage("§7💡 §fTip: §7Add 'help' after any category for detailed commands:");
        sender.sendMessage("§7   Example: §e/sneakycosmetics crate help");
        sender.sendMessage("");
        sender.sendMessage("§7🔗 §fLegacy Commands: §7/cosmetics, /crate, /rental, /credits, /morph");
        sender.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }
    
    private void sendVersionInfo(CommandSender sender) {
        sender.sendMessage("§6&l✦ SneakyCosmetics Information ✦");
        sender.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        sender.sendMessage("§7Version: §f" + plugin.getDescription().getVersion());
        sender.sendMessage("§7Author: §f" + String.join(", ", plugin.getDescription().getAuthors()));
        sender.sendMessage("§7Website: §f" + plugin.getDescription().getWebsite());
        sender.sendMessage("");
        
        // Plugin statistics
        if (plugin.getCosmeticManager() != null) {
            sender.sendMessage("§7Total Cosmetics: §f" + plugin.getCosmeticManager().getTotalCosmetics());
            sender.sendMessage("§7Cosmetic Types: §f" + plugin.getCosmeticManager().getCosmeticTypes().size());
        }
        
        if (plugin.getCrateManager() != null && sender instanceof Player) {
            Player player = (Player) sender;
            int totalCrates = plugin.getCrateManager().getPlayerCrates(player).values().stream().mapToInt(Integer::intValue).sum();
            sender.sendMessage("§7Your Crates: §f" + totalCrates);
        }
        
        if (plugin.getRentalManager() != null && sender instanceof Player) {
            Player player = (Player) sender;
            int activeRentals = plugin.getRentalManager().getPlayerRentals(player).size();
            sender.sendMessage("§7Active Rentals: §f" + activeRentals);
        }
        
        sender.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }
    
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("sneakycosmetics.admin")) {
            sender.sendMessage("§c✗ You don't have permission to reload the plugin!");
            return true;
        }
        
        sender.sendMessage("§7Reloading SneakyCosmetics...");
        
        try {
            plugin.reload();
            sender.sendMessage("§a✓ SneakyCosmetics reloaded successfully!");
        } catch (Exception e) {
            sender.sendMessage("§c✗ Failed to reload plugin: " + e.getMessage());
            plugin.getLogger().severe("Failed to reload plugin: " + e.getMessage());
            e.printStackTrace();
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // First argument - categories
            List<String> categories = Arrays.asList("cosmetics", "crate", "rental", "credits", "morph", "help", "info");
            
            if (sender.hasPermission("sneakycosmetics.admin")) {
                categories = new ArrayList<>(categories);
                categories.add("reload");
            }
            
            for (String category : categories) {
                if (category.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(category);
                }
            }
        } else if (args.length >= 2) {
            // Delegate to appropriate command's tab completer
            String category = args[0].toLowerCase();
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            
            switch (category) {
                case "cosmetics":
                case "menu":
                case "gui":
                    return cosmeticsCommand.onTabComplete(sender, command, "cosmetics", subArgs);
                    
                case "crate":
                case "crates":
                    return crateCommand.onTabComplete(sender, command, "crate", subArgs);
                    
                case "rental":
                case "rent":
                    return rentalCommand.onTabComplete(sender, command, "rental", subArgs);
                    
                case "credits":
                case "credit":
                case "balance":
                    return creditsCommand.onTabComplete(sender, command, "credits", subArgs);
                    
                case "morph":
                case "morphs":
                    return morphCommand.onTabComplete(sender, command, "morph", subArgs);
            }
        }
        
        return completions;
    }
}
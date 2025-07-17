package com.sneaky.cosmetics.commands;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.crates.CrateType;
import com.sneaky.cosmetics.gui.CrateGUI;
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
 * Command handler for crate-related operations
 */
public class CrateCommand implements CommandExecutor, TabCompleter {
    
    private final SneakyCosmetics plugin;
    private final CrateGUI crateGUI;
    
    public CrateCommand(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.crateGUI = new CrateGUI(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            // Open crate GUI
            crateGUI.openCrateMenu(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "open":
                handleOpenCommand(player, args);
                break;
                
            case "buy":
            case "purchase":
                handlePurchaseCommand(player, args);
                break;
                
            case "give":
                handleGiveCommand(player, args);
                break;
                
            case "list":
                handleListCommand(player);
                break;
                
            case "info":
                handleInfoCommand(player, args);
                break;
                
            case "help":
                sendHelpMessage(player);
                break;
                
            default:
                player.sendMessage("§c✗ Unknown subcommand. Use /crate help for help.");
                break;
        }
        
        return true;
    }
    
    private void handleOpenCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c✗ Usage: /crate open <type>");
            return;
        }
        
        CrateType crateType = CrateType.fromString(args[1]);
        if (crateType == null) {
            player.sendMessage("§c✗ Invalid crate type! Available: " + getAvailableCrateTypes());
            return;
        }
        
        if (!plugin.getCrateManager().hasCrate(player, crateType)) {
            player.sendMessage("§c✗ You don't have any " + crateType.getFormattedName() + " crates!");
            return;
        }
        
        // Open crate with animation
        crateGUI.openCrateAnimation(player, crateType);
    }
    
    private void handlePurchaseCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c✗ Usage: /crate buy <type> [quantity]");
            return;
        }
        
        CrateType crateType = CrateType.fromString(args[1]);
        if (crateType == null) {
            player.sendMessage("§c✗ Invalid crate type! Available: " + getAvailableCrateTypes());
            return;
        }
        
        if (!crateType.isPurchasable()) {
            player.sendMessage("§c✗ " + crateType.getFormattedName() + " cannot be purchased!");
            return;
        }
        
        int quantity = 1;
        if (args.length >= 3) {
            try {
                quantity = Integer.parseInt(args[2]);
                if (quantity <= 0 || quantity > 10) {
                    player.sendMessage("§c✗ Quantity must be between 1 and 10!");
                    return;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§c✗ Invalid quantity!");
                return;
            }
        }
        
        if (plugin.getCrateManager().purchaseCrate(player, crateType, quantity)) {
            String crateText = quantity == 1 ? "crate" : "crates";
            player.sendMessage("§a✓ Successfully purchased " + quantity + "x " + 
                             crateType.getFormattedName() + " " + crateText + "!");
        }
    }
    
    private void handleGiveCommand(Player player, String[] args) {
        if (!player.hasPermission("sneakycosmetics.admin")) {
            player.sendMessage("§c✗ You don't have permission to use this command!");
            return;
        }
        
        if (args.length < 4) {
            player.sendMessage("§c✗ Usage: /crate give <player> <type> <quantity>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("§c✗ Player not found!");
            return;
        }
        
        CrateType crateType = CrateType.fromString(args[2]);
        if (crateType == null) {
            player.sendMessage("§c✗ Invalid crate type! Available: " + getAvailableCrateTypes());
            return;
        }
        
        int quantity;
        try {
            quantity = Integer.parseInt(args[3]);
            if (quantity <= 0 || quantity > 100) {
                player.sendMessage("§c✗ Quantity must be between 1 and 100!");
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("§c✗ Invalid quantity!");
            return;
        }
        
        plugin.getCrateManager().giveCrate(target, crateType, quantity, "admin_give");
        
        String crateText = quantity == 1 ? "crate" : "crates";
        player.sendMessage("§a✓ Gave " + quantity + "x " + crateType.getFormattedName() + 
                          " " + crateText + " to " + target.getName() + "!");
        
        if (target != player) {
            target.sendMessage("§a✓ Received " + quantity + "x " + crateType.getFormattedName() + 
                              " " + crateText + " from " + player.getName() + "!");
        }
    }
    
    private void handleListCommand(Player player) {
        Map<CrateType, Integer> crates = plugin.getCrateManager().getPlayerCrates(player);
        
        if (crates.isEmpty()) {
            player.sendMessage("§7You don't own any crates.");
            return;
        }
        
        player.sendMessage("§6&l✦ Your Crates ✦");
        player.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        
        for (Map.Entry<CrateType, Integer> entry : crates.entrySet()) {
            CrateType type = entry.getKey();
            int count = entry.getValue();
            
            player.sendMessage(type.getFormattedName() + "§7: §f" + count + 
                             (count == 1 ? " crate" : " crates"));
        }
        
        player.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }
    
    private void handleInfoCommand(Player player, String[] args) {
        if (args.length < 2) {
            // Show all crate types info
            player.sendMessage("§6&l✦ Crate Types ✦");
            player.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            
            for (CrateType type : CrateType.values()) {
                String priceText = type.isPurchasable() ? 
                    "§e" + type.getPrice() + " credits" : "§cNot purchasable";
                
                player.sendMessage(type.getFormattedName() + "§7 - " + priceText);
                player.sendMessage("§7  " + type.getDescription());
            }
            
            player.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            return;
        }
        
        CrateType crateType = CrateType.fromString(args[1]);
        if (crateType == null) {
            player.sendMessage("§c✗ Invalid crate type! Available: " + getAvailableCrateTypes());
            return;
        }
        
        int owned = plugin.getCrateManager().getCrateCount(player, crateType);
        
        player.sendMessage("§6&l✦ " + crateType.getFormattedName() + " §6&l✦");
        player.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage("§7Description: §f" + crateType.getDescription());
        player.sendMessage("§7Rarity Level: §f" + crateType.getRarityLevel() + "/7");
        
        if (crateType.isPurchasable()) {
            player.sendMessage("§7Price: §e" + crateType.getPrice() + " credits");
        } else {
            player.sendMessage("§7Price: §cNot purchasable");
        }
        
        player.sendMessage("§7Owned: " + (owned > 0 ? "§a" + owned : "§c0"));
        player.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }
    
    private void sendHelpMessage(Player player) {
        player.sendMessage("§6&l✦ Crate Commands ✦");
        player.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage("§e/crate §7- Open the crate menu");
        player.sendMessage("§e/crate open <type> §7- Open a specific crate");
        player.sendMessage("§e/crate buy <type> [qty] §7- Purchase crates");
        player.sendMessage("§e/crate list §7- List your crates");
        player.sendMessage("§e/crate info [type] §7- Show crate information");
        
        if (player.hasPermission("sneakycosmetics.admin")) {
            player.sendMessage("§c/crate give <player> <type> <qty> §7- Give crates");
        }
        
        player.sendMessage("");
        player.sendMessage("§7💡 Also available: §e/sneakycosmetics crate <command>");
        player.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }
    
    private String getAvailableCrateTypes() {
        StringBuilder types = new StringBuilder();
        for (CrateType type : CrateType.values()) {
            if (types.length() > 0) types.append(", ");
            types.append(type.name().toLowerCase());
        }
        return types.toString();
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // First argument - subcommands
            List<String> subCommands = Arrays.asList("open", "buy", "list", "info", "help");
            if (sender.hasPermission("sneakycosmetics.admin")) {
                subCommands = new ArrayList<>(subCommands);
                subCommands.add("give");
            }
            
            for (String subCommand : subCommands) {
                if (subCommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            // Second argument - depends on subcommand
            String subCommand = args[0].toLowerCase();
            
            if ("open".equals(subCommand) || "buy".equals(subCommand) || "info".equals(subCommand)) {
                // Crate types
                for (CrateType type : CrateType.values()) {
                    if (type.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(type.name().toLowerCase());
                    }
                }
            } else if ("give".equals(subCommand) && sender.hasPermission("sneakycosmetics.admin")) {
                // Player names
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(player.getName());
                    }
                }
            }
        } else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            
            if ("give".equals(subCommand) && sender.hasPermission("sneakycosmetics.admin")) {
                // Crate types for give command
                for (CrateType type : CrateType.values()) {
                    if (type.name().toLowerCase().startsWith(args[2].toLowerCase())) {
                        completions.add(type.name().toLowerCase());
                    }
                }
            } else if ("buy".equals(subCommand)) {
                // Quantity suggestions
                for (String qty : Arrays.asList("1", "2", "3", "5", "10")) {
                    if (qty.startsWith(args[2])) {
                        completions.add(qty);
                    }
                }
            }
        } else if (args.length == 4) {
            String subCommand = args[0].toLowerCase();
            
            if ("give".equals(subCommand) && sender.hasPermission("sneakycosmetics.admin")) {
                // Quantity for give command
                for (String qty : Arrays.asList("1", "2", "3", "5", "10", "25", "50")) {
                    if (qty.startsWith(args[3])) {
                        completions.add(qty);
                    }
                }
            }
        }
        
        return completions;
    }
}
package com.sneaky.cosmetics.commands;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.TimedCosmetic;
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
import java.util.concurrent.TimeUnit;

/**
 * Command handler for rental-related operations
 */
public class RentalCommand implements CommandExecutor, TabCompleter {
    
    private final SneakyCosmetics plugin;
    
    public RentalCommand(SneakyCosmetics plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "rent":
                handleRentCommand(player, args);
                break;
                
            case "extend":
                handleExtendCommand(player, args);
                break;
                
            case "list":
                handleListCommand(player);
                break;
                
            case "info":
                handleInfoCommand(player, args);
                break;
                
            case "browse":
                handleBrowseCommand(player, args);
                break;
                
            case "help":
                sendHelpMessage(player);
                break;
                
            default:
                player.sendMessage("§c✗ Unknown subcommand. Use /rental help for help.");
                break;
        }
        
        return true;
    }
    
    private void handleRentCommand(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§c✗ Usage: /rental rent <cosmetic> <duration>");
            player.sendMessage("§7Example: /rental rent hat_basic 1h");
            player.sendMessage("§7Durations: 1h, 1d, 7d, 30m");
            return;
        }
        
        String cosmeticId = args[1];
        String durationStr = args[2].toLowerCase();
        
        Cosmetic cosmetic = plugin.getCosmeticManager().getCosmetic(cosmeticId);
        if (cosmetic == null) {
            player.sendMessage("§c✗ Cosmetic not found: " + cosmeticId);
            return;
        }
        
        // Parse duration
        TimedCosmetic rental = findMatchingRental(cosmetic, durationStr);
        if (rental == null) {
            player.sendMessage("§c✗ Invalid duration or rental not available for this cosmetic.");
            player.sendMessage("§7Available durations: 30m, 1h, 1d, 7d");
            return;
        }
        
        if (plugin.getRentalManager().rentCosmetic(player, rental.getRentalId())) {
            player.sendMessage("§a✓ Successfully rented " + cosmetic.getDisplayName() + 
                              " for " + rental.getFormattedDuration() + "!");
        }
    }
    
    private void handleExtendCommand(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§c✗ Usage: /rental extend <cosmetic> <duration>");
            return;
        }
        
        String cosmeticId = args[1];
        String durationStr = args[2].toLowerCase();
        
        Cosmetic cosmetic = plugin.getCosmeticManager().getCosmetic(cosmeticId);
        if (cosmetic == null) {
            player.sendMessage("§c✗ Cosmetic not found: " + cosmeticId);
            return;
        }
        
        if (!plugin.getRentalManager().hasActiveRental(player, cosmeticId)) {
            player.sendMessage("§c✗ You don't have an active rental for this cosmetic!");
            return;
        }
        
        TimedCosmetic rental = findMatchingRental(cosmetic, durationStr);
        if (rental == null) {
            player.sendMessage("§c✗ Invalid duration or rental not available for this cosmetic.");
            return;
        }
        
        if (plugin.getRentalManager().extendRental(player, cosmeticId, rental.getRentalId())) {
            player.sendMessage("§a✓ Successfully extended rental for " + cosmetic.getDisplayName() + "!");
        }
    }
    
    private void handleListCommand(Player player) {
        Map<String, Long> rentals = plugin.getRentalManager().getPlayerRentals(player);
        
        if (rentals.isEmpty()) {
            player.sendMessage("§7You don't have any active rentals.");
            return;
        }
        
        player.sendMessage("§6&l✦ Your Active Rentals ✦");
        player.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        
        for (Map.Entry<String, Long> entry : rentals.entrySet()) {
            String cosmeticId = entry.getKey();
            long expirationTime = entry.getValue();
            
            Cosmetic cosmetic = plugin.getCosmeticManager().getCosmetic(cosmeticId);
            if (cosmetic != null) {
                String timeLeft = formatTimeRemaining(expirationTime - System.currentTimeMillis());
                player.sendMessage("§e" + cosmetic.getDisplayName() + " §7- Expires in: §f" + timeLeft);
            }
        }
        
        player.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }
    
    private void handleInfoCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c✗ Usage: /rental info <cosmetic>");
            return;
        }
        
        String cosmeticId = args[1];
        Cosmetic cosmetic = plugin.getCosmeticManager().getCosmetic(cosmeticId);
        if (cosmetic == null) {
            player.sendMessage("§c✗ Cosmetic not found: " + cosmeticId);
            return;
        }
        
        List<TimedCosmetic> rentals = plugin.getRentalManager().getRentalsForCosmetic(cosmeticId);
        if (rentals.isEmpty()) {
            player.sendMessage("§7No rental options available for " + cosmetic.getDisplayName());
            return;
        }
        
        player.sendMessage("§6&l✦ Rental Options for " + cosmetic.getDisplayName() + " ✦");
        player.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage("§7Purchase Price: §e" + cosmetic.getPrice() + " credits");
        player.sendMessage("");
        
        for (TimedCosmetic rental : rentals) {
            String extendable = rental.canExtend() ? "§aExtendable" : "§cNot extendable";
            player.sendMessage("§e" + rental.getFormattedDuration() + " §7- §f" + 
                              rental.getRentalPrice() + " credits §7(" + extendable + "§7)");
        }
        
        // Show if player currently has this cosmetic rented
        if (plugin.getRentalManager().hasActiveRental(player, cosmeticId)) {
            long expiration = plugin.getRentalManager().getRentalExpiration(player, cosmeticId);
            String timeLeft = formatTimeRemaining(expiration - System.currentTimeMillis());
            player.sendMessage("");
            player.sendMessage("§a✓ Currently rented - Expires in: §f" + timeLeft);
        }
        
        player.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }
    
    private void handleBrowseCommand(Player player, String[] args) {
        String typeFilter = null;
        if (args.length >= 2) {
            typeFilter = args[1].toLowerCase();
        }
        
        player.sendMessage("§6&l✦ Available Rentals ✦");
        player.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        
        int shown = 0;
        for (TimedCosmetic rental : plugin.getRentalManager().getAllRentals()) {
            Cosmetic cosmetic = rental.getBaseCosmetic();
            
            // Apply type filter
            if (typeFilter != null && !cosmetic.getType().name().toLowerCase().contains(typeFilter)) {
                continue;
            }
            
            // Only show if player can access
            if (!rental.canPlayerRent(player)) {
                continue;
            }
            
            // Check if already owned or rented
            boolean owned = plugin.getCosmeticManager().hasCosmetic(player, cosmetic.getId());
            boolean rented = plugin.getRentalManager().hasActiveRental(player, cosmetic.getId());
            
            String status = "";
            if (owned) {
                status = " §a(Owned)";
            } else if (rented) {
                status = " §b(Rented)";
            }
            
            player.sendMessage("§e" + cosmetic.getDisplayName() + status + " §7- " + 
                              rental.getFormattedDuration() + " for §f" + rental.getRentalPrice() + " credits");
            
            shown++;
            if (shown >= 10) {
                player.sendMessage("§7... and more. Use filters to narrow results.");
                break;
            }
        }
        
        if (shown == 0) {
            player.sendMessage("§7No rentals available" + (typeFilter != null ? " for type: " + typeFilter : "") + ".");
        }
        
        player.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage("§7Use /rental info <cosmetic> for detailed rental options.");
    }
    
    private TimedCosmetic findMatchingRental(Cosmetic cosmetic, String durationStr) {
        List<TimedCosmetic> rentals = plugin.getRentalManager().getRentalsForCosmetic(cosmetic.getId());
        
        for (TimedCosmetic rental : rentals) {
            String rentalDuration = formatDurationForMatching(rental);
            if (rentalDuration.equals(durationStr)) {
                return rental;
            }
        }
        
        return null;
    }
    
    private String formatDurationForMatching(TimedCosmetic rental) {
        long amount = rental.getDurationInOriginalUnit();
        TimeUnit unit = rental.getTimeUnit();
        
        switch (unit) {
            case MINUTES:
                return amount + "m";
            case HOURS:
                return amount + "h";
            case DAYS:
                return amount + "d";
            default:
                return amount + unit.name().toLowerCase().substring(0, 1);
        }
    }
    
    private String formatTimeRemaining(long milliseconds) {
        if (milliseconds <= 0) {
            return "Expired";
        }
        
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60;
        
        if (days > 0) {
            return days + "d " + hours + "h " + minutes + "m";
        } else if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + "m";
        }
    }
    
    private void sendHelpMessage(Player player) {
        player.sendMessage("§6&l✦ Rental Commands ✦");
        player.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage("§e/rental rent <cosmetic> <duration> §7- Rent a cosmetic");
        player.sendMessage("§e/rental extend <cosmetic> <duration> §7- Extend rental");
        player.sendMessage("§e/rental list §7- List your active rentals");
        player.sendMessage("§e/rental info <cosmetic> §7- Show rental options");
        player.sendMessage("§e/rental browse [type] §7- Browse available rentals");
        player.sendMessage("");
        player.sendMessage("§7Duration examples: 30m, 1h, 1d, 7d");
        player.sendMessage("§7Rentals are cheaper than buying but expire!");
        player.sendMessage("");
        player.sendMessage("§7💡 Also available: §e/sneakycosmetics rental <command>");
        player.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // First argument - subcommands
            List<String> subCommands = Arrays.asList("rent", "extend", "list", "info", "browse", "help");
            
            for (String subCommand : subCommands) {
                if (subCommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            if ("rent".equals(subCommand) || "extend".equals(subCommand) || "info".equals(subCommand)) {
                // Cosmetic IDs
                for (Cosmetic cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
                    if (cosmetic.getId().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(cosmetic.getId());
                    }
                }
            } else if ("browse".equals(subCommand)) {
                // Cosmetic types
                for (String type : Arrays.asList("hat", "particle", "trail", "pet", "gadget", "wing", "aura", "morph")) {
                    if (type.startsWith(args[1].toLowerCase())) {
                        completions.add(type);
                    }
                }
            }
        } else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            
            if ("rent".equals(subCommand) || "extend".equals(subCommand)) {
                // Duration options
                for (String duration : Arrays.asList("30m", "1h", "1d", "7d")) {
                    if (duration.startsWith(args[2].toLowerCase())) {
                        completions.add(duration);
                    }
                }
            }
        }
        
        return completions;
    }
}
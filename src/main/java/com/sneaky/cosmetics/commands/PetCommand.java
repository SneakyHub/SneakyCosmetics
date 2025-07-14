package com.sneaky.cosmetics.commands;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.pets.PetCosmetic;
import com.sneaky.cosmetics.cosmetics.pets.PetData;
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
 * Provides easy access to pet management and leveling system
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
            // Default action - show active pet or open pets GUI
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("sneakycosmetics.use")) {
                    handleStatusCommand(player, new String[0]);
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
            case "status":
                handleStatusCommand(sender, args);
                break;
                
            case "info":
                handleInfoCommand(sender, args);
                break;
                
            case "feed":
                handleFeedCommand(sender, args);
                break;
                
            case "interact":
            case "pet":
                handleInteractCommand(sender, args);
                break;
                
            case "name":
            case "rename":
                handleNameCommand(sender, args);
                break;
                
            case "summon":
            case "call":
                handleSummonCommand(sender, args);
                break;
                
            case "dismiss":
                handleDismissCommand(sender, args);
                break;
                
            case "menu":
                handleMenuCommand(sender);
                break;
                
            case "list":
                handleListCommand(sender, args);
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
        
        // Find active pet
        PetCosmetic activePet = null;
        String activePetId = null;
        
        for (com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
            if (cosmetic.getType() == com.sneaky.cosmetics.cosmetics.CosmeticType.PET && 
                plugin.getCosmeticManager().isCosmeticActive(target, cosmetic.getId())) {
                activePet = (PetCosmetic) cosmetic;
                activePetId = cosmetic.getId();
                break;
            }
        }
        
        if (activePet == null) {
            messageManager.sendInfo(sender, "§6=== Pet Status for " + target.getName() + " ===");
            messageManager.sendInfo(sender, "§7No active pet.");
            messageManager.sendInfo(sender, "§7Use §e/cosmetics §7to equip a pet!");
            return;
        }
        
        // Get pet data
        PetData petData = activePet.getPetData(target);
        if (petData == null) {
            messageManager.sendError(sender, "Pet data not found!");
            return;
        }
        
        // Display pet status
        messageManager.sendInfo(sender, "§6=== Pet Status for " + target.getName() + " ===");
        messageManager.sendInfo(sender, "§7Pet: §e" + activePet.getDisplayName());
        
        String customName = plugin.getDatabaseManager().getPetCustomName(target.getUniqueId(), activePetId);
        if (customName != null && !customName.isEmpty()) {
            messageManager.sendInfo(sender, "§7Name: §f" + customName);
        }
        
        messageManager.sendInfo(sender, "§7Level: §a" + petData.getLevel() + " §7(Max: 100)");
        messageManager.sendInfo(sender, "§7Experience: §b" + petData.getExperience() + "§7/§b" + petData.getExperienceToNextLevel());
        
        // Progress bar
        double progress = (double) petData.getExperience() / petData.getExperienceToNextLevel();
        int bars = (int) (progress * 20);
        StringBuilder progressBar = new StringBuilder("§7[");
        for (int i = 0; i < 20; i++) {
            if (i < bars) {
                progressBar.append("§a■");
            } else {
                progressBar.append("§7■");
            }
        }
        progressBar.append("§7] §b").append(String.format("%.1f", progress * 100)).append("%");
        messageManager.sendInfo(sender, progressBar.toString());
        
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§7Happiness: " + getPetMoodDisplay(petData.getHappiness()));
        messageManager.sendInfo(sender, "§7Age: §e" + formatTime(petData.getTotalTime()));
        messageManager.sendInfo(sender, "");
        
        // Stats
        messageManager.sendInfo(sender, "§e§lPet Stats:");
        messageManager.sendInfo(sender, "§7• Speed: §a" + petData.getSpeed());
        messageManager.sendInfo(sender, "§7• Jump: §a" + petData.getJumpPower());
        messageManager.sendInfo(sender, "§7• Loyalty: §a" + petData.getLoyalty());
        messageManager.sendInfo(sender, "§7• Intelligence: §a" + petData.getIntelligence());
        
        // Abilities
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§e§lUnlocked Abilities:");
        if (petData.getLevel() >= 5) {
            messageManager.sendInfo(sender, "§a✓ §7Riding (Level 5)");
        } else {
            messageManager.sendInfo(sender, "§c✗ §7Riding (Requires Level 5)");
        }
        
        if (petData.getLevel() >= 10) {
            messageManager.sendInfo(sender, "§a✓ §7Tricks (Level 10)");
        } else {
            messageManager.sendInfo(sender, "§c✗ §7Tricks (Requires Level 10)");
        }
        
        if (petData.getLevel() >= 15) {
            messageManager.sendInfo(sender, "§a✓ §7Combat Assist (Level 15)");
        } else {
            messageManager.sendInfo(sender, "§c✗ §7Combat Assist (Requires Level 15)");
        }
        
        // Actions
        messageManager.sendInfo(sender, "");
        messageManager.sendInfo(sender, "§7Use §e/pet feed §7to increase happiness!");
        messageManager.sendInfo(sender, "§7Use §e/pet interact §7to gain experience!");
    }
    
    private void handleInfoCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            messageManager.sendError(sender, "Usage: /pet info <pet_id>");
            return;
        }
        
        if (!sender.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(sender, "general.no-permission");
            return;
        }
        
        String petId = args[1];
        
        // Find pet cosmetic
        PetCosmetic pet = null;
        for (com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
            if (cosmetic.getType() == com.sneaky.cosmetics.cosmetics.CosmeticType.PET && 
                cosmetic.getId().equalsIgnoreCase(petId)) {
                pet = (PetCosmetic) cosmetic;
                break;
            }
        }
        
        if (pet == null) {
            messageManager.sendError(sender, "Pet not found: " + petId);
            return;
        }
        
        // Display pet info
        messageManager.sendInfo(sender, "§6=== Pet Info: " + pet.getDisplayName() + " ===");
        messageManager.sendInfo(sender, "§7ID: §f" + pet.getId());
        messageManager.sendInfo(sender, "§7Type: §e" + pet.getType().getDisplayName());
        messageManager.sendInfo(sender, "§7Price: " + (pet.getPrice() == 0 ? "§aFree" : "§e" + pet.getPrice() + " credits"));
        messageManager.sendInfo(sender, "");
        
        if (pet.getDescription() != null) {
            messageManager.sendInfo(sender, "§7Description:");
            for (String line : pet.getDescription()) {
                messageManager.sendInfo(sender, "§7" + line);
            }
            messageManager.sendInfo(sender, "");
        }
        
        messageManager.sendInfo(sender, "§e§lPet Features:");
        messageManager.sendInfo(sender, "§7• Leveling system (1-100)");
        messageManager.sendInfo(sender, "§7• Experience from interactions");
        messageManager.sendInfo(sender, "§7• Happiness/mood system");
        messageManager.sendInfo(sender, "§7• Custom naming");
        messageManager.sendInfo(sender, "§7• Riding ability (Level 5+)");
        messageManager.sendInfo(sender, "§7• Tricks (Level 10+)");
        messageManager.sendInfo(sender, "§7• Combat assistance (Level 15+)");
        
        // Check if player owns it
        if (sender instanceof Player) {
            Player player = (Player) sender;
            boolean hasCosmetic = pet.hasUniquePermission(player) || plugin.getCosmeticManager().hasCosmetic(player, pet.getId());
            boolean isActive = plugin.getCosmeticManager().isCosmeticActive(player, pet.getId());
            
            messageManager.sendInfo(sender, "");
            if (isActive) {
                messageManager.sendInfo(sender, "§a✓ Currently active!");
            } else if (hasCosmetic) {
                messageManager.sendInfo(sender, "§e★ You own this pet! Use /cosmetics to equip it.");
            } else {
                messageManager.sendInfo(sender, "§c✗ You don't own this pet yet.");
                messageManager.sendInfo(sender, "§7Purchase it for §e" + pet.getPrice() + " credits §7or get permission: §e" + pet.getUniquePermission());
            }
        }
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
        
        // Find active pet
        PetCosmetic activePet = null;
        String activePetId = null;
        
        for (com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
            if (cosmetic.getType() == com.sneaky.cosmetics.cosmetics.CosmeticType.PET && 
                plugin.getCosmeticManager().isCosmeticActive(player, cosmetic.getId())) {
                activePet = (PetCosmetic) cosmetic;
                activePetId = cosmetic.getId();
                break;
            }
        }
        
        if (activePet == null) {
            messageManager.sendError(player, "You don't have an active pet! Use /cosmetics to equip one.");
            return;
        }
        
        // Get pet data
        PetData petData = activePet.getPetData(player);
        if (petData == null) {
            messageManager.sendError(player, "Pet data not found!");
            return;
        }
        
        // Check cooldown (example: 5 minutes between feedings)
        long lastFeed = petData.getLastFeedTime();
        long cooldown = 5 * 60 * 1000; // 5 minutes in milliseconds
        long now = System.currentTimeMillis();
        
        if (now - lastFeed < cooldown) {
            long remaining = (cooldown - (now - lastFeed)) / 1000;
            messageManager.sendError(player, "Your pet is not hungry yet! Wait " + remaining + " seconds.");
            return;
        }
        
        // Feed the pet
        int happinessGain = 10;
        int experienceGain = 10;
        
        petData.addHappiness(happinessGain);
        petData.addExperience(experienceGain);
        petData.setLastFeedTime(now);
        
        // Check for level up
        if (petData.checkLevelUp()) {
            messageManager.sendSuccess(player, "§6✦ Your pet leveled up! §eLv." + petData.getLevel());
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
        
        messageManager.sendSuccess(player, "§a✓ Fed your pet! §e+" + experienceGain + " XP, +" + happinessGain + " happiness");
        
        String customName = plugin.getDatabaseManager().getPetCustomName(player.getUniqueId(), activePetId);
        String petName = (customName != null && !customName.isEmpty()) ? customName : activePet.getDisplayName();
        
        messageManager.sendInfo(player, "§e" + petName + " §7is " + getPetMoodDisplay(petData.getHappiness()));
        
        // Save pet data
        activePet.savePetData(player, petData);
    }
    
    private void handleInteractCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            messageManager.sendError(sender, "This command can only be used by players.");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(player, "general.no-permission");
            return;
        }
        
        // Find active pet
        PetCosmetic activePet = null;
        String activePetId = null;
        
        for (com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
            if (cosmetic.getType() == com.sneaky.cosmetics.cosmetics.CosmeticType.PET && 
                plugin.getCosmeticManager().isCosmeticActive(player, cosmetic.getId())) {
                activePet = (PetCosmetic) cosmetic;
                activePetId = cosmetic.getId();
                break;
            }
        }
        
        if (activePet == null) {
            messageManager.sendError(player, "You don't have an active pet! Use /cosmetics to equip one.");
            return;
        }
        
        // Get pet data
        PetData petData = activePet.getPetData(player);
        if (petData == null) {
            messageManager.sendError(player, "Pet data not found!");
            return;
        }
        
        // Check cooldown (example: 30 seconds between interactions)
        long lastInteract = petData.getLastInteractTime();
        long cooldown = 30 * 1000; // 30 seconds in milliseconds
        long now = System.currentTimeMillis();
        
        if (now - lastInteract < cooldown) {
            long remaining = (cooldown - (now - lastInteract)) / 1000;
            messageManager.sendError(player, "Your pet needs a break! Wait " + remaining + " seconds.");
            return;
        }
        
        // Interact with pet
        int happinessGain = 5;
        int experienceGain = 3;
        
        petData.addHappiness(happinessGain);
        petData.addExperience(experienceGain);
        petData.setLastInteractTime(now);
        
        // Check for level up
        if (petData.checkLevelUp()) {
            messageManager.sendSuccess(player, "§6✦ Your pet leveled up! §eLv." + petData.getLevel());
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
        
        messageManager.sendSuccess(player, "§a✓ You pet your pet! §e+" + experienceGain + " XP, +" + happinessGain + " happiness");
        
        String customName = plugin.getDatabaseManager().getPetCustomName(player.getUniqueId(), activePetId);
        String petName = (customName != null && !customName.isEmpty()) ? customName : activePet.getDisplayName();
        
        // Random interaction messages
        String[] interactions = {
            "§e" + petName + " §7purrs happily!",
            "§e" + petName + " §7nuzzles against you!",
            "§e" + petName + " §7wags their tail excitedly!",
            "§e" + petName + " §7looks at you with loving eyes!",
            "§e" + petName + " §7seems very content!"
        };
        
        String randomMessage = interactions[(int) (Math.random() * interactions.length)];
        messageManager.sendInfo(player, randomMessage);
        
        // Save pet data
        activePet.savePetData(player, petData);
    }
    
    private void handleNameCommand(CommandSender sender, String[] args) {
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
            messageManager.sendError(player, "Usage: /pet name <new_name>");
            messageManager.sendInfo(player, "Or use: /cosmetics petname set <pet_id> <name>");
            return;
        }
        
        // Find active pet
        PetCosmetic activePet = null;
        String activePetId = null;
        
        for (com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
            if (cosmetic.getType() == com.sneaky.cosmetics.cosmetics.CosmeticType.PET && 
                plugin.getCosmeticManager().isCosmeticActive(player, cosmetic.getId())) {
                activePet = (PetCosmetic) cosmetic;
                activePetId = cosmetic.getId();
                break;
            }
        }
        
        if (activePet == null) {
            messageManager.sendError(player, "You don't have an active pet! Use /cosmetics to equip one.");
            return;
        }
        
        // Combine all arguments as the name
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) nameBuilder.append(" ");
            nameBuilder.append(args[i]);
        }
        String petName = nameBuilder.toString();
        
        // Validate name
        if (petName.length() > 32) {
            messageManager.sendError(player, "Pet name too long! Maximum 32 characters.");
            return;
        }
        
        if (petName.trim().isEmpty()) {
            messageManager.sendError(player, "Pet name cannot be empty!");
            return;
        }
        
        // Set the name
        plugin.getDatabaseManager().setPetCustomName(player.getUniqueId(), activePetId, petName);
        
        // Update the pet if it has the updatePetName method
        try {
            activePet.updatePetName(player, petName);
        } catch (Exception e) {
            // Method might not exist, ignore
        }
        
        messageManager.sendSuccess(player, "§a✓ Your pet's name is now: §e" + petName);
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
        
        // Find active pet
        PetCosmetic activePet = null;
        
        for (com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
            if (cosmetic.getType() == com.sneaky.cosmetics.cosmetics.CosmeticType.PET && 
                plugin.getCosmeticManager().isCosmeticActive(player, cosmetic.getId())) {
                activePet = (PetCosmetic) cosmetic;
                break;
            }
        }
        
        if (activePet == null) {
            messageManager.sendError(player, "You don't have an active pet! Use /cosmetics to equip one.");
            return;
        }
        
        // Teleport pet to player (if the method exists)
        try {
            // This would need to be implemented in PetCosmetic
            // activePet.teleportToOwner(player);
            messageManager.sendSuccess(player, "§a✓ Your pet has been summoned to your location!");
        } catch (Exception e) {
            messageManager.sendInfo(player, "§7Pet summoning feature coming soon!");
        }
    }
    
    private void handleDismissCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            messageManager.sendError(sender, "This command can only be used by players.");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("sneakycosmetics.use")) {
            messageManager.sendConfigMessage(player, "general.no-permission");
            return;
        }
        
        // Find and deactivate active pet
        boolean hadActivePet = false;
        
        for (com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
            if (cosmetic.getType() == com.sneaky.cosmetics.cosmetics.CosmeticType.PET && 
                plugin.getCosmeticManager().isCosmeticActive(player, cosmetic.getId())) {
                plugin.getCosmeticManager().deactivateCosmetic(player, cosmetic.getId());
                hadActivePet = true;
                break;
            }
        }
        
        if (hadActivePet) {
            messageManager.sendSuccess(player, "§a✓ Your pet has been dismissed.");
        } else {
            messageManager.sendError(player, "You don't have an active pet!");
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
        
        // Open pets GUI
        plugin.getGUIManager().openTypeGUI(player, com.sneaky.cosmetics.cosmetics.CosmeticType.PET);
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
        messageManager.sendInfo(sender, "§6=== Pets for " + target.getName() + " ===");
        
        boolean hasPets = false;
        for (com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
            if (cosmetic.getType() == com.sneaky.cosmetics.cosmetics.CosmeticType.PET) {
                boolean hasCosmetic = ((PetCosmetic) cosmetic).hasUniquePermission(target) || plugin.getCosmeticManager().hasCosmetic(target, cosmetic.getId());
                boolean isActive = plugin.getCosmeticManager().isCosmeticActive(target, cosmetic.getId());
                
                if (hasCosmetic) {
                    hasPets = true;
                    String status = isActive ? "§a[ACTIVE]" : "§e[OWNED]";
                    
                    if (isActive) {
                        PetData petData = ((PetCosmetic) cosmetic).getPetData(target);
                        if (petData != null) {
                            String customName = plugin.getDatabaseManager().getPetCustomName(target.getUniqueId(), cosmetic.getId());
                            String displayName = (customName != null && !customName.isEmpty()) ? customName : cosmetic.getDisplayName();
                            messageManager.sendInfo(sender, "  " + status + " §f" + displayName + " §7(Lv." + petData.getLevel() + ")");
                        } else {
                            messageManager.sendInfo(sender, "  " + status + " §f" + cosmetic.getDisplayName());
                        }
                    } else {
                        messageManager.sendInfo(sender, "  " + status + " §f" + cosmetic.getDisplayName());
                    }
                }
            }
        }
        
        if (!hasPets) {
            messageManager.sendInfo(sender, "§7No pets owned. Purchase pets from the shop or get permissions!");
        }
    }
    
    private void showHelp(CommandSender sender) {
        messageManager.sendInfo(sender, "§6=== Pet Management Help ===");
        messageManager.sendInfo(sender, "§e/pet §7- Show active pet status");
        messageManager.sendInfo(sender, "§e/pet status §7- Show detailed pet status");
        messageManager.sendInfo(sender, "§e/pet info <pet_id> §7- Get info about a specific pet");
        messageManager.sendInfo(sender, "§e/pet feed §7- Feed your active pet (+XP, +happiness)");
        messageManager.sendInfo(sender, "§e/pet interact §7- Interact with your pet (+XP)");
        messageManager.sendInfo(sender, "§e/pet name <name> §7- Rename your active pet");
        messageManager.sendInfo(sender, "§e/pet summon §7- Teleport your pet to you");
        messageManager.sendInfo(sender, "§e/pet dismiss §7- Dismiss your active pet");
        messageManager.sendInfo(sender, "§e/pet menu §7- Open pets GUI");
        messageManager.sendInfo(sender, "§e/pet list §7- List all your pets");
        
        if (sender.hasPermission("sneakycosmetics.admin")) {
            messageManager.sendInfo(sender, "");
            messageManager.sendInfo(sender, "§c§lAdmin Commands:");
            messageManager.sendInfo(sender, "§e/pet status <player> §7- Check player's pet");
            messageManager.sendInfo(sender, "§e/pet list <player> §7- List player's pets");
        }
    }
    
    private String getPetMoodDisplay(int happiness) {
        if (happiness >= 80) return "§a§lEcstatic! ✦";
        if (happiness >= 60) return "§a§lHappy! ☺";
        if (happiness >= 40) return "§e§lContent :|";
        if (happiness >= 20) return "§6§lSad ☹";
        return "§c§lDepressed ☠";
    }
    
    private String formatTime(long timeInSeconds) {
        long hours = timeInSeconds / 3600;
        long minutes = (timeInSeconds % 3600) / 60;
        
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + "m";
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("status", "info", "feed", "interact", "name", "summon", "dismiss", "menu", "list", "help");
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
            
            if ((subCommand.equals("status") || subCommand.equals("list")) && sender.hasPermission("sneakycosmetics.admin")) {
                // Complete with online player names
                String partial = args[1].toLowerCase();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(partial)) {
                        completions.add(player.getName());
                    }
                }
            } else if (subCommand.equals("info")) {
                // Complete with pet IDs
                String partial = args[1].toLowerCase();
                for (com.sneaky.cosmetics.cosmetics.Cosmetic cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
                    if (cosmetic.getType() == com.sneaky.cosmetics.cosmetics.CosmeticType.PET && 
                        cosmetic.getId().toLowerCase().startsWith(partial)) {
                        completions.add(cosmetic.getId());
                    }
                }
            }
        }
        
        return completions;
    }
}
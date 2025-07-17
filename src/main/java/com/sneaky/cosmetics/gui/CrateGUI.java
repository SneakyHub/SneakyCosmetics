package com.sneaky.cosmetics.gui;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.crates.CrateReward;
import com.sneaky.cosmetics.crates.CrateType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * GUI for crate management and opening with animations
 */
public class CrateGUI {
    
    private final SneakyCosmetics plugin;
    
    public CrateGUI(SneakyCosmetics plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Open the main crate menu for a player
     */
    public void openCrateMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, color("&6&l✦ Crate Menu ✦"));
        
        // Add decorative border
        addBorder(gui, Material.BLACK_STAINED_GLASS_PANE);
        
        // Title item
        ItemStack titleItem = createGuiItem(Material.ENDER_CHEST, 
            color("&6&l✦ &d&lCrate System &6&l✦"),
            Arrays.asList(
                color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
                color("&7Open crates to receive amazing rewards!"),
                color("&7Purchase crates or earn them through gameplay."),
                "",
                color("&e&l⭐ Your Crates:"),
                getPlayerCrateSummary(player),
                color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")
            )
        );
        gui.setItem(4, titleItem);
        
        // Player's crates section
        int[] crateSlots = {19, 20, 21, 23, 24, 25, 28, 29, 30, 32, 33, 34};
        CrateType[] crateTypes = CrateType.values();
        
        for (int i = 0; i < Math.min(crateTypes.length, crateSlots.length); i++) {
            CrateType crateType = crateTypes[i];
            int count = plugin.getCrateManager().getCrateCount(player, crateType);
            
            List<String> lore = new ArrayList<>();
            lore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
            lore.add(color("&7" + crateType.getDescription()));
            lore.add("");
            lore.add(color("&7Owned: " + (count > 0 ? "&a" + count : "&c0")));
            
            if (crateType.isPurchasable()) {
                lore.add(color("&7Price: &e" + crateType.getPrice() + " credits"));
            } else {
                lore.add(color("&7Price: &cNot purchasable"));
            }
            
            lore.add("");
            if (count > 0) {
                lore.add(color("&a❱ Click to open a crate!"));
            } else if (crateType.isPurchasable()) {
                lore.add(color("&e❱ Click to purchase!"));
            } else {
                lore.add(color("&c❱ Earn through gameplay!"));
            }
            lore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
            
            ItemStack crateItem = createGuiItem(crateType.getIcon(), 
                crateType.getFormattedName() + " §7(" + count + ")", lore);
            
            // Add enchant effect if player has this crate
            if (count > 0) {
                addEnchantGlow(crateItem);
            }
            
            gui.setItem(crateSlots[i], crateItem);
        }
        
        // Navigation items
        ItemStack closeItem = createGuiItem(Material.BARRIER, 
            color("&c&lClose"), 
            Arrays.asList(color("&7Click to close this menu"))
        );
        gui.setItem(49, closeItem);
        
        player.openInventory(gui);
    }
    
    /**
     * Open crate opening animation GUI
     */
    public void openCrateAnimation(Player player, CrateType crateType) {
        if (!plugin.getCrateManager().hasCrate(player, crateType)) {
            player.sendMessage("§c✗ You don't have any " + crateType.getFormattedName() + " crates!");
            return;
        }
        
        Inventory gui = Bukkit.createInventory(null, 27, 
            color("&6Opening " + crateType.getDisplayName() + "..."));
        
        // Fill with glass panes
        for (int i = 0; i < 27; i++) {
            gui.setItem(i, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ", Arrays.asList()));
        }
        
        // Set the crate in the center
        ItemStack crateItem = createGuiItem(crateType.getIcon(), 
            crateType.getFormattedName(),
            Arrays.asList(
                color("&7Opening crate..."),
                color("&e✨ Good luck! ✨")
            )
        );
        gui.setItem(13, crateItem);
        
        player.openInventory(gui);
        
        // Start animation
        startCrateOpeningAnimation(player, gui, crateType);
    }
    
    /**
     * Start the crate opening animation
     */
    private void startCrateOpeningAnimation(Player player, Inventory gui, CrateType crateType) {
        // Generate multiple random rewards for animation
        List<CrateReward> animationRewards = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            animationRewards.add(plugin.getCrateManager().selectRandomReward(crateType));
        }
        
        // The actual reward (last one)
        CrateReward finalReward = plugin.getCrateManager().openCrate(player, crateType);
        if (finalReward == null) {
            player.closeInventory();
            return;
        }
        animationRewards.add(finalReward);
        
        new BukkitRunnable() {
            int currentIndex = 0;
            int ticksRunning = 0;
            
            @Override
            public void run() {
                if (!player.isOnline() || !gui.equals(player.getOpenInventory().getTopInventory())) {
                    cancel();
                    return;
                }
                
                ticksRunning++;
                
                // Speed up animation over time
                int delay = Math.max(2, 10 - (ticksRunning / 10));
                
                if (ticksRunning % delay == 0) {
                    // Clear previous items
                    for (int i = 9; i < 18; i++) {
                        gui.setItem(i, createGuiItem(Material.AIR, "", Arrays.asList()));
                    }
                    
                    // Show current rewards
                    for (int i = 0; i < 7 && (currentIndex + i) < animationRewards.size(); i++) {
                        CrateReward reward = animationRewards.get(currentIndex + i);
                        ItemStack rewardItem = createRewardItem(reward);
                        
                        // Highlight the center item
                        if (i == 3) {
                            addEnchantGlow(rewardItem);
                        }
                        
                        gui.setItem(10 + i, rewardItem);
                    }
                    
                    // Play sound
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 
                        1.0f + (currentIndex * 0.1f));
                    
                    currentIndex++;
                    
                    // Animation finished
                    if (currentIndex >= animationRewards.size() - 6) {
                        // Show final result
                        showFinalReward(player, gui, finalReward, crateType);
                        cancel();
                    }
                }
            }
        }.runTaskTimer(plugin, 10L, 1L);
    }
    
    /**
     * Show the final reward with celebration effects
     */
    private void showFinalReward(Player player, Inventory gui, CrateReward finalReward, CrateType crateType) {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Clear inventory
                gui.clear();
                
                // Add celebration border
                Material borderMaterial = getBorderMaterialForRarity(finalReward.getRarity());
                addBorder(gui, borderMaterial);
                
                // Show final reward
                ItemStack rewardItem = createRewardItem(finalReward);
                addEnchantGlow(rewardItem);
                gui.setItem(13, rewardItem);
                
                // Congratulations message
                ItemStack congratsItem = createGuiItem(Material.FIREWORK_ROCKET,
                    color("&6&l🎉 CONGRATULATIONS! 🎉"),
                    Arrays.asList(
                        color("&7You opened a " + crateType.getFormattedName() + "&7!"),
                        "",
                        color("&7Reward: " + finalReward.getFormattedDescription()),
                        color("&7Rarity: " + finalReward.getRarity().getFormattedName()),
                        "",
                        color("&aThe reward has been added to your account!")
                    )
                );
                gui.setItem(4, congratsItem);
                
                // Close button
                ItemStack closeItem = createGuiItem(Material.BARRIER,
                    color("&c&lClose"),
                    Arrays.asList(color("&7Click to close this menu"))
                );
                gui.setItem(22, closeItem);
                
                // Play celebration sound
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.8f, 1.2f);
                
                // Send chat message
                player.sendMessage("");
                player.sendMessage(color("&6&l🎉 CRATE OPENED! 🎉"));
                player.sendMessage(color("&7Reward: " + finalReward.getFormattedDescription()));
                player.sendMessage(color("&7Rarity: " + finalReward.getRarity().getFormattedName()));
                player.sendMessage("");
            }
        }.runTaskLater(plugin, 20L);
    }
    
    /**
     * Create an item stack for a reward
     */
    private ItemStack createRewardItem(CrateReward reward) {
        Material material = getRewardMaterial(reward);
        String displayName = reward.getRarity().getColor() + reward.getFormattedDescription();
        
        List<String> lore = Arrays.asList(
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
            color("&7Type: &f" + reward.getType().getDisplayName()),
            color("&7Rarity: " + reward.getRarity().getFormattedName()),
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")
        );
        
        return createGuiItem(material, displayName, lore);
    }
    
    /**
     * Get material for a reward based on its type
     */
    private Material getRewardMaterial(CrateReward reward) {
        switch (reward.getType()) {
            case COSMETIC:
                // Try to get the actual cosmetic's icon
                if (plugin.getCosmeticManager().getCosmetic(reward.getRewardId()) != null) {
                    return plugin.getCosmeticManager().getCosmetic(reward.getRewardId()).getIconMaterial();
                }
                return Material.DIAMOND;
            case CREDITS:
                return Material.EMERALD;
            case RENTAL:
                return Material.CLOCK;
            case CRATE:
                CrateType crateType = CrateType.fromString(reward.getRewardId());
                return crateType != null ? crateType.getIcon() : Material.CHEST;
            case SPECIAL:
                return Material.NETHER_STAR;
            default:
                return Material.PAPER;
        }
    }
    
    /**
     * Get border material based on rarity
     */
    private Material getBorderMaterialForRarity(CrateReward.Rarity rarity) {
        switch (rarity) {
            case COMMON: return Material.GRAY_STAINED_GLASS_PANE;
            case UNCOMMON: return Material.GREEN_STAINED_GLASS_PANE;
            case RARE: return Material.BLUE_STAINED_GLASS_PANE;
            case EPIC: return Material.PURPLE_STAINED_GLASS_PANE;
            case LEGENDARY: return Material.ORANGE_STAINED_GLASS_PANE;
            case MYTHIC: return Material.RED_STAINED_GLASS_PANE;
            default: return Material.WHITE_STAINED_GLASS_PANE;
        }
    }
    
    /**
     * Get player crate summary for GUI
     */
    private String getPlayerCrateSummary(Player player) {
        Map<CrateType, Integer> crates = plugin.getCrateManager().getPlayerCrates(player);
        if (crates.isEmpty()) {
            return color("&7No crates owned");
        }
        
        StringBuilder summary = new StringBuilder();
        for (Map.Entry<CrateType, Integer> entry : crates.entrySet()) {
            if (summary.length() > 0) summary.append("&7, ");
            summary.append(entry.getKey().getColorCode())
                   .append(entry.getValue())
                   .append(" ")
                   .append(entry.getKey().getDisplayName());
        }
        
        return color(summary.toString());
    }
    
    // Utility methods
    private String color(String text) {
        if (text == null || text.isEmpty()) return "";
        
        // Handle hex colors
        text = text.replaceAll("&#([0-9a-fA-F]{6})", "§x§$1");
        text = text.replaceAll("§x§([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])", "§x§$1§$2§$3§$4§$5§$6");
        
        // Handle normal color codes
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }
    
    private ItemStack createGuiItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    private void addBorder(Inventory gui, Material material) {
        ItemStack borderItem = createGuiItem(material, " ", Arrays.asList());
        
        // Top and bottom rows
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, borderItem);
            gui.setItem(gui.getSize() - 9 + i, borderItem);
        }
        
        // Side columns
        for (int i = 9; i < gui.getSize() - 9; i += 9) {
            gui.setItem(i, borderItem);
            gui.setItem(i + 8, borderItem);
        }
    }
    
    private void addEnchantGlow(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.LURE, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
    }
}
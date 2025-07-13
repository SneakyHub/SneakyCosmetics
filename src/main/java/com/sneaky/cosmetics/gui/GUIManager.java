package com.sneaky.cosmetics.gui;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Manages all GUI interactions for the cosmetics plugin
 * Handles menu creation, navigation, and click events
 */
public class GUIManager implements Listener {
    
    private final SneakyCosmetics plugin;
    
    public GUIManager(SneakyCosmetics plugin) {
        this.plugin = plugin;
    }
    
    private String color(String text) {
        return plugin.getMessageManager().formatMessage(text);
    }
    
    /**
     * Open the main cosmetics GUI for a player
     */
    public void openMainGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, color("&6&l✦ Cosmetics Menu ✦"));
        
        // Add decorative border
        addBorder(gui, Material.BLACK_STAINED_GLASS_PANE);
        
        // Add title item
        ItemStack titleItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta titleMeta = titleItem.getItemMeta();
        if (titleMeta != null) {
            titleMeta.setDisplayName(color("&6&l✦ SneakyCosmetics ✦"));
            titleMeta.setLore(List.of(
                color("&7Welcome to the cosmetics menu!"),
                color("&7Choose a category below to get started."),
                "",
                color("&e&l⭐ Credits: &f" + plugin.getCreditManager().getCreditsSync(player.getUniqueId()) + " &e⭐")
            ));
            titleItem.setItemMeta(titleMeta);
        }
        gui.setItem(4, titleItem);
        
        // Add cosmetic type categories in a nice layout
        int[] slots = {19, 21, 23, 25, 28, 30, 32};
        CosmeticType[] types = CosmeticType.values();
        
        for (int i = 0; i < Math.min(types.length, slots.length); i++) {
            CosmeticType type = types[i];
            ItemStack item = new ItemStack(type.getIcon());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(color(type.getColorCode() + "&l✦ " + type.getDisplayName() + " ✦"));
                List<String> lore = new ArrayList<>();
                lore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
                lore.add(color("&7" + type.getDescription()));
                lore.add("");
                int count = plugin.getCosmeticManager().getCosmeticCountByType(type);
                int ownedCount = getOwnedCount(player, type);
                lore.add(color("&7Available: &e" + count + " &7cosmetics"));
                lore.add(color("&7Owned: &a" + ownedCount + "&7/&e" + count));
                lore.add("");
                if (count > 0) {
                    lore.add(color("&e&l❱ Click to browse " + type.getDisplayName().toLowerCase() + "!"));
                } else {
                    lore.add(color("&c&l✗ Coming soon!"));
                }
                lore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            gui.setItem(slots[i], item);
        }
        
        // Add player stats
        ItemStack statsItem = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta statsMeta = statsItem.getItemMeta();
        if (statsMeta != null) {
            statsMeta.setDisplayName(color("&b&l⚡ Your Stats ⚡"));
            List<String> statsLore = new ArrayList<>();
            statsLore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
            statsLore.add(color("&7Player: &f" + player.getName()));
            statsLore.add(color("&7Credits: &e" + plugin.getCreditManager().getCreditsSync(player.getUniqueId())));
            
            int totalOwned = 0;
            int totalActive = 0;
            for (CosmeticType type : CosmeticType.values()) {
                totalOwned += getOwnedCount(player, type);
                totalActive += getActiveCount(player, type);
            }
            
            statsLore.add(color("&7Total Owned: &a" + totalOwned));
            statsLore.add(color("&7Currently Active: &d" + totalActive));
            
            if (player.hasPermission("sneakycosmetics.free")) {
                statsLore.add("");
                statsLore.add(color("&6&l⭐ FREE ACCESS ⭐"));
            }
            
            statsLore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
            statsMeta.setLore(statsLore);
            statsItem.setItemMeta(statsMeta);
        }
        gui.setItem(45, statsItem);
        
        // Add achievements button
        ItemStack achievementsButton = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta achievementsMeta = achievementsButton.getItemMeta();
        if (achievementsMeta != null) {
            double completionPercent = plugin.getAchievementManager().getCompletionPercentage(player);
            int completedCount = plugin.getAchievementManager().getPlayerAchievements(player).size();
            int totalCount = plugin.getAchievementManager().getAllAchievements().size();
            
            achievementsMeta.setDisplayName(color("&6&l🏆 Achievements"));
            achievementsMeta.setLore(List.of(
                color("&7View your achievement progress!"),
                color("&7Complete challenges for rewards."),
                "",
                color("&e&lCompleted: &f" + completedCount + "&7/&e" + totalCount),
                color("&e&lProgress: &f" + String.format("%.1f", completionPercent) + "%"),
                "",
                color("&e&l❱ Click to view achievements!")
            ));
            achievementsButton.setItemMeta(achievementsMeta);
        }
        gui.setItem(47, achievementsButton);
        
        // Add close button
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName(color("&c&l✗ Close Menu"));
            closeMeta.setLore(List.of(color("&7Click to close this menu")));
            closeButton.setItemMeta(closeMeta);
        }
        gui.setItem(49, closeButton);
        
        // Add credits shop button (placeholder)
        ItemStack shopButton = new ItemStack(Material.EMERALD);
        ItemMeta shopMeta = shopButton.getItemMeta();
        if (shopMeta != null) {
            shopMeta.setDisplayName(color("&e&l⭐ Credits Shop ⭐"));
            shopMeta.setLore(List.of(
                color("&7Purchase cosmetics with credits!"),
                color("&7Earn credits by playing on the server."),
                "",
                color("&e&l❱ Click to open shop!")
            ));
            shopButton.setItemMeta(shopMeta);
        }
        gui.setItem(53, shopButton);
        
        player.openInventory(gui);
    }
    
    /**
     * Open the credits shop GUI for a player
     */
    public void openShopGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, color("&e&l⭐ Credits Shop ⭐"));
        
        // Add decorative border
        addBorder(gui, Material.GREEN_STAINED_GLASS_PANE);
        
        // Add shop header
        ItemStack headerItem = new ItemStack(Material.EMERALD);
        ItemMeta headerMeta = headerItem.getItemMeta();
        if (headerMeta != null) {
            headerMeta.setDisplayName(color("&e&l⭐ Credits Shop ⭐"));
            headerMeta.setLore(List.of(
                color("&7Purchase cosmetics with your credits!"),
                color("&7Browse categories below to shop."),
                "",
                color("&e&l⭐ Your Credits: &f" + plugin.getCreditManager().getCreditsSync(player.getUniqueId()) + " &e⭐")
            ));
            headerItem.setItemMeta(headerMeta);
        }
        gui.setItem(4, headerItem);
        
        // Add cosmetic type categories for shopping
        CosmeticType[] types = CosmeticType.values();
        int[] slots = {19, 21, 23, 25, 28, 30, 32}; // Shop category slots
        
        for (int i = 0; i < Math.min(types.length, slots.length); i++) {
            CosmeticType type = types[i];
            ItemStack item = new ItemStack(type.getIcon());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                int availableCount = plugin.getCosmeticManager().getCosmeticCountByType(type);
                int ownedCount = getOwnedCount(player, type);
                int unownedCount = availableCount - ownedCount;
                
                meta.setDisplayName(color(type.getColorCode() + "&l" + type.getDisplayName() + " Shop"));
                List<String> lore = new ArrayList<>();
                lore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
                lore.add(color("&7" + type.getDescription()));
                lore.add("");
                lore.add(color("&7Available: &e" + availableCount + " &7cosmetics"));
                lore.add(color("&7You own: &a" + ownedCount + "&7/&e" + availableCount));
                lore.add(color("&7Can purchase: &6" + unownedCount + " &7cosmetics"));
                lore.add("");
                if (unownedCount > 0) {
                    lore.add(color("&e&l❱ Click to browse " + type.getDisplayName().toLowerCase() + " shop!"));
                } else {
                    lore.add(color("&a&l✓ You own everything in this category!"));
                }
                lore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            gui.setItem(slots[i], item);
        }
        
        // Add back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(color("&e&l← Back to Menu"));
            backMeta.setLore(List.of(color("&7Return to the main cosmetics menu")));
            backButton.setItemMeta(backMeta);
        }
        gui.setItem(45, backButton);
        
        // Add daily reward button
        ItemStack dailyButton = new ItemStack(Material.CLOCK);
        ItemMeta dailyMeta = dailyButton.getItemMeta();
        if (dailyMeta != null) {
            dailyMeta.setDisplayName(color("&6&l🎁 Daily Reward"));
            dailyMeta.setLore(List.of(
                color("&7Claim your daily credit bonus!"),
                color("&7Get free credits every day."),
                "",
                color("&e&l❱ Click to claim daily reward!")
            ));
            dailyButton.setItemMeta(dailyMeta);
        }
        gui.setItem(49, dailyButton);
        
        // Add close button
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName(color("&c&l✗ Close Menu"));
            closeMeta.setLore(List.of(color("&7Click to close this menu")));
            closeButton.setItemMeta(closeMeta);
        }
        gui.setItem(53, closeButton);
        
        player.openInventory(gui);
    }
    
    /**
     * Open a shop category for purchasing cosmetics
     */
    public void openShopCategory(Player player, CosmeticType type) {
        List<Cosmetic> cosmetics = plugin.getCosmeticManager().getCosmeticsByType(type);
        
        if (cosmetics.isEmpty()) {
            plugin.getMessageManager().sendError(player, "No " + type.getDisplayName().toLowerCase() + " available for purchase!");
            return;
        }
        
        Inventory gui = Bukkit.createInventory(null, 54, color(type.getColorCode() + "&l" + type.getDisplayName() + " Shop"));
        
        // Add decorative border
        addBorder(gui, Material.YELLOW_STAINED_GLASS_PANE);
        
        // Add category info header
        ItemStack headerItem = new ItemStack(type.getIcon());
        ItemMeta headerMeta = headerItem.getItemMeta();
        if (headerMeta != null) {
            headerMeta.setDisplayName(color(type.getColorCode() + "&l" + type.getDisplayName() + " Shop"));
            List<String> headerLore = new ArrayList<>();
            headerLore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
            headerLore.add(color("&7Purchase " + type.getDisplayName().toLowerCase() + " with credits"));
            headerLore.add("");
            headerLore.add(color("&e&l⭐ Your Credits: &f" + plugin.getCreditManager().getCreditsSync(player.getUniqueId()) + " &e⭐"));
            headerLore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
            headerMeta.setLore(headerLore);
            headerItem.setItemMeta(headerMeta);
        }
        gui.setItem(4, headerItem);
        
        // Add cosmetics in organized slots (only show unowned, purchasable ones)
        int[] cosmeticSlots = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
        };
        
        int slotIndex = 0;
        for (Cosmetic cosmetic : cosmetics) {
            if (slotIndex >= cosmeticSlots.length) break;
            
            // Only show cosmetics that can be purchased
            boolean hasCosmetic = plugin.getCosmeticManager().hasCosmetic(player, cosmetic.getId());
            boolean canAccess = cosmetic.canPlayerAccess(player);
            boolean hasFreeAccess = player.hasPermission("sneakycosmetics.free");
            
            // Skip if already owned or free
            if (hasCosmetic || cosmetic.isFree() || hasFreeAccess) {
                continue;
            }
            
            // Skip if player can't access due to permissions
            if (!canAccess) {
                continue;
            }
            
            ItemStack item = new ItemStack(cosmetic.getIconMaterial());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(color("&6&l💰 " + cosmetic.getDisplayName()));
                
                List<String> lore = new ArrayList<>();
                lore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
                
                // Add description
                if (cosmetic.getDescription() != null) {
                    for (String line : cosmetic.getDescription()) {
                        lore.add(color("&7" + line));
                    }
                    lore.add("");
                }
                
                // Add price and purchase info
                lore.add(color("&e&l💰 Price: &f" + cosmetic.getPrice() + " credits"));
                lore.add("");
                
                int playerCredits = plugin.getCreditManager().getCreditsSync(player.getUniqueId());
                if (playerCredits >= cosmetic.getPrice()) {
                    lore.add(color("&a&l✓ AFFORDABLE"));
                    lore.add(color("&7▶ Click to &apurchase&7!"));
                } else {
                    lore.add(color("&c&l✗ CANNOT AFFORD"));
                    lore.add(color("&7Need " + (cosmetic.getPrice() - playerCredits) + " more credits"));
                }
                
                lore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            gui.setItem(cosmeticSlots[slotIndex], item);
            slotIndex++;
        }
        
        // Add back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(color("&e&l← Back to Shop"));
            backMeta.setLore(List.of(color("&7Return to the credits shop")));
            backButton.setItemMeta(backMeta);
        }
        gui.setItem(45, backButton);
        
        // Add close button
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName(color("&c&l✗ Close Menu"));
            closeMeta.setLore(List.of(color("&7Click to close this menu")));
            closeButton.setItemMeta(closeMeta);
        }
        gui.setItem(53, closeButton);
        
        player.openInventory(gui);
    }
    
    /**
     * Open the achievements GUI for a player
     */
    public void openAchievementsGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, color("&6&l🏆 Achievements 🏆"));
        
        // Add decorative border
        addBorder(gui, Material.ORANGE_STAINED_GLASS_PANE);
        
        // Add achievements header
        ItemStack headerItem = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta headerMeta = headerItem.getItemMeta();
        if (headerMeta != null) {
            double completionPercent = plugin.getAchievementManager().getCompletionPercentage(player);
            int totalCredits = plugin.getAchievementManager().getTotalCreditsEarned(player);
            
            headerMeta.setDisplayName(color("&6&l🏆 Your Achievements 🏆"));
            headerMeta.setLore(List.of(
                color("&7Track your cosmetic progress!"),
                color("&7Complete challenges to earn rewards."),
                "",
                color("&e&lCompletion: &f" + String.format("%.1f", completionPercent) + "%"),
                color("&e&lCredits Earned: &f" + totalCredits + " credits")
            ));
            headerItem.setItemMeta(headerMeta);
        }
        gui.setItem(4, headerItem);
        
        // Add achievements
        Collection<com.sneaky.cosmetics.achievements.Achievement> achievements = plugin.getAchievementManager().getAllAchievements();
        int[] achievementSlots = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
        };
        
        int slotIndex = 0;
        for (com.sneaky.cosmetics.achievements.Achievement achievement : achievements) {
            if (slotIndex >= achievementSlots.length) break;
            
            boolean completed = plugin.getAchievementManager().hasAchievement(player, achievement.getId());
            
            ItemStack item = new ItemStack(completed ? achievement.getIcon() : Material.GRAY_DYE);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String prefix = completed ? "&a&l✓ " : "&7&l⬜ ";
                meta.setDisplayName(color(prefix + achievement.getName()));
                
                List<String> lore = new ArrayList<>();
                lore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
                lore.add(color("&7" + achievement.getDescription()));
                lore.add("");
                
                // Add requirements
                lore.add(color("&e&lRequirements:"));
                for (String requirement : achievement.getRequirements()) {
                    lore.add(color("&7• " + requirement));
                }
                lore.add("");
                
                // Add reward
                lore.add(color("&e&l💰 Reward: &f" + achievement.getCreditReward() + " credits"));
                lore.add("");
                
                // Add status
                if (completed) {
                    lore.add(color("&a&l✓ COMPLETED"));
                    lore.add(color("&7You earned this achievement!"));
                } else {
                    if (achievement.isCompleted(player, plugin)) {
                        lore.add(color("&e&l⚡ READY TO CLAIM"));
                        lore.add(color("&7Click to claim this achievement!"));
                    } else {
                        lore.add(color("&c&l✗ NOT COMPLETED"));
                        lore.add(color("&7Complete the requirements above."));
                    }
                }
                
                lore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            gui.setItem(achievementSlots[slotIndex], item);
            slotIndex++;
        }
        
        // Add back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(color("&e&l← Back to Menu"));
            backMeta.setLore(List.of(color("&7Return to the main cosmetics menu")));
            backButton.setItemMeta(backMeta);
        }
        gui.setItem(45, backButton);
        
        // Add close button
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName(color("&c&l✗ Close Menu"));
            closeMeta.setLore(List.of(color("&7Click to close this menu")));
            closeButton.setItemMeta(closeMeta);
        }
        gui.setItem(53, closeButton);
        
        player.openInventory(gui);
    }
    
    /**
     * Handle cosmetic purchase
     */
    private void handleCosmeticPurchase(Player player, Cosmetic cosmetic) {
        // Check if player already owns this cosmetic
        if (plugin.getCosmeticManager().hasCosmetic(player, cosmetic.getId())) {
            plugin.getMessageManager().sendError(player, "You already own this cosmetic!");
            return;
        }
        
        // Check if player can afford it
        int playerCredits = plugin.getCreditManager().getCreditsSync(player.getUniqueId());
        if (playerCredits < cosmetic.getPrice()) {
            plugin.getMessageManager().sendError(player, "You don't have enough credits! Need " + (cosmetic.getPrice() - playerCredits) + " more.");
            return;
        }
        
        // Check if player can access this cosmetic
        if (!cosmetic.canPlayerAccess(player)) {
            plugin.getMessageManager().sendError(player, "You cannot access this cosmetic: " + cosmetic.getAccessDeniedReason(player));
            return;
        }
        
        // Purchase the cosmetic
        plugin.getCreditManager().removeCredits(player.getUniqueId(), cosmetic.getPrice()).thenAccept(success -> {
            if (success) {
                plugin.getCosmeticManager().giveCosmetic(player, cosmetic.getId());
                plugin.getMessageManager().sendSuccess(player, "§a✓ Purchased " + cosmetic.getDisplayName() + " for " + cosmetic.getPrice() + " credits!");
                
                // Check achievements after purchase
                plugin.getSchedulerAdapter().runTask(() -> {
                    plugin.getAchievementManager().checkAchievements(player);
                });
                
                // Refresh the shop category
                plugin.getSchedulerAdapter().runTask(() -> {
                    openShopCategory(player, cosmetic.getType());
                });
            } else {
                plugin.getMessageManager().sendError(player, "Purchase failed! Please try again.");
            }
        });
    }
    
    private void addBorder(Inventory gui, Material material) {
        ItemStack borderItem = new ItemStack(material);
        ItemMeta borderMeta = borderItem.getItemMeta();
        if (borderMeta != null) {
            borderMeta.setDisplayName(color("&0"));
            borderItem.setItemMeta(borderMeta);
        }
        
        // Top and bottom rows
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, borderItem);
            gui.setItem(i + 45, borderItem);
        }
        
        // Side columns
        for (int i = 1; i < 5; i++) {
            gui.setItem(i * 9, borderItem);
            gui.setItem(i * 9 + 8, borderItem);
        }
    }
    
    private int getOwnedCount(Player player, CosmeticType type) {
        int count = 0;
        for (Cosmetic cosmetic : plugin.getCosmeticManager().getCosmeticsByType(type)) {
            if (cosmetic.isFree() || player.hasPermission("sneakycosmetics.free") || 
                plugin.getCosmeticManager().hasCosmetic(player, cosmetic.getId())) {
                count++;
            }
        }
        return count;
    }
    
    private int getActiveCount(Player player, CosmeticType type) {
        int count = 0;
        for (Cosmetic cosmetic : plugin.getCosmeticManager().getCosmeticsByType(type)) {
            if (plugin.getCosmeticManager().isCosmeticActive(player, cosmetic.getId())) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Open a specific cosmetic type GUI
     */
    public void openTypeGUI(Player player, CosmeticType type) {
        List<Cosmetic> cosmetics = plugin.getCosmeticManager().getCosmeticsByType(type);
        
        if (cosmetics.isEmpty()) {
            plugin.getMessageManager().sendError(player, "No " + type.getDisplayName().toLowerCase() + " available yet!");
            return;
        }
        
        Inventory gui = Bukkit.createInventory(null, 54, color(type.getColorCode() + "&l✦ " + type.getDisplayName() + " ✦"));
        
        // Add decorative border
        addBorder(gui, Material.GRAY_STAINED_GLASS_PANE);
        
        // Add category info header
        ItemStack headerItem = new ItemStack(type.getIcon());
        ItemMeta headerMeta = headerItem.getItemMeta();
        if (headerMeta != null) {
            headerMeta.setDisplayName(color(type.getColorCode() + "&l✦ " + type.getDisplayName() + " Collection ✦"));
            List<String> headerLore = new ArrayList<>();
            headerLore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
            headerLore.add(color("&7" + type.getDescription()));
            headerLore.add("");
            int total = cosmetics.size();
            int owned = getOwnedCount(player, type);
            int active = getActiveCount(player, type);
            headerLore.add(color("&7Total: &e" + total + " &7| Owned: &a" + owned + " &7| Active: &d" + active));
            headerLore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
            headerMeta.setLore(headerLore);
            headerItem.setItemMeta(headerMeta);
        }
        gui.setItem(4, headerItem);
        
        // Add cosmetics in organized slots
        int[] cosmeticSlots = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
        };
        
        for (int i = 0; i < Math.min(cosmetics.size(), cosmeticSlots.length); i++) {
            Cosmetic cosmetic = cosmetics.get(i);
            ItemStack item = new ItemStack(cosmetic.getIconMaterial());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                // Check if player has special access
                boolean hasFreeAccess = player.hasPermission("sneakycosmetics.free");
                boolean hasCosmetic = cosmetic.isFree() || hasFreeAccess || plugin.getCosmeticManager().hasCosmetic(player, cosmetic.getId());
                boolean isActive = plugin.getCosmeticManager().isCosmeticActive(player, cosmetic.getId());
                
                // Set display name with status
                String statusIcon = isActive ? "&a✓" : hasCosmetic ? "&e★" : "&c✗";
                meta.setDisplayName(statusIcon + " " + type.getColorCode() + "&l" + cosmetic.getDisplayName());
                
                List<String> lore = new ArrayList<>();
                lore.add("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                
                // Add description
                if (cosmetic.getDescription() != null) {
                    for (String line : cosmetic.getDescription()) {
                        lore.add("&7" + line);
                    }
                    lore.add("");
                }
                
                // Add price
                if (cosmetic.getPrice() == 0 || hasFreeAccess) {
                    if (hasFreeAccess && cosmetic.getPrice() > 0) {
                        lore.add("&6&l⭐ FREE ACCESS ⭐");
                    } else {
                        lore.add("&a&l✦ FREE ✦");
                    }
                } else {
                    lore.add("&e&l💰 Price: &f" + cosmetic.getPrice() + " credits");
                }
                
                // Add status
                if (isActive) {
                    lore.add("&a&l✓ CURRENTLY ACTIVE");
                    lore.add("&7▶ Click to &cdeactivate&7!");
                } else if (hasCosmetic) {
                    lore.add("&e&l★ OWNED");
                    lore.add("&7▶ Click to &aactivate&7!");
                } else {
                    lore.add("&c&l✗ LOCKED");
                    if (cosmetic.getPrice() > 0) {
                        lore.add("&7▶ Purchase to unlock!");
                    }
                }
                
                // Add requirements
                if (cosmetic.requiresPremium()) {
                    lore.add("");
                    lore.add("&5&l👑 Premium Required");
                } else if (cosmetic.requiresVIP()) {
                    lore.add("");
                    lore.add("&6&l⭐ VIP Required");
                }
                
                lore.add("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            gui.setItem(cosmeticSlots[i], item);
        }
        
        // Add back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName("&e&l← Back to Menu");
            backMeta.setLore(List.of("&7Return to the main cosmetics menu"));
            backButton.setItemMeta(backMeta);
        }
        gui.setItem(45, backButton);
        
        // Add clear all button
        ItemStack clearButton = new ItemStack(Material.TNT);
        ItemMeta clearMeta = clearButton.getItemMeta();
        if (clearMeta != null) {
            clearMeta.setDisplayName("&c&l🗑 Clear All Active");
            clearMeta.setLore(List.of(
                "&7Clear all active cosmetics",
                "&7of this type.",
                "",
                "&c&l⚠ Click to clear!"
            ));
            clearButton.setItemMeta(clearMeta);
        }
        gui.setItem(46, clearButton);
        
        // Add close button
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName("&c&l✗ Close Menu");
            closeMeta.setLore(List.of("&7Click to close this menu"));
            closeButton.setItemMeta(closeMeta);
        }
        gui.setItem(49, closeButton);
        
        player.openInventory(gui);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        
        String title = event.getView().getTitle();
        
        // Debug logging
        plugin.getLogger().info("GUI Click Debug - Title: '" + title + "' | Player: " + player.getName() + " | Slot: " + event.getSlot());
        
        // Fix the title check - remove color codes for comparison
        String cleanTitle = org.bukkit.ChatColor.stripColor(title);
        if (!cleanTitle.contains("Cosmetics") && !cleanTitle.contains("Particles") && !cleanTitle.contains("Hats") && 
            !cleanTitle.contains("Pets") && !cleanTitle.contains("Trails") && !cleanTitle.contains("Gadgets") && 
            !cleanTitle.contains("Wings") && !cleanTitle.contains("Auras") && !cleanTitle.contains("Shop") && 
            !cleanTitle.contains("Achievement")) {
            plugin.getLogger().info("GUI Click Debug - Title not matching, clean title: '" + cleanTitle + "'");
            return;
        }
        
        event.setCancelled(true);
        
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        
        // Ignore border items (black glass panes with no display name or empty display name)
        if (clickedItem.getType() == Material.BLACK_STAINED_GLASS_PANE || clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE) {
            ItemMeta borderMeta = clickedItem.getItemMeta();
            if (borderMeta == null || !borderMeta.hasDisplayName() || borderMeta.getDisplayName().equals("&0")) {
                return;
            }
        }
        
        // Handle main menu clicks
        String mainMenuTitle = color("&6&l✦ Cosmetics Menu ✦");
        plugin.getLogger().info("GUI Click Debug - Comparing titles: '" + title + "' vs '" + mainMenuTitle + "'");
        
        if (title.equals(mainMenuTitle)) {
            plugin.getLogger().info("GUI Click Debug - Main menu detected, handling click...");
            if (clickedItem.getType() == Material.BARRIER) {
                player.closeInventory();
                return;
            }
            
            // Check for shop button (slot 53)
            int slot = event.getSlot();
            if (slot == 53 && clickedItem.getType() == Material.EMERALD) {
                openShopGUI(player);
                return;
            }
            
            // Check specific slots for cosmetic types (more reliable than material matching)
            int[] slots = {19, 21, 23, 25, 28, 30, 32};
            CosmeticType[] types = CosmeticType.values();
            
            plugin.getLogger().info("GUI Click Debug - Checking slot " + slot + " against slots: " + java.util.Arrays.toString(slots));
            
            for (int i = 0; i < Math.min(types.length, slots.length); i++) {
                if (slot == slots[i]) {
                    CosmeticType type = types[i];
                    plugin.getLogger().info("GUI Click Debug - Slot " + slot + " matched, opening " + type.getDisplayName() + " GUI");
                    openTypeGUI(player, type);
                    return;
                }
            }
            
            // Fallback: Check by material and display name
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                String displayName = meta.getDisplayName();
                for (CosmeticType type : CosmeticType.values()) {
                    if (clickedItem.getType() == type.getIcon() && 
                        displayName.contains(type.getDisplayName())) {
                        openTypeGUI(player, type);
                        return;
                    }
                }
            }
        }
        
        // Handle credits shop clicks
        else if (title.contains("Shop")) {
            if (clickedItem.getType() == Material.BARRIER) {
                player.closeInventory();
                return;
            }
            
            if (clickedItem.getType() == Material.ARROW) {
                // Check if we're in a shop category, go back to shop
                if (title.contains("Shop") && !title.equals(color("&e&l⭐ Credits Shop ⭐"))) {
                    openShopGUI(player);
                } else {
                    openMainGUI(player);
                }
                return;
            }
            
            // Handle daily reward button (slot 49)
            int slot = event.getSlot();
            if (slot == 49 && clickedItem.getType() == Material.CLOCK) {
                plugin.getCreditManager().claimDailyBonus(player);
                player.closeInventory();
                return;
            }
            
            // Handle category clicks for shop
            String shopMenuTitle = color("&e&l⭐ Credits Shop ⭐");
            plugin.getLogger().info("GUI Click Debug - Shop title comparison: '" + title + "' vs '" + shopMenuTitle + "'");
            
            if (title.equals(shopMenuTitle)) {
                plugin.getLogger().info("GUI Click Debug - Shop menu detected, handling category click...");
                int[] slots = {19, 21, 23, 25, 28, 30, 32};
                CosmeticType[] types = CosmeticType.values();
                
                for (int i = 0; i < Math.min(types.length, slots.length); i++) {
                    if (slot == slots[i]) {
                        CosmeticType type = types[i];
                        openShopCategory(player, type);
                        return;
                    }
                }
            }
            
            // Handle cosmetic purchase in shop category
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                String displayName = meta.getDisplayName();
                
                // Find the cosmetic by display name
                for (Cosmetic cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
                    if (displayName.contains(cosmetic.getDisplayName())) {
                        handleCosmeticPurchase(player, cosmetic);
                        return;
                    }
                }
            }
        }
        
        // Handle type-specific menu clicks
        else {
            if (clickedItem.getType() == Material.BARRIER) {
                player.closeInventory();
                return;
            }
            
            if (clickedItem.getType() == Material.ARROW) {
                openMainGUI(player);
                return;
            }
            
            if (clickedItem.getType() == Material.TNT) {
                // Clear all active cosmetics of this type
                ItemMeta meta = clickedItem.getItemMeta();
                if (meta != null && meta.getDisplayName().contains("Clear All Active")) {
                    // Determine which type from the GUI title
                    for (CosmeticType type : CosmeticType.values()) {
                        if (title.contains(type.getDisplayName())) {
                            // Clear all active cosmetics of this type
                            for (Cosmetic cosmetic : plugin.getCosmeticManager().getCosmeticsByType(type)) {
                                if (plugin.getCosmeticManager().isCosmeticActive(player, cosmetic.getId())) {
                                    plugin.getCosmeticManager().deactivateCosmetic(player, cosmetic.getId());
                                }
                            }
                            plugin.getMessageManager().sendSuccess(player, "&7⊘ Cleared all active " + type.getDisplayName().toLowerCase());
                            openTypeGUI(player, type);
                            return;
                        }
                    }
                }
                return;
            }
            
            // Handle cosmetic clicks
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                String displayName = meta.getDisplayName();
                
                // Remove status icons and find the cosmetic
                String cleanName = displayName.replaceAll("&[0-9a-fA-F]", "").replaceAll("[✓★✗] ", "");
                
                // Find the cosmetic by cleaned display name
                for (Cosmetic cosmetic : plugin.getCosmeticManager().getAllCosmetics()) {
                    String cosmeticName = cosmetic.getDisplayName();
                    if (cleanName.contains(cosmeticName)) {
                        handleCosmeticClick(player, cosmetic);
                        
                        // Refresh the GUI
                        openTypeGUI(player, cosmetic.getType());
                        return;
                    }
                }
            }
        }
    }
    
    private void handleCosmeticClick(Player player, Cosmetic cosmetic) {
        // Check if player can access the cosmetic
        if (!cosmetic.canPlayerAccess(player)) {
            plugin.getMessageManager().sendError(player, "You cannot access this cosmetic: " + cosmetic.getAccessDeniedReason(player));
            return;
        }
        
        // Check if player owns the cosmetic (unless it's free or they have free access)
        boolean hasFreeAccess = player.hasPermission("sneakycosmetics.free");
        if (!cosmetic.isFree() && !hasFreeAccess && !plugin.getCosmeticManager().hasCosmetic(player, cosmetic.getId())) {
            plugin.getMessageManager().sendError(player, "You don't own this cosmetic! Purchase it first for " + cosmetic.getPrice() + " credits.");
            return;
        }
        
        // Toggle the cosmetic
        boolean success = plugin.getCosmeticManager().toggleCosmetic(player, cosmetic.getId());
        if (success) {
            boolean isActive = plugin.getCosmeticManager().isCosmeticActive(player, cosmetic.getId());
            if (isActive) {
                plugin.getMessageManager().sendSuccess(player, "&a✓ Activated: " + cosmetic.getDisplayName());
            } else {
                plugin.getMessageManager().sendSuccess(player, "&7⊘ Deactivated: " + cosmetic.getDisplayName());
            }
        } else {
            plugin.getMessageManager().sendError(player, "Failed to toggle cosmetic: " + cosmetic.getDisplayName());
        }
    }
}
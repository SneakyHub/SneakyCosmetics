package com.sneaky.cosmetics.gui;

import com.sneaky.cosmetics.SneakyCosmetics;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Manages all GUI interactions for the cosmetics plugin
 * Handles menu creation, navigation, and click events
 */
public class GUIManager implements Listener {
    
    private final SneakyCosmetics plugin;
    
    public GUIManager(SneakyCosmetics plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Open the main cosmetics GUI for a player
     */
    public void openMainGUI(Player player) {
        // TODO: Implement main GUI
        plugin.getMessageManager().sendInfo(player, "Main cosmetics GUI coming soon!");
    }
    
    /**
     * Open a specific cosmetic type GUI
     */
    public void openTypeGUI(Player player, String type) {
        // TODO: Implement type-specific GUI
        plugin.getMessageManager().sendInfo(player, "Cosmetic type GUI coming soon for: " + type);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // TODO: Handle GUI clicks
        if (event.getView().getTitle().contains("Cosmetics")) {
            event.setCancelled(true);
            // Handle click logic here
        }
    }
}
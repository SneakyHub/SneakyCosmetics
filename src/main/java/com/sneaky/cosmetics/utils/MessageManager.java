package com.sneaky.cosmetics.utils;

import com.sneaky.cosmetics.SneakyCosmetics;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles all message formatting and sending with support for:
 * - Legacy color codes (&c, &a, etc.)
 * - Hex colors (&#FF5555)
 * - MiniMessage format
 * - Placeholder replacement
 */
public class MessageManager {
    
    private final SneakyCosmetics plugin;
    private FileConfiguration messageConfig;
    private final MiniMessage miniMessage;
    private final LegacyComponentSerializer legacySerializer;
    
    // Color patterns
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern LEGACY_PATTERN = Pattern.compile("&([0-9a-fk-or])");
    
    public MessageManager(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
        this.legacySerializer = LegacyComponentSerializer.legacyAmpersand();
        loadMessages();
    }
    
    private void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        
        this.messageConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }
    
    public void reload() {
        loadMessages();
    }
    
    /**
     * Get a message from the configuration
     */
    public String getMessage(String path) {
        return messageConfig.getString(path, "&cMessage not found: " + path);
    }
    
    /**
     * Get a message with placeholder replacement
     */
    public String getMessage(String path, Map<String, String> placeholders) {
        String message = getMessage(path);
        return replacePlaceholders(message, placeholders);
    }
    
    /**
     * Get a list of messages from the configuration
     */
    public List<String> getMessageList(String path) {
        return messageConfig.getStringList(path);
    }
    
    /**
     * Replace placeholders in a message
     */
    public String replacePlaceholders(String message, Map<String, String> placeholders) {
        if (placeholders == null || placeholders.isEmpty()) {
            return message;
        }
        
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        
        return message;
    }
    
    /**
     * Format a message with colors and effects
     */
    public String formatMessage(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        
        // Convert hex colors first
        message = convertHexColors(message);
        
        // Convert legacy colors
        message = ChatColor.translateAlternateColorCodes('&', message);
        
        return message;
    }
    
    /**
     * Convert hex color codes (&#FF5555) to Bukkit format
     */
    private String convertHexColors(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String hexCode = matcher.group(1);
            try {
                // Convert to Bukkit's hex format if supported
                if (supportsHex()) {
                    matcher.appendReplacement(result, net.md_5.bungee.api.ChatColor.of("#" + hexCode).toString());
                } else {
                    // Fallback to closest legacy color
                    matcher.appendReplacement(result, getClosestLegacyColor(hexCode));
                }
            } catch (Exception e) {
                // If hex parsing fails, remove the hex code
                matcher.appendReplacement(result, "");
            }
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
    
    /**
     * Check if the server supports hex colors
     */
    private boolean supportsHex() {
        try {
            Class.forName("net.md_5.bungee.api.ChatColor");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * Get the closest legacy color for a hex code
     */
    private String getClosestLegacyColor(String hexCode) {
        // Simple mapping of hex ranges to legacy colors
        int r = Integer.parseInt(hexCode.substring(0, 2), 16);
        int g = Integer.parseInt(hexCode.substring(2, 4), 16);
        int b = Integer.parseInt(hexCode.substring(4, 6), 16);
        
        // Determine the dominant color component
        if (r > g && r > b) {
            return r > 128 ? "&c" : "&4"; // Red
        } else if (g > r && g > b) {
            return g > 128 ? "&a" : "&2"; // Green
        } else if (b > r && b > g) {
            return b > 128 ? "&9" : "&1"; // Blue
        } else if (r > 200 && g > 200 && b > 200) {
            return "&f"; // White
        } else if (r < 64 && g < 64 && b < 64) {
            return "&0"; // Black
        } else {
            return "&7"; // Gray
        }
    }
    
    /**
     * Send a formatted message to a command sender
     */
    public void sendMessage(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        
        String prefix = formatMessage(getMessage("prefix"));
        String formattedMessage = formatMessage(message);
        
        sender.sendMessage(prefix + formattedMessage);
    }
    
    /**
     * Send a message with placeholders to a command sender
     */
    public void sendMessage(CommandSender sender, String messagePath, Map<String, String> placeholders) {
        String message = getMessage(messagePath, placeholders);
        sendMessage(sender, message);
    }
    
    /**
     * Send a message from config path to a command sender
     */
    public void sendConfigMessage(CommandSender sender, String configPath) {
        String message = getMessage(configPath);
        sendMessage(sender, message);
    }
    
    /**
     * Send a message from config path with placeholders to a command sender
     */
    public void sendConfigMessage(CommandSender sender, String configPath, Map<String, String> placeholders) {
        String message = getMessage(configPath, placeholders);
        sendMessage(sender, message);
    }
    
    /**
     * Send multiple lines to a command sender
     */
    public void sendMessages(CommandSender sender, List<String> messages) {
        for (String message : messages) {
            sendMessage(sender, message);
        }
    }
    
    /**
     * Send a list from config to a command sender
     */
    public void sendConfigMessages(CommandSender sender, String configPath) {
        List<String> messages = getMessageList(configPath);
        sendMessages(sender, messages);
    }
    
    /**
     * Create a placeholder map with common placeholders
     */
    public Map<String, String> createPlaceholders() {
        return new HashMap<>();
    }
    
    /**
     * Create a placeholder map with player-specific placeholders
     */
    public Map<String, String> createPlaceholders(Player player) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", player.getName());
        placeholders.put("uuid", player.getUniqueId().toString());
        placeholders.put("world", player.getWorld().getName());
        return placeholders;
    }
    
    /**
     * Add a placeholder to an existing map
     */
    public Map<String, String> addPlaceholder(Map<String, String> placeholders, String key, String value) {
        if (placeholders == null) {
            placeholders = new HashMap<>();
        }
        placeholders.put(key, value);
        return placeholders;
    }
    
    /**
     * Add multiple placeholders to an existing map
     */
    public Map<String, String> addPlaceholders(Map<String, String> placeholders, Map<String, String> newPlaceholders) {
        if (placeholders == null) {
            placeholders = new HashMap<>();
        }
        placeholders.putAll(newPlaceholders);
        return placeholders;
    }
    
    /**
     * Send a success message
     */
    public void sendSuccess(CommandSender sender, String message) {
        sendMessage(sender, "&a" + message);
    }
    
    /**
     * Send an error message
     */
    public void sendError(CommandSender sender, String message) {
        sendMessage(sender, "&c" + message);
    }
    
    /**
     * Send a warning message
     */
    public void sendWarning(CommandSender sender, String message) {
        sendMessage(sender, "&e" + message);
    }
    
    /**
     * Send an info message
     */
    public void sendInfo(CommandSender sender, String message) {
        sendMessage(sender, "&b" + message);
    }
    
    /**
     * Format a message for use in GUI items
     */
    public String formatForGUI(String message) {
        return formatMessage(message);
    }
    
    /**
     * Format a list of messages for use in GUI lore
     */
    public List<String> formatLoreForGUI(List<String> lore) {
        return lore.stream()
                .map(this::formatForGUI)
                .toList();
    }
    
    /**
     * Get the prefix for messages
     */
    public String getPrefix() {
        return formatMessage(getMessage("prefix"));
    }
    
    /**
     * Strip all color codes from a message
     */
    public String stripColors(String message) {
        if (message == null) {
            return "";
        }
        
        // Remove hex colors
        message = HEX_PATTERN.matcher(message).replaceAll("");
        
        // Remove legacy colors
        message = ChatColor.stripColor(message);
        
        return message;
    }
}
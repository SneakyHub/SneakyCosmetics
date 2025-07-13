package com.sneaky.cosmetics.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sneaky.cosmetics.SneakyCosmetics;
import okhttp3.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Handles checking for updates from GitHub and auto-updating the plugin
 * Uses the latest.json file to determine current version information
 */
public class UpdateChecker {
    
    private final SneakyCosmetics plugin;
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final String updateUrl;
    
    // Version information
    private String currentVersion;
    private String latestVersion;
    private String downloadUrl;
    private boolean updateAvailable;
    
    public UpdateChecker(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.gson = new Gson();
        this.currentVersion = plugin.getDescription().getVersion();
        this.updateUrl = plugin.getConfig().getString("admin.update-url", 
                "https://raw.githubusercontent.com/SneakyHub/SneakyCosmetics/main/latest.json");
        
        // Configure HTTP client with timeouts
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }
    
    /**
     * Check for updates asynchronously
     */
    public CompletableFuture<Boolean> checkForUpdates() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                plugin.getLogger().info("Checking for updates...");
                
                Request request = new Request.Builder()
                        .url(updateUrl)
                        .addHeader("User-Agent", "SneakyCosmetics/" + currentVersion)
                        .addHeader("Accept", "application/json")
                        .build();
                
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        plugin.getLogger().warning("Failed to check for updates: HTTP " + response.code());
                        return false;
                    }
                    
                    String responseBody = response.body().string();
                    JsonObject updateInfo = gson.fromJson(responseBody, JsonObject.class);
                    
                    return parseUpdateInfo(updateInfo);
                }
                
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Network error while checking for updates", e);
                return false;
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Error checking for updates", e);
                return false;
            }
        });
    }
    
    /**
     * Parse update information from JSON response
     */
    private boolean parseUpdateInfo(JsonObject updateInfo) {
        try {
            this.latestVersion = updateInfo.get("version").getAsString();
            this.downloadUrl = updateInfo.get("download_url").getAsString();
            
            // Compare versions
            this.updateAvailable = isNewerVersion(latestVersion, currentVersion);
            
            if (updateAvailable) {
                plugin.getLogger().info("Update available! Current: " + currentVersion + " -> Latest: " + latestVersion);
                
                // Notify admins if configured
                if (plugin.getConfig().getBoolean("admin.notify-updates", true)) {
                    notifyAdmins();
                }
                
                // Auto-update if enabled
                if (plugin.getConfig().getBoolean("admin.auto-update", false)) {
                    downloadUpdate();
                }
            } else {
                plugin.getLogger().info("No updates available. Current version: " + currentVersion);
            }
            
            return updateAvailable;
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error parsing update information", e);
            return false;
        }
    }
    
    /**
     * Compare two version strings to determine if first is newer than second
     */
    private boolean isNewerVersion(String version1, String version2) {
        try {
            String[] v1Parts = version1.replace("-SNAPSHOT", "").split("\\.");
            String[] v2Parts = version2.replace("-SNAPSHOT", "").split("\\.");
            
            int maxLength = Math.max(v1Parts.length, v2Parts.length);
            
            for (int i = 0; i < maxLength; i++) {
                int v1Part = i < v1Parts.length ? Integer.parseInt(v1Parts[i]) : 0;
                int v2Part = i < v2Parts.length ? Integer.parseInt(v2Parts[i]) : 0;
                
                if (v1Part > v2Part) {
                    return true;
                } else if (v1Part < v2Part) {
                    return false;
                }
            }
            
            return false; // Versions are equal
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Error comparing versions: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Notify online admins about available updates
     */
    private void notifyAdmins() {
        plugin.getSchedulerAdapter().runTask(() -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (player.hasPermission("sneakycosmetics.admin")) {
                    Map<String, String> placeholders = plugin.getMessageManager().createPlaceholders(player);
                    placeholders = plugin.getMessageManager().addPlaceholder(placeholders, "current", currentVersion);
                    placeholders = plugin.getMessageManager().addPlaceholder(placeholders, "latest", latestVersion);
                    plugin.getMessageManager().sendConfigMessage(player, "updates.available", placeholders);
                }
            }
        });
    }
    
    /**
     * Download the latest update
     */
    public CompletableFuture<Boolean> downloadUpdate() {
        if (!updateAvailable || downloadUrl == null) {
            return CompletableFuture.completedFuture(false);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                plugin.getLogger().info("Starting download of version " + latestVersion + "...");
                
                Request request = new Request.Builder()
                        .url(downloadUrl)
                        .addHeader("User-Agent", "SneakyCosmetics/" + currentVersion)
                        .build();
                
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        plugin.getLogger().warning("Failed to download update: HTTP " + response.code());
                        return false;
                    }
                    
                    // Get the updates directory
                    File updatesDir = new File(plugin.getServer().getUpdateFolderFile(), "");
                    if (!updatesDir.exists()) {
                        updatesDir.mkdirs();
                    }
                    
                    // Create the new file
                    String fileName = "SneakyCosmetics-" + latestVersion + ".jar";
                    File updateFile = new File(updatesDir, fileName);
                    
                    // Download the file
                    try (InputStream inputStream = response.body().byteStream();
                         FileOutputStream outputStream = new FileOutputStream(updateFile)) {
                        
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        long totalBytes = 0;
                        
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                            totalBytes += bytesRead;
                        }
                        
                        plugin.getLogger().info("Downloaded " + totalBytes + " bytes to " + updateFile.getName());
                        
                        // Notify admins
                        notifyDownloadComplete();
                        
                        return true;
                    }
                }
                
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Error downloading update", e);
                notifyDownloadFailed(e.getMessage());
                return false;
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Unexpected error during update download", e);
                notifyDownloadFailed(e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * Notify admins that download completed successfully
     */
    private void notifyDownloadComplete() {
        plugin.getSchedulerAdapter().runTask(() -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (player.hasPermission("sneakycosmetics.admin")) {
                    plugin.getMessageManager().sendConfigMessage(player, "updates.download-success");
                }
            }
        });
    }
    
    /**
     * Notify admins that download failed
     */
    private void notifyDownloadFailed(String error) {
        plugin.getSchedulerAdapter().runTask(() -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (player.hasPermission("sneakycosmetics.admin")) {
                    plugin.getMessageManager().sendConfigMessage(player, "updates.download-failed",
                            plugin.getMessageManager().addPlaceholder(
                                    plugin.getMessageManager().createPlaceholders(player),
                                    "error", error
                            ));
                }
            }
        });
    }
    
    /**
     * Force check for updates (for command usage)
     */
    public void forceCheckForUpdates(Player player) {
        plugin.getMessageManager().sendConfigMessage(player, "updates.checking");
        
        checkForUpdates().thenAccept(hasUpdate -> {
            plugin.getSchedulerAdapter().runTask(() -> {
                if (hasUpdate) {
                    Map<String, String> placeholders2 = plugin.getMessageManager().createPlaceholders(player);
                    placeholders2 = plugin.getMessageManager().addPlaceholder(placeholders2, "current", currentVersion);
                    placeholders2 = plugin.getMessageManager().addPlaceholder(placeholders2, "latest", latestVersion);
                    plugin.getMessageManager().sendConfigMessage(player, "updates.available", placeholders2);
                } else {
                    plugin.getMessageManager().sendConfigMessage(player, "updates.no-updates");
                }
            });
        });
    }
    
    /**
     * Force download update (for command usage)
     */
    public void forceDownloadUpdate(Player player) {
        if (!updateAvailable) {
            plugin.getMessageManager().sendConfigMessage(player, "updates.no-updates");
            return;
        }
        
        if (!plugin.getConfig().getBoolean("admin.auto-update", false)) {
            plugin.getMessageManager().sendConfigMessage(player, "updates.auto-update-disabled");
            return;
        }
        
        plugin.getMessageManager().sendConfigMessage(player, "updates.download-started",
                plugin.getMessageManager().addPlaceholder(
                        plugin.getMessageManager().createPlaceholders(player),
                        "version", latestVersion
                ));
        
        downloadUpdate().thenAccept(success -> {
            plugin.getSchedulerAdapter().runTask(() -> {
                if (success) {
                    plugin.getMessageManager().sendConfigMessage(player, "updates.download-success");
                } else {
                    plugin.getMessageManager().sendConfigMessage(player, "updates.download-failed",
                            plugin.getMessageManager().addPlaceholder(
                                    plugin.getMessageManager().createPlaceholders(player),
                                    "error", "Unknown error"
                            ));
                }
            });
        });
    }
    
    // Getters
    public String getCurrentVersion() {
        return currentVersion;
    }
    
    public String getLatestVersion() {
        return latestVersion;
    }
    
    public String getDownloadUrl() {
        return downloadUrl;
    }
    
    public boolean isUpdateAvailable() {
        return updateAvailable;
    }
    
    /**
     * Cleanup resources
     */
    public void shutdown() {
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
            httpClient.connectionPool().evictAll();
        }
    }
}
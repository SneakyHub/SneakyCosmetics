package com.sneaky.cosmetics;

import com.sneaky.cosmetics.achievements.AchievementManager;
import com.sneaky.cosmetics.api.SneakyCosmeticsAPI;
import com.sneaky.cosmetics.commands.CosmeticsCommand;
import com.sneaky.cosmetics.commands.CreditsCommand;
import com.sneaky.cosmetics.database.DatabaseManager;
import com.sneaky.cosmetics.gui.GUIManager;
import com.sneaky.cosmetics.integrations.CMIIntegration;
import com.sneaky.cosmetics.integrations.EssentialsXIntegration;
import com.sneaky.cosmetics.integrations.LuckPermsIntegration;
import com.sneaky.cosmetics.integrations.PlaceholderAPIIntegration;
import com.sneaky.cosmetics.integrations.VaultIntegration;
import com.sneaky.cosmetics.listeners.PlayerListener;
import com.sneaky.cosmetics.managers.*;
import com.sneaky.cosmetics.utils.MessageManager;
import com.sneaky.cosmetics.utils.SchedulerAdapter;
import com.sneaky.cosmetics.utils.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import com.sneaky.cosmetics.cosmetics.wings.WingCosmetic;

import java.util.logging.Level;

/**
 * SneakyCosmetics - A comprehensive cosmetics plugin for Minecraft 1.21.7
 * Features: Credits system, GUI, 80+ cosmetics, Vault/LuckPerms integration
 * 
 * @author SneakyHub
 * @version 1.1.0
 */
public class SneakyCosmetics extends JavaPlugin {

    // bStats plugin ID
    private static final int BSTATS_PLUGIN_ID = 26487;
    
    private static SneakyCosmetics instance;
    
    // Core managers
    private DatabaseManager databaseManager;
    private MessageManager messageManager;
    private SchedulerAdapter schedulerAdapter;
    private UpdateChecker updateChecker;
    
    // Feature managers
    private CreditManager creditManager;
    private CosmeticManager cosmeticManager;
    private GUIManager guiManager;
    private AchievementManager achievementManager;
    private StatisticsManager statisticsManager;
    
    // Cosmetic type managers
    private ParticleManager particleManager;
    private HatManager hatManager;
    private PetManager petManager;
    private TrailManager trailManager;
    private GadgetManager gadgetManager;
    private WingManager wingManager;
    private AuraManager auraManager;
    
    // Integration managers
    private VaultIntegration vaultIntegration;
    private LuckPermsIntegration luckPermsIntegration;
    private EssentialsXIntegration essentialsXIntegration;
    private PlaceholderAPIIntegration placeholderAPIIntegration;
    private CMIIntegration cmiIntegration;
    
    // Metrics
    private Metrics metrics;

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize scheduler adapter for Folia/Paper compatibility
        this.schedulerAdapter = new SchedulerAdapter(this);
        
        // Save default configuration files
        saveDefaultConfig();
        saveResource("messages.yml", false);
        
        // Initialize message manager first
        try {
            this.messageManager = new MessageManager(this);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to initialize message manager!", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Initialize database
        try {
            this.databaseManager = new DatabaseManager(this);
            this.databaseManager.initialize();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to initialize database!", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Initialize integrations
        initializeIntegrations();
        
        // Initialize managers
        initializeManagers();
        
        // Register commands
        registerCommands();
        
        // Register listeners
        registerListeners();
        
        // Start background tasks
        startBackgroundTasks();
        
        // Initialize bStats metrics (ID: 26487)
        if (getConfig().getBoolean("admin.metrics", true)) {
            try {
                this.metrics = new Metrics(this, 26487);
                setupMetrics();
                getLogger().info("bStats metrics enabled with ID: 26487");
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Failed to initialize bStats metrics", e);
            }
        }
        
        // Initialize update checker
        if (getConfig().getBoolean("admin.check-for-updates", true)) {
            try {
                this.updateChecker = new UpdateChecker(this);
                schedulerAdapter.runTaskAsynchronously(() -> updateChecker.checkForUpdates());
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Failed to initialize update checker", e);
            }
        }
        
        // Startup message
        getLogger().info("SneakyCosmetics v" + getDescription().getVersion() + " has been enabled!");
        getLogger().info("Running on " + (schedulerAdapter.isFolia() ? "Folia" : "Paper/Spigot"));
        
        if (cosmeticManager != null) {
            getLogger().info("Loaded " + cosmeticManager.getTotalCosmetics() + " cosmetics across " + 
                            cosmeticManager.getCosmeticTypes().size() + " categories");
        }
    }

    @Override
    public void onDisable() {
        // Save all player data
        if (creditManager != null) {
            creditManager.saveAllPlayerData();
        }
        
        // Shutdown cosmetic manager
        if (cosmeticManager != null) {
            cosmeticManager.shutdown();
        }
        
        // Stop all background tasks
        if (particleManager != null) particleManager.stopAllTasks();
        if (trailManager != null) trailManager.stopAllTasks();
        if (petManager != null) petManager.stopAllTasks();
        if (wingManager != null) wingManager.stopAllTasks();
        if (auraManager != null) auraManager.stopAllTasks();
        if (gadgetManager != null) gadgetManager.stopAllTasks();
        
        // Close database connections
        if (databaseManager != null) {
            databaseManager.close();
        }
        
        // Cleanup update checker
        if (updateChecker != null) {
            updateChecker.shutdown();
        }
        
        // Cleanup PlaceholderAPI
        if (placeholderAPIIntegration != null) {
            placeholderAPIIntegration.unregister();
        }
        
        // Cleanup pets
        com.sneaky.cosmetics.cosmetics.pets.PetCosmetic.cleanupAllPets();
        
        // Cleanup gadgets
        com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic.cleanupAllGadgets();
        
        // Cleanup wings
        com.sneaky.cosmetics.cosmetics.wings.WingCosmetic.cleanupAllWings();
        
        // Cleanup auras
        com.sneaky.cosmetics.cosmetics.auras.AuraCosmetic.cleanupAllAuras();
        
        // Cancel all running tasks
        getServer().getScheduler().cancelTasks(this);
        
        getLogger().info("SneakyCosmetics has been disabled!");
    }
    
    private void initializeIntegrations() {
        // Initialize Vault integration
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            this.vaultIntegration = new VaultIntegration(this);
            if (vaultIntegration.setupEconomy()) {
                getLogger().info("Successfully hooked into Vault economy!");
            }
        } else {
            getLogger().warning("Vault not found! Economy features will be disabled.");
        }
        
        // Initialize LuckPerms integration
        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            this.luckPermsIntegration = new LuckPermsIntegration(this);
            if (luckPermsIntegration.initialize()) {
                getLogger().info("Successfully hooked into LuckPerms!");
            }
        } else {
            getLogger().warning("LuckPerms not found! Some features may be limited.");
        }
        
        // Initialize EssentialsX integration
        if (getServer().getPluginManager().getPlugin("Essentials") != null || 
            getServer().getPluginManager().getPlugin("EssentialsX") != null) {
            this.essentialsXIntegration = new EssentialsXIntegration(this);
            if (essentialsXIntegration.initialize()) {
                getLogger().info("Successfully hooked into EssentialsX!");
            }
        }
        
        // Initialize PlaceholderAPI integration
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.placeholderAPIIntegration = new PlaceholderAPIIntegration(this);
            if (placeholderAPIIntegration.register()) {
                getLogger().info("Successfully hooked into PlaceholderAPI!");
            }
        } else {
            getLogger().warning("PlaceholderAPI not found! Placeholder features will be disabled.");
        }
        
        // Initialize CMI integration
        if (getServer().getPluginManager().getPlugin("CMI") != null) {
            this.cmiIntegration = new CMIIntegration(this);
            if (cmiIntegration.initialize()) {
                getLogger().info("Successfully hooked into CMI!");
            }
        } else {
            getLogger().info("CMI not found, CMI integration disabled.");
        }
    }
    
    private void initializeManagers() {
        // Initialize core managers
        this.creditManager = new CreditManager(this);
        this.cosmeticManager = new CosmeticManager(this);
        this.guiManager = new GUIManager(this);
        this.achievementManager = new AchievementManager(this);
        this.statisticsManager = new StatisticsManager(this);
        
        // Initialize cosmetic type managers
        this.particleManager = new ParticleManager(this);
        this.hatManager = new HatManager(this);
        this.petManager = new PetManager(this);
        this.trailManager = new TrailManager(this);
        this.gadgetManager = new GadgetManager(this);
        this.wingManager = new WingManager(this);
        this.auraManager = new AuraManager(this);
        
        // Initialize cosmetics
        cosmeticManager.initialize();
        
        // Initialize public API
        SneakyCosmeticsAPI.initialize(this);
    }
    
    private void registerCommands() {
        if (getCommand("cosmetics") != null) {
            getCommand("cosmetics").setExecutor(new CosmeticsCommand(this));
        } else {
            getLogger().warning("Failed to register /cosmetics command - command not found in plugin.yml");
        }
        
        if (getCommand("credits") != null) {
            getCommand("credits").setExecutor(new CreditsCommand(this));
        } else {
            getLogger().warning("Failed to register /credits command - command not found in plugin.yml");
        }
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        // Register GUI listener
        getServer().getPluginManager().registerEvents(guiManager, this);
    }
    
    private void startBackgroundTasks() {
        // Cosmetic effect tasks are now handled by CosmeticCleanupManager
        // Start individual manager tasks for backward compatibility
        particleManager.startParticleTask();
        trailManager.startTrailTask();
        petManager.startPetTask();
        wingManager.startWingTask();
        auraManager.startAuraTask();
        
        // Start periodic save task
        long saveInterval = getConfig().getLong("admin.auto-save-interval", 5) * 60 * 20L; // Convert minutes to ticks
        if (saveInterval > 0) {
            schedulerAdapter.runTaskTimerAsynchronously(() -> {
                if (creditManager != null) {
                    creditManager.saveAllPlayerData();
                }
            }, saveInterval, saveInterval);
        }
        
        // Cleanup task is now handled by CosmeticCleanupManager automatically
        // No need for separate cleanup task scheduling
    }
    
    private void setupMetrics() {
        try {
            // bStats metrics initialized successfully
            getLogger().info("bStats metrics enabled with ID: " + BSTATS_PLUGIN_ID);
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Failed to setup bStats custom charts", e);
        }
    }
    
    public void reload() {
        // Reload configuration
        reloadConfig();
        
        // Reload database configuration
        databaseManager.reloadDatabaseConfig();
        
        // Reload message manager
        messageManager.reload();
        
        // Reload cosmetic manager
        cosmeticManager.reload();
        
        getLogger().info("Configuration reloaded successfully!");
    }
    
    // Getters for managers and integrations
    public static SneakyCosmetics getInstance() {
        return instance;
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public MessageManager getMessageManager() {
        return messageManager;
    }
    
    public SchedulerAdapter getSchedulerAdapter() {
        return schedulerAdapter;
    }
    
    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }
    
    public CreditManager getCreditManager() {
        return creditManager;
    }
    
    public CosmeticManager getCosmeticManager() {
        return cosmeticManager;
    }
    
    public GUIManager getGUIManager() {
        return guiManager;
    }
    
    public ParticleManager getParticleManager() {
        return particleManager;
    }
    
    public HatManager getHatManager() {
        return hatManager;
    }
    
    public PetManager getPetManager() {
        return petManager;
    }
    
    public TrailManager getTrailManager() {
        return trailManager;
    }
    
    public GadgetManager getGadgetManager() {
        return gadgetManager;
    }
    
    public WingManager getWingManager() {
        return wingManager;
    }
    
    public AuraManager getAuraManager() {
        return auraManager;
    }
    
    public VaultIntegration getVaultIntegration() {
        return vaultIntegration;
    }
    
    public LuckPermsIntegration getLuckPermsIntegration() {
        return luckPermsIntegration;
    }
    
    public EssentialsXIntegration getEssentialsXIntegration() {
        return essentialsXIntegration;
    }
    
    public PlaceholderAPIIntegration getPlaceholderAPIIntegration() {
        return placeholderAPIIntegration;
    }
    
    public AchievementManager getAchievementManager() {
        return achievementManager;
    }
    
    public CMIIntegration getCMIIntegration() {
        return cmiIntegration;
    }
    
    public StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }
}
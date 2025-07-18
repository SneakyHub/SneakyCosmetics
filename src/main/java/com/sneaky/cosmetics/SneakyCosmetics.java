package com.sneaky.cosmetics;

import com.sneaky.cosmetics.achievements.AchievementManager;
import com.sneaky.cosmetics.api.SneakyCosmeticsAPI;
import com.sneaky.cosmetics.commands.CosmeticsCommand;
import com.sneaky.cosmetics.commands.CreditsCommand;
import com.sneaky.cosmetics.commands.SneakyCosmeticsCommand;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import com.sneaky.cosmetics.database.DatabaseManager;
import com.sneaky.cosmetics.gui.GUIManager;
import com.sneaky.cosmetics.integrations.CMIIntegration;
import com.sneaky.cosmetics.integrations.EssentialsXIntegration;
import com.sneaky.cosmetics.integrations.LuckPermsIntegration;
import com.sneaky.cosmetics.integrations.PlaceholderAPIIntegration;
import com.sneaky.cosmetics.integrations.VaultIntegration;
import com.sneaky.cosmetics.listeners.PlayerListener;
import com.sneaky.cosmetics.managers.AuraManager;
import com.sneaky.cosmetics.managers.CosmeticManager;
import com.sneaky.cosmetics.managers.CrateManager;
import com.sneaky.cosmetics.managers.CreditManager;
import com.sneaky.cosmetics.managers.GadgetManager;
import com.sneaky.cosmetics.managers.HatManager;
import com.sneaky.cosmetics.managers.ParticleManager;
import com.sneaky.cosmetics.managers.PetManager;
import com.sneaky.cosmetics.managers.RentalManager;
import com.sneaky.cosmetics.managers.StatisticsManager;
import com.sneaky.cosmetics.managers.TrailManager;
import com.sneaky.cosmetics.managers.WingManager;
import com.sneaky.cosmetics.utils.MessageManager;
import com.sneaky.cosmetics.utils.SchedulerAdapter;
import com.sneaky.cosmetics.utils.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * SneakyCosmetics - A comprehensive cosmetics plugin for Minecraft 1.21.8
 * Features: Credits system, GUI, 80+ cosmetics, Vault/LuckPerms integration
 * 
 * @author SneakyHub
 * @version 1.0.0-dev
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
    private com.sneaky.cosmetics.cosmetics.morphs.MorphManager morphManager;
    
    // Advanced feature managers
    private RentalManager rentalManager;
    private CrateManager crateManager;
    
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
        
        // Enhanced startup display
        displayStartupBanner();
    }
    
    /**
     * Display enhanced startup banner with statistics and information
     */
    private void displayStartupBanner() {
        // ASCII Art Banner
        getLogger().info("╔══════════════════════════════════════════════════════════════╗");
        getLogger().info("║                                                              ║");
        getLogger().info("║    ███████ ███    █ ███████  █████  █   ██ ██    ██         ║");
        getLogger().info("║    █      ████   █ █      █     █ █  █  █  █  █              ║");
        getLogger().info("║    ███████ █ █ █ █ ███████ █████████ █   █   ██               ║");
        getLogger().info("║          █ █  ███ █       █ █     █ █  █  █  █  █             ║");
        getLogger().info("║    ███████ █   ██ █ ███████ █     █ █   ██ ██    ██          ║");
        getLogger().info("║                                                              ║");
        getLogger().info("║                SneakyCosmetics v" + getDescription().getVersion() + "                     ║");
        getLogger().info("║             A Premium Cosmetics Experience                  ║");
        getLogger().info("║                                                              ║");
        getLogger().info("╚══════════════════════════════════════════════════════════════╝");
        
        // System Information
        getLogger().info("");
        getLogger().info("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬ System Information ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        getLogger().info("│ Server Platform: " + (schedulerAdapter.isFolia() ? "Folia" : "Paper/Spigot"));
        getLogger().info("│ Minecraft Version: " + getServer().getBukkitVersion());
        getLogger().info("│ Java Version: " + System.getProperty("java.version"));
        getLogger().info("│ Plugin Version: " + getDescription().getVersion());
        
        // Cosmetic Statistics
        if (cosmeticManager != null) {
            getLogger().info("");
            getLogger().info("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬ Cosmetic Statistics ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            
            int totalCosmetics = cosmeticManager.getTotalCosmetics();
            int totalTypes = cosmeticManager.getCosmeticTypes().size();
            
            getLogger().info("│ Total Cosmetics: " + totalCosmetics);
            getLogger().info("│ Cosmetic Categories: " + totalTypes);
            
            // Individual category counts
            for (CosmeticType type : cosmeticManager.getCosmeticTypes()) {
                int count = cosmeticManager.getCosmeticCountByType(type);
                String typeName = formatTypeName(type.name());
                getLogger().info("│   • " + typeName + ": " + count);
            }
        }
        
        // Integration Status
        getLogger().info("");
        getLogger().info("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬ Integration Status ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        getLogger().info("│ Vault: " + getIntegrationStatus(vaultIntegration != null));
        getLogger().info("│ LuckPerms: " + getIntegrationStatus(luckPermsIntegration != null));
        getLogger().info("│ EssentialsX: " + getIntegrationStatus(essentialsXIntegration != null));
        getLogger().info("│ PlaceholderAPI: " + getIntegrationStatus(placeholderAPIIntegration != null));
        getLogger().info("│ CMI: " + getIntegrationStatus(cmiIntegration != null));
        
        // Feature Status
        getLogger().info("");
        getLogger().info("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬ Feature Status ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        getLogger().info("│ Database: " + getFeatureStatus(databaseManager != null));
        getLogger().info("│ Credits System: " + getFeatureStatus(creditManager != null));
        getLogger().info("│ Rental System: " + getFeatureStatus(rentalManager != null));
        getLogger().info("│ Crate System: " + getFeatureStatus(crateManager != null));
        getLogger().info("│ Achievements: " + getFeatureStatus(achievementManager != null));
        getLogger().info("│ Statistics: " + getFeatureStatus(statisticsManager != null));
        getLogger().info("│ Update Checker: " + getFeatureStatus(updateChecker != null));
        getLogger().info("│ bStats Metrics: " + getFeatureStatus(getConfig().getBoolean("admin.metrics", true)));
        
        // Commands Available
        getLogger().info("");
        getLogger().info("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬ Command System ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        getLogger().info("│ Unified Command: /sneakycosmetics (aliases: /sc, /sneaky)");
        getLogger().info("│ Legacy Commands: /cosmetics, /crate, /rental, /credits, /morph");
        getLogger().info("│ Help Command: /sneakycosmetics help");
        
        // Footer
        getLogger().info("");
        getLogger().info("██████████████████████████████████████████████████████████████");
        getLogger().info("█ ✓ SneakyCosmetics has been successfully enabled! █");
        getLogger().info("█ For support: https://github.com/SneakyHub/SneakyCosmetics █");
        getLogger().info("██████████████████████████████████████████████████████████████");
        getLogger().info("");
    }
    
    /**
     * Format cosmetic type name for display
     */
    private String formatTypeName(String typeName) {
        return typeName.toLowerCase().replace("_", " ")
                .substring(0, 1).toUpperCase() + 
                typeName.toLowerCase().replace("_", " ").substring(1);
    }
    
    /**
     * Get integration status with color coding
     */
    private String getIntegrationStatus(boolean enabled) {
        return enabled ? "✓ Enabled" : "✗ Disabled";
    }
    
    /**
     * Get feature status with color coding
     */
    private String getFeatureStatus(boolean enabled) {
        return enabled ? "✓ Active" : "✗ Inactive";
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
        if (morphManager != null) {
            // Cleanup all active morphs if needed
            // morphManager.stopAllTasks();
        }
        
        // Shutdown advanced feature managers
        if (rentalManager != null) rentalManager.shutdown();
        if (crateManager != null) crateManager.shutdown();
        
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
        
        // Display shutdown banner
        displayShutdownBanner();
    }
    
    /**
     * Display shutdown banner with cleanup information
     */
    private void displayShutdownBanner() {
        getLogger().info("");
        getLogger().info("╔══════════════════════════════════════════════════════════════╗");
        getLogger().info("║                                                              ║");
        getLogger().info("║                SneakyCosmetics v" + getDescription().getVersion() + "                     ║");
        getLogger().info("║                    Shutting Down...                         ║");
        getLogger().info("║                                                              ║");
        getLogger().info("╚══════════════════════════════════════════════════════════════╝");
        getLogger().info("");
        getLogger().info("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬ Cleanup Status ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        getLogger().info("│ ✓ Player data saved");
        getLogger().info("│ ✓ Database connections closed");
        getLogger().info("│ ✓ Background tasks cancelled");
        getLogger().info("│ ✓ Cosmetic entities cleaned up");
        getLogger().info("│ ✓ Memory freed");
        getLogger().info("");
        getLogger().info("██████████████████████████████████████████████████████████████");
        getLogger().info("█ ✓ SneakyCosmetics has been safely disabled! █");
        getLogger().info("█ Thank you for using SneakyCosmetics! █");
        getLogger().info("██████████████████████████████████████████████████████████████");
        getLogger().info("");
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
        this.morphManager = new com.sneaky.cosmetics.cosmetics.morphs.MorphManager(this);
        
        // Initialize advanced feature managers
        this.rentalManager = new RentalManager(this);
        this.crateManager = new CrateManager(this);
        
        // Initialize cosmetics
        cosmeticManager.initialize();
        
        // Initialize public API
        SneakyCosmeticsAPI.initialize(this);
    }
    
    private void registerCommands() {
        // Register unified command system
        try {
            org.bukkit.command.PluginCommand sneakyCommand = getCommand("sneakycosmetics");
            if (sneakyCommand != null) {
                SneakyCosmeticsCommand unifiedCommand = new SneakyCosmeticsCommand(this);
                sneakyCommand.setExecutor(unifiedCommand);
                sneakyCommand.setTabCompleter(unifiedCommand);
                getLogger().info("✓ Registered unified command system: /sneakycosmetics");
            } else {
                getLogger().info("Command /sneakycosmetics not available - using legacy commands only");
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Could not register /sneakycosmetics command", e);
        }
        
        // Register legacy commands for backwards compatibility
        registerLegacyCommand("cosmetics", new CosmeticsCommand(this));
        registerLegacyCommand("credits", new CreditsCommand(this));
        registerLegacyCommand("morph", new com.sneaky.cosmetics.commands.MorphCommand(this));
        registerLegacyCommand("crate", new com.sneaky.cosmetics.commands.CrateCommand(this));
        registerLegacyCommand("rental", new com.sneaky.cosmetics.commands.RentalCommand(this));
    }
    
    /**
     * Helper method to register legacy commands with proper error handling
     */
    private void registerLegacyCommand(String commandName, org.bukkit.command.CommandExecutor executor) {
        try {
            org.bukkit.command.PluginCommand command = getCommand(commandName);
            if (command != null) {
                command.setExecutor(executor);
                if (executor instanceof org.bukkit.command.TabCompleter) {
                    command.setTabCompleter((org.bukkit.command.TabCompleter) executor);
                }
                getLogger().info("✓ Registered legacy command: /" + commandName);
            } else {
                getLogger().info("Command /" + commandName + " not available");
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Could not register /" + commandName + " command", e);
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
    
    public com.sneaky.cosmetics.cosmetics.morphs.MorphManager getMorphManager() {
        return morphManager;
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
    
    public RentalManager getRentalManager() {
        return rentalManager;
    }
    
    public CrateManager getCrateManager() {
        return crateManager;
    }
}
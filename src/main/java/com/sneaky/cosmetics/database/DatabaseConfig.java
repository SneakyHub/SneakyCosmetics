package com.sneaky.cosmetics.database;

import com.sneaky.cosmetics.SneakyCosmetics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

/**
 * Handles loading and management of database configuration from database.yml
 */
public class DatabaseConfig {
    
    private final SneakyCosmetics plugin;
    private FileConfiguration config;
    private File configFile;
    
    public DatabaseConfig(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "database.yml");
        loadConfig();
    }
    
    /**
     * Load the database configuration file
     */
    public void loadConfig() {
        // Create the file if it doesn't exist
        if (!configFile.exists()) {
            createDefaultConfig();
        }
        
        // Load the configuration
        config = YamlConfiguration.loadConfiguration(configFile);
        
        // Check for config version and update if needed
        int configVersion = config.getInt("config-version", 0);
        int expectedVersion = 1;
        
        if (configVersion < expectedVersion) {
            plugin.getLogger().info("Updating database.yml configuration...");
            backupConfig();
            createDefaultConfig();
            config = YamlConfiguration.loadConfiguration(configFile);
        }
        
        plugin.getLogger().info("Database configuration loaded successfully");
    }
    
    /**
     * Create the default database configuration file
     */
    private void createDefaultConfig() {
        try {
            // Create parent directories if they don't exist
            configFile.getParentFile().mkdirs();
            
            // Copy the default config from resources
            try (InputStream inputStream = plugin.getResource("database.yml")) {
                if (inputStream != null) {
                    Files.copy(inputStream, configFile.toPath(), 
                              java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                } else {
                    // If resource doesn't exist, create a basic config
                    createBasicConfig();
                }
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create database.yml", e);
            createBasicConfig();
        }
    }
    
    /**
     * Create a basic database configuration if the resource file is not available
     */
    private void createBasicConfig() {
        config = new YamlConfiguration();
        
        // Basic configuration
        config.set("config-version", 1);
        config.set("database.type", "sqlite");
        config.set("database.sqlite.filename", "sneakycosmetics.db");
        config.set("database.sqlite.wal-mode", true);
        
        // MySQL settings
        config.set("database.mysql.host", "localhost");
        config.set("database.mysql.port", 3306);
        config.set("database.mysql.database", "sneakycosmetics");
        config.set("database.mysql.username", "root");
        config.set("database.mysql.password", "password");
        config.set("database.mysql.ssl", false);
        
        // Connection pool settings
        config.set("connection-pool.hikari.maximum-pool-size", 10);
        config.set("connection-pool.hikari.minimum-idle", 2);
        config.set("connection-pool.hikari.connection-timeout", 30000);
        config.set("connection-pool.hikari.idle-timeout", 600000);
        config.set("connection-pool.hikari.max-lifetime", 1800000);
        
        // Schema settings
        config.set("schema.table-prefix", "sc_");
        config.set("schema.auto-create-tables", true);
        config.set("schema.auto-update-schema", true);
        
        // Save the basic configuration
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save basic database.yml", e);
        }
    }
    
    /**
     * Backup the current configuration file
     */
    private void backupConfig() {
        try {
            File backupFile = new File(configFile.getParent(), "database.yml.backup");
            Files.copy(configFile.toPath(), backupFile.toPath(), 
                      java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            plugin.getLogger().info("Backed up database.yml to database.yml.backup");
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to backup database.yml", e);
        }
    }
    
    /**
     * Reload the database configuration
     */
    public void reload() {
        loadConfig();
    }
    
    /**
     * Get the database type (sqlite, mysql, postgresql, mariadb)
     */
    public String getDatabaseType() {
        return config.getString("database.type", "sqlite").toLowerCase();
    }
    
    // SQLite configuration methods
    public String getSQLiteFilename() {
        return config.getString("database.sqlite.filename", "sneakycosmetics.db");
    }
    
    public boolean isSQLiteWalMode() {
        return config.getBoolean("database.sqlite.wal-mode", true);
    }
    
    // MySQL configuration methods
    public String getMySQLHost() {
        return config.getString("database.mysql.host", "localhost");
    }
    
    public int getMySQLPort() {
        return config.getInt("database.mysql.port", 3306);
    }
    
    public String getMySQLDatabase() {
        return config.getString("database.mysql.database", "sneakycosmetics");
    }
    
    public String getMySQLUsername() {
        return config.getString("database.mysql.username", "root");
    }
    
    public String getMySQLPassword() {
        return config.getString("database.mysql.password", "password");
    }
    
    public boolean isMySQLSSL() {
        return config.getBoolean("database.mysql.ssl", false);
    }
    
    public String getMySQLCharset() {
        return config.getString("database.mysql.charset", "utf8mb4");
    }
    
    // PostgreSQL configuration methods
    public String getPostgreSQLHost() {
        return config.getString("database.postgresql.host", "localhost");
    }
    
    public int getPostgreSQLPort() {
        return config.getInt("database.postgresql.port", 5432);
    }
    
    public String getPostgreSQLDatabase() {
        return config.getString("database.postgresql.database", "sneakycosmetics");
    }
    
    public String getPostgreSQLUsername() {
        return config.getString("database.postgresql.username", "postgres");
    }
    
    public String getPostgreSQLPassword() {
        return config.getString("database.postgresql.password", "password");
    }
    
    public String getPostgreSQLSchema() {
        return config.getString("database.postgresql.schema", "public");
    }
    
    public boolean isPostgreSQLSSL() {
        return config.getBoolean("database.postgresql.ssl", false);
    }
    
    // MariaDB configuration methods
    public String getMariaDBHost() {
        return config.getString("database.mariadb.host", "localhost");
    }
    
    public int getMariaDBPort() {
        return config.getInt("database.mariadb.port", 3306);
    }
    
    public String getMariaDBDatabase() {
        return config.getString("database.mariadb.database", "sneakycosmetics");
    }
    
    public String getMariaDBUsername() {
        return config.getString("database.mariadb.username", "root");
    }
    
    public String getMariaDBPassword() {
        return config.getString("database.mariadb.password", "password");
    }
    
    public boolean isMariaDBSSL() {
        return config.getBoolean("database.mariadb.ssl", false);
    }
    
    // Connection pool configuration methods
    public int getMaximumPoolSize() {
        return config.getInt("connection-pool.hikari.maximum-pool-size", 10);
    }
    
    public int getMinimumIdle() {
        return config.getInt("connection-pool.hikari.minimum-idle", 2);
    }
    
    public long getConnectionTimeout() {
        return config.getLong("connection-pool.hikari.connection-timeout", 30000);
    }
    
    public long getIdleTimeout() {
        return config.getLong("connection-pool.hikari.idle-timeout", 600000);
    }
    
    public long getMaxLifetime() {
        return config.getLong("connection-pool.hikari.max-lifetime", 1800000);
    }
    
    public long getKeepaliveTime() {
        return config.getLong("connection-pool.hikari.keepalive-time", 300000);
    }
    
    public long getValidationTimeout() {
        return config.getLong("connection-pool.hikari.validation-timeout", 5000);
    }
    
    public boolean isTestOnBorrow() {
        return config.getBoolean("connection-pool.hikari.test-on-borrow", true);
    }
    
    public String getConnectionTestQuery() {
        return config.getString("connection-pool.hikari.connection-test-query", "");
    }
    
    public String getPoolName() {
        return config.getString("connection-pool.hikari.pool-name", "SneakyCosmetics-Pool");
    }
    
    // Schema configuration methods
    public String getTablePrefix() {
        return config.getString("schema.table-prefix", "sc_");
    }
    
    public boolean isAutoCreateTables() {
        return config.getBoolean("schema.auto-create-tables", true);
    }
    
    public boolean isAutoUpdateSchema() {
        return config.getBoolean("schema.auto-update-schema", true);
    }
    
    public boolean isVersionTracking() {
        return config.getBoolean("schema.version-tracking", true);
    }
    
    // Optimization configuration methods
    public boolean isBatchOperationsEnabled() {
        return config.getBoolean("optimization.batch-operations.enabled", true);
    }
    
    public int getBatchSize() {
        return config.getInt("optimization.batch-operations.batch-size", 100);
    }
    
    public long getBatchTimeout() {
        return config.getLong("optimization.batch-operations.batch-timeout", 1000);
    }
    
    public boolean isUsePreparedStatements() {
        return config.getBoolean("optimization.query-optimization.use-prepared-statements", true);
    }
    
    public boolean isCachePreparedStatements() {
        return config.getBoolean("optimization.query-optimization.cache-prepared-statements", true);
    }
    
    public int getMaxCachedStatements() {
        return config.getInt("optimization.query-optimization.max-cached-statements", 50);
    }
    
    // Debugging configuration methods
    public boolean isDebugLogging() {
        return config.getBoolean("debugging.debug-logging", false);
    }
    
    public boolean isLogQueries() {
        return config.getBoolean("debugging.log-queries", false);
    }
    
    public boolean isLogSlowQueries() {
        return config.getBoolean("debugging.log-slow-queries", true);
    }
    
    public long getSlowQueryThreshold() {
        return config.getLong("debugging.slow-query-threshold", 1000);
    }
    
    public boolean isMonitorPool() {
        return config.getBoolean("debugging.monitor-pool", true);
    }
    
    // Data management configuration methods
    public boolean isCleanupEnabled() {
        return config.getBoolean("data-management.cleanup.enabled", true);
    }
    
    public int getTransactionLogsRetention() {
        return config.getInt("data-management.cleanup.transaction-logs", 90);
    }
    
    public int getLoginRecordsRetention() {
        return config.getInt("data-management.cleanup.login-records", 30);
    }
    
    public int getAchievementDataRetention() {
        return config.getInt("data-management.cleanup.achievement-data", 365);
    }
    
    public int getCleanupInterval() {
        return config.getInt("data-management.cleanup.cleanup-interval", 24);
    }
    
    public boolean isBackupEnabled() {
        return config.getBoolean("data-management.backup.enabled", true);
    }
    
    public int getBackupInterval() {
        return config.getInt("data-management.backup.backup-interval", 24);
    }
    
    public int getBackupRetention() {
        return config.getInt("data-management.backup.backup-retention", 7);
    }
    
    public String getBackupLocation() {
        return config.getString("data-management.backup.backup-location", "backups");
    }
    
    public boolean isCompressBackups() {
        return config.getBoolean("data-management.backup.compress-backups", true);
    }
    
    /**
     * Get the raw configuration object for advanced usage
     */
    public FileConfiguration getConfig() {
        return config;
    }
}
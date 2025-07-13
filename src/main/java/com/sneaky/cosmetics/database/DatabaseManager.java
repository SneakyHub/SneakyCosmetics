package com.sneaky.cosmetics.database;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Handles all database operations for SneakyCosmetics
 * Supports both SQLite and MySQL with connection pooling
 */
public class DatabaseManager {
    
    private final SneakyCosmetics plugin;
    private HikariDataSource dataSource;
    private final String databaseType;
    
    // Cache for frequently accessed data
    private final Map<UUID, Integer> creditCache = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, Boolean>> cosmeticCache = new ConcurrentHashMap<>();
    
    public DatabaseManager(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.databaseType = plugin.getConfig().getString("database.type", "sqlite").toLowerCase();
    }
    
    public void initialize() throws SQLException {
        setupDataSource();
        createTables();
        
        // Start cache cleanup task
        plugin.getSchedulerAdapter().runTaskTimerAsynchronously(() -> {
            cleanupCache();
        }, 6000L, 6000L); // Every 5 minutes
        
        plugin.getLogger().info("Database initialized successfully using " + databaseType.toUpperCase());
    }
    
    private void setupDataSource() {
        HikariConfig config = new HikariConfig();
        
        if (databaseType.equals("mysql")) {
            setupMySQLDataSource(config);
        } else {
            setupSQLiteDataSource(config);
        }
        
        this.dataSource = new HikariDataSource(config);
    }
    
    private void setupMySQLDataSource(HikariConfig config) {
        String host = plugin.getConfig().getString("database.mysql.host", "localhost");
        int port = plugin.getConfig().getInt("database.mysql.port", 3306);
        String database = plugin.getConfig().getString("database.mysql.database", "sneakycosmetics");
        String username = plugin.getConfig().getString("database.mysql.username", "root");
        String password = plugin.getConfig().getString("database.mysql.password", "password");
        boolean ssl = plugin.getConfig().getBoolean("database.mysql.ssl", false);
        
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + ssl + "&autoReconnect=true");
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        // Connection pool settings
        config.setMaximumPoolSize(plugin.getConfig().getInt("database.pool.maximum-pool-size", 10));
        config.setMinimumIdle(plugin.getConfig().getInt("database.pool.minimum-idle", 2));
        config.setConnectionTimeout(plugin.getConfig().getLong("database.pool.connection-timeout", 30000));
        config.setIdleTimeout(plugin.getConfig().getLong("database.pool.idle-timeout", 600000));
        config.setMaxLifetime(plugin.getConfig().getLong("database.pool.max-lifetime", 1800000));
        
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    }
    
    private void setupSQLiteDataSource(HikariConfig config) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        File databaseFile = new File(dataFolder, "cosmetics.db");
        
        config.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(1); // SQLite doesn't support multiple connections well
        
        config.addDataSourceProperty("journal_mode", "WAL");
        config.addDataSourceProperty("synchronous", "NORMAL");
        config.addDataSourceProperty("cache_size", "10000");
        config.addDataSourceProperty("foreign_keys", "true");
    }
    
    private void createTables() throws SQLException {
        try (Connection connection = getConnection()) {
            // Player data table
            connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS player_data (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "username VARCHAR(16) NOT NULL, " +
                "credits INTEGER DEFAULT 0, " +
                "last_login BIGINT DEFAULT 0, " +
                "last_daily_claim BIGINT DEFAULT 0, " +
                "total_playtime BIGINT DEFAULT 0, " +
                "created_at BIGINT DEFAULT " + System.currentTimeMillis() + ", " +
                "updated_at BIGINT DEFAULT " + System.currentTimeMillis() +
                ")"
            );
            
            // Cosmetic ownership table
            connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS cosmetic_ownership (" +
                "id " + (databaseType.equals("mysql") ? "INT AUTO_INCREMENT PRIMARY KEY" : "INTEGER PRIMARY KEY AUTOINCREMENT") + ", " +
                "player_uuid VARCHAR(36) NOT NULL, " +
                "cosmetic_id VARCHAR(64) NOT NULL, " +
                "purchased_at BIGINT DEFAULT " + System.currentTimeMillis() + ", " +
                "purchased_with VARCHAR(16) DEFAULT 'credits', " +
                "UNIQUE(player_uuid, cosmetic_id)" +
                ")"
            );
            
            // Active cosmetics table
            connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS active_cosmetics (" +
                "player_uuid VARCHAR(36) NOT NULL, " +
                "cosmetic_id VARCHAR(64) NOT NULL, " +
                "cosmetic_type VARCHAR(32) NOT NULL, " +
                "activated_at BIGINT DEFAULT " + System.currentTimeMillis() + ", " +
                "PRIMARY KEY(player_uuid, cosmetic_type)" +
                ")"
            );
            
            // Credit transactions table (for tracking purchases and transactions)
            connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS credit_transactions (" +
                "id " + (databaseType.equals("mysql") ? "INT AUTO_INCREMENT PRIMARY KEY" : "INTEGER PRIMARY KEY AUTOINCREMENT") + ", " +
                "player_uuid VARCHAR(36) NOT NULL, " +
                "amount INTEGER NOT NULL, " +
                "transaction_type VARCHAR(32) NOT NULL, " +
                "description TEXT, " +
                "timestamp BIGINT DEFAULT " + System.currentTimeMillis() +
                ")"
            );
            
            // Settings table for player preferences
            connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS player_settings (" +
                "player_uuid VARCHAR(36) PRIMARY KEY, " +
                "setting_key VARCHAR(64) NOT NULL, " +
                "setting_value TEXT, " +
                "updated_at BIGINT DEFAULT " + System.currentTimeMillis() +
                ")"
            );
            
            // Pet custom names table
            connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS pet_names (" +
                "player_uuid VARCHAR(36) NOT NULL, " +
                "pet_id VARCHAR(64) NOT NULL, " +
                "custom_name VARCHAR(32) NOT NULL, " +
                "updated_at BIGINT DEFAULT " + System.currentTimeMillis() + ", " +
                "PRIMARY KEY(player_uuid, pet_id)" +
                ")"
            );
            
            // Create indexes for better performance
            if (databaseType.equals("mysql")) {
                connection.createStatement().execute("CREATE INDEX IF NOT EXISTS idx_cosmetic_ownership_player ON cosmetic_ownership(player_uuid)");
                connection.createStatement().execute("CREATE INDEX IF NOT EXISTS idx_active_cosmetics_player ON active_cosmetics(player_uuid)");
                connection.createStatement().execute("CREATE INDEX IF NOT EXISTS idx_credit_transactions_player ON credit_transactions(player_uuid)");
                connection.createStatement().execute("CREATE INDEX IF NOT EXISTS idx_credit_transactions_timestamp ON credit_transactions(timestamp)");
            } else {
                connection.createStatement().execute("CREATE INDEX IF NOT EXISTS idx_cosmetic_ownership_player ON cosmetic_ownership(player_uuid)");
                connection.createStatement().execute("CREATE INDEX IF NOT EXISTS idx_active_cosmetics_player ON active_cosmetics(player_uuid)");
                connection.createStatement().execute("CREATE INDEX IF NOT EXISTS idx_credit_transactions_player ON credit_transactions(player_uuid)");
                connection.createStatement().execute("CREATE INDEX IF NOT EXISTS idx_credit_transactions_timestamp ON credit_transactions(timestamp)");
            }
        }
    }
    
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    // Player data methods
    public CompletableFuture<Void> createPlayerData(UUID uuid, String username) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                     "INSERT IGNORE INTO player_data (uuid, username, credits) VALUES (?, ?, ?)"
                 )) {
                
                statement.setString(1, uuid.toString());
                statement.setString(2, username);
                statement.setInt(3, plugin.getConfig().getInt("credits.welcome-amount", 500));
                statement.executeUpdate();
                
                // Cache the initial credits
                creditCache.put(uuid, plugin.getConfig().getInt("credits.welcome-amount", 500));
                
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create player data for " + username, e);
            }
        });
    }
    
    public CompletableFuture<Integer> getPlayerCredits(UUID uuid) {
        // Check cache first
        if (creditCache.containsKey(uuid)) {
            return CompletableFuture.completedFuture(creditCache.get(uuid));
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                     "SELECT credits FROM player_data WHERE uuid = ?"
                 )) {
                
                statement.setString(1, uuid.toString());
                ResultSet result = statement.executeQuery();
                
                int credits = result.next() ? result.getInt("credits") : 0;
                creditCache.put(uuid, credits);
                return credits;
                
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to get credits for " + uuid, e);
                return 0;
            }
        });
    }
    
    public CompletableFuture<Void> setPlayerCredits(UUID uuid, int credits) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                     "UPDATE player_data SET credits = ?, updated_at = ? WHERE uuid = ?"
                 )) {
                
                statement.setInt(1, credits);
                statement.setLong(2, System.currentTimeMillis());
                statement.setString(3, uuid.toString());
                statement.executeUpdate();
                
                // Update cache
                creditCache.put(uuid, credits);
                
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to set credits for " + uuid, e);
            }
        });
    }
    
    public CompletableFuture<Void> addPlayerCredits(UUID uuid, int credits) {
        return getPlayerCredits(uuid).thenCompose(currentCredits -> {
            int newCredits = Math.min(currentCredits + credits, plugin.getConfig().getInt("credits.max-credits", 100000));
            return setPlayerCredits(uuid, newCredits);
        });
    }
    
    public CompletableFuture<Boolean> removePlayerCredits(UUID uuid, int credits) {
        return getPlayerCredits(uuid).thenCompose(currentCredits -> {
            if (currentCredits >= credits) {
                return setPlayerCredits(uuid, currentCredits - credits).thenApply(v -> true);
            } else {
                return CompletableFuture.completedFuture(false);
            }
        });
    }
    
    // Cosmetic ownership methods
    public CompletableFuture<Boolean> hasCosmetic(UUID uuid, String cosmeticId) {
        // Check cache first
        Map<String, Boolean> playerCosmetics = cosmeticCache.get(uuid);
        if (playerCosmetics != null && playerCosmetics.containsKey(cosmeticId)) {
            return CompletableFuture.completedFuture(playerCosmetics.get(cosmeticId));
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                     "SELECT 1 FROM cosmetic_ownership WHERE player_uuid = ? AND cosmetic_id = ?"
                 )) {
                
                statement.setString(1, uuid.toString());
                statement.setString(2, cosmeticId);
                ResultSet result = statement.executeQuery();
                
                boolean hasCosmetic = result.next();
                
                // Update cache
                cosmeticCache.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(cosmeticId, hasCosmetic);
                
                return hasCosmetic;
                
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to check cosmetic ownership for " + uuid, e);
                return false;
            }
        });
    }
    
    public CompletableFuture<Void> giveCosmetic(UUID uuid, String cosmeticId) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                     "INSERT IGNORE INTO cosmetic_ownership (player_uuid, cosmetic_id) VALUES (?, ?)"
                 )) {
                
                statement.setString(1, uuid.toString());
                statement.setString(2, cosmeticId);
                statement.executeUpdate();
                
                // Update cache
                cosmeticCache.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(cosmeticId, true);
                
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to give cosmetic " + cosmeticId + " to " + uuid, e);
            }
        });
    }
    
    public CompletableFuture<Void> removeCosmetic(UUID uuid, String cosmeticId) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM cosmetic_ownership WHERE player_uuid = ? AND cosmetic_id = ?"
                 )) {
                
                statement.setString(1, uuid.toString());
                statement.setString(2, cosmeticId);
                statement.executeUpdate();
                
                // Update cache
                Map<String, Boolean> playerCache = cosmeticCache.get(uuid);
                if (playerCache != null) {
                    playerCache.remove(cosmeticId);
                }
                
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to remove cosmetic " + cosmeticId + " from " + uuid, e);
            }
        });
    }
    
    public String getPetCustomName(UUID uuid, String petId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                 "SELECT custom_name FROM pet_names WHERE player_uuid = ? AND pet_id = ?"
             )) {
            
            statement.setString(1, uuid.toString());
            statement.setString(2, petId);
            ResultSet result = statement.executeQuery();
            
            return result.next() ? result.getString("custom_name") : null;
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get pet custom name for " + uuid, e);
            return null;
        }
    }
    
    public CompletableFuture<Void> setPetCustomName(UUID uuid, String petId, String customName) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                String sql;
                if (databaseType.equals("mysql")) {
                    sql = "INSERT INTO pet_names (player_uuid, pet_id, custom_name) VALUES (?, ?, ?) " +
                          "ON DUPLICATE KEY UPDATE custom_name = VALUES(custom_name)";
                } else {
                    sql = "INSERT OR REPLACE INTO pet_names (player_uuid, pet_id, custom_name) VALUES (?, ?, ?)";
                }
                
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, uuid.toString());
                    statement.setString(2, petId);
                    statement.setString(3, customName);
                    statement.executeUpdate();
                }
                
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to set pet custom name for " + uuid, e);
            }
        });
    }
    
    // Statistics methods for bStats
    public int getTotalCosmeticsOwned() {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                 "SELECT COUNT(*) FROM cosmetic_ownership"
             )) {
            
            ResultSet result = statement.executeQuery();
            return result.next() ? result.getInt(1) : 0;
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get total cosmetics owned", e);
            return 0;
        }
    }
    
    public Map<String, Integer> getCosmeticTypeUsage() {
        Map<String, Integer> usage = new HashMap<>();
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                 "SELECT cosmetic_type, COUNT(*) as count FROM active_cosmetics GROUP BY cosmetic_type"
             )) {
            
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                usage.put(result.getString("cosmetic_type"), result.getInt("count"));
            }
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get cosmetic type usage", e);
        }
        
        return usage;
    }
    
    public int getTotalCreditsInCirculation() {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                 "SELECT SUM(credits) FROM player_data"
             )) {
            
            ResultSet result = statement.executeQuery();
            return result.next() ? result.getInt(1) : 0;
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get total credits in circulation", e);
            return 0;
        }
    }
    
    // Cache management
    private void cleanupCache() {
        long cacheExpiry = plugin.getConfig().getLong("performance.cache-duration", 10) * 60 * 1000; // Convert minutes to milliseconds
        long currentTime = System.currentTimeMillis();
        
        // Clean up credit cache for offline players
        creditCache.entrySet().removeIf(entry -> {
            UUID uuid = entry.getKey();
            return plugin.getServer().getPlayer(uuid) == null;
        });
        
        // Clean up cosmetic cache for offline players
        cosmeticCache.entrySet().removeIf(entry -> {
            UUID uuid = entry.getKey();
            return plugin.getServer().getPlayer(uuid) == null;
        });
    }
    
    public void invalidateCache(UUID uuid) {
        creditCache.remove(uuid);
        cosmeticCache.remove(uuid);
    }
    
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
    
    // Statistics database methods
    public void saveGlobalStatistics(long creditsEarned, long creditsSpent, long cosmeticsActivated, long achievementsUnlocked) {
        plugin.getSchedulerAdapter().runTaskAsynchronously(() -> {
            try (Connection conn = dataSource.getConnection()) {
                String sql = "INSERT OR REPLACE INTO global_statistics (id, credits_earned, credits_spent, cosmetics_activated, achievements_unlocked) VALUES (1, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setLong(1, creditsEarned);
                    stmt.setLong(2, creditsSpent);
                    stmt.setLong(3, cosmeticsActivated);
                    stmt.setLong(4, achievementsUnlocked);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to save global statistics", e);
            }
        });
    }
    
    public long[] loadGlobalStatistics() {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT credits_earned, credits_spent, cosmetics_activated, achievements_unlocked FROM global_statistics WHERE id = 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new long[]{
                        rs.getLong("credits_earned"),
                        rs.getLong("credits_spent"),
                        rs.getLong("cosmetics_activated"),
                        rs.getLong("achievements_unlocked")
                    };
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load global statistics", e);
        }
        return null;
    }
    
    public void savePlayerStatistics(UUID playerId, Object playerStats) {
        // Stub implementation - would save player statistics to database
        plugin.getLogger().fine("Saving player statistics for " + playerId + " (stub implementation)");
    }
    
    public Map<UUID, Object> loadAllPlayerStatistics() {
        // Stub implementation - would load all player statistics
        plugin.getLogger().fine("Loading all player statistics (stub implementation)");
        return new HashMap<>();
    }
    
    public void saveCosmeticUsageStats(Object usageCount, Object usageTime) {
        // Stub implementation - would save cosmetic usage statistics
        plugin.getLogger().fine("Saving cosmetic usage statistics (stub implementation)");
    }
    
    public Object[] loadCosmeticUsageStats() {
        // Stub implementation - would load cosmetic usage statistics
        plugin.getLogger().fine("Loading cosmetic usage statistics (stub implementation)");
        return null;
    }
    
    public void savePlayerActiveCosmetics(UUID playerId, java.util.Set<String> activeCosmetics) {
        plugin.getSchedulerAdapter().runTaskAsynchronously(() -> {
            try (Connection conn = dataSource.getConnection()) {
                // First clear existing active cosmetics
                String deleteSql = "DELETE FROM player_active_cosmetics WHERE player_id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setString(1, playerId.toString());
                    deleteStmt.executeUpdate();
                }
                
                // Insert new active cosmetics
                String insertSql = "INSERT INTO player_active_cosmetics (player_id, cosmetic_id) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    for (String cosmeticId : activeCosmetics) {
                        insertStmt.setString(1, playerId.toString());
                        insertStmt.setString(2, cosmeticId);
                        insertStmt.addBatch();
                    }
                    insertStmt.executeBatch();
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to save player active cosmetics", e);
            }
        });
    }
    
    public String getPetCustomName2(UUID playerId, String petId) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT custom_name FROM pet_custom_names WHERE player_id = ? AND pet_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, playerId.toString());
                stmt.setString(2, petId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("custom_name");
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to get pet custom name", e);
        }
        return null;
    }
}
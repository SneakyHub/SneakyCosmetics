# bStats Integration - SneakyCosmetics

## 📊 Analytics Overview

SneakyCosmetics includes **bStats** integration for anonymous usage analytics. This helps improve the plugin by understanding how it's being used across different servers.

### 🔢 **bStats ID: 26487**

- **Plugin ID**: 26487
- **Service**: [bStats.org](https://bstats.org/)
- **Plugin Page**: https://bstats.org/plugin/bukkit/SneakyCosmetics/26487

## 📈 **Collected Data**

### Standard Metrics
- Server count using the plugin
- Total player count across all servers
- Plugin version distribution
- Server software type (Paper, Spigot, Folia)
- Java version distribution

### Custom Metrics
1. **Total Cosmetics Owned** - Number of cosmetics owned by all players
2. **Active Players** - Current online players
3. **Cosmetic Types Used** - Usage breakdown by cosmetic category
4. **Vault Integration** - Whether Vault is enabled
5. **LuckPerms Integration** - Whether LuckPerms is enabled
6. **Total Credits in Circulation** - Economic data

## ⚙️ **Configuration**

### Enable/Disable bStats
```yaml
# config.yml
admin:
  metrics: true  # Set to false to disable
```

### Code Implementation
```java
// Initialize bStats with ID 26487
if (getConfig().getBoolean("admin.metrics", true)) {
    this.metrics = new Metrics(this, 26487);
    setupMetrics();
}
```

## 🔒 **Privacy & Security**

### What is Collected
- ✅ **Anonymous server statistics**
- ✅ **Plugin usage patterns**
- ✅ **Performance metrics**
- ✅ **Integration usage data**

### What is NOT Collected
- ❌ **Player names or UUIDs**
- ❌ **Server IPs or domains**
- ❌ **Chat messages or commands**
- ❌ **Personal player data**
- ❌ **Server configurations**

### Data Usage
- Data is **anonymized** and **aggregated**
- Used only for **plugin improvement**
- **No commercial use** of data
- Data helps identify **performance issues** and **popular features**

## 📊 **Custom Charts**

### 1. Total Cosmetics Owned
- **Type**: Single Line Chart
- **Description**: Tracks total cosmetics owned across all players
- **Purpose**: Understand cosmetic adoption rates

### 2. Active Players
- **Type**: Single Line Chart  
- **Description**: Current online players using cosmetics
- **Purpose**: Monitor active usage

### 3. Cosmetic Types Used
- **Type**: Advanced Pie Chart
- **Description**: Breakdown of cosmetic category usage
- **Purpose**: Identify popular cosmetic types

### 4. Integration Status
- **Type**: Simple Pie Charts
- **Description**: Shows Vault and LuckPerms usage
- **Purpose**: Understand integration adoption

### 5. Credits in Circulation
- **Type**: Single Line Chart
- **Description**: Total credits in the economy
- **Purpose**: Monitor economic health

## 🛠️ **Implementation Details**

### Maven Configuration
```xml
<dependency>
    <groupId>org.bstats</groupId>
    <artifactId>bstats-bukkit</artifactId>
    <version>3.0.2</version>
    <scope>compile</scope>
</dependency>
```

### Shading Configuration
```xml
<!-- Include bStats in shading -->
<include>org.bstats:bstats-bukkit</include>

<!-- Relocate to avoid conflicts -->
<relocation>
    <pattern>org.bstats</pattern>
    <shadedPattern>com.sneaky.cosmetics.libs.bstats</shadedPattern>
</relocation>
```

**Note**: Source code uses `import org.bstats.bukkit.Metrics;` - Maven automatically handles the relocation during build.

### Error Handling
```java
try {
    this.metrics = new Metrics(this, 26487);
    setupMetrics();
    getLogger().info("bStats metrics enabled with ID: 26487");
} catch (Exception e) {
    getLogger().log(Level.WARNING, "Failed to initialize bStats metrics", e);
}
```

## 🔧 **For Server Administrators**

### Viewing Statistics
1. Visit https://bstats.org/plugin/bukkit/SneakyCosmetics/26487
2. View global usage statistics
3. Compare your server with others
4. See plugin adoption trends

### Disabling bStats
If you prefer to disable analytics:
```yaml
# config.yml
admin:
  metrics: false
```

Or use the global bStats opt-out:
- Create `plugins/bStats/config.yml`
- Set `enabled: false`

## 📞 **Support**

### Issues with bStats
- Check server logs for bStats errors
- Ensure internet connectivity
- Verify plugin permissions
- Contact support if needed

### Questions
- **Discord**: [SneakyHub Discord](https://discord.gg/sneakyhub)
- **Issues**: [GitHub Issues](https://github.com/SneakyHub/SneakyCosmetics/issues)

---

**Note**: bStats is completely optional and can be disabled at any time. The analytics help improve SneakyCosmetics for everyone while respecting privacy and maintaining anonymity.
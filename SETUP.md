# SneakyCosmetics Setup Guide

## 🚀 Quick Start

### For Server Administrators

1. **Download & Install**
   ```bash
   # Download the latest release
   wget https://github.com/SneakyHub/SneakyCosmetics/releases/download/v1.1.0/SneakyCosmetics-1.1.0.jar
   
   # Place in plugins folder
   cp SneakyCosmetics-1.1.0.jar /path/to/server/plugins/
   
   # Restart server
   /restart
   ```

2. **Basic Configuration**
   - Edit `plugins/SneakyCosmetics/config.yml`
   - Configure economy settings if using Vault
   - Set up database preferences (SQLite recommended for small servers)
   - Customize messages in `messages.yml`

3. **Install Dependencies (Optional)**
   - **Vault** - For economy integration
   - **LuckPerms** - For advanced permissions
   - **EssentialsX** - For backup economy

### For Developers

1. **Clone Repository**
   ```bash
   git clone https://github.com/SneakyHub/SneakyCosmetics.git
   cd SneakyCosmetics
   ```

2. **Build Project**
   ```bash
   # Using Maven
   mvn clean package
   
   # Using build script (Windows)
   build.bat
   ```

3. **Development Setup**
   - Import into IntelliJ IDEA or Eclipse
   - Ensure Java 17+ is configured
   - Run `mvn clean install` to download dependencies

## 🔧 Configuration Details

### Essential Settings

```yaml
# config.yml - Key settings to configure

# Credit System
credits:
  cost-per-credit: 100.0      # Adjust based on your economy
  welcome-amount: 500         # Starting credits for new players
  max-credits: 100000         # Prevent credit hoarding

# Database - Choose your preference
database:
  type: sqlite                # Use 'mysql' for larger servers
  
# Performance - Tune for your server
performance:
  max-cosmetics-per-player: 5 # Prevent lag from too many effects
  folia-support: true         # Enable if using Folia

# Admin Features
admin:
  check-for-updates: true     # Get notified of new versions
  auto-update: false          # Enable for automatic updates
  metrics: true               # Help improve the plugin (bStats)
```

### Economy Integration

If using **Vault**:
```yaml
integrations:
  vault:
    enabled: true
```

Commands for economy:
- `/credits buy 1000` - Buy 1000 credits
- `/credits give PlayerName 500` - Admin give credits

### Permission Setup

**LuckPerms Example:**
```bash
# Basic permissions
lp group default permission set sneakycosmetics.use true
lp group default permission set sneakycosmetics.credits true

# VIP cosmetics
lp group vip permission set sneakycosmetics.vip true

# Premium cosmetics  
lp group premium permission set sneakycosmetics.premium true

# Admin access
lp group admin permission set sneakycosmetics.admin true
```

## 📊 Database Configuration

### SQLite (Recommended for small-medium servers)
```yaml
database:
  type: sqlite
```
- No additional setup required
- File stored in plugin folder
- Good performance for <100 players

### MySQL (Recommended for large servers)
```yaml
database:
  type: mysql
  mysql:
    host: localhost
    port: 3306
    database: sneakycosmetics
    username: your_username
    password: your_password
    ssl: false
```

**MySQL Setup:**
```sql
CREATE DATABASE sneakycosmetics;
CREATE USER 'sneaky_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON sneakycosmetics.* TO 'sneaky_user'@'localhost';
FLUSH PRIVILEGES;
```

## 🎨 Cosmetic Categories

The plugin includes 7 cosmetic categories:

1. **Particles** - Visual effects around players
2. **Hats** - Items displayed on player heads  
3. **Pets** - AI companions that follow players
4. **Trails** - Effects left behind when moving
5. **Gadgets** - Interactive items with special abilities
6. **Wings** - Cosmetic wings with gliding physics
7. **Auras** - Surrounding magical effects

Each category supports:
- Custom prices in credits
- Permission requirements
- VIP/Premium restrictions
- Activation/deactivation

## 🔒 Security & Performance

### Security Best Practices
- Use strong database passwords
- Regularly update the plugin
- Monitor bStats for usage patterns
- Review permission assignments

### Performance Optimization
```yaml
performance:
  max-cosmetics-per-player: 5     # Limit active cosmetics
  cleanup-interval: 30            # Clean up unused entities
  cache-duration: 10              # Cache player data (minutes)
  async-database: true            # Use async operations
  particle-optimization:
    enabled: true
    max-particles-per-player: 100
    reduce-distance: 30           # Reduce particles at distance
```

## 🚨 Troubleshooting

### Common Issues

1. **Plugin not loading**
   - Check Java version (requires 17+)
   - Verify server version (1.21.7+)
   - Check console for error messages

2. **Database connection failed**
   - Verify MySQL credentials
   - Check database server is running
   - Ensure database exists

3. **Economy not working**
   - Install Vault plugin
   - Install an economy plugin (EssentialsX, etc.)
   - Check Vault integration in logs

4. **Permissions not working**
   - Verify LuckPerms is installed
   - Check permission nodes are correct
   - Test with `/lp user <player> permission check <permission>`

5. **Updates not working**
   - Check internet connectivity
   - Verify GitHub access
   - Check admin.check-for-updates setting

### Debug Mode
Enable debug logging:
```yaml
admin:
  debug: true
```

This will provide detailed information in console logs.

### Log Locations
- **Console** - Real-time plugin messages
- **latest.log** - Server log file
- **plugins/SneakyCosmetics/logs/** - Plugin-specific logs

## 📞 Getting Help

1. **Check Documentation** - [GitHub Wiki](https://github.com/SneakyHub/SneakyCosmetics/wiki)
2. **Search Issues** - [GitHub Issues](https://github.com/SneakyHub/SneakyCosmetics/issues)
3. **Join Discord** - [SneakyHub Discord](https://discord.gg/sneakyhub)
4. **Create Issue** - Provide full error logs and configuration

### When Reporting Issues
Include:
- Server version and type (Paper/Spigot/Folia)
- Plugin version
- Full error message/stack trace
- Relevant configuration sections
- List of other plugins installed

## 🎯 Next Steps

After installation:
1. Test basic functionality with `/cosmetics`
2. Configure economy settings if using Vault
3. Set up permissions for different player groups
4. Customize messages to match your server theme
5. Monitor performance and adjust settings as needed

For advanced configuration and cosmetic creation, see the [Development Guide](DEVELOPMENT.md).

---

**Need help?** Join our Discord community for real-time support!
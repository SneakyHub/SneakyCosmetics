# SneakyCosmetics

A comprehensive Minecraft cosmetics plugin for 1.21.8+ with 80+ cosmetics, credit system, GUI management, and extensive integrations.

![Version](https://img.shields.io/badge/version-1.0.0--dev-blue.svg)
![Minecraft](https://img.shields.io/badge/minecraft-1.21.8+-green.svg)
![License](https://img.shields.io/badge/license-MIT-yellow.svg)
![Java](https://img.shields.io/badge/java-17+-orange.svg)

## 🌟 Features

### 🎨 Cosmetic System
- **80+ Cosmetics** across 7 categories
- **Particle Effects** - Beautiful particle effects around players
- **Hats** - Unique wearable items on player heads
- **Pets** - Loyal companions that follow players
- **Trails** - Magical trails left behind while moving
- **Gadgets** - Fun interactive items and tools
- **Wings** - Spread wings and glide with physics
- **Auras** - Mystical auras surrounding players

### 💰 Credit System
- **Integrated Economy** - Buy credits with real money via Vault
- **Daily Bonuses** - Login rewards for active players
- **Playtime Rewards** - Credits earned for time spent playing
- **Bulk Discounts** - Save money on larger credit purchases
- **EssentialsX Integration** - Backup economy support

### 🎯 Advanced Features
- **bStats Integration** - Anonymous analytics (ID: 26487)
- **Auto-Update System** - Automatic plugin updates from GitHub
- **Paper & Folia Support** - Full compatibility with modern servers
- **Permission Integration** - LuckPerms support for advanced permissions
- **Configurable Messages** - Fully customizable with hex color support
- **Performance Optimized** - Efficient for large servers

### 🛠️ Technical Features
- **Database Support** - SQLite and MySQL with connection pooling
- **Async Operations** - Non-blocking database operations
- **Cache System** - Optimized data caching for performance
- **Multi-Server Ready** - Designed for network compatibility

## 📋 Requirements

- **Java 17+**
- **Paper 1.21.8+** or **Spigot 1.21.8+** or **Folia**
- **Optional Dependencies:**
  - Vault (for economy features)
  - LuckPerms (for advanced permissions)
  - EssentialsX (for backup economy)

## 🚀 Installation

1. Download the latest release from [GitHub Releases](https://github.com/SneakyHub/SneakyCosmetics/releases)
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. Configure the plugin in `plugins/SneakyCosmetics/config.yml`
5. Enjoy your new cosmetics system!

## ⚙️ Configuration

### Basic Configuration

```yaml
# Credits System
credits:
  cost-per-credit: 100.0    # Cost per credit in economy
  welcome-amount: 500       # Credits given to new players
  max-credits: 100000       # Maximum credits per player
  
  daily-bonus:
    enabled: true
    amount: 50              # Daily login bonus
    
  playtime-rewards:
    enabled: true
    credits-per-hour: 25    # Credits per hour played

# Database (SQLite or MySQL)
database:
  type: sqlite              # or mysql
  
# Performance Settings
performance:
  max-cosmetics-per-player: 5
  cleanup-interval: 30      # minutes
  folia-support: true

# Admin Settings
admin:
  check-for-updates: true
  auto-update: false        # Enable for automatic updates
  metrics: true             # bStats analytics
```

### Message Customization

All messages are fully configurable in `messages.yml` with support for:
- Legacy color codes (`&c`, `&a`, etc.)
- Hex colors (`&#FF5555`)
- Placeholders (`{player}`, `{credits}`, etc.)

## 🎮 Commands

### Player Commands
- `/cosmetics` - Open the main cosmetics menu
- `/cosmetics list` - List available cosmetics
- `/cosmetics toggle <cosmetic>` - Toggle a cosmetic on/off
- `/credits` - Check your credit balance
- `/credits buy <amount>` - Purchase credits
- `/credits daily` - Claim daily bonus

### Admin Commands
- `/cosmetics give <player> <cosmetic>` - Give a cosmetic to a player
- `/cosmetics remove <player> <cosmetic>` - Remove a cosmetic from a player
- `/cosmetics clear <player>` - Clear all cosmetics from a player
- `/cosmetics reload` - Reload configuration
- `/cosmetics update` - Check for plugin updates
- `/credits give <player> <amount>` - Give credits to a player
- `/credits set <player> <amount>` - Set a player's credits

## 🔒 Permissions

### Basic Permissions
- `sneakycosmetics.use` - Use basic cosmetics features (default: true)
- `sneakycosmetics.credits` - Use the credits system (default: true)
- `sneakycosmetics.admin` - Access admin commands (default: op)

### Cosmetic Access
- `sneakycosmetics.vip` - Access VIP cosmetics
- `sneakycosmetics.premium` - Access Premium cosmetics
- `sneakycosmetics.bypass` - Bypass cosmetic restrictions

### Wildcard Permission
- `sneakycosmetics.*` - All permissions (default: op)

## 🔧 Building from Source

### Prerequisites
- Java 17+
- Maven 3.8+
- Git

### Build Steps
```bash
git clone https://github.com/SneakyHub/SneakyCosmetics.git
cd SneakyCosmetics
mvn clean package
```

The compiled JAR will be in the `target/` directory.

### Development Setup
1. Clone the repository
2. Import into your IDE as a Maven project
3. Install dependencies: `mvn clean install`
4. Build: `mvn clean package`

## 📊 Statistics & Analytics

SneakyCosmetics uses bStats (ID: 26487) to collect anonymous usage statistics:
- Server count and player count
- Cosmetic usage patterns
- Integration usage (Vault, LuckPerms, etc.)
- Performance metrics

This helps improve the plugin. You can disable this in `config.yml` by setting `admin.metrics: false`.

## 🤝 Support & Community

- **Discord**: [Join our Discord](https://discord.gg/sneakyhub)
- **Issues**: [GitHub Issues](https://github.com/SneakyHub/SneakyCosmetics/issues)
- **Documentation**: [Wiki](https://github.com/SneakyHub/SneakyCosmetics/wiki)
- **Website**: [SneakyHub](https://github.com/SneakyHub/SneakyCosmetics)

## 🔄 Auto-Update System

SneakyCosmetics features an intelligent auto-update system:

1. **Update Checking**: Automatically checks GitHub for new releases
2. **Admin Notifications**: Admins are notified of available updates
3. **One-Click Updates**: Download updates with `/cosmetics update`
4. **Safe Updates**: Updates are applied on server restart

The update system uses `latest.json` from the main branch to determine version information.

## 🗂️ Project Structure

```
SneakyCosmetics/
├── src/main/java/com/sneaky/cosmetics/
│   ├── SneakyCosmetics.java          # Main plugin class
│   ├── commands/                     # Command handlers
│   ├── cosmetics/                    # Cosmetic definitions
│   ├── database/                     # Database management
│   ├── gui/                         # GUI system
│   ├── integrations/                # Plugin integrations
│   ├── listeners/                   # Event listeners
│   ├── managers/                    # Core managers
│   └── utils/                       # Utility classes
├── src/main/resources/
│   ├── config.yml                   # Main configuration
│   ├── messages.yml                 # Message configuration
│   └── plugin.yml                   # Plugin metadata
├── latest.json                      # Update information
└── pom.xml                         # Maven configuration
```

## 📈 Roadmap

### Version 1.2.0
- [ ] Full GUI implementation with pagination
- [ ] 50+ additional cosmetics
- [ ] Advanced particle customization
- [ ] Cosmetic marketplace system

### Version 1.3.0
- [ ] Multi-server synchronization
- [ ] Cosmetic trading system
- [ ] Advanced permission groups
- [ ] API for developers

### Version 2.0.0
- [ ] Complete cosmetic overhaul
- [ ] Custom resource pack integration
- [ ] Advanced animation system
- [ ] Plugin API expansion

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Contributing

Contributions are welcome! Please read our [Contributing Guidelines](CONTRIBUTING.md) for details on how to submit pull requests, report issues, and contribute to the project.

## 🙏 Acknowledgments

- **Paper Team** - For the excellent Paper API
- **Vault Team** - For the economy API
- **LuckPerms** - For the permission system
- **bStats** - For plugin analytics
- **Community** - For feedback and suggestions

---

**Made with ❤️ by SneakyHub**

*SneakyCosmetics - Transform your Minecraft server with style!*
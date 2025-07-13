# Build Test Guide

## 🔨 Testing the Build Process

### Prerequisites
- Java 17+ installed
- Maven 3.8+ installed
- Internet connection (for downloading dependencies)

### Quick Build Test

1. **Navigate to project directory**:
   ```bash
   cd C:\Users\sneaky\Documents\GitHub\SneakyCosmetics
   ```

2. **Test compilation only**:
   ```bash
   mvn clean compile
   ```

3. **Full build with packaging**:
   ```bash
   mvn clean package
   ```

4. **Skip tests during build**:
   ```bash
   mvn clean package -DskipTests
   ```

### Expected Results

✅ **Successful compilation should show**:
```
[INFO] --- maven-compiler-plugin:3.11.0:compile (default-compile) @ SneakyCosmetics ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 23 source files to target/classes
[INFO] BUILD SUCCESS
```

✅ **Successful packaging should create**:
- `target/SneakyCosmetics-1.1.0.jar` (final shaded JAR)
- `target/original-SneakyCosmetics-1.1.0.jar` (original before shading)

### Troubleshooting

❌ **If you get bStats import errors**:
- Ensure Maven can download from the internet
- Check that `org.bstats:bstats-bukkit:3.0.2` is accessible
- Try: `mvn clean install -U` (force update dependencies)

❌ **If compilation fails**:
- Check Java version: `java --version` (must be 17+)
- Check Maven version: `mvn --version` (recommended 3.8+)
- Clear Maven cache: Delete `~/.m2/repository` folder

### Dependencies Download

First build will download dependencies (~50MB):
- Paper API
- Vault API
- LuckPerms API
- bStats library
- HikariCP
- SQLite JDBC
- OkHttp
- Gson
- Caffeine cache

### Verifying bStats Integration

After successful build, check the JAR:
```bash
# Extract and verify bStats is included and relocated
jar -tf target/SneakyCosmetics-1.1.0.jar | grep bstats
```

Should show files like:
```
com/sneaky/cosmetics/libs/bstats/bukkit/Metrics.class
com/sneaky/cosmetics/libs/bstats/bukkit/Metrics$1.class
...
```

### Build Performance

- **Clean compile**: ~30-60 seconds (first time)
- **Incremental compile**: ~5-15 seconds
- **Full package**: ~60-120 seconds (includes shading)

### Common Issues

1. **"Package does not exist"** = Maven dependency issue
2. **"Cannot find symbol"** = Import or classpath issue  
3. **"Build timeout"** = Network/firewall blocking Maven repositories
4. **"Java version"** = Wrong Java version (need 17+)

### Success Verification

After successful build:
1. JAR file exists: `target/SneakyCosmetics-1.1.0.jar`
2. JAR size: ~8-15MB (includes shaded dependencies)
3. No compilation errors in console
4. bStats classes relocated correctly

### Next Steps

1. Copy JAR to test server: `plugins/` folder
2. Start server and check console for startup messages
3. Verify bStats initialization: `bStats metrics enabled with ID: 26487`
4. Test basic commands: `/cosmetics`, `/credits`
# Building with IDE (No Maven Installation Required)

## 🎯 IntelliJ IDEA

### Import Project
1. **Open IntelliJ IDEA**
2. **File** → **Open**
3. Select the `SneakyCosmetics` folder
4. Choose **"Import as Maven project"**
5. Wait for dependencies to download

### Build Project
1. **View** → **Tool Windows** → **Maven**
2. Expand **SneakyCosmetics** → **Lifecycle**
3. Double-click **clean**
4. Double-click **package**
5. JAR will be created in `target/` folder

### Alternative Build
1. **Build** → **Build Project** (Ctrl+F9)
2. **Build** → **Build Artifacts** → **SneakyCosmetics:jar** → **Build**

## 🎯 Eclipse

### Import Project
1. **Open Eclipse**
2. **File** → **Import**
3. **Maven** → **Existing Maven Projects**
4. Browse to `SneakyCosmetics` folder
5. Click **Finish**

### Build Project
1. Right-click project → **Run As** → **Maven build...**
2. Goals: `clean package`
3. Click **Run**
4. JAR will be created in `target/` folder

## 🎯 Visual Studio Code

### Requirements
- Install **Extension Pack for Java**
- Install **Maven for Java** extension

### Import and Build
1. **File** → **Open Folder** → Select `SneakyCosmetics`
2. Open **Command Palette** (Ctrl+Shift+P)
3. Type: **Java: Rebuild Projects**
4. Or use terminal: `./mvnw.cmd package`

## ✅ Verification

After successful build:
- JAR file: `target/SneakyCosmetics-1.1.0.jar`
- Size: ~8-15MB (includes dependencies)
- No compilation errors

## 🐛 Troubleshooting

### Issue: "Project import failed"
- **Solution**: Ensure project folder contains `pom.xml`
- **Check**: Internet connection for dependency download

### Issue: "Java version error"
- **Solution**: Set project JDK to Java 17+
- **IntelliJ**: File → Project Structure → Project → Project SDK
- **Eclipse**: Right-click project → Properties → Java Build Path → Libraries

### Issue: "Dependencies not resolved"
- **Solution**: Refresh Maven project
- **IntelliJ**: Maven tool window → Refresh icon
- **Eclipse**: Right-click project → Maven → Reload Projects
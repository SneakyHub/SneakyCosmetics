# 🚀 SneakyCosmetics Build Instructions

## ⚡ Quick Start

**Simply run the build script:**
```cmd
cd "C:\Users\sneaky\Documents\GitHub\SneakyCosmetics"
build.bat
```

That's it! The script will handle everything automatically.

## 🎯 What the Build Script Does

### ✅ **Automatic Method Selection**
The script tries these methods in order until one works:

1. **Maven Wrapper** (fastest) - Uses included `mvnw.cmd`
2. **Downloaded Maven** (reliable) - Downloads Maven 3.9.6 automatically  
3. **Manual Compilation** (fallback) - Direct Java compilation

### ✅ **Smart Error Handling**
- **Never closes on errors** - Always pauses so you can see what happened
- **Automatic fallbacks** - If one method fails, tries the next
- **Clear error messages** - Tells you exactly what went wrong and how to fix it

### ✅ **Zero Installation Required**
- **No Maven installation needed**
- **No additional setup required**
- **Downloads dependencies automatically**

## 🛠️ Requirements

**Minimum:**
- **Java 17+** installed and in PATH
- **Internet connection** (for downloading dependencies)

**Recommended:**
- **JDK 17+** (includes Java compiler for best compatibility)
- **Windows 10/11** with PowerShell support

## 📋 Build Process Steps

The script performs these steps automatically:

1. **[1/8]** Check project directory
2. **[2/8]** Verify Java installation  
3. **[3/8]** Check for Java compiler (JDK)
4. **[4/8]** Choose best build method
5. **[5/8]** Start build process
6. **[6/8]** Setup tools (if needed)
7. **[7/8]** Compile and package
8. **[8/8]** Verify success and show results

## ✅ Expected Output

**Successful build:**
```
==========================================
          BUILD SUCCESSFUL!
==========================================

✓ JAR file created: target\SneakyCosmetics-1.1.0.jar
✓ File size: 12,345,678 bytes

Your plugin is ready to use!
```

## 🐛 Troubleshooting

### Common Issues

**"Java is not installed"**
- Install Java 17+ from https://adoptium.net/
- Restart Command Prompt after installation

**"Failed to download dependencies"**
- Check internet connection
- Try running as Administrator
- Check if firewall/antivirus is blocking downloads

**"Compilation failed"**
- Usually means there are syntax errors in the code
- The script will show the exact error messages

### Getting Help

If the build fails:
1. **Read the error message** - The script shows exactly what went wrong
2. **Try running as Administrator** - May fix permission issues
3. **Check internet connection** - Required for downloading dependencies
4. **Report the issue** with the full error message

## 🎉 After Successful Build

1. **Find your JAR file**: 
   - Location: `target\SneakyCosmetics-1.1.0.jar` or `SneakyCosmetics-1.1.0.jar`
   - Size: ~8-15MB (includes all dependencies)

2. **Install on server**:
   - Copy JAR to server's `plugins/` folder
   - Restart server
   - Check console for startup messages

3. **Verify installation**:
   - Look for: `SneakyCosmetics v1.1.0 has been enabled!`
   - Look for: `bStats metrics enabled with ID: 26487`
   - Test commands: `/cosmetics`, `/credits`

## 🎯 Features Included

Your built plugin includes:
- ✅ **bStats integration** (ID: 26487)
- ✅ **Paper/Spigot/Folia support**
- ✅ **Vault economy integration**
- ✅ **LuckPerms permission integration**  
- ✅ **Credit system with daily bonuses**
- ✅ **Auto-update system**
- ✅ **Configurable messages with hex colors**
- ✅ **SQLite and MySQL database support**
- ✅ **80+ cosmetic framework ready for expansion**

---

**Need help?** The build script provides detailed error messages and suggestions for fixing any issues!
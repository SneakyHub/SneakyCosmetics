# Maven Installation Guide for Windows

## 🚀 Quick Maven Installation

### Method 1: Using Chocolatey (Easiest)

1. **Install Chocolatey** (if not already installed):
   - Open PowerShell as Administrator
   - Run: 
   ```powershell
   Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
   ```

2. **Install Maven**:
   ```powershell
   choco install maven
   ```

3. **Verify Installation**:
   ```cmd
   mvn --version
   ```

### Method 2: Manual Installation

1. **Download Maven**:
   - Go to: https://maven.apache.org/download.cgi
   - Download: `apache-maven-3.9.6-bin.zip`

2. **Extract Maven**:
   - Extract to: `C:\Program Files\Apache\Maven\apache-maven-3.9.6`

3. **Set Environment Variables**:
   - Open "Environment Variables" (Win + R → `sysdm.cpl` → Advanced → Environment Variables)
   - Add new System Variable:
     - **Variable name**: `MAVEN_HOME`
     - **Variable value**: `C:\Program Files\Apache\Maven\apache-maven-3.9.6`
   - Edit PATH variable:
     - Add: `%MAVEN_HOME%\bin`

4. **Verify Installation**:
   - Open new Command Prompt
   - Run: `mvn --version`

### Method 3: Using Scoop

1. **Install Scoop** (if not already installed):
   ```powershell
   Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
   irm get.scoop.sh | iex
   ```

2. **Install Maven**:
   ```powershell
   scoop install maven
   ```

### Method 4: Using IntelliJ IDEA's Bundled Maven

If you have IntelliJ IDEA installed:
1. Find the bundled Maven (usually in `C:\Users\{username}\.m2\wrapper\dists\apache-maven-{version}`)
2. Add the `bin` folder to your PATH

## ✅ Verification

After installation, open a new Command Prompt and run:
```cmd
mvn --version
```

You should see output like:
```
Apache Maven 3.9.6
Maven home: C:\Program Files\Apache\Maven\apache-maven-3.9.6
Java version: 17.0.x, vendor: Eclipse Adoptium
```

## 🐛 Troubleshooting

### Issue: "mvn is not recognized"
- **Solution**: Restart Command Prompt/PowerShell after installation
- **Check**: Environment variables are set correctly
- **Verify**: PATH includes Maven bin directory

### Issue: Java not found
- **Solution**: Install Java 17+ first
- **Download**: https://adoptium.net/
- **Set**: JAVA_HOME environment variable

### Issue: Permission denied
- **Solution**: Run as Administrator
- **Alternative**: Install to user directory instead of Program Files

## 🎯 Quick Test

After Maven is installed, test the SneakyCosmetics build:
```cmd
cd "C:\Users\sneaky\Documents\GitHub\SneakyCosmetics"
mvn clean compile
```
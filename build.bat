@echo off
setlocal enabledelayedexpansion

echo.
echo ==========================================
echo   SneakyCosmetics Universal Build Script
echo ==========================================
echo This script will build your plugin using the best available method
echo No Maven installation required!
echo.

REM Function to pause on error
set "PAUSE_ON_ERROR=echo. & echo Press any key to continue... & pause >nul"

echo [1/8] Checking current directory...
echo Current directory: %CD%
if not exist "pom.xml" (
    echo.
    echo ERROR: pom.xml not found!
    echo Make sure you're running this script from the SneakyCosmetics project directory.
    echo Expected: C:\Users\sneaky\Documents\GitHub\SneakyCosmetics
    echo.
    %PAUSE_ON_ERROR%
    exit /b 1
)
echo ✓ Project directory confirmed

echo.
echo [2/8] Checking Java installation...
java --version >nul 2>&1
if errorlevel 1 (
    echo.
    echo ERROR: Java is not installed or not in PATH!
    echo.
    echo Please install Java 17+ from: https://adoptium.net/
    echo Make sure to:
    echo 1. Download JDK 17+ (not just JRE)
    echo 2. Add Java to your system PATH
    echo 3. Restart Command Prompt after installation
    echo.
    %PAUSE_ON_ERROR%
    exit /b 1
)

echo ✓ Java found:
java --version 2>&1 | findstr "openjdk\|java"

echo.
echo [3/8] Checking Java compiler (JDK)...
javac -version >nul 2>&1
if errorlevel 1 (
    echo.
    echo WARNING: Java compiler (javac) not found!
    echo You have JRE but need JDK. Attempting Maven wrapper method...
    set "USE_WRAPPER=true"
) else (
    echo ✓ Java compiler found
    set "USE_WRAPPER=false"
)

echo.
echo [4/8] Choosing build method...

REM Try Maven wrapper first if available
if exist "mvnw.cmd" (
    echo ✓ Maven wrapper found, testing it...
    
    call mvnw.cmd --version >nul 2>&1
    if !errorlevel! equ 0 (
        echo ✓ Maven wrapper working
        set "BUILD_METHOD=wrapper"
        goto :build_start
    ) else (
        echo ! Maven wrapper failed, trying alternative methods...
    )
)

REM Try downloading Maven if we have JDK
if "%USE_WRAPPER%"=="false" (
    echo Attempting to download Maven...
    set "BUILD_METHOD=download"
    goto :build_start
)

REM Fallback to manual compilation
echo Using manual compilation method...
set "BUILD_METHOD=manual"

:build_start
echo.
echo [5/8] Starting build with method: %BUILD_METHOD%
echo.

if "%BUILD_METHOD%"=="wrapper" goto :build_wrapper
if "%BUILD_METHOD%"=="download" goto :build_download  
if "%BUILD_METHOD%"=="manual" goto :build_manual

:build_wrapper
echo Building with Maven wrapper...
echo.

echo Cleaning previous build...
call mvnw.cmd clean
if !errorlevel! neq 0 (
    echo.
    echo ERROR: Maven clean failed!
    echo Trying alternative build method...
    set "BUILD_METHOD=download"
    goto :build_download
)

echo.
echo Compiling and packaging...
call mvnw.cmd package -DskipTests
if !errorlevel! neq 0 (
    echo.
    echo ERROR: Maven build failed!
    echo.
    echo Common causes:
    echo - Network connectivity issues
    echo - Dependency download problems
    echo - Source code compilation errors
    echo.
    echo Trying alternative build method...
    set "BUILD_METHOD=download"
    goto :build_download
)

goto :build_success

:build_download
echo Building with downloaded Maven...
echo.

echo [6/8] Setting up Maven...
if not exist "tools" mkdir tools
cd tools

if not exist "apache-maven-3.9.6" (
    echo Downloading Maven 3.9.6...
    powershell -Command "& {try { Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip' -OutFile 'maven.zip' -TimeoutSec 30 } catch { exit 1 }}"
    
    if !errorlevel! neq 0 (
        echo.
        echo ERROR: Failed to download Maven!
        echo Network connectivity issue or firewall blocking download.
        echo.
        cd ..
        set "BUILD_METHOD=manual"
        goto :build_manual
    )
    
    if exist "maven.zip" (
        echo Extracting Maven...
        powershell -Command "& {try { Expand-Archive -Path 'maven.zip' -DestinationPath '.' -Force } catch { exit 1 }}"
        if !errorlevel! neq 0 (
            echo ERROR: Failed to extract Maven!
            cd ..
            set "BUILD_METHOD=manual"
            goto :build_manual
        )
        del maven.zip >nul 2>&1
        echo ✓ Maven downloaded and extracted
    ) else (
        echo ERROR: Maven download failed!
        cd ..
        set "BUILD_METHOD=manual"
        goto :build_manual
    )
) else (
    echo ✓ Maven already available
)

echo.
echo [7/8] Building with Maven...
set "MAVEN_HOME=%CD%\apache-maven-3.9.6"
set "PATH=%MAVEN_HOME%\bin;%PATH%"

cd ..

echo Testing Maven...
mvn --version
if !errorlevel! neq 0 (
    echo ERROR: Maven test failed!
    set "BUILD_METHOD=manual"
    goto :build_manual
)

echo.
echo Building project...
mvn clean package -DskipTests
if !errorlevel! neq 0 (
    echo.
    echo ERROR: Maven build failed!
    echo Falling back to manual compilation...
    set "BUILD_METHOD=manual"
    goto :build_manual
)

goto :build_success

:build_manual
echo Building with manual compilation...
echo.

echo [6/8] Creating build directories...
if exist "build" rmdir /s /q "build" >nul 2>&1
mkdir build\classes
if not exist "lib" mkdir lib

echo.
echo [7/8] Downloading dependencies...
echo This may take a few minutes on first run...

REM Download Paper API
if not exist "lib\paper-api.jar" (
    echo Downloading Paper API...
    powershell -Command "& {try { Invoke-WebRequest -Uri 'https://repo.papermc.io/repository/maven-public/io/papermc/paper/paper-api/1.21.3-R0.1-SNAPSHOT/paper-api-1.21.3-R0.1-SNAPSHOT.jar' -OutFile 'lib\paper-api.jar' -TimeoutSec 60 } catch { Write-Host 'Failed to download Paper API'; exit 1 }}"
    if !errorlevel! neq 0 (
        echo ERROR: Failed to download Paper API!
        echo Check your internet connection.
        echo.
        %PAUSE_ON_ERROR%
        exit /b 1
    )
)

REM Download bStats
if not exist "lib\bstats-bukkit.jar" (
    echo Downloading bStats...
    powershell -Command "& {try { Invoke-WebRequest -Uri 'https://repo.codemc.org/repository/maven-public/org/bstats/bstats-bukkit/3.0.2/bstats-bukkit-3.0.2.jar' -OutFile 'lib\bstats-bukkit.jar' -TimeoutSec 30 } catch { Write-Host 'Failed to download bStats'; exit 1 }}"
    if !errorlevel! neq 0 (
        echo ERROR: Failed to download bStats!
        echo.
        %PAUSE_ON_ERROR%
        exit /b 1
    )
)

echo ✓ Dependencies downloaded

echo.
echo Compiling Java source files...
dir /s /b src\main\java\*.java > build\sources.txt 2>nul
if !errorlevel! neq 0 (
    echo ERROR: No Java source files found!
    echo Project structure may be corrupted.
    echo.
    %PAUSE_ON_ERROR%
    exit /b 1
)

javac -cp "lib\*" -d build\classes @build\sources.txt 2>&1
if !errorlevel! neq 0 (
    echo.
    echo ERROR: Java compilation failed!
    echo There may be syntax errors in the source code.
    echo.
    %PAUSE_ON_ERROR%
    exit /b 1
)

echo ✓ Compilation successful

echo.
echo Copying resources...
if exist "src\main\resources" (
    xcopy src\main\resources build\classes /E /I /Y >nul 2>&1
    echo ✓ Resources copied
)

echo.
echo Creating JAR file...
cd build\classes
jar -cf ..\..\SneakyCosmetics-1.1.0.jar * 2>&1
if !errorlevel! neq 0 (
    echo ERROR: Failed to create JAR file!
    cd ..\..
    %PAUSE_ON_ERROR%
    exit /b 1
)
cd ..\..

goto :build_success

:build_success
echo.
echo [8/8] Build completed successfully!
echo.
echo ==========================================
echo           BUILD SUCCESSFUL!
echo ==========================================
echo.

REM Check for JAR file
set "JAR_FOUND=false"
if exist "target\SneakyCosmetics-1.1.0.jar" (
    set "JAR_FILE=target\SneakyCosmetics-1.1.0.jar"
    set "JAR_FOUND=true"
) else if exist "SneakyCosmetics-1.1.0.jar" (
    set "JAR_FILE=SneakyCosmetics-1.1.0.jar"
    set "JAR_FOUND=true"
)

if "%JAR_FOUND%"=="true" (
    echo ✓ JAR file created: %JAR_FILE%
    for %%I in ("%JAR_FILE%") do echo ✓ File size: %%~zI bytes
    echo.
    echo Your plugin is ready to use!
    echo.
    echo Next steps:
    echo 1. Copy %JAR_FILE% to your server's plugins folder
    echo 2. Restart your server
    echo 3. Use /cosmetics and /credits commands
    echo.
    echo Features included:
    echo • bStats integration (ID: 26487)
    echo • Paper/Spigot/Folia support  
    echo • Vault economy integration
    echo • LuckPerms permission integration
    echo • Credit system with daily bonuses
    echo • Auto-update system
    echo • Configurable messages with hex colors
    echo • SQLite and MySQL database support
    echo.
) else (
    echo ERROR: JAR file was not created!
    echo The build may have failed silently.
    echo.
    %PAUSE_ON_ERROR%
    exit /b 1
)

echo Build method used: %BUILD_METHOD%
echo Build completed at: %date% %time%
echo.
echo Press any key to exit...
pause >nul
exit /b 0
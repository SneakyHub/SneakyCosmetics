@echo off
setlocal enabledelayedexpansion

echo.
echo ==========================================
echo   SneakyCosmetics Auto-Java Build Script
echo ==========================================
echo This script automatically finds Java on your system
echo.

echo [1/8] Checking current directory...
if not exist "pom.xml" (
    echo ERROR: pom.xml not found! Run from project directory.
    pause
    exit /b 1
)
echo ✓ Project directory confirmed

echo.
echo [2/8] Finding Java installation...

REM Try common Java locations
set "JAVA_FOUND=false"
set "JAVA_CMD="

REM Check PATH first
java --version >nul 2>&1
if !errorlevel! equ 0 (
    set "JAVA_CMD=java"
    set "JAVAC_CMD=javac"
    set "JAVA_FOUND=true"
    echo ✓ Java found in PATH
    goto :java_found
)

REM Search common installation directories
for %%d in (
    "C:\Program Files\Eclipse Adoptium"
    "C:\Program Files\Java" 
    "C:\Program Files (x86)\Eclipse Adoptium"
    "C:\Program Files (x86)\Java"
    "%JAVA_HOME%"
) do (
    if exist "%%~d" (
        for /f "delims=" %%j in ('dir /b /ad "%%~d" 2^>nul ^| findstr /i jdk') do (
            if exist "%%~d\%%j\bin\java.exe" (
                set "JAVA_CMD=%%~d\%%j\bin\java.exe"
                set "JAVAC_CMD=%%~d\%%j\bin\javac.exe"
                set "JAVA_FOUND=true"
                echo ✓ Java found at: %%~d\%%j
                goto :java_found
            )
        )
    )
)

:java_found
if "%JAVA_FOUND%"=="false" (
    echo.
    echo ERROR: Java not found!
    echo.
    echo Please install Java 17+ from: https://adoptium.net/
    echo After installation, restart Command Prompt and try again.
    echo.
    pause
    exit /b 1
)

echo Testing Java version...
"%JAVA_CMD%" --version 2>&1 | findstr "17\|18\|19\|20\|21"
if !errorlevel! neq 0 (
    echo.
    echo WARNING: Java version may not be 17+
    echo Please verify your Java version:
    "%JAVA_CMD%" --version
    echo.
    echo Press any key to continue anyway...
    pause
)

echo.
echo [3/8] Testing Java compiler...
if exist "%JAVAC_CMD%" (
    echo ✓ Java compiler found
    set "HAS_COMPILER=true"
) else (
    echo ! Java compiler not found, will use Maven
    set "HAS_COMPILER=false"
)

echo.
echo [4/8] Building project...
echo Using manual compilation with auto-detected Java...

REM Create build directories
if exist "build" rmdir /s /q "build" >nul 2>&1
mkdir build\classes
if not exist "lib" mkdir lib

echo.
echo [5/8] Downloading dependencies...

REM Download Paper API
if not exist "lib\paper-api.jar" (
    echo Downloading Paper API...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo.papermc.io/repository/maven-public/io/papermc/paper/paper-api/1.21.3-R0.1-SNAPSHOT/paper-api-1.21.3-R0.1-SNAPSHOT.jar' -OutFile 'lib\paper-api.jar'"
    if !errorlevel! neq 0 (
        echo ERROR: Failed to download Paper API
        pause
        exit /b 1
    )
)

REM Download bStats
if not exist "lib\bstats-bukkit.jar" (
    echo Downloading bStats...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo.codemc.org/repository/maven-public/org/bstats/bstats-bukkit/3.0.2/bstats-bukkit-3.0.2.jar' -OutFile 'lib\bstats-bukkit.jar'"
    if !errorlevel! neq 0 (
        echo ERROR: Failed to download bStats
        pause
        exit /b 1
    )
)

echo ✓ Dependencies ready

echo.
echo [6/8] Compiling source code...
dir /s /b src\main\java\*.java > build\sources.txt

if "%HAS_COMPILER%"=="true" (
    "%JAVAC_CMD%" -cp "lib\*" -d build\classes @build\sources.txt 2>&1
    if !errorlevel! neq 0 (
        echo ERROR: Compilation failed!
        pause
        exit /b 1
    )
    echo ✓ Compilation successful
) else (
    echo ERROR: No Java compiler available!
    echo You need JDK (not just JRE) to compile.
    pause
    exit /b 1
)

echo.
echo [7/8] Adding resources...
if exist "src\main\resources" (
    xcopy src\main\resources build\classes /E /I /Y >nul
)

echo.
echo [8/8] Creating JAR file...
cd build\classes

REM Check if jar command is available
jar -cf ..\..\SneakyCosmetics-1.1.0.jar * 2>&1
if !errorlevel! neq 0 (
    echo ERROR: jar command failed!
    echo Trying alternative method...
    
    REM Try with full Java path
    for %%p in ("%JAVA_CMD%") do set "JAVA_DIR=%%~dp"
    "!JAVA_DIR!jar.exe" -cf ..\..\SneakyCosmetics-1.1.0.jar * 2>&1
    if !errorlevel! neq 0 (
        echo ERROR: JAR creation failed!
        cd ..\..
        pause
        exit /b 1
    )
)

cd ..\..

echo.
echo ==========================================
echo           BUILD SUCCESSFUL!
echo ==========================================
echo.

if exist "SneakyCosmetics-1.1.0.jar" (
    echo ✓ JAR created: SneakyCosmetics-1.1.0.jar
    for %%I in ("SneakyCosmetics-1.1.0.jar") do echo ✓ Size: %%~zI bytes
    echo.
    echo Your plugin is ready!
    echo Copy SneakyCosmetics-1.1.0.jar to your server's plugins folder.
) else (
    echo ERROR: JAR file not created!
    pause
    exit /b 1
)

echo.
echo Press any key to exit...
pause >nul
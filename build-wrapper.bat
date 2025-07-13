@echo off
setlocal enabledelayedexpansion

echo.
echo ========================================
echo   SneakyCosmetics Build Script
echo   (Using Maven Wrapper - No Installation Required)
echo ========================================
echo.

:: Check current directory
echo Current directory: %CD%
echo.

:: Check if we're in the right directory
if not exist "pom.xml" (
    echo ERROR: pom.xml not found!
    echo Please make sure you're running this script from the SneakyCosmetics project directory.
    echo Expected location: C:\Users\sneaky\Documents\GitHub\SneakyCosmetics
    echo.
    pause
    exit /b 1
)

:: Check if Maven wrapper exists
if not exist "mvnw.cmd" (
    echo ERROR: Maven wrapper (mvnw.cmd) not found!
    echo The Maven wrapper should be in the same directory as this script.
    echo.
    pause
    exit /b 1
)

:: Check if Java is installed
echo Checking Java installation...
java --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH!
    echo Please install Java 17+ and try again.
    echo Download from: https://adoptium.net/
    echo.
    pause
    exit /b 1
)

:: Show Java version
echo Java version check:
java --version
echo.

echo Maven Wrapper will automatically download Maven if needed...
echo.

echo Starting build process...
echo.

:: Test Maven wrapper first
echo Testing Maven wrapper...
call mvnw.cmd --version
if errorlevel 1 (
    echo ERROR: Maven wrapper failed to start!
    echo This might be a permission issue or network connectivity problem.
    echo.
    pause
    exit /b 1
)
echo.

:: Clean and compile using Maven wrapper
echo [1/3] Cleaning previous build...
call mvnw.cmd clean
set CLEAN_RESULT=!errorlevel!

if !CLEAN_RESULT! neq 0 (
    echo ERROR: Clean failed with exit code !CLEAN_RESULT!
    echo.
    pause
    exit /b 1
)

echo.
echo [2/3] Compiling and packaging...
call mvnw.cmd package -DskipTests
set PACKAGE_RESULT=!errorlevel!

if !PACKAGE_RESULT! neq 0 (
    echo ERROR: Build failed with exit code !PACKAGE_RESULT!
    echo.
    echo Common causes:
    echo - Network connectivity issues
    echo - Java version incompatibility
    echo - Compilation errors in source code
    echo.
    pause
    exit /b 1
)

echo.
echo [3/3] Build completed successfully!

:: Check if JAR file exists
if exist "target\SneakyCosmetics-1.1.0.jar" (
    echo.
    echo ========================================
    echo   BUILD SUCCESSFUL!
    echo ========================================
    echo.
    echo JAR file location: target\SneakyCosmetics-1.1.0.jar
    echo File size: 
    for %%I in ("target\SneakyCosmetics-1.1.0.jar") do echo   %%~zI bytes
    echo.
    echo You can now copy this JAR to your server's plugins folder.
    echo.
    echo Features included:
    echo - bStats integration (ID: 26487)
    echo - Paper/Folia support
    echo - Vault integration
    echo - LuckPerms integration
    echo - Credit system
    echo - Auto-update system
    echo - Configurable messages
    echo.
) else (
    echo ERROR: JAR file was not created!
    pause
    exit /b 1
)

pause
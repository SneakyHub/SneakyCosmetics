@echo off
setlocal enabledelayedexpansion

echo SneakyCosmetics Simple Compile Test
echo ===================================
echo This will test basic compilation without Maven
echo.

echo Checking Java compiler...
javac -version 2>nul
if errorlevel 1 (
    echo ERROR: Java compiler (javac) not found
    echo You need the full JDK, not just JRE
    echo Download from: https://adoptium.net/temurin/releases/
    echo Make sure to download JDK 17+, not just JRE
    pause
    exit /b 1
)

echo ✓ Java compiler found
echo.

echo Creating simple test compilation...
echo.

echo Step 1: Create temporary build directory
if exist "temp-build" rmdir /s /q "temp-build"
mkdir temp-build

echo Step 2: Try to compile just the main class
echo Compiling SneakyCosmetics.java...

set "MAIN_FILE=src\main\java\com\sneaky\cosmetics\SneakyCosmetics.java"

if exist "%MAIN_FILE%" (
    echo Found main file: %MAIN_FILE%
    
    echo Attempting compilation (this will show missing dependencies)...
    javac -d temp-build "%MAIN_FILE%" 2>&1
    
    echo.
    echo Compilation attempt finished.
    echo Errors above are expected (missing Paper API, bStats, etc.)
    echo The important thing is that the .java file is readable and valid.
    
) else (
    echo ERROR: Main file not found at %MAIN_FILE%
    echo Project structure may be incorrect.
)

echo.
echo Step 3: List all Java files in project
echo Found Java files:
dir /s /b src\*.java 2>nul
if errorlevel 1 (
    echo No Java files found in src directory!
    echo Project structure is broken.
)

echo.
echo Step 4: Check project structure
echo Checking directories:
if exist "src" (echo ✓ src) else (echo ✗ src)
if exist "src\main" (echo ✓ src\main) else (echo ✗ src\main)
if exist "src\main\java" (echo ✓ src\main\java) else (echo ✗ src\main\java)
if exist "src\main\resources" (echo ✓ src\main\resources) else (echo ✗ src\main\resources)
if exist "pom.xml" (echo ✓ pom.xml) else (echo ✗ pom.xml)

echo.
echo Simple compilation test complete.
echo.
echo If you saw Java files listed and the main class was found,
echo then the project structure is correct and we just need Maven.
echo.
echo Next step: Try the no-maven-build.bat script
echo It will download dependencies and build properly.
echo.
pause
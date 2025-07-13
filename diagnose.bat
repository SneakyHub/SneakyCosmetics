@echo off
echo ========================================
echo   SneakyCosmetics Diagnostic Script
echo ========================================
echo.

echo Current Directory:
echo %CD%
echo.

echo Checking for required files:
if exist "pom.xml" (
    echo ✓ pom.xml found
) else (
    echo ✗ pom.xml NOT FOUND
)

if exist "mvnw.cmd" (
    echo ✓ mvnw.cmd found
) else (
    echo ✗ mvnw.cmd NOT FOUND
)

if exist "src\main\java\com\sneaky\cosmetics\SneakyCosmetics.java" (
    echo ✓ Main plugin class found
) else (
    echo ✗ Main plugin class NOT FOUND
)

if exist ".mvn\wrapper\maven-wrapper.properties" (
    echo ✓ Maven wrapper properties found
) else (
    echo ✗ Maven wrapper properties NOT FOUND
)

echo.
echo Checking Java installation:
java --version 2>nul
if errorlevel 1 (
    echo ✗ Java NOT FOUND or not in PATH
    echo Please install Java 17+ from https://adoptium.net/
) else (
    echo ✓ Java found
)

echo.
echo Checking JAVA_HOME:
if defined JAVA_HOME (
    echo ✓ JAVA_HOME is set to: %JAVA_HOME%
) else (
    echo ! JAVA_HOME not set (this is usually okay)
)

echo.
echo Directory contents:
dir /b

echo.
echo Project structure check:
if exist "src" (
    echo ✓ src directory exists
    if exist "src\main" (
        echo   ✓ src\main exists
        if exist "src\main\java" (
            echo     ✓ src\main\java exists
        ) else (
            echo     ✗ src\main\java missing
        )
        if exist "src\main\resources" (
            echo     ✓ src\main\resources exists
        ) else (
            echo     ✗ src\main\resources missing
        )
    ) else (
        echo   ✗ src\main missing
    )
) else (
    echo ✗ src directory missing
)

echo.
echo Network connectivity test (ping Maven Central):
ping -n 1 repo1.maven.org >nul 2>&1
if errorlevel 1 (
    echo ✗ Cannot reach Maven Central (repo1.maven.org)
    echo This may cause Maven downloads to fail
) else (
    echo ✓ Maven Central is reachable
)

echo.
echo ========================================
echo   Diagnostic Complete
echo ========================================
echo.
echo If everything shows ✓, try running build-wrapper.bat again
echo If you see ✗ marks, those issues need to be fixed first
echo.
pause
@echo off
echo Maven Wrapper Debug Script
echo ==========================
echo.

echo Current directory: %CD%
echo.

echo Checking mvnw.cmd contents...
if exist "mvnw.cmd" (
    echo mvnw.cmd file size:
    for %%I in ("mvnw.cmd") do echo %%~zI bytes
    echo.
    
    echo First few lines of mvnw.cmd:
    more +1 mvnw.cmd | head -10
    echo.
) else (
    echo mvnw.cmd not found!
    pause
    exit /b 1
)

echo Checking .mvn directory...
if exist ".mvn" (
    echo .mvn directory exists
    dir .mvn /s
) else (
    echo .mvn directory missing!
    echo Creating .mvn structure...
    mkdir .mvn\wrapper
)

echo.
echo Checking maven-wrapper.properties...
if exist ".mvn\wrapper\maven-wrapper.properties" (
    echo maven-wrapper.properties exists
    echo Contents:
    type ".mvn\wrapper\maven-wrapper.properties"
) else (
    echo maven-wrapper.properties missing!
)

echo.
echo Testing mvnw.cmd execution...
echo Running: mvnw.cmd --help
echo.

call mvnw.cmd --help
set MVN_EXIT_CODE=%errorlevel%

echo.
echo Maven wrapper exit code: %MVN_EXIT_CODE%

if %MVN_EXIT_CODE% neq 0 (
    echo Maven wrapper failed!
    echo.
    echo Possible causes:
    echo 1. Network connectivity issues
    echo 2. Firewall blocking downloads
    echo 3. Antivirus interference
    echo 4. Proxy settings needed
    echo 5. Corrupted wrapper files
    echo.
) else (
    echo Maven wrapper is working!
)

echo.
echo Press any key to continue with version test...
pause

echo.
echo Testing: mvnw.cmd --version
call mvnw.cmd --version
set VERSION_EXIT_CODE=%errorlevel%
echo Version test exit code: %VERSION_EXIT_CODE%

echo.
echo Debug complete. Press any key to exit.
pause
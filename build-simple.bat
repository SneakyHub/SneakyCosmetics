@echo off
echo Simple Build Script - Press ENTER to continue each step
echo =====================================================
echo.

echo Step 1: Checking current directory
echo Current dir: %CD%
pause

echo.
echo Step 2: Checking for pom.xml
if exist "pom.xml" (
    echo ✓ pom.xml found
) else (
    echo ✗ pom.xml NOT found - you're in the wrong directory!
    pause
    exit /b 1
)
pause

echo.
echo Step 3: Checking for Maven wrapper
if exist "mvnw.cmd" (
    echo ✓ mvnw.cmd found
) else (
    echo ✗ mvnw.cmd NOT found
    pause
    exit /b 1
)
pause

echo.
echo Step 4: Testing Java
java --version
if errorlevel 1 (
    echo ✗ Java test failed
    pause
    exit /b 1
) else (
    echo ✓ Java is working
)
pause

echo.
echo Step 5: Testing Maven wrapper
echo Running: mvnw.cmd --version
mvnw.cmd --version
echo Exit code: %errorlevel%
pause

echo.
echo Step 6: Maven clean
echo Running: mvnw.cmd clean
mvnw.cmd clean
echo Clean exit code: %errorlevel%
pause

echo.
echo Step 7: Maven compile (test compilation only)
echo Running: mvnw.cmd compile
mvnw.cmd compile
echo Compile exit code: %errorlevel%
pause

echo.
echo If compilation worked, we can try full package...
echo Running: mvnw.cmd package -DskipTests
mvnw.cmd package -DskipTests
echo Package exit code: %errorlevel%

echo.
echo Build process complete!
pause
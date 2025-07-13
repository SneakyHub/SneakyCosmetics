@echo off
echo Building WITHOUT Maven Wrapper
echo ==============================
echo This method compiles Java directly using javac
echo.

echo Step 1: Check Java compiler
javac -version
if errorlevel 1 (
    echo ERROR: javac not found. You need JDK, not just JRE.
    echo Download JDK 17+ from: https://adoptium.net/
    pause
    exit /b 1
)

echo.
echo Step 2: Create output directory
if not exist "build\classes" mkdir build\classes

echo.
echo Step 3: Download dependencies manually
echo Creating lib directory...
if not exist "lib" mkdir lib

echo.
echo Downloading Paper API...
if not exist "lib\paper-api-1.21.3-R0.1-SNAPSHOT.jar" (
    powershell -Command "Invoke-WebRequest -Uri 'https://repo.papermc.io/repository/maven-public/io/papermc/paper/paper-api/1.21.3-R0.1-SNAPSHOT/paper-api-1.21.3-R0.1-SNAPSHOT.jar' -OutFile 'lib\paper-api.jar'"
)

echo.
echo Downloading bStats...
if not exist "lib\bstats-bukkit-3.0.2.jar" (
    powershell -Command "Invoke-WebRequest -Uri 'https://repo.codemc.org/repository/maven-public/org/bstats/bstats-bukkit/3.0.2/bstats-bukkit-3.0.2.jar' -OutFile 'lib\bstats-bukkit.jar'"
)

echo.
echo Step 4: Find all Java files
dir /s /b src\main\java\*.java > sources.txt

echo.
echo Step 5: Compile Java files
javac -cp "lib\*" -d build\classes @sources.txt

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo Step 6: Copy resources
xcopy src\main\resources build\classes /E /I /Y

echo.
echo Step 7: Create JAR file
cd build\classes
jar -cf ..\..\SneakyCosmetics-manual.jar *
cd ..\..

echo.
if exist "SneakyCosmetics-manual.jar" (
    echo SUCCESS! JAR created: SneakyCosmetics-manual.jar
    for %%I in ("SneakyCosmetics-manual.jar") do echo Size: %%~zI bytes
) else (
    echo JAR creation failed!
)

echo.
pause
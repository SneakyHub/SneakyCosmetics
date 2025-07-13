@echo off
echo Manual Build Script (Downloads Maven if needed)
echo ===============================================
echo.

echo Step 1: Check Java
java --version
if errorlevel 1 (
    echo Java not found! Install Java 17+ from https://adoptium.net/
    pause
    exit /b 1
)

echo.
echo Step 2: Create tools directory
if not exist "tools" mkdir tools
cd tools

echo.
echo Step 3: Download Maven (if needed)
if not exist "apache-maven-3.9.6" (
    echo Downloading Maven 3.9.6...
    powershell -Command "& {Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip' -OutFile 'maven.zip'}"
    
    if exist "maven.zip" (
        echo Extracting Maven...
        powershell -Command "& {Expand-Archive -Path 'maven.zip' -DestinationPath '.' -Force}"
        del maven.zip
        echo Maven downloaded and extracted successfully!
    ) else (
        echo Failed to download Maven!
        cd ..
        pause
        exit /b 1
    )
) else (
    echo Maven already exists in tools directory
)

echo.
echo Step 4: Set Maven path
set "MAVEN_HOME=%CD%\apache-maven-3.9.6"
set "PATH=%MAVEN_HOME%\bin;%PATH%"

echo Maven Home: %MAVEN_HOME%
echo.

echo Step 5: Test Maven
mvn --version
if errorlevel 1 (
    echo Maven test failed!
    cd ..
    pause
    exit /b 1
)

echo.
echo Step 6: Go back to project directory
cd ..

echo.
echo Step 7: Build project
echo Running: mvn clean package -DskipTests

mvn clean package -DskipTests

if errorlevel 1 (
    echo Build failed!
    pause
    exit /b 1
) else (
    echo.
    echo ========================================
    echo BUILD SUCCESSFUL!
    echo ========================================
    echo.
    if exist "target\SneakyCosmetics-1.1.0.jar" (
        echo JAR created: target\SneakyCosmetics-1.1.0.jar
        for %%I in ("target\SneakyCosmetics-1.1.0.jar") do echo Size: %%~zI bytes
    )
)

echo.
pause
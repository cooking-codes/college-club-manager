# =========================================================
# COLLEGE CLUB MANAGER - BUILD AND DEPLOY SCRIPT
# Pure Java Compilation and WAR Packaging for Apache Tomcat 10.1.59
# =========================================================

param(
    [string]$TomcatDir = "C:\Tomcat\apache-tomcat-10.1.59",
    [switch]$Deploy = $true
)

$ErrorActionPreference = "Stop"

Write-Host "----------------------------------------------------" -ForegroundColor Cyan
Write-Host "   College Club Manager - Build & Package Script    " -ForegroundColor Cyan
Write-Host "----------------------------------------------------" -ForegroundColor Cyan

$ProjectDir = $PSScriptRoot
$JavaHome = "C:\Program Files\Java\jdk-17"
$Javac = Join-Path $JavaHome "bin\javac.exe"
$Jar = Join-Path $JavaHome "bin\jar.exe"

if (-not (Test-Path $Javac)) {
    Write-Error "javac.exe not found at $Javac. Please verify your JDK 17 installation."
    exit 1
}

# 1. Prepare Target Directory Structure
$TargetDir = Join-Path $ProjectDir "target"
$WarStagingDir = Join-Path $TargetDir "college-club-manager"
$WebInfClasses = Join-Path $WarStagingDir "WEB-INF\classes"
$WebInfLib = Join-Path $WarStagingDir "WEB-INF\lib"

Write-Host "[1/5] Cleaning and creating target directories..." -ForegroundColor Yellow
if (Test-Path $TargetDir) {
    Remove-Item -Path $TargetDir -Recurse -Force
}
New-Item -ItemType Directory -Force -Path $WebInfClasses | Out-Null
New-Item -ItemType Directory -Force -Path $WebInfLib | Out-Null

# 2. Ensure MySQL Driver is in WEB-INF/lib
$DriverSource = Join-Path $ProjectDir "src\main\webapp\WEB-INF\lib\mysql-connector-j-8.4.0.jar"
if (-not (Test-Path $DriverSource)) {
    Write-Host "Downloading mysql-connector-j-8.4.0.jar..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Force -Path (Split-Path $DriverSource) | Out-Null
    Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.4.0/mysql-connector-j-8.4.0.jar" -OutFile $DriverSource
}
Copy-Item $DriverSource -Destination $WebInfLib -Force

# 3. Compile Java Sources
Write-Host "[2/5] Compiling Java classes with JDK 17..." -ForegroundColor Yellow
$Classpath = "$TomcatDir\lib\servlet-api.jar;$TomcatDir\lib\jsp-api.jar;$DriverSource"
$SourceFiles = Get-ChildItem -Path (Join-Path $ProjectDir "src\main\java") -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }

& $Javac -encoding UTF-8 -cp $Classpath -d $WebInfClasses $SourceFiles
if ($LASTEXITCODE -ne 0) {
    Write-Error "Java compilation failed!"
    exit 1
}

# Copy properties/resources to classes directory
$PropsFile = Join-Path $ProjectDir "src\main\resources\db.properties"
if (Test-Path $PropsFile) {
    Copy-Item $PropsFile -Destination $WebInfClasses -Force
}

Write-Host "Java compilation succeeded! $( (Get-ChildItem $WebInfClasses -Recurse -Filter *.class).Count ) classes generated." -ForegroundColor Green

# 4. Copy Web Assets
Write-Host "[3/5] Copying web application resources (HTML, CSS, JS, JSP, web.xml)..." -ForegroundColor Yellow
$WebappSrc = Join-Path $ProjectDir "src\main\webapp"
Get-ChildItem -Path $WebappSrc | Where-Object { $_.Name -ne "WEB-INF" } | ForEach-Object {
    Copy-Item -Path $_.FullName -Destination $WarStagingDir -Recurse -Force
}

# Copy web.xml
$WebXmlSrc = Join-Path $WebappSrc "WEB-INF\web.xml"
$WebInfDest = Join-Path $WarStagingDir "WEB-INF"
Copy-Item -Path $WebXmlSrc -Destination $WebInfDest -Force

# 5. Create WAR file
Write-Host "[4/5] Packaging college-club-manager.war..." -ForegroundColor Yellow
$WarFile = Join-Path $TargetDir "college-club-manager.war"
Push-Location $WarStagingDir
try {
    & $Jar -cvf $WarFile * | Out-Null
} finally {
    Pop-Location
}

if (Test-Path $WarFile) {
    $warSizeMb = [math]::Round(((Get-Item $WarFile).Length / 1MB), 2)
    Write-Host "Successfully packaged: $WarFile ($warSizeMb MB)" -ForegroundColor Green
} else {
    Write-Error "Failed to generate WAR file."
    exit 1
}

# 6. Deploy to Tomcat if requested
if ($Deploy -and (Test-Path $TomcatDir)) {
    Write-Host "[5/5] Deploying WAR to Tomcat 10.1 webapps directory..." -ForegroundColor Yellow
    $TomcatWebapps = Join-Path $TomcatDir "webapps"
    if (Test-Path $TomcatWebapps) {
        Copy-Item -Path $WarFile -Destination $TomcatWebapps -Force
        # Also copy driver to tomcat/lib for global availability
        Copy-Item -Path $DriverSource -Destination "$TomcatDir\lib" -Force -ErrorAction SilentlyContinue
        Write-Host "WAR copied to: $TomcatWebapps\college-club-manager.war" -ForegroundColor Green
        Write-Host "Tomcat will automatically extract and deploy the application." -ForegroundColor Green
    }
}

Write-Host "----------------------------------------------------" -ForegroundColor Cyan
Write-Host "   BUILD COMPLETE! Ready to run on Apache Tomcat.   " -ForegroundColor Cyan
Write-Host "   URL: http://localhost:8080/college-club-manager/ " -ForegroundColor Cyan
Write-Host "----------------------------------------------------" -ForegroundColor Cyan

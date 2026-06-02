@REM Maven Wrapper batch script for Windows
@echo off
setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

if not exist "%APP_HOME%\.mvn" (
    echo Maven wrapper not found. Installing...
    mkdir "%APP_HOME%\.mvn\wrapper"
)

if not exist "%APP_HOME%\.mvn\wrapper\maven-wrapper.jar" (
    echo Downloading Maven...
    powershell -Command "(New-Object Net.WebClient).DownloadFile('https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.1.1/maven-wrapper-3.1.1.jar', '%APP_HOME%\.mvn\wrapper\maven-wrapper.jar')"
)

java -cp "%APP_HOME%\.mvn\wrapper\maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain %*

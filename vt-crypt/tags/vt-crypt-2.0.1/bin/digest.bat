@echo off
if "%OS%" == "Windows_NT" setlocal

if not defined JAVA_HOME goto no_java_home
if not defined VTCRYPT_HOME goto no_vtcrypt_home

set JAVA=%JAVA_HOME%\bin\java

set CRYPT_JAR=%VTCRYPT_HOME%\jars\vt-crypt-${project.version}.jar
set LIBDIR=%VTCRYPT_HOME%\lib

set CLASSPATH=%LIBDIR%\commons-cli-1.1.jar;%LIBDIR%\commons-logging-1.1.1.jar;%LIBDIR%\bcprov-jdk15-140.jar;%CRYPT_JAR%

call "%JAVA%" -cp "%CLASSPATH%" edu.vt.middleware.crypt.digest.DigestCli %*
goto end

:no_vtcrypt_home
echo ERROR: VTCRYPT_HOME environment variable must be set to VT Crypt install path.
goto end

:no_java_home
echo ERROR: JAVA_HOME environment variable must be set to JRE/JDK install path.

:end

@echo off
if "%OS%" == "Windows_NT" setlocal

set JAVA=java
set JAVA_OPTS=

if exist bin set PREFIX=
if exist ..\bin set PREFIX=..\

set CRYPT_JAR=vt-crypt-2.0.jar
set LIBDIR=%PREFIX%lib

set CLASSPATH=%LIBDIR%\commons-cli-1.1.jar;%LIBDIR%\commons-logging-1.1.1.jar;%LIBDIR%\bcprov-jdk14-140.jar;%PREFIX%jars\%CRYPT_JAR%

call %JAVA% %JAVA_OPTS% -cp %CLASSPATH% edu.vt.middleware.crypt.digest.DigestCli %*

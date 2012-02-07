@echo off
if "%OS%" == "Windows_NT" setlocal

if not defined JAVA_HOME goto no_java_home
if not defined LDAPTIVE_HOME goto no_ldaptive_home

set JAVA=%JAVA_HOME%\bin\java

set LDAP_JAR=%LDAPTIVE_HOME%\jars\ldaptive-${project.version}.jar
set LIBDIR=%LDAPTIVE_HOME%\lib

set CLASSPATH=%LIBDIR%\commons-cli-1.2.jar;%LIBDIR%\commons-codec-1.6.jar;%LDAP_JAR%

call "%JAVA%" -cp "%CLASSPATH%" org.ldaptive.cli.AddOperationCli %*
goto end

:no_ldaptive_home
echo ERROR: LDAPTIVE_HOME environment variable must be set to Ldaptive install path.
goto end

:no_java_home
echo ERROR: JAVA_HOME environment variable must be set to JRE/JDK install path.

:end

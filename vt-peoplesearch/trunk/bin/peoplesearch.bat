@echo off
if "%OS%" == "Windows_NT" setlocal

if not defined JAVA_HOME goto no_java_home
if not defined VTPEOPLESEARCH_HOME goto no_vtpeoplesearch_home

set JAVA=%JAVA_HOME%\bin\java

set PEOPLESEARCH_JAR=%VTPEOPLESEARCH_HOME%\jars\vt-peoplesearch-${project.version}.jar
set LIBDIR=%VTPEOPLESEARCH_HOME%\lib

set CLASSPATH=%LIBDIR%\aopalliance-1.0.jar;%LIBDIR%\commons-cli-1.2.jar;%LIBDIR%\commons-codec-1.3.jar;%LIBDIR%\commons-logging-1.1.1.jar;%LIBDIR%\dom4j-1.6.1.jar;%LIBDIR%\spring-beans-2.5.6.jar;%LIBDIR%\spring-context-2.5.6.jar;%LIBDIR%\spring-core-2.5.6.jar;%LIBDIR%\vt-ldap-3.2.jar;%LIBDIR%\vt-servlet-filters-2.0.2.jar;%PEOPLESEARCH_JAR%

call "%JAVA%" -cp "%CLASSPATH%" edu.vt.middleware.ldap.search.PeopleSearch %*
goto end

:no_vtpeoplesearch_home
echo ERROR: VTPEOPLESEARCH_HOME environment variable must be set to VT People Search install path.
goto end

:no_java_home
echo ERROR: JAVA_HOME environment variable must be set to JRE/JDK install path.

:end

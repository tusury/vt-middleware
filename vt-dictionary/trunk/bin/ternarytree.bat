@echo off
if "%OS%" == "Windows_NT" setlocal

if not defined JAVA_HOME goto no_java_home
if not defined VTDICT_HOME goto no_vtdict_home

set JAVA=%JAVA_HOME%\bin\java

set DICT_JAR=%VTDICT_HOME%\jars\vt-dictionary-${project.version}.jar

set CLASSPATH=%DICT_JAR%

call "%JAVA%" -cp "%CLASSPATH%" edu.vt.middleware.dictionary.TernaryTreeDictionary %*
goto end

:no_vtdict_home
echo ERROR: VTDICT_HOME environment variable must be set to VT Dictionary install path.
goto end

:no_java_home
echo ERROR: JAVA_HOME environment variable must be set to JRE/JDK install path.

:end

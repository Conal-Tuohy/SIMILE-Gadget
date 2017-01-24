@echo off
::
:: Configuration variables
::
:: JAVA_HOME
::   Home of Java installation.
::
:: JAVA_OPTIONS
::   Extra options to pass to the JVM
::

:: ----- Verify and Set Required Environment Variables -------------------------

if not "%JAVA_HOME%" == "" goto gotJavaHome
echo You must set JAVA_HOME to point at your Java Development Kit installation
goto end
:gotJavaHome

:: ----- Check System Properties -----------------------------------------------

if not "%JAVA_OPTIONS%" == "" goto gotJavaOptions
set JAVA_OPTIONS=-Xms32M -Xmx512M
:gotJavaOptions

::set JAVA_ARGS=-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n
::set JAVA_ARGS=-Xrunhprof:heap=all,cpu=samples,thread=y,depth=3
::set JAVA_ARGS=-Dcom.sun.management.jmxremote

:: ----- Set Up The Classpath --------------------------------------------------

set CP=.\tools\loader\classes
set MAIN=edu.mit.simile.gadget.Gadget

"%JAVA_HOME%\bin\java.exe" %JAVA_OPTIONS% %JAVA_ARGS% -classpath "%CP%" -Dorg.xml.sax.parser=org.apache.xerces.parsers.SAXParser -Dloader.jar.repositories=target -Dloader.main.class=%MAIN% Loader %1 %2 %3 %4 %5 %6 %7 %8 %9

:: ----- End -------------------------------------------------------------------

:end
set CP=


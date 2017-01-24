#!/bin/sh
#
# Configuration variables
#
# JAVA_HOME
#   Home of Java installation.
#
# JAVA_OPTIONS
#   Extra options to pass to the JVM
#

# ----- Verify and Set Required Environment Variables -------------------------

if [ "$JAVA_HOME" = "" ] ; then
  echo You must set JAVA_HOME to point at your Java Development Kit installation
  exit 1
fi

if [ "$JAVA_OPTIONS" = "" ] ; then
  JAVA_OPTIONS='-Xms32M -Xmx512M'
  # JAVA_OPTIONS='-Xms32M -Xmx512M -Xrunhprof:heap=all,cpu=samples,thread=y,depth=3'
fi

# ----- Set platform specific variables

PATHSEP=":";
case "`uname`" in
   CYGWIN*) PATHSEP=";" ;;
esac

# ----- Do the action ----------------------------------------------------------

$JAVA_HOME/bin/java $JAVA_OPTIONS -classpath ./lib/gadget.jar -Djava.endorsed.dirs=./lib/endorsed -Dorg.xml.sax.parser=org.apache.xerces.parsers.SAXParser Gadget $*
#!/bin/sh 
# Set CLASSPATH to include $CATALINA_HOME/domibus 
# where the domibus 'domibus.config.properties’ is located
export CLASSPATH=$CATALINA_HOME/domibus
export CATALINA_TMPDIR=$CATALINA_HOME/tmp
export JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8 -Xms128m -Xmx1024m "
export JAVA_OPTS="$JAVA_OPTS -Ddomibus.config.location=$CATALINA_HOME/domibus"

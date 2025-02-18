#!/bin/bash -ex

$CATALINA_HOME/smp/medcom-config.sh > $CATALINA_HOME/smp/medcom-config.log &
sleep 20 && catalina.sh run

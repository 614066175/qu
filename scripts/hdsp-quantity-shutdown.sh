#!/bin/bash

echo "Stopping SpringBoot Application [hdsp-quantity.jar] Starting...."
pid=`ps -ef | grep hdsp-quantity.jar | grep -v grep | grep -v hap-* | awk '{print $2}'`
if [ -n "$pid" ]
then
   echo "[hdsp-quantity.jar]Force Kill -9 pid:" $pid
   kill -9 $pid
fi
echo "Stopping SpringBoot Application [hdsp-quantity.jar] Finished...."

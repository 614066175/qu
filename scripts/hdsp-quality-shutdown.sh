#!/bin/bash

echo "Stopping SpringBoot Application [hdsp-quality.jar] Starting...."
pid=`ps -ef | grep hdsp-quality.jar | grep -v grep | grep -v hap-* | awk '{print $2}'`
if [ -n "$pid" ]
then
   echo "[hdsp-quality.jar]Force Kill -9 pid:" $pid
   kill -9 $pid
fi
echo "Stopping SpringBoot Application [hdsp-quality.jar] Finished...."

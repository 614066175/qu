#!/bin/bash

source /etc/profile
exec nohup java -Xmx500m -Xms500m \
    -DSPRING_DATASOURCE_URL='jdbc:mysql://db.hdsp.hand.com:3306/hdsp_quantity?useUnicode=true&characterEncoding=utf-8&useSSL=false' \
    -DSPRING_DATASOURCE_USERNAME='hdsp_dev' \
    -DSPRING_DATASOURCE_PASSWORD='hdsp_dev' \
	  -DSPRING_REDIS_HOST='redis.hdsp.hand.com' \
    -DSPRING_REDIS_PORT=6379 \
    -DSPRING_REDIS_PASSWORD='hdsp_dev' \
    -jar hdsp-quantity.jar > ./logs/hdsp-quantity.log 2>&1 &

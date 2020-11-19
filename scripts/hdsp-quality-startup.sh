#!/bin/bash

appName=hdsp-quality
source /etc/profile
java -Xmx500m -Xms500m \
  -DSPRING_CLOUD_CONFIG_ENABLED=true \
  -DSPRING_PROFILES_ACTIVE=default \
  -DSPRING_DATASOURCE_URL='jdbc:mysql://ip:23306/hdsp_quality?useUnicode=true&characterEncoding=utf-8&useSSL=false' \
  -DSPRING_DATASOURCE_USERNAME='username' \
  -DSPRING_DATASOURCE_PASSWORD='hpassword' \
  -DSPRING_REDIS_HOST='ip' \
  -DSPRING_REDIS_PORT=port \
  -DSPRING_REDIS_PASSWORD='password' \
  -DSPRING_CLOUD_CONFIG_URI='config' \
  -DEUREKA_DEFAULT_ZONE='eureka' \
  -DHDSP_ACCESS_SIGN_ENABLED=false \
  -DHDSP_ACCESS_SIGN_URL_SUFFIX='/v2/.*' \
  -DHDSP_ACCESS_SIGN_EXCLUDE_URL_SUFFIX ='/v1/.*' \
  -DHDSP_ACCESS_SIGN_INIT_TOKEN_NUM=10 \
  -jar $appName.jar &

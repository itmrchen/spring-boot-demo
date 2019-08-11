#!/bin/bash
sh /usr/local/kafka/bin/kafka-server-start.sh /usr/local/kafka/config/server.properties 1>/dev/null 2>&1 &
echo "kafka启动完成"

#!/bin/bash

JAR_NAME="base-web-customer.jar"
PORT=10010
PROFILE="prod"

# 查找端口是否被占用
PID=$(lsof -t -i:$PORT)

# 打印端口状态
if [ -n "$PID" ]; then
          echo "Found running process on port $PORT (PID: $PID), killing it..."
            kill -9 $PID
    else
              echo "Port $PORT is not in use, no process to kill."
fi

# 启动服务
echo "Starting $JAR_NAME on port $PORT with profile $PROFILE..."
nohup java -jar $JAR_NAME --server.port=$PORT --spring.profiles.active=$PROFILE > web-customer.log 2>&1 &

echo "$JAR_NAME started. Logs: web-customer.log"

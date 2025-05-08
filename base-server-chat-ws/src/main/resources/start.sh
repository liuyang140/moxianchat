#!/bin/bash

JAR_NAME="base-server-chat-ws.jar"
PORT=10012
PROFILE="prod"

# 查找端口是否被占用
PID=$(lsof -t -i:$PORT)

# 打印端口状态
if [ -n "$PID" ]; then
          echo "进程端口已被占用 port $PORT (PID: $PID), 正在关闭..."
            kill -9 $PID
    else
              echo "Port $PORT 未被使用, 没有进程可关闭."
fi

# 启动服务
echo "启动 $JAR_NAME 端口 $PORT 启动中, 启动环境: $PROFILE..."
nohup java -jar $JAR_NAME --server.port=$PORT --spring.profiles.active=$PROFILE > web-chat-ws.log 2>&1 &

echo "$JAR_NAME 已启动. Logs: web-chat-ws.log"

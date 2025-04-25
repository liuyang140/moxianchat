#!/bin/bash

JAR_NAME="base-web-customer.jar"
PORT=10010
PROFILE="prod"

# 查找端口是否被占用
PID=$(lsof -t -i:$PORT)

# 打印端口状态
if [ -n "$PID" ]; then
          echo "找到正在运行的进程，端口号 port $PORT (PID: $PID), 正在关闭..."
            kill -9 $PID
    else
              echo "Port $PORT 端口号未被占用, 没有进程可关闭."
fi

# 启动服务
echo "正在启动 $JAR_NAME 端口号 $PORT 配置文件 $PROFILE..."
nohup java -jar $JAR_NAME --server.port=$PORT --spring.profiles.active=$PROFILE > web-customer.log 2>&1 &

echo "$JAR_NAME 进程已启动. Logs: web-customer.log"

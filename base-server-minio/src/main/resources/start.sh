#!/bin/bash

# 配置项
JAR_NAME="base-server-minio.jar"
PORT=10013
PROFILE="prod"

echo "检查是否有运行在端口 $PORT 的进程..."
PID=$(lsof -i :$PORT -t)

if [ -n "$PID" ]; then
          echo "检测到进程 PID=$PID 正在使用端口 $PORT，准备杀死..."
            kill -9 $PID
              echo "已杀死进程 $PID"
      else
                echo "端口 $PORT 没有被占用"
fi

echo "🚀 正在以 profile=$PROFILE 启动 $JAR_NAME ..."
nohup java -jar -Dspring.profiles.active=$PROFILE $JAR_NAME > minio.log 2>&1 &

echo "启动命令已执行，日志输出到 minio.log"

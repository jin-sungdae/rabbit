#!/bin/sh
echo "Starting Spring Boot application..."
# Spring Boot를 백그라운드로 실행하고 프로세스 ID를 저장합니다.
java -jar app.jar &
SPRING_PID=$!

# Spring Boot 프로세스가 종료되면 메시지를 남기고, 컨테이너는 계속 대기합니다.
wait $SPRING_PID || echo "Spring Boot terminated, but container remains running for troubleshooting."

# 무한 대기하여 컨테이너가 종료되지 않도록 합니다.
tail -f /dev/null

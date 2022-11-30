FROM openjdk:17-jdk-alpine
EXPOSE 10000
COPY build/libs/transfer-service-1.0-SNAPSHOT.jar .
RUN apk add --no-cache bash
COPY wait-for.sh /wait-for.sh
RUN chmod +x /wait-for.sh
ENTRYPOINT ["/wait-for.sh", "bill-service:8082", "--timeout=130", "--", "java", "-jar", "transfer-service-1.0-SNAPSHOT.jar"]
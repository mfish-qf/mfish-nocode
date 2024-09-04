FROM openjdk:21

WORKDIR /app

COPY target/mf-*.jar /app/
COPY src/main/resources/prod/* /app/

ENTRYPOINT ["java", "-jar", "mf-start-boot.jar"]

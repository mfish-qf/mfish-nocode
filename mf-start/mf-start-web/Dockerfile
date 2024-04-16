FROM openjdk:8

WORKDIR /app

COPY target/mf-*.jar /app/

ENTRYPOINT ["java", "-jar", "mf-web.jar"]
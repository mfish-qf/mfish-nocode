FROM openjdk:17

WORKDIR /app

COPY target/mf-*.jar /app/

ENTRYPOINT ["java", "-jar", "mf-oauth.jar"]
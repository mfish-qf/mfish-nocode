FROM openjdk:21

WORKDIR /app

COPY target/mf-*.jar /app/

ENTRYPOINT ["java", "-jar", "mf-demo.jar"]
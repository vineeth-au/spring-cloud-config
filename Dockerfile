FROM azul/zulu-openjdk:17

WORKDIR /app


COPY target/spring-config-test-0.1-SNAPSHOT.jar /app/app.jar

# Specify the command to run your application
CMD ["java", "-jar", "app.jar"]
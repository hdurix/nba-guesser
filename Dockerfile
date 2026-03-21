FROM eclipse-temurin:25-jre-alpine
VOLUME /tmp

COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

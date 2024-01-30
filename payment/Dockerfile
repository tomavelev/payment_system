FROM eclipse-temurin:21-jdk
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
#ENTRYPOINT ["java","-jar","/app.jar","import", "users.csv"]
#docker-compose build web
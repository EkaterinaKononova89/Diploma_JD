FROM openjdk:17

EXPOSE 8080

ADD build/libs/Diploma_JD-0.0.1-SNAPSHOT.jar cloud.jar

ENTRYPOINT ["java", "-jar", "/cloud.jar"]
FROM openjdk:17

EXPOSE 5050

ADD target/cloud-storage-0.0.1-SNAPSHOT.jar backend.jar

ENTRYPOINT ["java", "-jar", "backend.jar"]

FROM openjdk:17

WORKDIR /app

COPY target/cloud-storage-0.0.1-SNAPSHOT.jar .

EXPOSE 5050

CMD ["java", "-jar", "cloud-storage-0.0.1-SNAPSHOT.jar"]
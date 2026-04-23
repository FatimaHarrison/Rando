#This Docker file now has the JFR profling to be used.
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/FibonacciSource-1.0-SNAPSHOT.jar app.jar
#Tne entrypoint wired to the JFR file.
ENTRYPOINT ["java", "-XX:StartFlightRecording=duration=60s,filename=/app/profile.jfr", "-jar", "app.jar"]


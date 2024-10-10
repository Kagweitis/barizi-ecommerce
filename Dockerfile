FROM openjdk:17
WORKDIR /app
COPY target/barizi-0.0.1-SNAPSHOT barizi-0.0.1-SNAPSHOT
EXPOSE 8082
CMD ["java", "-jar", "barizi-0.0.1-SNAPSHOT"]
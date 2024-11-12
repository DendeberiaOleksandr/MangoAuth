#
# Build stage
#
FROM maven:3.9-amazoncorretto-21 AS build
COPY . .
RUN mvn clean install

#
# Package stage
#
FROM amazoncorretto:21
COPY --from=build /target/server-0.0.1-SNAPSHOT.jar server.jar
# ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","server.jar"]
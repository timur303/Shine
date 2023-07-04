#FROM maven:3.8.2-jdk-17 AS build
#COPY ./ ./
#RUN mvn clean package -DskipTests
#
#FROM openjdk:17-jdk-hotspot
#
#WORKDIR /app
#
#COPY target/Shine-0.0.1-SNAPSHOT.jar /app/Shine.jar
#
#EXPOSE 9090
#
#CMD ["java", "-jar", "Shine.jar"]

# Build stage
##
#FROM maven:3.8.2-openjdk-17 AS build
#COPY . .
#RUN mvn clean package -Pprod -DskipTests
#
##
## Package stage
##
#FROM openjdk:17-jdk
#COPY --from=build /target/demo-0.0.1-SNAPSHOT.jar demo.jar
#EXPOSE 8080
#ENTRYPOINT ["java","-jar","demo.jar"]

#
# Build stage
#
# Stage 1: Build stage
FROM maven:3.8.2-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -Pprod -DskipTests -DmyCustomOption=value

# Stage 2: Package stage
FROM openjdk:11-jdk
COPY --from=build /app/target/classes /app/classes
EXPOSE 8080
ENTRYPOINT ["java","-cp","/app/classes","ShineApplication"]

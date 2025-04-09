# Stage 1: Cache Gradle dependencies
FROM gradle:8.13 AS cache
RUN apt-get update && apt-get install -y --no-install-recommends openjdk-21-jdk
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME=/home/gradle/cache_home
COPY build.gradle.* gradle.properties /home/gradle/app/
COPY gradle /home/gradle/app/gradle
WORKDIR /home/gradle/app
RUN gradle clean build -D https.protocols=TLSv1.2 -i --stacktrace

# Stage 2: Build Application
FROM gradle:8.13  AS build
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar -D https.protocols=TLSv1.2 --no-daemon

# Stage 3: Create the Runtime Image
FROM amazoncorretto:21 AS runtime
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/emp_server.jar
ENTRYPOINT ["java","-jar","/app/emp_server.jar"]
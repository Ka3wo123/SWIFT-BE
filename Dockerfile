FROM gradle:8.13.0-jdk17-alpine AS build

WORKDIR /swift-app

COPY . .

RUN gradle build --no-daemon -x test

FROM openjdk:17-alpine

WORKDIR /swift-app

COPY --from=build /swift-app/build/libs/swift-*.jar app.jar

CMD ["java", "-jar", "app.jar"]
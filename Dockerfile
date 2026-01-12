FROM clojure:temurin-21-lein AS build
WORKDIR /app

COPY project.clj .
COPY src ./src
COPY resources ./resources
COPY test ./test

RUN lein deps
RUN lein uberjar

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*-standalone.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

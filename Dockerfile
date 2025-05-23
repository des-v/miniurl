FROM maven:3.9.9-ibm-semeru-21-jammy AS build

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build /app/target/miniurl-0.0.1-SNAPSHOT.jar miniurl.jar

EXPOSE 8080

CMD ["java", "-jar", "miniurl.jar"]

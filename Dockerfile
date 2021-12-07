FROM maven:3.8.4-jdk-11 as builder

RUN mkdir /app
RUN mkdir /app/public
COPY src /app/src
COPY pom.xml /app/

WORKDIR /app
RUN --mount=type=cache,target=/root/.m2 mvn -f /app/pom.xml -B dependency:resolve
#RUN mvn dependency:resolve

# Until it's released fake it
COPY contrib/json-schema-validator-1.0.64.jar /app
RUN --mount=type=cache,target=/root/.m2 mvn -f /app/pom.xml -B install:install-file \
    -Dfile=/app/json-schema-validator-1.0.64.jar \
    -DgroupId=com.networknt \
    -DartifactId=json-schema-validator \
    -Dversion=1.0.64 \
    -Dpackaging=jar

RUN --mount=type=cache,target=/root/.m2 mvn -f /app/pom.xml -B package
#RUN mvn -B package

FROM openjdk:11

RUN mkdir /opt/amcrm
RUN mkdir /opt/amcrm/public
COPY --from=builder /app/target/amcrm-1.0-SNAPSHOT.jar /opt/amcrm/amcrm.jar

EXPOSE 4567

WORKDIR /opt/amcrm
CMD ["java", "-jar", "/opt/amcrm/amcrm.jar"]

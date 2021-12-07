FROM maven:3.8.4-jdk-11

RUN mkdir /app
RUN mkdir /app/public
COPY src /app/src
COPY pom.xml /app/

WORKDIR /app
RUN mvn -B package -D'groups=!functional'

FROM openjdk:11

RUN mkdir /opt/amcrm
RUN mkdir /opt/amcrm/public
COPY --from=0 /app/target/amcrm-1.0-SNAPSHOT.jar /opt/amcrm/amcrm.jar

EXPOSE 4567

WORKDIR /opt/amcrm
CMD ["java", "-jar", "/opt/amcrm/amcrm.jar"]

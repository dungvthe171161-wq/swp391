# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace

# Cache dependencies separately from application sources.
COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp clean package -DskipTests \
    && war_file="$(find target -maxdepth 1 -type f -name '*.war' -print -quit)" \
    && test -n "$war_file" \
    && cp "$war_file" /tmp/hrms.war

FROM tomcat:10.1-jre17-temurin

WORKDIR ${CATALINA_HOME}

RUN groupadd --system tomcat \
    && useradd --system --gid tomcat --home-dir "${CATALINA_HOME}" --shell /usr/sbin/nologin tomcat \
    && chown -R tomcat:tomcat "${CATALINA_HOME}"

COPY --from=build --chown=tomcat:tomcat /tmp/hrms.war webapps/HRMS.war

USER tomcat

EXPOSE 8080

CMD ["catalina.sh", "run"]

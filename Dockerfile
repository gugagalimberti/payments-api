# ---------- Estágio 1: build ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# 1) Primeiro só o que define as dependências (aproveita o cache do Docker)
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -q dependency:go-offline

# 2) Depois o código-fonte (muda com frequência)
COPY src src
RUN ./mvnw -q package -DskipTests

# ---------- Estágio 2: runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Não executar como root
RUN groupadd --system spring && useradd --system --gid spring spring
USER spring

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

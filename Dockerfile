# ---------- Estágio 1: build ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# 1) Primeiro só o que define as dependências (aproveita o cache do Docker)
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -q dependency:go-offline

# 2) Depois o código-fonte (muda com frequência), compila e extrai as camadas
COPY src src
RUN ./mvnw -q package -DskipTests \
 && cp target/*.jar app.jar \
 && java -Djarmode=tools -jar app.jar extract --layers --launcher --destination extracted
 
# ---------- Estágio 2: runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Não executar como root
RUN groupadd --system spring && useradd --system --gid spring spring
USER spring

COPY --from=build /workspace/extracted/dependencies/ ./
COPY --from=build /workspace/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/extracted/application/ ./

EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
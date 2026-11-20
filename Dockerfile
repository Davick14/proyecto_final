# ---------- Etapa 1: compilar el proyecto con Maven ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests

# ---------- Etapa 2: imagen final, solo con el JRE ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/target/app.jar app.jar

# Tasa USD -> COP por defecto (se puede cambiar con: docker run -e TASA_DEFECTO=4200 ...)
ENV TASA_DEFECTO=4000
# Puerto en el que escucha la API
ENV PORT=4567
# Carpeta de los archivos JSON (se monta como volumen)
ENV DATA_DIR=/app/data

RUN mkdir -p /app/data

EXPOSE 4567
CMD ["java", "-jar", "app.jar"]

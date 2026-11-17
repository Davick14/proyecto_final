# Cards4Play - Backend

Sistema backend para la gestion de inventario, clientes, compras, boosters y torneos de Cards4Play (TCG).
Proyecto final - Institucion Universitaria EAM, Armenia, Colombia.

## Integrantes
- Nombre 1
- Nombre 2
- Nombre 3

## Tecnologias
- Java (IntelliJ IDEA)
- Spark Java (API REST)
- Persistencia en archivos JSON
- Docker

## Construir la imagen
```bash
docker build -t cards4play .
```

## Ejecutar el contenedor (con volumen y variable de entorno)
```bash
docker run -p 4567:4567 -v cards4play-data:/app/data -e TASA_DEFECTO=4000 cards4play
```

## API externa utilizada
_(pendiente: nombre y URL de la API de conversion USD -> COP)_

## Tasa por defecto
_(pendiente: valor de TASA_DEFECTO)_

## Endpoints
_(pendiente: listado de endpoints)_

## URL publica (PaaS)
_(pendiente)_

## Flujo de trabajo con Git
- `main`: version estable.
- Una rama por funcionalidad: `feature/boosters`, `feature/torneos`, etc., integradas a `main` con merge.

## Resolucion de conflicto de fusion
_(pendiente: documentar al menos un conflicto resuelto)_

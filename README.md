# Cards4Play - API REST

API REST desarrollada en Java con Spark para la gestión de inventario, ventas, apertura de boosters y torneos de cartas coleccionables.

## Integrantes
* **David Hurtado Saldarriaga** (GitHub: `Davick14`)
* **Matías** (GitHub: `LaMamaburra69`)

## Despliegue en la Nube
* **URL pública:** https://proyectofinal-production-39d61254.up.railway.app
* **Credenciales de administrador:** `admin` / `admin123` (`Basic Auth`)

## Ejecución con Docker

1. **Construir la imagen:**
   ```bash
   docker build -t cards4play .
   ```

2. **Crear el volumen para persistencia de datos JSON:**
   ```bash
   docker volume create datos_cards4play
   ```

3. **Ejecutar el contenedor con volumen y variable de entorno:**
   ```bash
   docker run -d -p 4567:4567 -v datos_cards4play:/app/data -e TASA_DEFECTO=4000.0 --name cards4play cards4play
   ```

## Conversión de Moneda (API Externa)
* **API utilizada:** `https://open.er-api.com/v6/latest/USD` para consultar la tasa de cambio vigente de USD a COP.
* **Tasa por defecto:** `4000.0` COP (configurable mediante la variable de entorno `TASA_DEFECTO`), aplicada automáticamente cuando la API externa no responde o supera el tiempo de espera.

## Endpoints Disponibles

### Productos e Inventario
| Método | Ruta | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/cartas` | Administrador | Registrar carta (`nombre`, `precioUSD`, `rareza`) |
| `POST` | `/productos-sellados` | Administrador | Registrar producto sellado (`nombre`, `precioUSD`, `descripcion`) |
| `POST` | `/accesorios` | Administrador | Registrar accesorio (`nombre`, `precioUSD`, `tipo`) |
| `GET` | `/productos` | Autenticado | Listar productos disponibles en inventario |
| `GET` | `/cartas` | Autenticado | Listar cartas disponibles |
| `GET` | `/productos-sellados` | Autenticado | Listar productos sellados disponibles |
| `GET` | `/accesorios` | Autenticado | Listar accesorios disponibles |
| `GET` | `/productos/:id` | Autenticado | Consultar un producto por ID |
| `DELETE` | `/productos/:id` | Administrador | Eliminar un producto del inventario |

### Boosters
| Método | Ruta | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/boosters` | Administrador | Crear booster de 10 cartas (`nombre`, `precioUSD`) |
| `GET` | `/boosters` | Autenticado | Listar boosters disponibles (sin revelar contenido) |
| `GET` | `/boosters/:id` | Autenticado | Consultar un booster por ID |
| `POST` | `/boosters/:id/abrir` | Cliente (dueño) | Abrir un booster comprado por única vez |

### Clientes y Compras
| Método | Ruta | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/clientes` | Administrador | Registrar cliente (`usuario`, `contrasena`) |
| `GET` | `/clientes` | Administrador | Listar todos los clientes |
| `GET` | `/clientes/:id` | Administrador | Consultar un cliente por ID |
| `GET` | `/clientes/:id/historial` | Admin / Propio cliente | Consultar historial de compras de un cliente |
| `POST` | `/compras` | Cliente | Registrar compra (`items` con `productoId` y `cantidad`) |
| `GET` | `/compras` | Autenticado | Listar compras (del cliente autenticado o todas si es admin) |

### Torneos
| Método | Ruta | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/torneos` | Administrador | Crear torneo (`nombre`, `fechaProgramada`, `cupoMaximo`) |
| `GET` | `/torneos` | Autenticado | Listar todos los torneos |
| `GET` | `/torneos/:id` | Autenticado | Consultar un torneo por ID |
| `POST` | `/torneos/:id/inscripciones` | Cliente | Inscribir al cliente autenticado en un torneo |
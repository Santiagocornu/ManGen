# GenMan API

GenMan es una API REST para administrar la operacion de una sucursal: materias primas, productos, proveedores, pedidos, ventas, usuarios y control de stock. Esta construida con Spring Boot, Spring Security, JWT y JPA.

La aplicacion esta pensada para trabajar con multiples sucursales. Cada usuario pertenece a una sucursal y, al autenticarse, el token JWT define sobre que sucursal puede operar. Los datos de negocio quedan aislados por sucursal.

## Funcionalidades principales

- Autenticacion con JWT.
- Registro inicial de una sucursal con usuario administrador.
- Gestion de usuarios por sucursal.
- Gestion de materias primas, productos y proveedores.
- Relacion entre productos y materias primas, con cantidades.
- Relacion entre proveedores y materias primas, con marca y precio ofertado.
- Gestion de pedidos y productos incluidos.
- Gestion de ventas.
- Creacion de ventas desde pedidos.
- Ajuste de stock al operar ventas o relaciones marcadas `con-stock`.
- Validacion de pago de sucursal antes de permitir el uso de endpoints protegidos.
- Filtro anti spam configurable para rechazar solicitudes identicas en una ventana corta.
- Swagger UI para explorar la API.

## Stack

- Java 17
- Spring Boot 4
- Spring Web
- Spring Security
- Spring Data JPA
- JWT con `jjwt`
- MySQL o PostgreSQL, segun la URL configurada
- Maven Wrapper

## Requisitos

- JDK 17 instalado y `JAVA_HOME` configurado.
- Base de datos disponible.
- Variables de entorno configuradas en `.env` o en el entorno del sistema.

Variables esperadas:

```properties
MYSQL_URL=jdbc:mysql://localhost:3306/ManGen
MYSQL_USER=root
MYSQL_PASSWORD=1234
APP_SECRET=una_clave_segura
```

El archivo `src/main/resources/application.properties` importa automaticamente `.env`:

```properties
spring.config.import=optional:file:.env[.properties]
```

## Ejecutar localmente

```powershell
.\mvnw.cmd spring-boot:run
```

La API levanta por defecto en:

```text
http://localhost:8080
```

Swagger queda disponible en:

```text
http://localhost:8080/swagger-ui/index.html
```

## Seguridad

La API usa JWT en los endpoints protegidos:

```text
Authorization: Bearer TU_TOKEN
```

Endpoints publicos:

- `POST /auth/login`
- `POST /auth/register-sucursal-admin`
- `POST /auth/pagar-sucursal`
- `POST /auth/desactivar-pago-sucursal`
- `GET /auth/sucursales`
- Swagger y OpenAPI

Los endpoints de negocio requieren token valido. Ademas:

- `/apiManGen/Sucursal/**` requiere rol `ADMIN`.
- `/apiManGen/User/**` requiere rol `ADMIN`.
- El usuario solo accede a datos de su propia sucursal.
- Si la sucursal no esta marcada como paga, la API responde `402 Payment Required`.

## Primer uso

1. Crear una sucursal con su administrador:

```http
POST /auth/register-sucursal-admin
Content-Type: application/json
```

```json
{
  "nombreSucursal": "Sucursal Centro",
  "nombreAdmin": "Admin Centro",
  "emailAdmin": "admin@centro.com",
  "passwordAdmin": "1234"
}
```

2. Usar el `token` de la respuesta para las solicitudes protegidas.

3. Crear usuarios, productos, materias primas, proveedores, pedidos y ventas desde los endpoints `/apiManGen`.

## Login

```http
POST /auth/login
Content-Type: application/json
```

```json
{
  "email": "admin@centro.com",
  "password": "1234",
  "sucursalId": 1
}
```

Respuesta:

```json
{
  "token": "jwt...",
  "user": {
    "id": 1,
    "nombre": "Admin Centro",
    "email": "admin@centro.com",
    "roll": "ADMIN",
    "sucursalId": 1
  }
}
```

## Endpoints principales

Base de negocio:

```text
/apiManGen
```

### Auth

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `POST` | `/auth/login` | Inicia sesion y devuelve JWT |
| `POST` | `/auth/register-sucursal-admin` | Crea sucursal y admin inicial |
| `POST` | `/auth/change-password` | Cambia la contrasena de un usuario |
| `POST` | `/auth/pagar-sucursal` | Marca una sucursal como paga usando `Key` |
| `POST` | `/auth/desactivar-pago-sucursal` | Desactiva el pago de una sucursal |
| `GET` | `/auth/sucursales` | Lista sucursales para gestion externa usando `Key` |

### Sucursales

Base: `/apiManGen/Sucursal`

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `GET` | `/` | Lista sucursales |
| `POST` | `/` | Crea sucursal |
| `GET` | `/{id}` | Obtiene sucursal |
| `PUT` | `/{id}` | Actualiza sucursal |
| `DELETE` | `/{id}` | Elimina sucursal |
| `POST` | `/{id}/backfill` | Asigna sucursal a datos antiguos sin `sucursal_id` |

### Usuarios

Base: `/apiManGen/User`

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `GET` | `/` | Lista usuarios de la sucursal |
| `POST` | `/` | Crea usuario en la sucursal del admin |
| `GET` | `/{id}` | Obtiene usuario |
| `PUT` | `/{id}` | Actualiza usuario |
| `DELETE` | `/{id}` | Elimina usuario |

### Materias primas

Base: `/apiManGen/Materia_prima`

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `GET` | `/` | Lista materias primas |
| `POST` | `/` | Crea materia prima |
| `GET` | `/{id}` | Obtiene materia prima |
| `PUT` | `/{id}` | Actualiza materia prima |
| `DELETE` | `/{id}` | Elimina materia prima |
| `GET` | `/{id}/productos` | Lista productos relacionados |
| `POST` | `/{id}/productos` | Relaciona producto |
| `POST` | `/{id}/productos/con-stock` | Relaciona producto ajustando stock |
| `PATCH` | `/{id}/productos/{productoId}/cantidad` | Cambia cantidad relacionada |
| `PATCH` | `/{id}/productos/{productoId}/cantidad/con-stock` | Cambia cantidad ajustando stock |
| `GET` | `/{id}/proveedores` | Lista proveedores relacionados |
| `POST` | `/{id}/proveedores` | Relaciona proveedor con marca y precio |
| `PATCH` | `/{id}/proveedores/{proveedorId}/oferta` | Cambia oferta del proveedor |
| `DELETE` | `/{id}/proveedores/{proveedorId}` | Elimina relacion con proveedor |

### Productos

Base: `/apiManGen/Producto`

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `GET` | `/` | Lista productos |
| `POST` | `/` | Crea producto |
| `GET` | `/{id}` | Obtiene producto |
| `PUT` | `/{id}` | Actualiza producto |
| `DELETE` | `/{id}` | Elimina producto |
| `GET` | `/{id}/materias-primas` | Lista materias primas relacionadas |
| `POST` | `/{id}/materias-primas` | Relaciona materia prima |
| `POST` | `/{id}/materias-primas/con-stock` | Relaciona materia prima ajustando stock |
| `PATCH` | `/{id}/materias-primas/{materiaPrimaId}/cantidad` | Cambia cantidad relacionada |
| `PATCH` | `/{id}/materias-primas/{materiaPrimaId}/cantidad/con-stock` | Cambia cantidad ajustando stock |

### Proveedores

Base: `/apiManGen/Proveedor`

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `GET` | `/` | Lista proveedores |
| `POST` | `/` | Crea proveedor |
| `GET` | `/{id}` | Obtiene proveedor |
| `PUT` | `/{id}` | Actualiza proveedor |
| `DELETE` | `/{id}` | Elimina proveedor |
| `GET` | `/{id}/materias-primas` | Lista materias primas ofrecidas |
| `POST` | `/{id}/materias-primas` | Relaciona materia prima con marca y precio |
| `PATCH` | `/{id}/materias-primas/{materiaPrimaId}/oferta` | Cambia oferta |
| `DELETE` | `/{id}/materias-primas/{materiaPrimaId}` | Elimina relacion |

### Pedidos

Base: `/apiManGen/Pedidos`

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `GET` | `/` | Lista pedidos |
| `POST` | `/` | Crea pedido |
| `GET` | `/{id}` | Obtiene pedido |
| `PUT` | `/{id}` | Actualiza pedido |
| `DELETE` | `/{id}` | Elimina pedido |
| `GET` | `/{id}/productos` | Lista productos del pedido |
| `POST` | `/{id}/productos` | Agrega o actualiza producto del pedido |
| `PATCH` | `/{id}/productos/{productoId}/cantidad` | Cambia cantidad del producto |

### Ventas

Base: `/apiManGen/Ventas`

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `GET` | `/` | Lista ventas |
| `POST` | `/` | Crea venta |
| `POST` | `/desde-pedido/{pedidoId}` | Crea venta desde pedido |
| `POST` | `/desde-pedido/{pedidoId}/con-stock` | Crea venta desde pedido y descuenta stock |
| `GET` | `/{id}` | Obtiene venta |
| `PUT` | `/{id}` | Actualiza venta |
| `DELETE` | `/{id}` | Elimina venta |
| `GET` | `/{id}/productos` | Lista productos vendidos |
| `POST` | `/{id}/productos` | Agrega o actualiza producto |
| `POST` | `/{id}/productos/con-stock` | Agrega producto y descuenta stock |
| `PATCH` | `/{id}/productos/{productoId}/cantidad` | Cambia cantidad |
| `PATCH` | `/{id}/productos/{productoId}/cantidad/con-stock` | Cambia cantidad ajustando stock |
| `DELETE` | `/{id}/productos/{productoId}` | Elimina producto de la venta |
| `DELETE` | `/{id}/productos/{productoId}/con-stock` | Elimina producto y repone stock |

## Ejemplos de cuerpos JSON

Crear producto:

```json
{
  "asset": "pan.png",
  "nombre": "Pan",
  "precio": 800.0,
  "cantidad": 12.0,
  "unidad": "unidad"
}
```

Crear materia prima:

```json
{
  "asset": "harina.png",
  "nombre": "Harina",
  "precio": 1200.0,
  "unidad": "kg",
  "cantidad": 25.0
}
```

Relacionar producto con materia prima:

```json
{
  "materiaPrimaId": 1,
  "cantidad": 2
}
```

Relacionar venta o pedido con producto:

```json
{
  "productoId": 1,
  "cantidad": 3
}
```

Relacionar proveedor con materia prima:

```json
{
  "materiaPrimaId": 1,
  "proveedorId": 2,
  "marca": "Molino Norte",
  "precio": 950.0
}
```

## Anti spam

La API incluye un filtro anti spam para evitar que dos solicitudes identicas lleguen en milisegundos y se procesen dos veces. Si una request repite la misma firma dentro de la ventana configurada, responde:

```text
429 Too Many Requests
```

Configuracion:

```properties
app.anti-spam.enabled=true
app.anti-spam.window-ms=500
app.anti-spam.max-entries=10000
```

## Errores

Los errores de negocio se devuelven con un formato consistente:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "El total no puede ser nulo",
  "timestamp": "2026-05-20T10:30:00"
}
```

Codigos comunes:

| Codigo | Significado |
| --- | --- |
| `200` | Operacion correcta |
| `201` | Recurso creado |
| `204` | Recurso eliminado |
| `400` | Datos invalidos |
| `401` | Falta token o el token no es valido |
| `402` | Sucursal sin pago activo |
| `403` | Sin permisos |
| `404` | Recurso no encontrado |
| `429` | Solicitud duplicada o spam |
| `500` | Error interno |

## Comandos utiles

Ejecutar la app:

```powershell
.\mvnw.cmd spring-boot:run
```

Ejecutar tests:

```powershell
.\mvnw.cmd test
```

Compilar:

```powershell
.\mvnw.cmd clean package
```

## Notas de desarrollo

- Los endpoints protegidos dependen de `JwtFilter`.
- El contexto de sucursal se toma del token y se guarda durante la request.
- Los services son responsables de filtrar y validar datos por sucursal.
- Las relaciones entre entidades validan que los IDs pertenezcan a la misma sucursal.
- La documentacion interactiva se genera con Springdoc OpenAPI.

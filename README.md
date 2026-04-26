# GenMan API

API REST para gestionar:

- Materias primas
- Productos
- Pedidos
- Ventas
- Usuarios
- Sucursales
- Relaciones entre esas entidades
- Autenticacion JWT

## Base URL

La aplicacion corre por defecto en:

```text
http://localhost:8080
```

Prefijo comun de la API:

```text
http://localhost:8080/apiManGen
```

## Requisitos

- Java 17
- Maven Wrapper incluido en el proyecto
- MySQL corriendo localmente

Configuracion actual en `src/main/resources/application.properties`:

- Puerto: `8080`
- Base de datos: `ManGen`
- Usuario: `root`
- Password: `1234`

## Levantar el proyecto

```powershell
./mvnw.cmd spring-boot:run
```

## Documentacion Swagger

Si la aplicacion esta levantada, la UI de Swagger suele quedar disponible en:

```text
http://localhost:8080/swagger-ui/index.html
```

## Autenticacion

La API usa JWT.

- `POST /auth/login` es publico.
- El resto de los endpoints requieren `Authorization: Bearer <token>`.
- En cada request protegida, el backend valida la firma del token, extrae el `userId` y el `sucursal_id` del JWT y comprueba que ese usuario exista en la base local.
- Cada usuario solo puede ver y modificar datos de su propia sucursal.
- Los endpoints de `User` y `Sucursal` solo pueden ser usados por usuarios con rol `ADMIN`.
- Si falta el token, es invalido o el usuario ya no existe, responde `401 Unauthorized`.

Formato del header:

```text
Authorization: Bearer eyJhbGciOi...
```

## Multi-sucursal

El proyecto funciona con aislamiento logico por `Sucursal`.

- Cada `User` pertenece a una sucursal.
- `MateriaPrima`, `Producto`, `Pedidos` y `Ventas` tambien pertenecen a una sucursal.
- El token lleva el `sucursal_id`.
- Todos los services consultan y modifican solo los datos de la sucursal del token.
- Un admin puede crear y administrar usuarios de su misma sucursal.

## Formato de errores

Cuando ocurre un error, la API responde con este formato:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "El total no puede ser nulo",
  "timestamp": "2026-04-25T22:35:12.123"
}
```

## Modelos principales

### MateriaPrimaDTO

```json
{
  "id": 1,
  "asset": "harina.png",
  "nombre": "Harina",
  "precio": 1200.0,
  "unidad": "kg",
  "cantidad": 25.0
}
```

### ProductoDTO

```json
{
  "id": 1,
  "asset": "pan.png",
  "nombre": "Pan",
  "precio": 800.0,
  "cantidad": 12.0,
  "unidad": "unidad"
}
```

### PedidosDTO

```json
{
  "id": 1,
  "total": 5000.0,
  "totalDesc": 4500.0,
  "descripcion": "Pedido para cliente mayorista",
  "estado": "Pendiente"
}
```

### VentasDTO

```json
{
  "id": 1,
  "total": 5000.0,
  "totalDesc": 4500.0,
  "metodoPago": "Efectivo",
  "fecha": null
}
```

### UserDTO

```json
{
  "id": 1,
  "nombre": "Juan",
  "fechaCreacion": "2026-04-26T00:00:00.000+00:00",
  "email": "juan@mail.com",
  "roll": "ADMIN",
  "sucursalId": 1
}
```

La contraseña no se devuelve en ninguna respuesta.

### SucursalDTO

```json
{
  "id": 1,
  "nombre": "Arcor"
}
```

### LoginRequestDTO

```json
{
  "email": "juan@mail.com",
  "password": "1234"
}
```

### LoginResponseDTO

```json
{
  "token": "jwt...",
  "user": {
    "id": 1,
    "nombre": "Juan",
    "fechaCreacion": "2026-04-26T00:00:00.000+00:00",
    "email": "juan@mail.com",
    "roll": "ADMIN",
    "sucursalId": 1
  }
}
```

### BackfillResultDTO

```json
{
  "sucursalId": 1,
  "usuariosActualizados": 3,
  "materiasPrimasActualizadas": 12,
  "productosActualizados": 8,
  "pedidosActualizados": 4,
  "ventasActualizadas": 5
}
```

### DTOs de relaciones

Asignar producto:

```json
{
  "productoId": 1,
  "cantidad": 3
}
```

Asignar materia prima:

```json
{
  "materiaPrimaId": 1,
  "cantidad": 2
}
```

## Endpoints

## Auth

Base path: `/auth`

### `POST /auth/login`

Inicia sesion con email y contraseña y devuelve un JWT mas los datos del usuario autenticado.

Body:

```json
{
  "email": "juan@mail.com",
  "password": "1234"
}
```

Respuesta:

```json
{
  "token": "jwt...",
  "user": {
    "id": 1,
    "nombre": "Juan",
    "fechaCreacion": "2026-04-26T00:00:00.000+00:00",
    "email": "juan@mail.com",
    "roll": "ADMIN",
    "sucursalId": 1
  }
}
```

### `POST /auth/register-sucursal-admin`

Endpoint publico para alta inicial desde frontend.

Crea:

- una sucursal nueva
- un usuario admin para esa sucursal
- un JWT listo para usar

Body:

```json
{
  "nombreSucursal": "Arcor",
  "nombreAdmin": "Admin Arcor",
  "emailAdmin": "admin@arcor.com",
  "passwordAdmin": "1234"
}
```

Respuesta:

```json
{
  "token": "jwt...",
  "sucursal": {
    "id": 1,
    "nombre": "Arcor"
  },
  "user": {
    "id": 1,
    "nombre": "Admin Arcor",
    "fechaCreacion": "2026-04-26T00:00:00.000+00:00",
    "email": "admin@arcor.com",
    "roll": "ADMIN",
    "sucursalId": 1
  }
}
```

### `POST /auth/change-password`

Cambia la contraseña del usuario dentro de su misma sucursal.

Requiere token.

Parametros:

- `email`
- `password`
- `newPassword`

Ejemplo:

```text
POST /auth/change-password?email=juan@mail.com&password=1234&newPassword=abcd1234
```

## Sucursal

Base path: `/apiManGen/Sucursal`

Todos los endpoints de sucursal requieren token y rol `ADMIN`.

### `GET /apiManGen/Sucursal`

Lista todas las sucursales.

### `POST /apiManGen/Sucursal`

Crea una sucursal.

Body:

```json
{
  "nombre": "Arcor"
}
```

### `GET /apiManGen/Sucursal/{id}`

Obtiene una sucursal por id.

### `PUT /apiManGen/Sucursal/{id}`

Actualiza una sucursal.

Body:

```json
{
  "nombre": "Arcor Norte"
}
```

### `DELETE /apiManGen/Sucursal/{id}`

Elimina una sucursal vacia.

Si la sucursal tiene usuarios o datos asociados, responde error.

### `POST /apiManGen/Sucursal/{id}/backfill`

Asigna esa sucursal a datos viejos que todavia no tengan `sucursal_id`.

Actualiza:

- usuarios sin sucursal
- materias primas sin sucursal
- productos sin sucursal
- pedidos sin sucursal
- ventas sin sucursal

Respuesta:

```json
{
  "sucursalId": 1,
  "usuariosActualizados": 3,
  "materiasPrimasActualizadas": 12,
  "productosActualizados": 8,
  "pedidosActualizados": 4,
  "ventasActualizadas": 5
}
```

## User

Base path: `/apiManGen/User`

Todos los endpoints de usuario requieren token y rol `ADMIN`.

Los usuarios creados desde esta API quedan asociados automaticamente a la misma sucursal del admin autenticado.

### `GET /apiManGen/User`

Lista todos los usuarios de la misma sucursal del admin autenticado.

### `POST /apiManGen/User`

Crea un usuario dentro de la sucursal del admin autenticado.

Body:

```json
{
  "nombre": "Juan",
  "fechaCreacion": "2026-04-26T00:00:00.000+00:00",
  "email": "juan@mail.com",
  "password": "1234",
  "roll": "ADMIN"
}
```

No hace falta mandar `sucursalId`: el backend usa la sucursal del token.

### `GET /apiManGen/User/{id}`

Obtiene un usuario por id dentro de la misma sucursal del admin autenticado.

### `PUT /apiManGen/User/{id}`

Actualiza un usuario dentro de la misma sucursal del admin autenticado.

Body:

```json
{
  "nombre": "Juan Perez",
  "fechaCreacion": "2026-04-26T00:00:00.000+00:00",
  "email": "juan@mail.com",
  "password": "nueva1234",
  "roll": "ADMIN"
}
```

### `DELETE /apiManGen/User/{id}`

Elimina un usuario dentro de la misma sucursal del admin autenticado.

## Materia prima

Base path: `/apiManGen/Materia_prima`

Todos los endpoints requieren token y operan solo sobre la sucursal del usuario autenticado.

### `GET /apiManGen/Materia_prima`

Lista todas las materias primas de la sucursal actual.

### `POST /apiManGen/Materia_prima`

Crea una materia prima en la sucursal actual.

### `GET /apiManGen/Materia_prima/{id}`

Obtiene una materia prima por id de la sucursal actual.

### `PUT /apiManGen/Materia_prima/{id}`

Actualiza una materia prima de la sucursal actual.

### `DELETE /apiManGen/Materia_prima/{id}`

Elimina una materia prima de la sucursal actual.

### `GET /apiManGen/Materia_prima/{id}/productos`

Devuelve los productos asociados a una materia prima de la sucursal actual.

### `POST /apiManGen/Materia_prima/{id}/productos`

Asocia un producto a una materia prima de la misma sucursal.

### `PATCH /apiManGen/Materia_prima/{id}/productos/{productoId}/cantidad`

Cambia la cantidad de la relacion entre materia prima y producto.

## Producto

Base path: `/apiManGen/Producto`

Todos los endpoints requieren token y operan solo sobre la sucursal del usuario autenticado.

### `GET /apiManGen/Producto`

Lista todos los productos de la sucursal actual.

### `POST /apiManGen/Producto`

Crea un producto en la sucursal actual.

### `GET /apiManGen/Producto/{id}`

Obtiene un producto por id de la sucursal actual.

### `PUT /apiManGen/Producto/{id}`

Actualiza un producto de la sucursal actual.

### `DELETE /apiManGen/Producto/{id}`

Elimina un producto de la sucursal actual.

### `GET /apiManGen/Producto/{id}/materias-primas`

Devuelve las materias primas asociadas a un producto de la sucursal actual.

### `POST /apiManGen/Producto/{id}/materias-primas`

Asocia una materia prima al producto dentro de la misma sucursal.

### `PATCH /apiManGen/Producto/{id}/materias-primas/{materiaPrimaId}/cantidad`

Cambia la cantidad de la relacion entre producto y materia prima.

## Pedidos

Base path: `/apiManGen/Pedidos`

Todos los endpoints requieren token y operan solo sobre la sucursal del usuario autenticado.

### `GET /apiManGen/Pedidos`

Lista todos los pedidos de la sucursal actual.

### `POST /apiManGen/Pedidos`

Crea un pedido en la sucursal actual.

### `GET /apiManGen/Pedidos/{id}`

Obtiene un pedido por id de la sucursal actual.

### `PUT /apiManGen/Pedidos/{id}`

Actualiza un pedido de la sucursal actual.

### `DELETE /apiManGen/Pedidos/{id}`

Elimina un pedido de la sucursal actual.

### `GET /apiManGen/Pedidos/{id}/productos`

Devuelve los productos asociados a un pedido de la sucursal actual.

### `POST /apiManGen/Pedidos/{id}/productos`

Asocia un producto a un pedido dentro de la misma sucursal.

### `PATCH /apiManGen/Pedidos/{id}/productos/{productoId}/cantidad`

Cambia la cantidad de la relacion entre pedido y producto.

## Ventas

Base path: `/apiManGen/Ventas`

Todos los endpoints requieren token y operan solo sobre la sucursal del usuario autenticado.

### `GET /apiManGen/Ventas`

Lista todas las ventas de la sucursal actual.

### `POST /apiManGen/Ventas`

Crea una venta en la sucursal actual.

### `POST /apiManGen/Ventas/desde-pedido/{pedidoId}`

Crea una venta a partir de un pedido existente de la sucursal actual y marca el pedido como `Terminado`.

### `POST /apiManGen/Ventas/desde-pedido/{pedidoId}/con-stock`

Crea una venta a partir de un pedido existente de la sucursal actual, marca el pedido como `Terminado` y descuenta stock.

### `GET /apiManGen/Ventas/{id}`

Obtiene una venta por id de la sucursal actual.

### `PUT /apiManGen/Ventas/{id}`

Actualiza una venta de la sucursal actual.

### `DELETE /apiManGen/Ventas/{id}`

Elimina una venta de la sucursal actual.

### `GET /apiManGen/Ventas/{id}/productos`

Devuelve los productos asociados a una venta de la sucursal actual.

### `POST /apiManGen/Ventas/{id}/productos`

Asocia un producto a una venta de la misma sucursal.

### `POST /apiManGen/Ventas/{id}/productos/con-stock`

Asocia un producto a una venta de la misma sucursal ajustando stock.

### `PATCH /apiManGen/Ventas/{id}/productos/{productoId}/cantidad`

Cambia la cantidad de la relacion entre venta y producto.

### `PATCH /apiManGen/Ventas/{id}/productos/{productoId}/cantidad/con-stock`

Cambia la cantidad ajustando stock.

### `DELETE /apiManGen/Ventas/{id}/productos/{productoId}`

Elimina la relacion entre venta y producto sin tocar stock.

### `DELETE /apiManGen/Ventas/{id}/productos/{productoId}/con-stock`

Elimina la relacion entre venta y producto y repone stock.

## Codigos de estado mas comunes

- `200 OK`: consulta o actualizacion correcta
- `201 Created`: recurso creado correctamente
- `204 No Content`: recurso eliminado correctamente
- `400 Bad Request`: datos invalidos o validaciones de negocio
- `401 Unauthorized`: token faltante o invalido
- `403 Forbidden`: el usuario no tiene permisos para el recurso
- `404 Not Found`: recurso no encontrado
- `500 Internal Server Error`: error interno del servidor

## Ejemplos con curl

Login:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"juan@mail.com\",\"password\":\"1234\"}"
```

Registro inicial de sucursal con admin:

```bash
curl -X POST http://localhost:8080/auth/register-sucursal-admin \
  -H "Content-Type: application/json" \
  -d "{\"nombreSucursal\":\"Arcor\",\"nombreAdmin\":\"Admin Arcor\",\"emailAdmin\":\"admin@arcor.com\",\"passwordAdmin\":\"1234\"}"
```

Crear sucursal con token de admin:

```bash
curl -X POST http://localhost:8080/apiManGen/Sucursal \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN_ADMIN" \
  -d "{\"nombre\":\"Arcor\"}"
```

Crear usuario desde un admin de sucursal:

```bash
curl -X POST http://localhost:8080/apiManGen/User \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN_ADMIN" \
  -d "{\"nombre\":\"Operador 1\",\"fechaCreacion\":\"2026-04-26T00:00:00.000+00:00\",\"email\":\"op1@arcor.com\",\"password\":\"1234\",\"roll\":\"USER\"}"
```

Backfill de datos viejos sin sucursal:

```bash
curl -X POST http://localhost:8080/apiManGen/Sucursal/1/backfill \
  -H "Authorization: Bearer TU_TOKEN_ADMIN"
```

Crear producto con token:

```bash
curl -X POST http://localhost:8080/apiManGen/Producto \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN" \
  -d "{\"asset\":\"pan.png\",\"nombre\":\"Pan\",\"precio\":800.0,\"cantidad\":12.0,\"unidad\":\"unidad\"}"
```

Obtener materias primas de un producto:

```bash
curl http://localhost:8080/apiManGen/Producto/1/materias-primas \
  -H "Authorization: Bearer TU_TOKEN"
```

Crear venta desde pedido:

```bash
curl -X POST http://localhost:8080/apiManGen/Ventas/desde-pedido/1 \
  -H "Authorization: Bearer TU_TOKEN"
```

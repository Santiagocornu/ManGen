# GenMan API

API REST para gestionar:

- Materias primas
- Productos
- Pedidos
- Ventas
- Relaciones entre esas entidades

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

## Materia prima

Base path: `/apiManGen/Materia_prima`

### `GET /apiManGen/Materia_prima`

Lista todas las materias primas.

### `POST /apiManGen/Materia_prima`

Crea una materia prima.

Body:

```json
{
  "asset": "harina.png",
  "nombre": "Harina",
  "precio": 1200.0,
  "unidad": "kg",
  "cantidad": 25.0
}
```

### `GET /apiManGen/Materia_prima/{id}`

Obtiene una materia prima por id.

### `PUT /apiManGen/Materia_prima/{id}`

Actualiza una materia prima.

Body:

```json
{
  "asset": "harina-premium.png",
  "nombre": "Harina Premium",
  "precio": 1500.0,
  "unidad": "kg",
  "cantidad": 18.0
}
```

### `DELETE /apiManGen/Materia_prima/{id}`

Elimina una materia prima.

### `GET /apiManGen/Materia_prima/{id}/productos`

Devuelve los productos asociados a una materia prima.

Ejemplo de respuesta:

```json
[
  {
    "productoId": 1,
    "nombre": "Pan",
    "asset": "pan.png",
    "precio": 800.0,
    "unidad": "unidad",
    "cantidadDisponible": 20.0,
    "cantidadRelacionada": 2
  }
]
```

### `POST /apiManGen/Materia_prima/{id}/productos`

Asocia un producto a una materia prima o actualiza la relacion.

Body:

```json
{
  "productoId": 1,
  "cantidad": 2
}
```

Respuesta:

```json
{
  "materiaPrimaId": 1,
  "productoId": 1,
  "cantidad": 2
}
```

### `PATCH /apiManGen/Materia_prima/{id}/productos/{productoId}/cantidad`

Cambia solo la cantidad de la relacion entre materia prima y producto.

Body:

```json
{
  "productoId": 1,
  "cantidad": 5
}
```

## Producto

Base path: `/apiManGen/Producto`

### `GET /apiManGen/Producto`

Lista todos los productos.

### `POST /apiManGen/Producto`

Crea un producto.

Body:

```json
{
  "asset": "pan.png",
  "nombre": "Pan",
  "precio": 800.0,
  "cantidad": 12.0,
  "unidad": "unidad"
}
```

### `GET /apiManGen/Producto/{id}`

Obtiene un producto por id.

### `PUT /apiManGen/Producto/{id}`

Actualiza un producto.

Body:

```json
{
  "asset": "pan-lactal.png",
  "nombre": "Pan Lactal",
  "precio": 1100.0,
  "cantidad": 8.0,
  "unidad": "unidad"
}
```

### `DELETE /apiManGen/Producto/{id}`

Elimina un producto.

### `GET /apiManGen/Producto/{id}/materias-primas`

Devuelve las materias primas asociadas a un producto.

Ejemplo de respuesta:

```json
[
  {
    "materiaPrimaId": 1,
    "nombre": "Harina",
    "asset": "harina.png",
    "precio": 1200.0,
    "unidad": "kg",
    "cantidadDisponible": 25.0,
    "cantidadRelacionada": 2
  }
]
```

### `POST /apiManGen/Producto/{id}/materias-primas`

Asocia una materia prima al producto o actualiza la relacion.

Body:

```json
{
  "materiaPrimaId": 1,
  "cantidad": 2
}
```

Respuesta:

```json
{
  "materiaPrimaId": 1,
  "productoId": 1,
  "cantidad": 2
}
```

### `PATCH /apiManGen/Producto/{id}/materias-primas/{materiaPrimaId}/cantidad`

Cambia solo la cantidad de la relacion entre producto y materia prima.

Body:

```json
{
  "materiaPrimaId": 1,
  "cantidad": 4
}
```

## Pedidos

Base path: `/apiManGen/Pedidos`

### `GET /apiManGen/Pedidos`

Lista todos los pedidos.

### `POST /apiManGen/Pedidos`

Crea un pedido.

Body:

```json
{
  "total": 5000.0,
  "totalDesc": 4500.0,
  "descripcion": "Pedido para cliente mayorista",
  "estado": "Pendiente"
}
```

### `GET /apiManGen/Pedidos/{id}`

Obtiene un pedido por id.

### `PUT /apiManGen/Pedidos/{id}`

Actualiza un pedido.

Body:

```json
{
  "total": 6200.0,
  "totalDesc": 5800.0,
  "descripcion": "Pedido actualizado",
  "estado": "En proceso"
}
```

### `DELETE /apiManGen/Pedidos/{id}`

Elimina un pedido.

### `GET /apiManGen/Pedidos/{id}/productos`

Devuelve los productos asociados a un pedido.

Ejemplo de respuesta:

```json
[
  {
    "productoId": 1,
    "nombre": "Pan",
    "asset": "pan.png",
    "precio": 800.0,
    "unidad": "unidad",
    "cantidadDisponible": 20.0,
    "cantidadRelacionada": 3
  }
]
```

### `POST /apiManGen/Pedidos/{id}/productos`

Asocia un producto a un pedido o actualiza la relacion.

Body:

```json
{
  "productoId": 1,
  "cantidad": 3
}
```

Respuesta:

```json
{
  "pedidosId": 1,
  "productoId": 1,
  "cantidad": 3
}
```

### `PATCH /apiManGen/Pedidos/{id}/productos/{productoId}/cantidad`

Cambia solo la cantidad de la relacion entre pedido y producto.

Body:

```json
{
  "productoId": 1,
  "cantidad": 6
}
```

## Ventas

Base path: `/apiManGen/Ventas`

### `GET /apiManGen/Ventas`

Lista todas las ventas.

### `POST /apiManGen/Ventas`

Crea una venta.

Body:

```json
{
  "total": 7000.0,
  "totalDesc": 6500.0,
  "metodoPago": "Transferencia",
  "fecha": null
}
```

### `POST /apiManGen/Ventas/desde-pedido/{pedidoId}`

Crea una venta a partir de un pedido existente y marca el pedido como `Terminado`.

### `POST /apiManGen/Ventas/desde-pedido/{pedidoId}/con-stock`

Crea una venta a partir de un pedido existente, marca el pedido como `Terminado` y descuenta stock de productos y materias primas segun las cantidades del pedido.

### `GET /apiManGen/Ventas/{id}`

Obtiene una venta por id.

### `PUT /apiManGen/Ventas/{id}`

Actualiza una venta.

Body:

```json
{
  "total": 7100.0,
  "totalDesc": 6700.0,
  "metodoPago": "Tarjeta",
  "fecha": null
}
```

### `DELETE /apiManGen/Ventas/{id}`

Elimina una venta.

### `GET /apiManGen/Ventas/{id}/productos`

Devuelve los productos asociados a una venta.

Ejemplo de respuesta:

```json
[
  {
    "productoId": 1,
    "nombre": "Pan",
    "asset": "pan.png",
    "precio": 800.0,
    "unidad": "unidad",
    "cantidadDisponible": 20.0,
    "cantidadRelacionada": 4
  }
]
```

### `POST /apiManGen/Ventas/{id}/productos`

Asocia un producto a una venta o actualiza la relacion.

Body:

```json
{
  "productoId": 1,
  "cantidad": 4
}
```

Respuesta:

```json
{
  "ventaId": 1,
  "productoId": 1,
  "cantidad": 4
}
```

### `POST /apiManGen/Ventas/{id}/productos/con-stock`

Asocia un producto a una venta o actualiza la relacion, descontando stock del producto y de todas sus materias primas asociadas.

Si el producto ya estaba relacionado con la venta, solo descuenta la diferencia adicional.

Body:

```json
{
  "productoId": 1,
  "cantidad": 4
}
```

### `PATCH /apiManGen/Ventas/{id}/productos/{productoId}/cantidad`

Cambia solo la cantidad de la relacion entre venta y producto.

Body:

```json
{
  "productoId": 1,
  "cantidad": 7
}
```

### `PATCH /apiManGen/Ventas/{id}/productos/{productoId}/cantidad/con-stock`

Cambia la cantidad de la relacion entre venta y producto ajustando stock:

- Si la nueva cantidad es mayor, descuenta stock adicional.
- Si la nueva cantidad es menor, repone stock del producto y de sus materias primas.

Body:

```json
{
  "productoId": 1,
  "cantidad": 7
}
```

### `DELETE /apiManGen/Ventas/{id}/productos/{productoId}`

Elimina la relacion entre venta y producto sin tocar stock.

### `DELETE /apiManGen/Ventas/{id}/productos/{productoId}/con-stock`

Elimina la relacion entre venta y producto y repone stock del producto y de sus materias primas segun la cantidad vendida.

## Codigos de estado mas comunes

- `200 OK`: consulta o actualizacion correcta
- `201 Created`: recurso creado correctamente
- `204 No Content`: recurso eliminado correctamente
- `400 Bad Request`: datos invalidos o validaciones de negocio
- `404 Not Found`: recurso no encontrado
- `500 Internal Server Error`: error interno del servidor

## Ejemplos con curl

Crear producto:

```bash
curl -X POST http://localhost:8080/apiManGen/Producto \
  -H "Content-Type: application/json" \
  -d "{\"asset\":\"pan.png\",\"nombre\":\"Pan\",\"precio\":800.0,\"cantidad\":12.0,\"unidad\":\"unidad\"}"
```

Obtener materias primas de un producto:

```bash
curl http://localhost:8080/apiManGen/Producto/1/materias-primas
```

Crear venta desde pedido:

```bash
curl -X POST http://localhost:8080/apiManGen/Ventas/desde-pedido/1
```

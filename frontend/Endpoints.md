# Documentación técnica: Mantenedor RAG - BCN

Esta documentación describe los endpoints tal como están implementados en el backend Java/Cacatua.

## Convenciones

- Base URL:
  - Desarrollo: `http://localhost:8080/MantenedorRAG/servicio`
  - Institucional: `http://laloma-desarrollo.bcn.cl:8080/MantenedorRAG/servicio`
- Las rutas se exponen con el patrón `/servicio/{NombreComando}`.
- Los comandos marcados `HTTPGetService` reciben parámetros por query string o formulario.
- Los comandos marcados `HTTPPostService` reciben formularios `application/x-www-form-urlencoded`, salvo `AgregarArchivo`, que usa `multipart/form-data`.
- `EvaluarJuez` recibe el cuerpo como JSON porque lee directamente `request.getReader()`.
- La mayoría de las respuestas son `JSONObject` o `JSONArray`; no existe un único formato HTTP de errores.
- Los nombres de parámetros del backend usan `snake_case`. Los objetos Angular están definidos con nombres camelCase, pero los servicios los transforman antes de enviar la petición.

## Esquemas comunes producidos por `toJSON()`

### Api

```json
{
  "id": 1,
  "nombreProveedor": "Ollama Local BCN",
  "baseUrl": "http://localhost:11434",
  "organizacionId": null,
  "tipoAutenticacion": "NONE",
  "apiKeyEncriptada": "valor",
  "crdate": "2026-03-01 10:00:00"
}
```

`ObtenerApis` actualmente construye el `JSONArray` con los objetos directamente. También coincide con los campos expuestos por `Api.toJSON()`.

### Conexion

```json
{
  "id": 1,
  "nombre": "PostgreSQL Leyes",
  "puerto": 5432,
  "password": "contraseña",
  "usuario": "usr_rag",
  "host": "10.0.0.15",
  "motor": "POSTGRESQL",
  "crdate": "2026-03-01 10:00:00"
}
```

Aunque es un campo del modelo, el API no debería exponer `password` fuera de una interfaz administrativa controlada.

### ModeloLlm

```json
{
  "id": 1,
  "nombre": "Gemma 3",
  "nombreTecnico": "gemma3:4b",
  "limiteTokens": 8192,
  "endpoint": "/generate",
  "plantilla_payload": "{\"model\":\"gemma3:4b\",\"prompt\":\"{prompt}\"}",
  "ruta_respuesta": "response",
  "idApi": 1,
  "nombreProveedor": "Ollama Local",
  "crdate": "2026-03-01 10:00:00"
}
```

### ModeloEmbedding

```json
{
  "id": 1,
  "nombreTecnico": "qwen3-embedding:8b",
  "dimensiones": 4096,
  "proveedor": "Ollama Local",
  "idApi": 1,
  "maxTokensInput": 8191,
  "crdate": "2026-03-01 09:00:00"
}
```

### Dataset

```json
{
  "id": 1,
  "nombre": "Base Normativa",
  "crdate": "2026-03-01 10:00:00",
  "tamanoChunk": 200,
  "overlap": 30,
  "collectionName": "dataset_bcn_1",
  "topK": 4,
  "systemPrompt": "Eres un asistente...",
  "idApi": 1,
  "idModeloLlm": 1,
  "nombreModeloLlm": "Gemma 3",
  "idModeloEmbedding": 1,
  "idRetrievalMethod": 1,
  "idIndexMethod": 1
}
```

### Fuente

```json
{
  "id": 10,
  "nombre": "Fuente normativa",
  "tipo": "ARCHIVO",
  "estadoIndexacion": "PENDIENTE",
  "logError": null,
  "idExternoVectorial": null,
  "idIndexacion": null,
  "idDataset": 1,
  "nombreDataset": "Base Normativa",
  "crdate": "2026-03-01 10:00:00"
}
```

Estados válidos: `PENDIENTE`, `INDEXANDO`, `INDEXADA`, `ERROR`.

### BaseDeDatos

Incluye todos los campos de `Fuente.toJSON()` y agrega:

```json
{
  "tablaObjetivo": "normas",
  "querySql": "SELECT * FROM normas",
  "modoExtraccion": "QUERY",
  "columnaContenido": "texto",
  "idConexion": 1,
  "nombreConexion": "PostgreSQL Leyes"
}
```

### ServicioWeb

Incluye todos los campos de `Fuente.toJSON()` y agrega:

```json
{
  "id": 10,
  "urlEndpoint": "https://ejemplo.cl/api",
  "metodoHttp": "GET",
  "headersJson": "valor encriptado",
  "bodyPayload": null,
  "rutaTexto": "contenido.texto",
  "rutasMetadatos": "titulo,uri"
}
```

### IndexMethod

```json
{
  "id": 1,
  "nombre": "Fragmentación Adaptativa",
  "descripcion": "Descripción",
  "crdate": "2026-03-01"
}
```

### RetrievalMethod

```json
{
  "id": 1,
  "nombre": "Híbrida",
  "descripcion": "Descripción",
  "crdate": "2026-03-01"
}
```

## 1. Configuración global

### AgregarApi

- Método backend: `HTTPPostService`
- URL: `POST /AgregarApi`
- Parámetros reales:

| Campo | Tipo | Obligatorio |
|---|---|---|
| `nombre_proveedor` | String | Sí |
| `base_url` | String | Sí |
| `organizacion_id` | String | Sí por validación actual |
| `tipo_autenticacion` | String | Sí |
| `api_key_encriptada` | String | Sí |

Respuesta:

```json
{
  "success": true,
  "message": "API agregada exitosamente"
}
```

### ActualizarApi

- URL: `POST /ActualizarApi`
- Parámetros: `id`, `nombre_proveedor`, `base_url`, `organizacion_id`, `tipo_autenticacion`, `api_key_encriptada`.
- Respuesta:

```json
{"status": "success"}
```

### EliminarApi

- URL: `POST /EliminarApi`
- Parámetro: `id`.
- Respuesta exitosa:

```json
{
  "status": "success",
  "message": "Proveedor de API eliminado correctamente.",
  "filasAfectadas": 1
}
```

- Si existen datasets, modelos LLM o modelos Embedding asociados, devuelve:

```json
{"error": "mensaje de integridad referencial"}
```

### ObtenerApis

- Método backend: `HTTPGetService`
- URL: `GET /ObtenerApis`
- Respuesta: `JSONArray<Api>`.

### ObtenerApisPaginadas

- URL: `GET /ObtenerApisPaginadas?limit={limit}&offset={offset}`
- Respuesta:

```json
{
  "data": [],
  "total": 0
}
```

Nota: `total` se calcula actualmente con `conexiones.size()`, por lo que representa el número de elementos retornados en la página, no el total global.

### AgregarConexion

- Método backend: `HTTPGetService`
- URL frontend actual: `POST /AgregarConexion`
- Parámetros reales:

| Campo | Tipo | Obligatorio |
|---|---|---|
| `nombre` | String | Sí |
| `puerto` | Integer | Sí |
| `password` | String | Sí |
| `usuario` | String | Sí |
| `host` | String | Sí |
| `motor` | String | Sí |

La implementación JDBC disponible es PostgreSQL. El motor debe enviarse como `POSTGRESQL`.

Respuesta:

```json
{
  "success": true,
  "id_conexion": 1
}
```

### ActualizarConexion

- URL: `POST /ActualizarConexion`
- Parámetros: `id`, `nombre`, `puerto`, `password`, `usuario`, `host`, `motor`.
- Respuesta:

```json
{"status": "success"}
```

### EliminarConexion

- URL: `POST /EliminarConexion`
- Parámetro: `id`.
- Bloqueada si la conexión está asociada a fuentes PostgreSQL.
- Respuesta exitosa:

```json
{
  "status": "success",
  "message": "Conexión eliminada correctamente.",
  "filasAfectadas": 1
}
```

### ObtenerConexiones

- URL: `GET /ObtenerConexiones`
- Respuesta: `JSONArray<Conexion>`, basada en `Conexion.toJSON()`.

### ObtenerConexionesPaginadas

- URL: `GET /ObtenerConexionesPaginadas?limit={limit}&offset={offset}`
- Respuesta:

```json
{
  "data": [],
  "total": 0
}
```

Nota: `total` representa el número de elementos de la página actual porque se calcula con `conexiones.size()`.

### AgregarModeloLlm

- Método backend: `HTTPGetService`
- URL frontend actual: `POST /AgregarModeloLlm`
- Parámetros reales:

| Campo | Tipo | Obligatorio |
|---|---|---|
| `nombre` | String | Sí |
| `nombre_tecnico` | String | Sí |
| `limite_tokens` | Integer | Sí |
| `id_api` | Integer | Sí |
| `endpoint` | String | Sí funcionalmente |
| `plantilla_payload` | String | Sí funcionalmente |
| `ruta_respuesta` | String | Sí funcionalmente |

Respuesta:

```json
{
  "success": true,
  "message": "Modelo LLM agregado exitosamente"
}
```

### ActualizarModeloLlm

- URL: `POST /ActualizarModeloLlm`
- Parámetros: `id`, `nombre`, `nombre_tecnico`, `limite_tokens`, `endpoint`, `plantilla_payload`, `ruta_respuesta`, `id_api`.
- Respuesta:

```json
{"status": "success"}
```

### EliminarModeloLlm

- URL: `POST /EliminarModeloLlm`
- Parámetro: `id`.
- Bloqueado si está asociado a datasets.
- Respuesta:

```json
{
  "status": "success",
  "message": "Modelo LLM eliminado correctamente.",
  "filasAfectadas": 1
}
```

### ObtenerModelosLlm

- URL: `GET /ObtenerModelosLlm`
- Respuesta: `JSONArray<ModeloLlm>`, basada en `ModeloLlm.toJSON()`.

### ObtenerModelosLlmPaginados

- URL: `GET /ObtenerModelosLlmPaginados?limit={limit}&offset={offset}`
- Respuesta:

```json
{
  "data": [],
  "total": 0
}
```

Nota: `total` representa el número de elementos de la página actual porque se calcula con `conexiones.size()`.

### ObtenerModelosLlmPorApi

- URL: `GET /ObtenerModelosLlmPorApi?id={id}`
- Parámetro real: `id`, correspondiente al `id_api`.
- Respuesta: `JSONArray<ModeloLlm>`.

### ObtenerModeloLlmPorId

- URL: `GET /ObtenerModeloLlmPorId?id={id}`
- Respuesta: `ModeloLlm.toJSON()` o `{"error":"Modelo LLM no encontrado"}`.

### AgregarModeloEmbedding

- Método backend: `HTTPPostService`
- URL: `POST /AgregarModeloEmbedding`
- Parámetros: `nombre_tecnico`, `dimensiones`, `max_tokens_input`, `id_api`.
- Respuesta:

```json
{
  "status": "success",
  "id": 1
}
```

Este CRUD existe en backend, aunque no se muestra actualmente en ajustes globales del frontend.

### ActualizarModeloEmbedding

- URL: `POST /ActualizarModeloEmbedding`
- Parámetros: `id`, `nombre_tecnico`, `dimensiones`, `max_tokens_input`, `id_api`.
- Respuesta:

```json
{
  "status": "success",
  "filasAfectadas": 1
}
```

### EliminarModeloEmbedding

- URL: `POST /EliminarModeloEmbedding`
- Parámetro: `id`.
- Respuesta exitosa:

```json
{
  "status": "success",
  "message": "Modelo de embedding eliminado correctamente.",
  "filasAfectadas": 1
}
```

### ObtenerModelosEmbedding

- URL: `GET /ObtenerModelosEmbedding`
- Respuesta: `JSONArray<ModeloEmbedding>`, basada en `ModeloEmbedding.toJSON()`.

### ObtenerModeloEmbeddingPorId

- URL: `GET /ObtenerModeloEmbeddingPorId?id={id}`
- Respuesta: `ModeloEmbedding.toJSON()`.
- El comando llama directamente a `modelo_embedding.toJSON()` y no valida `null`; consultar un ID inexistente puede producir una excepción.

## 2. Catálogos y datasets

### ObtenerIndexMethods

- URL: `GET /ObtenerIndexMethods`
- Respuesta: `JSONArray<IndexMethod>`, basada en `IndexMethod.toJSON()`.

### ObtenerIndexMethodPorId

- URL: `GET /ObtenerIndexMethodPorId?id={id}`
- Respuesta: `IndexMethod.toJSON()` o `{"error":"Index Method no encontrado"}`.

### ObtenerRetrievalMethods

- URL: `GET /ObtenerRetrievalMethods`
- Respuesta: `JSONArray<RetrievalMethod>`, basada en `RetrievalMethod.toJSON()`.

### AgregarDataset

- Método backend: `HTTPGetService`
- URL frontend actual: `POST /AgregarDataset`
- Parámetros reales:

| Campo | Tipo | Obligatorio |
|---|---|---|
| `nombre` | String | Sí |
| `tamano_chunk` | Integer | Sí |
| `overlap` | Integer | Sí |
| `top_k` | Integer | Sí |
| `system_prompt` | String | Sí |
| `id_api` | Integer | Sí |
| `id_modelo_llm` | Integer | Sí |
| `id_modelo_embedding` | Integer | Sí |
| `id_retrieval_method` | Integer | Sí |
| `id_index_method` | Integer | Sí |

El backend crea primero el dataset y luego genera `collection_name` mediante `QdrantNameGenerator`.

Respuesta real:

```json
{
  "success": true,
  "id_dataset": 1
}
```

### ActualizarDataset

- URL: `POST /ActualizarDataset`
- Parámetros: `id`, `nombre`, `tamano_chunk`, `overlap`, `top_k`, `system_prompt`, `id_api`, `id_modelo_llm`, `id_modelo_embedding`, `id_retrieval_method`, `id_index_method`.
- Respuesta:

```json
{"status": "success"}
```

### EliminarDataset

- URL: `POST /EliminarDataset`
- Parámetro: `id`.
- Elimina primero la colección Qdrant y luego fuentes/documentos y el dataset PostgreSQL.
- Respuesta:

```json
{
  "status": "success",
  "datasetEliminado": 1,
  "filasAfectadas": 1
}
```

### ObtenerDatasets

- URL: `GET /ObtenerDatasets`
- Respuesta: `JSONArray<Dataset>`, basada en `Dataset.toJSON()`.

### ObtenerDatasetPorId

- URL: `GET /ObtenerDatasetPorId?id={id}`
- Respuesta: `Dataset.toJSON()` o `{"error":"No se encontró el Dataset con el ID especificado."}`.

## 3. Fuentes

### AgregarArchivo

- URL: `POST /AgregarArchivo`
- Content-Type: `multipart/form-data`.
- Archivo: campo `file`.
- Campos de query o multipart:

| Campo | Tipo | Obligatorio |
|---|---|---|
| `id_dataset` | Integer | Sí |
| `nombre` | String | Sí |
| `extension` | String | Sí |

Actualmente el backend acepta únicamente archivos con extensión `.txt`.

Respuesta:

```json
{
  "status": "success",
  "id_fuente": 10,
  "mensaje": "Archivo subido y guardado en /ruta/final"
}
```

### EliminarArchivo

- URL: `POST /EliminarArchivo`
- Parámetro: `id`.
- Elimina vectores Qdrant, documentos PostgreSQL, el registro de archivo, la fuente y el archivo físico cuando existe.
- Respuesta:

```json
{
  "status": "success",
  "message": "Archivo eliminado correctamente de la BD, Qdrant y del disco."
}
```

### ObtenerArchivosPorDataset

- URL: `GET /ObtenerArchivosPorDataset?id={id_dataset}`
- Respuesta: `JSONArray<Archivo>`.
- `Archivo` no sobrescribe `toJSON()` y hereda `Fuente.toJSON()`; este endpoint usa `new JSONArray(archivos)`, por lo que la serialización puede exponerse mediante los getters Java en lugar de los campos exactos de `Fuente.toJSON()`.

### ObtenerArchivosPorDatasetPaginados

- URL: `GET /ObtenerArchivosPorDatasetPaginados?id={id_dataset}&limit={limit}&offset={offset}`
- Respuesta:

```json
{
  "data": [],
  "total": 0
}
```

### AgregarBaseDeDatos

- Método backend: `HTTPGetService`
- URL frontend actual: `POST /AgregarBaseDeDatos`
- Parámetros reales:

| Campo | Tipo | Obligatorio |
|---|---|---|
| `nombre` | String | Sí |
| `id_dataset` | Integer | Sí |
| `id_conexion` | Integer | Sí, `0` permite crear una conexión nueva |
| `tabla_objetivo` | String | Dependiendo del modo |
| `query_sql` | String | Dependiendo del modo |
| `modo_extraccion` | String | Sí |
| `columna_contenido` | String | Sí |
| `id_indexacion` | String | Opcional |
| `id_externo_vectorial` | String | Opcional |
| `nombre_conexion`, `host`, `puerto`, `usuario`, `password`, `motor` | String/Integer | Solo para conexión nueva |

JDBC admite PostgreSQL. La validación actual de `AgregarBaseDeDatos` contiene varios campos vacíos como obligatorios y puede rechazar configuraciones donde solo haya tabla o solo query; el cliente debe enviar los campos esperados.

Respuesta:

```json
{
  "success": true,
  "id_fuente": 11,
  "id_conexion_usada": 1
}
```

### ObtenerBasesDeDatos

- URL: `GET /ObtenerBasesDeDatos`
- Este endpoint no recibe `id_dataset`; su ejecución llama a `findAll()` en `BaseDeDatosDAO`.
- La implementación de `BaseDeDatosDAO.findAll()` no está definida y puede quedar sin datos.
- Si opera, la respuesta sería `JSONArray<BaseDeDatos>`, basada en `BaseDeDatos.toJSON()`.

### ObtenerBaseDeDatosPorId

- URL: `GET /ObtenerBaseDeDatosPorId?id={id}`
- Respuesta: `BaseDeDatos.toJSON()` o `{"error":"No se encontró la base de datos con el ID proporcionado."}`.

### ObtenerBDPorDatasetPaginados

- URL: `GET /ObtenerBDPorDatasetPaginados?id={id_dataset}&limit={limit}&offset={offset}`
- Respuesta:

```json
{
  "data": [],
  "total": 0
}
```

### EliminarBaseDeDatos

- URL: `POST /EliminarBaseDeDatos`
- Parámetro: `id`.
- Elimina primero vectores y documentos asociados y luego la fuente PostgreSQL.
- Respuesta:

```json
{
  "status": "success",
  "message": "Base de datos y fuente eliminados correctamente."
}
```

### AgregarServicioWeb

- URL: `POST /AgregarServicioWeb`
- Content-Type esperado por el frontend: `application/x-www-form-urlencoded`.
- Parámetros reales:

| Campo | Tipo | Obligatorio |
|---|---|---|
| `nombre` | String | Sí |
| `url_endpoint` | String | Sí |
| `metodo_http` | String | Sí |
| `headers` | String | No |
| `body` | String | No |
| `ruta_texto` | String | Sí funcionalmente |
| `rutas_metadatos` | String | No |
| `id_dataset` | Integer | Sí |
| `log_error` | String | No |

Respuesta:

```json
{
  "success": true,
  "message": "Servicio Web agregado exitosamente con id: 12"
}
```

### ObtenerServiciosWebPorDatasetPaginados

- URL: `GET /ObtenerServiciosWebPorDatasetPaginados?id={id_dataset}&limit={limit}&offset={offset}`
- Respuesta:

```json
{
  "data": [],
  "total": 0
}
```

Cada elemento usa `ServicioWeb.toJSON()`.

### EliminarServicioWeb

- URL: `POST /EliminarServicioWeb`
- Parámetro: `id`.
- Elimina vectores, documentos, registro de servicio y fuente.
- Respuesta:

```json
{
  "status": "success",
  "message": "Servicio Web y fuente eliminados correctamente."
}
```

### ObtenerFuentePorId

- URL: `GET /ObtenerFuentePorId?id={id}`
- Respuesta: `Fuente.toJSON()` o `{"error":"Fuente no encontrada"}`.

### ObtenerFuentesPorDataset

- URL: `GET /ObtenerFuentesPorDataset?id={id_dataset}`
- Respuesta: `JSONArray<Fuente>`, basada en `Fuente.toJSON()`.

### EliminarFuente

- URL: `POST /EliminarFuente`
- Parámetro: `id`.
- Elimina vectores, documentos y fuente.
- Respuesta:

```json
{
  "status": "success",
  "filasAfectadas": 1
}
```

### EliminarDocumento

- URL: `POST /EliminarDocumento`
- Parámetro: `id`.
- Elimina primero los puntos Qdrant asociados al `documento_id`, luego el documento PostgreSQL.
- Respuesta:

```json
{
  "status": "success",
  "filasAfectadas": 1
}
```

## 4. Indexación

### IndexarFuente

- URL: `POST /IndexarFuente`
- Parámetro: `id`.
- Reutiliza el dataset configurado, limpia documentos/vectores previos de la fuente, fragmenta, genera embeddings y upserta en Qdrant.

Si ya está `INDEXADA`:

```json
{"status":"la fuente ya está indexada"}
```

Si finaliza:

```json
{"status":"indexación completa"}
```

Ante una excepción, la fuente queda `ERROR` y se registra `log_error`.

### IndexarFuentes

- URL: `POST /IndexarFuentes`
- Parámetro: `id` correspondiente al dataset.
- Respuesta exitosa:

```json
{"status":"indexación masiva completa"}
```

Si una o más fuentes fallan:

```json
{
  "status": "partial",
  "mensaje": "Indexación masiva completada con errores. Revise el estado y log_error de las fuentes."
}
```

El overlap se lee desde el dataset. Solo si es `null` o negativo se usa el 10% de `tamano_chunk` como fallback.

### ReIndexarFuente

- Método backend: `HTTPGetService`
- URL frontend actual: `POST /ReIndexarFuente`
- Parámetro: `id`.
- Elimina por filtro los puntos asociados a los documentos existentes, elimina los documentos PostgreSQL y reconstruye el contenido.
- Respuesta:

```json
{
  "status": "success",
  "mensaje": "Re-Indexación completada."
}
```

Para archivos puede devolver:

```json
{
  "status": "success",
  "mensaje": "Archivo releído desde disco y re-indexado."
}
```

### ReIndexarDataset

- URL: `POST /ReIndexarDataset`
- Parámetro real: `id`.
- Elimina la colección existente, la recrea y procesa todas las fuentes.
- Respuesta normal:

```json
{"status":"re-indexación completa"}
```

Respuesta parcial:

```json
{
  "status": "partial",
  "mensaje": "Re-indexación completada con errores. Revise el estado y log_error de las fuentes."
}
```

### IndexarCatalogoLeyFacil

- URL: `POST /IndexarCatalogoLeyFacil`
- Parámetros reales:

| Campo | Tipo | Obligatorio |
|---|---|---|
| `id_dataset` | Integer | Sí |
| `url_indice` | String | Sí |

La fuente se crea automáticamente con el tipo `SERVICIO_WEB`.

Respuesta normal:

```json
{
  "status": "success",
  "mensaje": "Catálogo procesado. Fuente ID 10 creada automáticamente. Total de guías inyectadas a Qdrant: 42"
}
```

## 5. Consultas RAG

### Consultar

- URL: `POST /Consultar`
- Parámetros reales:

| Campo | Tipo | Obligatorio |
|---|---|---|
| `id` | Integer | Sí |
| `consulta` | String | Sí |

Ejemplo de formulario:

```text
id=1&consulta=¿Qué requisitos existen?
```

Respuesta normal:

```json
{
  "contexto": "Chunks recuperados como texto",
  "respuesta_generada": "Respuesta del LLM"
}
```

Cuando no hay resultados:

```json
{"respuesta":"Lo siento, no se encontró información relevante para tu consulta."}
```

Cuando la consulta está vacía:

```json
{"Error":"Lo siento, la consulta no puede estar vacía."}
```

La recuperación híbrida usa vector denso y vector disperso; la fusión RRF actual se delega a Qdrant. El orquestador aún no implementa un RRF explícito con una constante `k` propia.

## 6. Utilitarios y evaluación

### ObtenerFilas

- URL: `POST /ObtenerFilas`
- Parámetros reales: `nombre`, `query`, `columna_contenido`, `id_conexion`, `tabla_objetivo`, `tipo`, `modo_extraccion`.
- Respuesta:

```json
{
  "status": "success",
  "data": ["fila 1", "fila 2"]
}
```

### TestConnection

- URL: `POST /TestConnection`
- Parámetros: `host`, `puerto`, `usuario`, `password`, `motor`, `nombre`.
- Motor soportado: `POSTGRESQL`.
- Respuesta exitosa:

```json
{
  "tablas": ["tabla1", "tabla2"]
}
```

Ante error, el comando lanza una excepción con el detalle de conexión.

### TestConsumirServicio

- URL: `POST /TestConsumirServicio`
- Actualmente no consume parámetros dinámicos recibidos. Ejecuta una llamada fija al endpoint de búsqueda de Ley Fácil de BCN y devuelve el `JSONObject` recibido.

### EvaluarJuez

- URL: `POST /EvaluarJuez`
- Content-Type: `application/json`.
- Cuerpo real:

```json
{
  "pregunta": "Texto de la pregunta",
  "contextoRecuperado": "Contexto usado",
  "respuestaGemma": "Respuesta generada"
}
```

Respuesta:

```json
{
  "Fidelidad": 4,
  "Relevancia": 4
}
```

### Version

- URL: `GET /Version`
- Parámetro opcional: `test`.
- El comando comienza con el resultado estándar `getOkDefaultResult()` y agrega los datos siguientes:

```json
{
  "ver": "versión",
  "servicio": "ok",
  "in": 1,
  "configuración": "object",
  "test": "valor"
}
```
- `config` solo se incluye cuando `entorno` tiene el valor `Desarrollo`.

### Test

- URL: `POST /Test`
- Parámetros: `test`, `number`.
- El comando valida `test` con longitud mínima 4 y `number` como entero.
- También agrega los campos al resultado estándar `getOkDefaultResult()`:

```json
{
  "test": "valor",
  "number": 1
}
```

### TestPersistencia

- URL: `GET /TestPersistencia`
- Parámetro real: `id`.
- Busca un registro `Partido` y convierte el objeto Java a JSON.
- Validación de parámetros pendiente: el comando no valida `id`.

### ObtenerServidoresVectoriales

- URL: `GET /ObtenerServidoresVectoriales`
- Respuesta: `JSONArray<ServidorVectorial>`.
- El comando usa `new JSONArray(servidores)` en lugar de llamar explícitamente a `ServidorVectorial.toJSON()`.

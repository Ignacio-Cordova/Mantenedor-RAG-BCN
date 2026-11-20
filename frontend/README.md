# Mantenedor RAG BCN

## 1. Título y Descripción

### ¿Qué es el Mantenedor RAG BCN?
El **Mantenedor RAG BCN** es una plataforma modular de administración, configuración y orquestación de flujos de Inteligencia Artificial basada en el paradigma **RAG** (*Retrieval-Augmented Generation* o Generación Aumentada por Recuperación), desarrollada para la **Biblioteca del Congreso Nacional de Chile (BCN)**.

El sistema permite crear, gestionar y consultar bases de conocimiento especializadas (**Datasets**) de forma local, segura y parametrizable. Su objetivo principal es conectar Modelos de Lenguaje Grande (LLMs) con las fuentes institucionales de la BCN, mitigando las alucinaciones mediante inyección precisa de contexto, preservación estructural de párrafos y búsqueda híbrida.

**Características Principales:**
* **Arquitectura Modular y Escalable:** Frontend estructurado bajo *Package by Feature* (patrón Contenedor/Presentación) y backend estandarizado mediante el patrón Comando (RPC sobre HTTP `POST`), permitiendo incorporar nuevas funcionalidades sin refactorizar el núcleo del sistema.
* **Módulo de Configuración Global:** Administración centralizada (CRUD) de proveedores de API, conexiones a bases de datos y modelos LLM directamente desde la interfaz gráfica, con validación de integridad referencial.
* **Ingesta Omnicanal y Mapeo Dinámico:** Extracción de texto desde archivos locales (`.txt`), bases de datos relacionales PostgreSQL (vía JDBC) y servicios web REST (JSON) con extracción parametrizable de texto principal (`ruta_texto`), limpieza HTML y captura dinámica de metadatos (`rutas_metadatos`).
* **Fragmentación Híbrida Adaptativa:** Enrutamiento dinámico de estrategias de *chunking* (por palabras, por párrafos o fragmentación híbrida adaptativa que respeta fronteras de párrafo con ventana deslizante y *overlap* de contingencia).
* **Motor de Búsqueda Híbrida con RRF:** Combinación de búsqueda semántica (vectores densos) y búsqueda léxica (vectores dispersos SPLADE) mediante subconsultas paralelas (*prefetch*) y fusión nativa **Reciprocal Rank Fusion (RRF)** en Qdrant ($k = 2$).
* **Control de Generación por Dataset:** Parametrización independiente de tamaño de *chunk*, *overlap*, `top-k`, método de indexación/recuperación e instrucción de sistema (`system_prompt`) con reglas de abstención segura.

---

## 2. Arquitectura y Stack Tecnológico

La arquitectura del sistema opera bajo un modelo cliente-servidor respaldado por contenedores Docker y servicios locales de inferencia para resguardar la confidencialidad institucional:

### Frontend (Interfaz de Usuario)
* **Framework:** Angular (v18.2+) organizado por funcionalidades (*Package by Feature*).
* **Lenguajes:** TypeScript, HTML5, CSS3.
* **Diseño UI:** Bootstrap 5 (*Responsive Design*).

### Backend (Orquestador y Capa de Servicios)
* **Lenguaje:** Java.
* **Framework:** Cacatúa (Framework MVC Institucional) con endpoints estructurados bajo el patrón Comando.
* **Servidor de Aplicaciones:** Apache Tomcat 8.
* **Comunicación:** Implementaciones HTTP nativas (sin SDKs externos pesados), REST y JDBC.

### Bases de Datos (Almacenamiento Dual)
* **PostgreSQL:** Actúa como Fuente de Verdad Única para configuraciones, fuentes, datasets, documentos y metadatos estructurados (`JSONB`).
* **Qdrant (v1.19.0):** Base de datos vectorial con soporte para vectores nombrados (densos y dispersos), índices de *payload*, identificadores deterministas **UUID v3** (`documento_id` + `chunk_index`) y fusión híbrida RRF nativa.

### Servicios de Embeddings e Inferencia IA
* **Vectores Densos (Local):** Modelo `qwen3-embedding:8b` (4096 dimensiones, ventana de 8191 tokens) ejecutado localmente mediante **Ollama**.
* **Vectores Dispersos (Microservicio Docker):** Servicio en Python (**FastAPI** + **FastEmbed** sobre ONNX Runtime) ejecutando el modelo `prithivida/Splade_PP_en_v1` en CPU.
* **LLM Generativo (Local):** Modelo `Gemma3` (4B parámetros) ejecutado localmente vía **Ollama**.
* **LLM Evaluador (Opcional / Validación):** Integración con la API REST de **Groq** (`gpt-oss-120b`) utilizada exclusivamente para evaluación automatizada (*LLM-as-a-Judge*).

---

## 3. Instalación y Despliegue

El entorno del Mantenedor RAG combina infraestructura contenerizada (PostgreSQL, Qdrant y microservicio SPLADE), inferencia local en Ollama, el backend Java empaquetado como `.war` en Apache Tomcat y el cliente web en Angular.

### Prerrequisitos
Para compilar y desplegar el sistema en un entorno local o de servidor, se requiere:
* **Docker** y **Docker Compose** (para servicios de datos y vectorización dispersa).
* **Ollama** instalado y en ejecución con los modelos descargados:
  ```bash
  ollama pull qwen3-embedding:8b
  ollama pull gemma3:4b
  ```
* **Java Development Kit (JDK)** y **Apache Tomcat 8.5** (para compilación/despliegue del backend `.war`).
* **Node.js** (v16 o superior) y **Angular CLI** (v18.2+) para compilar o ejecutar el frontend.

### Puesta en Marcha Rápida
1. **Levantar servicios base en Docker:** Iniciar los contenedores de **PostgreSQL**, **Qdrant** y el microservicio **SPLADE** mediante Docker Compose (`docker compose up -d`).
2. **Desplegar Backend:** Configurar las credenciales de conexión a PostgreSQL, Qdrant, Ollama y SPLADE según el entorno (local o Docker) y desplegar el archivo `.war` en **Apache Tomcat 9**.
3. **Ejecutar Frontend:** Instalar dependencias (`npm install`) y levantar el servidor de desarrollo (`ng serve`) o compilar la distribución estática (`ng build`).

---

## 4. Manual de Usuario

La interfaz gráfica del Mantenedor RAG permite administrar el ciclo de vida completo de cada Dataset sin necesidad de interactuar directamente con el código fuente o la base de datos.

### 4.1. Panel Principal (Dashboard)
Al ingresar al sistema, el usuario visualizará el catálogo completo de Datasets existentes (por ejemplo, las configuraciones de prueba sobre *"Ley Fácil"*).
* **Configuraciones Globales:** Acceso directo al módulo centralizado de administración de APIs, Conexiones y Modelos LLM.
* **Nueva Instancia:** Botón principal para iniciar la creación de un nuevo Dataset.
* **Acciones por Dataset:** Acceso a edición/ajustes (ícono de lápiz), consulta RAG y eliminación (ícono de papelera).

### 4.2. Módulo de Configuración Global
Centraliza la parametrización transversal del sistema mediante operaciones CRUD:
* **APIs:** Registro, edición y eliminación de proveedores de IA (ej. Ollama Local, Groq, OpenAI) junto con sus URLs y credenciales.
* **Conexiones:** Administración de cadenas de conexión JDBC para bases de datos PostgreSQL institucionales.
* **Modelos LLM:** Gestión de modelos generativos disponibles vinculados a una API registrada, indicando nombre técnico, nombre visual y límite de tokens.

> **Nota técnica:** El sistema valida la integridad referencial antes de eliminar cualquier recurso, impidiendo borrar APIs, conexiones o modelos que estén siendo utilizados por un Dataset activo. Los modelos de *Embedding* se mantienen controlados a nivel de base de datos para garantizar compatibilidad de dimensionalidad con las colecciones existentes en Qdrant.

### 4.3. Creación de un Nuevo Dataset
Al hacer clic en **"Nueva Instancia"**, se abrirá un asistente (*Wizard*) de configuración de dos pasos:
1. **Paso 1 (General):** Definir el **Nombre** del dataset, seleccionar la **API** proveedora, elegir el **Modelo LLM** y definir el **`system_prompt`** operativo (instrucciones y reglas de abstención para el modelo).
2. **Paso 2 (Configuración de Indexación y Recuperación):** Parametrizar el comportamiento del pipeline RAG:
   * **Tamaño del chunk y Overlap:** Límites de palabras y solapamiento para la segmentación.
   * **Top-K:** Cantidad máxima de fragmentos a inyectar como contexto en cada consulta.
   * **Modelo de Embedding:** Modelo vectorial denso configurado en el sistema.
   * **Método de Indexación (*Chunking*):** División por palabras, división por párrafos o **Fragmentación Híbrida Adaptativa**.
   * **Método de Recuperación:** Búsqueda Vectorial Densa o **Búsqueda Híbrida (Densa + Dispersa con RRF)**.

### 4.4. Ajustes y Edición de Datasets
Al presionar el ícono de edición (lápiz) sobre un dataset existente, se desplegará el panel de Ajustes con tres pestañas principales:
* **Generales:** Modificación del nombre, API, Modelo LLM y `system_prompt`.
* **Indexación:** Ajuste de tamaño de *chunk*, *overlap*, `top-k` y métodos de fragmentación o recuperación. Si se modifican parámetros estructurales de indexación, el sistema requerirá re-indexar las fuentes para mantener consistencia en Qdrant.
* **Fuentes:** Centro de control para agregar, inspeccionar, sincronizar o eliminar los orígenes de datos del dataset.

### 4.5. Gestión y Configuración de Fuentes
Dentro de la pestaña **"Fuentes"**, los orígenes se organizan en tres categorías con una tabla de estado (`INDEXADA` / `PENDIENTE`) y acciones individuales de re-sincronización o borrado en cascada (PostgreSQL + Qdrant):

* **a) Archivos Locales:** Zona de carga interactiva (*Drag and Drop*) para incorporar documentos de texto plano (`.txt`).
* **b) Base de Datos PostgreSQL (Extracción Estructurada vía JDBC):** Ofrece dos modalidades con botón **"Probar Conexión"** previo al guardado:
  * **Modo Tabla:** Selección directa de tabla y columna objetivo a vectorizar.
  * **Modo Query:** Consulta SQL personalizada (con `JOINs` o filtros) para extraer el texto exacto.
* **c) Servicio Web (APIs REST JSON con Mapeo Dinámico):** Permite integrar APIs externas o institucionales configurando:
  * Endpoint, método HTTP (`GET`/`POST`), *Headers*, parámetros y *Body*.
  * **Ruta de texto (`ruta_texto`):** Clave JSON donde reside el contenido principal a limpiar y vectorizar.
  * **Rutas de metadatos (`rutas_metadatos`):** Lista separada por comas con las claves secundarias (soporta rutas anidadas como `ruta.metadato`) que se almacenan en formato `JSONB` en PostgreSQL y como *payload* en Qdrant para anteponerse al contexto del prompt.

### 4.6. Indexación y Consulta RAG
Las fuentes pueden procesarse globalmente con **"Indexar Todo"** o individualmente desde su fila de acciones. Durante la indexación:
1. El texto extraído se limpia mediante `LimpiadorTexto` (removiendo etiquetas HTML y preservando saltos de línea estructurales).
2. `TextSplitter` divide el texto según la estrategia configurada (por ejemplo, `splitEstructuralAvanzado`).
3. Cada fragmento se vectoriza (vector denso en Ollama y, en modo híbrido, vector disperso en SPLADE) y se indexa en Qdrant con un **UUID v3** determinista junto a sus metadatos.

Al realizar una consulta, el motor ejecuta la búsqueda configurada (en modo híbrido, `prefetch` de `2 * top-k` candidatos por motor y fusión nativa RRF con $k = 2$ en Qdrant), antepone los metadatos recuperados al contexto y ensambla el prompt junto al `system_prompt` para la inferencia local.

---

## 5. Documentación de la API y Base de Datos

La documentación detallada de los Endpoints REST/RPC (patrón Comando) se encuentra centralizada en un documento técnico dedicado:

🔗 **[Ver Documentación Técnica Completa de Endpoints](Endpoints.md)**

### Modelo Entidad-Relación (PostgreSQL)
El esquema relacional incluye el soporte para el módulo de configuración global, extracción dinámica de metadatos (`servicio_web.rutas_metadatos`, `documento.metadatos` en `JSONB`) y personalización de prompts por dataset (`dataset.system_prompt`):

![Diagrama Entidad-Relación de la Base de Datos](docs/diagrama_er.png)

---

## 6. Validación Experimental (Resumen)
El sistema fue validado cuantitativamente sobre el catálogo **"Ley Fácil"** de la BCN mediante el paradigma **LLM-as-a-Judge** (`gpt-oss-120b` vía Groq):
* **Fase 1 (Calibración sobre 30 consultas):** Concordancia inter-evaluador frente a anotación humana con Kappa ponderado cuadrático $\kappa = 0.9302$ en Fidelidad (*casi perfecto*) y $\kappa = 0.6400$ en Relevancia (*sustancial*).
* **Fase 2 (Ablación sobre 100 consultas y 5 configuraciones):** Reducción del ruido inyectado del **60.7%** (`top-k = 4`) al **37.5%–40.0%** (`top-k = 2`), manteniendo fidelidad promedio entre **3.84 y 3.94 sobre 4.00** y alcanzando hasta un **92% de Recall@2**.
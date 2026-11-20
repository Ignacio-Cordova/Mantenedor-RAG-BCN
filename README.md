# Mantenedor RAG - Biblioteca del Congreso Nacional (BCN)

**Proyecto de Título:** Implementación de Búsqueda Híbrida y Segmentación Estructural para un Mantenedor RAG.  
**Autor:** Ignacio Javier Córdova Moraga (Pontificia Universidad Católica de Valparaíso - Escuela de Ingeniería Informática).

Este repositorio contiene el código fuente completo y los scripts de despliegue para la refactorización arquitectónica y optimización algorítmica del Mantenedor RAG de la BCN. El sistema permite administrar de forma agnóstica múltiples orígenes de datos, aplicar fragmentación adaptativa e inyectar contexto preciso mediante búsqueda híbrida (vectores densos y dispersos con fusión RRF) para reducir alucinaciones en Modelos de Lenguaje (LLMs).

---

## Arquitectura y Stack Tecnológico

El proyecto está diseñado bajo una arquitectura modular contenerizada y orquestada completamente mediante Docker.

* **Frontend:** Angular (Arquitectura orientada a Funcionalidades).
* **Backend Orquestador:** Java (Apache Tomcat) bajo patrón Comando.
* **Motor Disperso (Léxico):** Microservicio Python (FastAPI + SPLADE).
* **Bases de Datos:** PostgreSQL (Relacional) y Qdrant (Vectorial).
* **Inferencia Local:** Ollama (Modelos: `gemma3:4b` para generación y `qwen3-embedding:8b` para embeddings densos).

---

## Requisitos Previos

Para ejecutar este sistema en cualquier equipo desde cero, **solo necesitas tener instalado**:

* [Docker Desktop](https://www.docker.com/products/docker-desktop/) (Asegúrate de que el motor de Docker esté en ejecución).
* Git.

*Nota: No es necesario tener instalados Java, Node.js, Python ni PostgreSQL en tu máquina local. Todo se construye, compila y ejecuta dentro de contenedores aislados.*

---

## Instrucciones de Despliegue

Sigue estos pasos para compilar e inicializar todo el ecosistema (bases de datos, APIs, frontend y descarga de modelos de Inteligencia Artificial):

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/Ignacio-Cordova/Mantenedor-RAG-BCN/
   cd Mantenedor-RAG-BCN
   ```

2. **Levantar el sistema:**
   Abre una consola (CMD, PowerShell o Terminal) en la raíz del proyecto y ejecuta:
   ```bash
   docker-compose up -d --build
   ```
   *Este proceso tomará varios minutos la primera vez, ya que descargará las imágenes base, compilará los paquetes de Angular y Java, e iniciará la descarga de los modelos de IA (aprox. 5.5 GB).*

---

## Resolución de Problemas (Troubleshooting)

### Falla en la descarga de los Modelos de Ollama (Error 503 / Timeout)
Al ejecutar el proyecto por primera vez, el sistema intentará descargar automáticamente `gemma3:4b` y `qwen3-embedding:8b`. Si tu conexión es inestable o los servidores de Ollama se saturan, el contenedor `rag_ollama_init` fallará y detendrá el despliegue del Backend/Frontend.

Si esto ocurre, el problema se soluciona terminando la descarga manualmente:

1. Ejecuta el comando para descargar el modelo de embedding (reanuda desde donde se cortó):
   ```bash
   docker exec -it rag_ollama ollama pull qwen3-embedding:8b
   ```

2. Ejecuta el comando para descargar el modelo generativo:
   ```bash
   docker exec -it rag_ollama ollama pull gemma3:4b
   ```

3. Una vez que ambos muestren `success`, vuelve a levantar el sistema (esta vez será instantáneo):
   ```bash
   docker-compose up -d
   ```

---

## Accesos y Puertos

Una vez que el despliegue finalice exitosamente, los servicios estarán disponibles en los siguientes enlaces locales:

* **Interfaz de Usuario (Frontend):** [http://localhost:4200](http://localhost:4200)
* **API Orquestador (Backend Tomcat):** [http://localhost:8080/MantenedorRAG](http://localhost:8080/MantenedorRAG)
* **Panel de Administración Vectorial (Qdrant Dashboard):** [http://localhost:6333/dashboard](http://localhost:6333/dashboard)
* **API Motor SPLADE (Vectores Dispersos):** [http://localhost:8000/docs](http://localhost:8000/docs) (Interfaz Swagger)

---

## Estructura del Repositorio

* `/frontend`: Código fuente Angular (Vistas, Componentes y Servicios). Dockerfile Multi-stage.
* `/backend`: Código fuente Java, dependencias y binario compilado `.war` para Apache Tomcat.
* `/splade`: Microservicio en Python con `FastEmbed` para la generación de vectores dispersos en CPU.
* `/db`: Script de inicialización `init.sql` para el esquema relacional en PostgreSQL.
* `docker-compose.yml`: Orquestador principal de la topología de red, volúmenes y dependencias entre contenedores.

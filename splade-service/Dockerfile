# 1. Usar una imagen oficial de Python ligera
FROM python:3.11-slim

# 2. Definir el directorio de trabajo dentro del contenedor
WORKDIR /app

# 3. Copiar e instalar las dependencias primero (aprovecha la caché de Docker)
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 4. PRE-DESCARGAR EL MODELO (TRUCO CLAVE)
# Esto ejecuta FastEmbed una vez durante el 'build' para que el modelo SPLADE 
# quede guardado dentro de la imagen de Docker para siempre.
RUN python -c "from fastembed import SparseTextEmbedding; SparseTextEmbedding(model_name='prithivida/Splade_PP_en_v1')"

# 5. Copiar el código fuente (main.py)
COPY main.py .

# 6. Exponer el puerto donde correrá FastAPI
EXPOSE 8000

# 7. Comando para iniciar el servidor
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
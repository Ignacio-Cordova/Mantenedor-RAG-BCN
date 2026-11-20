# Usar la imagen oficial de Tomcat con Java (ajusta la versión de Java si usas una superior, ej. jdk17)
FROM tomcat:8.5-jdk11

# Copiar tu archivo .war directamente a la carpeta webapps de Tomcat
COPY MantenedorRAG.war /usr/local/tomcat/webapps/

# Exponer el puerto de Tomcat
EXPOSE 8080

# Iniciar Tomcat
CMD ["catalina.sh", "run"]
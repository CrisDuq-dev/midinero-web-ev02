# Módulo de Transacciones – Mi Dinero+ (GA7-220501096-AA2-EV02)

Evolución de EV01: el mismo módulo de Transacciones ahora expuesto como
aplicación web con **Servlets** y **JSP**, corriendo en **Apache Tomcat 9**.

## Estructura

```
src/com/midinero/conexion  -> ConexionBD.java       (igual que EV01)
src/com/midinero/modelo    -> Transaccion.java      (igual que EV01)
src/com/midinero/dao       -> TransaccionDAO.java   (igual que EV01)
src/com/midinero/servlet   -> TransaccionServlet.java (NUEVO: doGet + doPost)

webapp/formulario.html     -> formulario para registrar (POST)
webapp/resultado.jsp       -> JSP que muestra el listado (GET)
webapp/error.jsp           -> JSP de error
webapp/index.html          -> redirige al formulario
webapp/WEB-INF/classes/    -> aquí van los .class compilados
webapp/WEB-INF/lib/        -> aquí va el driver mysql-connector-j.jar
```

## Requisitos

- JDK 17+ (ya lo tienes de EV01)
- Apache Tomcat 9 instalado (puerto 8080)
- Driver JDBC `mysql-connector-j-x.x.x.jar` (el mismo de EV01)
- Base de datos `midinero_db` con la tabla `transacciones` (la misma de EV01)

## Paso a paso para compilar y desplegar

### 1. Copiar el driver JDBC
Copia tu archivo `mysql-connector-j-26.7.0.jar` (el que ya descargaste en EV01)
dentro de `lib/` (para compilar) **y** dentro de `webapp/WEB-INF/lib/` (para que
Tomcat lo tenga disponible en tiempo de ejecución).

### 2. Compilar los archivos Java
Necesitas el `servlet-api.jar` de tu instalación de Tomcat para compilar el
Servlet. Normalmente está en:
```
C:\Program Files\Apache Software Foundation\Tomcat 9.0\lib\servlet-api.jar
```

Desde la raíz del proyecto (`midinero-web`), ejecuta:
```
javac -d webapp/WEB-INF/classes -cp "lib/mysql-connector-j-26.7.0.jar;C:\Program Files\Apache Software Foundation\Tomcat 9.0\lib\servlet-api.jar" -encoding UTF-8 src/com/midinero/conexion/ConexionBD.java src/com/midinero/modelo/Transaccion.java src/com/midinero/dao/TransaccionDAO.java src/com/midinero/servlet/TransaccionServlet.java
```

Esto deja los `.class` ya organizados en `webapp/WEB-INF/classes/com/midinero/...`,
tal como Tomcat los espera.

### 3. Copiar la carpeta al servidor
Copia toda la carpeta `webapp/` (con su contenido: HTML, JSP, WEB-INF) dentro
de la carpeta `webapps` de tu instalación de Tomcat, renombrándola a `midinero`:
```
C:\Program Files\Apache Software Foundation\Tomcat 9.0\webapps\midinero\
```

### 4. Iniciar Tomcat
Si lo instalaste como servicio de Windows, ya debería estar corriendo. Si no,
ejecuta `bin\startup.bat` dentro de la carpeta de Tomcat.

### 5. Probar en el navegador
```
http://localhost:8080/midinero/formulario.html
```
- Llena el formulario y dale "Registrar transacción" (POST).
- Deberías caer en la vista de listado (`resultado.jsp`) mostrando la nueva fila.
- Prueba también entrar directo a `http://localhost:8080/midinero/transacciones` (GET).

## Control de versiones (Git/GitHub)

Este proyecto continúa el repositorio de EV01. Haz commits por funcionalidad:
```bash
git add src/com/midinero/servlet
git commit -m "Servlet con doGet y doPost"

git add webapp/formulario.html
git commit -m "Formulario HTML con method post"

git add webapp/resultado.jsp webapp/error.jsp webapp/index.html
git commit -m "Paginas JSP para mostrar resultados"

git push
```

## Checklist de entrega (según la guía EV02)

- [x] Archivos completos del proyecto web
- [x] Formulario HTML con método POST/GET
- [x] Servlet con doGet y doPost implementados
- [x] Páginas JSP mostrando resultados dinámicos
- [x] Conexión JDBC funcionando desde el Servlet
- [ ] Archivo con enlace del repositorio GitHub
- [ ] Nombre del ZIP: NOMBREAPELLIDO_AA2_EV02

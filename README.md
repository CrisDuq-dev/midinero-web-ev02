# GA7-220501096-AA2-EV02: Módulos de software codificados y probados

## Descripción de la evidencia

Este proyecto es la evolución de la evidencia **EV01 (Codificación de módulos
del software)**. En EV01 se construyó un módulo Java de consola que se
conectaba a una base de datos MySQL mediante JDBC e implementaba el CRUD
completo (insertar, consultar, actualizar, eliminar) sobre la entidad
**Transacción** del proyecto Mi Dinero+.

En esta evidencia (EV02) ese mismo módulo se **expone como una aplicación
web**, reemplazando el menú de consola por una interfaz accesible desde el
navegador, usando las tecnologías Java para desarrollo web del lado del
servidor: **Servlets** y **JSP (JavaServer Pages)**.

## Funcionalidades implementadas

| Requisito de la guía | Cómo se cumple |
|---|---|
| Código con formularios HTML y Servlets | `webapp/formulario.html` envía los datos al `TransaccionServlet` |
| Métodos GET y POST para uso de parámetros | El Servlet implementa `doGet()` (consulta el listado) y `doPost()` (recibe el formulario y registra una transacción) |
| Elementos de JSP | `resultado.jsp` y `error.jsp` usan scriptlets (`<% %>`), expresiones (`<%= %>`) y directivas de página para mostrar los datos de forma dinámica |
| Herramientas de versionamiento del código | Proyecto versionado en Git, con un commit por cada funcionalidad, y subido a GitHub |

## Arquitectura y flujo de la aplicación

```
Usuario (navegador)
     |
     v
formulario.html --- POST (tipo, monto, fecha, descripcion, categoria) ---+
     |                                                                   v
     |                                                  TransaccionServlet (doPost)
     |                                                                   |
     |                                                TransaccionDAO.insertarTransaccion()
     |                                                                   |
     |                                                          MySQL (via JDBC)
     |                                                                   |
     |                                                  redirect -> /transacciones (GET)
     |                                                                   |
     +----------------  Ver todas las transacciones (GET)  --------------+
                                                                          |
                                                  TransaccionServlet (doGet)
                                                                          |
                                                TransaccionDAO.consultarTransacciones()
                                                                          |
                                                          resultado.jsp (muestra tabla)
```

## Estructura del proyecto

```
src/com/midinero/conexion  -> ConexionBD.java         (reutilizado de EV01, sin cambios)
src/com/midinero/modelo    -> Transaccion.java        (reutilizado de EV01, sin cambios)
src/com/midinero/dao       -> TransaccionDAO.java     (reutilizado de EV01, sin cambios)
src/com/midinero/servlet   -> TransaccionServlet.java (NUEVO: capa web con doGet/doPost)

webapp/formulario.html     -> formulario HTML (method="post")
webapp/resultado.jsp       -> JSP que renderiza el listado (llamado por GET)
webapp/error.jsp           -> JSP de manejo de errores
webapp/index.html          -> redirige al formulario
webapp/WEB-INF/classes/    -> clases .java ya compiladas, listas para desplegar
webapp/WEB-INF/lib/        -> driver JDBC (mysql-connector-j) usado en tiempo de ejecución
```

## Estándares de codificación aplicados

- **Variables**: camelCase (`nombreTransaccion`, `precioTotal`, etc.)
- **Métodos**: camelCase con verbo (`insertarTransaccion`, `consultarTransacciones`)
- **Clases**: PascalCase (`TransaccionServlet`, `TransaccionDAO`, `ConexionBD`)
- **Paquetes**: minúsculas (`com.midinero.servlet`, `com.midinero.dao`)

## Tecnologías utilizadas

- Java 25 (JDK)
- Apache Tomcat 9 (contenedor de Servlets)
- Servlets (`javax.servlet`) y JSP
- JDBC con MySQL (mysql-connector-j)
- MySQL / XAMPP
- Git y GitHub

---

## Guía técnica: cómo compilar y desplegar (referencia)

### 1. Base de datos
Reutiliza la misma base `midinero_db` y tabla `transacciones` creadas en EV01.

### 2. Compilar
Se necesita el `servlet-api.jar` de la instalación de Tomcat 9 además del driver JDBC:
```
javac -d webapp/WEB-INF/classes -cp "lib/mysql-connector-j-26.7.0.jar;C:\Program Files\Apache Software Foundation\Tomcat 9.0\lib\servlet-api.jar" -encoding UTF-8 src/com/midinero/conexion/ConexionBD.java src/com/midinero/modelo/Transaccion.java src/com/midinero/dao/TransaccionDAO.java src/com/midinero/servlet/TransaccionServlet.java
```

### 3. Desplegar
Copiar la carpeta `webapp/` dentro de `webapps` de Tomcat, renombrándola a `midinero`:
```
C:\Program Files\Apache Software Foundation\Tomcat 9.0\webapps\midinero\
```

### 4. Ejecutar
Con Tomcat corriendo, abrir en el navegador:
```
http://localhost:8080/midinero/formulario.html
```

## Repositorio

El código completo, con el historial de commits por funcionalidad, está en:
https://github.com/CrisDuq-dev/midinero-web-ev02

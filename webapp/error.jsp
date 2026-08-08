<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mi Dinero+ | Error</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 500px; margin: 40px auto; padding: 0 20px; }
        h1 { color: #c62828; }
        a { display: inline-block; margin-top: 20px; color: #1565c0; }
    </style>
</head>
<body>
    <h1>Ocurrió un error</h1>
    <p><%= request.getAttribute("mensajeError") %></p>
    <a href="formulario.html">← Volver al formulario</a>
</body>
</html>

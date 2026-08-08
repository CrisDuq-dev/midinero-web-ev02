<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.midinero.modelo.Transaccion" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mi Dinero+ | Transacciones</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 700px; margin: 40px auto; padding: 0 20px; }
        h1 { color: #2e7d32; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background: #2e7d32; color: white; }
        .ingreso { color: #2e7d32; font-weight: bold; }
        .gasto { color: #c62828; font-weight: bold; }
        a { display: inline-block; margin-top: 20px; color: #1565c0; }
    </style>
</head>
<body>
    <h1>Listado de transacciones</h1>

    <%
        // Elemento JSP: scriptlet que recupera el atributo enviado por el Servlet
        List<Transaccion> transacciones = (List<Transaccion>) request.getAttribute("transacciones");
    %>

    <% if (transacciones == null || transacciones.isEmpty()) { %>
        <p>No hay transacciones registradas todavía.</p>
    <% } else { %>
        <table>
            <tr>
                <th>ID</th>
                <th>Tipo</th>
                <th>Monto</th>
                <th>Fecha</th>
                <th>Descripción</th>
                <th>Categoría</th>
            </tr>
            <% for (Transaccion t : transacciones) { %>
                <tr>
                    <td><%= t.getId() %></td>
                    <td class="<%= t.getTipo().equalsIgnoreCase("Ingreso") ? "ingreso" : "gasto" %>">
                        <%= t.getTipo() %>
                    </td>
                    <td><%= t.getMonto() %></td>
                    <td><%= t.getFecha() %></td>
                    <td><%= t.getDescripcion() %></td>
                    <td><%= t.getCategoria() %></td>
                </tr>
            <% } %>
        </table>
    <% } %>

    <a href="formulario.html">← Registrar otra transacción</a>
</body>
</html>

package com.midinero.servlet;

import com.midinero.dao.TransaccionDAO;
import com.midinero.modelo.Transaccion;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servlet que expone la entidad Transaccion como una API en formato JSON,
 * para ser consumida por el frontend en React.
 *
 * Ruta: /midinero/api/transacciones
 * - GET    -> devuelve el listado completo en JSON
 * - POST   -> recibe un JSON con los datos y registra una nueva transacción (Crear)
 * - PUT    -> recibe un JSON con id + datos y actualiza esa transacción (Actualizar)
 * - DELETE -> recibe ?id=X y elimina esa transacción (Eliminar)
 */
@WebServlet("/api/transacciones")
public class TransaccionApiServlet extends HttpServlet {

    private final TransaccionDAO transaccionDAO = new TransaccionDAO();

    private void habilitarCORS(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        habilitarCORS(response);
    }

    /**
     * GET -> Devuelve todas las transacciones en formato JSON.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        habilitarCORS(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<Transaccion> listaTransacciones = transaccionDAO.consultarTransacciones();
        String json = convertirListaAJson(listaTransacciones);

        try (PrintWriter salida = response.getWriter()) {
            salida.write(json);
        }
    }

    /**
     * POST -> Recibe un JSON del formulario en React e inserta la nueva transacción.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        habilitarCORS(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String cuerpoJson = leerCuerpoPeticion(request);

        try {
            Transaccion nuevaTransaccion = convertirJsonATransaccion(cuerpoJson);
            validarTransaccion(nuevaTransaccion);
            boolean exito = transaccionDAO.insertarTransaccion(nuevaTransaccion);

            response.setStatus(exito ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter salida = response.getWriter()) {
                salida.write("{\"exito\": " + exito + "}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter salida = response.getWriter()) {
                salida.write("{\"exito\": false, \"error\": \"" + escaparJson(e.getMessage()) + "\"}");
            }
        }
    }

    /**
     * PUT -> Recibe un JSON con id + datos y actualiza esa transacción existente.
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        habilitarCORS(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String cuerpoJson = leerCuerpoPeticion(request);

        try {
            int id = Integer.parseInt(extraerCampoNumerico(cuerpoJson, "id"));
            Transaccion transaccionActualizada = convertirJsonATransaccion(cuerpoJson);
            transaccionActualizada.setId(id);
            validarTransaccion(transaccionActualizada);

            boolean exito = transaccionDAO.actualizarTransaccion(transaccionActualizada);

            response.setStatus(exito ? HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter salida = response.getWriter()) {
                salida.write("{\"exito\": " + exito + "}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter salida = response.getWriter()) {
                salida.write("{\"exito\": false, \"error\": \"" + escaparJson(e.getMessage()) + "\"}");
            }
        }
    }

    /**
     * DELETE -> Recibe el id como parámetro de la URL (?id=X) y elimina esa transacción.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        habilitarCORS(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean exito = transaccionDAO.eliminarTransaccion(id);

            response.setStatus(exito ? HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter salida = response.getWriter()) {
                salida.write("{\"exito\": " + exito + "}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter salida = response.getWriter()) {
                salida.write("{\"exito\": false, \"error\": \"" + escaparJson(e.getMessage()) + "\"}");
            }
        }
    }

    /**
     * Valida los datos de una transacción antes de guardarla.
     * Lanza IllegalArgumentException con un mensaje claro si algo no cumple.
     * Estas validaciones son las que se documentan en las pruebas de EV02.
     */
    private void validarTransaccion(Transaccion t) {
        if (t.getTipo() == null || (!t.getTipo().equalsIgnoreCase("Ingreso") && !t.getTipo().equalsIgnoreCase("Gasto"))) {
            throw new IllegalArgumentException("El tipo debe ser 'Ingreso' o 'Gasto'");
        }
        if (t.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
        if (t.getFecha() == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        if (t.getFecha().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha no puede ser futura");
        }
        if (t.getDescripcion() == null || t.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        }
        if (t.getDescripcion().length() > 100) {
            throw new IllegalArgumentException("La descripción no puede superar 100 caracteres");
        }
        if (t.getCategoria() == null || t.getCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }
        if (t.getCategoria().length() > 50) {
            throw new IllegalArgumentException("La categoría no puede superar 50 caracteres");
        }
    }

    // ---------- Métodos auxiliares de conversión JSON (sin librerías externas) ----------

    private String leerCuerpoPeticion(HttpServletRequest request) throws IOException {
        StringBuilder contenido = new StringBuilder();
        try (BufferedReader lector = request.getReader()) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                contenido.append(linea);
            }
        }
        return contenido.toString();
    }

    private String convertirListaAJson(List<Transaccion> transacciones) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < transacciones.size(); i++) {
            json.append(convertirTransaccionAJson(transacciones.get(i)));
            if (i < transacciones.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    private String convertirTransaccionAJson(Transaccion t) {
        return "{"
                + "\"id\":" + t.getId() + ","
                + "\"tipo\":\"" + escaparJson(t.getTipo()) + "\","
                + "\"monto\":" + t.getMonto() + ","
                + "\"fecha\":\"" + t.getFecha() + "\","
                + "\"descripcion\":\"" + escaparJson(t.getDescripcion()) + "\","
                + "\"categoria\":\"" + escaparJson(t.getCategoria()) + "\""
                + "}";
    }

    private Transaccion convertirJsonATransaccion(String json) {
        String tipo = extraerCampoTexto(json, "tipo");
        double monto = Double.parseDouble(extraerCampoNumerico(json, "monto"));
        LocalDate fecha = LocalDate.parse(extraerCampoTexto(json, "fecha"));
        String descripcion = extraerCampoTexto(json, "descripcion");
        String categoria = extraerCampoTexto(json, "categoria");

        return new Transaccion(tipo, monto, fecha, descripcion, categoria);
    }

    /**
     * Extrae el valor de texto de un campo JSON, respetando comillas y
     * barras invertidas escapadas dentro del propio texto (por ejemplo,
     * una descripción que contiene comillas: "tienda \"El Ahorro\"").
     * A diferencia de una simple expresión regular, este método recorre
     * carácter por carácter para no cortar el valor en la primera comilla
     * que encuentre.
     */
    private String extraerCampoTexto(String json, String nombreCampo) {
        String buscar = "\"" + nombreCampo + "\"";
        int inicioClave = json.indexOf(buscar);
        if (inicioClave == -1) {
            throw new IllegalArgumentException("Falta el campo: " + nombreCampo);
        }

        int posDosPuntos = json.indexOf(':', inicioClave + buscar.length());
        int i = posDosPuntos + 1;
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
            i++;
        }
        if (i >= json.length() || json.charAt(i) != '"') {
            throw new IllegalArgumentException("El campo " + nombreCampo + " debe ser texto");
        }
        i++; // saltar la comilla de apertura

        StringBuilder valor = new StringBuilder();
        while (i < json.length()) {
            char actual = json.charAt(i);
            if (actual == '\\' && i + 1 < json.length()) {
                char siguiente = json.charAt(i + 1);
                switch (siguiente) {
                    case '"': valor.append('"'); break;
                    case '\\': valor.append('\\'); break;
                    case 'n': valor.append('\n'); break;
                    case 'r': valor.append('\r'); break;
                    case 't': valor.append('\t'); break;
                    default: valor.append(siguiente);
                }
                i += 2;
            } else if (actual == '"') {
                break; // fin real del valor
            } else {
                valor.append(actual);
                i++;
            }
        }
        return valor.toString();
    }

    /**
     * Extrae el valor de un campo numérico (sin comillas), como monto o id.
     */
    private String extraerCampoNumerico(String json, String nombreCampo) {
        String buscar = "\"" + nombreCampo + "\"";
        int inicioClave = json.indexOf(buscar);
        if (inicioClave == -1) {
            throw new IllegalArgumentException("Falta el campo: " + nombreCampo);
        }

        int posDosPuntos = json.indexOf(':', inicioClave + buscar.length());
        int i = posDosPuntos + 1;
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
            i++;
        }
        int inicioValor = i;
        while (i < json.length() && json.charAt(i) != ',' && json.charAt(i) != '}') {
            i++;
        }
        return json.substring(inicioValor, i).trim();
    }

    /**
     * Escapa los caracteres especiales para que el texto sea válido dentro
     * de un JSON, incluso si el usuario ingresó comillas, barras invertidas,
     * saltos de línea o etiquetas tipo <script> en los campos de texto
     * (ver prueba de validación V5).
     */
    private String escaparJson(String texto) {
        if (texto == null) return "";
        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

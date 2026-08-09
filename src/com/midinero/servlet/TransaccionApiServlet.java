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
 * para ser consumida por el frontend en React (evidencia AA3-EV01).
 *
 * Este Servlet es ADICIONAL: no modifica el TransaccionServlet original
 * de EV02 (que sigue sirviendo el formulario.html y las vistas JSP).
 *
 * Ruta: /midinero/api/transacciones
 * - GET  -> devuelve el listado completo en JSON
 * - POST -> recibe un JSON con los datos y registra una nueva transacción
 */
@WebServlet("/api/transacciones")
public class TransaccionApiServlet extends HttpServlet {

    private final TransaccionDAO transaccionDAO = new TransaccionDAO();

    /**
     * Habilita CORS para que React (que corre en otro puerto, ej. localhost:5173)
     * pueda llamar a este backend sin que el navegador lo bloquee.
     */
    private void habilitarCORS(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
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
        String tipo = extraerCampo(json, "tipo");
        double monto = Double.parseDouble(extraerCampo(json, "monto"));
        LocalDate fecha = LocalDate.parse(extraerCampo(json, "fecha"));
        String descripcion = extraerCampo(json, "descripcion");
        String categoria = extraerCampo(json, "categoria");

        return new Transaccion(tipo, monto, fecha, descripcion, categoria);
    }

    private String extraerCampo(String json, String nombreCampo) {
        Pattern patron = Pattern.compile("\"" + nombreCampo + "\"\\s*:\\s*\"?([^\",}]+)\"?");
        Matcher coincidencia = patron.matcher(json);
        if (coincidencia.find()) {
            return coincidencia.group(1).trim();
        }
        throw new IllegalArgumentException("Falta el campo: " + nombreCampo);
    }

    private String escaparJson(String texto) {
        if (texto == null) return "";
        return texto.replace("\"", "\\\"");
    }
}

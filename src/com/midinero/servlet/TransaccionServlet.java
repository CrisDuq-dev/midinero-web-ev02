package com.midinero.servlet;

import com.midinero.dao.TransaccionDAO;
import com.midinero.modelo.Transaccion;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Servlet controlador para el módulo de Transacciones.
 * - doGet: consulta y muestra el listado de transacciones (parámetros por URL).
 * - doPost: recibe el formulario e inserta una nueva transacción (parámetros por cuerpo).
 *
 * Estándar de nombramiento de clases: PascalCase.
 */
@WebServlet("/transacciones")
public class TransaccionServlet extends HttpServlet {

    private final TransaccionDAO transaccionDAO = new TransaccionDAO();

    /**
     * GET -> Consulta todas las transacciones y las envía a la vista JSP.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Transaccion> listaTransacciones = transaccionDAO.consultarTransacciones();
        request.setAttribute("transacciones", listaTransacciones);
        request.getRequestDispatcher("/resultado.jsp").forward(request, response);
    }

    /**
     * POST -> Recibe los datos del formulario HTML e inserta una nueva transacción.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        try {
            String tipo = request.getParameter("tipo");
            double monto = Double.parseDouble(request.getParameter("monto"));
            LocalDate fecha = LocalDate.parse(request.getParameter("fecha"));
            String descripcion = request.getParameter("descripcion");
            String categoria = request.getParameter("categoria");

            Transaccion nuevaTransaccion = new Transaccion(tipo, monto, fecha, descripcion, categoria);
            boolean exito = transaccionDAO.insertarTransaccion(nuevaTransaccion);

            if (exito) {
                response.sendRedirect("transacciones");
            } else {
                request.setAttribute("mensajeError", "No se pudo registrar la transacción.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("mensajeError", "Datos inválidos: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}

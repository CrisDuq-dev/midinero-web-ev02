package com.midinero.modelo;

import java.time.LocalDate;

/**
 * Clase modelo que representa una transacción financiera (ingreso o gasto)
 * del proyecto Mi Dinero+.
 *
 * Estándar de nombramiento de clases: PascalCase.
 * Estándar de nombramiento de variables: camelCase.
 */
public class Transaccion {

    private int id;
    private String tipo;          // "Ingreso" o "Gasto"
    private double monto;
    private LocalDate fecha;
    private String descripcion;
    private String categoria;

    public Transaccion() {
    }

    public Transaccion(String tipo, double monto, LocalDate fecha, String descripcion, String categoria) {
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.categoria = categoria;
    }

    public Transaccion(int id, String tipo, double monto, LocalDate fecha, String descripcion, String categoria) {
        this.id = id;
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.categoria = categoria;
    }

    // Getters y setters (camelCase)

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "ID: " + id +
                " | Tipo: " + tipo +
                " | Monto: " + monto +
                " | Fecha: " + fecha +
                " | Descripción: " + descripcion +
                " | Categoría: " + categoria;
    }
}

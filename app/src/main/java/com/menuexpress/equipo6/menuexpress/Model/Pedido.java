package com.menuexpress.equipo6.menuexpress.Model;

public class Pedido {
    private String idComida;
    private String nombreComida;
    private String cantidad;
    private String precio;
    private String descuento;

    public Pedido() {
    }

    public Pedido(String idComida, String nombreComida, String cantidad, String precio, String descuento) {
        this.idComida = idComida;
        this.nombreComida = nombreComida;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descuento = descuento;
    }

    public String getIdComida() {
        return idComida;
    }

    public void setIdComida(String idComida) {
        this.idComida = idComida;
    }

    public String getNombreComida() {
        return nombreComida;
    }

    public void setNombreComida(String nombreComida) {
        this.nombreComida = nombreComida;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getDescuento() {
        return descuento;
    }

    public void setDescuento(String descuento) {
        this.descuento = descuento;
    }
}

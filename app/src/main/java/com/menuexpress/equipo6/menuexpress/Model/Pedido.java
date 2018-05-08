package com.menuexpress.equipo6.menuexpress.Model;

public class Pedido {
    private String email;
    private String idComida;
    private String nombreComida;
    private String cantidad;
    private String precio;
    private String descuento;
    private String imagen;

    public Pedido() {
    }

    public Pedido(String email, String idComida, String nombreComida, String cantidad, String precio, String descuento, String imagen) {
        this.email = email;
        this.idComida = idComida;
        this.nombreComida = nombreComida;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descuento = descuento;
        this.imagen = imagen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}

package com.menuexpress.equipo6.menuexpress.Model;

import java.util.List;

public class Solicitud {
    private String telefono;
    private String nombre;
    private String direccion;
    private String total;
    private List<Pedido> comidas; //la comida en el pedido

    public Solicitud() {
    }

    public Solicitud(String telefono, String nombre, String direccion, String total, List<Pedido> comidas) {
        this.telefono = telefono;
        this.nombre = nombre;
        this.direccion = direccion;
        this.total = total;
        this.comidas = comidas;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Pedido> getComidas() {
        return comidas;
    }

    public void setComidas(List<Pedido> comidas) {
        this.comidas = comidas;
    }
}

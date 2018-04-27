package com.menuexpress.equipo6.menuexpress.Model;

import java.util.List;

public class Solicitar {
    private String telefono;
    private String nombre;
    private String email;
    private String direccion;
    private String total;
    private String estado = "0";
    private List<Pedido> comidas; //la comida en el pedido

    public Solicitar() {
    }

    public Solicitar(String nombre, String email, String total, List<Pedido> comidas) {
        this.nombre = nombre;
        this.email = email;
        this.total = total;
        this.comidas = comidas;
        this.estado = "0"; //0=colocado, 1=envío, 2=enviado
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Pedido> getComidas() {
        return comidas;
    }

    public void setComidas(List<Pedido> comidas) {
        this.comidas = comidas;
    }
}

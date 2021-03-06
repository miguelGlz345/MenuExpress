package com.menuexpress.equipo6.menuexpress.Model;

import java.util.List;

public class Solicitar {
    private String telefono;
    private String nombre;
    private String email;
    private String direccion;
    private String total;
    private String estado;
    private String fecha;
    private String hora;
    private String estado_email;

    private List<Pedido> comidas; //la comida en el pedido

    public Solicitar() {
    }

    public Solicitar(String nombre, String email, String total, List<Pedido> comidas, String estado, String fecha, String hora, String estado_email) {
        this.nombre = nombre;
        this.email = email;
        this.total = total;
        this.comidas = comidas;
        this.estado = estado;
        this.fecha = fecha;
        this.hora = hora;
        this.estado_email = estado_email;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getEstado_email() {
        return estado_email;
    }

    public void setEstado_email(String estado_email) {
        this.estado_email = estado_email;
    }

    public List<Pedido> getComidas() {
        return comidas;
    }

    public void setComidas(List<Pedido> comidas) {
        this.comidas = comidas;
    }
}

package com.menuexpress.equipo6.menuexpress.Model;

public class Sender {
    public String to;
    public Notificacion notificacion;

    public Sender(String to, Notificacion notificacion) {
        this.to = to;
        this.notificacion = notificacion;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notificacion getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(Notificacion notificacion) {
        this.notificacion = notificacion;
    }
}

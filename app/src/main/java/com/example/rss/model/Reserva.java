package com.example.rss.model;

import java.io.Serializable;

public class Reserva implements Serializable {

    private int id;
    private String fecha_reserva;
    private String hora_inicio;
    private String hora_fin;

    public Reserva(int id, String fecha_reserva, String hora_inicio, String hora_fin) {
        this.id = id;
        this.fecha_reserva = fecha_reserva;
        this.hora_inicio = hora_inicio;
        this.hora_fin = hora_fin;
    }

    public Reserva(String fecha_reserva, String hora_inicio, String hora_fin) {
        this.fecha_reserva = fecha_reserva;
        this.hora_inicio = hora_inicio;
        this.hora_fin = hora_fin;
    }

    public Reserva() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFecha_reserva() {
        return fecha_reserva;
    }

    public void setFecha_reserva(String fecha_reserva) {
        this.fecha_reserva = fecha_reserva;
    }

    public String getHora_inicio() {
        if (hora_inicio.matches("^..:..:..$")){
            return hora_inicio.substring(0,5);
        }else {
            return hora_inicio;
        }
    }

    public void setHora_inicio(String hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public String getHora_fin() {
        if (hora_fin.matches("^..:..:..$")){
            return hora_fin.substring(0,5);
        }else {
            return hora_fin;
        }
    }

    public void setHora_fin(String hora_fin) {
        this.hora_fin = hora_fin;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "fecha_reserva='" + fecha_reserva + '\'' +
                ", hora_inicio='" + hora_inicio + '\'' +
                ", hora_fin='" + hora_fin + '\'' +
                '}';
    }
}

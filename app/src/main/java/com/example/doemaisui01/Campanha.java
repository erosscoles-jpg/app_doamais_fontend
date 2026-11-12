package com.example.doemaisui01;

public class Campanha {

    private String titulo;
    private String data;
    private String horario;
    private String local;
    private String status;

    public Campanha(String titulo, String data, String horario,
                    String local, String status) {

        this.titulo = titulo;
        this.data = data;
        this.horario = horario;
        this.local = local;
        this.status = status;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getData() {
        return data;
    }

    public String getHorario() {
        return horario;
    }

    public String getLocal() {
        return local;
    }

    public String getStatus() {
        return status;
    }
}
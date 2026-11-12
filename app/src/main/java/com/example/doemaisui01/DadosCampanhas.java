package com.example.doemaisui01;

import java.util.ArrayList;

public class DadosCampanhas {

    public static ArrayList<Campanha> listar() {

        ArrayList<Campanha> lista = new ArrayList<>();

        lista.add(new Campanha(
                "Campanha de Natal",
                "15 de Dezembro de 2026",
                "08:00",
                "Hospital Central",
                "Pendente"
        ));

        lista.add(new Campanha(
                "Campanha de Nossa Senhora",
                "4 de Novembro de 2026",
                "08:00",
                "Hospital Central",
                "Encerrada"
        ));

        lista.add(new Campanha(
                "Doe Sangue, Salve Vidas",
                "20 de Dezembro de 2026",
                "09:00",
                "Hemocentro",
                "Pendente"
        ));

        return lista;
    }
}
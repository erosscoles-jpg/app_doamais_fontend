package com.example.doemaisui01.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Formatos {

    private static final Locale PT_BR = new Locale("pt", "BR");

    // "2026-12-01T08:00:00"
    public static Date dataHora(String iso) {
        try {
            if (iso == null || iso.length() < 19) return null;
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(iso.substring(0, 19));
        } catch (Exception e) {
            return null;
        }
    }

    // "15 de Dezembro de 2026"
    public static String dataExtenso(Date d) {
        if (d == null) return "";
        String s = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", PT_BR).format(d);
        int i = s.indexOf(" de ") + 4;
        return s.substring(0, i) + Character.toUpperCase(s.charAt(i)) + s.substring(i + 1);
    }

    // "08:00"
    public static String hora(Date d) {
        return d == null ? "" : new SimpleDateFormat("HH:mm", PT_BR).format(d);
    }

    // Encerrada / Ativa / Pendente (ainda vai acontecer)
    public static String statusCampanha(Date inicio, Date fim) {
        Date agora = new Date();
        if (fim != null && agora.after(fim)) return "Encerrada";
        if (inicio != null && agora.before(inicio)) return "Pendente";
        return "Ativa";
    }

    // "1998-05-20" -> idade em anos
    public static int idade(String nascimento) {
        try {
            Date nasc = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(nascimento);
            Calendar n = Calendar.getInstance();
            n.setTime(nasc);
            Calendar h = Calendar.getInstance();
            int anos = h.get(Calendar.YEAR) - n.get(Calendar.YEAR);
            if (h.get(Calendar.DAY_OF_YEAR) < n.get(Calendar.DAY_OF_YEAR)) anos--;
            return anos;
        } catch (Exception e) {
            return 0;
        }
    }

    // "O_POSITIVE" -> "O+"
    public static String sangue(String tipo) {
        if (tipo == null) return "";
        return tipo.replace("_POSITIVE", "+").replace("_NEGATIVE", "-");
    }

    // Status da ficha (API) -> texto da tela
    public static String situacaoFicha(String status) {
        if ("ELIGIBLE".equals(status)) return "Aprovado";
        if ("INELIGIBLE".equals(status)) return "Inapto";
        return "Pendente"; // PENDING ou IN_PROGRESS
    }
}
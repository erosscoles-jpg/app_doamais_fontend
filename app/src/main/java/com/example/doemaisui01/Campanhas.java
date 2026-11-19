package com.example.doemaisui01;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doemaisui01.api.ApiClient;
import com.example.doemaisui01.api.ApiModels;
import com.example.doemaisui01.api.Formatos;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Campanhas extends AppCompatActivity {

    private LinearLayout containerCampanhas;
    private TextView tituloCampanhas;

    // Define qual lista sera apresentada: "todas" ou "inscritas"
    private String modo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campanhas);

        containerCampanhas = findViewById(R.id.containerCampanhas);
        tituloCampanhas = findViewById(R.id.tituloCampanhas);

        Button btnVoltar = findViewById(R.id.btnVoltarCampanhas);
        btnVoltar.setOnClickListener(v -> finish());

        modo = getIntent().getStringExtra("modo");
        if (modo == null) {
            modo = "todas";
        }

        configurarTitulo();
        carregarCampanhas();
    }

    private void configurarTitulo() {
        if (modo.equals("inscritas")) {
            tituloCampanhas.setText("Minhas Campanhas");
        } else {
            tituloCampanhas.setText("Campanhas de Doações");
        }
    }

    // BUSCA AS CAMPANHAS NA API (GET /campaigns)
    private void carregarCampanhas() {
        containerCampanhas.removeAllViews();
        mostrarMensagem("Carregando...");
        Log.d("CAMPANHAS", "chamando a API");

        ApiClient.get().listCampaigns().enqueue(new Callback<List<ApiModels.Campaign>>() {
            @Override
            public void onResponse(Call<List<ApiModels.Campaign>> call,
                                   Response<List<ApiModels.Campaign>> resp) {
                if (isFinishing() || isDestroyed()) return;
                Log.d("CAMPANHAS", "resposta HTTP " + resp.code());

                if (!resp.isSuccessful() || resp.body() == null) {
                    mostrarMensagem("Erro ao carregar campanhas (" + resp.code() + ").");
                    return;
                }

                ArrayList<Campanha> lista = new ArrayList<>();
                for (ApiModels.Campaign c : resp.body()) {
                    lista.add(converter(c));
                }

                if (modo.equals("inscritas")) {
                    filtrarInscritas(lista);
                } else {
                    mostrar(lista);
                }
            }

            @Override
            public void onFailure(Call<List<ApiModels.Campaign>> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                Log.e("CAMPANHAS", "falha: " + t);
                mostrarMensagem("Sem conexão com o servidor.\n(" + t.getClass().getSimpleName() + ")");
            }
        });
    }

    // Converte o JSON da API para o modelo que as telas ja usam
    private Campanha converter(ApiModels.Campaign c) {
        Date inicio = Formatos.dataHora(c.startDate);
        Date fim = Formatos.dataHora(c.endDate);

        Campanha campanha = new Campanha(
                c.campaignName,
                Formatos.dataExtenso(inicio),
                Formatos.hora(inicio),
                c.location,
                Formatos.statusCampanha(inicio, fim)
        );
        campanha.setId(c.idCampaign == null ? 0 : c.idCampaign);
        return campanha;
    }

    // MODO "INSCRITAS": usa o id do doador salvo no aparelho (GET /enrollments?donorId=...)
    private void filtrarInscritas(ArrayList<Campanha> todas) {
        SharedPreferences prefs = getSharedPreferences("DoaMais", MODE_PRIVATE);
        long donorId = prefs.getLong("donorId", -1);

        if (donorId == -1) {
            mostrarMensagem("Entre com uma conta de voluntário para ver suas inscrições.");
            return;
        }

        ApiClient.get().listEnrollments(donorId, null)
                .enqueue(new Callback<List<ApiModels.Enrollment>>() {
                    @Override
                    public void onResponse(Call<List<ApiModels.Enrollment>> call,
                                           Response<List<ApiModels.Enrollment>> resp) {
                        if (isFinishing() || isDestroyed()) return;

                        if (!resp.isSuccessful() || resp.body() == null) {
                            mostrarMensagem("Erro ao carregar inscrições (" + resp.code() + ").");
                            return;
                        }

                        Set<Long> ids = new HashSet<>();
                        for (ApiModels.Enrollment e : resp.body()) {
                            ids.add(e.campaignId);
                        }

                        ArrayList<Campanha> minhas = new ArrayList<>();
                        for (Campanha c : todas) {
                            if (ids.contains(c.getId())) {
                                minhas.add(c);
                            }
                        }
                        mostrar(minhas);
                    }

                    @Override
                    public void onFailure(Call<List<ApiModels.Enrollment>> call, Throwable t) {
                        if (isFinishing() || isDestroyed()) return;
                        mostrarMensagem("Sem conexão com o servidor.");
                    }
                });
    }

    // MOSTRA OS CARDS (ou a mensagem de lista vazia)
    private void mostrar(ArrayList<Campanha> lista) {
        containerCampanhas.removeAllViews();

        if (lista.isEmpty()) {
            mostrarMensagem("Nenhuma campanha encontrada.");
            return;
        }

        for (Campanha campanha : lista) {
            adicionarCard(campanha);
        }
    }

    private void mostrarMensagem(String texto) {
        containerCampanhas.removeAllViews();

        TextView mensagem = new TextView(this);
        mensagem.setText(texto);
        mensagem.setTextSize(18);
        mensagem.setTextColor(Color.GRAY);
        mensagem.setPadding(0, 30, 0, 30);

        containerCampanhas.addView(mensagem);
    }

    // CRIA CADA CARD (igual ao seu codigo anterior)
    private void adicionarCard(Campanha campanha) {

        View card = LayoutInflater.from(this).inflate(
                R.layout.item_campanha,
                containerCampanhas,
                false
        );

        TextView titulo = card.findViewById(R.id.txtTituloCampanha);
        TextView data = card.findViewById(R.id.txtDataCampanha);
        TextView horario = card.findViewById(R.id.txtHorarioCampanha);
        TextView local = card.findViewById(R.id.txtLocalCampanha);
        TextView status = card.findViewById(R.id.txtStatusCampanha);

        titulo.setText(campanha.getTitulo());
        data.setText(campanha.getData());
        horario.setText(campanha.getHorario());
        local.setText(campanha.getLocal());
        status.setText(campanha.getStatus());

        configurarStatus(status, campanha.getStatus());

        Button btnLembrete = card.findViewById(R.id.btnLembreteCampanha);

        if (btnLembrete != null) {
            if (campanha.getStatus().equalsIgnoreCase("Pendente")) {
                btnLembrete.setVisibility(View.VISIBLE);
                btnLembrete.setOnClickListener(v -> Toast.makeText(
                        Campanhas.this,
                        "Lembrete marcado para " + campanha.getTitulo(),
                        Toast.LENGTH_SHORT
                ).show());
            } else {
                btnLembrete.setVisibility(View.GONE);
            }
        }

        containerCampanhas.addView(card);
    }

    private void configurarStatus(TextView status, String situacao) {

        GradientDrawable fundo = new GradientDrawable();
        fundo.setCornerRadius(12f);
        fundo.setStroke(1, Color.TRANSPARENT);

        if (situacao.equalsIgnoreCase("Pendente")) {
            fundo.setColor(Color.parseColor("#FF5722"));
            status.setTextColor(Color.BLACK);

        } else if (situacao.equalsIgnoreCase("Encerrada")) {
            fundo.setColor(Color.parseColor("#676A73"));
            status.setTextColor(Color.WHITE);

        } else if (situacao.equalsIgnoreCase("Ativa")) {
            fundo.setColor(Color.parseColor("#009688"));
            status.setTextColor(Color.WHITE);
        }

        status.setBackground(fundo);
    }
}
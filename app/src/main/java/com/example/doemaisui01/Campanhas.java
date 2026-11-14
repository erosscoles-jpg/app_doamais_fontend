package com.example.doemaisui01;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Campanhas extends AppCompatActivity {

    private LinearLayout containerCampanhas;

    private TextView tituloCampanhas;

    private ArrayList<Campanha> listaCampanhas;

    // Define qual lista será apresentada
    private String modo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campanhas);

        // Referenciando os componentes
        containerCampanhas = findViewById(R.id.containerCampanhas);

        tituloCampanhas = findViewById(R.id.tituloCampanhas);

        Button btnVoltar = findViewById(R.id.btnVoltarCampanhas);

        btnVoltar.setOnClickListener(v -> finish());

        // Recebendo o modo enviado pelo Dashboard
        modo = getIntent().getStringExtra("modo");

        // Caso nenhum modo seja enviado, mostra todas
        if (modo == null) {
            modo = "todas";
        }

        // Configurando o título
        configurarTitulo();

        // Carregando as campanhas
        carregarCampanhas();
    }

    // CONFIGURAR O TÍTULO CONFORME O MODO
    private void configurarTitulo() {

        if (modo.equals("inscritas")) {

            tituloCampanhas.setText("Minhas Campanhas");

        } else {

            tituloCampanhas.setText("Campanhas de Doações");
        }
    }

    // MÉTODO PARA CARREGAR AS CAMPANHAS
    private void carregarCampanhas() {

        // Busca todas as campanhas cadastradas
        listaCampanhas = DadosCampanhas.listar();

        // Limpa os cards anteriores
        containerCampanhas.removeAllViews();

        /*
         * MODO TODAS:
         * Apresenta todas as campanhas.
         *
         * MODO INSCRITAS:
         * Deverá apresentar somente as campanhas
         * em que o usuário está inscrito.
         */

        if (modo.equals("inscritas")) {

            // FUTURA IMPLEMENTAÇÃO:
            // Filtrar a lista usando os dados de inscrição do usuário.

            Toast.makeText(
                    this,
                    "O filtro de campanhas inscritas precisa ser implementado.",
                    Toast.LENGTH_SHORT
            ).show();
        }

        // Verifica se existem campanhas
        if (listaCampanhas == null || listaCampanhas.isEmpty()) {

            TextView mensagem = new TextView(this);

            mensagem.setText("Nenhuma campanha encontrada.");
            mensagem.setTextSize(18);
            mensagem.setTextColor(Color.GRAY);
            mensagem.setPadding(0, 30, 0, 30);

            containerCampanhas.addView(mensagem);

            return;
        }

        // Cria os cards
        for (Campanha campanha : listaCampanhas) {

            adicionarCard(campanha);
        }
    }

    // MÉTODO PARA CRIAR CADA CARD
    private void adicionarCard(Campanha campanha) {

        LayoutInflater inflater = LayoutInflater.from(this);

        View card = inflater.inflate(
                R.layout.item_campanha,
                containerCampanhas,
                false
        );

        // Referenciando os componentes do card
        TextView titulo = card.findViewById(R.id.txtTituloCampanha);
        TextView data = card.findViewById(R.id.txtDataCampanha);
        TextView horario = card.findViewById(R.id.txtHorarioCampanha);
        TextView local = card.findViewById(R.id.txtLocalCampanha);
        TextView status = card.findViewById(R.id.txtStatusCampanha);

        // Preenchendo os dados
        titulo.setText(campanha.getTitulo());
        data.setText(campanha.getData());
        horario.setText(campanha.getHorario());
        local.setText(campanha.getLocal());
        status.setText(campanha.getStatus());

        // Configurando o status
        configurarStatus(status, campanha.getStatus());

        // Configurando o botão de lembrete
        Button btnLembrete = card.findViewById(R.id.btnLembreteCampanha);

        if (btnLembrete != null) {

            if (campanha.getStatus().equalsIgnoreCase("Pendente")) {

                btnLembrete.setVisibility(View.VISIBLE);

                btnLembrete.setOnClickListener(v -> {

                    Toast.makeText(
                            Campanhas.this,
                            "Lembrete marcado para " + campanha.getTitulo(),
                            Toast.LENGTH_SHORT
                    ).show();

                });

            } else {

                btnLembrete.setVisibility(View.GONE);
            }
        }

        // Adicionando o card ao container
        containerCampanhas.addView(card);
    }

    // MÉTODO PARA CONFIGURAR AS CORES DO STATUS
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
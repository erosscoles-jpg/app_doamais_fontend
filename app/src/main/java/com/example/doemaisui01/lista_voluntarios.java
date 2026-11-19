package com.example.doemaisui01;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doemaisui01.api.ApiClient;
import com.example.doemaisui01.api.ApiModels;
import com.example.doemaisui01.api.Formatos;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class lista_voluntarios extends AppCompatActivity {

    private LinearLayout containerVoluntarios;
    Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_voluntarios);

        View main = findViewById(R.id.btn_cadastrar);

        btnVoltar = findViewById(R.id.button10);

        btnVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(lista_voluntarios.this, dashboradFuncionario.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(main, (v, insets) -> {
            Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            return insets;
        });

        containerVoluntarios = findViewById(R.id.containerVoluntarios);

        carregarVoluntarios();
    }

    // BUSCA AS FICHAS NA API (GET /donor-forms)
    private void carregarVoluntarios() {

        // Limpa os cartões anteriores, evitando duplicações.
        containerVoluntarios.removeAllViews();

        ApiClient.get().listForms(null).enqueue(new Callback<List<ApiModels.DonorForm>>() {
            @Override
            public void onResponse(Call<List<ApiModels.DonorForm>> call,
                                   Response<List<ApiModels.DonorForm>> resp) {
                if (isFinishing() || isDestroyed()) return;

                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(lista_voluntarios.this,
                            "Erro ao carregar voluntários (" + resp.code() + ").",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Voluntario> voluntarios = new ArrayList<>();
                for (ApiModels.DonorForm f : resp.body()) {
                    voluntarios.add(new Voluntario(
                            f.donorName,
                            Formatos.idade(f.birthDate),
                            Formatos.sangue(f.bloodType),
                            Formatos.situacaoFicha(f.status),
                            String.valueOf(f.donorId)
                    ));
                }
                mostrarVoluntarios(voluntarios);
            }

            @Override
            public void onFailure(Call<List<ApiModels.DonorForm>> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                Toast.makeText(lista_voluntarios.this, "Sem conexão com o servidor.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Monta os cartões na tela
    private void mostrarVoluntarios(List<Voluntario> voluntarios) {

        // Cria um cartão para cada voluntário.
        for (Voluntario voluntario : voluntarios) {

            View card = LayoutInflater.from(this).inflate(
                    R.layout.item_voluntario,
                    containerVoluntarios,
                    false
            );

            TextView txtNome = card.findViewById(
                    R.id.txtNomeVoluntario
            );

            TextView txtIdade = card.findViewById(
                    R.id.txtIdadeVoluntario
            );

            TextView txtTipoSanguineo = card.findViewById(
                    R.id.txtTipoSanguineo
            );

            TextView txtSituacao = card.findViewById(
                    R.id.txtSituacaoVoluntario
            );

            TextView txtInscricao = card.findViewById(
                    R.id.txtInscricaoVoluntario
            );

            // Preenche os campos do cartão.
            txtNome.setText("Nome: " + voluntario.nome);
            txtIdade.setText("Idade: " + voluntario.idade);

            txtTipoSanguineo.setText(
                    "Tipo Sanguíneo: " + voluntario.tipoSanguineo
            );

            txtSituacao.setText(voluntario.situacao);

            txtInscricao.setText(
                    "Nº de Inscrição: " + voluntario.inscricao
            );

            // Define a cor conforme a situação.
            switch (voluntario.situacao) {
                case "Aprovado":
                    txtSituacao.setTextColor(Color.GREEN);
                    break;

                case "Pendente":
                    txtSituacao.setTextColor(Color.YELLOW);
                    break;

                case "Inapto":
                    txtSituacao.setTextColor(Color.RED);
                    break;
            }

            // Adiciona o cartão à lista.
            containerVoluntarios.addView(card);
        }
    }

    // Modelo que representa um voluntário.
    private static class Voluntario {

        String nome;
        int idade;
        String tipoSanguineo;
        String situacao;
        String inscricao;

        public Voluntario(
                String nome,
                int idade,
                String tipoSanguineo,
                String situacao,
                String inscricao
        ) {
            this.nome = nome;
            this.idade = idade;
            this.tipoSanguineo = tipoSanguineo;
            this.situacao = situacao;
            this.inscricao = inscricao;
        }
    }
}
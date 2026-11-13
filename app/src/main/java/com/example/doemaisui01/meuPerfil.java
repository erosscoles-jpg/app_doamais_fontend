package com.example.doemaisui01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class meuPerfil extends AppCompatActivity {

    Button btnVoltar, btnFichaPessoal, btnSair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meu_perfil);

        btnVoltar = findViewById(R.id.btn_voltar);
        btnFichaPessoal = findViewById(R.id.btn_ficha);
        btnSair = findViewById(R.id.button9);

        // Recupera o tipo de usuário salvo no login
        SharedPreferences preferencias =
                getSharedPreferences("DoaMais", MODE_PRIVATE);

        boolean isUsuario = preferencias.getBoolean("isUsuario", false);

        // Se for funcionário, esconde o botão Minha ficha
        if (!isUsuario) {
            btnFichaPessoal.setVisibility(View.GONE);
        } else {
            btnFichaPessoal.setVisibility(View.VISIBLE);
        }

        // Botão voltar
        btnVoltar.setOnClickListener(v -> {
            finish();
        });

        // Botão Minha ficha
        btnFichaPessoal.setOnClickListener(v -> {
            Intent intent = new Intent(meuPerfil.this, ficha_usuario.class);
            startActivity(intent);
        });

        // Botão sair do sistema
        btnSair.setOnClickListener(v -> {
            preferencias.edit().clear().apply();

            Intent intent = new Intent(meuPerfil.this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });
    }
}
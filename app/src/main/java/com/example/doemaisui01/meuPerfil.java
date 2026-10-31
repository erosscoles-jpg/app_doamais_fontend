package com.example.doemaisui01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class meuPerfil extends AppCompatActivity {

    Button btnVoltar;

    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meu_perfil);

        btnVoltar = findViewById(R.id.btn_voltar);
        btnVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(meuPerfil.this, dashboradFuncionario.class);
            startActivity(intent);
        });
    }
}
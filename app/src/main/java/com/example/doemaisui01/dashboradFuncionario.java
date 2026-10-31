package com.example.doemaisui01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class dashboradFuncionario extends AppCompatActivity {

     Button btnUsuario, btnEscaner, btnLista, btnCadastrar, btnInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashborad_funcionario);

        btnUsuario = findViewById(R.id.btn_user);
        btnEscaner = findViewById(R.id.btn_escaner);
        btnLista = findViewById(R.id.btn_lista);
        btnCadastrar = findViewById(R.id.btn_cadastrar);
        btnInfo = findViewById(R.id.btn_info);

        btnUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(dashboradFuncionario.this, meuPerfil.class);
            startActivity(intent);
        });
        btnEscaner.setOnClickListener(v -> {
            Intent intent = new Intent(dashboradFuncionario.this, meuPerfil.class);
            startActivity(intent);
        });
        btnLista.setOnClickListener(v -> {
            Intent intent = new Intent(dashboradFuncionario.this, lista_voluntarios.class);
            startActivity(intent);
        });
        btnCadastrar.setOnClickListener(v -> {
            Intent intent = new Intent(dashboradFuncionario.this, cadastroDeVoluntarios.class);
            startActivity(intent);
        });
        btnInfo.setOnClickListener(v -> {
            Intent intent = new Intent(dashboradFuncionario.this, information.class);
            startActivity(intent);
        });

    }
}
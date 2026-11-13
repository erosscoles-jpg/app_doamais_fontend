package com.example.doemaisui01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class configuracoes extends AppCompatActivity {

    ImageButton btnHome,btnPerfil,btnConfig;
    Button btnNotificacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_configuracoes);

        btnHome = findViewById(R.id.btn_home);
        btnConfig = findViewById(R.id.btn_config);
        btnPerfil = findViewById(R.id.btn_perfil);
        btnNotificacoes = findViewById(R.id.btn_notificacoes);

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(configuracoes.this, dashboardUsuario.class);
            startActivity(intent);
        });

        btnConfig.setOnClickListener(v -> {
            Intent intent = new Intent(configuracoes.this, configuracoes.class);
            startActivity(intent);
        });

        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(configuracoes.this, meuPerfil.class);
            startActivity(intent);
        });

        btnNotificacoes.setOnClickListener(v -> {
            Intent intent = new Intent(configuracoes.this, Notificacoes.class);
            startActivity(intent);
        });

    }
}
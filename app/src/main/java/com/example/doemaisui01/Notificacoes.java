package com.example.doemaisui01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class Notificacoes extends AppCompatActivity {

    Button btnVoltar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_notificacoes);

        btnVoltar = findViewById(R.id.btn_camapnhas);

        btnVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(Notificacoes.this, configuracoes.class);
            startActivity(intent);
        });

    }
}
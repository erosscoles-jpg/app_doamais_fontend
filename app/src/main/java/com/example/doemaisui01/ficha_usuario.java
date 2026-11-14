package com.example.doemaisui01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class ficha_usuario extends AppCompatActivity {

    Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_ficha_usuario);

        btnVoltar = findViewById(R.id.btn_informativo);

        btnVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(ficha_usuario.this, meuPerfil.class);
            startActivity(intent);
        });

    }
}
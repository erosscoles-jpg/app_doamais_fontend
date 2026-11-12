package com.example.doemaisui01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button btnEntrar;
    TextView textEsqueciSenha, textCriarConta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnEntrar = findViewById(R.id.btn_entrar);
        textCriarConta = findViewById(R.id.text_criar);
        textEsqueciSenha = findViewById(R.id.text_esqueci);

        btnEntrar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, dashboardUsuario.class);
            startActivity(intent);
        });

        textEsqueciSenha.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NovaSenha.class);
            startActivity(intent);
        });

        textCriarConta.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, criar_conta2.class);
            startActivity(intent);
        });

    }
}
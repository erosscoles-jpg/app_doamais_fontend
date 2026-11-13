package com.example.doemaisui01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    Button btnEntrar;
    TextView textEsqueciSenha, textCriarConta;

    TextInputEditText inputEmail, inputSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnEntrar = findViewById(R.id.btn_entrar);
        textCriarConta = findViewById(R.id.text_criar);
        textEsqueciSenha = findViewById(R.id.text_esqueci);
        inputEmail = findViewById(R.id.input_Email);
        inputSenha = findViewById(R.id.input_Senha);

        // Armazenamento das preferências do usuário
        SharedPreferences preferencias =
                getSharedPreferences("DoaMais", MODE_PRIVATE);

        btnEntrar.setOnClickListener(v -> {

            // Recuperar dados dos inputs
            String email = inputEmail.getText().toString().trim();
            String senha = inputSenha.getText().toString().trim();

            // Dados de acesso
            String admin = "adimin";
            String usuario = "eros";
            String senhaPadrao = "123";

            // Validar os campos
            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(MainActivity.this,
                        "Preencha todos os campos!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // LOGIN DO FUNCIONÁRIO
            if (email.equals(admin) && senha.equals(senhaPadrao)) {

                // false = funcionário
                preferencias.edit()
                        .putBoolean("isUsuario", false)
                        .apply();

                Intent intent = new Intent(
                        MainActivity.this,
                        dashboradFuncionario.class
                );

                startActivity(intent);
                finish();
            }

            // LOGIN DO USUÁRIO
            else if (email.equals(usuario) && senha.equals(senhaPadrao)) {

                // true = usuário
                preferencias.edit()
                        .putBoolean("isUsuario", true)
                        .apply();

                Intent intent = new Intent(
                        MainActivity.this,
                        dashboardUsuario.class
                );

                startActivity(intent);
                finish();
            }

            // DADOS INCORRETOS
            else {
                Toast.makeText(MainActivity.this,
                        "Usuário ou senha incorretos!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // ESQUECI A SENHA
        textEsqueciSenha.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NovaSenha.class);
            startActivity(intent);
        });

        // CRIAR CONTA
        textCriarConta.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, criar_conta2.class);
            startActivity(intent);
        });
    }
}
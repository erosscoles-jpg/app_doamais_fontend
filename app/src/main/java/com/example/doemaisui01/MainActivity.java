package com.example.doemaisui01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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



        btnEntrar.setOnClickListener(v -> {

            // Recuperar Dados dos inputs
            String email = inputEmail.getText().toString().trim();
            String senha = inputSenha.getText().toString().trim();

            // Dados de Acesso
            String admin = "adimin";
            String usuario = "eros";
            String senhaPadrao = "123";

            //Validado os campos
            if (email.isEmpty() || senha.isEmpty()){
                Toast.makeText(MainActivity.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            // LOGIN DO ADMINISTRADOR
            if (email.equals(admin) && senha.equals(senhaPadrao)) {
                Intent intent = new Intent(MainActivity.this,dashboradFuncionario.class);
                startActivity(intent);
            }
            // LOGIN DO USUÁRIO
            else if (email.equals(usuario) && senha.equals(senhaPadrao)) {
                Intent intent = new Intent(MainActivity.this, dashboardUsuario.class);
                startActivity(intent);
            }
            // Dados incorretos
            else{
                Toast.makeText(MainActivity.this, "Usuario ou senha incorretos!", Toast.LENGTH_SHORT).show();
            }
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
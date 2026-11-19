package com.example.doemaisui01;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doemaisui01.api.ApiClient;
import com.example.doemaisui01.api.ApiModels;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class criar_conta2 extends AppCompatActivity {

    TextInputEditText editNome, editEmail, editSenha;
    Button btnCriar, btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_criar_conta2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editNome = findViewById(R.id.editTextNome);
        editEmail = findViewById(R.id.editTextEmail);
        editSenha = findViewById(R.id.editTextSenha);
        btnCriar = findViewById(R.id.button2);
        btnVoltar = findViewById(R.id.btn_voltar3);

        btnVoltar.setOnClickListener(v -> finish());
        btnCriar.setOnClickListener(v -> criarConta());
    }

    private void criarConta() {
        String nome = editNome.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (senha.length() < 6) {
            Toast.makeText(this, "A senha precisa ter no mínimo 6 caracteres.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        btnCriar.setEnabled(false);

        ApiClient.get().createDonor(new ApiModels.DonorCreate(nome, email, senha))
                .enqueue(new Callback<ApiModels.Donor>() {
                    @Override
                    public void onResponse(Call<ApiModels.Donor> call, Response<ApiModels.Donor> resp) {
                        btnCriar.setEnabled(true);

                        if (resp.isSuccessful() && resp.body() != null) {
                            SharedPreferences prefs = getSharedPreferences("DoaMais", MODE_PRIVATE);
                            prefs.edit().putLong("donorId", resp.body().idUser).apply();

                            Toast.makeText(criar_conta2.this, "Conta criada com sucesso!",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else if (resp.code() == 400) {
                            Toast.makeText(criar_conta2.this,
                                    "Dados inválidos. Confira o e-mail e a senha.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(criar_conta2.this,
                                    "Não foi possível criar a conta. O e-mail pode já estar em uso.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiModels.Donor> call, Throwable t) {
                        btnCriar.setEnabled(true);
                        Toast.makeText(criar_conta2.this, "Sem conexão com o servidor.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
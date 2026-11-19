package com.example.doemaisui01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doemaisui01.api.ApiClient;
import com.example.doemaisui01.api.ApiModels;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class meuPerfil extends AppCompatActivity {

    Button btnVoltar, btnFichaPessoal, btnSair;
    TextView tvBadgePapel, tvNomeDestaque, tvNomeCompleto, tvEmail;

    private boolean isUsuario;
    private long userIdLogado;
    private String emailLogadoPrefs;
    private String nomeAtual = "";
    private String emailAtual = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meu_perfil);

        btnVoltar = findViewById(R.id.btn_voltar);
        btnFichaPessoal = findViewById(R.id.btn_ficha);
        btnSair = findViewById(R.id.button9);

        tvBadgePapel = findViewById(R.id.textView37);
        tvNomeDestaque = findViewById(R.id.textView40);
        tvNomeCompleto = findViewById(R.id.tvNomeCompletoPerfil);
        tvEmail = findViewById(R.id.tvEmailPerfil);

        // Fallback caso você ainda não tenha adicionado os IDs tvNomeCompletoPerfil / tvEmailPerfil no XML
        LinearLayout layoutInformacoes = findViewById(R.id.layoutInformacoes);
        if (layoutInformacoes != null) {
            if (tvNomeCompleto == null && layoutInformacoes.getChildCount() >= 2
                    && layoutInformacoes.getChildAt(1) instanceof LinearLayout) {
                LinearLayout boxNome = (LinearLayout) layoutInformacoes.getChildAt(1);
                for (int i = 0; i < boxNome.getChildCount(); i++) {
                    if (boxNome.getChildAt(i) instanceof TextView) {
                        tvNomeCompleto = (TextView) boxNome.getChildAt(i);
                        break;
                    }
                }
            }
            if (tvEmail == null && layoutInformacoes.getChildCount() >= 4
                    && layoutInformacoes.getChildAt(3) instanceof LinearLayout) {
                LinearLayout boxEmail = (LinearLayout) layoutInformacoes.getChildAt(3);
                for (int i = 0; i < boxEmail.getChildCount(); i++) {
                    if (boxEmail.getChildAt(i) instanceof TextView) {
                        tvEmail = (TextView) boxEmail.getChildAt(i);
                        break;
                    }
                }
            }
        }

        SharedPreferences preferencias = getSharedPreferences("DoaMais", MODE_PRIVATE);
        isUsuario = preferencias.getBoolean("isUsuario", false);
        userIdLogado = preferencias.getLong("userId", -1L);
        emailLogadoPrefs = preferencias.getString("email", "");

        if (!isUsuario) {
            btnFichaPessoal.setVisibility(View.GONE);
            if (tvBadgePapel != null) {
                tvBadgePapel.setText("Funcionário");
            }
        } else {
            btnFichaPessoal.setVisibility(View.VISIBLE);
            if (tvBadgePapel != null) {
                tvBadgePapel.setText("Doador");
            }
        }

        btnVoltar.setOnClickListener(v -> {
            Intent intent;
            if (isUsuario) {
                intent = new Intent(meuPerfil.this, dashboardUsuario.class);
            } else {
                intent = new Intent(meuPerfil.this, dashboradFuncionario.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        btnFichaPessoal.setOnClickListener(v -> {
            Intent intent = new Intent(meuPerfil.this, ficha_usuario.class);
            startActivity(intent);
        });

        btnSair.setOnClickListener(v -> {
            preferencias.edit().clear().apply();
            Intent intent = new Intent(meuPerfil.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Permite editar nome ou e-mail ao tocar no campo (para contas de doador/usuário)
        View.OnClickListener abrirEdicao = v -> {
            if (!isUsuario) {
                Toast.makeText(meuPerfil.this,
                        "Edição de funcionário não disponível na API atual.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (userIdLogado <= 0) {
                Toast.makeText(meuPerfil.this,
                        "ID da conta não encontrado.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            mostrarDialogoEditarPerfil();
        };

        if (tvNomeCompleto != null) {
            tvNomeCompleto.setOnClickListener(abrirEdicao);
        }
        if (tvEmail != null) {
            tvEmail.setOnClickListener(abrirEdicao);
        }

        carregarDadosContaLogada();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDadosContaLogada();
    }

    private void carregarDadosContaLogada() {
        if (userIdLogado <= 0 && (emailLogadoPrefs == null || emailLogadoPrefs.isEmpty())) {
            return;
        }

        if (isUsuario && userIdLogado > 0) {
            ApiClient.get().getDonor(userIdLogado).enqueue(new Callback<ApiModels.Donor>() {
                @Override
                public void onResponse(Call<ApiModels.Donor> call, Response<ApiModels.Donor> response) {
                    if (isFinishing() || isDestroyed()) return;
                    if (!response.isSuccessful() || response.body() == null) {
                        Toast.makeText(meuPerfil.this,
                                "Erro ao carregar perfil (HTTP " + response.code() + ")",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ApiModels.Donor donor = response.body();
                    preencherDadosNaTela(donor.userName, donor.email, "Doador");
                }

                @Override
                public void onFailure(Call<ApiModels.Donor> call, Throwable t) {
                    if (isFinishing() || isDestroyed()) return;
                    Toast.makeText(meuPerfil.this,
                            "Falha ao carregar dados do perfil.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ApiClient.get().listEmployees().enqueue(new Callback<List<ApiModels.Employee>>() {
                @Override
                public void onResponse(Call<List<ApiModels.Employee>> call,
                                       Response<List<ApiModels.Employee>> response) {
                    if (isFinishing() || isDestroyed()) return;
                    if (!response.isSuccessful() || response.body() == null) return;

                    for (ApiModels.Employee emp : response.body()) {
                        boolean bateId = (emp.idUser != null && emp.idUser == userIdLogado);
                        boolean bateEmail = (emailLogadoPrefs != null && !emailLogadoPrefs.isEmpty()
                                && emailLogadoPrefs.equalsIgnoreCase(emp.email));
                        if (bateId || bateEmail) {
                            String papel = (emp.function != null && !emp.function.isEmpty())
                                    ? "Funcionário (" + emp.function + ")"
                                    : "Funcionário";
                            preencherDadosNaTela(emp.userName, emp.email, papel);
                            break;
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<ApiModels.Employee>> call, Throwable t) {
                    if (isFinishing() || isDestroyed()) return;
                }
            });
        }
    }

    private void preencherDadosNaTela(String nome, String email, String papel) {
        if (nome != null) {
            nomeAtual = nome;
            if (tvNomeDestaque != null) {
                tvNomeDestaque.setText(nome);
            }
            if (tvNomeCompleto != null) {
                tvNomeCompleto.setText(nome);
            }
        }
        if (email != null) {
            emailAtual = email;
            if (tvEmail != null) {
                tvEmail.setText(email);
            }
        }
        if (papel != null && tvBadgePapel != null) {
            tvBadgePapel.setText(papel);
        }
    }

    private void mostrarDialogoEditarPerfil() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(pad, pad, pad, pad);

        final EditText inputNome = new EditText(this);
        inputNome.setHint("Nome Completo");
        inputNome.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        inputNome.setText(nomeAtual);
        container.addView(inputNome);

        final EditText inputEmail = new EditText(this);
        inputEmail.setHint("Email");
        inputEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputEmail.setText(emailAtual);
        container.addView(inputEmail);

        new AlertDialog.Builder(this)
                .setTitle("Atualizar Perfil")
                .setView(container)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String novoNome = inputNome.getText().toString().trim();
                    String novoEmail = inputEmail.getText().toString().trim();

                    if (novoNome.isEmpty() || novoEmail.isEmpty()) {
                        Toast.makeText(meuPerfil.this,
                                "Preencha nome e e-mail.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ApiModels.DonorUpdate body = new ApiModels.DonorUpdate(novoNome, novoEmail);
                    ApiClient.get().updateDonor(userIdLogado, body).enqueue(new Callback<ApiModels.Donor>() {
                        @Override
                        public void onResponse(Call<ApiModels.Donor> call,
                                               Response<ApiModels.Donor> response) {
                            if (isFinishing() || isDestroyed()) return;
                            if (response.isSuccessful()) {
                                SharedPreferences prefs = getSharedPreferences("DoaMais", MODE_PRIVATE);
                                prefs.edit().putString("email", novoEmail).apply();
                                emailLogadoPrefs = novoEmail;
                                preencherDadosNaTela(novoNome, novoEmail, "Doador");
                                Toast.makeText(meuPerfil.this,
                                        "Perfil atualizado com sucesso!",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(meuPerfil.this,
                                        "Erro ao atualizar (HTTP " + response.code() + ")",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiModels.Donor> call, Throwable t) {
                            if (isFinishing() || isDestroyed()) return;
                            Toast.makeText(meuPerfil.this,
                                    "Falha na conexão ao atualizar.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
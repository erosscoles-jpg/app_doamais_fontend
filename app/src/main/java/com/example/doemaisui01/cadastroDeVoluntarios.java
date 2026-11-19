package com.example.doemaisui01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doemaisui01.api.ApiClient;
import com.example.doemaisui01.api.ApiModels;
import com.example.doemaisui01.api.ApiService;
import com.example.doemaisui01.api.Formatos;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class cadastroDeVoluntarios extends AppCompatActivity {

    EditText nome, idade, peso, altura, observacoes;
    // Se adicionar campos de e-mail e senha no XML, recupere-os aqui também
    EditText emailEt, senhaEt;
    Spinner tipoSanguineo, statusVoluntario;
    Button btnPreencherQuestionario, btnVoltar;

    private ApiService apiService;

    // Controle de modo edição
    private boolean modoEdicao = false;
    private long donorIdEmEdicao = -1L;
    private String birthDateExistente = null;

    // ID do funcionário logado (substitua pelo ID real salvo em SharedPreferences/Intent)
    private static final long FUNCIONARIO_LOGADO_ID = 1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_de_voluntarios);

        apiService = ApiClient.get();

        nome = findViewById(R.id.editTextText2);
        idade = findViewById(R.id.editTextNumber);
        peso = findViewById(R.id.editTextNumber2);
        altura = findViewById(R.id.editTextNumber3);
        observacoes = findViewById(R.id.etObservacoes);
        btnVoltar = findViewById(R.id.button11);
        btnPreencherQuestionario = findViewById(R.id.button12);

        btnVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(cadastroDeVoluntarios.this, dashboradFuncionario.class);
            startActivity(intent);
        });

        tipoSanguineo = findViewById(R.id.spTipoSanguineo);
        String[] tipos = {
                "Selecione o tipo sanguíneo",
                "A+", "A-", "B+", "B-",
                "AB+", "AB-", "O+", "O-"
        };
        ArrayAdapter<String> adapterSangue = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tipos
        );
        adapterSangue.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoSanguineo.setAdapter(adapterSangue);

        statusVoluntario = findViewById(R.id.spStatusVoluntario);
        String[] statusOpcoes = {
                "Selecione o status",
                "Apto",
                "Inapto",
                "Pendente",
                "Em análise"
        };
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statusOpcoes
        );
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusVoluntario.setAdapter(adapterStatus);

        verificarModoEdicao();

        btnPreencherQuestionario.setOnClickListener(v -> {
            if (modoEdicao) {
                atualizarVoluntario();
            } else {
                cadastrarVoluntario();
            }
        });
    }

    private void verificarModoEdicao() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("donorId")) {
            long idRecebido = intent.getLongExtra("donorId", -1L);
            if (idRecebido > 0) {
                modoEdicao = true;
                donorIdEmEdicao = idRecebido;
                btnPreencherQuestionario.setText("Atualizar");

                // Preenche dados básicos vindos do card imediatamente
                String nomeExtra = intent.getStringExtra("nome");
                int idadeExtra = intent.getIntExtra("idade", 0);
                String sangueExtra = intent.getStringExtra("tipoSanguineo");
                String situacaoExtra = intent.getStringExtra("situacao");

                if (nomeExtra != null) nome.setText(nomeExtra);
                if (idadeExtra > 0) idade.setText(String.valueOf(idadeExtra));
                selecionarSpinnerPorTexto(tipoSanguineo, sangueExtra);
                selecionarSpinnerPorTexto(statusVoluntario, situacaoExtra);

                // Busca os dados completos da ficha (peso, altura, observação, birthDate exata)
                carregarFichaExistente(donorIdEmEdicao);
            }
        }
    }

    private void carregarFichaExistente(long donorId) {
        apiService.getForm(donorId).enqueue(new Callback<ApiModels.DonorForm>() {
            @Override
            public void onResponse(Call<ApiModels.DonorForm> call, Response<ApiModels.DonorForm> response) {
                if (isFinishing() || isDestroyed()) return;
                if (!response.isSuccessful() || response.body() == null) return;

                ApiModels.DonorForm form = response.body();
                if (form.donorName != null && !form.donorName.isEmpty()) {
                    nome.setText(form.donorName);
                }
                if (form.birthDate != null && !form.birthDate.isEmpty()) {
                    birthDateExistente = form.birthDate;
                    int idadeCalculada = Formatos.idade(form.birthDate);
                    if (idadeCalculada > 0) {
                        idade.setText(String.valueOf(idadeCalculada));
                    }
                }
                if (form.weight != null) {
                    peso.setText(String.valueOf(form.weight));
                }
                if (form.height != null) {
                    altura.setText(String.valueOf(form.height));
                }
                if (form.observation != null) {
                    observacoes.setText(form.observation);
                }
                if (form.bloodType != null) {
                    selecionarSpinnerPorTexto(tipoSanguineo, Formatos.sangue(form.bloodType));
                }
                if (form.status != null) {
                    selecionarSpinnerPorTexto(statusVoluntario, mapApiStatusToSpinnerLabel(form.status));
                }
            }

            @Override
            public void onFailure(Call<ApiModels.DonorForm> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                Toast.makeText(cadastroDeVoluntarios.this,
                        "Não foi possível carregar todos os detalhes da ficha.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cadastrarVoluntario() {
        DadosFormulario dados = validarEExtrairDados();
        if (dados == null) return;

        String baseEmail = dados.nome.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", ".")
                .replaceAll("^\\.|\\.$", "");
        if (baseEmail.isEmpty()) {
            baseEmail = "voluntario";
        }

        final String email = emailEt != null && !emailEt.getText().toString().trim().isEmpty()
                ? emailEt.getText().toString().trim()
                : baseEmail + "." + System.currentTimeMillis() + "@exemplo.com";
        final String senha = senhaEt != null && !senhaEt.getText().toString().trim().isEmpty()
                ? senhaEt.getText().toString().trim()
                : "SenhaTemporaria123!";

        btnPreencherQuestionario.setEnabled(false);

        ApiModels.DonorCreate donorBody = new ApiModels.DonorCreate(dados.nome, email, senha);
        apiService.createDonor(donorBody).enqueue(new Callback<ApiModels.Donor>() {
            @Override
            public void onResponse(Call<ApiModels.Donor> call, Response<ApiModels.Donor> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().idUser == null) {
                    btnPreencherQuestionario.setEnabled(true);
                    String msg = response.code() == 409
                            ? "Conflito (HTTP 409): já existe um voluntário com esse e-mail."
                            : "Erro ao criar voluntário (HTTP " + response.code() + ")";
                    Toast.makeText(cadastroDeVoluntarios.this, msg, Toast.LENGTH_LONG).show();
                    return;
                }

                long donorId = response.body().idUser;
                salvarFichaEavaliacao(donorId, dados, "Cadastro e ficha salvos com sucesso!");
            }

            @Override
            public void onFailure(Call<ApiModels.Donor> call, Throwable t) {
                btnPreencherQuestionario.setEnabled(true);
                Toast.makeText(cadastroDeVoluntarios.this,
                        "Falha na conexão: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void atualizarVoluntario() {
        DadosFormulario dados = validarEExtrairDados();
        if (dados == null || donorIdEmEdicao <= 0) return;

        btnPreencherQuestionario.setEnabled(false);

        // Atualiza dados básicos do doador (buscando o e-mail atual para montar DonorUpdate)
        apiService.getDonor(donorIdEmEdicao).enqueue(new Callback<ApiModels.Donor>() {
            @Override
            public void onResponse(Call<ApiModels.Donor> call, Response<ApiModels.Donor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String emailAtual = response.body().email != null ? response.body().email : "";
                    ApiModels.DonorUpdate updateBody = new ApiModels.DonorUpdate(dados.nome, emailAtual);
                    apiService.updateDonor(donorIdEmEdicao, updateBody).enqueue(new Callback<ApiModels.Donor>() {
                        @Override
                        public void onResponse(Call<ApiModels.Donor> c, Response<ApiModels.Donor> r) {
                            salvarFichaEavaliacao(donorIdEmEdicao, dados, "Dados do voluntário atualizados!");
                        }

                        @Override
                        public void onFailure(Call<ApiModels.Donor> c, Throwable t) {
                            salvarFichaEavaliacao(donorIdEmEdicao, dados, "Ficha atualizada!");
                        }
                    });
                } else {
                    salvarFichaEavaliacao(donorIdEmEdicao, dados, "Ficha atualizada!");
                }
            }

            @Override
            public void onFailure(Call<ApiModels.Donor> call, Throwable t) {
                salvarFichaEavaliacao(donorIdEmEdicao, dados, "Ficha atualizada!");
            }
        });
    }

    private void salvarFichaEavaliacao(long donorId, DadosFormulario dados, String mensagemSucesso) {
        String birthDateParaEnvio = (modoEdicao && birthDateExistente != null && !birthDateExistente.isEmpty())
                ? birthDateExistente
                : estimateBirthDateFromAge(dados.idade);

        ApiModels.DonorFormRequest formRequest = new ApiModels.DonorFormRequest(
                birthDateParaEnvio,
                dados.bloodTypeApi,
                dados.alturaMetros,
                dados.pesoKg
        );

        apiService.saveForm(donorId, formRequest).enqueue(new Callback<ApiModels.DonorForm>() {
            @Override
            public void onResponse(Call<ApiModels.DonorForm> call, Response<ApiModels.DonorForm> response) {
                if (!response.isSuccessful()) {
                    btnPreencherQuestionario.setEnabled(true);
                    Toast.makeText(cadastroDeVoluntarios.this,
                            "Erro ao salvar ficha (HTTP " + response.code() + ")",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                String obsLimitada = dados.observacao.length() > 500
                        ? dados.observacao.substring(0, 500)
                        : dados.observacao;

                ApiModels.DonorFormEvaluation evalRequest =
                        new ApiModels.DonorFormEvaluation(FUNCIONARIO_LOGADO_ID, dados.statusApi, obsLimitada);

                apiService.evaluateForm(donorId, evalRequest).enqueue(new Callback<ApiModels.DonorForm>() {
                    @Override
                    public void onResponse(Call<ApiModels.DonorForm> call, Response<ApiModels.DonorForm> evalResp) {
                        btnPreencherQuestionario.setEnabled(true);
                        if (evalResp.isSuccessful()) {
                            Toast.makeText(cadastroDeVoluntarios.this,
                                    mensagemSucesso,
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(cadastroDeVoluntarios.this,
                                    "Ficha salva, mas erro na avaliação (HTTP " + evalResp.code() + ")",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiModels.DonorForm> call, Throwable t) {
                        btnPreencherQuestionario.setEnabled(true);
                        Toast.makeText(cadastroDeVoluntarios.this,
                                "Erro ao enviar avaliação: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiModels.DonorForm> call, Throwable t) {
                btnPreencherQuestionario.setEnabled(true);
                Toast.makeText(cadastroDeVoluntarios.this,
                        "Erro ao salvar ficha: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private DadosFormulario validarEExtrairDados() {
        String nomeVoluntario = nome.getText().toString().trim();
        String idadeStr = idade.getText().toString().trim();
        String pesoStr = peso.getText().toString().trim();
        String alturaStr = altura.getText().toString().trim();
        String obsVoluntario = observacoes.getText().toString().trim();

        if (nomeVoluntario.isEmpty() || idadeStr.isEmpty() || pesoStr.isEmpty() || alturaStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os dados pessoais.", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (tipoSanguineo.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione o tipo sanguíneo.", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (statusVoluntario.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione o status do voluntário.", Toast.LENGTH_SHORT).show();
            return null;
        }

        final int idadeAnos;
        final double pesoKg;
        final double alturaMetros;
        try {
            idadeAnos = Integer.parseInt(idadeStr);
            pesoKg = Double.parseDouble(pesoStr.replace(",", "."));

            double alturaCalculada = Double.parseDouble(alturaStr.replace(",", "."));
            if (alturaCalculada > 3.0) {
                alturaCalculada = alturaCalculada / 100.0;
            }
            alturaMetros = alturaCalculada;
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Verifique os valores numéricos de idade, peso e altura.", Toast.LENGTH_SHORT).show();
            return null;
        }

        String bloodTypeApi = mapBloodTypeToApi(tipoSanguineo.getSelectedItem().toString());
        String statusApi = mapStatusToApi(statusVoluntario.getSelectedItem().toString());

        return new DadosFormulario(
                nomeVoluntario,
                idadeAnos,
                pesoKg,
                alturaMetros,
                obsVoluntario,
                bloodTypeApi,
                statusApi
        );
    }

    private void selecionarSpinnerPorTexto(Spinner spinner, String valor) {
        if (spinner == null || valor == null) return;
        for (int i = 0; i < spinner.getCount(); i++) {
            Object item = spinner.getItemAtPosition(i);
            if (item != null && item.toString().equalsIgnoreCase(valor)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private String mapApiStatusToSpinnerLabel(String apiStatus) {
        if ("ELIGIBLE".equals(apiStatus)) return "Apto";
        if ("INELIGIBLE".equals(apiStatus)) return "Inapto";
        if ("IN_PROGRESS".equals(apiStatus)) return "Em análise";
        return "Pendente";
    }

    private String mapBloodTypeToApi(String label) {
        switch (label) {
            case "A+": return "A_POSITIVE";
            case "A-": return "A_NEGATIVE";
            case "B+": return "B_POSITIVE";
            case "B-": return "B_NEGATIVE";
            case "AB+": return "AB_POSITIVE";
            case "AB-": return "AB_NEGATIVE";
            case "O+": return "O_POSITIVE";
            case "O-": return "O_NEGATIVE";
            default: return "O_POSITIVE";
        }
    }

    private String mapStatusToApi(String label) {
        switch (label) {
            case "Apto": return "ELIGIBLE";
            case "Inapto": return "INELIGIBLE";
            case "Em análise": return "IN_PROGRESS";
            case "Pendente":
            default: return "PENDING";
        }
    }

    private String estimateBirthDateFromAge(int age) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -age);
        return String.format(Locale.US, "%04d-%02d-%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH));
    }

    private static class DadosFormulario {
        final String nome;
        final int idade;
        final double pesoKg;
        final double alturaMetros;
        final String observacao;
        final String bloodTypeApi;
        final String statusApi;

        DadosFormulario(String nome, int idade, double pesoKg, double alturaMetros,
                        String observacao, String bloodTypeApi, String statusApi) {
            this.nome = nome;
            this.idade = idade;
            this.pesoKg = pesoKg;
            this.alturaMetros = alturaMetros;
            this.observacao = observacao;
            this.bloodTypeApi = bloodTypeApi;
            this.statusApi = statusApi;
        }
    }
}
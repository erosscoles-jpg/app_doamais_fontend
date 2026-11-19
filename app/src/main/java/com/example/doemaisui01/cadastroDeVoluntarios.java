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

        btnPreencherQuestionario = findViewById(R.id.button12);
        btnPreencherQuestionario.setOnClickListener(v -> cadastrarVoluntario());
    }

    private void cadastrarVoluntario() {
        String nomeVoluntario = nome.getText().toString().trim();
        String idadeStr = idade.getText().toString().trim();
        String pesoStr = peso.getText().toString().trim();
        String alturaStr = altura.getText().toString().trim();
        String obsVoluntario = observacoes.getText().toString().trim();

        if (nomeVoluntario.isEmpty() || idadeStr.isEmpty() || pesoStr.isEmpty() || alturaStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os dados pessoais.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tipoSanguineo.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione o tipo sanguíneo.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (statusVoluntario.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione o status do voluntário.", Toast.LENGTH_SHORT).show();
            return;
        }

        final int idadeAnos;
        final double pesoKg;
        final double alturaMetros;
        try {
            idadeAnos = Integer.parseInt(idadeStr);
            pesoKg = Double.parseDouble(pesoStr.replace(",", "."));

            double alturaCalculada = Double.parseDouble(alturaStr.replace(",", "."));
            // Se o usuário digitar altura em cm (ex.: 175), converte para metros (1.75)
            if (alturaCalculada > 3.0) {
                alturaCalculada = alturaCalculada / 100.0;
            }
            alturaMetros = alturaCalculada;
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Verifique os valores numéricos de idade, peso e altura.", Toast.LENGTH_SHORT).show();
            return;
        }

        final String bloodTypeApi = mapBloodTypeToApi(tipoSanguineo.getSelectedItem().toString());
        final String statusApi = mapStatusToApi(statusVoluntario.getSelectedItem().toString());
        final String birthDateIso = estimateBirthDateFromAge(idadeAnos);
        final String obsFinal = obsVoluntario;

        String baseEmail = nomeVoluntario.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", ".")
                .replaceAll("^\\.|\\.$", "");
        if (baseEmail.isEmpty()) baseEmail = "voluntario";

        final String email = emailEt != null && !emailEt.getText().toString().trim().isEmpty()
                ? emailEt.getText().toString().trim()
                : baseEmail + "." + System.currentTimeMillis() + "@exemplo.com";

        final String senha = senhaEt != null && !senhaEt.getText().toString().trim().isEmpty()
                ? senhaEt.getText().toString().trim()
                : "SenhaTemporaria123!";

        btnPreencherQuestionario.setEnabled(false);

        // 1) Cria o doador no back-end
        ApiModels.DonorCreate donorBody = new ApiModels.DonorCreate(nomeVoluntario, email, senha);
        apiService.createDonor(donorBody).enqueue(new Callback<ApiModels.Donor>() {
            @Override
            public void onResponse(Call<ApiModels.Donor> call, Response<ApiModels.Donor> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().idUser == null) {
                    btnPreencherQuestionario.setEnabled(true);
                    Toast.makeText(cadastroDeVoluntarios.this,
                            "Erro ao criar voluntário (HTTP " + response.code() + ")",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                long donorId = response.body().idUser;
                salvarFichaEavaliacao(donorId, birthDateIso, bloodTypeApi, alturaMetros, pesoKg, statusApi, obsFinal);
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

    private void salvarFichaEavaliacao(long donorId,
                                       String birthDate,
                                       String bloodType,
                                       double height,
                                       double weight,
                                       String statusApi,
                                       String obs) {

        // 2) Salva a ficha do doador
        ApiModels.DonorFormRequest formRequest = new ApiModels.DonorFormRequest(birthDate, bloodType, height, weight);
        apiService.saveForm(donorId, formRequest).enqueue(new Callback<ApiModels.DonorForm>() {
            @Override
            public void onResponse(Call<ApiModels.DonorForm> call, Response<ApiModels.DonorForm> response) {
                if (!response.isSuccessful()) {
                    btnPreencherQuestionario.setEnabled(true);
                    Toast.makeText(cadastroDeVoluntarios.this,
                            "Voluntário criado, mas erro ao salvar ficha (HTTP " + response.code() + ")",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // 3) Registra avaliação (status + observação)
                String obsLimitada = obs.length() > 500 ? obs.substring(0, 500) : obs;
                ApiModels.DonorFormEvaluation evalRequest =
                        new ApiModels.DonorFormEvaluation(FUNCIONARIO_LOGADO_ID, statusApi, obsLimitada);

                apiService.evaluateForm(donorId, evalRequest).enqueue(new Callback<ApiModels.DonorForm>() {
                    @Override
                    public void onResponse(Call<ApiModels.DonorForm> call, Response<ApiModels.DonorForm> evalResp) {
                        btnPreencherQuestionario.setEnabled(true);
                        if (evalResp.isSuccessful()) {
                            Toast.makeText(cadastroDeVoluntarios.this,
                                    "Cadastro e ficha salvos com sucesso!",
                                    Toast.LENGTH_LONG).show();
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
}
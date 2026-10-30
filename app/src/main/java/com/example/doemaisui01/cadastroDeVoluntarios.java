package com.example.doemaisui01;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class cadastroDeVoluntarios extends AppCompatActivity {

    EditText nome, idade, peso, altura;
    Spinner tipoSanguineo;

    RadioGroup rgDoacaoAnterior;
    RadioGroup rgReacaoDoacao;
    RadioGroup rgResponsavel;

    LinearLayout areaUltimaDoacao;
    Button btnDataUltimaDoacao;
    Button btnPreencherQuestionario;

    String dataUltimaDoacao = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_de_voluntarios);

        // Campos pessoais
        nome = findViewById(R.id.editTextText2);
        idade = findViewById(R.id.editTextNumber);
        peso = findViewById(R.id.editTextNumber2);
        altura = findViewById(R.id.editTextNumber3);

        // Tipo sanguíneo
        tipoSanguineo = findViewById(R.id.spTipoSanguineo);

        String[] tipos = {
                "Selecione o tipo sanguíneo",
                "A+", "A-", "B+", "B-",
                "AB+", "AB-", "O+", "O-"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tipos
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        tipoSanguineo.setAdapter(adapter);

        // Grupos de perguntas
        rgDoacaoAnterior = findViewById(R.id.rgDoacaoAnterior);
        rgReacaoDoacao = findViewById(R.id.rgReacaoDoacao);
        rgResponsavel = findViewById(R.id.rgResponsavel);

        // Data da última doação
        areaUltimaDoacao = findViewById(R.id.areaUltimaDoacao);
        btnDataUltimaDoacao = findViewById(R.id.btnDataUltimaDoacao);

        // Botão de cadastro
        btnPreencherQuestionario = findViewById(R.id.button12);

        configurarData();
        configurarDoacaoAnterior();

        btnPreencherQuestionario.setOnClickListener(v -> cadastrarVoluntario());
    }

    private void configurarDoacaoAnterior() {

        rgDoacaoAnterior.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.rbDoacaoSim) {
                areaUltimaDoacao.setVisibility(View.VISIBLE);

            } else {
                areaUltimaDoacao.setVisibility(View.GONE);
                dataUltimaDoacao = "";
                btnDataUltimaDoacao.setText("Selecionar data");
            }
        });
    }

    private void configurarData() {

        btnDataUltimaDoacao.setOnClickListener(v -> {

            Calendar calendario = Calendar.getInstance();

            int ano = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog seletorData = new DatePickerDialog(
                    this,
                    (view, anoSelecionado, mesSelecionado, diaSelecionado) -> {

                        dataUltimaDoacao = String.format(
                                Locale.getDefault(),
                                "%02d/%02d/%04d",
                                diaSelecionado,
                                mesSelecionado + 1,
                                anoSelecionado
                        );

                        btnDataUltimaDoacao.setText(dataUltimaDoacao);
                    },
                    ano, mes, dia
            );

            seletorData.getDatePicker().setMaxDate(
                    System.currentTimeMillis()
            );

            seletorData.show();
        });
    }

    private void cadastrarVoluntario() {

        String nomeVoluntario = nome.getText().toString().trim();
        String idadeVoluntario = idade.getText().toString().trim();
        String pesoVoluntario = peso.getText().toString().trim();
        String alturaVoluntario = altura.getText().toString().trim();

        String sangue = tipoSanguineo.getSelectedItem().toString();

        // Validação dos campos
        if (nomeVoluntario.isEmpty() ||
                idadeVoluntario.isEmpty() ||
                pesoVoluntario.isEmpty() ||
                alturaVoluntario.isEmpty()) {

            Toast.makeText(this,
                    "Preencha todos os dados pessoais.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (tipoSanguineo.getSelectedItemPosition() == 0) {
            Toast.makeText(this,
                    "Selecione o tipo sanguíneo.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (rgDoacaoAnterior.getCheckedRadioButtonId() == -1 ||
                rgReacaoDoacao.getCheckedRadioButtonId() == -1 ||
                rgResponsavel.getCheckedRadioButtonId() == -1) {

            Toast.makeText(this,
                    "Responda todas as perguntas.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (rgDoacaoAnterior.getCheckedRadioButtonId() == R.id.rbDoacaoSim
                && dataUltimaDoacao.isEmpty()) {

            Toast.makeText(this,
                    "Informe a data da última doação.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Dados das respostas
        String doacaoAnterior =
                rgDoacaoAnterior.getCheckedRadioButtonId() == R.id.rbDoacaoSim
                        ? "Sim" : "Não";

        String reacao =
                rgReacaoDoacao.getCheckedRadioButtonId() == R.id.rbReacaoSim
                        ? "Sim" : "Não";

        String responsavel =
                rgResponsavel.getCheckedRadioButtonId() == R.id.rbResponsavelSim
                        ? "Sim" : "Não";

        // Exibição dos dados
        String mensagem =
                "Voluntário: " + nomeVoluntario +
                        "\nIdade: " + idadeVoluntario +
                        "\nTipo sanguíneo: " + sangue +
                        "\nPeso: " + pesoVoluntario +
                        "\nAltura: " + alturaVoluntario +
                        "\nJá doou: " + doacaoAnterior +
                        "\nÚltima doação: " + dataUltimaDoacao +
                        "\nReação anterior: " + reacao +
                        "\nResponsável presente: " + responsavel;

        Toast.makeText(this,
                "Dados preenchidos com sucesso!",
                Toast.LENGTH_LONG).show();
    }
}
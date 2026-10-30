package com.example.doemaisui01;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class cadastroDeVoluntarios extends AppCompatActivity {

    EditText nome, idade, peso, altura, observacoes;
    Spinner tipoSanguineo, statusVoluntario;

    Button btnPreencherQuestionario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_de_voluntarios);

        // Campos pessoais
        nome = findViewById(R.id.editTextText2);
        idade = findViewById(R.id.editTextNumber);
        peso = findViewById(R.id.editTextNumber2);
        altura = findViewById(R.id.editTextNumber3);
        observacoes = findViewById(R.id.etObservacoes);

        // Spinner - Tipo Sanguíneo
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

        // Spinner - Status do Voluntário
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

        // Botão de cadastro
        btnPreencherQuestionario = findViewById(R.id.button12);
        btnPreencherQuestionario.setOnClickListener(v -> cadastrarVoluntario());
    }

    private void cadastrarVoluntario() {
        String nomeVoluntario = nome.getText().toString().trim();
        String idadeVoluntario = idade.getText().toString().trim();
        String pesoVoluntario = peso.getText().toString().trim();
        String alturaVoluntario = altura.getText().toString().trim();
        String obsVoluntario = observacoes.getText().toString().trim();

        String sangue = tipoSanguineo.getSelectedItem().toString();
        String status = statusVoluntario.getSelectedItem().toString();

        // Validação dos dados pessoais
        if (nomeVoluntario.isEmpty() ||
                idadeVoluntario.isEmpty() ||
                pesoVoluntario.isEmpty() ||
                alturaVoluntario.isEmpty()) {

            Toast.makeText(this,
                    "Preencha todos os dados pessoais.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Validação do Tipo Sanguíneo
        if (tipoSanguineo.getSelectedItemPosition() == 0) {
            Toast.makeText(this,
                    "Selecione o tipo sanguíneo.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Validação do Status
        if (statusVoluntario.getSelectedItemPosition() == 0) {
            Toast.makeText(this,
                    "Selecione o status do voluntário.",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        // Exibição de sucesso (ou envio para a próxima tela)
        Toast.makeText(this,
                "Cadastro realizado com sucesso! Status: " + status,
                Toast.LENGTH_LONG).show();
    }
}
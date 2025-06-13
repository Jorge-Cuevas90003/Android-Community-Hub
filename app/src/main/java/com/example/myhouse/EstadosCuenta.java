package com.example.myhouse;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EstadosCuenta extends AppCompatActivity {

    private Spinner spinnerMeses;
    private LinearLayout resultadoLayout;
    private TextView textMes, textEstado, textPago, textBoleta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estados_cuenta);

        spinnerMeses = findViewById(R.id.spinnerMeses);
        resultadoLayout = findViewById(R.id.resultadoLayout);
        textMes = findViewById(R.id.textMes);
        textEstado = findViewById(R.id.textEstado);
        textPago = findViewById(R.id.textPago);
        textBoleta = findViewById(R.id.textBoleta);

        // Datos del Spinner
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, meses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeses.setAdapter(adapter);

        // Manejar la selección del Spinner
        spinnerMeses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String mesSeleccionado = meses[position];
                mostrarResultado(mesSeleccionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                resultadoLayout.setVisibility(View.GONE);
            }
        });
    }

    private void mostrarResultado(String mes) {
        resultadoLayout.setVisibility(View.VISIBLE);
        textMes.setText(mes);
        textEstado.setText("Estado : Pagado");
        textPago.setText("Pago realizado: Q5,000.00");
        textBoleta.setText("Boleta: 00544654");
    }
}
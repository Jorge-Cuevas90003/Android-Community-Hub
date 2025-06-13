package com.example.myhouse;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class actitivty_presupuesto extends AppCompatActivity {

    private Spinner spinnerMes;
    private TextView headerGarita, bodyGarita, headerLimpieza, bodyLimpieza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presupuesto);

        spinnerMes = findViewById(R.id.spinnerMes);
        headerGarita = findViewById(R.id.headerGarita);
        bodyGarita = findViewById(R.id.bodyGarita);
        headerLimpieza = findViewById(R.id.headerLimpieza);
        bodyLimpieza = findViewById(R.id.bodyLimpieza);

        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, meses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMes.setAdapter(adapter);

        headerGarita.setOnClickListener(view -> toggleVisibility(bodyGarita));
        headerLimpieza.setOnClickListener(view -> toggleVisibility(bodyLimpieza));

        spinnerMes.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String mesSeleccionado = parent.getItemAtPosition(position).toString();
                Toast.makeText(actitivty_presupuesto.this, "Mes seleccionado: " + mesSeleccionado, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    private void toggleVisibility(TextView body) {
        if (body.getVisibility() == View.GONE) {
            body.setVisibility(View.VISIBLE);
        } else {
            body.setVisibility(View.GONE);
        }
    }
}

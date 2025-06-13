package com.example.myhouse;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Regista extends AppCompatActivity {
    private static final String TAG = "Regista";
    private EditText houseNumberEdit, contraEdit;
    private DatabaseReference databaseReference;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regista);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Casas");
        Log.d(TAG, "Firebase inicializado correctamente");


        houseNumberEdit = findViewById(R.id.houseNumberEdit);
        contraEdit = findViewById(R.id.contraEdit);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            Log.d(TAG, "Botón de registro presionado");
            registerHouse();
        });

        testFirebaseConnection();
    }

    private void testFirebaseConnection() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference testRef = database.getReference("test");
        testRef.setValue("Test Value").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Conexión a Firebase exitosa");
            } else {
                Log.e(TAG, "Error en la conexión a Firebase: " + task.getException());
            }
        });
    }

    private void registerHouse() {
        String houseNumber = houseNumberEdit.getText().toString().trim().toUpperCase();
        String password = contraEdit.getText().toString().trim();

        if (validateInputs(houseNumber, password)) {
            if (!isValidHouseNumber(houseNumber)) {
                Toast.makeText(this, "Formato inválido. Ej: A4, B5", Toast.LENGTH_LONG).show();
                return;
            }

            String houseId = houseNumber.replace(" ", "");
            checkHouseExists(houseId, houseNumber, password);
        }
    }

    private boolean isValidHouseNumber(String houseNumber) {
        Pattern pattern = Pattern.compile("^[A-Z]\\d+$");
        Matcher matcher = pattern.matcher(houseNumber);
        return matcher.matches();
    }

    private void checkHouseExists(String houseId, String houseNumber, String password) {
        Log.d(TAG, "Verificando si la casa existe: " + houseId);
        databaseReference.child(houseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(Regista.this, "Casa ya registrada", Toast.LENGTH_SHORT).show();
                } else {
                    saveHouse(houseId, houseNumber, password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error al verificar: " + databaseError.getMessage());
                Toast.makeText(Regista.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveHouse(String houseId, String houseNumber, String password) {
        Casa house = new Casa(houseNumber, password);
        Log.d(TAG, "Guardando casa con ID: " + houseId);
        databaseReference.child(houseId).setValue(house)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Casa guardada exitosamente: " + houseId);
                    Toast.makeText(Regista.this, "Registrado con éxito", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al guardar: " + e.getMessage());
                    Toast.makeText(Regista.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validateInputs(String houseNumber, String password) {
        if (houseNumber.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Contraseña min 6 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void clearFields() {
        houseNumberEdit.setText("");
        contraEdit.setText("");
    }
}


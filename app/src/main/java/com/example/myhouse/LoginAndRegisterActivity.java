package com.example.myhouse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class LoginAndRegisterActivity extends AppCompatActivity {
    private MaterialButton btnRegister, btnLogin;
    private TextInputEditText etHouseNumber, etPassword;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_and_register);

        databaseReference = FirebaseDatabase.getInstance().getReference("Casas");

        initializeViews();

        setupWindowInsets();

        setupButtonListeners();
    }

    private void initializeViews() {
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        etHouseNumber = findViewById(R.id.etHouseNumber);
        etPassword = findViewById(R.id.etPassword);
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginandregister), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupButtonListeners() {
        btnRegister.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            v.startAnimation(animation);

            Intent intent = new Intent(LoginAndRegisterActivity.this, Regista.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnLogin.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            v.startAnimation(animation);

            btnLogin.setEnabled(false);

            String houseNumber = etHouseNumber.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (houseNumber.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginAndRegisterActivity.this,
                        "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                btnLogin.setEnabled(true);
                return;
            }

            verifyCredentials(houseNumber, password);
        });
    }


    private void verifyCredentials(String houseNumber, String password) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean credentialsFound = false;

                for (DataSnapshot houseSnapshot : dataSnapshot.getChildren()) {
                    String storedHouseNumber = houseSnapshot.getKey();
                    String storedPassword = houseSnapshot.child("password").getValue(String.class);

                    if (storedHouseNumber != null &&
                            storedHouseNumber.equals(houseNumber) &&
                            storedPassword != null &&
                            storedPassword.equals(password)) {

                        credentialsFound = true;
                        Toast.makeText(LoginAndRegisterActivity.this,
                                "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginAndRegisterActivity.this, Home.class);

                        intent.putExtra("HOUSE_NUMBER", houseNumber);
                        startActivity(intent);
                        finish();
                        break;
                    }
                }

                if (!credentialsFound) {
                    Toast.makeText(LoginAndRegisterActivity.this,
                            "Número de casa o contraseña incorrectos",
                            Toast.LENGTH_SHORT).show();
                }

                btnLogin.setEnabled(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginAndRegisterActivity.this,
                        "Error de conexión: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
                btnLogin.setEnabled(true);
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
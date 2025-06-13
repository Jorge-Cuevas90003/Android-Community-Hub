package com.example.myhouse;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "mi_canal_notificaciones";
    private static final String TAG = "MyFirebaseMessaging";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        crearCanalNotificacion();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, LoginAndRegisterActivity.class);
            startActivity(intent);
            finish();
        }, 2000);

    }

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = "Canal de Notificaciones";
            String descripcion = "Canal para notificaciones de la app";
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, nombre, importancia);
            channel.setDescription(descripcion);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String token = task.getResult();
                            copyTokenToClipboard(token);
                            Log.d("MainActivity", "Token manual: " + token);
                            // Puedes enviarlo al servidor o usarlo según necesites.
                        } else {
                            Log.e("MainActivity", "Error al obtener el token", task.getException());
                        }
                    });
        }
    }

    private void copyTokenToClipboard(String token) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("Firebase Token", token);
            clipboard.setPrimaryClip(clip);
            Log.d(TAG, "Token copiado al portapapeles.");
        } else {
            Log.e(TAG, "No se pudo acceder al portapapeles.");
        }
    }


}

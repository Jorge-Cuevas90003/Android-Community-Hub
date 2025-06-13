package com.example.myhouse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Home extends AppCompatActivity {


    private static void onClick(View view) {

    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_home);


            String houseNumber = getIntent().getStringExtra("HOUSE_NUMBER");


            TextView tvHouseNumber = findViewById(R.id.tvHouseNumber);
            tvHouseNumber.setText("Casa " + houseNumber);


            CardView cardView = findViewById(R.id.propuestas);
            CardView Reglamento=findViewById(R.id.Reglamento);
            CardView Pagos=findViewById(R.id.pagos);
CardView EstadosCuenta=findViewById(R.id.estadoscuentas);
CardView presu=findViewById(R.id.presupuesto);
presu.setOnClickListener(view -> {
    Intent intent=new Intent(Home.this, actitivty_presupuesto.class);
    startActivity(intent);

});
EstadosCuenta.setOnClickListener(view -> {
    Intent intent=new Intent(Home.this, EstadosCuenta.class);
    startActivity(intent);
});
            Pagos.setOnClickListener(view ->{

            Intent intent=new Intent(Home.this, Pagos.class);
            startActivity(intent);
            });

            Reglamento.setOnClickListener(view -> {
                Intent intent=new Intent(Home.this, Reglamento.class);
                startActivity(intent);

            });

            cardView.setOnClickListener(view -> {
                Intent intent = new Intent(Home.this, Votos.class);
                intent.putExtra("HOUSE_NUMBER", houseNumber);
                startActivity(intent);
            });


    }
}
package com.example.qr_code_generatorscanner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

// Actividad de la pantalla de carga inicial
public class MainActivity extends AppCompatActivity
{
    // Referencia a la base de datos firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Hacemos que se muestre en pantalla coimpleta
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Firebase tiene una funcion muy util de guardar una copia local de la base de datos, de esta forma
        // podemos seguir leyendo QRs registrados incluso sin internet, y se actualizara automaticamente
        // la siguiente vez que tengamos conexion
        FirebaseFirestoreSettings configuracion = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        firebaseFirestore.setFirestoreSettings(configuracion);

        // Intentamos actualizar la base de datos
        firebaseFirestore.collection("codigos").get();

        setContentView(R.layout.activity_main);

        // Muestra esta vista por 2 segundos y carga la actividad de autorizacion
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
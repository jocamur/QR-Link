package com.example.qr_code_generatorscanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qr_code_generatorscanner.databinding.ActivityAuthBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

// Actividad de autorizacion
public class AuthActivity extends AppCompatActivity
{
    // Referencia a la vista para accesar a los elementos
    ActivityAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Cargamos la vista inflando el xml y definimos el binding, root y contexto que usaremos en el script
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Context context = binding.getRoot().getContext();

        // Evento al clickear el boton "Entrar"
        binding.buttonEntrar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String email = binding.editTextEmail.getText().toString();
                String contrasena = binding.editTextContrasena.getText().toString();

                // Mandamos a firebase el email y contrasena ingresados para comprobar si existen
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        // Si existen cargamos la siguiente pantalla y enviamos como extra el correo del usuario
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(AuthActivity.this, HomeActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                        }
                        else
                            Toast.makeText(context, "Email y/o contrasena incorrectos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Evento al clickear el boton "Registrarse"
        binding.buttonRegistrarse.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String email = binding.editTextEmail.getText().toString();
                String contrasena = binding.editTextContrasena.getText().toString();

                // Mandamos a firebase el email y contrasena ingresados para comprobar si existen y si no, registrarlos
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        // Si existen cargamos la siguiente pantalla y enviamos como extra el correo del usuario
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(AuthActivity.this, HomeActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                        }
                        else
                            Toast.makeText(context, "Email ya registrado", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

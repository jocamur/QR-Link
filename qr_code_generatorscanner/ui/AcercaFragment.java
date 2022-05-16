package com.example.qr_code_generatorscanner.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.qr_code_generatorscanner.databinding.FragmentAcercaBinding;

// Vista acerca de, no contiene en si ninguna logica, unicamente carga la vista
public class AcercaFragment extends Fragment
{
    // Referencia a la vista para accesar a los elementos, en este caso solo nos servira para inflar la vista
    FragmentAcercaBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentAcercaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
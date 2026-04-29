package com.yinya.crosswarr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.yinya.crosswarr.databinding.FragmentLoadingBinding;

/**
 * Fragmento utilizado para mostrar una pantalla o estado de carga (Loading).
 * Es una vista transitoria que se presenta al usuario mientras se realizan
 * operaciones asíncronas de fondo, como descargas de base de datos o autenticación.
 */
public class LoadingFragment extends Fragment {

    /** Binding para acceder a las vistas del layout asociado a este fragmento. */
    FragmentLoadingBinding binding;

    /**
     * Invocado para inflar el diseño XML asociado a este fragmento utilizando View Binding.
     *
     * @param inflater           El objeto LayoutInflater utilizado para inflar las vistas en el contexto.
     * @param container          El ViewGroup padre en el que se insertará la vista del fragmento.
     * @param savedInstanceState Si no es null, este fragmento está siendo reconstruido a partir de un estado guardado previamente.
     * @return La vista raíz (View) del diseño inflado lista para ser renderizada.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoadingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Invocado cuando la jerarquía de vistas asociada a este fragmento va a ser destruida.
     * Es fundamental anular la referencia al objeto de View Binding aquí para permitir
     * que el recolector de basura (Garbage Collector) libere la memoria y se eviten
     * fugas de memoria (memory leaks).
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
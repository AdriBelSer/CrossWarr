package com.yinya.crosswarr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.yinya.crosswarr.databinding.FragmentNoChallengeBinding;

/**
 * Fragmento utilizado como "Empty State" (Estado Vacío) para el Reto Diario.
 * Se muestra al usuario en la pestaña principal cuando el sistema detecta que el
 * administrador no ha programado ningún desafío específico para la fecha actual.
 */
public class NoChallenge extends Fragment {

    /**
     * Objeto de View Binding para acceder a las vistas del layout asociado de forma segura.
     */
    FragmentNoChallengeBinding binding;

    /**
     * Invocado para inflar el diseño XML asociado a este fragmento.
     *
     * @param inflater           El objeto LayoutInflater utilizado para inflar las vistas en el contexto.
     * @param container          El ViewGroup padre en el que se insertará la vista.
     * @param savedInstanceState Si no es null, el fragmento está siendo reconstruido.
     * @return La vista raíz (View) del diseño inflado.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNoChallengeBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    /**
     * Invocado inmediatamente después de que la vista del fragmento ha sido creada.
     * Configura reglas de navegación y experiencia de usuario (UX).
     *
     * @param view               La Vista devuelta por onCreateView().
     * @param savedInstanceState Si no es null, el fragmento está siendo reconstruido.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // LÓGICA DE NAVEGACIÓN: Bloqueo del botón físico "Atrás".
        // Como esta es una pantalla de nivel superior (Top-Level Destination) en la navegación
        // inferior, evitamos que el usuario salga de la aplicación accidentalmente al pulsar atrás.
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Se deja vacío para que no haga nada al pulsar "atrás"
            }
        });
    }

    /**
     * Invocado cuando la vista del fragmento va a ser destruida.
     * Es imperativo anular la referencia del View Binding para permitir que el
     * Garbage Collector libere la memoria, evitando así fugas (memory leaks).
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
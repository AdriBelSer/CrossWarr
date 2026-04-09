package com.yinya.crosswarr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChallengesEdition extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_challenges_edition, container, false);
    }

    /* TODO: EL SIGUIENTE CÓDIGO ES PARA QUE EL ADMINISTRADOR PUEDA ELEGIR LA IMAGEN DE LA CHALLENGE ENTRE LAS 3 QUE HAY EN LA CARPETA DRAWABLE FALTA REVISARLO
    //TODO: Poner el isUsed del exercise a true cuando se utiliza en un challenge

    public class ChallengesEdition extends Fragment {
    private FragmentChallengesEditionBinding binding;

    // 1. Variable para recordar qué imagen ha elegido para guardarla luego en Firebase
    private String imagenSeleccionada = "imagen_1"; // Por defecto

    // ... (tu onCreateView) ...

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 2. Le damos "vida" al botón de la cámara
        binding.fabEditPhotoChallengeFragmentChallengesEdition.setOnClickListener(v -> {
            mostrarMenuDeImagenes();
        });

        // ... (resto de tu código para el botón de guardar, etc.) ...
    }

    private void mostrarMenuDeImagenes() {
        // 3. Preparamos los nombres que verá el administrador en el menú
        String[] opciones = {"Fuerza (Pesa)", "Cardio (Rayo)", "Resistencia (Fuego)"};

        // 4. Preparamos las imágenes reales de tu carpeta drawable correspondientes a las opciones
        // (Sustituye 'ic_launcher_background' por los nombres reales de tus imágenes)
        int[] imagenesDrawables = {
                R.drawable.tu_imagen_pesa,
                R.drawable.tu_imagen_rayo,
                R.drawable.tu_imagen_fuego
        };

        // 5. Creamos el menú emergente de Material Design
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Elige la imagen del Challenge")
                .setItems(opciones, (dialog, which) -> {
                    // 'which' nos dice qué número de opción ha tocado (0, 1 o 2)

                    // A) Cambiamos la foto redonda para que el administrador la vea
                    binding.ivChallengeAvatarFragmentChallengesEdition.setImageResource(imagenesDrawables[which]);

                    // B) Guardamos el nombre o identificador para enviarlo a Firebase luego
                    if (which == 0) imagenSeleccionada = "imagen_pesa";
                    else if (which == 1) imagenSeleccionada = "imagen_rayo";
                    else if (which == 2) imagenSeleccionada = "imagen_fuego";
                })
                .show(); // ¡No te olvides del .show() para que aparezca!
    }
}
    *
    * */
}
package com.yinya.crosswarr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UsersEdition extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /*TODO: El código de abajo es un ejemplo de cómo elegir una foto de la galería de imágenes del propio programa
       ¿pongo avatares predeterminados?
    * @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.fabEditPhotoChallengeFragmentChallengesEdition.setOnClickListener(v -> {
            showImageMenu();
        });
        binding.btnCreateChallengeFragmentChallengesEdition.setOnClickListener(v -> {
            saveChallenge();
        });

    }

    private void showImageMenu() {
        String[] options = {"10 minutos", "15 minutos", "20 minutos"};
        int[] imagenesDrawables = {
                R.drawable.photo_10,
                R.drawable.photo_15,
                R.drawable.photo_20
        };

        // 5. Creamos el menú emergente de Material Design
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Elige la imagen del desafío")
                .setItems(options, (dialog, which) -> {
                    // 'which' nos dice qué número de opcion se ha elegido (0, 1 o 2)

                    // Cambiamos la foto
                    binding.ivChallengeAvatarFragmentChallengesEdition.setImageResource(imagenesDrawables[which]);

                    // Guardamos el nombre o identificador para enviarlo a
                    if (which == 0) photoChallenge = "photo_10";
                    else if (which == 1) photoChallenge = "photo_15";
                    else if (which == 2) photoChallenge = "photo_20";
                })
                .show();
    }
    * */
}
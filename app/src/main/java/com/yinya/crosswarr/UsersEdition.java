package com.yinya.crosswarr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.Timestamp;
import com.yinya.crosswarr.databinding.FragmentUsersEditionBinding;
import com.yinya.crosswarr.models.UserData;
import com.yinya.crosswarr.repository.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UsersEdition extends Fragment {
    private FragmentUsersEditionBinding binding;
    private String currentUid = null;
    private String currentPhoto = null;
    private Timestamp currentCreationDate = null;
    private String currentPushToken = null;
    private Map<String, Object> currentSettings = new HashMap<>();
    private List<Map<String, Object>> currentChallenges = new java.util.ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
        binding = FragmentUsersEditionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null && getArguments().containsKey("id")) {
            currentUid = getArguments().getString("id");
            String name = getArguments().getString("name");
            String email = getArguments().getString("email");
            String role = getArguments().getString("role");
            currentPhoto = getArguments().getString("photo");

            binding.etNameFragmentUsersEdition.setText(name);
            binding.etEmailFragmentUsersEdition.setText(email);

            binding.switchRoleFragmentUsersEdition.setChecked("admin".equals(role));

            // Recuperar Mapas (Si existen)
            if (getArguments().containsKey("settings")) {
                currentSettings = (Map<String, Object>) getArguments().getSerializable("settings");
            }
            if (getArguments().containsKey("challenges")) {
                currentChallenges = (List<Map<String, Object>>) getArguments().getSerializable("challenges");
            }
        }

        binding.btnCreateUser.setOnClickListener(v -> saveUser());
    }

    private void saveUser() {
        String name = binding.etNameFragmentUsersEdition.getText().toString().trim();
        String email = binding.etEmailFragmentUsersEdition.getText().toString().trim();

        String role = binding.switchRoleFragmentUsersEdition.isChecked() ? "admin" : "user";

        if (name.isEmpty() || email.isEmpty()) {
            android.widget.Toast.makeText(requireContext(), "Por favor, rellena todos los campos", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Ver si quiero crear un usuario nuevo:
        //  Si es un usuario NUEVO, generamos datos base
        if (currentUid == null) {
            currentUid = UUID.randomUUID().toString(); // ID aleatorio
            currentCreationDate = Timestamp.now();
        }

        // Creamos el objeto con los datos combinados (los nuevos de la UI + los viejos que guardamos)
        UserData savedUser = new UserData(
                currentUid,
                email,
                name,
                currentPhoto,
                role,
                currentCreationDate,
                currentPushToken,
                currentSettings,
                currentChallenges
        );

        // createDocumentWithId en FirebaseService usa set() que machaca el documento si existe.
        // Si quieres que actúe como un update combinando datos, en tu FirebaseService.java
        // deberías añadir SetOptions.merge() a ese método en el futuro.
        Repository.getInstance().createUser(savedUser);

        android.widget.Toast.makeText(requireContext(), "Usuario guardado correctamente", android.widget.Toast.LENGTH_SHORT).show();
        goBackToUsers();
    }

    private void goBackToUsers() {
        Navigation.findNavController(requireView()).popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


/*//TODO: El código de abajo es un ejemplo de cómo elegir una foto de la galería de imágenes del propio programa
       //¿pongo avatares predeterminados?
     @Override
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
    */

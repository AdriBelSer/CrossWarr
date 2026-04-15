package com.yinya.crosswarr;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yinya.crosswarr.databinding.FragmentUserProfileBinding;
import com.yinya.crosswarr.models.UserData;
import com.yinya.crosswarr.network.FirebaseService;
import com.yinya.crosswarr.network.IFirebaseCallback;
import com.yinya.crosswarr.repository.Repository;

import java.util.HashMap;
import java.util.Map;

//TODO: REVISAR VOY POR AQUI!!!
//TODO: IR A "DESAFIO"/ INICIO CUANDO GUARDO LOS DATOS DEL USUARIO SIENDO USUARIO
public class UserProfile extends Fragment {
    private FragmentUserProfileBinding binding;
    private UserData currentUserData; // Guardaremos aquí los datos actuales

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupLanguageDropdown();
        loadMyProfile();

        binding.btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void setupLanguageDropdown() {
        String[] languages = new String[]{"Español", "English"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, languages);
        binding.acLanguageProfile.setAdapter(adapter);
    }

    private void loadMyProfile() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser == null) return; // Si no hay usuario logueado, cortamos

        String myUid = fUser.getUid();

        // Le pedimos a FirebaseService que nos traiga SOLO nuestro documento
        FirebaseService.getInstance().getDocument("crosswarr", myUid, new IFirebaseCallback() {
            @Override
            public void onSuccess(Map<String, Object> dataFromFirebase) {
                if (dataFromFirebase != null) {
                    currentUserData = new UserData();
                    currentUserData.setUid((String) dataFromFirebase.get("uid"));
                    currentUserData.setName((String) dataFromFirebase.get("name"));
                    currentUserData.setEmail((String) dataFromFirebase.get("email"));
                    currentUserData.setPhoto((String) dataFromFirebase.get("photo"));
                    currentUserData.setRole((String) dataFromFirebase.get("role"));
                    currentUserData.setAccountCreationDate((com.google.firebase.Timestamp) dataFromFirebase.get("accountCreationDate"));

                    // Recuperar el mapa de Settings
                    Map<String, Object> settings = (Map<String, Object>) dataFromFirebase.get("settings");
                    if (settings != null) {
                        currentUserData.setSettings(settings);

                        // Aplicar los settings a la vista
                        Boolean useMaterials = (Boolean) settings.get("useMaterials");
                        if (useMaterials != null) binding.switchMaterialsProfile.setChecked(useMaterials);

                        String lang = (String) settings.get("language");
                        if (lang != null) binding.acLanguageProfile.setText(lang, false);
                    } else {
                        currentUserData.setSettings(new HashMap<>());
                    }

                    // Rellenar datos básicos en la vista
                    binding.etNameProfile.setText(currentUserData.getName());
                    binding.etEmailProfile.setText(currentUserData.getEmail());
                }
            }

            @Override
            public void onFailure(Exception e) {
                android.widget.Toast.makeText(requireContext(), "Error cargando perfil", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        if (currentUserData == null) return; // Aún no ha cargado

        // 1. Recoger los datos de texto
        String newName = binding.etNameProfile.getText().toString().trim();
        if (newName.isEmpty()) {
            android.widget.Toast.makeText(requireContext(), "El nombre no puede estar vacío", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        currentUserData.setName(newName);

        // 2. Recoger los Settings (El mapa)
        Map<String, Object> mySettings = currentUserData.getSettings();

        mySettings.put("useMaterials", binding.switchMaterialsProfile.isChecked());
        mySettings.put("language", binding.acLanguageProfile.getText().toString());

        // (El Token de notificaciones no lo cambiamos aquí manualmente,
        // eso se suele actualizar en segundo plano al abrir la app).

        // 3. Guardar en Firebase (Pisando el documento actual con los nuevos datos)
        Repository.getInstance().createUser(currentUserData);

        android.widget.Toast.makeText(requireContext(), "Perfil actualizado", android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
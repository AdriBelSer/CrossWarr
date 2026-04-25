package com.yinya.crosswarr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.yinya.crosswarr.databinding.FragmentUserProfileBinding;
import com.yinya.crosswarr.models.UserData;
import com.yinya.crosswarr.network.FirebaseService;
import com.yinya.crosswarr.network.IFirebaseCallback;
import com.yinya.crosswarr.repository.Repository;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends Fragment {
    private FragmentUserProfileBinding binding;
    private UserData currentUserData;
    private PreferencesHelper prefsHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefsHelper = new PreferencesHelper(requireContext());

        setupLanguageDropdown();
        setupThemeSwitch();
        loadMyProfile();

        binding.btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void setupLanguageDropdown() {
        String[] languages = new String[]{"Español", "English"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, languages);
        binding.acLanguageProfile.setAdapter(adapter);
        binding.acLanguageProfile.setText(prefsHelper.getLanguage(), false);
    }

    private void setupThemeSwitch() {
        binding.switchThemeProfile.setChecked(prefsHelper.isDarkMode());
    }

    private void loadMyProfile() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser == null) return; // Si no hay usuario logueado, cortamos

        String myUid = fUser.getUid();

        // Le pedimos a FirebaseService que nos traiga SOLO nuestro documento
        FirebaseService.getInstance().getDocument("crosswarr", myUid, new IFirebaseCallback() {
            @Override
            public void onSuccess(Map<String, Object> dataFromFirebase) {
                if (binding == null || !isAdded()) return;
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
                        if (useMaterials != null)
                            binding.switchMaterialsProfile.setChecked(useMaterials);

                        String lang = (String) settings.get("language");
                        if (lang != null) binding.acLanguageProfile.setText(lang, false);
                    } else {
                        currentUserData.setSettings(new HashMap<>());
                    }
                    Boolean wantsNotifications = (Boolean) settings.get("notifications");
                    // Si es null (usuario nuevo), asumimos que sí quiere (true)
                    if (wantsNotifications != null) {
                        binding.switchNotificationProfile.setChecked(wantsNotifications);
                    } else {
                        binding.switchNotificationProfile.setChecked(true);
                    }

                    // Rellenar datos básicos en la vista
                    binding.etNameProfile.setText(currentUserData.getName());
                    binding.etEmailProfile.setText(currentUserData.getEmail());
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (binding == null || !isAdded()) return;
                android.widget.Toast.makeText(requireContext(), R.string.user_profile_toas_error_charging_profile, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        if (currentUserData == null) return; // Aún no ha cargado

        // 1. Recoger los datos de texto
        String newName = binding.etNameProfile.getText().toString().trim();
        if (newName.isEmpty()) {
            android.widget.Toast.makeText(requireContext(), R.string.user_profile_toas_error_empty_name, android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        currentUserData.setName(newName);

        // 2. Recoger los Settings (El mapa)
        Map<String, Object> mySettings = currentUserData.getSettings();

        mySettings.put("useMaterials", binding.switchMaterialsProfile.isChecked());
        mySettings.put("language", binding.acLanguageProfile.getText().toString());

        boolean wantsNotifications = binding.switchNotificationProfile.isChecked();
        mySettings.put("notifications", wantsNotifications);

        // 3. Guardar en Firebase (Pisando el documento actual con los nuevos datos)
        Repository.getInstance().createUser(currentUserData);

        if (wantsNotifications) {
            FirebaseMessaging.getInstance().subscribeToTopic("nuevos_retos")
                    .addOnCompleteListener(task -> android.util.Log.d("FCM", "Suscrito a retos"));
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("nuevos_retos")
                    .addOnCompleteListener(task -> android.util.Log.d("FCM", "Desuscrito de retos"));
        }
        //Variables tema
        boolean isDark = binding.switchThemeProfile.isChecked();
        prefsHelper.saveDarkMode(isDark);

        //Variables idioma
        String selectedLang = binding.acLanguageProfile.getText().toString();
        prefsHelper.saveLanguage(selectedLang);

        android.widget.Toast.makeText(requireContext(), R.string.user_profile_toas_successful_update, android.widget.Toast.LENGTH_SHORT).show();

        //Aplicacion del tema
        if (isDark) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        }

        //Aplicacion de idioma
        String langCode = selectedLang.equals("English") ? "en" : "es";
        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
                androidx.core.os.LocaleListCompat.forLanguageTags(langCode)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
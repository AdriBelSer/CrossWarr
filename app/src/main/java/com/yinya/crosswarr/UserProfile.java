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

/**
 * Fragmento encargado de gestionar el Perfil del Usuario y sus Preferencias (Ajustes).
 * Permite visualizar y modificar datos personales (nombre), preferencias de entrenamiento
 * (uso de material) y configuraciones de la aplicación (idioma, tema oscuro y notificaciones).
 * Sincroniza estos datos tanto en la base de datos en la nube (Firestore) como en
 * el almacenamiento local del dispositivo (SharedPreferences).
 */
public class UserProfile extends Fragment {
    private FragmentUserProfileBinding binding;
    private UserData currentUserData;
    private PreferencesHelper prefsHelper;

    /**
     * Invocado para inflar el diseño XML asociado a este fragmento utilizando View Binding.
     *
     * @param inflater           El objeto LayoutInflater utilizado para inflar las vistas en el contexto.
     * @param container          El ViewGroup padre en el que se insertará la vista.
     * @param savedInstanceState Si no es null, el fragmento está siendo reconstruido.
     * @return La vista raíz (View) del diseño inflado.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Invocado inmediatamente después de que onCreateView ha retornado.
     * Inicializa las utilidades locales, configura los desplegables e interruptores de la interfaz,
     * dispara la descarga del perfil desde Firebase y establece el listener de guardado.
     *
     * @param view               La Vista devuelta por onCreateView().
     * @param savedInstanceState Si no es null, el fragmento está siendo reconstruido.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefsHelper = new PreferencesHelper(requireContext());

        setupLanguageDropdown();
        setupThemeSwitch();
        loadMyProfile();

        binding.btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    /**
     * Configura el menú desplegable (AutoCompleteTextView) para la selección de idioma.
     * Popula el adaptador con los idiomas disponibles y establece la selección actual
     * leyendo las preferencias locales.
     */
    private void setupLanguageDropdown() {
        String[] languages = new String[]{"Español", "English"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, languages);
        binding.acLanguageProfile.setAdapter(adapter);
        binding.acLanguageProfile.setText(prefsHelper.getLanguage(), false);
    }

    /**
     * Configura el interruptor (Switch) del modo oscuro, leyendo su estado
     * directamente de las preferencias locales del dispositivo.
     */
    private void setupThemeSwitch() {
        binding.switchThemeProfile.setChecked(prefsHelper.isDarkMode());
    }

    /**
     * Descarga los datos del perfil del usuario logueado desde la base de datos de Firestore.
     * Mapea el diccionario de datos a un objeto {@link UserData} y extrae el sub-mapa de
     * ajustes (settings) para configurar los campos de texto e interruptores de la interfaz gráfica.
     */
    private void loadMyProfile() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser == null) return; // Si no hay usuario logueado, cortamos

        String myUid = fUser.getUid();

        // Petición exclusiva del documento correspondiente al usuario actual
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

                    // Recuperar y procesar el mapa anidado de configuraciones (Settings)
                    Map<String, Object> settings = (Map<String, Object>) dataFromFirebase.get("settings");
                    if (settings != null) {
                        currentUserData.setSettings(settings);

                        // Reflejar la preferencia de materiales en la interfaz
                        Boolean useMaterials = (Boolean) settings.get("useMaterials");
                        if (useMaterials != null)
                            binding.switchMaterialsProfile.setChecked(useMaterials);

                        // Reflejar la preferencia de idioma desde la nube
                        String lang = (String) settings.get("language");
                        if (lang != null) binding.acLanguageProfile.setText(lang, false);
                    } else {

                        // Si el usuario es nuevo y no tiene settings, creamos un mapa vacío
                        currentUserData.setSettings(new HashMap<>());
                    }

                    // Configuración del interruptor de notificaciones
                    Boolean wantsNotifications = (Boolean) settings.get("notifications");

                    // Si el dato no existe, asumimos true por defecto (opt-out)
                    if (wantsNotifications != null) {
                        binding.switchNotificationProfile.setChecked(wantsNotifications);
                    } else {
                        binding.switchNotificationProfile.setChecked(true);
                    }

                    // Rellenar los campos de texto de información personal
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


    /**
     * Recoge todos los datos modificados en la interfaz y procesa su guardado.
     * Actualiza el perfil en Firestore, gestiona la suscripción a los canales de Firebase
     * Cloud Messaging (FCM) según las preferencias de notificación, guarda configuraciones visuales
     * en el almacenamiento local y aplica los cambios estéticos (tema/idioma) inmediatamente.
     */
    private void saveProfile() {
        if (currentUserData == null) return; // Evita errores si se pulsa guardar antes de cargar

        // Validar y recoger los datos personales
        String newName = binding.etNameProfile.getText().toString().trim();
        if (newName.isEmpty()) {
            android.widget.Toast.makeText(requireContext(), R.string.user_profile_toas_error_empty_name, android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        currentUserData.setName(newName);

        // Empaquetar el mapa de ajustes (Settings)
        Map<String, Object> mySettings = currentUserData.getSettings();

        mySettings.put("useMaterials", binding.switchMaterialsProfile.isChecked());
        mySettings.put("language", binding.acLanguageProfile.getText().toString());

        boolean wantsNotifications = binding.switchNotificationProfile.isChecked();
        mySettings.put("notifications", wantsNotifications);

        // Sobrescribir el documento en Firebase a través del Repositorio
        Repository.getInstance().createUser(currentUserData);

        // Gestión dinámica de temas (Topics) en Firebase Cloud Messaging
        if (wantsNotifications) {
            FirebaseMessaging.getInstance().subscribeToTopic("nuevos_retos").addOnCompleteListener(task -> android.util.Log.d("FCM", "Suscrito a retos"));
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("nuevos_retos").addOnCompleteListener(task -> android.util.Log.d("FCM", "Desuscrito de retos"));
        }

        // Actualización persistente de preferencias locales visuales
        boolean isDark = binding.switchThemeProfile.isChecked();
        prefsHelper.saveDarkMode(isDark);

        String selectedLang = binding.acLanguageProfile.getText().toString();
        prefsHelper.saveLanguage(selectedLang);

        android.widget.Toast.makeText(requireContext(), R.string.user_profile_toas_successful_update, android.widget.Toast.LENGTH_SHORT).show();

        // Aplicación inmediata del tema visual
        if (isDark) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Aplicación inmediata del idioma (Locales) a toda la aplicación
        String langCode = selectedLang.equals("English") ? "en" : "es";
        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(androidx.core.os.LocaleListCompat.forLanguageTags(langCode));
    }

    /**
     * Invocado cuando la vista del fragmento va a ser destruida.
     * Desvincula el objeto View Binding para evitar fugas de memoria (memory leaks).
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
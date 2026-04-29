package com.yinya.crosswarr;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Clase de utilidad (Helper) para gestionar el almacenamiento local de las preferencias del usuario.
 * Utiliza {@link SharedPreferences} para guardar de forma persistente configuraciones de la aplicación
 * como la visualización del tutorial, el tema visual y el idioma seleccionado.
 */
public class PreferencesHelper {

    /**
     * Nombre del archivo interno donde se guardarán las preferencias.
     */
    private static final String PREFS_NAME = "AppPreferences";

    // Claves de acceso para cada configuración
    private static final String KEY_GUIDE = "needGuide";
    private static final String KEY_DARK_MODE = "isDarkMode";
    private static final String KEY_LANGUAGE = "language";

    private final SharedPreferences preferences;

    /**
     * Constructor que inicializa el acceso al archivo de preferencias.
     * * @param context El contexto de la aplicación o de la actividad que invoca a esta clase.
     * Es necesario para acceder al sistema de almacenamiento de Android de forma privada (MODE_PRIVATE).
     */
    public PreferencesHelper(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // --- GUÍA (TUTORIAL) ---

    /**
     * Comprueba si el usuario necesita ver la guía interactiva inicial.
     * Generalmente devuelve true si es la primera vez que abre la app.
     *
     * @return true si se debe mostrar la guía, false si el usuario ya la ha visto y cerrado.
     */
    public boolean getSavedNeedGuide() {
        return preferences.getBoolean(KEY_GUIDE, true); // Por defecto es true
    }

    /**
     * Guarda la preferencia del usuario sobre la visualización de la guía interactiva.
     *
     * @param needGuide true para volver a mostrar la guía, false para ocultarla permanentemente.
     */
    public void saveNeedGuide(boolean needGuide) {
        preferences.edit().putBoolean(KEY_GUIDE, needGuide).apply();
    }

    // --- TEMA (MODO OSCURO) ---

    /**
     * Comprueba la preferencia de tema visual seleccionada por el usuario.
     *
     * @return true si el modo oscuro está activado, false si prefiere el modo claro.
     * Por defecto asume el modo oscuro (true).
     */
    public boolean isDarkMode() {
        return preferences.getBoolean(KEY_DARK_MODE, true); // Supongamos que tu app es oscura por defecto
    }

    /**
     * Guarda la preferencia de tema visual (oscuro o claro).
     *
     * @param isDark true para activar el modo oscuro, false para el modo claro.
     */
    public void saveDarkMode(boolean isDark) {
        preferences.edit().putBoolean(KEY_DARK_MODE, isDark).apply();
    }

    // --- IDIOMA ---

    /**
     * Recupera el idioma preferido guardado por el usuario para la interfaz de la aplicación.
     *
     * @return Un String con el nombre del idioma (ej. "Español", "English").
     * El valor por defecto es "Español".
     */
    public String getLanguage() {
        return preferences.getString(KEY_LANGUAGE, "Español"); // Por defecto
    }

    public void saveLanguage(String language) {
        preferences.edit().putString(KEY_LANGUAGE, language).apply();
    }
}

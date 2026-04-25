package com.yinya.crosswarr;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper{
        private static final String PREFS_NAME = "AppPreferences";
        private static final String KEY_GUIDE = "needGuide";
        private static final String KEY_DARK_MODE = "isDarkMode";
        private static final String KEY_LANGUAGE = "language";

        private final SharedPreferences preferences;

        // Constructor que recibe el contexto
        public PreferencesHelper(Context context) {
            preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }

        // --- GUÍA ---
        public boolean getSavedNeedGuide() {
            return preferences.getBoolean(KEY_GUIDE, true); // Por defecto es true
        }

        public void saveNeedGuide(boolean needGuide) {
            preferences.edit().putBoolean(KEY_GUIDE, needGuide).apply();
        }

        // --- TEMA (MODO OSCURO) ---
        public boolean isDarkMode() {
            return preferences.getBoolean(KEY_DARK_MODE, true); // Supongamos que tu app es oscura por defecto
        }

        public void saveDarkMode(boolean isDark) {
            preferences.edit().putBoolean(KEY_DARK_MODE, isDark).apply();
        }

        // --- IDIOMA ---
        public String getLanguage() {
            return preferences.getString(KEY_LANGUAGE, "Español"); // Por defecto
        }

        public void saveLanguage(String language) {
            preferences.edit().putString(KEY_LANGUAGE, language).apply();
        }
    }

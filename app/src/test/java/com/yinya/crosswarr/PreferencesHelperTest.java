package com.yinya.crosswarr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Clase de pruebas unitarias (Unit Tests) para {@link PreferencesHelper}.
 * Utiliza la librería Mockito para simular (mockear) las dependencias del sistema operativo Android
 * como {@link Context} y {@link SharedPreferences}, permitiendo probar la lógica de guardado
 * y lectura de preferencias de forma aislada y rápida en la máquina virtual local (JVM).
 */
public class PreferencesHelperTest {

    /**
     * Contexto de la aplicación simulado.
     */
    @Mock
    Context mockContext;

    /**
     * Objeto SharedPreferences simulado para evitar escritura real en disco.
     */
    @Mock
    SharedPreferences mockPrefs;

    /**
     * Editor de SharedPreferences simulado para interceptar las llamadas de guardado.
     */
    @Mock
    SharedPreferences.Editor mockEditor;

    /**
     * Instancia de la clase bajo prueba.
     */
    private PreferencesHelper preferencesHelper;

    /**
     * Configuración inicial que se ejecuta antes de cada prueba (@Test).
     * Inicializa los objetos simulados (Mocks) y establece el comportamiento predeterminado
     * que deben devolver los métodos de Android cuando la clase {@link PreferencesHelper} los invoque.
     */
    @Before
    public void setUp() {
        // Inicializa los Mocks
        MockitoAnnotations.openMocks(this);

        // Simulamos el comportamiento de Android
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs);
        when(mockPrefs.edit()).thenReturn(mockEditor);
        when(mockEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);

        // Instanciamos nuestra clase pasándole el Context falso (Mock)
        preferencesHelper = new PreferencesHelper(mockContext);
    }

    /**
     * Prueba PU-01: Verificar el guardado y lectura del idioma preferido.
     *
     * Entradas: saveLanguage("English")
     * Salidas esperadas: getLanguage() devuelve "English" y se verifica que el editor
     * ha guardado la clave correspondiente.
     *
     */
    @Test
    public void testSaveLanguage() {
        // Preparación: Simular que Android nos devuelve "English" cuando le pedimos el idioma
        when(mockPrefs.getString(eq("language"), anyString())).thenReturn("English");

        // Ejecución
        preferencesHelper.saveLanguage("English");
        String result = preferencesHelper.getLanguage();

        // Verificación: Comprobamos que se ejecutó el método putString con los valores exactos
        verify(mockEditor).putString("language", "English");

        // Verificamos que el resultado devuelto es el esperado
        assertEquals("English", result);
    }

    /**
     * Prueba PU-02: Verificar el guardado y lectura del estado del tema oscuro.
     *
     * Entradas: saveDarkMode(false)
     * Salidas esperadas: isDarkMode() devuelve false y se verifica que el editor
     * ha guardado el estado correspondiente en las preferencias.
     *
     */
    @Test
    public void testDarkMode() {
        // Preparación: Simular que Android nos devuelve false para el modo oscuro
        when(mockPrefs.getBoolean(eq("isDarkMode"), anyBoolean())).thenReturn(false);

        // Ejecución
        preferencesHelper.saveDarkMode(false);
        boolean result = preferencesHelper.isDarkMode();

        // Verificación: Comprobamos que se guardó el valor false bajo la clave "isDarkMode"
        verify(mockEditor).putBoolean("isDarkMode", false);

        // Verificamos que el estado devuelto es falso
        assertFalse(result);
    }
}
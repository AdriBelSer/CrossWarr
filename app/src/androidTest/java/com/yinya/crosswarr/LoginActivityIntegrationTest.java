package com.yinya.crosswarr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Prueba de Integración PI-01: Flujo de Login y Navegación.
 * Utiliza Espresso para automatizar la interfaz de usuario, rellenar los datos de inicio
 * de sesión y verificar que el sistema reacciona navegando a la pantalla principal.
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityIntegrationTest {

    /**
     * Regla moderna recomendada por Google para gestionar el ciclo de vida de la actividad.
     * Se encarga de lanzar automáticamente {@link LoginActivity} antes de cada prueba
     * y de cerrarla de forma segura al terminar.
     */
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Configuración inicial que se ejecuta antes de cada prueba (@Test).
     * Inicializa la herramienta de captura de Intents (navegación) de Espresso,
     * permitiendo interceptar y verificar hacia qué pantallas intenta navegar la aplicación.
     */
    @Before
    public void setUp() {
        Intents.init();
    }

    /**
     * Limpieza que se ejecuta inmediatamente después de cada prueba.
     * Libera los recursos de Espresso Intents para evitar fugas de memoria (memory leaks)
     * y garantizar que las pruebas posteriores comiencen con un estado limpio.
     */
    @After
    public void tearDown() {
        // Liberamos los recursos de Espresso Intents al terminar para evitar fugas de memoria
        Intents.release();
    }

    /**
     * Prueba PI-01: Verificar el flujo de inicio de sesión exitoso.
     * Entradas:Se escribe "usuario@test.com" en el campo de email (etEmail),
     * "123456" en la contraseña (etPassword) y se pulsa el botón de registro/login (btnActionRegister).
     * Salidas esperadas: Se verifica que la aplicación lanza un Intent explícito
     * hacia {@link MainActivity}, confirmando que la navegación tras el login se ha ejecutado.
     *
     */
    @Test
    public void testPI01_LoginFlow() {
        // INPUTS: Escribir email y cerrar teclado
        onView(withId(R.id.et_email)) // Asegúrate de que este es el ID de tu XML
                .perform(typeText("new@new.com"), closeSoftKeyboard());

        // INPUTS: Escribir contraseña y cerrar teclado
        onView(withId(R.id.et_password)) // Asegúrate de que este es el ID de tu XML
                .perform(typeText("142536Aa"), closeSoftKeyboard());

        // INPUTS: Pulsar el botón de login
        onView(withId(R.id.btn_action_register)) // Cambia al ID real de tu botón de login
                .perform(click());

        // PAUSA DE ESPERA
        // Pausamos el test 3 segundos para darle tiempo a Firebase a ir a internet y volver
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // OUTPUTS: Verificar que la app intentó navegar a MainActivity
        // (Esto asume que el Mock o el Login real fue exitoso y el código hizo startActivity(intent))
        intended(hasComponent(MainActivity.class.getName()));
    }
}
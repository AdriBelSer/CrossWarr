package com.yinya.crosswarr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.replaceText;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Prueba de Integración PI-02: Flujo de creación de ejercicio en el Fragmento.
 * Verifica la interacción del usuario rellenando el formulario y comprueba
 * que la interfaz se reinicia (cleanForm) tras el envío exitoso de los datos.
 */
@RunWith(AndroidJUnit4.class)
public class ExercisesEditionIntegrationTest {

    @Test
    public void testPI02_CreateExerciseFlow() {
        // 1. Lanzar el fragmento en un entorno de pruebas aislado
        FragmentScenario<ExercisesEdition> scenario = FragmentScenario.launchInContainer(
                ExercisesEdition.class, null, R.style.Theme_Crosswarr);

        // 2. INPUTS: Inyectar nombre del ejercicio usando replaceText
        onView(withId(R.id.et_name_fragment_exercises_edition))
                .perform(replaceText("FlexionesPrueba"), closeSoftKeyboard());

        // 3. INPUTS: Inyectar descripción usando replaceText
        onView(withId(R.id.et_description_fragment_exercises_edition))
                .perform(replaceText("Ejercicio de prueba para integración"), closeSoftKeyboard());

        // 4. INPUTS: Seleccionar tipo de ejercicio y materiales
        onView(withId(R.id.rb_upper_body_exercises_edition))
                .perform(click());

        onView(withId(R.id.switch_materials_fragment_exercises_edition))
                .perform(click());

        // 5. INPUTS: Pulsar el botón "Crear"
        onView(withId(R.id.btn_create_exercise))
                .perform(click());

        // 6. OUTPUTS: Verificar que cleanForm() se ejecutó correctamente
        // Lo sabemos porque el campo de nombre debería volver a estar vacío ("")
        onView(withId(R.id.et_name_fragment_exercises_edition))
                .check(matches(withText("")));
    }
}
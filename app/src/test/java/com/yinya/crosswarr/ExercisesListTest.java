package com.yinya.crosswarr;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.yinya.crosswarr.models.ExerciseData;

import org.junit.Test;

/**
 * Clase de pruebas unitarias (Unit Tests) para aislar y validar la lógica de negocio
 * correspondiente al filtrado del catálogo de ejercicios.
 * Asegura que los filtros cruzados (estado activo del ejercicio vs. preferencias de
 * material del usuario) funcionen con precisión antes de renderizar las listas visuales.
 */
public class ExercisesListTest {

    /**
     * Método auxiliar que simula y aísla la lógica exacta de filtrado iterativo (bucle for)
     * que se ejecuta en el fragmento ExercisesList.
     * Evalúa dos filtros consecutivos:
     * 1. Si el ejercicio está marcado como en uso (isUsed).
     * 2. Si el ejercicio requiere material y, de ser así, si el usuario dispone de él.
     *
     * @param ex               El objeto de datos del ejercicio a evaluar.
     * @param userHasEquipment Booleano que indica si el perfil del usuario tiene habilitado el uso de material.
     * @return true si el ejercicio supera los filtros y debe mostrarse en pantalla; false en caso contrario.
     */
    private boolean shouldShowExercise(ExerciseData ex, boolean userHasEquipment) {
        // FILTRO 1: ¿Está activo?
        if (!ex.isUsed()) return false;

        // FILTRO 2: Requerimientos de material
        boolean exerciseRequiresEquipment = false;
        if (ex.getType() != null) {
            String type = ex.getType().toLowerCase();
            exerciseRequiresEquipment = type.contains("with_equipment") && !type.contains("without_equipment");
        }

        // Se muestra si no requiere equipo, o si requiere y el usuario lo tiene
        return !exerciseRequiresEquipment || userHasEquipment;
    }

    /**
     * Prueba PU-05: Verificar el filtro primario (Ejercicio oculto si no está en uso).
     * Entradas: Ejercicio con isUsed = false, Preferencia Usuario: true.
     * Salidas esperadas: La función devuelve false (El ejercicio NO se añade a la lista visual).
     *
     */
    @Test
    public void testPU05_HiddenIfNotUsed() {

        // Preparación
        ExerciseData exercise = new ExerciseData();
        exercise.setUsed(false); // isUsed = false

        // Ejecución
        boolean isVisible = shouldShowExercise(exercise, true);

        // Verificación
        assertFalse(isVisible); // exercises.size() == 0
    }

    /**
     * Prueba PU-06: Verificar la visibilidad de un ejercicio activo sin requerimientos especiales.
     * Entradas: Ejercicio con isUsed = true y tipo "core_without_equipment".
     * Salidas esperadas: La función devuelve true (El ejercicio SÍ se añade a la lista visual).
     *
     */
    @Test
    public void testPU06_VisibleIfUsed() {

        // Preparación
        ExerciseData exercise = new ExerciseData();
        exercise.setUsed(true); // isUsed = true
        exercise.setType("core_without_equipment");

        // Ejecución
        boolean isVisible = shouldShowExercise(exercise, false);

        // Verificación
        assertTrue(isVisible);
    }

    /**
     * Prueba PU-07: Verificar el filtro de materiales (Bloqueo por falta de material).
     * Entradas: Ejercicio de tipo "upper_body_with_equipment", Preferencia Usuario: useMaterials = false.
     * Salidas esperadas: La función devuelve false (El ejercicio NO se añade a la lista).
     *
     */
    @Test
    public void testPU07_BlockIfRequiresMaterialAndUserHasNone() {

        // Preparación
        ExerciseData exercise = new ExerciseData();
        exercise.setUsed(true);
        exercise.setType("upper_body_with_equipment"); // Requiere material

        // Ejecución
        boolean userHasMaterials = false; // useMaterials = false
        boolean isVisible = shouldShowExercise(exercise, userHasMaterials);

        //Verificación
        assertFalse(isVisible); // NO se añade
    }

    /**
     * Prueba PU-08: Verificar el filtro de materiales (Permiso por posesión de material).
     * Entradas: Ejercicio de tipo "upper_body_with_equipment", Preferencia Usuario: useMaterials = true.
     * Salidas esperadas: La función devuelve true (El ejercicio SÍ se añade a la lista).
     *
     */
    @Test
    public void testPU08_AllowIfRequiresMaterialAndUserHasMaterial() {

        // Preparación
        ExerciseData exercise = new ExerciseData();
        exercise.setUsed(true);
        exercise.setType("upper_body_with_equipment"); // Requiere material

        boolean userHasMaterials = true; // useMaterials = true

        // Ejecución
        boolean isVisible = shouldShowExercise(exercise, userHasMaterials);

        // Verificación
        assertTrue(isVisible); // SÍ se añade
    }
}
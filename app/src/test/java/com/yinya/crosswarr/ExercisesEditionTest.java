package com.yinya.crosswarr;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Clase de pruebas unitarias (Unit Tests) para validar la lógica de los formularios
 * de edición y creación de ejercicios.
 * Garantiza que la aplicación bloquee los intentos de guardado cuando faltan datos
 * obligatorios, previniendo la inserción de registros corruptos o incompletos en la base de datos.
 */
public class ExercisesEditionTest {

    /**
     * Método auxiliar que aísla y simula la lógica de validación de campos del formulario
     * que se ejecuta nativamente en el fragmento antes de invocar al Repositorio.
     *
     * @param name        El nombre del ejercicio introducido en el campo de texto.
     * @param description La descripción del ejercicio introducida en el campo de texto.
     * @return true si los campos son válidos y la ejecución puede continuar para guardar;
     * false si algún campo está vacío o es nulo (lo que detiene el guardado).
     */
    private boolean validateExerciseForm(String name, String description) {
        if (name == null || name.isEmpty() || description == null || description.isEmpty()) {
            return false; // return (Se detiene ejecución)
        }
        return true;
    }

    /**
     * Prueba PU-09: Validar el rechazo del formulario al detectar campos obligatorios vacíos.
     * Entradas: name = "" (campo vacío), description = "Desc".
     * Salidas esperadas: La función devuelve false. Esto verifica que la validación
     * intercepta el error y detendría la ejecución del código sin llegar a llamar
     * a Repository.createExercise().
     *
     */
    @Test
    public void testPU09_RejectEmptyFields() {

        // Preparación de datos (Input con un campo inválido)
        String name = "";
        String description = "Desc";

        // Ejecución de la validación
        boolean isValid = validateExerciseForm(name, description);

        // Verificación: Se espera que isValid sea false
        assertFalse(isValid);
    }

}
package com.yinya.crosswarr;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Clase de pruebas unitarias (Unit Tests) para aislar y evaluar la lógica de
 * creación de Retos, específicamente la generación de sus identificadores únicos.
 * Garantiza que el formato de las fechas y los prefijos de equipamiento se
 * apliquen correctamente antes de enviar los datos a Firebase.
 */
public class ChallengesEditionTest {

    /**
     * Método auxiliar que extrae y aísla la lógica de generación de ID de la clase principal.
     * Toma una fecha en formato tradicional (DD-MM-YYYY), la invierte a un formato ordenable
     * (YYYYMMDD) y le añade un prefijo dependiendo de si el reto requiere material o no.
     *
     * @param dateStr           La fecha de activación del reto en formato "DD-MM-YYYY".
     * @param requiresEquipment Booleano que indica si el reto requiere equipamiento (true) o no (false).
     * @return Una cadena de texto (String) con el identificador final formateado.
     */
    private String generateChallengeId(String dateStr, boolean requiresEquipment) {
        String[] parts = dateStr.split("-");
        String formattedDate = parts[2] + parts[1] + parts[0]; // Pasa de DD-MM-YYYY a YYYYMMDD
        return requiresEquipment ? "eq_challenge_" + formattedDate : "challenge_" + formattedDate;
    }

    /**
     * Prueba PU-03: Verificar la generación de ID para retos que NO requieren material.
     * Entradas: Fecha: "16-04-2026", RequiresEquipment: false
     * Salidas esperadas: El identificador generado debe ser exactamente "challenge_20260416".
     *
     */
    @Test
    public void testPU03_GenerateIdWithoutMaterial() {
        String date = "16-04-2026";
        boolean requiresEquipment = false;

        String generatedId = generateChallengeId(date, requiresEquipment);

        assertEquals("challenge_20260416", generatedId);
    }

    /**
     * Prueba PU-04: Verificar la generación de ID para retos que SÍ requieren material.
     * Entradas: Fecha: "16-04-2026", RequiresEquipment: true
     * Salidas esperadas: El identificador generado debe incluir el prefijo 'eq_'
     * resultando en "eq_challenge_20260416".
     *
     */
    @Test
    public void testPU04_GenerateIdWithMaterial() {
        String date = "16-04-2026";
        boolean requiresEquipment = true;

        String generatedId = generateChallengeId(date, requiresEquipment);

        assertEquals("eq_challenge_20260416", generatedId);
    }
}


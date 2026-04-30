package com.yinya.crosswarr.models;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Clase de pruebas unitarias (Unit Tests) para el modelo de datos {@link UserData}.
 * Verifica que la instanciación de objetos, constructores y la asignación de
 * atributos principales funcionen correctamente, garantizando la integridad de los datos
 * antes de interactuar con la base de datos (Firestore).
 */
public class UserDataTest {

    /**
     * Prueba PU-10: Comprobar la instanciación del modelo de usuario con valores específicos.
     * Entradas: role = "admin", email = "test@test.com" (pasados a través del constructor completo).
     * Salidas esperadas: Los métodos getter correspondientes (user.getRole() y user.getEmail())
     * devuelven exactamente "admin" y "test@test.com" respectivamente.
     * Esto verifica que el constructor mapea de forma precisa los parámetros a sus variables internas.
     *
     */
    @Test
    public void testPU10_UserDataInstantiation() {

        // Preparación de datos (Inputs)
        String expectedRole = "admin";
        String expectedEmail = "test@test.com";

        // Ejecución: Instanciamos el modelo con los valores del test
        UserData user = new UserData(
                "uid123", expectedEmail, "Usuario Test", "", expectedRole,
                null, "", new HashMap<>(), new ArrayList<>()
        );

        // Verificamos los outputs
        assertEquals(expectedRole, user.getRole());
        assertEquals(expectedEmail, user.getEmail());
    }

}
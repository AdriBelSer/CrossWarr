package com.yinya.crosswarr.network;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Servicio de Autenticación que actúa como envoltorio (wrapper) para {@link FirebaseAuth}.
 * Implementa el patrón Singleton para garantizar que toda la aplicación utilice
 * una única instancia de conexión para gestionar las sesiones de los usuarios.
 */
public class FirebaseAuthService {
    private static FirebaseAuthService instance;
    private final FirebaseAuth mAuth;

    /**
     * Constructor privado para forzar el uso del patrón Singleton.
     * Inicializa la instancia interna de FirebaseAuth.
     */
    private FirebaseAuthService() {
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Obtiene la instancia única y global del servicio de autenticación.
     * Si la instancia no existe, la crea de forma segura.
     *
     * @return La instancia Singleton de {@link FirebaseAuthService}.
     */
    public static FirebaseAuthService getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthService();
        }
        return instance;
    }

    /**
     * Recupera el usuario que tiene la sesión iniciada actualmente en el dispositivo.
     *
     * @return El objeto {@link FirebaseUser} actual, o null si no hay ninguna sesión activa.
     */
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    /**
     * Inicia sesión en Firebase utilizando un correo electrónico y una contraseña.
     * La operación es asíncrona, por lo que el resultado se devuelve a través de un callback.
     *
     * @param email    El correo electrónico del usuario.
     * @param password La contraseña del usuario.
     * @param callback Interfaz para manejar el éxito (devuelve el usuario) o el fallo de la operación.
     */
    public void loginWithEmailAndPassword(String email, String password, IAuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(mAuth.getCurrentUser());
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Envía un correo electrónico automatizado de Firebase para restablecer la contraseña.
     *
     * @param email    El correo electrónico asociado a la cuenta que se desea recuperar.
     * @param callback Interfaz para manejar el resultado. En caso de éxito, devuelve null
     *                 ya que no hay un usuario logueado como resultado de esta acción.
     */
    public void sendPasswordResetEmail(String email, IAuthCallback callback) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null); // No hay usuario que devolver aquí
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Cierra la sesión del usuario actual y borra los tokens de acceso del dispositivo.
     */
    public void logout() {
        mAuth.signOut();
    }

    /**
     * Registra una nueva cuenta de usuario en Firebase Authentication.
     * Una vez creado el usuario, Firebase inicia su sesión automáticamente.
     *
     * @param email    El correo electrónico para la nueva cuenta.
     * @param password La contraseña para la nueva cuenta (mínimo 6 caracteres por defecto en Firebase).
     * @param callback Interfaz para manejar el éxito (devuelve el nuevo usuario creado) o el fallo.
     */
    public void registerWithEmailAndPassword(String email, String password, IAuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(mAuth.getCurrentUser());
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Interfaz de comunicación (Callback) para gestionar las respuestas asíncronas
     * de las operaciones de Firebase Authentication.
     */
    public interface IAuthCallback {

        /**
         * Se invoca cuando la operación de autenticación finaliza con éxito.
         *
         * @param user El usuario autenticado, o null si la operación no devuelve un usuario (ej. reseteo de contraseña).
         */
        void onSuccess(FirebaseUser user);

        /**
         * Se invoca cuando la operación de autenticación falla debido a credenciales inválidas,
         * problemas de red, o cuentas inexistentes.
         *
         * @param e La excepción lanzada por Firebase con los detalles del error.
         */
        void onFailure(Exception e);
    }
}


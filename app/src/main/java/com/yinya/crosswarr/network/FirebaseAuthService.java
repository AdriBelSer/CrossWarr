package com.yinya.crosswarr.network;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthService {
    private static FirebaseAuthService instance;
    private final FirebaseAuth mAuth;

    private FirebaseAuthService() {
        mAuth = FirebaseAuth.getInstance();
    }

    public static FirebaseAuthService getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthService();
        }
        return instance;
    }

    // Obtener el usuario actual
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    // Iniciar sesión
    public void loginWithEmailAndPassword(String email, String password, IAuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(mAuth.getCurrentUser());
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    // Recuperar contraseña
    public void sendPasswordResetEmail(String email, IAuthCallback callback) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(null); // No hay usuario que devolver aquí
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    // (Opcional) Método para cerrar sesión que puedes usar en tu MainActivity
    public void logout() {
        mAuth.signOut();
    }

    public void registerWithEmailAndPassword(String email, String password, IAuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(mAuth.getCurrentUser());
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    // Interfaz para avisar a la Activity cuando la tarea termine
    public interface IAuthCallback {
        void onSuccess(FirebaseUser user);

        void onFailure(Exception e);
    }
}


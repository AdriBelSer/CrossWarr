package com.yinya.crosswarr;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.yinya.crosswarr.databinding.ActivityLoginBinding;
import com.yinya.crosswarr.models.UserData;
import com.yinya.crosswarr.network.FirebaseAuthService;
import com.yinya.crosswarr.repository.Repository;

import java.util.ArrayList;

/**
 * Actividad principal de autenticación.
 * Actúa como la puerta de entrada a la aplicación, gestionando tanto el inicio de sesión
 * de usuarios existentes como el registro de nuevas cuentas.
 * Sincroniza la autenticación (Firebase Auth) con la base de datos (Firestore),
 * asegurando que cada usuario tenga su perfil creado antes de acceder al contenido principal.
 */
public class LoginActivity extends AppCompatActivity {
    ObjectAnimator animator;
    private Repository rp;
    private FirebaseAuthService authService;
    private ActivityLoginBinding binding;
    private boolean isLoginMode = false;

    /**
     * Invocado cuando la actividad es creada.
     * Configura el View Binding, inicializa las dependencias de red, arranca la
     * animación de carga y verifica si el usuario ya tiene una sesión activa (Auto-login).
     *
     * @param savedInstanceState Si la actividad se está reiniciando, contiene el estado anterior.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rp = Repository.getInstance();
        authService = FirebaseAuthService.getInstance();

        // Animación de pulso (latido) para el logo de carga
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.1f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.1f, 1f);
        animator = ObjectAnimator.ofPropertyValuesHolder(binding.ivLoadingLogo, scaleX, scaleY);
        animator.setDuration(800);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();

        // Comprobar si ya hay sesión iniciada (Auto-login) para evitar pedir credenciales de nuevo
        FirebaseUser user = authService.getCurrentUser();
        if (user != null) {
            verifyUserDocument(user);

            // Si no hay sesión, detenemos el logo palpitante y mostramos el formulario
        } else {
            animator.cancel();
            binding.ivLoadingLogo.setVisibility(View.GONE);
        }

        setupUI();
        setupListeners();
    }

    /**
     * Configuración inicial de la interfaz de usuario.
     * Por defecto, la pantalla arranca en modo "Inicio de sesión".
     */
    private void setupUI() {
        switchToLoginMode();
        binding.tilUsername.setVisibility(View.GONE);
    }

    /**
     * Establece los oyentes (listeners) para los eventos de interacción del usuario,
     * como el cambio de pestañas, el botón principal de acción y la recuperación de contraseña.
     */
    private void setupListeners() {
        // Listener para alternar visualmente entre Login y Registro
        binding.toggleTabs.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_tab_login) {
                    switchToLoginMode();
                } else if (checkedId == R.id.btn_tab_register) {
                    switchToRegisterMode();
                }
            }
        });

        // Listener para el botón principal ("Continuar" o "Entrar")
        binding.btnActionRegister.setOnClickListener(v -> {
            if (isLoginMode) {
                performLogin();
            } else {
                performRegistration();
            }
        });

        // Listener para el flujo de "He olvidado la contraseña"
        binding.tvForgotPassword.setOnClickListener(v -> resetPassword());
    }

    /**
     * Transforma la interfaz de usuario al modo "Inicio de Sesión".
     * Oculta el campo de nombre de usuario, muestra la opción de recuperar contraseña
     * y ajusta los colores de las pestañas para indicar el estado activo.
     */
    private void switchToLoginMode() {
        isLoginMode = true;
        int primaryColor = androidx.core.content.ContextCompat.getColor(this, R.color.md_theme_primary);

        binding.tilUsername.setVisibility(View.GONE);
        binding.tvForgotPassword.setVisibility(View.VISIBLE);
        binding.btnActionRegister.setText(R.string.btn_tab_login_activity_login);

        // Ajuste de colores para la pestaña activa
        binding.btnTabLogin.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
        binding.btnTabLogin.setTextColor(android.graphics.Color.BLACK);
        binding.btnTabRegister.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT));
        binding.btnTabRegister.setTextColor(primaryColor);
        binding.btnTabLogin.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
    }

    /**
     * Transforma la interfaz de usuario al modo "Registro".
     * Muestra el campo de nombre de usuario, oculta la recuperación de contraseña
     * y ajusta los colores de las pestañas.
     */
    private void switchToRegisterMode() {
        isLoginMode = false;
        int primaryColor = androidx.core.content.ContextCompat.getColor(this, R.color.md_theme_primary);

        binding.tilUsername.setVisibility(View.VISIBLE);
        binding.tvForgotPassword.setVisibility(View.GONE);
        binding.btnActionRegister.setText(R.string.btn_continue_activity_login);

        // Ajuste de colores para la pestaña activa
        binding.btnTabRegister.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
        binding.btnTabRegister.setTextColor(android.graphics.Color.BLACK);
        binding.btnTabLogin.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT));
        binding.btnTabLogin.setTextColor(primaryColor);
    }

    // --- MÉTODOS DE AUTENTICACIÓN ---

    /**
     * Valida los campos de entrada y solicita el inicio de sesión a Firebase Auth.
     * Si tiene éxito, procede a verificar el documento del usuario en la base de datos.
     */
    private void performLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.login_activity_toast_email_pss_needed, Toast.LENGTH_SHORT).show();
            return;
        }

        authService.loginWithEmailAndPassword(email, password, new FirebaseAuthService.IAuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                verifyUserDocument(user);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(LoginActivity.this, R.string.login_activity_toast_error, Toast.LENGTH_SHORT).show();
                Log.e("TEST_LOGIN", "Error en login", e);
            }
        });
    }

    /**
     * Valida los campos, crea una cuenta nueva en Firebase Auth y actualiza el perfil
     * asociado con el nombre proporcionado. Una vez completado, verifica y crea su documento
     * en la base de datos de Firestore.
     */
    private void performRegistration() {
        String username = binding.etUsername.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.login_activity_toast_resitration_fiels_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        authService.registerWithEmailAndPassword(email, password, new FirebaseAuthService.IAuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                // Actualizamos el perfil interno de Firebase Auth con el nombre visual
                com.google.firebase.auth.UserProfileChangeRequest profileUpdates = new com.google.firebase.auth.UserProfileChangeRequest.Builder().setDisplayName(username).build();

                user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                    // Una vez registrado y actualizado el nombre, pasamos a la verificación del documento
                    verifyUserDocument(user);
                });
            }

            @Override
            public void onFailure(Exception e) {
                String message = getString(R.string.login_activity_toast_resitration_error, e.getMessage());
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                Log.e("TEST_LOGIN", "Error en registro", e);
            }
        });
    }

    /**
     * Envía un correo electrónico de recuperación de contraseña a través de Firebase.
     * Requiere que el campo de correo electrónico esté rellenado.
     */
    private void resetPassword() {
        String email = binding.etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            binding.tilEmail.setError(getString(R.string.login_activity_title_reset_pss));
            Toast.makeText(this, R.string.login_activity_toast_reset_pss, Toast.LENGTH_LONG).show();
            return;
        }
        binding.tilEmail.setError(null);

        authService.sendPasswordResetEmail(email, new FirebaseAuthService.IAuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Toast.makeText(LoginActivity.this, R.string.login_activity_toast_reset_pss_success, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(LoginActivity.this, R.string.login_activity_toast_reset_pss_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- MÉTODOS DE NAVEGACIÓN Y FIRESTORE ---

    /**
     * Comprueba si el usuario autenticado tiene su documento de perfil correspondiente en Firestore.
     * Si el documento existe (login normal), le da acceso a la app.
     * Si no existe (nuevo registro o error previo), construye el objeto {@link UserData} base
     * y ordena al Repositorio que lo guarde en la nube antes de dejarle entrar.
     *
     * @param user El usuario autenticado devuelto por Firebase Auth.
     */
    private void verifyUserDocument(FirebaseUser user) {
        com.yinya.crosswarr.network.FirebaseService.getInstance().getDocument("crosswarr", user.getUid(), new com.yinya.crosswarr.network.IFirebaseCallback() {
            @Override
            public void onSuccess(java.util.Map<String, Object> dataFromFirebase) {
                Log.d("TEST_LOGIN", "Documento encontrado, navegando a Main.");
                goToMainActivity();
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("TEST_LOGIN", "Documento NO encontrado. Creándolo ahora...");

                // Fallback seguro: si Auth no ha tenido tiempo de propagar el DisplayName, lo cogemos de la UI
                String name = user.getDisplayName();
                if (name == null || name.isEmpty()) {
                    name = binding.etUsername.getText().toString().trim();
                }
                if (name.isEmpty()) name = "Usuario";

                String photoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";

                // Creación de la estructura del perfil estándar para un usuario nuevo
                UserData userData = new UserData(user.getUid(), user.getEmail(), name, photoUrl, "user", // Rol por defecto
                        com.google.firebase.Timestamp.now(), "", new java.util.HashMap<>(), new ArrayList<>());

                rp.createUser(userData);
                goToMainActivity();
            }
        });
    }

    /**
     * Inicia la Actividad Principal de la aplicación (MainActivity) y finaliza la actual.
     * Finalizar esta actividad asegura que el usuario no pueda volver a la pantalla de login
     * simplemente pulsando el botón físico "Atrás".
     */
    private void goToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * Invocado cuando la actividad va a ser destruida por el sistema.
     * Se asegura de limpiar las animaciones en ejecución para evitar fugas de memoria.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Asegurarnos de que la animación muera con la actividad
        if (animator != null) {
            animator.cancel();
        }
    }
}
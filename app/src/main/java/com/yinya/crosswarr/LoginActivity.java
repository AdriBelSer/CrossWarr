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

public class LoginActivity extends AppCompatActivity {
    ObjectAnimator animator;
    private Repository rp;
    private FirebaseAuthService authService;
    private ActivityLoginBinding binding;
    private boolean isLoginMode = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rp = Repository.getInstance();
        authService = FirebaseAuthService.getInstance();

// Animación para el logo de carga
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.1f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.1f, 1f);
        animator = ObjectAnimator.ofPropertyValuesHolder(binding.ivLoadingLogo, scaleX, scaleY);
        animator.setDuration(800);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();

        // Comprobar si ya hay sesión iniciada (Auto-login)
        FirebaseUser user = authService.getCurrentUser();
        if (user != null) {
            verifyUserDocument(user);

        } else {
            animator.cancel();
            binding.ivLoadingLogo.setVisibility(View.GONE);
        }

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        switchToLoginMode();
        binding.tilUsername.setVisibility(View.GONE);
    }

    private void setupListeners() {
        // Listener para las pestañas (Toggle Group)
        binding.toggleTabs.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_tab_login) {
                    switchToLoginMode();
                } else if (checkedId == R.id.btn_tab_register) {
                    switchToRegisterMode();
                }
            }
        });

        // Listener para el botón principal ("Continuar")
        binding.btnActionRegister.setOnClickListener(v -> {
            if (isLoginMode) {
                performLogin();
            } else {
                performRegistration();
            }
        });

        // Listener para "He olvidado la contraseña"
        binding.tvForgotPassword.setOnClickListener(v -> resetPassword());
    }

    private void switchToLoginMode() {
        isLoginMode = true;
        int primaryColor = androidx.core.content.ContextCompat.getColor(this, R.color.md_theme_primary);

        binding.tilUsername.setVisibility(View.GONE);
        binding.tvForgotPassword.setVisibility(View.VISIBLE);
        binding.btnActionRegister.setText(R.string.btn_tab_login_activity_login);
        binding.btnTabLogin.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
        binding.btnTabLogin.setTextColor(android.graphics.Color.BLACK);
        binding.btnTabRegister.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT));
        binding.btnTabRegister.setTextColor(primaryColor);
        binding.btnTabLogin.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
    }

    private void switchToRegisterMode() {
        isLoginMode = false;
        int primaryColor = androidx.core.content.ContextCompat.getColor(this, R.color.md_theme_primary);

        binding.tilUsername.setVisibility(View.VISIBLE);
        binding.tvForgotPassword.setVisibility(View.GONE);
        binding.btnActionRegister.setText(R.string.btn_continue_activity_login);
        binding.btnTabRegister.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
        binding.btnTabRegister.setTextColor(android.graphics.Color.BLACK);
        binding.btnTabLogin.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT));
        binding.btnTabLogin.setTextColor(primaryColor);
    }

    // --- MÉTODOS DE AUTENTICACIÓN ---

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
                // Actualizamos el perfil de Firebase Auth con el nombre que introdujo
                com.google.firebase.auth.UserProfileChangeRequest profileUpdates = new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build();

                user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                    // Una vez registrado y actualizado el nombre, vamos al Main
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

                // Si estamos en flujo de registro manual, user.getDisplayName() podría no estar listo aún en Firebase,
                // así que cogemos el texto del campo si está disponible.
                String name = user.getDisplayName();
                if (name == null || name.isEmpty()) {
                    name = binding.etUsername.getText().toString().trim();
                }
                if (name.isEmpty()) name = "Usuario";

                String photoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";

                UserData userData = new UserData(
                        user.getUid(),
                        user.getEmail(),
                        name,
                        photoUrl,
                        "user",
                        com.google.firebase.Timestamp.now(),
                        "",
                        new java.util.HashMap<>(),
                        new ArrayList<>()
                );

                rp.createUser(userData);
                goToMainActivity();
            }
        });
    }

    private void goToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 🔥 Asegurarnos de que la animación muera con la actividad
        if (animator != null) {
            animator.cancel();
        }
    }
}
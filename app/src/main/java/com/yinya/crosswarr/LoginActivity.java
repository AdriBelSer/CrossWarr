package com.yinya.crosswarr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yinya.crosswarr.models.UserData;
import com.yinya.crosswarr.repository.Repository;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private Repository rp;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TEST_LOGIN", "onCreate");
        // TODO: setContentView(R.layout.activity_login); Cambiar aqui la interfaz y poner una preparada

        rp = Repository.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            goToMainActivity();
        } else {
            startSignIn();
        }
    }

    private void startSignIn() {
        Log.d("TEST_LOGIN", "startSignIn");
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.logo)
                .setTheme(R.style.Base_Theme_Crosswarr)
                .build();

        Log.d("TEST_LOGIN", "BEFORE signInLauncher");
        signInLauncher.launch(signInIntent);
        Log.d("TEST_LOGIN", "AFTER signInLauncher");
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        Log.d("TEST_LOGIN", "onSignInResult");

        if (result.getResultCode() == RESULT_OK) {
            Log.d("TEST_LOGIN", "USER OK onSignInResult");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                // Delegamos la lógica a nuestra nueva función segura
                verifyUserDocumentAndGo(user);
            }

        } else {
            Log.d("TEST_LOGIN", "USER ERROR onSignInResult: " + result.getResultCode());
            IdpResponse response = result.getIdpResponse();
            if (response != null && response.getError() != null) {
                Log.e("TEST_LOGIN", "ERROR INTERNO DE FIREBASE: ", response.getError());
            }
            Toast.makeText(this, "Error o inicio de sesión cancelado.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TEST_LOGIN", "onStart");

      /*  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            goToMainActivity();
        } else {
            startSignIn();
        }*/


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TEST_LOGIN", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TEST_LOGIN", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TEST_LOGIN", "onDestroy");
    }
    private void verifyUserDocumentAndGo(FirebaseUser user) {
        com.yinya.crosswarr.network.FirebaseService.getInstance().getDocument("crosswarr", user.getUid(), new com.yinya.crosswarr.network.IFirebaseCallback() {
            @Override
            public void onSuccess(java.util.Map<String, Object> dataFromFirebase) {
                // El documento existe.
                Log.d("TEST_LOGIN", "Documento encontrado, navegando a Main.");
                goToMainActivity();
            }

            @Override
            public void onFailure(Exception e) {
                // El documento NO existe
                Log.d("TEST_LOGIN", "Documento NO encontrado. Creándolo ahora...");

                String name = user.getDisplayName() != null ? user.getDisplayName() : "";
                String photoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";

                UserData userData = new UserData(
                        user.getUid(),
                        user.getEmail(),
                        name,
                        photoUrl,
                        "user", // Rol por defecto
                        com.google.firebase.Timestamp.now(),
                        "", // Token notificaciones
                        new java.util.HashMap<>(), // Settings vacío (MAPA)
                        new java.util.ArrayList<>() // TODO: Comprobar que no de error, si da cambiar a java.util.HashMap<>().
                );



                // Creamos el usuario en Firestore
                rp.createUser(userData);

                // Navegamos al Main. Firestore guarda en caché local inmediatamente, así que no habrá crasheo.
                goToMainActivity();
            }
        });
    }

    private void goToMainActivity() {
        Log.d("TEST_LOGIN", "goToMainActivity");
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}

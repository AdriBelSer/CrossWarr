package com.yinya.crosswarr;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.yinya.crosswarr.databinding.ActivityMainBinding;

import android.annotation.SuppressLint;
import androidx.appcompat.view.menu.MenuBuilder;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private NavController navController;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.logoutBtn.setOnClickListener(this::logoutSession);

        // 1. Obtener el NavHostFragment a través del Gestor de Fragmentos
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHostFragment);

        // 2. Extraer el NavController de ese fragmento
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        // 3. Configurar la barra superior (Toolbar) para que cambie de título automáticamente
        NavigationUI.setupWithNavController(binding.mainAppbar, navController);

        // 4. Configurar el menú inferior (BottomNavigationView) para que cambie de pantalla
        NavigationUI.setupWithNavController(binding.navView, navController);

        // --- MOSTRAR ICONOS EN LOS 3 PUNTITOS ---
        Menu menu = binding.mainAppbar.getMenu();
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
    }

    private void logoutSession(View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "Se ha salido de la sesión correctamente.", Toast.LENGTH_SHORT).show();
                        goToLogin();
                    }
                });


    }

    private void goToLogin() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();

    }
}
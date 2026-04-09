package com.yinya.crosswarr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.yinya.crosswarr.databinding.ActivityMainBinding;
import com.yinya.crosswarr.models.ExerciseData;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation();

        // Escuchar los clics del menú superior de los 3 puntitos
        binding.mainAppbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
                // Llama a tu méto-do de cerrar sesión
                logoutSession(binding.getRoot());
                return true;

            } else if (id == R.id.nav_exercices_edition) {
                // Viajamos al fragmento de edición de ejercicios
                navController.navigate(R.id.exercisesEdition);
                return true;

            } else if (id == R.id.nav_challenges_edition) {
                // Viajamos al fragmento de edición de challenges
                navController.navigate(R.id.challengesEdition);
                return true;

            } else if (id == R.id.nav_users_edition) {
                // Viajamos al fragmento de edición de usuarios
                navController.navigate(R.id.usersEdition);
                return true;

            } else if (id == R.id.nav_settings) {
                // TODO: Navegación de nav_setings
                navController.navigate(R.id.settings);
                return true;
            }

            return false;
        });
    }

    public void exerciseUserClicked(ExerciseData exercise, View view) {
        Bundle bundle = new Bundle();
        bundle.putString("name", exercise.getName()); // Pasa el nombre
        bundle.putString("description", exercise.getDescription()); // Pasa la descripción
        bundle.putString("type", exercise.getType()); // Pasa el tipo
        bundle.putString("image", exercise.getImage()); // Pasa la imagen
        bundle.putString("video", exercise.getVideo());
        bundle.putStringArrayList("materials", exercise.getMaterials());

        // Navegar al ExerciseDetailFragment con el Bundle
        Navigation.findNavController(view).navigate(R.id.exerciseDetail, bundle);
    }

    // Méto do para navegar en el menú
    // Para que se vean los iconos en el menu desplegable de los 3 puntitos primero hay que
    //"suprimir" el aviso de error de API restringida
    @SuppressLint("RestrictedApi")
    private void setupNavigation() {

        // Obtener el NavHostFragment a través del Gestor de Fragmentos
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHostFragment);

        // Extraer el NavController de ese fragmento
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        // Configurar la barra superior (Toolbar) para que cambie de título automáticamente
        NavigationUI.setupWithNavController(binding.mainAppbar, navController);

        // Configurar el menú inferior (BottomNavigationView) para que cambie de pantalla
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Mostrar iconos en los 3 puntitos -> Requiere @SuppressLint
        Menu menu = binding.mainAppbar.getMenu();
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Buscamos el AppBarLayout contenedor (el padre de tu mainAppbar)
            // NOTA: Revisa tu activity_main.xml. Si tu AppBarLayout no tiene ID, ponle uno
            // como android:id="@+id/appBarLayout" y úsalo aquí abajo:

            com.google.android.material.appbar.AppBarLayout appBarLayout = findViewById(R.id.main_appbar_layout);

            if (appBarLayout != null) {
                // El primer 'true' significa "expándete", el segundo 'true' significa "hazlo con animación suave"
                appBarLayout.setExpanded(true, true);
            }
        });
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
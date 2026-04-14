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
import com.yinya.crosswarr.models.ChallengeData;
import com.yinya.crosswarr.models.ExerciseData;
import com.yinya.crosswarr.repository.Repository;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private NavController navController;
    private boolean hasNavigatedToChallenge = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation();

        loadAndGoToTodaysChallengeAutomatically();

        // Escuchar los clics del menú superior de los 3 puntitos
        binding.mainAppbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
                // Llama a tu méto-do de cerrar sesión
                logoutSession(binding.getRoot());
                return true;

            } else if (id == R.id.nav_exercices_edition) {
                navController.navigate(R.id.exercisesEditionList);
                return true;

            } else if (id == R.id.nav_challenges_edition) {
                navController.navigate(R.id.challengesEditionList);
                return true;

            } else if (id == R.id.nav_users_edition) {
                navController.navigate(R.id.usersEditionList);
                return true;

            } else if (id == R.id.nav_settings) {
                // TODO: Navegación de nav_setings
                navController.navigate(R.id.settings);
                return true;
            }

            return false;
        });
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
    //TODO: PONER SKELETON EN LA VISTA PARA CUANDO ESTÁ CARGANDO EL CHALLENGE DEL DIA QUE NO SE VEA FEO

    private void loadAndGoToTodaysChallengeAutomatically() {
        // 1. Pedimos los desafíos al repositorio
        Repository.getInstance().fetchChallengesFromFirebase();

        // 2. Observamos la lista
        Repository.getInstance().getChallengesLiveData().observe(this, challenges -> {
            // Si ya navegamos una vez en esta sesión o la lista está vacía, no hacemos nada
            if (hasNavigatedToChallenge || challenges == null || challenges.isEmpty()) return;

            // 3. Obtenemos la fecha de hoy
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());
            String todayString = sdf.format(new java.util.Date());

            ChallengeData todayChallenge = null;

            // 4. Buscamos el reto que coincida con la fecha de activación
            for (ChallengeData ch : challenges) {
                if (ch.getActivationDate() != null) {
                    String chDate = sdf.format(ch.getActivationDate().toDate());
                    if (todayString.equals(chDate)) {
                        todayChallenge = ch;
                        break;
                    }
                }
            }

            // 5. Si lo encontramos, navegamos
            if (todayChallenge != null) {
                hasNavigatedToChallenge = true; // Marcamos que ya cumplimos la misión
                navigateToChallengeDetail(todayChallenge);
            }
        });
    }

    private void navigateToChallengeDetail(ChallengeData challenge) {
        Bundle bundle = new Bundle();
        bundle.putString("id", challenge.getId());
        bundle.putString("title", challenge.getTitle());
        bundle.putString("time", String.valueOf(challenge.getChallenteTime()));
        bundle.putString("exerciseSup", challenge.getExerciseSup());
        bundle.putString("exerciseInf", challenge.getExerciseInf());
        bundle.putString("exerciseCore", challenge.getExerciseCore());
        bundle.putString("state", String.valueOf(challenge.isState()));
        bundle.putString("repetitionSup", String.valueOf(challenge.getRepetitionSup()));
        bundle.putString("repetitionInf", String.valueOf(challenge.getRepetitionInf()));
        bundle.putString("repetitionCore", String.valueOf(challenge.getRepetitionCore()));
        bundle.putString("type", challenge.getType());

        // Navegación automática al fragmento reutilizado
        navController.navigate(R.id.challengeDetail, bundle);
    }

}
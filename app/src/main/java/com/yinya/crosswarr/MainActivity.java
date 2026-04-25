package com.yinya.crosswarr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.yinya.crosswarr.databinding.ActivityMainBinding;
import com.yinya.crosswarr.models.ChallengeData;
import com.yinya.crosswarr.network.FirebaseService;
import com.yinya.crosswarr.network.IFirebaseCallback;
import com.yinya.crosswarr.repository.Repository;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    android.util.Log.d("FCM", "Permiso de notificaciones concedido");
                } else {
                    android.util.Log.w("FCM", "Permiso de notificaciones denegado");
                }
            });
    ActivityMainBinding binding;
    private NavController navController;
    private boolean hasNavigatedToChallenge = false;
    private ChallengeData todayChallenge = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        PreferencesHelper prefs = new PreferencesHelper(this);
        //Aplicar tena
        if (prefs.isDarkMode()) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        }
        //Aplicar idioma
        String savedLang = prefs.getLanguage();
        String langCode = savedLang.equals("English") ? "en" : "es";
        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
                androidx.core.os.LocaleListCompat.forLanguageTags(langCode)
        );

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadTodaysChallenge();

        setupNavigation();

        askNotificationPermission();

        // Escuchar los clics del menú superior de los 3 puntitos
        binding.mainAppbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
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
                navController.navigate(R.id.userProfile);
                return true;
            }
            return false;
        });


    }

    private boolean onBottomNavItemClick(MenuItem item) {
        int id = item.getItemId();
        return handleBottomNavClick(id);
    }

    private boolean handleBottomNavClick(int itemId) {
        // Configuramos las opciones de navegación
        NavOptions options = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setRestoreState(false) // <--- ESTO evita que vuelva al fragmento de detalle
                .setPopUpTo(navController.getGraph().getStartDestinationId(), false, true)
                .build();

        if (itemId == R.id.nav_daily_challenge) {
            // 5. Si lo encontramos, navegamos
            if (todayChallenge != null) {
                hasNavigatedToChallenge = true; // Marcamos que ya cumplimos la misión
                navigateToDailyChallenge(todayChallenge, options);
            } else {
                navController.navigate(R.id.noChallenge, null, options);
            }
            return true;
        } else if (itemId == R.id.nav_exercises) {
            navController.navigate(R.id.nav_exercises, null, options);
            return true;
        } else if (itemId == R.id.nav_challenges) {
            navController.navigate(R.id.nav_challenges, null, options);
            return true;
        }

        return false;
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

        // Configurar la barra superior (Toolbar) para que cno se vea la flecha de "atrás"
        androidx.navigation.ui.AppBarConfiguration appBarConfiguration =
                new androidx.navigation.ui.AppBarConfiguration.Builder(
                        R.id.nav_daily_challenge,
                        R.id.noChallenge,
                        R.id.nav_exercises,
                        R.id.nav_challenges
                ).build();

        // 2. Vinculamos el AppBar pasándole nuestra configuración
        NavigationUI.setupWithNavController(binding.mainAppbar, navController, appBarConfiguration);

        // Configurar el menú inferior (BottomNavigationView) para que cambie de pantalla
        binding.navView.setOnItemSelectedListener(this::onBottomNavItemClick);

        // Mostrar iconos en los 3 puntitos -> Requiere @SuppressLint
        Menu menu = binding.mainAppbar.getMenu();
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }

        android.view.MenuItem adminOptions = menu.findItem(R.id.nav_admin_options);

        // 2. Obtenemos el usuario actual de Auth
        com.google.firebase.auth.FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            FirebaseService service = FirebaseService.getInstance();

            service.getDocument(service.COLLECTION_NAME, currentUser.getUid(), new IFirebaseCallback() {
                @Override
                public void onSuccess(Map<String, Object> dataFromFirebase) {
                    if (dataFromFirebase != null && dataFromFirebase.containsKey("role")) {
                        String role = (String) dataFromFirebase.get("role");

                        // Si el rol es exactamente "admin", mostramos el menú
                        if ("admin".equals(role)) {
                            if (adminOptions != null) {
                                adminOptions.setVisible(true);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    android.util.Log.e("MainActivity", "Error al validar rol de admin", e);
                }
            });
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
                        Toast.makeText(MainActivity.this, R.string.main_activity_logout_toast, Toast.LENGTH_SHORT).show();
                        goToLogin();
                    }
                });
    }

    private void goToLogin() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();

    }

    private void loadTodaysChallenge() {
        Repository.getInstance().fetchChallengesFromFirebase();
        Repository.getInstance().getChallengesLiveData().observe(this, challenges -> {
            if (hasNavigatedToChallenge || challenges == null || challenges.isEmpty()) return;
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());
            String todayString = sdf.format(new java.util.Date());
            for (ChallengeData ch : challenges) {
                if (ch.getActivationDate() != null) {
                    String chDate = sdf.format(ch.getActivationDate().toDate());
                    if (todayString.equals(chDate)) {
                        todayChallenge = ch;
                        handleBottomNavClick(R.id.nav_daily_challenge);
                        break;
                    }
                }
            }
            hasNavigatedToChallenge = true;
            handleBottomNavClick(R.id.nav_daily_challenge);
        });
    }

    private void navigateToDailyChallenge(ChallengeData challenge, NavOptions options) {
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

        navController.navigate(R.id.nav_daily_challenge, bundle, options);
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED &&
                !shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS))

            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
    }

}
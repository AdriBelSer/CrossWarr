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
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.yinya.crosswarr.databinding.ActivityMainBinding;
import com.yinya.crosswarr.databinding.GuideChallengeBinding;
import com.yinya.crosswarr.databinding.GuideExercisesBinding;
import com.yinya.crosswarr.databinding.GuideMenuSettingsBinding;
import com.yinya.crosswarr.databinding.GuideRecordsBinding;
import com.yinya.crosswarr.databinding.GuideWelcomeBinding;
import com.yinya.crosswarr.models.ChallengeData;
import com.yinya.crosswarr.network.FirebaseService;
import com.yinya.crosswarr.network.IFirebaseCallback;
import com.yinya.crosswarr.repository.Repository;

import java.util.Map;

/**
 * Actividad principal y corazón estructural de la aplicación.
 * Aloja el contenedor de navegación (NavHostFragment) y gestiona los componentes globales de la UI
 * como la barra superior (Toolbar), el menú de navegación inferior (BottomNavigationView) y el
 * sistema de tutorial interactivo (Guía). También es responsable de cargar las preferencias del usuario
 * (idioma, tema), gestionar permisos y validar los roles (Admin/User).
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Lanzador de resultados para solicitar el permiso de notificaciones push
     * requerido a partir de Android 13 (Tiramisu).
     */
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            android.util.Log.d("FCM", "Permiso de notificaciones concedido");
        } else {
            android.util.Log.w("FCM", "Permiso de notificaciones denegado");
        }
    });
    // Bindings principales y de las capas de la guía (Tutorial)
    ActivityMainBinding binding;
    GuideChallengeBinding guideChallengesBinding;
    GuideExercisesBinding guideExercisesBinding;
    GuideMenuSettingsBinding guideMenuSettingsBinding;
    GuideRecordsBinding guideRecordsBinding;
    GuideWelcomeBinding guideWelcomeBinding;

    private Map<Integer, float[]> menuItemsInfo = new java.util.HashMap<>();
    private boolean reproducingGuide = false;

    private NavController navController;

    // Control de flujo para el desafío del día
    private boolean hasNavigatedToChallenge = false;
    private ChallengeData todayChallenge = null;

    /**
     * Invocado cuando se crea la actividad.
     * Inicializa las preferencias de tema e idioma, infla la UI, solicita permisos
     * e inicia la búsqueda del reto del día actual en la base de datos.
     *
     * @param savedInstanceState Estado anterior de la actividad, si existe.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        PreferencesHelper prefs = new PreferencesHelper(this);

        // Aplicar tema (Modo Oscuro / Claro) según las preferencias locales
        if (prefs.isDarkMode()) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        }
        // Aplicar idioma de la aplicación (Español / Inglés)
        String savedLang = prefs.getLanguage();
        String langCode = savedLang.equals("English") ? "en" : "es";
        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(androidx.core.os.LocaleListCompat.forLanguageTags(langCode));

        setBindings();
        setContentView(binding.getRoot());

        loadTodaysChallenge();

        setupNavigation();

        askNotificationPermission();

        // Escuchar los clics del menú de opciones (3 puntitos de la esquina superior derecha)
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

        getToolbarInfoItemSize();

        // Determinar si es la primera vez que el usuario entra para mostrarle el tutorial
        boolean showGuide = prefs.getSavedNeedGuide();
        if (showGuide) initializeGuide();
    }

    /**
     * Calcula dinámicamente la posición y tamaño del ícono de ajustes en la Toolbar
     * para enfocarlo correctamente durante el tutorial.
     */
    private void getToolbarInfoItemSize() {
        MaterialToolbar mToolbar = findViewById(R.id.main_appbar);

        mToolbar.post(() -> {
            Menu mToolbarMenu = mToolbar.getMenu();
            MenuItem menuItem = mToolbarMenu.findItem(R.id.nav_settings);

            if (menuItem != null) {
                View infoItem = mToolbar.findViewById(menuItem.getItemId());

                if (infoItem != null) {
                    float itemWidth = infoItem.getWidth();
                    int[] location = new int[2];
                    infoItem.getLocationOnScreen(location);
                    float[] itemInfo = {0, itemWidth, location[0], infoItem.getY()};
                    menuItemsInfo.put(R.id.nav_settings, itemInfo); // Guardamos por ID
                } else {
                    // Si está oculto en los 3 puntitos, apuntamos a la esquina superior derecha a mano
                    float[] itemInfo = {0, 100f, mToolbar.getWidth() - 150f, mToolbar.getY() + 20f};
                    menuItemsInfo.put(R.id.nav_settings, itemInfo);
                }
            }
        });
    }

    /**
     * Vincula todas las vistas y sub-vistas (incluidas las capas del tutorial)
     * utilizando View Binding.
     */
    private void setBindings() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        guideWelcomeBinding = binding.includeLayoutWelcome;
        guideChallengesBinding = binding.includeLayoutChallenge;
        guideExercisesBinding = binding.includeLayoutExercises;
        guideRecordsBinding = binding.includeLayoutRecords;
        guideMenuSettingsBinding = binding.includeLayoutSettings;
    }

    // --- MÉTODOS DEL TUTORIAL (GUÍA INTERACTIVA) ---

    private void setWelcomeGuideOnClickListeners() {
        guideWelcomeBinding.btnStartGuide.setOnClickListener(view -> {
            guideWelcomeBinding.guideWelcome.setVisibility(View.GONE);
            onStartGuide(); // Inicia la guía
        });
        guideWelcomeBinding.btnExitGuide.setOnClickListener(this::onExitGuide);
    }

    private void setChallengesGuideOnClickListeners() {
        guideChallengesBinding.btnExitGuideChallenges.setOnClickListener(this::onExitGuide);
        guideChallengesBinding.btnNextGuideChallenges.setOnClickListener(view -> {
            guideChallengesBinding.guideChallengeLayout.setVisibility(View.GONE);
            goToExercises();
        });
    }

    private void setExercisesGuideOnClickListeners() {
        guideExercisesBinding.btnExitGuideExercises.setOnClickListener(this::onExitGuide);
        guideExercisesBinding.btnNextGuideExercises.setOnClickListener(v -> {
            guideExercisesBinding.guideExercisesLayout.setVisibility(View.GONE);
            goToRecords();
        });
    }

    private void setRecordsGuideClickListeners() {
        guideRecordsBinding.btnExitGuideRecords.setOnClickListener(this::onExitGuide);
        guideRecordsBinding.btnNextGuideRecords.setOnClickListener(v -> {
            guideRecordsBinding.guideRecordsLayout.setVisibility(View.GONE);
            goToMenuSettings();
        });
    }


    private void setMenuSettingsGuideClickListeners() {
        guideMenuSettingsBinding.btnExitGuideMenuSettings.setOnClickListener(this::onExitGuide);
    }

    private void initializeGuide() {
        setWelcomeGuideOnClickListeners();
        reproducingGuide = true;
        guideWelcomeBinding.getRoot().setVisibility(View.VISIBLE);
    }

    private void onStartGuide() {
        guideWelcomeBinding.getRoot().setVisibility(View.GONE);
        goToChallenges();
    }

    /**
     * Finaliza la visualización de la guía interactiva y guarda en las preferencias
     * que el usuario ya no necesita verla en futuros inicios de sesión.
     *
     * @param view La vista que desencadenó el evento de salida.
     */
    private void onExitGuide(View view) {
        PreferencesHelper prefs = new PreferencesHelper(this);
        prefs.saveNeedGuide(false);

        guideWelcomeBinding.guideWelcome.setVisibility(View.GONE);
        guideChallengesBinding.guideChallengeLayout.setVisibility(View.GONE);
        guideExercisesBinding.guideExercisesLayout.setVisibility(View.GONE);
        guideRecordsBinding.guideRecordsLayout.setVisibility(View.GONE);
        guideMenuSettingsBinding.guideMenuSettingsLayout.setVisibility(View.GONE);

        handleBottomNavClick(R.id.nav_daily_challenge);
        reproducingGuide = false;
    }


    private void goToChallenges() {
        guideChallengesBinding.guideChallengeLayout.setVisibility(View.VISIBLE);
        setChallengesGuideOnClickListeners();
        handleBottomNavClick(R.id.nav_daily_challenge);
    }

    private void goToExercises() {
        guideExercisesBinding.guideExercisesLayout.setVisibility(View.VISIBLE);
        setExercisesGuideOnClickListeners();
        handleBottomNavClick(R.id.nav_exercises);
    }

    private void goToRecords() {
        guideRecordsBinding.guideRecordsLayout.setVisibility(View.VISIBLE);
        setRecordsGuideClickListeners();
        handleBottomNavClick(R.id.nav_challenges);
    }

    private void goToMenuSettings() {
        guideMenuSettingsBinding.guideMenuSettingsLayout.setVisibility(View.VISIBLE);
        setMenuSettingsGuideClickListeners();
        navController.navigate(R.id.userProfile);
    }

    // --- MÉTODOS DE NAVEGACIÓN ---

    private boolean onBottomNavItemClick(MenuItem item) {
        int id = item.getItemId();
        return handleBottomNavClick(id);
    }

    /**
     * Maneja los clics en la barra de navegación inferior aplicando reglas de negocio.
     * Limpia la pila de retroceso (Backstack) para evitar acumulaciones de fragmentos
     * y rutea al usuario correctamente dependiendo de si hay un reto diario disponible o no.
     *
     * @param itemId El ID del elemento del menú seleccionado.
     * @return true si la navegación fue gestionada con éxito, false en caso contrario.
     */
    private boolean handleBottomNavClick(int itemId) {
        // Configuramos opciones de navegación para no acumular fragmentos en memoria
        NavOptions options = new NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(false) // <--- ESTO evita que vuelva al fragmento de detalle
                .setPopUpTo(navController.getGraph().getStartDestinationId(), false, true).build();

        if (itemId == R.id.nav_daily_challenge) {
            // Evaluamos si el administrador ha publicado un reto para hoy
            if (todayChallenge != null) {
                hasNavigatedToChallenge = true;
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

    /**
     * Configura el componente de navegación principal (Jetpack Navigation).
     * Vincula el BottomNavigationView y la Toolbar con el NavController, y añade lógica
     * dinámica para validar el rol del usuario contra Firebase y mostrar las opciones de administrador.
     * * Se usa SuppressLint("RestrictedApi") porque forzar la visibilidad de íconos en el
     * menú de desbordamiento (overflow) usa APIs internas de AndroidX.
     */
    @SuppressLint("RestrictedApi")
    private void setupNavigation() {

        // Obtener el NavHostFragment a través del Gestor de Fragmentos
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);

        // Extraer el NavController de ese fragmento
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        // Definir qué destinos son de nivel superior (para que no muestren flecha de "atrás")
        androidx.navigation.ui.AppBarConfiguration appBarConfiguration = new androidx.navigation.ui.AppBarConfiguration.Builder(R.id.nav_daily_challenge, R.id.noChallenge, R.id.nav_exercises, R.id.nav_challenges).build();

        // Vincular el AppBar pasándole nuestra configuración
        NavigationUI.setupWithNavController(binding.mainAppbar, navController, appBarConfiguration);

        // Configurar el menú inferior para responder a los clics
        binding.navView.setOnItemSelectedListener(this::onBottomNavItemClick);

        // Forzar la visualización de iconos en el menú de los 3 puntitos
        Menu menu = binding.mainAppbar.getMenu();
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }

        android.view.MenuItem adminOptions = menu.findItem(R.id.nav_admin_options);

        // LÓGICA DE ROLES: Validar si el usuario actual tiene permisos de Administrador
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
        // Listener global de cambio de destino para auto-expandir la barra superior y marcar el ítem inferior
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            AppBarLayout appBarLayout = findViewById(R.id.main_appbar_layout);

            if (appBarLayout != null) {
                // El primer 'true' significa "expándete", el segundo 'true' significa "hazlo con animación suave"
                appBarLayout.setExpanded(true, true);
            }

            Menu bottomMenu = binding.navView.getMenu();
            MenuItem item = bottomMenu.findItem(destination.getId());
            if (item != null) {
                item.setChecked(true);
            }
        });
    }

    /**
     * Cierra la sesión activa en Firebase Auth, muestra un mensaje de éxito y devuelve
     * al usuario a la pantalla de Login, impidiendo que pueda volver atrás.
     *
     * @param view La vista que disparó la acción (generalmente un botón del menú).
     */
    private void logoutSession(View view) {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this, R.string.main_activity_logout_toast, Toast.LENGTH_SHORT).show();
                goToLogin();
            }
        });
    }

    private void goToLogin() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish(); // Destruimos la Main para que no quede en la pila (Backstack)

    }

    /**
     * Observa la lista de desafíos proveniente del Repositorio y busca si existe alguno
     * programado específicamente para la fecha de hoy. Si lo encuentra, actualiza la
     * variable de estado y redirige al usuario automáticamente a la pantalla del reto.
     */
    private void loadTodaysChallenge() {
        Repository repo = Repository.getInstance();
        repo.fetchChallengesFromFirebase();
        repo.getChallengesLiveData().observe(this, new androidx.lifecycle.Observer<java.util.ArrayList<com.yinya.crosswarr.models.ChallengeData>>() {
            @Override
            public void onChanged(java.util.ArrayList<com.yinya.crosswarr.models.ChallengeData> challenges) {
                // Si ya procesamos la navegación o no hay datos, abortamos
                if (hasNavigatedToChallenge || challenges == null || challenges.isEmpty()) return;

                // Obtenemos la fecha actual en formato texto (ej: 25-04-2026)
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());
                String todayString = sdf.format(new java.util.Date());

                for (com.yinya.crosswarr.models.ChallengeData ch : challenges) {
                    if (ch.getActivationDate() != null) {
                        String chDate = sdf.format(ch.getActivationDate().toDate());

                        // Si la fecha del reto coincide exactamente con el día de hoy
                        if (todayString.equals(chDate)) {
                            todayChallenge = ch;
                            handleBottomNavClick(R.id.nav_daily_challenge);
                            repo.getChallengesLiveData().removeObserver(this);
                            hasNavigatedToChallenge = true;
                            return;
                        }
                    }
                }

                // Si recorrimos toda la lista y no hubo match, navegamos a la pantalla de "No hay reto"
                hasNavigatedToChallenge = true;
                handleBottomNavClick(R.id.nav_daily_challenge);
                repo.getChallengesLiveData().removeObserver(this);
            }
        });
    }

    /**
     * Empaqueta la información del desafío del día en un Bundle y ejecuta la navegación
     * hacia el fragmento DailyChallenge usando el NavController.
     *
     * @param challenge El objeto con la información del reto a mostrar.
     * @param options   Opciones de comportamiento de navegación (limpieza de backstack).
     */
    private void navigateToDailyChallenge(ChallengeData challenge, NavOptions options) {
        Bundle bundle = new Bundle();
        bundle.putString("id", challenge.getId());
        bundle.putString("title", challenge.getTitle());
        bundle.putString("time", String.valueOf(challenge.getChallengeTime()));
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

    /**
     * Solicita activamente al usuario el permiso para mostrar notificaciones push.
     * Este paso es un requisito técnico indispensable a partir de Android 13 (API 33 / Tiramisu).
     */
    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED && !shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS))

            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
    }

}
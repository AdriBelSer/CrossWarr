package com.yinya.crosswarr;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yinya.crosswarr.databinding.FragmentDailyChallengeBinding;
import com.yinya.crosswarr.network.FirebaseService;
import com.yinya.crosswarr.network.FirebaseUserService;
import com.yinya.crosswarr.network.IFirebaseCallback;

import java.util.Map;

/**
 * Fragmento dedicado exclusivamente a la pantalla del "Reto Diario" o desafío destacado.
 * Su estructura es similar a la vista de detalles general, pero incluye reglas de negocio
 * más estrictas, como el bloqueo de la navegación hacia atrás para evitar que el usuario
 * abandone la pantalla por accidente mientras el cronómetro está en marcha.
 */
public class DailyChallenge extends Fragment {
    long totalTimeInMillis;
    private FragmentDailyChallengeBinding binding;
    private FirebaseService firebaseService;
    private FirebaseUserService firebaseUserService;
    private boolean isChronometerRunning = false;

    // Aquí guardaremos el tiempo real que ha entrenado
    private long timeSpentInMillis = 0;

    // Reproductores para los efectos de sonido
    private MediaPlayer mpMinute;
    private MediaPlayer mpFinish;

    /**
     * Invocado para inflar el diseño XML asociado a este fragmento utilizando View Binding.
     *
     * @param inflater           El objeto LayoutInflater utilizado para inflar las vistas en el contexto.
     * @param container          El ViewGroup padre en el que se insertará la vista del fragmento.
     * @param savedInstanceState Si no es null, este fragmento está siendo reconstruido.
     * @return La vista raíz (View) del diseño inflado.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDailyChallengeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Invocado inmediatamente después de que onCreateView ha retornado.
     * Configura la intercepción del botón hardware "atrás", inicializa los sonidos,
     * recupera los argumentos pasados a la pantalla y configura la UI del desafío.
     *
     * @param view               La Vista devuelta por onCreateView().
     * @param savedInstanceState Si no es null, el fragmento está siendo reconstruido.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // LÓGICA DE NEGOCIO: Bloqueo del botón físico "Atrás" del dispositivo.
        // Se añade un callback vacío para absorber el evento y evitar que el usuario
        // salga del Reto Diario por accidente o intente saltárselo.
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Se deja intencionalmente vacío para que no haga nada al pulsar "atrás"
            }
        });

        // Inicialización de los sonidos de notificación
        mpMinute = MediaPlayer.create(getContext(), R.raw.gong_emom);
        mpFinish = MediaPlayer.create(getContext(), R.raw.gong_final);

        // Extracción de los datos del desafío desde los argumentos
        if (getArguments() != null) {
            String id = getArguments().getString("id");
            String title = getArguments().getString("title");
            String creationDate = getArguments().getString("creationDate");
            String activationDate = getArguments().getString("activationDate");
            String time = getArguments().getString("time");
            String exerciseSup = getArguments().getString("exerciseSup");
            String exerciseInf = getArguments().getString("exerciseInf");
            String exerciseCore = getArguments().getString("exerciseCore");
            String state = getArguments().getString("state");
            String repetitionSup = getArguments().getString("repetitionSup");
            String repetitionInf = getArguments().getString("repetitionInf");
            String repetitionCore = getArguments().getString("repetitionCore");
            String type = getArguments().getString("type");

            // Asignar Título
            if (binding.tvChallengeTitleFragmentDailyChallenge != null) {
                binding.tvChallengeTitleFragmentDailyChallenge.setText(title);
            }

            // Asignar Imagen con lógica condicional según la duración del reto
            if (binding.ivFragmentDailyChallenge != null && time != null) {
                if ("10".equals(time)) {
                    binding.ivFragmentDailyChallenge.setImageResource(R.drawable.photo_10);
                } else if ("15".equals(time)) {
                    binding.ivFragmentDailyChallenge.setImageResource(R.drawable.photo_15);
                } else if ("20".equals(time)) {
                    binding.ivFragmentDailyChallenge.setImageResource(R.drawable.photo_20);
                } else {
                    binding.ivFragmentDailyChallenge.setImageResource(R.drawable.photo_20);
                }
            }

            // Construir la descripción dinámicamente según la modalidad (AMRAP, FT, EMOM)
            if (binding.tvChallengeDescriptionFragmentDailyChallenge != null && type != null) {
                String description = "";
                String safeType = type.trim().toLowerCase();

                if ("amrap".equals(safeType)) {
                    description = requireContext().getString(R.string.challenge_detail_description_amrap, safeType.toUpperCase(), exerciseSup, repetitionSup, exerciseInf, repetitionInf, exerciseCore, repetitionCore, time);

                } else if ("ft".equals(safeType)) {
                    description = requireContext().getString(R.string.challenge_detail_description_ft, safeType.toUpperCase(), exerciseSup, repetitionSup, exerciseInf, repetitionInf, exerciseCore, repetitionCore, time, time);

                } else if ("emom".equals(safeType)) {
                    description = requireContext().getString(R.string.challenge_detail_description_emom, safeType.toUpperCase(), exerciseSup, repetitionSup, exerciseInf, repetitionInf, exerciseCore, repetitionCore, time);

                } else {
                    description = requireContext().getString(R.string.challenge_detail_description_error, type);
                }

                binding.tvChallengeDescriptionFragmentDailyChallenge.setText(description);
            }

            // Asignar Duración estimada
            if (binding.tvDurationDescriptionFragmentDailyChallenge != null) {
                binding.tvDurationDescriptionFragmentDailyChallenge.setText(requireContext().getString(R.string.challenge_detail_duration, time));
            }

            // Lógica de estados del botón de inicio/parada
            if (binding.btnStartFragmentDailyChallenge != null && time != null) {
                binding.btnStartFragmentDailyChallenge.setOnClickListener(v -> {

                    if (!isChronometerRunning) {

                        // Estado 1: Iniciar el desafio
                        totalTimeInMillis = Long.parseLong(time) * 60 * 1000;

                        if (binding.chronometerFragmentDailyChallenge != null) {
                            binding.chronometerFragmentDailyChallenge.setVisibility(android.view.View.VISIBLE);

                            // Mantener pantalla encendida durante el entrenamiento
                            requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            initChronometer(id, type);

                            // Cambiamos el texto del botón para permitir la parada anticipada
                            binding.btnStartFragmentDailyChallenge.setText(requireContext().getString(R.string.challenge_detail_btn_stop));
                        }

                        timeSpentInMillis = totalTimeInMillis;
                    } else {
                        // Estado 2: El usuario detiene el desafío antes de tiempo
                        if (binding.chronometerFragmentDailyChallenge != null) {
                            binding.chronometerFragmentDailyChallenge.stop();
                            isChronometerRunning = false;

                            // Ya no es necesario forzar la pantalla encendida
                            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                            // Calculamos cuánto tiempo sobraba en el cronómetro
                            long timeRemaining = binding.chronometerFragmentDailyChallenge.getBase() - android.os.SystemClock.elapsedRealtime();

                            // El tiempo invertido es el Total menos lo que sobró
                            timeSpentInMillis = totalTimeInMillis - timeRemaining;
                            onChallengeFinished(id);
                        }
                    }


                });
            }
        }
    }

    /**
     * Configura y arranca el cronómetro en modo cuenta regresiva.
     * Gestiona los eventos temporales como alertas sonoras cada minuto (para modo EMOM)
     * y el cierre automático cuando el tiempo llega a cero.
     *
     * @param id   El identificador único del desafío diario.
     * @param type La modalidad del entrenamiento (usado para aplicar lógicas de sonido específicas).
     */
    private void initChronometer(String id, String type) {
        try {
            binding.chronometerFragmentDailyChallenge.setCountDown(true);

            // La base es el momento actual + el tiempo total del desafío
            binding.chronometerFragmentDailyChallenge.setBase(android.os.SystemClock.elapsedRealtime() + totalTimeInMillis);

            binding.chronometerFragmentDailyChallenge.setOnChronometerTickListener(chronometer -> {

                long timeRemaining = chronometer.getBase() - android.os.SystemClock.elapsedRealtime();

                // Calculamos el tiempo que ya ha pasado
                long timeElapsedMillis = totalTimeInMillis - timeRemaining;
                long secondsElapsed = timeElapsedMillis / 1000;

                // Lógica EMOM: Sonar cada minuto (60, 120, 180 segundos...)
                if ("emom".equals(type) && secondsElapsed > 0 && secondsElapsed % 60 == 0) {
                    if (mpMinute != null) {
                        mpMinute.start();
                    }
                }

                // Lógica de Fin de Desafío
                if (timeRemaining <= 0) {
                    chronometer.stop();
                    chronometer.setText("00:00");
                    isChronometerRunning = false;
                    onChallengeFinished(id);

                    if (mpFinish != null) {
                        mpFinish.start();
                    }

                    android.widget.Toast.makeText(requireContext(), R.string.challenge_detail_toast_completed, android.widget.Toast.LENGTH_SHORT).show();
                }

            });

            binding.chronometerFragmentDailyChallenge.start();
            isChronometerRunning = true;
        } catch (NumberFormatException e) {
            android.util.Log.e("ChallengeDetail", "Error al convertir el tiempo", e);
        }
    }

    /**
     * Procesa la finalización del Reto Diario.
     * Bloquea la UI, registra el tiempo en Firebase y, tras un breve lapso,
     * fuerza la navegación a la lista de desafíos generales (`nav_challenges`).
     *
     * @param id El identificador único del desafío que se ha completado.
     */
    private void onChallengeFinished(String id) {
        binding.btnStartFragmentDailyChallenge.setText(requireContext().getText(R.string.challenge_detail_challenge_finised));
        binding.btnStartFragmentDailyChallenge.setEnabled(false); // Lo bloqueamos para que no le dé más
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseService = FirebaseService.getInstance();
        firebaseUserService = FirebaseUserService.getInstance(firebaseService);

        // Guardamos el registro en Firebase
        firebaseUserService.upsertChallengeTime(user.getUid(), id, (int) timeSpentInMillis, new IFirebaseCallback() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                // Convertimos los milisegundos a minutos y segundos para mostrarle un Toast de enhorabuena
                int minutesCompleted = (int) (timeSpentInMillis / 1000) / 60;
                int secondsCompleted = (int) (timeSpentInMillis / 1000) % 60;

                String timeMsg = requireContext().getString(R.string.challenge_detail_toast_time_trained, minutesCompleted, secondsCompleted);
                android.widget.Toast.makeText(requireContext(), timeMsg, android.widget.Toast.LENGTH_LONG).show();

                // Esperamos 4 segundos (4000ms) para que lea el mensaje y luego forzamos la navegación
                // explícitamente a la pestaña de retos (en lugar de solo usar popBackStack)
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    if (getView() != null) { // Comprobación de seguridad por si ya se ha ido
                        androidx.navigation.Navigation.findNavController(requireView()).navigate(R.id.nav_challenges);
                    }
                }, 4000);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), R.string.challenge_detail_error_saving_time, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Invocado cuando la vista del fragmento va a ser destruida.
     * Se liberan los bloqueos de pantalla, los reproductores de audio y las
     * referencias de View Binding para prevenir fugas de memoria.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        // Limpieza segura de recursos de audio
        if (mpMinute != null) {
            mpMinute.release();
            mpMinute = null;
        }
        if (mpFinish != null) {
            mpFinish.release();
            mpFinish = null;
        }
        binding = null;
    }
}
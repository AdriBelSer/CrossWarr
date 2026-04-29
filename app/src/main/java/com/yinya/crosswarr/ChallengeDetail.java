package com.yinya.crosswarr;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yinya.crosswarr.databinding.FragmentChallengeDetailBinding;
import com.yinya.crosswarr.network.FirebaseService;
import com.yinya.crosswarr.network.FirebaseUserService;
import com.yinya.crosswarr.network.IFirebaseCallback;

import java.util.Map;

/**
 * Fragmento encargado de mostrar los detalles completos de un desafío y gestionar su ejecución.
 * Actúa como la pantalla principal de entrenamiento, permitiendo al usuario iniciar un temporizador,
 * escuchar alertas sonoras según la modalidad (ej. EMOM), y registrar automáticamente su tiempo
 * en la base de datos una vez finalizado o detenido el entrenamiento.
 */
public class ChallengeDetail extends Fragment {
    long totalTimeInMillis;
    private FragmentChallengeDetailBinding binding;
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
     * @param savedInstanceState Si no es null, este fragmento está siendo reconstruido a partir de un estado guardado previamente.
     * @return La vista raíz (View) del diseño inflado.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChallengeDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Invocado inmediatamente después de que onCreateView ha retornado.
     * Aquí se inicializan los MediaPlayers, se recogen los argumentos pasados al fragmento (Safe Args/Bundle),
     * se construyen los textos descriptivos dinámicamente según el tipo de reto y se configura
     * el comportamiento del botón de inicio/parada.
     *
     * @param view               La Vista devuelta por onCreateView().
     * @param savedInstanceState Si no es null, el fragmento está siendo reconstruido.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicialización de los sonidos de notificación
        mpMinute = MediaPlayer.create(getContext(), R.raw.gong_emom);
        mpFinish = MediaPlayer.create(getContext(), R.raw.gong_final);

        // Obtener datos empaquetados desde la vista anterior
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
            if (binding.tvChallengeTitleFragmentChallengeDetail != null) {
                binding.tvChallengeTitleFragmentChallengeDetail.setText(title);
            }

            // Asignar Imagen con Null Safety según la duración del desafío
            if (binding.ivFragmentChallengeDetail != null && time != null) {
                if ("10".equals(time)) {
                    binding.ivFragmentChallengeDetail.setImageResource(R.drawable.photo_10);
                } else if ("15".equals(time)) {
                    binding.ivFragmentChallengeDetail.setImageResource(R.drawable.photo_15);
                } else if ("20".equals(time)) {
                    binding.ivFragmentChallengeDetail.setImageResource(R.drawable.photo_20);
                } else {
                    binding.ivFragmentChallengeDetail.setImageResource(R.drawable.photo_20);
                }
            }

            // Asignar Descripción con Formato Seguro y dinámico según la modalidad
            if (binding.tvChallengeDescriptionFragmentChallengeDetail != null && type != null) {
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

                binding.tvChallengeDescriptionFragmentChallengeDetail.setText(description);
            }

            // Asignar Duración
            if (binding.tvDurationDescriptionFragmentChallengeDetail != null) {
                binding.tvDurationDescriptionFragmentChallengeDetail.setText(requireContext().getString(R.string.challenge_detail_duration, time));
            }

            // Lógica principal de los estados del botón de entrenamiento (Start / Stop)
            if (binding.btnStartFragmentChallengeDetail != null && time != null) {
                binding.btnStartFragmentChallengeDetail.setOnClickListener(v -> {

                    if (!isChronometerRunning) {

                        // Estado 1: Iniciar el desafío
                        totalTimeInMillis = Long.parseLong(time) * 60 * 1000;

                        if (binding.chronometerFragmentChallengeDetail != null) {
                            binding.chronometerFragmentChallengeDetail.setVisibility(android.view.View.VISIBLE);

                            // Mantiene la pantalla encendida mientras el usuario entrena
                            requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            initChronometer(id, type);

                            // Cambiamos el botón para que ahora sirva para parar
                            binding.btnStartFragmentChallengeDetail.setText(requireContext().getString(R.string.challenge_detail_btn_stop));
                        }
                        // Por defecto asumimos que gastará el tiempo completo (se sobrescribe si para antes)
                        timeSpentInMillis = totalTimeInMillis;
                    } else {
                        // Estado 2: El usuario para el desafío antes de tiempo
                        if (binding.chronometerFragmentChallengeDetail != null) {
                            binding.chronometerFragmentChallengeDetail.stop();
                            isChronometerRunning = false;

                            // Ya no es necesario mantener la pantalla encendida a la fuerza
                            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                            // Calculamos cuánto tiempo sobraba en el cronómetro
                            long timeRemaining = binding.chronometerFragmentChallengeDetail.getBase() - android.os.SystemClock.elapsedRealtime();

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
     * Configura y arranca el cronómetro en modo de cuenta regresiva (CountDown).
     * Establece un listener que se ejecuta cada segundo (Tick) para manejar los eventos
     * especiales, como los avisos de minuto en la modalidad EMOM o la finalización del tiempo.
     *
     * @param id   Identificador único del desafío (necesario para guardar los resultados posteriormente).
     * @param type Tipo de modalidad del entrenamiento (ej. "emom", "amrap") para las reglas de sonido.
     */
    private void initChronometer(String id, String type) {
        try {
            binding.chronometerFragmentChallengeDetail.setCountDown(true);

            // La base es el momento actual + el tiempo total del desafío
            binding.chronometerFragmentChallengeDetail.setBase(android.os.SystemClock.elapsedRealtime() + totalTimeInMillis);

            binding.chronometerFragmentChallengeDetail.setOnChronometerTickListener(chronometer -> {

                long timeRemaining = chronometer.getBase() - android.os.SystemClock.elapsedRealtime();

                // Calculamos el tiempo que ya ha pasado
                long timeElapsedMillis = totalTimeInMillis - timeRemaining;
                long secondsElapsed = timeElapsedMillis / 1000;

                // Lógica de avisos para EMOM: Sonar cada minuto (60, 120, 180 segundos...)
                if ("emom".equals(type) && secondsElapsed > 0 && secondsElapsed % 60 == 0) {
                    if (mpMinute != null) {
                        mpMinute.start();
                    }
                }

                // Lógica de Fin de Desafío por agotamiento de tiempo
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

            binding.chronometerFragmentChallengeDetail.start();
            isChronometerRunning = true;
        } catch (NumberFormatException e) {
            android.util.Log.e("ChallengeDetail", "Error al convertir el tiempo", e);
        }
    }

    /**
     * Procesa la finalización del entrenamiento, ya sea por haber agotado el tiempo o
     * por detención manual. Se encarga de actualizar la UI (bloqueo de botones), comunicarse
     * con Firebase para registrar el tiempo invertido y programar la navegación automática de salida.
     *
     * @param id El identificador único del desafío que acaba de concluir.
     */
    private void onChallengeFinished(String id) {
        binding.btnStartFragmentChallengeDetail.setText(requireContext().getText(R.string.challenge_detail_challenge_finised));
        binding.btnStartFragmentChallengeDetail.setEnabled(false); // Lo bloqueamos para que no le dé más
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseService = FirebaseService.getInstance();
        firebaseUserService = FirebaseUserService.getInstance(firebaseService);

        // Guardar o actualizar el tiempo del usuario en la base de datos
        firebaseUserService.upsertChallengeTime(user.getUid(), id, (int) timeSpentInMillis, new IFirebaseCallback() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                // Convertimos los milisegundos a minutos y segundos para mostrarle un Toast de enhorabuena
                int minutesCompleted = (int) (timeSpentInMillis / 1000) / 60;
                int secondsCompleted = (int) (timeSpentInMillis / 1000) % 60;

                String timeMsg = requireContext().getString(R.string.challenge_detail_toast_time_trained, minutesCompleted, secondsCompleted);
                android.widget.Toast.makeText(requireContext(), timeMsg, android.widget.Toast.LENGTH_LONG).show();
                // Espera de 4 segundos (4000 milisegundos) para que el usuario pueda leer el Toast y escuchar el sonido y luego vuelve al inicio
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    // Comprobación de seguridad: asegura que el usuario no ha navegado manualmente a otra pantalla antes
                    if (getView() != null) {
                        androidx.navigation.Navigation.findNavController(requireView()).popBackStack();
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
     * Invocado cuando la vista asociada a este fragmento va a ser destruida.
     * Es crucial para la gestión de memoria de Android: aquí limpiamos los bloqueos de pantalla,
     * liberamos los recursos pesados de hardware (MediaPlayers) y anulamos el View Binding
     * para prevenir memory leaks (fugas de memoria).
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        // Liberar recursos de audio críticos
        if (mpMinute != null) {
            mpMinute.release();
            mpMinute = null;
        }
        if (mpFinish != null) {
            mpFinish.release();
            mpFinish = null;
        }

        // Evitar memory leaks en la vista
        binding = null;

    }
}

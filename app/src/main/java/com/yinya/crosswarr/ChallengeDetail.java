package com.yinya.crosswarr;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yinya.crosswarr.databinding.FragmentChallengeDetailBinding;

//TODO: Hacer que cuando termine el desafío/le de al botón de parar vuelva a la página de inicio
public class ChallengeDetail extends Fragment {
    private FragmentChallengeDetailBinding binding;
    private boolean isChronometerRunning = false;
    private long timeSpentInMillis = 0; // Aquí guardaremos el tiempo real que ha entrenado
    private MediaPlayer mpMinute;
    private MediaPlayer mpFinish;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChallengeDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mpMinute = MediaPlayer.create(getContext(), R.raw.gong_emom);
        mpFinish = MediaPlayer.create(getContext(), R.raw.gong_final);

        // Obtener datos
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

            // Asignar Imagen con Null Safety
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

            // Asignar Descripción con Formato Seguro
            if (binding.tvChallengeDescriptionFragmentChallengeDetail != null && type != null) {
                String description = "";

                String safeType = type.trim().toLowerCase();

                if ("amrap".equals(safeType)) {
                    description = String.format("Descripción del desafío: Este desafío consistirá en un %s *,\n" +
                                    "- Tren superior: %s. %s repeticiones. \n" +
                                    "- Tren inferior: %s. %s repeticiones. \n" +
                                    "- Core: %s. %s repeticiones. \n" +
                                    "El tiempo para superar el desafío es de: %s minutos \n\n" +
                                    "* As Many Rounds As possible: Haz tantas rondas de este bloque de ejercicios como puedas.",
                            safeType.toUpperCase(), exerciseSup, repetitionSup, exerciseInf, repetitionInf, exerciseCore, repetitionCore, time);

                } else if ("ft".equals(safeType)) {
                    description = String.format("Descripción del desafío: Este desafío consistirá en un %s *,\n" +
                                    "- Tren superior: %s. %s repeticiones. \n" +
                                    "- Tren inferior: %s. %s repeticiones. \n" +
                                    "- Core: %s. %s repeticiones. \n" +
                                    "El tiempo para superar el desafío es de: %s minutos \n\n" +
                                    "* For time: Tu tiempo máximo para completar estos ejercicios es de %s minutos.",
                            safeType.toUpperCase(), exerciseSup, repetitionSup, exerciseInf, repetitionInf, exerciseCore, repetitionCore, time, time);

                } else if ("emom".equals(safeType)) {
                    description = String.format("Descripción del desafío: Este desafío consistirá en un %s *,\n" +
                                    "- Tren superior: %s. %s repeticiones. \n" +
                                    "- Tren inferior: %s. %s repeticiones. \n" +
                                    "- Core: %s. %s repeticiones. \n" +
                                    "El tiempo para superar el desafío es de: %s minutos \n\n" +
                                    "* Every minute on the minute: Tienes un minuto para hacer las repeticiones y descansar, ¡Mientras antes las hagas más tiempo descansas!",
                            safeType.toUpperCase(), exerciseSup, repetitionSup, exerciseInf, repetitionInf, exerciseCore, repetitionCore, time);

                } else {
                    // Si no es ninguno de los 3, que nos lo diga por pantalla
                    description = "Error: El tipo de desafío guardado en la base de datos no es válido. Tipo recibido: '" + type + "'";
                }

                binding.tvChallengeDescriptionFragmentChallengeDetail.setText(description);
            }

            // Asignar Duración
            if (binding.tvDurationDescriptionFragmentChallengeDetail != null) {
                binding.tvDurationDescriptionFragmentChallengeDetail.setText("Duración del desafío: " + time + " min.");
            }

            // Lógica del tempotizador
            if (binding.btnStartFragmentChallengeDetail != null && time != null) {
                binding.btnStartFragmentChallengeDetail.setOnClickListener(v -> {

                    long totalTimeInMillis = Long.parseLong(time) * 60 * 1000;

                    if (!isChronometerRunning) {
                        // INICIAR EL DESAFÍO

                        if (binding.ivFragmentChallengeDetail != null) {
                            binding.ivFragmentChallengeDetail.setVisibility(android.view.View.GONE);
                        }

                        if (binding.chronometerFragmentChallengeDetail != null) {
                            binding.chronometerFragmentChallengeDetail.setVisibility(android.view.View.VISIBLE);

                            try {
                                binding.chronometerFragmentChallengeDetail.setCountDown(true);
                                // La base es el momento actual + el tiempo total del desafío
                                binding.chronometerFragmentChallengeDetail.setBase(android.os.SystemClock.elapsedRealtime() + totalTimeInMillis);

                                binding.chronometerFragmentChallengeDetail.setOnChronometerTickListener(chronometer -> {
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

                                        if (mpFinish != null) {
                                            mpFinish.start();
                                        }

                                        android.widget.Toast.makeText(requireContext(), "¡Desafío completado!", android.widget.Toast.LENGTH_SHORT).show();
                                    }
                                });// TODO: Guardar en el historial totalTimeInMillis

                                binding.chronometerFragmentChallengeDetail.start();
                                isChronometerRunning = true;

                                // Cambiamos el botón para que ahora sirva para parar
                                binding.btnStartFragmentChallengeDetail.setText("Terminar / Parar");

                            } catch (NumberFormatException e) {
                                android.util.Log.e("ChallengeDetail", "Error al convertir el tiempo", e);
                            }
                        }

                    } else {
                        // ESTADO 2: EL USUARIO PARA EL DESAFÍO ANTES DE TIEMPO
                        if (binding.chronometerFragmentChallengeDetail != null) {
                            binding.chronometerFragmentChallengeDetail.stop();
                            isChronometerRunning = false;

                            // Calculamos cuánto tiempo sobraba en el cronómetro
                            long timeRemaining = binding.chronometerFragmentChallengeDetail.getBase() - android.os.SystemClock.elapsedRealtime();

                            // El tiempo invertido es el Total menos lo que sobró
                            timeSpentInMillis = totalTimeInMillis - timeRemaining;

                            binding.btnStartFragmentChallengeDetail.setText("Desafío Finalizado");
                            binding.btnStartFragmentChallengeDetail.setEnabled(false); // Lo bloqueamos para que no le dé más veces

                            // Convertimos los milisegundos a minutos y segundos para mostrarle un Toast de enhorabuena
                            int minutesCompleted = (int) (timeSpentInMillis / 1000) / 60;
                            int secondsCompleted = (int) (timeSpentInMillis / 1000) % 60;

                            String timeMsg = String.format("Has entrenado: %02d:%02d", minutesCompleted, secondsCompleted);
                            android.widget.Toast.makeText(requireContext(), timeMsg, android.widget.Toast.LENGTH_LONG).show();

                            // TODO: Aquí ya tienes tu variable `timeSpentInMillis` llena y lista para
                            // pasarla al UserData y subirla a Firebase a su lista de challenges completados.
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mpMinute != null) {
            mpMinute.release();
            mpMinute = null;
        }
        if (mpFinish != null) {
            mpFinish.release();
            mpFinish = null;
        }
    }

}

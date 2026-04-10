package com.yinya.crosswarr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yinya.crosswarr.databinding.FragmentChallengeDetailBinding;


public class ChallengeDetail extends Fragment {
    private FragmentChallengeDetailBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChallengeDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

                if ("amrap".equalsIgnoreCase(type)) {
                    description = String.format("Descripción del desafío: Este desafío consistirá en un %s *,\n" +
                                    "- Tren superior: %s. %s repeticiones. \n" +
                                    "- Tren inferior: %s. %s repeticiones. \n" +
                                    "- Core: %s. %s repeticiones. \n" +
                                    "El tiempo para superar el desafío es de : %s minutos \n\n" +
                                    "* As Many Rounds As possible: Haz tantas rondas de este bloque de ejercicios como puedas.",
                            type.toUpperCase(), exerciseSup, repetitionSup, exerciseInf, repetitionInf, exerciseCore, repetitionCore, time);
                } else if ("ft".equalsIgnoreCase(type)) {
                    description = String.format("Descripción del desafío: Este desafío consistirá en un %s *,\n" +
                                    "- Tren superior: %s. %s repeticiones. \n" +
                                    "- Tren inferior: %s. %s repeticiones. \n" +
                                    "- Core: %s. %s repeticiones. \n" +
                                    "El tiempo para superar el desafío es de : %s minutos \n\n" +
                                    "* For time: Tu tiempo máximo para completar estos ejercicios es: %s.",
                            type.toUpperCase(), exerciseSup, repetitionSup, exerciseInf, repetitionInf, exerciseCore, repetitionCore, time, time);
                } else if ("emom".equalsIgnoreCase(type)) {
                    description = String.format("Descripción del desafío: Este desafío consistirá en un %s *,\n" +
                                    "- Tren superior: %s. %s repeticiones. \n" +
                                    "- Tren inferior: %s. %s repeticiones. \n" +
                                    "- Core: %s. %s repeticiones. \n" +
                                    "El tiempo para superar el desafío es de : %s minutos \n\n" +
                                    "* Every minute on the minute: Tienes un minuto para hacer las repeticiones y descansar, ¡Mientras antes las hagas más tiempo descansas!",
                            type.toUpperCase(), exerciseSup, repetitionSup, exerciseInf, repetitionInf, exerciseCore, repetitionCore, time);
                }

                binding.tvChallengeDescriptionFragmentChallengeDetail.setText(description);
            }

            // Asignar Duración
            if (binding.tvDurationDescriptionFragmentChallengeDetail != null) {
                binding.tvDurationDescriptionFragmentChallengeDetail.setText("Duración del desafío: " + time + " min.");
            }

            // Lógica del Cronómetro
            if (binding.btnStartFragmentChallengeDetail != null) {
                binding.btnStartFragmentChallengeDetail.setOnClickListener(v -> {

                    if (binding.ivFragmentChallengeDetail != null) {
                        binding.ivFragmentChallengeDetail.setVisibility(android.view.View.GONE);
                    }

                    if (binding.chronometerFragmentChallengeDetail != null && time != null) {
                        binding.chronometerFragmentChallengeDetail.setVisibility(android.view.View.VISIBLE);

                        try {
                            long timeInMillis = Long.parseLong(time) * 60 * 1000;
                            binding.chronometerFragmentChallengeDetail.setCountDown(true);
                            binding.chronometerFragmentChallengeDetail.setBase(android.os.SystemClock.elapsedRealtime() + timeInMillis);

                            binding.chronometerFragmentChallengeDetail.setOnChronometerTickListener(chronometer -> {
                                long timeRemaining = chronometer.getBase() - android.os.SystemClock.elapsedRealtime();
                                if (timeRemaining <= 0) {
                                    chronometer.stop();
                                    chronometer.setText("00:00");
                                    //TODO: Que al llegar a 0 aparezca un toast de desafío completado,
                                    // se guarde el desafío en el historial de challenges del usuario y la vista se vaya al historial de challenges
                                }
                            });

                            binding.chronometerFragmentChallengeDetail.start();

                        } catch (NumberFormatException e) {
                            android.util.Log.e("ChallengeDetail", "Error al convertir el tiempo", e);
                        }
                    }

                    binding.btnStartFragmentChallengeDetail.setText("¡Desafío en curso!");
                    binding.btnStartFragmentChallengeDetail.setEnabled(false);
                });
            }
        }
    }
}

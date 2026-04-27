package com.yinya.crosswarr;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

public class DailyChallenge extends Fragment {
    long totalTimeInMillis;
    private FragmentDailyChallengeBinding binding;
    private FirebaseService firebaseService;
    private FirebaseUserService firebaseUserService;
    private boolean isChronometerRunning = false;
    private long timeSpentInMillis = 0; // Aquí guardaremos el tiempo real que ha entrenado
    private MediaPlayer mpMinute;
    private MediaPlayer mpFinish;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDailyChallengeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mpMinute = MediaPlayer.create(getContext(), R.raw.gong_emom);
        mpFinish = MediaPlayer.create(getContext(), R.raw.gong_final);

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

            // Asignar Imagen con Null Safety
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

            if (binding.tvChallengeDescriptionFragmentDailyChallenge != null && type != null) {
                String description = "";
                String safeType = type.trim().toLowerCase();

                if ("amrap".equals(safeType)) {
                    description = requireContext().getString(R.string.challenge_detail_description_amrap,
                            safeType.toUpperCase(), exerciseSup, repetitionSup, exerciseInf, repetitionInf, exerciseCore, repetitionCore, time);

                } else if ("ft".equals(safeType)) {
                    description = requireContext().getString(R.string.challenge_detail_description_ft,
                            safeType.toUpperCase(), exerciseSup, repetitionSup, exerciseInf, repetitionInf, exerciseCore, repetitionCore, time, time);

                } else if ("emom".equals(safeType)) {
                    description = requireContext().getString(R.string.challenge_detail_description_emom,
                            safeType.toUpperCase(), exerciseSup, repetitionSup, exerciseInf, repetitionInf, exerciseCore, repetitionCore, time);

                } else {
                    description = requireContext().getString(R.string.challenge_detail_description_error,type);
                }

                binding.tvChallengeDescriptionFragmentDailyChallenge.setText(description);
            }

            // Asignar Duración
            if (binding.tvDurationDescriptionFragmentDailyChallenge != null) {
                binding.tvDurationDescriptionFragmentDailyChallenge.setText(requireContext().getString(R.string.challenge_detail_duration, time));
            }

            // Lógica del tempotizador
            if (binding.btnStartFragmentDailyChallenge != null && time != null) {
                binding.btnStartFragmentDailyChallenge.setOnClickListener(v -> {

                    if (!isChronometerRunning) {
                        // INICIAR EL DESAFÍO

                        totalTimeInMillis = Long.parseLong(time) * 60 * 1000;

                        if (binding.chronometerFragmentDailyChallenge != null) {
                            binding.chronometerFragmentDailyChallenge.setVisibility(android.view.View.VISIBLE);

                            initChronometer(id, type);

                            // Cambiamos el botón para que ahora sirva para parar
                            binding.btnStartFragmentDailyChallenge.setText(requireContext().getString(R.string.challenge_detail_btn_stop));
                        }

                        timeSpentInMillis = totalTimeInMillis;
                    } else {
                        // ESTADO 2: EL USUARIO PARA EL DESAFÍO ANTES DE TIEMPO
                        if (binding.chronometerFragmentDailyChallenge != null) {
                            binding.chronometerFragmentDailyChallenge.stop();
                            isChronometerRunning = false;

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

    private void onChallengeFinished(String id){
        binding.btnStartFragmentDailyChallenge.setText(requireContext().getText(R.string.challenge_detail_challenge_finised));
        binding.btnStartFragmentDailyChallenge.setEnabled(false); // Lo bloqueamos para que no le dé más

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseService = FirebaseService.getInstance();
        firebaseUserService = FirebaseUserService.getInstance(firebaseService);
        firebaseUserService.upsertChallengeTime(user.getUid(), id, (int) timeSpentInMillis, new IFirebaseCallback() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                // Convertimos los milisegundos a minutos y segundos para mostrarle un Toast de enhorabuena
                int minutesCompleted = (int) (timeSpentInMillis / 1000) / 60;
                int secondsCompleted = (int) (timeSpentInMillis / 1000) % 60;

                String timeMsg = requireContext().getString(R.string.challenge_detail_toast_time_trained, minutesCompleted, secondsCompleted);
                android.widget.Toast.makeText(requireContext(), timeMsg, android.widget.Toast.LENGTH_LONG).show();
                // Espera 4 segundos (4000 milisegundos) y luego vuelve al inicio
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


}
package com.yinya.crosswarr;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.yinya.crosswarr.databinding.FragmentChallengesEditionBinding;
import com.yinya.crosswarr.models.ChallengeData;
import com.yinya.crosswarr.models.ExerciseData;
import com.yinya.crosswarr.repository.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChallengesEdition extends Fragment {
    private FragmentChallengesEditionBinding binding;
    private ArrayList<ExerciseData> allExercisesList = new ArrayList<>();
    ArrayAdapter<String> upperAdapter;
    ArrayAdapter<String> lowerAdapter;
    ArrayAdapter<String> coreAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChallengesEditionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupDatePicker();
        setupExercisesDropdowns();
        Repository.getInstance().fetchExercisesFromFirebase();
        binding.btnCreateChallengeFragmentChallengesEdition.setOnClickListener(v -> {
            saveChallenge();
        });

    }


    private void setupDatePicker() {
        binding.etChallengeDateFragmentChallengesEdition.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Formateamos la fecha para que se vea bonita en el cajón de texto (dd-MM-yyyy)
                        String selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear);
                        binding.etChallengeDateFragmentChallengesEdition.setText(selectedDate);
                    }, year, month, day);

            // Evitar que elijan fechas del pasado
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });
    }

    private void setupExercisesDropdowns() {
        Repository.getInstance().getExercisesLiveData().observe(getViewLifecycleOwner(), exercises -> {
            if (exercises != null) {
                // Guardamos la lista completa para usarla luego al marcar 'isUsed'
                allExercisesList.clear();
                allExercisesList.addAll(exercises);

                ArrayList<String> upperExercises = new ArrayList<>();
                ArrayList<String> lowerExercises = new ArrayList<>();
                ArrayList<String> coreExercises = new ArrayList<>();

                // Clasificamos cada ejercicio según su tipo
                for (ExerciseData ex : exercises) {
                    if (ex.getType() != null) {
                        String type = ex.getType().toLowerCase();
                        if (type.contains("upper_body")) {
                            upperExercises.add(ex.getName());
                        } else if (type.contains("lower_body")) {
                            lowerExercises.add(ex.getName());
                        } else if (type.contains("core")) {
                            coreExercises.add(ex.getName());
                        }
                    }
                }

                // Creamos tres adapters distintos, uno para cada lista
                upperAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, upperExercises);
                lowerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, lowerExercises);
                coreAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, coreExercises);

                // Asignamos cada adapter a su desplegable correspondiente
                binding.acUpperBodyFragmentChallengesEdition.setAdapter(upperAdapter);
                binding.acLowerBodyFragmentChallengesEdition.setAdapter(lowerAdapter);
                binding.acCoreFragmentChallengesEdition.setAdapter(coreAdapter);
            }
        });
    }

    private void saveChallenge() {
        String title = binding.etChallengeNameFragmentChallengesEdition.getText().toString().trim();
        String timeStr = binding.etChallengeTimeFragmentChallengesEdition.getText().toString().trim();
        String dateStr = binding.etChallengeDateFragmentChallengesEdition.getText().toString().trim();
        boolean requiresEquipment = binding.switchRequiresEquipmentChallenge.isChecked();

        String exerciseSup = binding.acUpperBodyFragmentChallengesEdition.getText().toString().trim();
        String repsSupStr = binding.etRepsUpperFragmentChallengesEdition.getText().toString().trim();

        String exerciseInf = binding.acLowerBodyFragmentChallengesEdition.getText().toString().trim();
        String repsInfStr = binding.etRepsLowerFragmentChallengesEdition.getText().toString().trim();

        String exerciseCore = binding.acCoreFragmentChallengesEdition.getText().toString().trim();
        String repsCoreStr = binding.etRepsCoreFragmentChallengesEdition.getText().toString().trim();

        String type = "";
        if (binding.btnTypeAmrapFragmentChallengesEdition.isChecked()) type = "amrap";
        else if (binding.btnTypeEmomFragmentChallengesEdition.isChecked()) type = "emom";
        else if (binding.btnTypeFtFragmentChallengesEdition.isChecked()) type = "ft";

        // Comprobar que no haya nada vacío
        if (title.isEmpty() || timeStr.isEmpty() || dateStr.isEmpty() || type.isEmpty() ||
                exerciseSup.isEmpty() || repsSupStr.isEmpty() ||
                exerciseInf.isEmpty() || repsInfStr.isEmpty() ||
                exerciseCore.isEmpty() || repsCoreStr.isEmpty()) {

            android.widget.Toast.makeText(requireContext(), R.string.challenges_edition_saveChallenge_error, android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Transformar los textos de números a 'int'
            int time = Integer.parseInt(timeStr);
            int repsSup = Integer.parseInt(repsSupStr);
            int repsInf = Integer.parseInt(repsInfStr);
            int repsCore = Integer.parseInt(repsCoreStr);

            // Transformar el texto de la fecha a un 'Timestamp' de Firebase
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date javaDate = sdf.parse(dateStr);
            Timestamp activationDate = new Timestamp(javaDate); // Convertimos el Date a Timestamp
            Timestamp creationDate = Timestamp.now(); // Fecha y hora actuales

            // Generar un ID único (usando la fecha actual sin guiones para que sea limpio)
            SimpleDateFormat sdfId = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            String dateForId = sdfId.format(javaDate);
            String generatedId;

            // Asignamos el ID directamente dependiendo de si requiere material o no
            if (requiresEquipment) {
                generatedId = "eq_challenge_" + dateForId; // Ej: eq_challenge_20260416
            } else {
                generatedId = "challenge_" + dateForId;    // Ej: challenge_20260416
            }

            ChallengeData newChallenge = new ChallengeData(
                    generatedId,
                    title,
                    creationDate,
                    activationDate,
                    time,
                    exerciseSup,
                    exerciseInf,
                    exerciseCore,
                    false, // Estado por defecto
                    repsSup,
                    repsInf,
                    repsCore,
                    type,
                    requiresEquipment
            );

            // Guardar en Firebase usando el Repositorio
            Repository.getInstance().createChallenge(newChallenge);

            // Marcar como usados los ejercicios en Firebase
            markExerciseAsUsedInFirebase(exerciseSup);
            markExerciseAsUsedInFirebase(exerciseInf);
            markExerciseAsUsedInFirebase(exerciseCore);

            android.widget.Toast.makeText(requireContext(), R.string.challenges_edition_saveChallenge_success, android.widget.Toast.LENGTH_SHORT).show();
            Repository.getInstance().fetchExercisesFromFirebase();
            cleanForm();

        } catch (ParseException e) {
            android.widget.Toast.makeText(requireContext(), R.string.challenges_edition_saveChallenge_errorDate, android.widget.Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            android.widget.Toast.makeText(requireContext(), R.string.challenges_edition_saveChallenge_errorTimeOrReps, android.widget.Toast.LENGTH_SHORT).show();
        }

    }

    private void markExerciseAsUsedInFirebase(String exerciseName) {
        for (ExerciseData ex : allExercisesList) {
            if (ex.getName().equals(exerciseName)) {
                if (!ex.isUsed()) { // Solo lo actualizamos si estaba a false
                    Repository.getInstance().updateExerciseUsage(ex);
                }
                break; // Lo encontramos, dejamos de buscar
            }
        }
    }

    private void cleanForm() {
        binding.etChallengeNameFragmentChallengesEdition.setText("");
        binding.etChallengeTimeFragmentChallengesEdition.setText("");
        binding.etChallengeDateFragmentChallengesEdition.setText("");

        binding.acUpperBodyFragmentChallengesEdition.setText("", false);
        binding.etRepsUpperFragmentChallengesEdition.setText("");

        binding.acLowerBodyFragmentChallengesEdition.setText("", false);
        binding.etRepsLowerFragmentChallengesEdition.setText("");

        binding.acCoreFragmentChallengesEdition.setText("", false);
        binding.etRepsCoreFragmentChallengesEdition.setText("");

        binding.switchRequiresEquipmentChallenge.setChecked(false);
        binding.btnTypeAmrapFragmentChallengesEdition.setChecked(false);

        binding.getRoot().clearFocus();
        binding.toggleChallengeType.clearChecked();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}




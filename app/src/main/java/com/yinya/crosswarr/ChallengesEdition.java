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

/**
 * Fragmento destinado al modo administrador para la creación de nuevos Desafíos (Challenges).
 * Permite rellenar un formulario estructurado seleccionando la fecha, la modalidad,
 * y escogiendo ejercicios de la base de datos divididos por grupos musculares.
 * Finalmente, empaqueta todos los datos y los envía al Repositorio para guardarlos en Firebase.
 */
public class ChallengesEdition extends Fragment {
    // Adaptadores para los menús desplegables (AutoCompleteTextView)
    ArrayAdapter<String> upperAdapter;
    ArrayAdapter<String> lowerAdapter;
    ArrayAdapter<String> coreAdapter;
    private FragmentChallengesEditionBinding binding;
    /**
     * Lista local para almacenar todos los ejercicios descargados.
     * Se utiliza posteriormente para buscar y actualizar el estado 'isUsed' de los ejercicios seleccionados.
     */
    private ArrayList<ExerciseData> allExercisesList = new ArrayList<>();

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
        binding = FragmentChallengesEditionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Invocado inmediatamente después de que onCreateView ha retornado.
     * Aquí se inicializan los componentes de la interfaz (calendario y desplegables),
     * se solicita la carga inicial de ejercicios al repositorio y se configuran los listeners de los botones.
     *
     * @param view               La Vista devuelta por onCreateView().
     * @param savedInstanceState Si no es null, el fragmento está siendo reconstruido.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupDatePicker();
        setupExercisesDropdowns();

        // Solicitamos al Repositorio que actualice su LiveData con los ejercicios de Firebase
        Repository.getInstance().fetchExercisesFromFirebase();
        binding.btnCreateChallengeFragmentChallengesEdition.setOnClickListener(v -> {
            saveChallenge();
        });
    }

    /**
     * Configura el selector de fechas (DatePickerDialog) para el campo de texto de la fecha del reto.
     * Aplica una restricción para que el administrador no pueda seleccionar fechas pasadas
     * y formatea la salida visual al estándar europeo (dd-MM-yyyy).
     */
    private void setupDatePicker() {
        binding.etChallengeDateFragmentChallengesEdition.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {

                // Formateamos la fecha para que se vea bonita en el cajón de texto (dd-MM-yyyy)
                String selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear);
                binding.etChallengeDateFragmentChallengesEdition.setText(selectedDate);
            }, year, month, day);

            // Evitar que elijan fechas del pasado (restando 1 segundo por seguridad)
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });
    }

    /**
     * Configura los menús desplegables (Dropdowns) para la selección de ejercicios.
     * Se suscribe al LiveData del Repositorio de forma reactiva. Cuando llegan los datos,
     * clasifica los ejercicios en tres listas separadas (Superior, Inferior, Core) basándose en su tipo
     * y alimenta los adaptadores correspondientes de la interfaz.
     */
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

    /**
     * Recoge, valida y procesa todos los datos introducidos por el administrador en el formulario.
     * Si la validación es correcta, genera un ID único inteligente, crea el objeto {@link ChallengeData},
     * lo envía al repositorio para su guardado y marca los ejercicios seleccionados como utilizados.
     */
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

        // Comprobar que no haya campos de texto o selecciones vacías
        if (title.isEmpty() || timeStr.isEmpty() || dateStr.isEmpty() || type.isEmpty() || exerciseSup.isEmpty() || repsSupStr.isEmpty() || exerciseInf.isEmpty() || repsInfStr.isEmpty() || exerciseCore.isEmpty() || repsCoreStr.isEmpty()) {

            android.widget.Toast.makeText(requireContext(), R.string.challenges_edition_saveChallenge_error, android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Transformar los textos de números a enteros (int)
            int time = Integer.parseInt(timeStr);
            int repsSup = Integer.parseInt(repsSupStr);
            int repsInf = Integer.parseInt(repsInfStr);
            int repsCore = Integer.parseInt(repsCoreStr);

            // Transformar el texto de la fecha a un 'Timestamp' compatible con Firebase
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date javaDate = sdf.parse(dateStr);
            Timestamp activationDate = new Timestamp(javaDate); // Convertimos el Date a Timestamp
            Timestamp creationDate = Timestamp.now(); // Fecha y hora actuales

            // Generar un ID único (usando la fecha actual sin guiones para que sea limpio)
            SimpleDateFormat sdfId = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            String dateForId = sdfId.format(javaDate);
            String generatedId;

            // Asignamos el prefijo del ID directamente dependiendo de si requiere material o no
            if (requiresEquipment) {
                generatedId = "eq_challenge_" + dateForId; // Ej: eq_challenge_20260416
            } else {
                generatedId = "challenge_" + dateForId;    // Ej: challenge_20260416
            }

            ChallengeData newChallenge = new ChallengeData(generatedId, title, creationDate, activationDate, time, exerciseSup, exerciseInf, exerciseCore, false, // Estado por defecto
                    repsSup, repsInf, repsCore, type, requiresEquipment);

            // Guardar en Firebase usando el Repositorio
            Repository.getInstance().createChallenge(newChallenge);

            // Actualizar el estado 'isUsed' de los ejercicios en la base de datos
            markExerciseAsUsedInFirebase(exerciseSup);
            markExerciseAsUsedInFirebase(exerciseInf);
            markExerciseAsUsedInFirebase(exerciseCore);

            android.widget.Toast.makeText(requireContext(), R.string.challenges_edition_saveChallenge_success, android.widget.Toast.LENGTH_SHORT).show();

            // Refrescar los ejercicios para que los adaptadores tengan la información de uso actualizada
            Repository.getInstance().fetchExercisesFromFirebase();
            cleanForm();

        } catch (ParseException e) {
            android.widget.Toast.makeText(requireContext(), R.string.challenges_edition_saveChallenge_errorDate, android.widget.Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            android.widget.Toast.makeText(requireContext(), R.string.challenges_edition_saveChallenge_errorTimeOrReps, android.widget.Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Busca un ejercicio por su nombre en la lista local de memoria y, si no estaba marcado
     * como utilizado previamente, envía una petición de actualización a Firebase para cambiar
     * su estado (isUsed = true).
     *
     * @param exerciseName El nombre exacto del ejercicio tal cual se muestra en el desplegable.
     */
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

    /**
     * Restablece completamente el formulario, borrando todos los textos,
     * desmarcando los selectores e interruptores y quitando el foco del teclado.
     */
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

    /**
     * Invocado cuando la vista del fragmento va a ser destruida.
     * Se anula el objeto de View Binding para liberar memoria y evitar memory leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}




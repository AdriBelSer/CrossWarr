package com.yinya.crosswarr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yinya.crosswarr.databinding.FragmentExercisesEditionBinding;
import com.yinya.crosswarr.models.ExerciseData;
import com.yinya.crosswarr.repository.Repository;

import java.util.ArrayList;

/**
 * Fragmento destinado al modo administrador para la creación de nuevos Ejercicios.
 * Presenta un formulario donde se introducen los datos multimedia (enlaces a imagen/vídeo),
 * descripciones y, opcionalmente, la lista de materiales requeridos.
 * Incluye la lógica de negocio necesaria para clasificar automáticamente el ejercicio
 * en el array correspondiente de Firebase.
 */
public class ExercisesEdition extends Fragment {
    private FragmentExercisesEditionBinding binding;

    /**
     * Invocado para inflar el diseño XML asociado a este fragmento utilizando View Binding.
     *
     * @param inflater           El objeto LayoutInflater utilizado para inflar las vistas en el contexto.
     * @param container          El ViewGroup padre en el que se insertará la vista.
     * @param savedInstanceState Si no es null, el fragmento está siendo reconstruido.
     * @return La vista raíz (View) del diseño inflado.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        binding = FragmentExercisesEditionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Invocado inmediatamente después de que la vista ha sido creada.
     * Se encarga de configurar el comportamiento interactivo del formulario, como mostrar u
     * ocultar el campo de texto de materiales según el estado del Switch, y asignar
     * el evento de clic al botón de guardar.
     *
     * @param view               La Vista devuelta por onCreateView().
     * @param savedInstanceState Si no es null, el fragmento está siendo reconstruido.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Listener dinámico para mostrar u ocultar la entrada de materiales
        binding.switchMaterialsFragmentExercisesEdition.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.switchMaterialsFragmentExercisesEdition.setText(R.string.exercise_edition_materials);
                binding.tilMaterialsListFragmentExercisesEdition.setVisibility(View.VISIBLE);
            } else {
                binding.switchMaterialsFragmentExercisesEdition.setText(R.string.exercise_edition_noMaterials);
                binding.tilMaterialsListFragmentExercisesEdition.setVisibility(View.GONE);
                binding.etMaterialsListFragmentExercisesEdition.setText(""); // Borramos si se vuelve a apagar
            }
        });

        binding.btnCreateExercise.setOnClickListener(v -> {
            saveExercise();
        });
    }

    /**
     * Recoge y valida los datos introducidos en el formulario.
     * Aplica reglas de negocio fundamentales:
     * 1. Determina dinámicamente el nombre del array destino en Firebase concatenando
     * la zona del cuerpo con el requerimiento de material (ej. "upper_body" + "_with_equipment").
     * 2. Procesa la cadena de texto de los materiales, separándola por comas en una lista.
     * 3. Genera un ID único temporal basado en los milisegundos actuales.
     * Finalmente, empaqueta el objeto {@link ExerciseData} y delega su creación al Repositorio.
     */
    private void saveExercise() {

        String imageUrl = binding.etImageUrlFragmentExercisesEdition.getText().toString().trim();
        String videoUrl = binding.etVideoUrlFragmentExercisesEdition.getText().toString().trim();
        String name = binding.etNameFragmentExercisesEdition.getText().toString().trim();
        String description = binding.etDescriptionFragmentExercisesEdition.getText().toString().trim();

        // 1. Miramos qué tren han elegido para formar la primera parte de la clave
        int selectedRadioId = binding.rgTypeFragmentExercisesEdition.getCheckedRadioButtonId();
        String baseType = "";

        if (selectedRadioId == R.id.rb_upper_body_exercises_edition) {
            baseType = "upper_body";
        } else if (selectedRadioId == R.id.rb_lower_body_exercises_edition) {
            baseType = "lower_body";
        } else if (selectedRadioId == R.id.rb_core_exercises_edition) {
            baseType = "core";
        }

        // 2. Miramos si el Switch de materiales está encendido o apagado para el sufijo
        String materialsSuffix = "";
        if (binding.switchMaterialsFragmentExercisesEdition.isChecked()) {
            materialsSuffix = "_with_equipment";
        } else {
            materialsSuffix = "_without_equipment";
        }

        // 3. Unimos las dos palabras para crear exactamente el nombre del array de Firebase
        String finalType = baseType + materialsSuffix;

        // Validación de campos obligatorios
        if (name.isEmpty() || description.isEmpty() || selectedRadioId == -1) {
            Toast.makeText(getContext(), R.string.exercise_edition_toast_error, Toast.LENGTH_SHORT).show();
            return; // Cortamos la ejecución aquí, no seguimos
        }

        // Generamos un ID único para Firebase. Usamos el tiempo actual en milisegundos para evitar colisiones
        String generatedId = "ex_" + System.currentTimeMillis();

        String materialsRawList = binding.etMaterialsListFragmentExercisesEdition.getText().toString().trim();
        ArrayList<String> materialsList = new ArrayList<>();

        // Si el switch está encendido y el usuario ha escrito algo, procesamos el String
        if (binding.switchMaterialsFragmentExercisesEdition.isChecked() && !materialsRawList.isEmpty()) {

            // Dividimos el texto por las comas y limpiamos los espacios de cada palabra
            String[] arrayMateriales = materialsRawList.split(",");
            for (String material : arrayMateriales) {
                materialsList.add(material.trim());
            }
        }

        ExerciseData newExercise = new ExerciseData(generatedId, name, description, finalType, imageUrl, videoUrl, materialsList, false);

        Repository.getInstance().createExercise(newExercise);

        // Le avisamos al usuario de que ha ido bien
        Toast.makeText(getContext(), R.string.exercise_edition_toast_success, Toast.LENGTH_SHORT).show();

        cleanForm();
    }

    /**
     * Restablece el formulario a su estado inicial.
     * Borra todos los campos de texto, desmarca las opciones de los RadioButtons
     * y apaga el interruptor de materiales.
     */
    private void cleanForm() {
        binding.etImageUrlFragmentExercisesEdition.setText("");
        binding.etVideoUrlFragmentExercisesEdition.setText("");
        binding.etNameFragmentExercisesEdition.setText("");
        binding.etDescriptionFragmentExercisesEdition.setText("");
        binding.etMaterialsListFragmentExercisesEdition.setText("");
        binding.rgTypeFragmentExercisesEdition.clearCheck();
        binding.switchMaterialsFragmentExercisesEdition.setChecked(false);
    }

    /**
     * Invocado cuando la vista del fragmento va a ser destruida.
     * Se anula el objeto de View Binding para liberar la memoria y evitar memory leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
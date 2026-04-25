package com.yinya.crosswarr;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yinya.crosswarr.databinding.FragmentExercisesEditionBinding;
import com.yinya.crosswarr.models.ExerciseData;
import com.yinya.crosswarr.repository.Repository;

import java.util.ArrayList;

public class ExercisesEdition extends Fragment {
    private FragmentExercisesEditionBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        binding = FragmentExercisesEditionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

    private void saveExercise() {

        String imageUrl = binding.etImageUrlFragmentExercisesEdition.getText().toString().trim();
        String videoUrl = binding.etVideoUrlFragmentExercisesEdition.getText().toString().trim();
        String name = binding.etNameFragmentExercisesEdition.getText().toString().trim();
        String description = binding.etDescriptionFragmentExercisesEdition.getText().toString().trim();

        // 1. Miramos qué tren han elegido
        int selectedRadioId = binding.rgTypeFragmentExercisesEdition.getCheckedRadioButtonId();
        String baseType = "";

        if (selectedRadioId == R.id.rb_upper_body_exercises_edition) {
            baseType = "upper_body";
        } else if (selectedRadioId == R.id.rb_lower_body_exercises_edition) {
            baseType = "lower_body";
        } else if (selectedRadioId == R.id.rb_core_exercises_edition) {
            baseType = "core";
        }

        // 2. Miramos si el Switch de materiales está encendido o apagado
        String materialsSuffix = "";
        if (binding.switchMaterialsFragmentExercisesEdition.isChecked()) {
            materialsSuffix = "_with_equipment";
        } else {
            materialsSuffix = "_without_equipment";
        }

        // 3. Unimos las dos palabras para crear exactamente el nombre del array de Firebase
        String finalType = baseType + materialsSuffix;

        // VALIDACIÓN
        if (name.isEmpty() || description.isEmpty() || selectedRadioId == -1) {
            Toast.makeText(getContext(), R.string.exercise_edition_toast_error, Toast.LENGTH_SHORT).show();
            return; // Cortamos la ejecución aquí, no seguimos
        }

        // Generamos un ID único para Firebase. Podemos usar el tiempo actual en milisegundos
        String generatedId = "ex_" + System.currentTimeMillis();

        String materialsRawList= binding.etMaterialsListFragmentExercisesEdition.getText().toString().trim();
        ArrayList<String> materialsList = new ArrayList<>();

        // Si el switch está encendido y el usuario ha escrito algo
        if (binding.switchMaterialsFragmentExercisesEdition.isChecked() && !materialsRawList.isEmpty()) {
            // Dividimos el texto por las comas y limpiamos los espacios de cada palabra
            String[] arrayMateriales = materialsRawList.split(",");
            for (String material : arrayMateriales) {
                materialsList.add(material.trim());
            }
        }

        // --- PASO 3: EMPAQUETAR EN NUESTRO MODELO ---
        ExerciseData newExercise = new ExerciseData(
                generatedId,
                name,
                description,
                finalType,
                imageUrl,
                videoUrl,
                materialsList,
                false
        );

        Repository.getInstance().createExercise(newExercise);

        // Le avisamos al usuario de que ha ido bien
        Toast.makeText(getContext(), R.string.exercise_edition_toast_success, Toast.LENGTH_SHORT).show();

        cleanForm();
    }

    private void cleanForm() {
        binding.etImageUrlFragmentExercisesEdition.setText("");
        binding.etVideoUrlFragmentExercisesEdition.setText("");
        binding.etNameFragmentExercisesEdition.setText("");
        binding.etDescriptionFragmentExercisesEdition.setText("");
        binding.etMaterialsListFragmentExercisesEdition.setText("");
        binding.rgTypeFragmentExercisesEdition.clearCheck();
        binding.switchMaterialsFragmentExercisesEdition.setChecked(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
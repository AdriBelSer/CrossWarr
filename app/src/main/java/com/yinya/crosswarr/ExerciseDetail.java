package com.yinya.crosswarr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;
import com.yinya.crosswarr.databinding.FragmentExerciseDetailBinding;

import java.util.ArrayList;

public class ExerciseDetail extends Fragment {
    private FragmentExerciseDetailBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        binding = FragmentExerciseDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener datos del argumento que inicia este fragmento
        if (getArguments() != null) {
            String image = getArguments().getString("image");
            String name = getArguments().getString("name");
            String description = getArguments().getString("description");
            String type = getArguments().getString("type");
            String video = getArguments().getString("video");
            ArrayList<String> materials = getArguments().getStringArrayList("materials");

            // Asignar los datos
            if (binding.tvExerciseDetailName != null) {
                binding.tvExerciseDetailName.setText(name);
            }
            if (binding.tvExerciseDetailDescription != null) {
                binding.tvExerciseDetailDescription.setText(description);
            }
            if (binding.tvExerciseDetailTypes != null) {
                if (type.equalsIgnoreCase("upper_body_with_equipment"))binding.tvExerciseDetailTypes.setText(R.string.exercise_type_detail_uper_with_material);
                else if (type.equalsIgnoreCase("upper_body_without_equipment"))binding.tvExerciseDetailTypes.setText(R.string.exercise_type_detail_uper_without_material);
                else if(type.equalsIgnoreCase("lower_body_with_equipment"))binding.tvExerciseDetailTypes.setText(R.string.exercise_type_detail_lower_with_material);
                else if(type.equalsIgnoreCase("lower_body_without_equipment"))binding.tvExerciseDetailTypes.setText(R.string.exercise_type_detail_lower_without_material);
                else if(type.equalsIgnoreCase("core_with_equipment"))binding.tvExerciseDetailTypes.setText(R.string.exercise_type_detail_core_with_material);
                else if(type.equalsIgnoreCase("core_without_equipment"))binding.tvExerciseDetailTypes.setText(R.string.exercise_type_detail_core_without_material);
            }
            // Cargar los materiales con TextUtils para añadirlos uno tras otro
            if (binding.tvExerciseDetailMaterials != null && materials != null) {
                String txtMaterials = TextUtils.join(", ", materials);
                binding.tvExerciseDetailMaterials.setText(requireContext().getString(R.string.exercise_detail_materials, txtMaterials));
            } else if (binding.tvExerciseDetailMaterials == null || materials == null) {
                binding.tvExerciseDetailMaterials.setText(R.string.exercise_detail_noMaterials);
            }

            // Cargar la imagen con Picasso
            if (image != null && !image.isEmpty()) {
                Picasso.get()
                        .load(image)
                        .into(binding.ivExerciseDetail);
            }

            // Configurar botón video
            if (binding.btnExerciseDetailVideo != null) {

                // Primero, una buena práctica: si no hay vídeo, ocultamos el botón
                if (video == null || video.isEmpty()) {
                    binding.btnExerciseDetailVideo.setVisibility(View.GONE);
                } else {
                    binding.btnExerciseDetailVideo.setVisibility(View.VISIBLE);

                    // Ponemos el Listener para cuando pulsen
                    binding.btnExerciseDetailVideo.setOnClickListener(v -> {
                        // Creamos la acción para "ver" el link
                        Intent playVideoIntent = new Intent(Intent.ACTION_VIEW);
                        playVideoIntent.setData(Uri.parse(video)); // Convertimos el String a Uri

                        startActivity(playVideoIntent);

                    });
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
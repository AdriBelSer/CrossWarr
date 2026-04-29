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

/**
 * Fragmento encargado de mostrar la vista detallada de un Ejercicio específico.
 * Recibe los datos empaquetados desde la pantalla anterior y se encarga de renderizar
 * la información textual, cargar dinámicamente la imagen representativa a través de Internet
 * y gestionar la redirección a enlaces de vídeo externos.
 */
public class ExerciseDetail extends Fragment {
    private FragmentExerciseDetailBinding binding;

    /**
     * Invocado para inflar el diseño XML asociado a este fragmento utilizando View Binding.
     *
     * @param inflater           El objeto LayoutInflater utilizado para inflar las vistas en el contexto.
     * @param container          El ViewGroup padre en el que se insertará la vista del fragmento.
     * @param savedInstanceState Si no es null, este fragmento está siendo reconstruido.
     * @return La vista raíz (View) del diseño inflado lista para ser mostrada.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        binding = FragmentExerciseDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Invocado inmediatamente después de que la vista ha sido creada.
     * Es el lugar donde se extraen los argumentos de navegación (Bundle), se mapean
     * los textos a la interfaz, se formatea la categoría del ejercicio y se configuran
     * elementos dinámicos como la carga de imágenes con Picasso y el botón de vídeo externo.
     *
     * @param view               La Vista devuelta por onCreateView().
     * @param savedInstanceState Si no es null, el fragmento está siendo reconstruido.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener datos del argumento que inicia este fragmento (Safe Args / Bundle)
        if (getArguments() != null) {
            String image = getArguments().getString("image");
            String name = getArguments().getString("name");
            String description = getArguments().getString("description");
            String type = getArguments().getString("type");
            String video = getArguments().getString("video");
            ArrayList<String> materials = getArguments().getStringArrayList("materials");

            // Asignar los datos textuales básicos
            if (binding.tvExerciseDetailName != null) {
                binding.tvExerciseDetailName.setText(name);
            }
            if (binding.tvExerciseDetailDescription != null) {
                binding.tvExerciseDetailDescription.setText(description);
            }

            // Traducción y mapeo del tipo de ejercicio (clave de la BBDD) a texto legible por el usuario
            if (binding.tvExerciseDetailTypes != null) {
                if (type.equalsIgnoreCase("upper_body_with_equipment"))
                    binding.tvExerciseDetailTypes.setText(R.string.exercise_type_detail_uper_with_material);
                else if (type.equalsIgnoreCase("upper_body_without_equipment"))
                    binding.tvExerciseDetailTypes.setText(R.string.exercise_type_detail_uper_without_material);
                else if (type.equalsIgnoreCase("lower_body_with_equipment"))
                    binding.tvExerciseDetailTypes.setText(R.string.exercise_type_detail_lower_with_material);
                else if (type.equalsIgnoreCase("lower_body_without_equipment"))
                    binding.tvExerciseDetailTypes.setText(R.string.exercise_type_detail_lower_without_material);
                else if (type.equalsIgnoreCase("core_with_equipment"))
                    binding.tvExerciseDetailTypes.setText(R.string.exercise_type_detail_core_with_material);
                else if (type.equalsIgnoreCase("core_without_equipment"))
                    binding.tvExerciseDetailTypes.setText(R.string.exercise_type_detail_core_without_material);
            }
            // Cargar los materiales con TextUtils para añadirlos uno tras otro
            if (binding.tvExerciseDetailMaterials != null && materials != null) {
                String txtMaterials = TextUtils.join(", ", materials);
                binding.tvExerciseDetailMaterials.setText(requireContext().getString(R.string.exercise_detail_materials, txtMaterials));
            } else if (binding.tvExerciseDetailMaterials == null || materials == null) {
                binding.tvExerciseDetailMaterials.setText(R.string.exercise_detail_noMaterials);
            }

            // Cargar la imagen remota de forma asíncrona utilizando la librería Picasso
            if (image != null && !image.isEmpty()) {
                Picasso.get().load(image).into(binding.ivExerciseDetail);
            }

            // Configurar la visibilidad y acción del botón de vídeo demostrativo
            if (binding.btnExerciseDetailVideo != null) {

                // Si no hay enlace de vídeo, ocultamos el botón para no confundir al usuario
                if (video == null || video.isEmpty()) {
                    binding.btnExerciseDetailVideo.setVisibility(View.GONE);
                } else {
                    binding.btnExerciseDetailVideo.setVisibility(View.VISIBLE);

                    // Asignamos el Listener para lanzar un Intent Implícito hacia el navegador o app de YouTube
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

    /**
     * Invocado cuando la vista asociada a este fragmento va a ser destruida.
     * Se liberan los recursos activos, como las peticiones de red pendientes de Picasso,
     * y se anula el objeto de View Binding para evitar fugas de memoria (memory leaks).
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Cancelamos la carga de la imagen si el usuario cierra el fragmento antes de que termine
        if (binding != null && binding.ivExerciseDetail != null) {
            Picasso.get().cancelRequest(binding.ivExerciseDetail);
        }
        binding = null;
    }
}
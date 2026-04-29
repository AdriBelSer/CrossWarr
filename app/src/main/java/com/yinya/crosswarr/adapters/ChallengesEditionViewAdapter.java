package com.yinya.crosswarr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yinya.crosswarr.R;
import com.yinya.crosswarr.databinding.CardListChallengesEditionItemBinding;
import com.yinya.crosswarr.models.ChallengeData;

import java.util.ArrayList;

/**
 * Adaptador para el RecyclerView que muestra la lista de desafíos en la vista de administrador o edición.
 * Se encarga de inflar las tarjetas de los desafíos, vincular los datos correspondientes
 * y gestionar los eventos de clic, tanto para visualizar el detalle como para eliminar un desafío.
 */
public class ChallengesEditionViewAdapter extends RecyclerView.Adapter<ChallengesEditionViewAdapter.ChallengesEditionViewHolder> {
    private final ArrayList<ChallengeData> challenge;
    private final Context context;
    private final OnChallengeAdminListener listener;

    /**
     * Constructor del adaptador.
     *
     * @param challenge Lista de objetos {@link ChallengeData} que contiene la información de los desafíos a mostrar.
     * @param context   Contexto de la aplicación o actividad que aloja el RecyclerView.
     * @param listener  Interfaz para escuchar y gestionar los eventos de clic (selección y eliminación) de los elementos.
     */
    public ChallengesEditionViewAdapter(ArrayList<ChallengeData> challenge, Context context, OnChallengeAdminListener listener) {
        this.challenge = challenge;
        this.context = context;
        this.listener = listener;
    }

    /**
     * Se llama cuando el RecyclerView necesita un nuevo {@link ChallengesEditionViewHolder}
     * para representar un elemento.
     *
     * @param parent   El ViewGroup al que se añadirá la nueva vista después de vincularla a una posición.
     * @param viewType El tipo de vista del nuevo elemento.
     * @return Un nuevo ChallengesEditionViewHolder que contiene la vista inflada (tarjeta del desafío).
     */
    @NonNull
    @Override
    public ChallengesEditionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardListChallengesEditionItemBinding binding = CardListChallengesEditionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChallengesEditionViewHolder(binding);
    }

    /**
     * Se llama por el RecyclerView para mostrar los datos en una posición específica.
     * Este método actualiza el contenido del ViewHolder para reflejar el desafío en la posición dada
     * y configura los listeners de interacción.
     *
     * @param holder   El ViewHolder que debe ser actualizado para representar el contenido del elemento.
     * @param position La posición del elemento dentro del conjunto de datos del adaptador.
     */
    @Override
    public void onBindViewHolder(@NonNull ChallengesEditionViewAdapter.ChallengesEditionViewHolder holder, int position) {
        ChallengeData currentChallenge = this.challenge.get(position);
        holder.bind(currentChallenge);
        // Configura el clic en toda la tarjeta para ver los detalles del desafío
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onChallengeClick(currentChallenge, view);
            }
        });
        // Configura el clic en el botón de eliminar desafío
        holder.binding.btnDeleteChallenge.setOnClickListener(view -> {
            if (listener != null) {
                listener.onDeleteClick(currentChallenge);
            }
        });
    }

    /**
     * Devuelve el número total de elementos en el conjunto de datos que posee el adaptador.
     *
     * @return El tamaño de la lista de desafíos.
     */
    @Override
    public int getItemCount() {
        return challenge.size();
    }

    /**
     * Clase interna estática que representa el ViewHolder para los elementos del RecyclerView.
     * Mantiene las referencias a los componentes de la interfaz de usuario para evitar
     * llamadas costosas a findViewById durante el desplazamiento (scroll).
     */
    public static class ChallengesEditionViewHolder extends RecyclerView.ViewHolder {
        private final CardListChallengesEditionItemBinding binding;

        /**
         * Constructor del ViewHolder.
         *
         * @param binding El objeto de View Binding que contiene las referencias a las vistas de la tarjeta.
         */
        public ChallengesEditionViewHolder(@NonNull CardListChallengesEditionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Vincula los datos de un objeto {@link ChallengeData} a las vistas correspondientes en la interfaz.
         * Se encarga específicamente de formatear y mostrar la fecha de activación del desafío.
         *
         * @param challenge El objeto que contiene los datos del desafío a mostrar.
         */
        public void bind(ChallengeData challenge) {
            binding.tvChallengeDateCardListChallengesEditionItem.setText(challenge.getCreationDate().toDate().toString());
            if (challenge.getActivationDate() != null) {

                //Convertir el "Timestamp" de Firebase a un "Date"
                java.util.Date date = challenge.getActivationDate().toDate();

                // formato DD-MM-YYYY
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());

                binding.tvChallengeDateCardListChallengesEditionItem.setText(sdf.format(date));

            } else {
                // Texto de seguridad por si algún challenge antiguo no tiene fecha
                binding.tvChallengeDateCardListChallengesEditionItem.setText(R.string.challenges_edition_view_adapter_getItemCount_noDataChallenge);
            }
        }
    }
}

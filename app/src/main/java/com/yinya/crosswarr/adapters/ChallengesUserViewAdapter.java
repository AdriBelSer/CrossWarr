package com.yinya.crosswarr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yinya.crosswarr.R;
import com.yinya.crosswarr.databinding.CardListChallengesItemBinding;
import com.yinya.crosswarr.models.ChallengeData;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

/**
 * Adaptador para el RecyclerView que muestra la lista de desafíos en la vista de usuario estándar.
 * Se encarga de mostrar los desafíos disponibles y cruzar esa información con el historial del usuario
 * para indicar visualmente si un desafío está pendiente o completado, mostrando el tiempo invertido.
 */
public class ChallengesUserViewAdapter extends RecyclerView.Adapter<ChallengesUserViewAdapter.ChallengesViewHolder> {
    private final ArrayList<ChallengeData> challenge;
    private final Context context;
    private final OnChallengeClickListener listener;
    private Map<String, Object> userCompletedChallenges;

    /**
     * Constructor del adaptador para los desafíos del usuario.
     *
     * @param challenge               Lista de objetos {@link ChallengeData} con los desafíos disponibles.
     * @param userCompletedChallenges Mapa que contiene los IDs de los desafíos completados por el usuario como clave, y el tiempo (en milisegundos) como valor.
     * @param context                 Contexto de la aplicación o actividad.
     * @param listener                Interfaz para escuchar y gestionar los eventos de clic en las tarjetas de desafío.
     */
    public ChallengesUserViewAdapter(ArrayList<ChallengeData> challenge, Map<String, Object> userCompletedChallenges, Context context, OnChallengeClickListener listener) {
        this.challenge = challenge;
        this.userCompletedChallenges = userCompletedChallenges;
        this.context = context;
        this.listener = listener;
    }
    /**
     * Actualiza el historial de desafíos completados del usuario y refresca la lista visual.
     * Es útil cuando los datos del usuario se descargan de Firebase después de haber inicializado el adaptador.
     *
     * @param userCompletedChallenges Nuevo mapa con el historial actualizado de desafíos completados.
     */
    public void setCompletedChallenges(Map<String, Object> userCompletedChallenges) {
        this.userCompletedChallenges = userCompletedChallenges;
        notifyDataSetChanged();
    }

    /**
     * Se llama cuando el RecyclerView necesita un nuevo {@link ChallengesViewHolder}
     * para representar un elemento.
     *
     * @param parent   El ViewGroup al que se añadirá la nueva vista.
     * @param viewType El tipo de vista del nuevo elemento.
     * @return Un nuevo ChallengesViewHolder que contiene la vista inflada (tarjeta del desafío).
     */
    @NonNull
    @Override
    public ChallengesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardListChallengesItemBinding binding = CardListChallengesItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChallengesViewHolder(binding);
    }

    /**
     * Se llama por el RecyclerView para mostrar los datos en una posición específica.
     * Pasa tanto los datos del desafío como el mapa de completados al ViewHolder.
     *
     * @param holder   El ViewHolder que debe ser actualizado.
     * @param position La posición del elemento dentro de la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull ChallengesViewHolder holder, int position) {
        ChallengeData currentChallenge = this.challenge.get(position);
        holder.bind(currentChallenge, userCompletedChallenges);
        // Configura el clic en toda la tarjeta
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onChallengeClick(currentChallenge, view);
            }
        });
    }

    /**
     * Devuelve el número total de elementos en el conjunto de datos del adaptador.
     *
     * @return El tamaño de la lista de desafíos.
     */
    @Override
    public int getItemCount() {
        return challenge.size();
    }

    /**
     * Clase interna estática que representa el ViewHolder para los elementos del RecyclerView de los usuarios.
     * Contiene la lógica visual para mostrar fechas, estados (completado/pendiente) y tiempos.
     */
    public static class ChallengesViewHolder extends RecyclerView.ViewHolder {
        private final CardListChallengesItemBinding binding;

        /**
         * Constructor del ViewHolder.
         *
         * @param binding El objeto de View Binding que contiene las referencias a las vistas de la tarjeta.
         */
        public ChallengesViewHolder(@NonNull CardListChallengesItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Vincula los datos del desafío a la interfaz de usuario.
         * Comprueba si el ID del desafío existe en el mapa de desafíos completados del usuario
         * para cambiar el color, el texto de estado y mostrar el tiempo registrado.
         *
         * @param challenge               El objeto {@link ChallengeData} con los detalles del desafío.
         * @param userCompletedChallenges El mapa con el historial del usuario para cruzar los datos.
         */
        public void bind(ChallengeData challenge, Map<String, Object> userCompletedChallenges) {
            android.content.Context context = binding.getRoot().getContext();
            int successColor = androidx.core.content.ContextCompat.getColor(context, R.color.md_theme_success);
            int neutralColor = androidx.core.content.ContextCompat.getColor(context, R.color.md_theme_onSurfaceVariant);

            // Lógica de la fecha
            if (challenge.getActivationDate() != null) {
                java.util.Date date = challenge.getActivationDate().toDate();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                binding.tvChallengeDateCardListChallengesItem.setText(sdf.format(date));
            } else {
                binding.tvChallengeDateCardListChallengesItem.setText(R.string.challenges_user_view_adapter_bind_noData_message);
            }

            // Lógica de completado y tiempo
            if (userCompletedChallenges != null && userCompletedChallenges.containsKey(challenge.getId())) {

                // El usuario ha completado este reto
                binding.tvChallengeStatusCardListChallengesItem.setText(R.string.challenges_user_view_adapter_bind_completed_message);
                binding.tvChallengeStatusCardListChallengesItem.setTextColor(android.content.res.ColorStateList.valueOf(successColor));
                binding.tvChallengeTimeCardListChallengesItem.setVisibility(android.view.View.VISIBLE);

                // Recuperamos el tiempo de Firebase
                Object timeObj = userCompletedChallenges.get(challenge.getId());
                long timeInMillis = 0;

                // Soporte para los distintos tipos numéricos que puede devolver Firestore
                if (timeObj instanceof Long) {
                    timeInMillis = (Long) timeObj;
                } else if (timeObj instanceof Integer) {
                    timeInMillis = ((Integer) timeObj).longValue();
                }

                // Transformamos a minutos y segundos
                int minutes = (int) (timeInMillis / 1000) / 60;
                int seconds = (int) (timeInMillis / 1000) % 60;
                binding.tvChallengeTimeCardListChallengesItem.setText(context.getString(R.string.challenges_user_view_adapter_bind_your_time, minutes, seconds));

            } else {
                // No lo ha completado
                binding.tvChallengeStatusCardListChallengesItem.setText(R.string.challenges_user_view_adapter_bind_pending_message);
                binding.tvChallengeStatusCardListChallengesItem.setTextColor(android.content.res.ColorStateList.valueOf(neutralColor));
                binding.tvChallengeTimeCardListChallengesItem.setVisibility(android.view.View.GONE);
            }
        }
    }
}


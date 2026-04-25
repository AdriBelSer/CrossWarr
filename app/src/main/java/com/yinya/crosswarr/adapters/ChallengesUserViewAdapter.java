package com.yinya.crosswarr.adapters;

import android.content.Context;
import android.graphics.Color;
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

public class ChallengesUserViewAdapter extends RecyclerView.Adapter<ChallengesUserViewAdapter.ChallengesViewHolder> {
    private final ArrayList<ChallengeData> challenge;
    private final Context context;
    private final OnChallengeClickListener listener;
    private Map<String, Object> userCompletedChallenges;

    public ChallengesUserViewAdapter(ArrayList<ChallengeData> challenge, Map<String, Object> userCompletedChallenges, Context context, OnChallengeClickListener listener) {
        this.challenge = challenge;
        this.userCompletedChallenges = userCompletedChallenges;
        this.context = context;
        this.listener = listener;
    }

    public void setCompletedChallenges(Map<String, Object> userCompletedChallenges) {
        this.userCompletedChallenges = userCompletedChallenges;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChallengesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardListChallengesItemBinding binding = CardListChallengesItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChallengesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengesViewHolder holder, int position) {
        ChallengeData currentChallenge = this.challenge.get(position);
        holder.bind(currentChallenge, userCompletedChallenges);
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onChallengeClick(currentChallenge, view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return challenge.size();
    }

    public static class ChallengesViewHolder extends RecyclerView.ViewHolder {
        private final CardListChallengesItemBinding binding;

        public ChallengesViewHolder(@NonNull CardListChallengesItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ChallengeData challenge, Map<String, Object> userCompletedChallenges) {
            android.content.Context context = binding.getRoot().getContext();
            int successColor = androidx.core.content.ContextCompat.getColor(context, R.color.md_theme_success);
            int neutralColor = androidx.core.content.ContextCompat.getColor(context, R.color.md_theme_onSurfaceVariant);
            // 1. FECHA
            if (challenge.getActivationDate() != null) {
                java.util.Date date = challenge.getActivationDate().toDate();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                binding.tvChallengeDateCardListChallengesItem.setText(sdf.format(date));
            } else {
                binding.tvChallengeDateCardListChallengesItem.setText(R.string.challenges_user_view_adapter_bind_noData_message);
            }

            // LÓGICA DE COMPLETADO Y TIEMPO
            if (userCompletedChallenges != null && userCompletedChallenges.containsKey(challenge.getId())) {
                // El usuario ha completado este reto
                binding.tvChallengeStatusCardListChallengesItem.setText(R.string.challenges_user_view_adapter_bind_completed_message);
                binding.tvChallengeStatusCardListChallengesItem.setTextColor(android.content.res.ColorStateList.valueOf(successColor));
                binding.tvChallengeTimeCardListChallengesItem.setVisibility(android.view.View.VISIBLE);

                // Recuperamos el tiempo de Firebase
                Object timeObj = userCompletedChallenges.get(challenge.getId());
                long timeInMillis = 0;

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


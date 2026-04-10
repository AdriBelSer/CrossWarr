package com.yinya.crosswarr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yinya.crosswarr.databinding.CardListChallengesItemBinding;
import com.yinya.crosswarr.models.ChallengeData;

import java.util.ArrayList;

public class ChallengesUserViewAdapter extends RecyclerView.Adapter<ChallengesUserViewAdapter.ChallengesViewHolder> {
    private final ArrayList<ChallengeData> challenge;
    private final Context context;
    private final OnChallengeClickListener listener;

    public ChallengesUserViewAdapter(ArrayList<ChallengeData> challenge, Context context, OnChallengeClickListener listener) {
        this.challenge = challenge;
        this.context = context;
        this.listener = listener;
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
        holder.bind(currentChallenge);
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

        public void bind(ChallengeData challenge) {
            binding.tvChallengeDateCardListChallengesItem.setText(challenge.getActivationDate().toString());

            //Comprobar que la fecha no venga nula desde Firebase
            if (challenge.getActivationDate() != null) {

                //Convertir el "Timestamp" de Firebase a un "Date"
                java.util.Date date = challenge.getActivationDate().toDate();

                // formato DD-MM-YYYY
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());

                binding.tvChallengeDateCardListChallengesItem.setText(sdf.format(date));

            } else {
                // Texto de seguridad por si algún challenge antiguo no tiene fecha
                binding.tvChallengeDateCardListChallengesItem.setText("Fecha no disponible");
            }
        }
    }
}

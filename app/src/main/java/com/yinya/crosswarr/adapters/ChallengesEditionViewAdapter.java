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

public class ChallengesEditionViewAdapter extends RecyclerView.Adapter<ChallengesEditionViewAdapter.ChallengesEditionViewHolder> {
    private final ArrayList<ChallengeData> challenge;
    private final Context context;
    private final OnChallengeAdminListener listener;

    public ChallengesEditionViewAdapter(ArrayList<ChallengeData> challenge, Context context, OnChallengeAdminListener listener) {
        this.challenge = challenge;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChallengesEditionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardListChallengesEditionItemBinding binding = CardListChallengesEditionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChallengesEditionViewHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull ChallengesEditionViewAdapter.ChallengesEditionViewHolder holder, int position) {
        ChallengeData currentChallenge = this.challenge.get(position);
        holder.bind(currentChallenge);
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onChallengeClick(currentChallenge, view);
            }
        });
        holder.binding.btnDeleteChallenge.setOnClickListener(view -> {
            if (listener != null) {
                listener.onDeleteClick(currentChallenge);
            }
        });
    }

    @Override
    public int getItemCount() {
        return challenge.size();
    }

    public static class ChallengesEditionViewHolder extends RecyclerView.ViewHolder {
        private final CardListChallengesEditionItemBinding binding;
        public ChallengesEditionViewHolder(@NonNull CardListChallengesEditionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
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

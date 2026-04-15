package com.yinya.crosswarr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yinya.crosswarr.databinding.CardListUsersEditionItemBinding;
import com.yinya.crosswarr.models.UserData;

import java.util.ArrayList;

public class UsersEditionViewAdapter extends RecyclerView.Adapter<UsersEditionViewAdapter.UsersEditionViewHolder>{
    private final ArrayList<UserData> user;
    private final Context context;
    private final OnUserAdminListener listener;

    public UsersEditionViewAdapter(ArrayList<UserData> user, Context context, OnUserAdminListener listener) {
        this.user = user;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsersEditionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardListUsersEditionItemBinding binding = CardListUsersEditionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UsersEditionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersEditionViewAdapter.UsersEditionViewHolder holder, int position) {
        UserData currentUser = this.user.get(position);
        holder.bind(currentUser);
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onUserClick(currentUser, view);
            }
        });
        holder.binding.btnDeleteUser.setOnClickListener(view -> {
            if (listener != null) {
                listener.onDeleteClick(currentUser);
            }
        });
    }

    @Override
    public int getItemCount() {
        return user.size();
    }

    public static class UsersEditionViewHolder extends RecyclerView.ViewHolder {
        private final CardListUsersEditionItemBinding binding;

        public UsersEditionViewHolder(@NonNull CardListUsersEditionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(UserData user) {
            binding.tvUserNameCardListUsersEditionItem.setText(user.getName());
            binding.tvUserEmailCardListUsersEditionItem.setText(user.getEmail());
            if (user.getRole().equals("admin")) {
                binding.tvUserRoleCardListUsersEditionItem.setText("Administrador");
            } else {
                binding.tvUserRoleCardListUsersEditionItem.setText("Usuario");
            }
        }
    }

}

package com.yinya.crosswarr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yinya.crosswarr.adapters.OnUserAdminListener;
import com.yinya.crosswarr.adapters.UsersEditionViewAdapter;
import com.yinya.crosswarr.databinding.FragmentUsersEditionListBinding;
import com.yinya.crosswarr.models.UserData;
import com.yinya.crosswarr.repository.Repository;

import java.util.ArrayList;

public class UsersEditionList extends Fragment {
    private FragmentUsersEditionListBinding binding;
    private ArrayList<UserData> users;
    private UsersEditionViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsersEditionListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        users = new ArrayList<>();
        adapter = new UsersEditionViewAdapter(users, getContext(), new OnUserAdminListener() {
            @Override
            public void onUserClick(UserData user, View view) {
                userAdminClicked(user, view);
            }

            @Override
            public void onDeleteClick(UserData user) {
                new android.app.AlertDialog.Builder(requireContext())
                        .setTitle(R.string.User_edition_list_delete_title)
                        .setMessage(requireContext().getString(R.string.User_edition_list_delete_message))
                        .setPositiveButton(R.string.User_edition_list_delete_yes_btn, (dialog, which) -> {
                            Repository.getInstance().deleteUser(user);
                            users.remove(user);
                            adapter.notifyDataSetChanged();
                            android.widget.Toast.makeText(requireContext(), R.string.User_edition_list_delete_success, android.widget.Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(R.string.User_edition_list_delete_cancel_btn, null)
                        .show();
            }


        });
        binding.usersEditionRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.usersEditionRecyclerview.setAdapter(adapter);

        setupObservers();
        loadUsers();
    }

    private void setupObservers() {
        Repository.getInstance().getUsersLiveData().observe(getViewLifecycleOwner(), listFromFirebase -> {
            if (listFromFirebase != null) {
                users.clear();
                users.addAll(listFromFirebase);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    com.yinya.crosswarr.SkeletonUtils.hideSkeleton(
                            binding.shimmerViewContainer,
                            binding.usersEditionRecyclerview
                    );
                }
            }
        });
    }

    private void loadUsers() {
        Repository.getInstance().fetchUsersFromFirebase();
    }
    private void userAdminClicked(UserData user, View view) {
        Bundle bundle = new Bundle();
        bundle.putString("id", user.getUid());
        bundle.putString("name", user.getName());
        bundle.putString("email", user.getEmail());
        bundle.putString("photo", user.getPhoto());
        bundle.putString("role", user.getRole());
        bundle.putString("notificationPushToken", user.getNotificationPushToken());
        if (user.getSettings() != null) {
            bundle.putSerializable("settings", (java.io.Serializable) user.getSettings());
        }
        if (user.getChallenges() != null) {
            bundle.putSerializable("challenges", (java.io.Serializable) user.getChallenges());
        }
        if (user.getAccountCreationDate() != null) {
            bundle.putString("accountCreationDate", user.getAccountCreationDate().toString());
        } else {
            // Si el usuario es antiguo y no tiene fecha, le pasamos un texto por defecto
            bundle.putString("accountCreationDate", "Fecha desconocida");
        }

        Navigation.findNavController(view).navigate(R.id.usersEdition, bundle);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null && binding.usersEditionRecyclerview != null) {
            binding.usersEditionRecyclerview.setAdapter(null);
        }
        adapter = null;
        binding = null;
    }
}

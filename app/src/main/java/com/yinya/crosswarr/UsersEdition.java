package com.yinya.crosswarr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.Timestamp;
import com.yinya.crosswarr.databinding.FragmentUsersEditionBinding;
import com.yinya.crosswarr.models.UserData;
import com.yinya.crosswarr.repository.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UsersEdition extends Fragment {
    private FragmentUsersEditionBinding binding;
    private String currentUid = null;
    private String currentPhoto = null;
    private Timestamp currentCreationDate = null;
    private String currentPushToken = null;
    private Map<String, Object> currentSettings = new HashMap<>();
    private List<Map<String, Object>> currentChallenges = new java.util.ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
        binding = FragmentUsersEditionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null && getArguments().containsKey("id")) {
            currentUid = getArguments().getString("id");
            String name = getArguments().getString("name");
            String email = getArguments().getString("email");
            String role = getArguments().getString("role");
            currentPhoto = getArguments().getString("photo");

            binding.etNameFragmentUsersEdition.setText(name);
            binding.etEmailFragmentUsersEdition.setText(email);

            binding.switchRoleFragmentUsersEdition.setChecked("admin".equals(role));

            // Recuperar Mapas (Si existen)
            if (getArguments().containsKey("settings")) {
                currentSettings = (Map<String, Object>) getArguments().getSerializable("settings");
            }
            if (getArguments().containsKey("challenges")) {
                currentChallenges = (List<Map<String, Object>>) getArguments().getSerializable("challenges");
            }
        }

        binding.btnCreateUser.setOnClickListener(v -> saveUser());
    }

    private void saveUser() {
        String name = binding.etNameFragmentUsersEdition.getText().toString().trim();
        String email = binding.etEmailFragmentUsersEdition.getText().toString().trim();

        String role = binding.switchRoleFragmentUsersEdition.isChecked() ? "admin" : "user";

        if (name.isEmpty() || email.isEmpty()) {
            android.widget.Toast.makeText(requireContext(), R.string.users_edition_toast_error_empty_data, android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUid == null) {
            currentUid = UUID.randomUUID().toString();
            currentCreationDate = Timestamp.now();
        }

        UserData savedUser = new UserData(
                currentUid,
                email,
                name,
                currentPhoto,
                role,
                currentCreationDate,
                currentPushToken,
                currentSettings,
                currentChallenges
        );

        Repository.getInstance().createUser(savedUser);

        android.widget.Toast.makeText(requireContext(), R.string.users_edition_toast_save_successfull, android.widget.Toast.LENGTH_SHORT).show();
        goBackToUsers();
    }

    private void goBackToUsers() {
        Navigation.findNavController(requireView()).popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


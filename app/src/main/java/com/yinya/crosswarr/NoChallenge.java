package com.yinya.crosswarr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.yinya.crosswarr.databinding.FragmentNoChallengeBinding;

public class NoChallenge extends Fragment {
    FragmentNoChallengeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNoChallengeBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }
}
package com.ualberta.eventlottery.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ualberta.static2.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // --- 1. Observe LiveData to update TextViews automatically ---
        profileViewModel.getName().observe(getViewLifecycleOwner(), name ->
                binding.textName.setText(name)
        );

        profileViewModel.getEmail().observe(getViewLifecycleOwner(), email ->
                binding.textEmail.setText(email)
        );



        // --- 2. Populate EditTexts with current values ---
        binding.editName.setText(profileViewModel.getName().getValue());
        binding.editEmail.setText(profileViewModel.getEmail().getValue());


        // --- 3. Save button updates the ViewModel ---
        binding.buttonSave.setOnClickListener(v -> {
            profileViewModel.setName(binding.editName.getText().toString());
            profileViewModel.setEmail(binding.editEmail.getText().toString());
        });

        // TODO: Later load data from Firebase if needed


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

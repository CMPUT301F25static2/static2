package com.ualberta.eventlottery.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);


        String userId = UserManager.getCurrentUserId();
        profileViewModel.loadProfileFromFirebase(userId);


        profileViewModel.getName().observe(getViewLifecycleOwner(), name -> {
            binding.textName.setText(name);
            binding.editName.setText(name);
        });

        profileViewModel.getEmail().observe(getViewLifecycleOwner(), email -> {
            binding.textEmail.setText(email);
            binding.editEmail.setText(email);
        });


        profileViewModel.getPhone().observe(getViewLifecycleOwner(), phone -> {
            binding.editPhone.setText(phone);  // Remove the null check
        });

        profileViewModel.getFavoriteRecCenter().observe(getViewLifecycleOwner(), recCenter -> {
            binding.editRecCenter.setText(recCenter);
        });


        profileViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.buttonSave.setEnabled(!isLoading);  // Remove the null check
        });


        profileViewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });


        binding.buttonSave.setOnClickListener(v -> {
            profileViewModel.setName(binding.editName.getText().toString());
            profileViewModel.setEmail(binding.editEmail.getText().toString());
            profileViewModel.setPhone(binding.editPhone.getText().toString());
            profileViewModel.setFavoriteRecCenter(binding.editRecCenter.getText().toString());


            profileViewModel.saveProfileToFirebase(userId);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
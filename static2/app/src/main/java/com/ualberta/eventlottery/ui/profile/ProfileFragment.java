package com.ualberta.eventlottery.ui.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private String userId;
    private String isAdmin;
    private FirebaseFirestore db;

    private Boolean previousNotificationsEnabled = null;

    private ActivityResultLauncher<String> requestNotificationPermissionLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Handle admin access
        if (getArguments() != null) {
            isAdmin = getArguments().getString("isAdmin");
            if (isAdmin != null && isAdmin.equals("true")) {
                userId = getArguments().getString("userId");
                Toast.makeText(getContext(), "Admin access granted", Toast.LENGTH_SHORT).show();
            }
        } else {
            userId = UserManager.getCurrentUserId();
        }

        profileViewModel.loadProfileFromFirebase(userId);

        // Observe profile fields
        profileViewModel.getName().observe(getViewLifecycleOwner(), name -> {
            binding.textName.setText(name);
            binding.editName.setText(name);
        });

        profileViewModel.getEmail().observe(getViewLifecycleOwner(), email -> {
            binding.textEmail.setText(email);
            binding.editEmail.setText(email);
        });

        profileViewModel.getPhone().observe(getViewLifecycleOwner(), phone -> {
            binding.editPhone.setText(phone);
        });

        profileViewModel.getFavoriteRecCenter().observe(getViewLifecycleOwner(), recCenter -> {
            binding.editRecCenter.setText(recCenter);
        });

        profileViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.buttonSave.setEnabled(!isLoading);
        });

        profileViewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe notifications
        profileViewModel.getNotificationsEnabled().observe(getViewLifecycleOwner(), enabled -> {
            binding.switchNotifications.setChecked(enabled != null && enabled);
            previousNotificationsEnabled = enabled;
        });

        // Initialize permission launcher
        requestNotificationPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        Toast.makeText(getContext(), "Notifications enabled", Toast.LENGTH_SHORT).show();
                        profileViewModel.setNotificationsEnabled(true);
                    } else {
                        Toast.makeText(getContext(), "Notifications denied", Toast.LENGTH_SHORT).show();
                        binding.switchNotifications.setChecked(false);
                        profileViewModel.setNotificationsEnabled(false);
                    }
                });

        // Save button click
        binding.buttonSave.setOnClickListener(v -> {
            // Save other profile fields
            profileViewModel.setName(binding.editName.getText().toString());
            profileViewModel.setEmail(binding.editEmail.getText().toString());
            profileViewModel.setPhone(binding.editPhone.getText().toString());
            profileViewModel.setFavoriteRecCenter(binding.editRecCenter.getText().toString());

            boolean switchOn = binding.switchNotifications.isChecked();

            if (switchOn && (previousNotificationsEnabled == null || !previousNotificationsEnabled)) {
                // Only request permission if previously disabled
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                        return; // Wait for permission result
                    }
                }
                // Permission already granted -> set notificationsEnabled before saving
                profileViewModel.setNotificationsEnabled(true);
            } else {
                // Either switch is off or previously enabled
                profileViewModel.setNotificationsEnabled(switchOn);
            }

            // Save profile after handling notifications
            profileViewModel.saveProfileToFirebase(userId);
            previousNotificationsEnabled = switchOn;
        });

        // Delete user button
        binding.buttonDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete User")
                    .setMessage("Are you sure you want to delete this user? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        deleteUser(userId);
                        deleteOrganizedEvents(userId);
                        deleteUserRegistrations(userId);
                        requireActivity().onBackPressed();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // User deletion
    public void deleteUser(String userId) {
        db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("users").document(userId);
        userDocRef.delete();
        Toast.makeText(getContext(), "Profile deleted", Toast.LENGTH_SHORT).show();
    }

    public void deleteOrganizedEvents(String userId) {
        db = FirebaseFirestore.getInstance();
        CollectionReference eventDocRef = db.collection("events");

        eventDocRef.get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                if (doc != null && userId.equals(doc.getString("organizerId"))) {
                    String eventId = doc.getId();
                    deleteOtherUserRegistrations(eventId);
                    eventDocRef.document(eventId).delete();
                }
            }
        });
    }

    public void deleteUserRegistrations(String userId) {
        db = FirebaseFirestore.getInstance();
        CollectionReference registrationDocRef = db.collection("registrations");

        registrationDocRef.get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                if (doc != null && userId.equals(doc.getString("entrantId"))) {
                    registrationDocRef.document(doc.getId()).delete();
                }
            }
        });
    }

    public void deleteOtherUserRegistrations(String eventId) {
        db = FirebaseFirestore.getInstance();
        CollectionReference registrationDocRef = db.collection("registrations");

        registrationDocRef.get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                if (doc != null && eventId.equals(doc.getString("eventId"))) {
                    registrationDocRef.document(doc.getId()).delete();
                }
            }
        });
    }
}

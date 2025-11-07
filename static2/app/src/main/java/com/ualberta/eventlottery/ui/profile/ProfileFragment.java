package com.ualberta.eventlottery.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private String userId;
    private String isAdmin;
    private FirebaseFirestore db;
    private CollectionReference docRef;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

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

        binding.buttonDelete.setOnClickListener(v -> {
            deleteUser(userId);
            deleteOrganizedEvents(userId);
            deleteUseregistrations(userId);
            requireActivity().onBackPressed();

        });


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void deleteUser(String userId) {
        db = FirebaseFirestore.getInstance();

        DocumentReference userDocRef = db.collection("users").document(userId);
        userDocRef.delete();
        Toast.makeText(getContext(), "Profile deleted", Toast.LENGTH_SHORT).show();
    }

    public void deleteOrganizedEvents(String userId) {
        CollectionReference eventDocRef = db.collection("events");

        eventDocRef.get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                if (doc != null && doc.getString("organizerId") != null && doc.getString("organizerId").trim().equals(userId)) {
                    String eventId = doc.getId();
                    deleteOtherUserRegistrations(eventId);
                    eventDocRef.document(doc.getId()).delete();
                }
            }
        });
    }

    public void deleteUseregistrations(String userId) {
        CollectionReference registrationDocRef = db.collection("registrations");

        registrationDocRef.get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                if (doc != null && doc.getString("entrantId") != null && doc.getString("entrantId").trim().equals(userId)) {
                    registrationDocRef.document(doc.getId()).delete();
                }
            }
        });

    }

    public void deleteOtherUserRegistrations(String eventId) {
        CollectionReference registrationDocRef = db.collection("registrations");

        registrationDocRef.get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                if (doc != null && doc.getString("eventId") != null && doc.getString("eventId").trim().equals(eventId)) {
                    registrationDocRef.document(doc.getId()).delete();
                }
            }
        });
    }
}
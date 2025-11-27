package com.ualberta.eventlottery.ui.profile;

import android.app.AlertDialog;
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



    /**
     * Called to have the fragment instantiate its user interface view. [11]
     * This is optional, and non-graphical fragments can return null. This will be called between
     * {@link #onCreate(Bundle)} and {@link #onViewCreated(View, Bundle)}. [19]
     * <p>A default View can be returned by calling {@link #Fragment(int)} in your
     * constructor. Otherwise, this method returns null.
     *
     * <p>It is recommended to <strong>only</strong> inflate the layout in this method and move
     * logic that operates on the returned View to {@link #onViewCreated(View, Bundle)}.
     *
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to. The fragment should not add the view
     * itself, but this can be used to generate the LayoutParams of
     * the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
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

        profileViewModel.getNotificationsEnabled().observe(getViewLifecycleOwner(), enabled -> {
            binding.switchNotifications.setChecked(enabled != null && enabled);
        });

        // Handle user toggling the switch
        binding.switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            profileViewModel.setNotificationsEnabled(isChecked);
        });


        binding.buttonSave.setOnClickListener(v -> {
            profileViewModel.setName(binding.editName.getText().toString());
            profileViewModel.setEmail(binding.editEmail.getText().toString());
            profileViewModel.setPhone(binding.editPhone.getText().toString());
            profileViewModel.setFavoriteRecCenter(binding.editRecCenter.getText().toString());


            profileViewModel.saveProfileToFirebase(userId);
        });

        binding.buttonDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete User")
                    .setMessage("Are you sure you want to delete this user? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // User confirmed deletion
                        deleteUser(userId);
                        deleteOrganizedEvents(userId);
                        deleteUserRegistrations(userId);
                        requireActivity().onBackPressed();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // User cancelled, dialog will dismiss automatically
                        dialog.dismiss();
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert) // Optional: adds an icon
                    .show();
        });
        return binding.getRoot();
    }

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Deletes a user from the database.
     * @param userId
     */
    public void deleteUser(String userId) {
        db = FirebaseFirestore.getInstance();

        DocumentReference userDocRef = db.collection("users").document(userId);
        userDocRef.delete();
        Toast.makeText(getContext(), "Profile deleted", Toast.LENGTH_SHORT).show();
    }

    /**
     * Deletes all events organized by a user.
     * @param userId
     */
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

    /**
     * Deletes all registrations made by a user.
     * @param userId
     */
    public void deleteUserRegistrations(String userId) {
        CollectionReference registrationDocRef = db.collection("registrations");

        registrationDocRef.get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                if (doc != null && doc.getString("entrantId") != null && doc.getString("entrantId").trim().equals(userId)) {
                    registrationDocRef.document(doc.getId()).delete();
                }
            }
        });

    }

    /**
     * Deletes all registrations made by other users for events organized by the deleted user.
     * @param eventId
     */
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

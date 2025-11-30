package com.ualberta.eventlottery.ui.adminUsers;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ualberta.eventlottery.MainActivity;
import com.ualberta.eventlottery.admin.AdminMainActivity;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.User;
import com.ualberta.eventlottery.ui.organizer.adapter.OrganizerEventAdapter;
import com.ualberta.eventlottery.ui.organizer.organizerEventInfo.OrganizerEventInfoFragment;
import com.ualberta.eventlottery.ui.profile.ProfileFragment;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentAdminUsersBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lumbani
 * @version 1.0
 * This is a class that serves as the users screen for the admin.
 */
public class AdminUsersFragment extends Fragment {
    private final ArrayList<User> masterList = new ArrayList<>();
    private final ArrayList<User> displayList = new ArrayList<>();
    private UserAdapter adapter;
    private String selectedTypeFilter = null;
    private String searchText = "";

    private FirebaseFirestore db;

    private CollectionReference usersRef;
    private com.ualberta.static2.databinding.FragmentAdminUsersBinding binding;

    /**
     * Called to have the fragment initiate the search users fragment
     * Sets up the user list for the admin
     * Sets up the search bar for the admin
     * Sets up the sort buttons for the admin
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAdminUsersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.adminBackButton.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        adapter = new UserAdapter(requireContext(), displayList);
        binding.userListView.setAdapter(adapter);

        // Listen for changes to the users collection
        // If a change is detected, update the list of users
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        usersRef.addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", error.toString());
                    }
                    if (value != null && !value.isEmpty()) {
                        masterList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            String userId = snapshot.getId();
                            String name = snapshot.getString("name");
                            String email = snapshot.getString("email");
                            String phone = snapshot.getString("phone");
                            String favRecCenter = snapshot.getString("favRecCenter");
                            String userType = snapshot.getString("userType");
                            Boolean notificationsEnabled = snapshot.getBoolean("notificationsEnabled");

                            masterList.add(new User(userId, name, email, phone, "fcmToken", userType, favRecCenter, notificationsEnabled));
                        }
                        selectedTypeFilter = null;
                        searchText = "";
                        applyFilter();
                    }
                });

        // Sort by type of user
        // Updates the list of users when a sort button is clicked
        binding.sortButtonUsersEntrants.setOnClickListener(v -> {
            selectedTypeFilter = "entrant";
            applyFilter();
            binding.sortButtonUsersEntrants.setTextColor(getResources().getColorStateList(R.color.black, null));
            binding.sortButtonUsersOrganizers.setTextColor(getResources().getColorStateList(R.color.white, null));
            binding.sortButtonUsersAdmins.setTextColor(getResources().getColorStateList(R.color.white, null));
            binding.sortButtonUsersEntrants.setBackgroundTintList(getResources().getColorStateList(R.color.white, null));
            binding.sortButtonUsersOrganizers.setBackgroundTintList(getResources().getColorStateList(R.color.black, null));
            binding.sortButtonUsersAdmins.setBackgroundTintList(getResources().getColorStateList(R.color.black, null));

        });

        binding.sortButtonUsersOrganizers.setOnClickListener(v -> {
            selectedTypeFilter = "organizer";
            applyFilter();
            binding.sortButtonUsersEntrants.setTextColor(getResources().getColorStateList(R.color.white, null));
            binding.sortButtonUsersOrganizers.setTextColor(getResources().getColorStateList(R.color.black, null));
            binding.sortButtonUsersAdmins.setTextColor(getResources().getColorStateList(R.color.white, null));
            binding.sortButtonUsersEntrants.setBackgroundTintList(getResources().getColorStateList(R.color.black, null));
            binding.sortButtonUsersOrganizers.setBackgroundTintList(getResources().getColorStateList(R.color.white, null));
            binding.sortButtonUsersAdmins.setBackgroundTintList(getResources().getColorStateList(R.color.black, null));
        });

        binding.sortButtonUsersAdmins.setOnClickListener(v -> {
            selectedTypeFilter = "admin";
            applyFilter();
            binding.sortButtonUsersEntrants.setTextColor(getResources().getColorStateList(R.color.white, null));
            binding.sortButtonUsersOrganizers.setTextColor(getResources().getColorStateList(R.color.white, null));
            binding.sortButtonUsersAdmins.setTextColor(getResources().getColorStateList(R.color.black, null));
            binding.sortButtonUsersEntrants.setBackgroundTintList(getResources().getColorStateList(R.color.black, null));
            binding.sortButtonUsersOrganizers.setBackgroundTintList(getResources().getColorStateList(R.color.black, null));
            binding.sortButtonUsersAdmins.setBackgroundTintList(getResources().getColorStateList(R.color.white, null));

        });

        // Setup click listener for each user in the list
        // Passes admin status and userID to the profile fragment
        // Sends the user to the profile fragment
        binding.userListView.setOnItemClickListener((parent, view, position, id) -> {

            User user = displayList.get(position);
            Toast.makeText(requireContext(), "Clicked: " + user.getUserType(), Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putString("isAdmin", "true");
            bundle.putString("userId", user.getUserId());
            Fragment userProfile = new ProfileFragment();
            userProfile.setArguments(bundle);
            getParentFragmentManager()
                    .beginTransaction()
                    .hide(this)
                    .add(R.id.fragment_container, userProfile)
                    .addToBackStack(null)
                    .commit();
                });


        binding.searchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            /**
             * This method is called after the text has been changed.
             * Filters the users based on the search text.
             *

             * @param editable the text query
             */
            @Override
            public void afterTextChanged(Editable editable) {
                searchText = editable.toString().toLowerCase();
                applyFilter();
            }
        });
        return root;
    }

    /**
     * Called when the fragment is no longer in use.
     * When the fragment is reopened after going to a new fragment, the adapter is updated.
     * Accounts for any changes in the list of users.

     * @param hidden True if the fragment is now hidden, false otherwise.
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            adapter.notifyDataSetChanged(); // Update the adapter when the fragment is shown again
        }
    }

    /**
     * Called when the fragment is no longer in use.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Applies a filter to the list of users.
     * Filters by user type and search text.
     */
    public void applyFilter() {
        displayList.clear();
        for (User u : masterList) {
            if (selectedTypeFilter != null && ! selectedTypeFilter.equals(u.getUserType())) {
                continue;
            }
            if (! searchText.isEmpty() && ! u.getName().toLowerCase().contains(searchText)) {
                continue;
            }
            displayList.add(u);
        }
        adapter.notifyDataSetChanged();
    }





}
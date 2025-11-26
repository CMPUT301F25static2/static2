package com.ualberta.eventlottery.ui.adminEvents;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ualberta.eventlottery.MainActivity;
import com.ualberta.eventlottery.admin.AdminMainActivity;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.User;
import com.ualberta.eventlottery.ui.adminUsers.UserAdapter;
import com.ualberta.eventlottery.ui.home.entrant.EventAdapter;
import com.ualberta.eventlottery.ui.home.entrant.HomeFragment;
import com.ualberta.eventlottery.ui.organizer.adapter.OrganizerEventAdapter;
import com.ualberta.eventlottery.ui.organizer.organizerEventInfo.OrganizerEventInfoFragment;
import com.ualberta.eventlottery.ui.organizer.organizerHome.OrganizerHomeFragment;
import com.ualberta.eventlottery.ui.profile.ProfileFragment;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentAdminEventsBinding;
import com.ualberta.static2.databinding.FragmentAdminUsersBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a class that serves as the events screen for the admin.
 */
public class AdminEventFragment extends Fragment implements EventAdapter.OnEventListener {

    private ArrayList<Event> eventArrayList;

    private ArrayList<Event> filtered;
    private EventAdapter eventArrayAdapter;

    private EventAdapter filteredEventAdapter;

    private FirebaseFirestore db;

    private RecyclerView recyclerView;

    private CollectionReference eventsRef;

    private View.OnClickListener onClickListener;

    private EventAdapter.OnEventListener onItemClickListener;

    private String name;
    private boolean deletion;



    private com.ualberta.static2.databinding.FragmentAdminEventsBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.adminBackButton.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        recyclerView = root.findViewById(R.id.adminEventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        eventArrayList = new ArrayList<>();
        eventArrayAdapter = new EventAdapter(eventArrayList, this);
        recyclerView.setAdapter(eventArrayAdapter);


        eventsRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
            }
            if (value != null && !value.isEmpty()) {
                eventArrayList.clear();
                for (QueryDocumentSnapshot snapshot : value) {
                    String id = snapshot.getId();
                    String organizerId = snapshot.getString("organizerId");
                    String title = snapshot.getString("title");
                    String description = snapshot.getString("description");

                    eventArrayList.add(new Event(id, organizerId, title, description));
                }

                // dummy data
                // dummy data

                eventArrayAdapter.notifyDataSetChanged();;
            }
        });
        binding.viewButtonEvents.setOnClickListener(v -> {
            deletion = false;
            binding.viewButtonEvents.setTextColor(getResources().getColorStateList(R.color.black, null));
            binding.deleteButtonEvents.setTextColor(getResources().getColorStateList(R.color.white, null));
            binding.viewButtonEvents.setBackgroundTintList(getResources().getColorStateList(R.color.white, null));
            binding.deleteButtonEvents.setBackgroundTintList(getResources().getColorStateList(R.color.black, null));

        });

        binding.deleteButtonEvents.setOnClickListener(v -> {
            deletion = true;
            binding.deleteButtonEvents.setTextColor(getResources().getColorStateList(R.color.black, null));
            binding.viewButtonEvents.setTextColor(getResources().getColorStateList(R.color.white, null));
            binding.viewButtonEvents.setBackgroundTintList(getResources().getColorStateList(R.color.black, null));
            binding.deleteButtonEvents.setBackgroundTintList(getResources().getColorStateList(R.color.red_deep, null));

        });

        binding.adminSearchEvents.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            /**
             * This method is called after the text has been changed.
             * Filters the events based on the search text.
             *
             * @param editable The text.
             */
            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString().toLowerCase();
                filtered = new ArrayList<>();
                filteredEventAdapter = new EventAdapter(filtered, AdminEventFragment.this::onEventClick);

                for (Event event : eventArrayList) {
                    if (event.getTitle().toLowerCase().contains(searchText.toLowerCase())){
                        filtered.add(event);
                    }
                }
                recyclerView.setAdapter(filteredEventAdapter);
                eventArrayAdapter.notifyDataSetChanged();

            }
        });


        return root;
    }

    /**
     * Called when the fragment is no longer in use.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onEventClick(Event event) {
        Toast.makeText(getContext(), "Event clicked: "+event.getTitle(), Toast.LENGTH_SHORT).show();
        if (deletion == true) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this event? This action cannot be undone." +
                            "\nEvent ID: " + event.getId() + "" +
                            "\nOrganizer ID: " + event.getOrganizerId() + "" +
                            "\nOrganizer Name: " + getName(event.getOrganizerId()))

                    .setPositiveButton("Delete", (dialog, which) -> {
                        // User confirmed deletion
                        deleteEvent(event.getId());

                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // User cancelled, dialog will dismiss automatically
                        dialog.dismiss();
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert) // Optional: adds an icon
                    .show();
        }
        else {
            OrganizerEventInfoFragment fragment = OrganizerEventInfoFragment.newInstance(event.getId());
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .hide(this)
                    .add(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void deleteEvent(String eventID) {
        CollectionReference eventDocRef = db.collection("events");

        eventDocRef.get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                if (doc != null && doc.getId().trim().equals(eventID)) {
                    String eventId = doc.getId();
                    deleteOtherUserRegistrations(eventId);
                    eventDocRef.document(doc.getId()).delete();
                }
            }
        });
    }

    public String getName(String userId) {
        CollectionReference eventDocRef = db.collection("users");
        eventDocRef.get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                if (doc != null && doc.getId().trim().equals(userId)) {
                    name = doc.getString("name");

                }
            }
        });
        return name;

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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            eventArrayAdapter.notifyDataSetChanged(); // Update the adapter when the fragment is shown again
        }

    }

}
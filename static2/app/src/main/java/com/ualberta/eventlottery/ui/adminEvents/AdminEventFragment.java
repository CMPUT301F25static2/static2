package com.ualberta.eventlottery.ui.adminEvents;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ualberta.eventlottery.MainActivity;
import com.ualberta.eventlottery.admin.AdminMainActivity;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.User;
import com.ualberta.eventlottery.ui.adminUsers.UserAdapter;
import com.ualberta.eventlottery.ui.home.entrant.EventAdapter;
import com.ualberta.eventlottery.ui.organizer.adapter.OrganizerEventAdapter;
import com.ualberta.eventlottery.ui.organizer.organizerEventInfo.OrganizerEventInfoFragment;
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
        eventArrayAdapter = new EventAdapter(eventArrayList, null);
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

                eventArrayAdapter.notifyDataSetChanged();
            }
        });
/* Fix: Event browse onClickListener
        binding.adminEventsRecyclerView.onEventClick((parent, view, position, id) -> {
                User event = userArrayList.get(position);
            Toast.makeText(requireContext(), "Clicked: " + user.getName(), Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putString("isAdmin", "true");
            bundle.putString("userId", user.getUserId());
            Fragment userProfile = new ProfileFragment();
            userProfile.setArguments(bundle);
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, userProfile)
                    .addToBackStack(null)
                    .commit();

        });

 */



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
                filteredEventAdapter = new EventAdapter(filtered, null);

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

    }

    interface onClickListener {
        void onClick(Event event, int position);

    }

}
package com.ualberta.eventlottery.ui.organizer.organizerHome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.ui.organizer.adapter.OrganizerEventAdapter;
import com.ualberta.eventlottery.ui.organizer.organizerEventCreate.OrganizerEventCreateFragment;
import com.ualberta.eventlottery.ui.organizer.organizerEventDraw.OrganizerEventDrawFragment;
import com.ualberta.eventlottery.ui.organizer.organizerEventInfo.OrganizerEventInfoFragment;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentOrganizerHomeBinding;

import java.util.List;

public class OrganizerHomeFragment extends Fragment {
    private FragmentOrganizerHomeBinding binding;
    private OrganizerEventAdapter adapter;
    private EventRepository eventRepository;
    private String organizerId;


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrganizerHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        initViews();
        setupAdapter();
        setupListener();
    }

    private void initData() {
        eventRepository = EventRepository.getInstance();
        organizerId = UserManager.getCurrentUserId();
    }

    private void initViews() {
        binding.lvOrganzierEventList.setDivider(null);
        binding.lvOrganzierEventList.setDividerHeight(18);
    }

    private void setupAdapter() {
        binding.lvOrganzierEventList.setVisibility(View.GONE);

        eventRepository.getEventsByOrganizer(organizerId, new EventRepository.EventListCallback() {
            @Override
            public void onSuccess(List<Event> events) {
                binding.lvOrganzierEventList.setVisibility(View.VISIBLE);
                setupAdapterWithData(events);

            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupAdapterWithData(List<Event> eventList) {
        adapter = new OrganizerEventAdapter(requireContext(), eventList);
        binding.lvOrganzierEventList.setAdapter(adapter);

        // set up draw button (in each item) click listener
        adapter.setOnDrawButtonClickListener(event -> {
            OrganizerEventDrawFragment fragment = OrganizerEventDrawFragment.newInstance(event.getId());
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_organizer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // set up item click listener
        binding.lvOrganzierEventList.setOnItemClickListener((parent, view, position, id) -> {
            Event selectedEvent = eventList.get(position);

            OrganizerEventInfoFragment fragment = OrganizerEventInfoFragment.newInstance(selectedEvent.getId());
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_organizer, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupListener() {
        binding.btnCreateEvent.setOnClickListener(v -> {
            OrganizerEventCreateFragment fragment = new OrganizerEventCreateFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_organizer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

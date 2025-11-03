package com.ualberta.eventlottery.ui.organizer.organizerHome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.ui.organizer.adapter.OrganizerEventAdapter;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.ui.organizer.organizerEventCreate.OrganizerEventCreateFragment;
import com.ualberta.eventlottery.ui.organizer.organizerEventInfo.OrganizerEventInfoFragment;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentOrganizerHomeBinding;

import java.util.List;

public class OrganizerHomeFragment extends Fragment {
    private FragmentOrganizerHomeBinding binding;
    private OrganizerEventAdapter adapter;
    private EventRepository eventRepo;


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
        eventRepo = EventRepository.getInstance();
    }

    private void initViews() {
        binding.lvOrganzierEventList.setDivider(null);
        binding.lvOrganzierEventList.setDividerHeight(18);
    }

    private void setupAdapter() {
        List<Event> eventList = eventRepo.getAllEvents();
        adapter = new OrganizerEventAdapter(requireContext(), eventList);
        binding.lvOrganzierEventList.setAdapter(adapter);

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

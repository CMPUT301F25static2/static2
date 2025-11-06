package com.ualberta.eventlottery.ui.organizer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ualberta.eventlottery.model.Entrant;
import com.ualberta.eventlottery.model.EntrantRegistrationStatus;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.repository.EntrantRepository;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.eventlottery.ui.organizer.adapter.EntrantAdapter;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentOrganzerEntrantListBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EntrantsFragment extends Fragment {

    private FragmentOrganzerEntrantListBinding binding;
    private List<LinearLayout> statusButtons = new ArrayList<>();
    private RegistrationRepository registrationRepository = RegistrationRepository.getInstance();
    private EntrantRepository entrantRepository = EntrantRepository.getInstance();

    private static final String ARG_EVENT_ID = "event_id";
    private String eventId;
    public static EntrantsFragment newInstance(String eventId) {
        EntrantsFragment fragment = new EntrantsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    public EntrantsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrganzerEntrantListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        receiveArguments();
        initViews();
        setupClickListeners();
        loadEntrantsData();
    }

    private void receiveArguments() {
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_EVENT_ID)) {
            eventId = args.getString(ARG_EVENT_ID);
            Toast.makeText(requireContext(), "EntrantsFragment received Event ID: " + eventId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "No event ID received in EntrantsFragment", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        statusButtons.add(binding.btnEntrantsConfirmed);
        statusButtons.add(binding.btnEntrantsWaiting);
        statusButtons.add(binding.btnEntrantsSelected);
        statusButtons.add(binding.btnEntrantsCancelled);

        loadRegistrationCounts();
    }

    private void loadRegistrationCounts() {
        registrationRepository.getRegistrationsByEvent(eventId, new RegistrationRepository.RegistrationListCallback() {
            @Override
            public void onSuccess(List<Registration> registrations) {
                int confirmed = (int) registrations.stream().filter(registration -> registration.getStatus() == EntrantRegistrationStatus.CONFIRMED).count();
                int waiting = (int) registrations.stream().filter(registration -> registration.getStatus() == EntrantRegistrationStatus.WAITING).count();
                int selected = (int) registrations.stream().filter(registration -> registration.getStatus() == EntrantRegistrationStatus.SELECTED).count();
                int cancelled = (int) registrations.stream().filter(registration -> registration.getStatus() == EntrantRegistrationStatus.CANCELLED).count();
                updateEntrantsCount(confirmed, waiting, selected, cancelled);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load registration counts", Toast.LENGTH_SHORT).show();
                // Set default counts
                updateEntrantsCount(0, 0, 0, 0);
            }
        });
    }

    private void setupClickListeners() {
        binding.btnEntrantsConfirmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateButtonSelection(binding.btnEntrantsConfirmed);
                loadConfirmedEntrants();
            }
        });

        binding.btnEntrantsWaiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateButtonSelection(binding.btnEntrantsWaiting);
                loadWaitingEntrants();
            }
        });

        binding.btnEntrantsSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateButtonSelection(binding.btnEntrantsSelected);
                loadSelectedEntrants();
            }
        });

        binding.btnEntrantsCancelled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateButtonSelection(binding.btnEntrantsCancelled);
                loadCancelledEntrants();
            }
        });

        updateButtonSelection(binding.btnEntrantsConfirmed);
    }

    private void updateButtonSelection(LinearLayout selectedButton) {
        for (LinearLayout button : statusButtons) {
            boolean isSelected = button == selectedButton;
            button.setSelected(isSelected);

            button.setBackgroundResource(R.drawable.button_selector);
        }
    }

    private void loadEntrantsData() {
        loadConfirmedEntrants();
    }

    private void loadConfirmedEntrants() {
        registrationRepository.getConfirmedRegistrationsByEvent(eventId, new RegistrationRepository.RegistrationListCallback() {
            @Override
            public void onSuccess(List<Registration> registrations) {
                List<Entrant> entrants = registrations.stream()
                        .map(Registration::getEntrantId)
                        .map(entrantRepository::findEntrantById)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                setupListView(entrants);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load confirmed entrants", Toast.LENGTH_SHORT).show();
                setupListView(new ArrayList<>());
            }
        });
    }

    private void loadWaitingEntrants() {
        registrationRepository.getWaitingRegistrationsByEvent(eventId, new RegistrationRepository.RegistrationListCallback() {
            @Override
            public void onSuccess(List<Registration> registrations) {
                List<Entrant> entrants = registrations.stream()
                        .map(Registration::getEntrantId)
                        .map(entrantRepository::findEntrantById)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                setupListView(entrants);

            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load waiting entrants", Toast.LENGTH_SHORT).show();
                setupListView(new ArrayList<>());
            }
        });
    }

    private void loadSelectedEntrants() {
        registrationRepository.getSelectedRegistrationsByEvent(eventId, new RegistrationRepository.RegistrationListCallback() {
            @Override
            public void onSuccess(List<Registration> registrations) {
                List<Entrant> entrants = registrations.stream()
                        .map(Registration::getEntrantId)
                        .map(entrantRepository::findEntrantById)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                setupListView(entrants);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load selected entrants", Toast.LENGTH_SHORT).show();
                setupListView(new ArrayList<>());
            }
        });
    }

    private void loadCancelledEntrants() {
        registrationRepository.getRegistrationsByStatus(eventId, EntrantRegistrationStatus.CANCELLED, new RegistrationRepository.RegistrationListCallback() {
            @Override
            public void onSuccess(List<Registration> registrations) {
                List<Entrant> entrants = registrations.stream()
                        .map(Registration::getEntrantId)
                        .map(entrantRepository::findEntrantById)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                setupListView(entrants);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load cancelled entrants", Toast.LENGTH_SHORT).show();
                setupListView(new ArrayList<>());
            }
        });
    }

    private void setupListView(List<Entrant> entrants) {
        binding.lvEventEntrantList.setLayoutManager(new LinearLayoutManager(requireContext()));
        EntrantAdapter adapter = new EntrantAdapter(requireContext(), entrants, eventId);
        binding.lvEventEntrantList.setAdapter(adapter);

        adapter.setOnItemClickListener(entrant -> onEntrantSelected(entrant));
    }

    private void onEntrantSelected(Entrant entrant) {
        Toast.makeText(requireContext(), "Selected: " + entrant.getName(), Toast.LENGTH_SHORT).show();
    }

    public void updateEntrantsCount(int confirmed, int waiting, int selected, int cancelled) {
        binding.tvEventEntrantsConfirmedNumber.setText("(" + confirmed + ")");
        binding.tvEntrantsWaitingNumber.setText("(" + waiting + ")");
        binding.tvEntrantsSelectedNumber.setText("(" + selected + ")");
        binding.tvEntrantsCancelledNumber.setText("(" + cancelled + ")");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
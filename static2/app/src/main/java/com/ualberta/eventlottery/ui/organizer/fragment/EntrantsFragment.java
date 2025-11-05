package com.ualberta.eventlottery.ui.organizer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

//    public EntrantsFragment() {
//        // Required empty public constructor
//    }

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
        statusButtons.add(binding.btnEntrantsPending);
        statusButtons.add(binding.btnEntrantsCancelled);

        List<Registration> registrations = registrationRepository.getRegistrationsByEvent(eventId);
        int confirmed = (int) registrations.stream().filter(registration -> registration.getStatus() == EntrantRegistrationStatus.CONFIRMED).count();
        int waiting = (int) registrations.stream().filter(registration -> registration.getStatus() == EntrantRegistrationStatus.WAITING).count();
        int pending = (int) registrations.stream().filter(registration -> registration.getStatus() == EntrantRegistrationStatus.REGISTERED).count();
        int cancelled = (int) registrations.stream().filter(registration -> registration.getStatus() == EntrantRegistrationStatus.CANCELLED).count();
        updateEntrantsCount(confirmed, waiting, pending, cancelled);

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

        binding.btnEntrantsPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateButtonSelection(binding.btnEntrantsPending);
                loadPendingEntrants();
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

//            if (isSelected) {
//                button.setBackgroundResource(R.drawable.button_selected);
//            } else {
//                button.setBackgroundResource(R.drawable.button_selector);
//            }
            button.setBackgroundResource(R.drawable.button_selector);
        }
    }

    private void loadEntrantsData() {
        loadConfirmedEntrants();
    }

    private void loadConfirmedEntrants() {
        List<Entrant> entrants = getConfirmedEntrants();
        setupListView(entrants);
    }

    private void loadWaitingEntrants() {
        List<Entrant> entrants = getWaitingEntrants();
        setupListView(entrants);
    }

    private void loadPendingEntrants() {
        List<Entrant> entrants = getPendingEntrants();
        setupListView(entrants);
    }

    private void loadCancelledEntrants() {
        List<Entrant> entrants = getCancelledEntrants();
        setupListView(entrants);
    }

    private void setupListView(List<Entrant> entrants) {
        binding.lvEventEntrantList.setLayoutManager(new LinearLayoutManager(requireContext()));
        EntrantAdapter adapter = new EntrantAdapter(requireContext(), entrants, eventId);
        binding.lvEventEntrantList.setAdapter(adapter);

        adapter.setOnItemClickListener(entrant -> onEntrantSelected(entrant));
    }

    private List<Entrant> getConfirmedEntrants() {
        List<Registration> registrations = registrationRepository.getRegistrationsByEvent(eventId);
        List<Entrant> entrants = registrations.stream()
                .filter(registration -> registration.getStatus() == EntrantRegistrationStatus.CONFIRMED)
                .map(Registration::getEntrantId)
                .map(entrantRepository::findEntrantById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return entrants;
    }

    private List<Entrant> getWaitingEntrants() {
        List<Registration> registrations = registrationRepository.getRegistrationsByEvent(eventId);
        List<Entrant> entrants = registrations.stream()
                .filter(registration -> registration.getStatus() == EntrantRegistrationStatus.WAITING)
                .map(Registration::getEntrantId)
                .map(entrantRepository::findEntrantById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return entrants;
    }

    private List<Entrant> getPendingEntrants() {
        List<Registration> registrations = registrationRepository.getRegistrationsByEvent(eventId);
        List<Entrant> entrants = registrations.stream()
                .filter(registration -> registration.getStatus() == EntrantRegistrationStatus.REGISTERED)
                .map(Registration::getEntrantId)
                .map(entrantRepository::findEntrantById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return entrants;
    }

    private List<Entrant> getCancelledEntrants() {
        List<Registration> registrations = registrationRepository.getRegistrationsByEvent(eventId);
        List<Entrant> entrants = registrations.stream()
                .filter(registration -> registration.getStatus() == EntrantRegistrationStatus.CANCELLED)
                .map(Registration::getEntrantId)
                .map(entrantRepository::findEntrantById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return entrants;
    }

    private void onEntrantSelected(Entrant entrant) {
        Toast.makeText(requireContext(), "Selected: " + entrant.getName(), Toast.LENGTH_SHORT).show();
    }

    public void updateEntrantsCount(int confirmed, int waiting, int pending, int cancelled) {
        binding.tvEventEntrantsConfirmedNumber.setText("(" + confirmed + ")");
        binding.tvEntrantsWaitingNumber.setText("(" + waiting + ")");
        binding.tvEntrantsPendingNumber.setText("(" + pending + ")");
        binding.tvEntrantsCancelledNumber.setText("(" + cancelled + ")");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

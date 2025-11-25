package com.ualberta.eventlottery.ui.home.entrant;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.idling.CountingIdlingResource;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventCategory;
import com.ualberta.eventlottery.model.EventRegistrationStatus;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Fragment that displays home screen for the entrants.
 * Provides functionality to switch between My Events and Available Events
 *Options provided for filter, sort and search
 */
public class HomeFragment extends Fragment implements EventAdapter.OnEventListener {
    @VisibleForTesting
    public static CountingIdlingResource idlingResource = new CountingIdlingResource("HomeFragment-AvailableEventsAction");
    @VisibleForTesting
    private CountingIdlingResource getAvailableEventsIdlingResource = null;

    // History button has been removed
    private Button myEventsButton, availableEventsButton;
    private EditText searchInputHome;
    private ChipGroup filterGroup;
    private Chip categoryFilter;
    private RecyclerView recyclerView;
    // History adapter has been removed
    private EventAdapter myEventsAdapter, availableEventsAdapter;
    private List<Event> myEventsList;
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private Observer<List<Event>> availableEventsObserver;
    // This observer will now be for the "My Events" data
    private Observer<List<Event>> myEventsObserver;

    /**
     * Creates initializes the view for the home fragment.
     * Sets up the RecyclerView with event adapter, initializing different ui components,
     * and configures event listeners for search, filter, sort, and navigation button.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the root view of the fragment's layout
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        searchInputHome = view.findViewById(R.id.searchInputHome);
        myEventsButton = view.findViewById(R.id.myEventsButton);
        availableEventsButton = view.findViewById(R.id.availableEventsButton);
        recyclerView = view.findViewById(R.id.eventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        filterGroup = view.findViewById(R.id.filterGroup);
        categoryFilter = addFilterChip("Category", "Any");
        categoryFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterBottomSheet();
                // Optional: visually show chip as "active"
                categoryFilter.setChecked(true);
            }
        });

        // Initialize adapters and observers
        myEventsAdapter = new EventAdapter(new ArrayList<>(), this); // Now starts empty
        availableEventsAdapter = new EventAdapter(new ArrayList<>(), this);

        availableEventsObserver = newData -> {
            if (newData != null) {
                availableEventsAdapter.updateEvents(newData);
            }
            if (getAvailableEventsIdlingResource != null) {
                getAvailableEventsIdlingResource.decrement();
                getAvailableEventsIdlingResource = null;
            }
        };

        // Observer for the dynamic "My Events" list
        myEventsObserver = newData -> {
            if (newData != null) {
                myEventsAdapter.updateEvents(newData);
            }
        };

        showMyEvents();

        searchInputHome.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                // Search logic here
                return true;
            }
            return false;
        });

        // Navigation listeners
        myEventsButton.setOnClickListener(v -> showMyEvents());
        availableEventsButton.setOnClickListener(v -> showAvailableEvents());

        return view;
    }

    private Chip addFilterChip(String label, String value) {
        Chip chip = new Chip(getContext());
        chip.setText(String.format("%s: %s", label, value));
        chip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);

        chip.setId(View.generateViewId()); // Assign a unique ID if needed for single selection
        filterGroup.addView(chip);
        return chip;
    }

    private void showFilterBottomSheet() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.filter_category_bottom_sheet, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setOnDismissListener(dialog -> {
            // When user closes the sheet, uncheck the chip
            categoryFilter.setChecked(false);
        });

        ChipGroup chipGroup = bottomSheetView.findViewById(R.id.filterGroup);
        MaterialButton btnApply = bottomSheetView.findViewById(R.id.applyFilters);

        List<EventCategory> selectedCategories = homeViewModel.getSelectedCategories();
        for (EventCategory category : EventCategory.values()) {
            Chip chip = new Chip(getContext());
            chip.setId(View.generateViewId()); // Assign a unique ID if needed for single selection
            chip.setText(category.toString().toLowerCase().replace("_", " "));
            chip.setTag(category);
            chipGroup.addView(chip);

            chip.setCheckable(true); // Make the chip checkable
            boolean isChecked = selectedCategories.contains(category);
            chip.setChecked(isChecked);
            if (isChecked) {
                chipGroup.check(chip.getId());
            }
        }

        btnApply.setOnClickListener(v -> {
            // Get all checked chip IDs
            List<Integer> selectedChipIds = chipGroup.getCheckedChipIds();

            // Convert to readable strings (or use your own logic)
            List<EventCategory> selectedFilters = new ArrayList<>();
            for (Integer id : selectedChipIds) {
                Chip chip = bottomSheetView.findViewById(id);
                if (chip != null) {
                    selectedFilters.add((EventCategory) chip.getTag());
                }
            }

            homeViewModel.applyCategoryFilters(selectedFilters);

            // Close the sheet
            bottomSheetDialog.dismiss();

            String selectedFiltersCount = String.format("(%d)", selectedFilters.size());
            categoryFilter.setText(String.format("Category: %s", selectedFilters.size() == chipGroup.getChildCount() ? "Any" : selectedFiltersCount));
        });

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                ((View) bottomSheetView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if (behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setPeekHeight(400);
        }

        bottomSheetDialog.show();
    }

    private void applyFilters(List<EventCategory> categoryFilters) {
        homeViewModel.applyCategoryFilters(categoryFilters);
    }



    /**
     * Called when an event is clicked.
     *
     * @param event The clicked event.
     */
    @Override
    public void onEventClick(Event event) {
        Bundle bundle = new Bundle();
        bundle.putString("eventId", event.getId());
        NavHostFragment.findNavController(HomeFragment.this)
                .navigate(R.id.action_home_to_details, bundle);
    }

    /**
     * Cleans up UI resources to prevent memory leaks
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Resets the style of all buttons.
     */
    private void resetAllButtonStyles() {
        Button[] buttons = {myEventsButton, availableEventsButton};
        for (Button btn : buttons) {
            btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.black));
            btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
        }
    }

    /**
     * Sets the style of the active button.
     *
     * @param activeButton The button to be styled as active.
     */
    private void setActiveButtonStyle(Button activeButton) {
        activeButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        activeButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.black));
    }

    /**
     * Shows the list of events the user has registered for.
     */
    private void showMyEvents() {

        // Stop observing the other LiveData to prevent getting unwanted updates.
        homeViewModel.getAvailableEvents().removeObservers(getViewLifecycleOwner());

        // Start observing the LiveData for "My Events"
        homeViewModel.getMyEvents().observe(getViewLifecycleOwner(), myEventsObserver);

        // Tell the ViewModel to fetch the data for "My Events"
        homeViewModel.loadMyRegisteredEvents();

        recyclerView.setAdapter(myEventsAdapter);
        resetAllButtonStyles();
        setActiveButtonStyle(myEventsButton);
    }

    /**
     * Shows the list of available events.
     */
    private void showAvailableEvents() {
        getAvailableEventsIdlingResource = idlingResource;
        getAvailableEventsIdlingResource.increment();

        // Stop observing the other LiveData.
        homeViewModel.getMyEvents().removeObservers(getViewLifecycleOwner());

        // Start observing the LiveData for "Available Events"
        homeViewModel.getAvailableEvents().observe(getViewLifecycleOwner(), availableEventsObserver);

        recyclerView.setAdapter(availableEventsAdapter);
        resetAllButtonStyles();
        setActiveButtonStyle(availableEventsButton);
    }
}
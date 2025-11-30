package com.ualberta.eventlottery.ui.home.entrant;

import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.idling.CountingIdlingResource;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventCategory;
import com.ualberta.eventlottery.model.TimeRange;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentHomeBinding;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private ChipGroup filterGroup;
    private Chip categoryFilter, timeRangesFilter, daysOfWeekFilter;
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recyclerView = view.findViewById(R.id.eventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        filterGroup = view.findViewById(R.id.filterGroup);
        categoryFilter = addFilterChip("category", "Category", getCategoryFilterText());
        categoryFilter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                showCategoryFilterBottomSheet();
            }
        });

        daysOfWeekFilter = addFilterChip("daysOfWeek", "Days", getDaysOfWeekFilterText());
        daysOfWeekFilter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                showDaysOfWeekFilterBottomSheet();
            }
        });

        timeRangesFilter = addFilterChip("timeRanges", "Start Time", getTimeRangesFilterText());
        timeRangesFilter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                showTimeRangeFilterBottomSheet();
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

        MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.toggleGroup);

        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {  // Only act when a button becomes selected
                    if (checkedId == R.id.myEventsButton) {
                        showMyEvents();
                    } else if (checkedId == R.id.availableEventsButton) {
                        showAvailableEvents();
                    }
                }
            }
        });

        return view;
    }

    private Chip addFilterChip(String tag, String label, String value) {
        Chip chip = new Chip(getContext());
        chip.setText(String.format("%s: %s", label, value));
        chip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        chip.setTag(tag);

        chip.setId(View.generateViewId()); // Assign a unique ID if needed for single selection
        filterGroup.addView(chip);
        return chip;
    }
    private void showFilterBottomSheet(View bottomSheetView, Runnable buildChipGroup, View.OnClickListener applyBtnClickListener) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(bottomSheetView);

        if (buildChipGroup != null) {
            buildChipGroup.run();
        }

        MaterialButton btnApply = bottomSheetView.findViewById(R.id.applyFilters);
        btnApply.setOnClickListener(view -> {
            applyBtnClickListener.onClick(bottomSheetView);
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();
    }

    private void showCategoryFilterBottomSheet() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.filter_category_bottom_sheet, null);
        ChipGroup chipGroup = bottomSheetView.findViewById(R.id.filterGroup);

        Runnable buildChipGroup = () -> {
            List<EventCategory> selectedCategories = homeViewModel.getSelectedCategories();
            boolean allCategoriesSelected = selectedCategories.size() == EventCategory.values().length;
            for (EventCategory category : EventCategory.values()) {
                Chip chip = new Chip(getContext());
                chip.setId(View.generateViewId()); // Assign a unique ID if needed for single selection
                chip.setText(category.toString().toLowerCase().replace("_", " "));
                chip.setTag(category);
                chipGroup.addView(chip);

                chip.setCheckable(true); // Make the chip checkable
                boolean isChecked = selectedCategories.contains(category);
                if (isChecked && !allCategoriesSelected) {
                    chip.setChecked(isChecked);
                    chipGroup.check(chip.getId());
                }
            }
        };

        showFilterBottomSheet(bottomSheetView, buildChipGroup, view -> {
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
            categoryFilter.setText(String.format("Category: %s", getCategoryFilterText()));
        });
    }

    private String getCategoryFilterText() {
        List<EventCategory> selectedFilters = homeViewModel.getSelectedCategories();
        String selectedFiltersCount = String.format("(%d)", selectedFilters.size());
        boolean allOrNoFiltersSelected = selectedFilters.size() == EventCategory.values().length || selectedFilters.size() == 0;
        return allOrNoFiltersSelected ? "Any" : selectedFiltersCount;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDaysOfWeekFilterBottomSheet() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.filter_days_of_week_bottom_sheet, null);
        ChipGroup chipGroup = bottomSheetView.findViewById(R.id.filterGroup);

        Runnable buildChipGroup = () -> {
            List<DayOfWeek> selectedDaysOfWeek = homeViewModel.getSelectedDaysOfWeek();
            boolean allDaysSelected = selectedDaysOfWeek.size() == DayOfWeek.values().length;
            if (allDaysSelected) {
                return;
            }
            for (int i = 0; i < chipGroup.getChildCount(); ++i) {
                View child = chipGroup.getChildAt(i);
                if (child instanceof Chip) {
                    Chip chip = (Chip) child;

                    String chipTag = (String) chip.getTag();
                    if (chipTag != null) {
                        DayOfWeek dow = DayOfWeek.valueOf(chipTag);
                        if (dow != null) {
                            boolean isChecked = selectedDaysOfWeek.contains(dow);
                            chip.setChecked(isChecked);
                            if (isChecked) {
                                chipGroup.check(chip.getId());
                            }
                        }
                    }
                }
            }
        };

        showFilterBottomSheet(bottomSheetView, buildChipGroup, view -> {
            List<Integer> selectedChipIds = chipGroup.getCheckedChipIds();

            // Convert to readable strings (or use your own logic)
            List<DayOfWeek> selectedDaysOfWeek = new ArrayList<>();
            for (Integer id : selectedChipIds) {
                Chip chip = bottomSheetView.findViewById(id);
                if (chip != null) {
                    selectedDaysOfWeek.add(DayOfWeek.valueOf((String) chip.getTag()));
                }
            }

            homeViewModel.applyDaysOfWeekFilter(selectedDaysOfWeek);

            String fmt = "Days: %s";
            daysOfWeekFilter.setText(String.format(fmt, getDaysOfWeekFilterText()));
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getDaysOfWeekFilterText() {
        List<DayOfWeek> selectedDaysOfWeek = homeViewModel.getSelectedDaysOfWeek();
        if (selectedDaysOfWeek.size() == 7 || selectedDaysOfWeek.isEmpty()) {
            return "Any";
        } else {
            StringBuffer buffer = new StringBuffer();
            for (DayOfWeek dow : DayOfWeek.values()) {
                if (selectedDaysOfWeek.contains(dow)) {
                    buffer.append(dow.getDisplayName(TextStyle.NARROW, Locale.CANADA));
                    buffer.append(" ");
                }
            }
            return buffer.toString().trim();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showTimeRangeFilterBottomSheet() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.filter_time_ranges_bottom_sheet, null);
        ChipGroup chipGroup = bottomSheetView.findViewById(R.id.filterGroup);

        Runnable buildChipGroup = () -> {
            List<TimeRange> selectedTimeRanges = homeViewModel.getSelectedTimeRanges();
            boolean allRangesSelected = selectedTimeRanges.size() == TimeRange.values().length;
            if (allRangesSelected) {
                return;
            }
            for (int i = 0; i < chipGroup.getChildCount(); ++i) {
                View child = chipGroup.getChildAt(i);
                if (child instanceof Chip) {
                    Chip chip = (Chip) child;

                    String chipTag = (String) chip.getTag();
                    if (chipTag != null) {
                        TimeRange tr = TimeRange.valueOf(chipTag);
                        if (tr != null) {
                            boolean isChecked = selectedTimeRanges.contains(tr);
                            chip.setChecked(isChecked);
                            if (isChecked) {
                                chipGroup.check(chip.getId());
                            }
                        }
                    }
                }
            }
        };

        showFilterBottomSheet(bottomSheetView, buildChipGroup, view -> {
            List<Integer> selectedChipIds = chipGroup.getCheckedChipIds();

            // Convert to readable strings (or use your own logic)
            List<TimeRange> selectedTimeRanges = new ArrayList<>();
            for (Integer id : selectedChipIds) {
                Chip chip = bottomSheetView.findViewById(id);
                if (chip != null) {
                    selectedTimeRanges.add(TimeRange.valueOf((String) chip.getTag()));
                }
            }

            homeViewModel.applyTimeRangeFilter(selectedTimeRanges);

            String fmt = "Start Time: %s";
            timeRangesFilter.setText(String.format(fmt, getTimeRangesFilterText()));
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getTimeRangesFilterText() {
        List<TimeRange> selectedTimeRanges = homeViewModel.getSelectedTimeRanges();
        if (selectedTimeRanges.size() == 3 || selectedTimeRanges.isEmpty()) {
            return "Any";
        } else {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < selectedTimeRanges.size(); ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(selectedTimeRanges.get(i).getShortDisplayName());
            }
            return buffer.toString().trim();
        }
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
    }
}
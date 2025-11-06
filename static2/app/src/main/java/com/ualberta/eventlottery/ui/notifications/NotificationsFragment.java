package com.ualberta.eventlottery.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ualberta.eventlottery.notification.NotificationModel;
import com.ualberta.static2.databinding.FragmentNotificationsBinding;

import java.util.List;

/**
 * Fragment that displays a list of notifications for the current user.
 * Observes the {@link NotificationsViewModel} and updates the UI in real time.
 */
public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private NotificationsAdapter adapter;
    private NotificationsViewModel notificationsViewModel;

    /**
     * Inflates the layout, sets up the RecyclerView, and starts observing notification data.
     *
     * @param inflater  layout inflater
     * @param container parent view group
     * @param savedInstanceState saved state
     * @return the root view of the fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize ViewModel
        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        // Setup RecyclerView
        RecyclerView recyclerView = binding.recyclerNotifications;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NotificationsAdapter(notification -> {
            // Handle item click if needed
        });
        recyclerView.setAdapter(adapter);

        // Observe LiveData from ViewModel
        notificationsViewModel.getNotifications().observe(getViewLifecycleOwner(), this::updateNotifications);

        return root;
    }

    /**
     * Updates the RecyclerView with the latest notifications.
     * Shows or hides the empty state view based on data availability.
     *
     * @param notifications list of notifications retrieved from Firestore
     */
    private void updateNotifications(List<NotificationModel> notifications) {
        if (notifications != null && !notifications.isEmpty()) {
            adapter.submitList(notifications);
            binding.emptyState.setVisibility(View.GONE);
        } else {
            binding.emptyState.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Cleans up the view binding when the fragment's view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

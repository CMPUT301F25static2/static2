package com.ualberta.eventlottery.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ualberta.eventlottery.notification.NotificationModel;
import com.ualberta.static2.databinding.FragmentNotificationsBinding;

import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private NotificationsListAdapter adapter;
    private NotificationsViewModel notificationsViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize ViewModel
        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        // Setup ListView and adapter
        ListView listView = binding.listNotifications;
        adapter = new NotificationsListAdapter(requireContext(), notification -> {
            // Handle item click if needed
        });
        listView.setAdapter(adapter);

        // Add footer view for bottom padding
        View footer = new View(requireContext());
        int footerHeightPx = (int) (100 * getResources().getDisplayMetrics().density);
        footer.setLayoutParams(new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                footerHeightPx
        ));
        listView.addFooterView(footer);

        // Observe notifications
        notificationsViewModel.getNotifications().observe(getViewLifecycleOwner(), this::updateNotifications);

        return root;
    }

    private void updateNotifications(List<NotificationModel> notifications) {
        if (notifications != null && !notifications.isEmpty()) {
            adapter.setNotifications(notifications);
            binding.emptyState.setVisibility(View.GONE);
        } else {
            binding.emptyState.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

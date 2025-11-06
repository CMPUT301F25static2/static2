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

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private NotificationsAdapter adapter;
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

        // Setup RecyclerView
        RecyclerView recyclerView = binding.recyclerNotifications; // ID from binding matches XML
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NotificationsAdapter(notification -> {
            // Handle onClick here if needed
        });
        recyclerView.setAdapter(adapter);

        // Observe LiveData
        notificationsViewModel.getNotifications().observe(getViewLifecycleOwner(), this::updateNotifications);

        return root;
    }

    private void updateNotifications(List<NotificationModel> notifications) {
        if (notifications != null && !notifications.isEmpty()) {
            adapter.submitList(notifications);
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

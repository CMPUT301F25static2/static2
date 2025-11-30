package com.ualberta.eventlottery.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ualberta.eventlottery.notification.NotificationModel;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.R;
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
        adapter = new NotificationsListAdapter(requireContext(), new NotificationsListAdapter.OnNotificationClickListener() {
            public void onNotificationClick(NotificationModel notification) {

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                        .setTitle(notification.getTitle() != null ? notification.getTitle() : "Notification")
                        .setMessage(notification.getBody() != null ? notification.getBody() : "No content")
                        .setNegativeButton("Close", null); // Always show Close button

                // Show Go to Event button only if type is "accept" and eventId exists
                if ("action".equals(notification.getNotificationType()) && notification.getEventId() != null) {
                    builder.setPositiveButton("Go to Event", (dialog, which) -> {
                        Bundle args = new Bundle();
                        args.putString("eventId", notification.getEventId());
                        NavHostFragment.findNavController(NotificationsFragment.this)
                                .navigate(R.id.navigation_event_details, args);
                    });
                }

                builder.show();
            }


            @Override
            public void onNotificationClose(NotificationModel notification) {
                notification.markAsRead();
            }
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
            binding.emptyStateLayout.setVisibility(View.GONE);
            binding.listNotifications.setVisibility(View.VISIBLE);
        } else {
            binding.emptyStateLayout.setVisibility(View.VISIBLE);
            binding.listNotifications.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

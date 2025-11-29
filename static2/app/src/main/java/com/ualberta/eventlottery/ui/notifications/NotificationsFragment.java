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

                String currentUserId = UserManager.getCurrentUserId();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Query Firestore to get the user document
                db.collection("users") // Assuming your users collection is named "users"
                        .document(currentUserId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    DocumentSnapshot doc = task.getResult();
                                    String userType = doc.getString("userType"); // Field in Firestore

                                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                                            .setTitle(notification.getTitle() != null ? notification.getTitle() : "Notification")
                                            .setMessage(notification.getBody() != null ? notification.getBody() : "No content")
                                            .setNegativeButton("Close", null); // Always show Close button

                                    // Only show Go to Event for entrants
                                    if ("entrant".equals(userType) && notification.getEventId() != null) {
                                        builder.setPositiveButton("Go to Event", (dialog, which) -> {
                                            // Navigate to EventDetailsFragment with the eventId from the notification
                                            Bundle args = new Bundle();
                                            args.putString("eventId", notification.getEventId());
                                            NavHostFragment.findNavController(NotificationsFragment.this)
                                                    .navigate(R.id.navigation_event_details, args);
                                        });
                                    }

                                    builder.show();

                                } else {
                                    // Failed to get user type from Firestore
                                    AlertDialog.Builder fallback = new AlertDialog.Builder(requireContext())
                                            .setTitle(notification.getTitle() != null ? notification.getTitle() : "Notification")
                                            .setMessage(notification.getBody() != null ? notification.getBody() : "No content")
                                            .setNegativeButton("Close", null);
                                    fallback.show();
                                }
                            }
                        });
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

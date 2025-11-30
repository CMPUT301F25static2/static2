package com.ualberta.eventlottery.ui.adminLogs;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ualberta.eventlottery.model.NotificationLog;
import com.ualberta.static2.databinding.FragmentAdminLogsBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Fragment for admin to view all notification logs.
 * US 03.08.01: As an administrator, I want to review logs of all notifications sent to entrants by organizers.
 */
public class AdminLogFragment extends Fragment implements NotificationLogsAdapter.OnLogClickListener {
    private FragmentAdminLogsBinding binding;
    private AdminLogViewModel viewModel;
    private NotificationLogsAdapter adapter;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminLogsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AdminLogViewModel.class);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup back button
        binding.adminBackButton.setOnClickListener(v -> requireActivity().onBackPressed());

        // Setup search
        setupSearch();

        // Observe data
        observeViewModel();

        return root;
    }

    private void setupRecyclerView() {
        adapter = new NotificationLogsAdapter(this);
        binding.rvNotificationLogs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvNotificationLogs.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.etSearchLogs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.filterLogs(s.toString());
            }
        });
    }

    private void observeViewModel() {
        // Observe logs list
        viewModel.getLogs().observe(getViewLifecycleOwner(), logs -> {
            if (logs != null && !logs.isEmpty()) {
                adapter.setLogList(logs);
                binding.rvNotificationLogs.setVisibility(View.VISIBLE);
                binding.tvEmptyState.setVisibility(View.GONE);
            } else {
                binding.rvNotificationLogs.setVisibility(View.GONE);
                binding.tvEmptyState.setVisibility(View.VISIBLE);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onLogClick(NotificationLog log) {
        // Show detailed log information in a dialog
        showLogDetailsDialog(log);
    }

    private void showLogDetailsDialog(NotificationLog log) {
        StringBuilder details = new StringBuilder();
        details.append("Title: ").append(log.getTitle() != null ? log.getTitle() : "N/A").append("\n\n");
        details.append("Message: ").append(log.getBody() != null ? log.getBody() : "N/A").append("\n\n");
        details.append("Event: ").append(log.getEventTitle() != null ? log.getEventTitle() : "Unknown").append("\n\n");
        details.append("Organizer: ").append(log.getOrganizerName() != null ? log.getOrganizerName() : "Unknown").append("\n\n");
        details.append("Recipients: ").append(log.getRecipientCount()).append("\n\n");

        if (log.getCreatedAt() != null) {
            details.append("Sent: ").append(dateFormat.format(log.getCreatedAt())).append("\n\n");
        }

        details.append("Notification ID: ").append(log.getNotificationId() != null ? log.getNotificationId() : "N/A");

        new AlertDialog.Builder(requireContext())
                .setTitle("Notification Log Details")
                .setMessage(details.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


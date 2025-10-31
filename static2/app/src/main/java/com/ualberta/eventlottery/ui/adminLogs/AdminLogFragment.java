package com.ualberta.eventlottery.ui.adminLogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ualberta.static2.databinding.FragmentAdminLogsBinding;

public class AdminLogFragment extends Fragment {

    private FragmentAdminLogsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AdminLogViewModel adminLogViewModel =
                new ViewModelProvider(this).get(AdminLogViewModel.class);

        binding = FragmentAdminLogsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textLogs;
        adminLogViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
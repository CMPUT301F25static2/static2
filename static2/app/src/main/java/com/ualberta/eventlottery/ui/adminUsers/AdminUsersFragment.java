package com.ualberta.eventlottery.ui.adminUsers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ualberta.static2.databinding.FragmentAdminUsersBinding;

public class AdminUsersFragment extends Fragment {

    private com.ualberta.static2.databinding.FragmentAdminUsersBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AdminUserViewModel adminUserViewModel =
                new ViewModelProvider(this).get(AdminUserViewModel.class);

        binding = FragmentAdminUsersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textUsers;
        adminUserViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package com.ualberta.eventlottery.ui.adminImages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ualberta.static2.databinding.FragmentAdminImagesBinding;

public class AdminImagesFragment extends Fragment {
    private com.ualberta.static2.databinding.FragmentAdminImagesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AdminImageViewModel adminImageViewModel =
                new ViewModelProvider(this).get(AdminImageViewModel.class);

        binding = com.ualberta.static2.databinding.FragmentAdminImagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textImages;
        adminImageViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

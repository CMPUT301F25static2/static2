package com.ualberta.eventlottery.ui.organizer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentImageViewerBinding;

public class ImageViewerFragment extends Fragment {

    private static final String ARG_IMAGE_URL = "image_url";
    private FragmentImageViewerBinding binding;
    private String imageUrl;

    public static ImageViewerFragment newInstance(String imageUrl) {
        ImageViewerFragment fragment = new ImageViewerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentImageViewerBinding.inflate(inflater, container, false);

        binding.getRoot().setOnClickListener(v -> closeFragment());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (imageUrl != null && !imageUrl.isEmpty() && getContext() != null) {
            Glide.with(getContext())
                    .load(imageUrl)
                    .fitCenter()
                    .placeholder(R.drawable.placeholder_background)
                    .into(binding.ivFullscreenImage);
        } else {

            closeFragment();
        }
    }

    private void closeFragment() {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
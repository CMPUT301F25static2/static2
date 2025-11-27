package com.ualberta.eventlottery.ui.adminImages;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ualberta.eventlottery.model.ImageItem;
import com.ualberta.eventlottery.ui.organizer.fragment.ImageViewerFragment;
import com.ualberta.static2.databinding.FragmentAdminImagesBinding;

/**
 * Fragment for admin to view and delete all images in the system.
 * US 03.03.01: As an administrator, I want to be able to remove images.
 */
public class AdminImagesFragment extends Fragment implements AdminImagesAdapter.OnImageActionListener {
    private FragmentAdminImagesBinding binding;
    private AdminImageViewModel viewModel;
    private AdminImagesAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminImagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AdminImageViewModel.class);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup back button
        binding.adminBackButton.setOnClickListener(v -> requireActivity().onBackPressed());

        // Observe data
        observeViewModel();

        return root;
    }

    private void setupRecyclerView() {
        adapter = new AdminImagesAdapter(this);
        binding.rvAdminImages.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAdminImages.setAdapter(adapter);
    }

    private void observeViewModel() {
        // Observe images list
        viewModel.getImages().observe(getViewLifecycleOwner(), images -> {
            if (images != null && !images.isEmpty()) {
                adapter.setImageList(images);
                binding.rvAdminImages.setVisibility(View.VISIBLE);
                binding.tvEmptyState.setVisibility(View.GONE);
            } else {
                binding.rvAdminImages.setVisibility(View.GONE);
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
    public void onDeleteImage(ImageItem imageItem, int position) {
        // Show confirmation dialog
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image from \"" + imageItem.getTitle() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    performDelete(imageItem, position);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performDelete(ImageItem imageItem, int position) {
        viewModel.deleteImage(imageItem, new AdminImageViewModel.OnDeleteCompleteListener() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Image deleted successfully", Toast.LENGTH_SHORT).show();
                        adapter.removeItem(position);

                        // Check if list is now empty
                        if (adapter.getItemCount() == 0) {
                            binding.rvAdminImages.setVisibility(View.GONE);
                            binding.tvEmptyState.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Delete failed: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    @Override
    public void onViewImage(ImageItem imageItem) {
        // Open image viewer fragment to view full image
        if (imageItem.getImageUrl() != null && !imageItem.getImageUrl().isEmpty()) {
            ImageViewerFragment viewerFragment = ImageViewerFragment.newInstance(imageItem.getImageUrl());
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, viewerFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package com.ualberta.eventlottery.ui.organizer.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.ui.organizer.organizerEventShowcase.ImageSelectionDialog;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentImageViewerBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImageViewerFragment extends Fragment {

    private static final String ARG_IMAGE_URL = "image_url";
    private static final String ARG_EVENT_ID = "event_id";
    private FragmentImageViewerBinding binding;
    private String imageUrl;
    private String eventId;
    private OnPosterUpdatedListener posterUpdatedListener;

    public interface OnPosterUpdatedListener {
        void onPosterUpdated(String newPosterUrl);
    }

    public static ImageViewerFragment newInstance(String imageUrl, String eventId) {
        ImageViewerFragment fragment = new ImageViewerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnPosterUpdatedListener(OnPosterUpdatedListener listener) {
        this.posterUpdatedListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
            eventId = getArguments().getString(ARG_EVENT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentImageViewerBinding.inflate(inflater, container, false);

        binding.getRoot().setOnClickListener(v -> closeFragment());

        // Add long press listener for update poster functionality
        binding.ivFullscreenImage.setOnLongClickListener(v -> {
            showUpdatePosterDialog();
//            showImagePicker();
            return true;
        });

        binding.ivFullscreenImage.setOnClickListener(v -> {

        });

        return binding.getRoot();
    }

    private void showImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1); // Start the gallery activity
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData(); // Get the selected image URI
            if (selectedImageUri != null && eventId != null) {
                // Upload the image to Firebase Storage and update the poster URL
                uploadImageToStorage(selectedImageUri);
            }
        }
    }

    private void uploadImageToStorage(Uri imageUri) {
        if (eventId == null) {
            Log.e("UploadImage", "eventId is null, cannot update event poster.");
            return;
        }

        EventRepository eventRepository = EventRepository.getInstance();
        eventRepository.updateEventPoster(eventId, imageUri, new EventRepository.PosterUpdateCallback() {
            @Override
            public void onSuccess(String posterUrl) {
                // Update the image URL and reload the image immediately
                imageUrl = posterUrl;
                if (binding != null && binding.ivFullscreenImage != null && getContext() != null) {
                    Glide.with(getContext())
                            .load(posterUrl)
                            .fitCenter()
                            .placeholder(R.drawable.placeholder_background)
                            .into(binding.ivFullscreenImage);
                }

                // Notify the listener (OrganizerEventShowcaseFragment) about the poster update
                if (posterUpdatedListener != null) {
                    posterUpdatedListener.onPosterUpdated(posterUrl);
                }

                Toast.makeText(requireContext(), "Poster updated successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to update poster: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private void showUpdatePosterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Update Poster")
               .setMessage("Do you want to update the poster image?")
               .setPositiveButton("Update Poster", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       showImagePicker();
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               });

        AlertDialog dialog = builder.create();
        dialog.show();
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
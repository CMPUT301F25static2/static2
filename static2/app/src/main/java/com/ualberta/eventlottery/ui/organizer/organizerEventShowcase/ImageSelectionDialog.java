package com.ualberta.eventlottery.ui.organizer.organizerEventShowcase;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.static2.R;

import java.util.UUID;

/**
 * Dialog fragment for selecting a new poster image for an event.
 */
public class ImageSelectionDialog extends DialogFragment {
    private static final String ARG_EVENT_ID = "event_id";
    private static final String ARG_IMAGE_URL = "image_url";
    private String eventId;
    private String imageUrl;
    private EventRepository eventRepository;
    private OnImageSelectedListener listener;

    public interface OnImageSelectedListener {
        void onImageSelected(String imageUrl);
    }

    public static ImageSelectionDialog newInstance(String eventId, String imageUrl, OnImageSelectedListener listener) {
        ImageSelectionDialog dialog = new ImageSelectionDialog();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        args.putString(ARG_IMAGE_URL, imageUrl);
        dialog.setArguments(args);
        dialog.listener = listener;
        return dialog;
    }

    public static ImageSelectionDialog newInstance(String imageUrl, OnImageSelectedListener listener) {
        return newInstance(null, imageUrl, listener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
        }
        eventRepository = EventRepository.getInstance();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_image_selection, null);

        Button btnSelectFromGallery = view.findViewById(R.id.btn_select_from_gallery);
        Button btnTakePhoto = view.findViewById(R.id.btn_take_photo);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        btnSelectFromGallery.setOnClickListener(v -> {
            selectImageFromGallery();
            dismiss();
        });

        btnTakePhoto.setOnClickListener(v -> {
            takePhoto();
            dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            dismiss();
        });

        return new android.app.AlertDialog.Builder(requireContext())
                .setView(view)
                .setTitle("Select Poster Image")
                .setCancelable(true)
                .create();
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    private void takePhoto() {
        selectImageFromGallery();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == requireActivity().RESULT_OK && data != null && requestCode == 1) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Upload the image to Firebase Storage
                uploadImageToStorage(selectedImageUri);
            }
        }
    }


    private void uploadImageToStorage(Uri imageUri) {
        if (eventId == null) {
            Log.e("UploadImage", "eventId is null, cannot update event poster.");
            if (listener != null) {
                listener.onImageSelected(imageUri.toString());
            }
            return;
        }

        eventRepository = EventRepository.getInstance();
        eventRepository.updateEventPoster(eventId, imageUri, new EventRepository.OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(requireContext(), "Poster updated successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to update poster: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
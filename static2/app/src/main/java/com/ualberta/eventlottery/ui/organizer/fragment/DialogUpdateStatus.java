package com.ualberta.eventlottery.ui.organizer.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.DialogFragment;

import com.ualberta.static2.R;

public class DialogUpdateStatus extends DialogFragment {
    private static final String ARG_CURRENT_STATUS = "current_status";

    private OnStatusChangeListener listener;
    private String currentStatus;
    private RadioGroup radioGroup;

    public interface OnStatusChangeListener {
        void onStatusChanged(String newStatus);
    }

    public static DialogUpdateStatus newInstance(String currentStatus) {
        DialogUpdateStatus fragment = new DialogUpdateStatus();
        Bundle args = new Bundle();
        args.putString(ARG_CURRENT_STATUS, currentStatus);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentStatus = getArguments().getString(ARG_CURRENT_STATUS);
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_organizer_update_status, null);

        radioGroup = dialogView.findViewById(R.id.rg_event_status);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);


        setCurrentStatus();

        AlertDialog dialog = builder.setView(dialogView).create();

        btnCancel.setOnClickListener(v -> dismiss());

        btnConfirm.setOnClickListener(v -> {
            if (listener != null) {
                String selectedStatus = getSelectedStatus();
                if (selectedStatus != null) {
                    listener.onStatusChanged(selectedStatus);
                }
            }
            dismiss();
        });


        setupRowClickListeners(dialogView);

        return dialog;
    }

    private void setCurrentStatus() {
        if (currentStatus == null) return;

        int radioButtonId = -1;
        switch (currentStatus.toLowerCase()) {
            case "ongoing":
                radioButtonId = R.id.rb_event_status_ongoing;
                break;
            case "closed":
                radioButtonId = R.id.rb_event_status_closed;
                break;
            case "upcoming":
                radioButtonId = R.id.rb_event_status_upcoming;
                break;
        }

        if (radioButtonId != -1) {
            radioGroup.check(radioButtonId);
        }
    }

    private String getSelectedStatus() {
        int selectedId = radioGroup.getCheckedRadioButtonId();

        if (selectedId == R.id.rb_event_status_ongoing) {
            return "ongoing";
        } else if (selectedId == R.id.rb_event_status_closed) {
            return "closed";
        } else if (selectedId == R.id.rb_event_status_upcoming) {
            return "upcoming";
        }

        return null;
    }

    private void setupRowClickListeners(View dialogView) {
        View rowOpened = dialogView.findViewById(R.id.btn_event_status_ongoing);
        View rowClosed = dialogView.findViewById(R.id.btn_event_status_closed);
        View rowUpcoming = dialogView.findViewById(R.id.btn_event_status_upcoming);


        RadioButton rbOngoing = dialogView.findViewById(R.id.rb_event_status_ongoing);
        RadioButton rbClosed = dialogView.findViewById(R.id.rb_event_status_closed);
        RadioButton rbUpcoming = dialogView.findViewById(R.id.rb_event_status_upcoming);

        rbOngoing.setClickable(false);
        rbClosed.setClickable(false);
        rbUpcoming.setClickable(false);

        rowOpened.setOnClickListener(v -> radioGroup.check(R.id.rb_event_status_ongoing));
        rowClosed.setOnClickListener(v -> radioGroup.check(R.id.rb_event_status_closed));
        rowUpcoming.setOnClickListener(v -> radioGroup.check(R.id.rb_event_status_upcoming));
    }

    public void setOnStatusChangeListener(OnStatusChangeListener listener) {
        this.listener = listener;
    }
}
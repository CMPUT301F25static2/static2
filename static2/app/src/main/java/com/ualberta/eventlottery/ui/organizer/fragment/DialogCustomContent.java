package com.ualberta.eventlottery.ui.organizer.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.ualberta.static2.R;

/**
 * Dialog fragment for editing event fields like title, description, and location.
 *
 * @author static2
 * @version 1.0
 */
public class DialogCustomContent extends DialogFragment {
    private static final String ARG_FIELD_TYPE = "field_type";
    private static final String ARG_CURRENT_VALUE = "current_value";

    private OnDialogConfirmListener listener;
    private String fieldType;
    private String currentValue;

    /**
     * Callback interface for dialog confirmation events.
     */
    public interface OnDialogConfirmListener {
        /**
         * Called when dialog is confirmed with new input.
         *
         * @param fieldType the type of field being edited
         * @param inputText the new text input by user
         */
        void onConfirm(String fieldType, String inputText);
    }

    /**
     * Creates a new instance of the dialog.
     *
     * @param fieldType the type of field to edit
     * @param currentValue the current value of the field
     * @return new DialogCustomContent instance
     */
    public static DialogCustomContent newInstance(String fieldType, String currentValue) {
        DialogCustomContent fragment = new DialogCustomContent();
        Bundle args = new Bundle();
        args.putString(ARG_FIELD_TYPE, fieldType);
        args.putString(ARG_CURRENT_VALUE, currentValue);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initializes dialog arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fieldType = getArguments().getString(ARG_FIELD_TYPE);
            currentValue = getArguments().getString(ARG_CURRENT_VALUE);
        }
    }

    /**
     * Creates the dialog with field-specific configuration.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_organizer_custom_content, null);

        TextView tvUpdatedTitle = dialogView.findViewById(R.id.tv_updated_title);
        EditText etUpdatedContent = dialogView.findViewById(R.id.et_updated_content);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);

        // Configure dialog based on field type
        setupDialogByFieldType(tvUpdatedTitle, etUpdatedContent);

        AlertDialog dialog = builder.setView(dialogView).create();

        // Set up button listeners
        btnCancel.setOnClickListener(v -> dismiss());
        btnConfirm.setOnClickListener(v -> {
            if (listener != null) {
                String inputText = etUpdatedContent.getText().toString();
                listener.onConfirm(fieldType, inputText);
            }
            dismiss();
        });

        return dialog;
    }

    /**
     * Configures dialog title and hint based on field type.
     */
    private void setupDialogByFieldType(TextView titleView, EditText contentEditText) {
        if ("title".equals(fieldType)) {
            titleView.setText("Title");
            contentEditText.setHint("Enter new title");
        } else if ("description".equals(fieldType)) {
            titleView.setText("Description");
            contentEditText.setHint("Enter new description");
        } else if ("location".equals(fieldType)) {
            titleView.setText("Location");
            contentEditText.setHint("Enter new location");
        } else if ("endTime".equals(fieldType)) {
            titleView.setText("End Time");
            contentEditText.setHint("Enter new End Time");
        }

        // Set current value and position cursor
        if (currentValue != null) {
            contentEditText.setText(currentValue);
            contentEditText.setSelection(currentValue.length());
        }
    }

    /**
     * Sets the confirmation listener for the dialog.
     *
     * @param listener the listener to receive confirmation events
     */
    public void setOnDialogConfirmListener(OnDialogConfirmListener listener) {
        this.listener = listener;
    }
}
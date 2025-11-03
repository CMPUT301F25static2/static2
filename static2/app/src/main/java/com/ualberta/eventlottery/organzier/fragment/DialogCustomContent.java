package com.ualberta.eventlottery.organzier.fragment;

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

public class DialogCustomContent extends DialogFragment {
    private static final String ARG_FIELD_TYPE = "field_type";
    private static final String ARG_CURRENT_VALUE = "current_value";

    private OnDialogConfirmListener listener;
    private String fieldType;
    private String currentValue;

    public interface OnDialogConfirmListener {
        void onConfirm(String fieldType, String inputText);
    }

    // factory used to create instance
    public static DialogCustomContent newInstance(String fieldType, String currentValue) {
        DialogCustomContent fragment = new DialogCustomContent();
        Bundle args = new Bundle();
        args.putString(ARG_FIELD_TYPE, fieldType);
        args.putString(ARG_CURRENT_VALUE, currentValue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fieldType = getArguments().getString(ARG_FIELD_TYPE);
            currentValue = getArguments().getString(ARG_CURRENT_VALUE);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_organizer_custom_content, null);

        TextView tvUpdatedTitle = dialogView.findViewById(R.id.tv_updated_title);
        EditText etUpdatedContent = dialogView.findViewById(R.id.et_updated_content);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);

        // set text based on fieldType and fieldValue
        setupDialogByFieldType(tvUpdatedTitle, etUpdatedContent);

        AlertDialog dialog = builder.setView(dialogView).create();

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

        // setCurrentValue
        if (currentValue != null) {
            contentEditText.setText(currentValue);
            contentEditText.setSelection(currentValue.length()); // move cursor to end
        }
    }

    public void setOnDialogConfirmListener(OnDialogConfirmListener listener) {
        this.listener = listener;
    }
}
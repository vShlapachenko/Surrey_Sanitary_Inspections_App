package com.example.cmpt276_project_iron.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.cmpt276_project_iron.R;

public class InquiryFragment extends AppCompatDialogFragment {

    private final Context context;
    private final String csvRestaurantUrl;
    private final String csvInspectionUrl;
    private final CallBackInquiryFragment callBackInquiryFragment;
    private static final String RESTAURANTS_FILE_TIME_STAMP = "restaurants_time_stamp.txt";
    private static final String INSPECTIONS_FILE_TIME_STAMP = "inspections_time_stamp.txt";
    private static final String JSON_RESTAURANTS_LAST_MODIFIED = "restaurants_last_modified.json";
    private static final String JSON_INSPECTIONS_LAST_MODIFIED = "inspections_last_modified.json";

    public InquiryFragment(Context context, String csvRestaurantUrl, String csvInspectionUrl, CallBackInquiryFragment callBackInquiryFragment) {
        this.context = context;
        this.csvRestaurantUrl = csvRestaurantUrl;
        this.csvInspectionUrl = csvInspectionUrl;
        this.callBackInquiryFragment = callBackInquiryFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setCancelable(false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.inquire_data_update_restaurant, null);

        DialogInterface.OnClickListener listener;

        listener = getOnClickListener();
        return new AlertDialog.Builder(getActivity())
                .setTitle("Permission to download updated CSV files")
                .setView(view)
                .setPositiveButton(getString(R.string.yes_positive), listener)
                .setNegativeButton(getString(R.string.negative_update), listener)
                .create();

    }

    private DialogInterface.OnClickListener getOnClickListener() {
        return (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (callBackInquiryFragment != null) {
                        callBackInquiryFragment.callBackMethod(csvRestaurantUrl, csvInspectionUrl);
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    Intent intent = new Intent(context, RestaurantList.class);
                    startActivity(intent);
                    break;
            }
        };
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        context.getFileStreamPath(RESTAURANTS_FILE_TIME_STAMP).delete();
        context.getFileStreamPath(INSPECTIONS_FILE_TIME_STAMP).delete();
        context.getFileStreamPath(JSON_RESTAURANTS_LAST_MODIFIED).delete();
        context.getFileStreamPath(JSON_INSPECTIONS_LAST_MODIFIED).delete();
        super.onCancel(dialog);
    }

}

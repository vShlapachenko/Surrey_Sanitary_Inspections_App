package com.example.cmpt276_project_iron.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

        //Create View to show
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.inquire_data_update_restaurant, null);

        DialogInterface.OnClickListener listener;
        //Create a button listener

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
}

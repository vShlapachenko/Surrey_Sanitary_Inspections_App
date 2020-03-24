package com.example.cmpt276_project_iron.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.cmpt276_project_iron.R;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

public class InquiryFragment extends AppCompatDialogFragment {

    private static final String RESTAURANTS_FILE = "restaurants.csv";
    private static final String INSPECTIONS_FILE = "inspections.csv";

    private ProgressDialog progressDialog;
    private final Context context;
    private final String jsonRestaurantUrl;
    private final String jsonInspectionUrl;

    public InquiryFragment(Context context, String jsonRestaurantUrl, String jsonInspectionUrl) {
        this.context = context;
        this.jsonRestaurantUrl = jsonRestaurantUrl;
        this.jsonInspectionUrl = jsonInspectionUrl;
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
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, listener)
                .create();


    }

    private DialogInterface.OnClickListener getOnClickListener() {
        return (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    new CsvTask().execute(jsonRestaurantUrl, jsonInspectionUrl);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };
    }

    private class CsvTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Downloading and executing CSV files");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected String doInBackground(String... params) {
            String content = getStringFromUrl(params[0]);
            writeToInternal(content, RESTAURANTS_FILE);
            content = getStringFromUrl(params[1]);
            writeToInternal(content, INSPECTIONS_FILE);
            return content;
        }

        private String getStringFromUrl(String csvRestaurantUrl) {
            try (BufferedReader reader = getBufferedReader(csvRestaurantUrl)) {
                return reader.lines()
                        .collect(Collectors.joining("\n"));
            } catch (IOException e) {
                throw new IllegalArgumentException("failed to read from URL", e);
            }
        }

        private BufferedReader getBufferedReader(String csvRestaurantUrl) throws IOException {
            URL restaurantURL = new URL(csvRestaurantUrl);
            InputStreamReader isr = new InputStreamReader(restaurantURL.openStream());
            return new BufferedReader(isr);
        }

        private void writeToInternal(String content, String file) {
            try (FileOutputStream fileOutputStream = context.openFileOutput(file, Context.MODE_PRIVATE)) {
                fileOutputStream.write(content.getBytes());
            } catch (IOException e) {
                throw new IllegalArgumentException("failed to write to " +
                        "internal storage", e);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}

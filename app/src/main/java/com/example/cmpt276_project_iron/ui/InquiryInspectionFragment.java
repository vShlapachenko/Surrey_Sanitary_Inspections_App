package com.example.cmpt276_project_iron.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cmpt276_project_iron.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.stream.Collectors;

public class InquiryInspectionFragment extends AppCompatDialogFragment {
    private static final String INSPECTIONS_FILE = "inspections.csv";
    private static final String INSPECTIONS_FILE_TIME_STAMP = "inspections_time_stamp.txt";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private ProgressDialog progressDialog;
    private RequestQueue mQueue;

    private final Context context;

    public InquiryInspectionFragment(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setCancelable(false);
        mQueue = Volley.newRequestQueue(context);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.inquire_data_update_inspection, null);
        DialogInterface.OnClickListener listener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    jsonParse();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        return new AlertDialog.Builder(getActivity())
                .setTitle("Permission to download updated CSV files")
                .setView(view)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, listener)
                .create();
    }

    public void jsonParse() {
        String url = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject result = response.getJSONObject("result");
                Log.e("JSONPARSE", "in json parse");
                Log.e("TAG", "BEFORE URL");
                JSONArray jsonArray = result.getJSONArray("resources");
                JSONObject csvObject = jsonArray.getJSONObject(0);
                String csvRestaurantsUrl = csvObject.getString("url").replace("http", "https");
                new CsvTask().execute(csvRestaurantsUrl);
            } catch (JSONException e) {
                Log.e("TAG", "error in jsonParse failed to read restaurant.csv ", e);
            } finally {
                rewriteFileTimeStamp();
            }
        }, error -> Log.e("TAG", "error to retrieve JSON file", error));
        mQueue.add(request);
    }

    private void rewriteFileTimeStamp() {
        context.getFileStreamPath(INSPECTIONS_FILE_TIME_STAMP).delete();
        try (FileOutputStream fileOutputStream = context.openFileOutput(INSPECTIONS_FILE_TIME_STAMP, Context.MODE_PRIVATE)) {
            Calendar currentTime = Calendar.getInstance();
            String time = DATE_FORMAT.format(currentTime.getTime());
            fileOutputStream.write(time.getBytes());
        } catch (IOException e) {
            Log.e("TAG", "error in jsonParse failed to read restaurant.csv ", e);
        }
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
            writeToInternal(content);
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

        private void writeToInternal(String content) {
            try (FileOutputStream fileOutputStream = context.openFileOutput(INSPECTIONS_FILE, Context.MODE_PRIVATE)) {
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

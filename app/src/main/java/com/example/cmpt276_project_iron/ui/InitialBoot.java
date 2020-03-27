package com.example.cmpt276_project_iron.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.DateConversionCalculator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.stream.Collectors;

public class InitialBoot extends AppCompatActivity implements CallBackInquiryFragment {
    private static final int UPDATE_THRESHOLD = 20;
    private static final String RESTAURANTS_FILE_TIME_STAMP = "restaurants_time_stamp.txt";
    private static final String INSPECTIONS_FILE_TIME_STAMP = "inspections_time_stamp.txt";
    private static final String JSON_RESTAURANTS_LAST_MODIFIED = "restaurants_last_modified.json";
    private static final String JSON_INSPECTIONS_LAST_MODIFIED = "inspections_last_modified.json";
    private static final String RESTAURANTS_FILE = "restaurants.csv";
    private static final String INSPECTIONS_FILE = "inspections.csv";
    private static final String RESTAURANTS_FILE_BACKUP = "restaurants_backup.csv";
    private static final String INSPECTIONS_FILE_BACKUP = "inspections_backup.csv";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final int END_INDEX_TIME_FORMAT = 19;
    private static final int START_INDEX_TIME_FORMAT = 0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_boot);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //Uncomment these lines to wipe internal data

        if(isOnline()) {
            downloadCsvFiles();
        } else {
            startRestaurantList();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isAvailable() && ni.isConnected();
    }

    private void downloadCsvFiles() {
        FileInputStream fileInputStream = null;
        String jsonRestaurantUrl = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
        String jsonInspectionUrl = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
        if ((!fileExist(RESTAURANTS_FILE_TIME_STAMP)) || (!fileExist(INSPECTIONS_FILE_TIME_STAMP))) {
            new JsonTask().execute(jsonRestaurantUrl, jsonInspectionUrl);
        } else {
            try {
                fileInputStream = openFileInput(RESTAURANTS_FILE_TIME_STAMP);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String retrievedTime = bufferedReader.readLine();
                retrievedTime = retrievedTime.substring(START_INDEX_TIME_FORMAT, END_INDEX_TIME_FORMAT);
                Calendar retrievedTimeCalendarFormat = convertToCalendar(retrievedTime);
                Calendar currentTimeCalendarFormat = Calendar.getInstance();
                long restaurantDifferenceInHours = DateConversionCalculator.getDifferenceInHours(
                        currentTimeCalendarFormat,
                        retrievedTimeCalendarFormat);
                fileInputStream = openFileInput(RESTAURANTS_FILE_TIME_STAMP);
                inputStreamReader = new InputStreamReader(fileInputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
                retrievedTime = bufferedReader.readLine();
                retrievedTime = retrievedTime.substring(START_INDEX_TIME_FORMAT, END_INDEX_TIME_FORMAT);
                retrievedTimeCalendarFormat = convertToCalendar(retrievedTime);
                currentTimeCalendarFormat = Calendar.getInstance();
                long inspectionDifferenceInHours = DateConversionCalculator.getDifferenceInHours(
                        currentTimeCalendarFormat,
                        retrievedTimeCalendarFormat);
                if (restaurantDifferenceInHours >= UPDATE_THRESHOLD
                        || inspectionDifferenceInHours >= UPDATE_THRESHOLD) {
                    new JsonTask().execute(jsonRestaurantUrl, jsonInspectionUrl);
                } else {
                    startRestaurantList();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void showFragmentInquiry(String csvRestaurantUrl, String csvInspectionUrl) {
        FragmentManager manager = getSupportFragmentManager();
        InquiryFragment dialog = new InquiryFragment(this, csvRestaurantUrl, csvInspectionUrl, this);
        dialog.show(manager, "MessageDialog");
    }

    public boolean fileExist(String fname) {
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

    private Calendar convertToCalendar(String csvDate) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(DATE_FORMAT.parse(csvDate));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Wrong Date was given : " + csvDate, e);
        }
        return cal;
    }

    public void startRestaurantList(){
        Intent intent = new Intent(this, RestaurantList.class);
        startActivity(intent);
    }

    @Override
    public void callBackMethod(String restaurantUrl, String inspectionUrl) {
        new CsvTask().execute(restaurantUrl, inspectionUrl);
    }


    private class DateAndUrl {
        String dateRestaurant;
        String urlRestaurant;
        String dateInspection;
        String urlInspection;

        public DateAndUrl(String dateRestaurant, String urlRestaurant, String dateInspection, String urlInspection) {
            this.dateRestaurant = dateRestaurant;
            this.urlRestaurant = urlRestaurant;
            this.dateInspection = dateInspection;
            this.urlInspection = urlInspection;
        }
    }

    private class JsonTask extends AsyncTask<String, String, DateAndUrl> {

        @Override
        protected DateAndUrl doInBackground(String... params) {
            String content = getStringFromUrl(params[0]);
            JsonObject jsonObject = new Gson().fromJson(content, JsonObject.class);
            JsonObject result = jsonObject.getAsJsonObject("result");
            JsonArray jsonArray = result.getAsJsonArray("resources");
            JsonObject csvObject = (JsonObject) jsonArray.get(0);
            JsonPrimitive jsonPrimitive = csvObject.getAsJsonPrimitive("last_modified");
            String lastModified = jsonPrimitive.getAsString();
            jsonPrimitive = csvObject.getAsJsonPrimitive("url");
            String csvRestaurantsUrl = jsonPrimitive.getAsString().replace("http", "https");
            content = getStringFromUrl(params[1]);
            jsonObject = new Gson().fromJson(content, JsonObject.class);
            result = jsonObject.getAsJsonObject("result");
            jsonArray = result.getAsJsonArray("resources");
            csvObject = (JsonObject) jsonArray.get(0);
            jsonPrimitive = csvObject.getAsJsonPrimitive("last_modified");
            String lastModifiedInspection = jsonPrimitive.getAsString();
            jsonPrimitive = csvObject.getAsJsonPrimitive("url");
            String csvInspectionUrl = jsonPrimitive.getAsString().replace("http", "https");
            return new DateAndUrl(lastModified, csvRestaurantsUrl, lastModifiedInspection, csvInspectionUrl);
        }

        @Override
        protected void onPostExecute(DateAndUrl s) {
            if (fileExist(JSON_RESTAURANTS_LAST_MODIFIED) && fileExist(JSON_INSPECTIONS_LAST_MODIFIED)) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput(JSON_RESTAURANTS_LAST_MODIFIED)));
                BufferedReader inspectionReader = new BufferedReader(new InputStreamReader(openFileInput(JSON_INSPECTIONS_LAST_MODIFIED)))) {
                    String restaurantsPreviousRetrievedTime = reader.readLine();
                    String inspectionsPreviousRetrievedTime = inspectionReader.readLine();
                    if ((!s.dateRestaurant.equals(restaurantsPreviousRetrievedTime))
                            || (!s.dateInspection.equals(inspectionsPreviousRetrievedTime))) {
                        writeToInternal(s.dateRestaurant, JSON_RESTAURANTS_LAST_MODIFIED);
                        Calendar cal = Calendar.getInstance();
                        String time = DATE_FORMAT.format(cal.getTime());
                        writeToInternal(time, RESTAURANTS_FILE_TIME_STAMP);
                        writeToInternal(s.dateInspection, JSON_INSPECTIONS_LAST_MODIFIED);
                        writeToInternal(time, INSPECTIONS_FILE_TIME_STAMP);
                        showFragmentInquiry(s.urlRestaurant, s.urlInspection);
                    } else {
                        startRestaurantList();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                writeToInternal(s.dateRestaurant, JSON_RESTAURANTS_LAST_MODIFIED);
                Calendar cal = Calendar.getInstance();
                String time = DATE_FORMAT.format(cal.getTime());
                writeToInternal(time, RESTAURANTS_FILE_TIME_STAMP);
                writeToInternal(s.dateInspection, JSON_INSPECTIONS_LAST_MODIFIED);
                writeToInternal(time, INSPECTIONS_FILE_TIME_STAMP);
                showFragmentInquiry(s.urlRestaurant, s.urlInspection);
            }
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
            try (FileOutputStream fileOutputStream = openFileOutput(file, Context.MODE_PRIVATE)) {
                fileOutputStream.write(content.getBytes());
            } catch (IOException e) {
                throw new IllegalArgumentException("failed to write to " +
                        "internal storage", e);
            }
        }
    }

    private class CsvTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(InitialBoot.this);
            progressDialog.setMessage("Downloading and executing CSV files");
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, InitialBoot.this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CsvTask.this.cancel(true);
                }
            });
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
            try (FileOutputStream fileOutputStream = openFileOutput(file, Context.MODE_PRIVATE)) {
                fileOutputStream.write(content.getBytes());
            } catch (IOException e) {
                throw new IllegalArgumentException("failed to write to " +
                        "internal storage", e);
            }
        }

        private void copy(String fileFrom, String fileTo){
            try (FileOutputStream fileOutputStream = openFileOutput(fileTo, Context.MODE_PRIVATE);
                 FileInputStream fileInputStream = openFileInput(fileFrom)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fileInputStream.read(buffer)) > 0){
                    fileOutputStream.write(buffer, 0, length);
                }
            } catch (IOException e) {
                Log.e("AsyncTask CsvTask", "Error copy() internal to internal", e);
            }
        }

        private void copyFromRawToInternal(InputStream rawRecourseStream, String fileTo){
            try(FileOutputStream fileOutputStream = openFileOutput(fileTo, Context.MODE_PRIVATE)){
                byte[] buffer = new byte[1024];
                int length;
                while((length = rawRecourseStream.read(buffer)) > 0){
                    fileOutputStream.write(buffer, 0, length);
                }
            } catch (IOException e) {
                Log.e("AsyncTask CsvTask", "Error in copyFromRawToInternal()", e);
            }
        }

        @Override
        protected void onCancelled() {
            if (fileExist(RESTAURANTS_FILE_BACKUP) && fileExist(INSPECTIONS_FILE_BACKUP)) {
                copy(RESTAURANTS_FILE_BACKUP, RESTAURANTS_FILE);
                copy(INSPECTIONS_FILE_BACKUP, INSPECTIONS_FILE);
            } else {
                copyFromRawToInternal(getResources().openRawResource(R.raw.restaurants_itr1), RESTAURANTS_FILE);
                copyFromRawToInternal(getResources().openRawResource(R.raw.inspectionreports_itr1), INSPECTIONS_FILE);
            }
            startRestaurantList();
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            copy(RESTAURANTS_FILE, RESTAURANTS_FILE_BACKUP);
            copy(INSPECTIONS_FILE, INSPECTIONS_FILE_BACKUP);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            startRestaurantList();
        }
    }
}



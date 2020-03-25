package com.example.cmpt276_project_iron.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

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
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.stream.Collectors;

public class InitialBoot extends AppCompatActivity {
    private static final int UPDATE_THRESHOLD = 20;
    private static final String RESTAURANTS_FILE_TIME_STAMP = "restaurants_time_stamp.txt";
    private static final String INSPECTIONS_FILE_TIME_STAMP = "inspections_time_stamp.txt";
    private static final String JSON_RESTAURANTS_LAST_MODIFIED = "restaurants_last_modified.json";
    private static final String JSON_INSPECTIONS_LAST_MODIFIED = "inspections_last_modified.json";
    private static final String RESTAURANTS_FILE = "restaurants.csv";
    private static final String INSPECTIONS_FILE = "inspections.csv";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final int END_INDEX_TIME_FORMAT = 19;
    private static final int START_INDEX_TIME_FORMAT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_boot);

        //Uncomment these lines to wipe internal data

        getFileStreamPath(RESTAURANTS_FILE_TIME_STAMP).delete();
        getFileStreamPath(INSPECTIONS_FILE_TIME_STAMP).delete();
        getFileStreamPath(RESTAURANTS_FILE).delete();
        getFileStreamPath(INSPECTIONS_FILE).delete();
        getFileStreamPath(JSON_RESTAURANTS_LAST_MODIFIED).delete();
        getFileStreamPath(JSON_INSPECTIONS_LAST_MODIFIED).delete();
        downloadCsvFiles();
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
                    startRestaurantListVOID();
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

    private void showFragmentInquiry(String jsonRestaurantUrl, String jsonInspectionUrl) {
        FragmentManager manager = getSupportFragmentManager();
        InquiryFragment dialog = new InquiryFragment(this, jsonRestaurantUrl, jsonInspectionUrl);
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

    public void startRestaurantListVOID(){
        Intent intent = new Intent(this, RestaurantList.class);
        startActivity(intent);
    }


    public void startRestaurantList(View view){
        Intent intent = new Intent(this, RestaurantList.class);
        startActivity(intent);
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
                        startRestaurantListVOID();
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
}



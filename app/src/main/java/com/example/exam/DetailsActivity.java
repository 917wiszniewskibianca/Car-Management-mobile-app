package com.example.exam;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;

public class DetailsActivity extends AppCompatActivity {
    public static final String DETAILS_EXTRA = "details_extra";
    private ProgressBar progressBar;


    private List<String> carDetails = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailsactivity);

        progressBar = findViewById(R.id.progressBar1);
        // Set up ListView and Adapter
        ListView listView = findViewById(R.id.list_view_car_details);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, carDetails);
        listView.setAdapter(adapter);

        showProgress();
        // Fetch car details for the selected car
        fetchCarDetails();
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private void fetchCarDetails() {
        // Retrieve the selected car id from the intent
        String selectedCarId = getIntent().getStringExtra(DETAILS_EXTRA);

        // Make HTTP GET request to retrieve car details
        new CarDetailsTask().execute(selectedCarId);
    }

    // AsyncTask for making HTTP GET request to retrieve car details
    private class CarDetailsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String selectedCarId = params[0];

                // Perform the HTTP GET request and return the response
                return fetchCarDetailsFromServer(selectedCarId);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            if (result != null) {
                try {
                    // Parse the JSON response and update UI
                    parseCarDetailsJson(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing error
                    showToast("Error parsing car details");
                }
            } else {
                // Handle the case where the request failed
                showToast("Failed to fetch car details. Check your internet connection.");
            }
        }
    }

    private String fetchCarDetailsFromServer(String selectedCarId) throws IOException {
        // Implement the HTTP GET request to retrieve car details for the selected car
        // Return the car details as a string
        // Replace this with the actual implementation based on your server API
        URL url = new URL("http://10.0.2.2:2406/car/" + selectedCarId);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } finally {
            urlConnection.disconnect();
        }
    }

    private void parseCarDetailsJson(String result) throws JSONException {
        // Parse JSON response and update UI
        JSONObject carObject = new JSONObject(result);
        String carInfo = "ID: " + carObject.getString("id") + "\n"
                + "Name: " + carObject.getString("name") + "\n"
                + "Supplier: " + carObject.getString("supplier") + "\n"
                + "Details: " + carObject.getString("details") + "\n"
                + "Status: " + carObject.getString("status") + "\n"
                + "Type: " + carObject.getString("type");
        carDetails.clear();
        carDetails.add(carInfo);
        adapter.notifyDataSetChanged();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

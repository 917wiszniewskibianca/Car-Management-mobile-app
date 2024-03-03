package com.example.exam;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.HashMap;
import java.util.Map;



public class StaffActivity extends AppCompatActivity {

    private Map<String, Integer> carTypesMap = new HashMap<>();
    private ProgressBar progressBar;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        progressBar = findViewById(R.id.progressBar3);
        ListView listView = findViewById(R.id.listViewCarTypes);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCarType = adapter.getItem(position);
                String[] parts = selectedCarType.split(":");
                String carType = parts[0].trim(); // Extract the car type
                // Send a PUT request to request additional cars of the selected type
                new RequestCarTask().execute(carType);
            }
        });

        // Fetch car types and their quantities from the server
        showProgress();
        new FetchCarTypesTask().execute();
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private class FetchCarTypesTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                // Perform the HTTP GET request to fetch car types and quantities
                return fetchCarTypesFromServer();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            if (result != null) {
                // Parse the JSON response and populate the UI with car types and quantities
                parseCarTypesJson(result);
            } else {
                Log.e("FetchCarTypesTask", "Failed to fetch car types");
            }
        }
    }

    private String fetchCarTypesFromServer() throws IOException {
        // Implement the HTTP GET request to fetch car types and quantities from the server
        // Return the server response as a string
        // Replace this with the actual implementation based on your server API

        URL url = new URL("http://10.0.2.2:2406/carstypes");
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

    private void parseCarTypesJson(String result) {
        // Parse JSON response and populate the UI with car types and quantities
        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject carTypeObject = jsonArray.getJSONObject(i);
                String type = carTypeObject.getString("type");
                int quantity = carTypeObject.getInt("quantity");

                // Update the map with car types and quantities
                carTypesMap.put(type, quantity);
            }

            // Update the adapter with grouped car types and quantities
            updateCarTypesUI();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateCarTypesUI() {
        // Populate the ListView with car types and quantities
        adapter.clear();
        for (Map.Entry<String, Integer> entry : carTypesMap.entrySet()) {
            adapter.add(entry.getKey() + ": " + entry.getValue());
        }
    }

    private class RequestCarTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String carType = params[0];
                Log.d("RequestCarTask", "Requesting car type: " + carType);

                // Perform the HTTP PUT request to request additional cars
                return requestCarFromServer(carType);
            } catch (IOException e) {
                Log.e("RequestCarTask", "Error during network operation", e);
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                // Handle the case where the request was successful
                Toast.makeText(StaffActivity.this, "Additional cars requested successfully!", Toast.LENGTH_SHORT).show();
            } else {
                // Handle the case where the request failed
                Toast.makeText(StaffActivity.this, "Failed to request additional cars. Check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean requestCarFromServer(String carType) throws IOException {
        // Implement the HTTP PUT request to request additional cars from the server
        // Return true if the request is successful, false otherwise
        // Replace this with the actual implementation based on your server API

        // Example implementation (replace with your actual endpoint URL)
        URL url = new URL("http://10.0.2.2:2406/requestcar/" + carType);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("PUT");

        // Check the response code and return true if successful (e.g., responseCode == 200)
        int responseCode = urlConnection.getResponseCode();
        return responseCode == HttpURLConnection.HTTP_OK;
    }
}

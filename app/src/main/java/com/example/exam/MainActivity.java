package com.example.exam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

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


public class MainActivity extends AppCompatActivity {

    private List<String> cars = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ProgressBar progressBar;
    private MyWebSocketClient webSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find ProgressBar by ID
        progressBar = findViewById(R.id.progressBar);

        // Initialize and connect WebSocket
        try {
            webSocketClient = new MyWebSocketClient(this, "ws://10.0.2.2:2406");
            webSocketClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Set up ListView and Adapter
        ListView listView = findViewById(R.id.list_view_cars);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cars);
        listView.setAdapter(adapter);

        // Set item click listener to open DetailActivity

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCar = cars.get(position);
                // Assuming selectedCar is in the format "ID:Name:Supplier:Type"
                String[] parts = selectedCar.split(":");
                String carIdPart = parts[1]; // Extract the first part which is the ID
                String carId = carIdPart.replaceAll("[^0-9]", "");

                // Create intent to start DetailActivity
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra(DetailsActivity.DETAILS_EXTRA, carId);
                startActivity(intent);
            }
        });
        Button buttonAddCar = findViewById(R.id.buttonAddNewCar);
        buttonAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to start AddTipActivity
                Intent intent = new Intent(MainActivity.this, AddCarActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        Button buttonStaffSection = findViewById(R.id.buttonShowCarTypes);
        buttonStaffSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to start AddTipActivity
                Intent intent = new Intent(MainActivity.this, StaffActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        Button buttonSupplier= findViewById(R.id.buttonShowCarOrders);
        buttonSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to start AddTipActivity
                Intent intent = new Intent(MainActivity.this, SupplierActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        // Fetch cars from the server
        fetchCars();
    }

    private void fetchCars() {
        // Show progress before making a server request
        showProgress();

        // Check if the device is offline
        if (NetworkUtils.isOffline(this)) {
            // If offline, show offline message and offer retry option
            showOfflineMessage();
        } else {
            // If online, proceed with fetching cars from the server
            if (cars.isEmpty()) {
                // If not cached, fetch cars from the server
                new CarTask().execute();
            } else {
                // If cached, update UI from the cached data
                updateCarsUI();
                // Hide progress when the operation is complete
                hideProgress();
            }
        }
    }


    private void updateCarsUI() {
        // Update UI or handle the result from cached data
        adapter.notifyDataSetChanged();
    }

    private class CarTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                // Perform the HTTP GET request and return the response
                return fetchCarsFromServer();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Hide progress when the operation is complete
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            hideProgress();

            if (result != null) {
                // Handle the successful response
                try {
                    // Parse the JSON response and update UI
                    parseCarJson(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing error
                }
            } else {
                // Handle the case where the request failed
                showOfflineMessage();
            }
        }
    }

    private void parseCarJson(String result) throws JSONException {
        // Parse JSON response and update UI
        JSONArray jsonArray = new JSONArray(result);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject carObject = jsonArray.getJSONObject(i);
            String carInfo = "ID: " + carObject.getString("id") + "\n"
                    + "Name: " + carObject.getString("name") + "\n"
                    + "Supplier: " + carObject.getString("supplier") + "\n"
                    + "Type: " + carObject.getString("type");
            cars.add(carInfo);
        }
        updateCarsUI();
    }

    private String fetchCarsFromServer() throws IOException {
        // Implement the HTTP GET request to fetch cars from the server
        // Return the server response as a string
        // Replace this with the actual implementation based on your server API

        URL url = new URL("http://10.0.2.2:2406/cars");
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

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private void showOfflineMessage() {
        // Display an offline message and offer retry option
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You are currently offline. Please check your internet connection and try again.")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Retry fetching cars
                        fetchCars();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog, do nothing or exit the app
                        dialog.dismiss();
                    }
                });
        // Create and show the AlertDialog
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}

package com.example.exam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;



public class AddCarActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextSupplier;
    private EditText editTextDetails;
    private EditText editTextStatus;
    private EditText editTextQuantity;
    private EditText editTextType;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        editTextName = findViewById(R.id.editTextCarName);
        editTextSupplier = findViewById(R.id.editTextSupplier);
        editTextDetails = findViewById(R.id.editTextDetails);
        editTextStatus = findViewById(R.id.editTextStatus);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        editTextType = findViewById(R.id.editTextType);
        progressBar = findViewById(R.id.progressBar2);

        Button buttonAddCar = findViewById(R.id.buttonAddCar);
        buttonAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the button click to add a new car
                showProgress();
                addCar();
            }
        });
    }
    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }
    private void addCar() {
        // Get the car details from the EditText fields
        String name = editTextName.getText().toString();
        String supplier = editTextSupplier.getText().toString();
        String details = editTextDetails.getText().toString();
        String status = editTextStatus.getText().toString();
        int quantity = Integer.parseInt(editTextQuantity.getText().toString());
        String type = editTextType.getText().toString();

        // Perform the HTTP POST request to add a new car
        new AddCarTask().execute(name, supplier, details, status, String.valueOf(quantity), type);
    }

    // AsyncTask for making HTTP POST request to add a new car
    private class AddCarTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String name = params[0];
                String supplier = params[1];
                String details = params[2];
                String status = params[3];
                int quantity = Integer.parseInt(params[4]);
                String type = params[5];
                Log.d("AddCarTask", "Sending HTTP POST request...");

                // Perform the HTTP POST request
                return addCarToServer(name, supplier, details, status, quantity, type);
            } catch (IOException e) {
                Log.e("AddCarTask", "Error during network operation", e);
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                hideProgress();
                // Handle the case where the car addition was successful
                Toast.makeText(AddCarActivity.this, "Car added successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity after adding the car
            } else {
                // Handle the case where the request failed
                Toast.makeText(AddCarActivity.this, "Failed to add car. Check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean addCarToServer(String name, String supplier, String details, String status, int quantity, String type) throws IOException {
        // Create a URL for the POST request
        URL url = new URL("http://10.0.2.2:2406/car");

        // Create a HttpURLConnection
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            // Set the request method to POST
            urlConnection.setRequestMethod("POST");

            // Enable input/output streams
            urlConnection.setDoOutput(true);

            // Write the car details to the output stream
            String body = "name=" + name +
                    "&supplier=" + supplier +
                    "&details=" + details +
                    "&status=" + status +
                    "&quantity=" + quantity +
                    "&type=" + type;

            // Use a BufferedWriter to write the data
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"))) {
                writer.write(body);
            }

            // Get the response code
            int responseCode = urlConnection.getResponseCode();

            // Return true if the response code is 200 (OK)
            return responseCode == 200;
        } finally {
            // Disconnect the HttpURLConnection
            urlConnection.disconnect();
        }
    }
}

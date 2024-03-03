package com.example.exam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class SupplierActivity extends AppCompatActivity {

    private List<String> carOrdersList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ProgressBar progressBar;
    private TextView noOrdersTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);

        progressBar = findViewById(R.id.progressBar4);
        noOrdersTextView = findViewById(R.id.noOrdersTextView);

        ListView listView = findViewById(R.id.listViewCarOrders);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, carOrdersList);
        listView.setAdapter(adapter);

        showProgress();
        fetchCarOrders();
    }

    private void fetchCarOrders() {
        // Show progress bar while fetching car orders
        progressBar.setVisibility(View.VISIBLE);

        // Perform asynchronous task to fetch car orders
        new FetchCarOrdersTask().execute();
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void showNoOrdersMessage() {
        noOrdersTextView.setVisibility(View.VISIBLE);
    }
    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }


    private class FetchCarOrdersTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Perform HTTP GET request to fetch car orders from the server
                URL url = new URL("http://10.0.2.2:2406/carorders");
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
            } catch (IOException e) {
                Log.e("FetchCarOrdersTask", "Error fetching car orders", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Hide progress bar after fetching car orders
            hideProgressBar();

            if (result != null) {
                try {
                    // Parse JSON response to extract car orders
                    JSONArray jsonArray = new JSONArray(result);
                    if (jsonArray.length() > 0) {
                        // Car orders available, populate list view
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            // Extract relevant information from JSON object and add to list
                            String carOrderInfo = "ID: " + jsonObject.getInt("id")
                                    + "\nName: " + jsonObject.getString("name")
                                    + "\nSupplier: " + jsonObject.getString("supplier")
                                    + "\nDetails: " + jsonObject.getString("details")
                                    + "\nStatus: " + jsonObject.getString("status")
                                    + "\nQuantity: " + jsonObject.getInt("quantity")
                                    + "\nType: " + jsonObject.getString("type");
                            carOrdersList.add(carOrderInfo);
                        }
                        // Notify adapter about data changes
                        adapter.notifyDataSetChanged();
                    } else {
                        // No car orders available, display message
                        showNoOrdersMessage();
                    }
                } catch (JSONException e) {
                    Log.e("FetchCarOrdersTask", "Error parsing JSON", e);
                }
            } else {
                Log.e("FetchCarOrdersTask", "No response from server");
            }
        }

    }
}

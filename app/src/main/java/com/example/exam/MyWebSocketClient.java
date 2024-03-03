package com.example.exam;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;


public class MyWebSocketClient extends WebSocketClient {
    private Context context;

    public MyWebSocketClient(Context context, String serverUrl) throws URISyntaxException {
        super(new URI(serverUrl));
        this.context = context ;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("WebSocket connection opened");

        // You can perform actions upon successful connection here
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
        handleWebSocketMessage(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket connection closed: " + reason);

        // Notify the user with a Toast message
        Toast.makeText(context, "WebSocket connection closed: " + reason, Toast.LENGTH_SHORT).show();

        // Attempt to reconnect after a certain delay (e.g., 5 seconds)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isOpen()) {
                    System.out.println("Reconnecting to WebSocket...");
                    reconnect();
                } else {
                    System.out.println("WebSocket is already closed. No reconnect attempt.");
                }
            }
        }, 5000);

        // Log additional information for debugging purposes
        Log.d("WebSocket", "Connection closed. Code: " + code + ", Reason: " + reason + ", Remote: " + remote);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    private void handleWebSocketMessage(String message) {
        // Parse the WebSocket message and display the new tip details
        displayNewTipNotification(message);
    }

    private void displayNewTipNotification(String message) {
        // Convert the message to a Tip object (you need to define the Tip class)
        CarInventory newInventory = convertMessageToInventory(message);

        // Display the new tip details using an in-app notification (Snackbar, Toast, Dialog, etc.)
            showInventoryNotification(newInventory);
    }
    private CarInventory convertMessageToInventory(String message) {
        try {
            // Parse the JSON message
            JSONObject jsonTip = new JSONObject(message);

            // Extract data from JSON and create a Tip object
            int id = jsonTip.getInt("id");
            String name = jsonTip.getString("name");
            String supplier = jsonTip.getString("supplier");
            String details = jsonTip.getString("details");
            String status = jsonTip.getString("status");
            Integer quantity = jsonTip.getInt("quantity");
            String type = jsonTip.getString("type");


            return new CarInventory(id, name,supplier, details, status,quantity,type);
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON parsing error, return null or throw an exception based on your preference
            return null;
        }
    }


            private void showInventoryNotification(final CarInventory newInventory) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,
                        "New Inventory Added:\nName: " + newInventory.getName() + "\nSupplier: " + newInventory.getSupplier() + "\nDetails: " + newInventory.getDetails(),
                        Toast.LENGTH_LONG).show();
            }
            });

    }


}

package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WineClient {
    public static void main(String[] args) {
        try {
            URL url = new URL("http://localhost:6969/wine/red");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                System.out.println("Risposta dal server: " + response.toString());
            } else {
                System.out.println("Errore nella risposta del server. Codice: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

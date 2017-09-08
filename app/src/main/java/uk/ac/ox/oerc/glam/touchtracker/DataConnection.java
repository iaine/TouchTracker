package uk.ac.ox.oerc.glam.touchtracker;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Function to send data to connection
 */

public class DataConnection {
    public void sendData(String params) {
        try {
            URL url = new URL("http://demeter.oerc.ox.ac.uk/glam");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setDoOutput(true);
                int length = params.length();
                urlConnection.setFixedLengthStreamingMode(length);
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(params);
                writer.flush();
            } finally {
                urlConnection.disconnect();
            }
        } catch (MalformedURLException me) {
            Log.d("ENCODE",me.getMessage());
        } catch (IOException ioe) {
            Log.d("ENCODE",ioe.getMessage());
        }
    }
}

package com.example.hi.album;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocationFetch extends AsyncTask<Void, Void, String> {

    private float lat;
    private float lon;

    @Override
    protected String doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String JsonResponse;

        try {
            URL url = new URL("https://nominatim.openstreetmap.org/reverse?format=json&lat="
                    + lat + "&lon=" + lon + "&zoom=18&addressdetails=0");

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2;" +
                    " .NET CLR 1.0.3705;)");
            urlConnection.setRequestProperty("Referer", "http://www.microsoft.com");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            JsonResponse = buffer.toString();

            return JsonResponse;
        } catch (IOException e) {
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public LocationFetch(float _lat, float _lon) {
        this.lat = _lat;
        this.lon = _lon;
    }

}
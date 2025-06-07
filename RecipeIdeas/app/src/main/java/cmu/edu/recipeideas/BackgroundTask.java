// author: Chia-Szu, Kuo (chiaszuk)

package cmu.edu.recipeideas;

import android.os.AsyncTask;
import android.view.View;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.lang.ref.WeakReference;

public class BackgroundTask extends AsyncTask<String, Void, String> {
    private WeakReference<MainActivity> activityReference;

    private static final String WEB_SERVICE_URL = "https://ominous-space-xylophone-6pj965wpgvvc77g-8080.app.github.dev/";

    public BackgroundTask(MainActivity activity) {
        this.activityReference = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity activity = activityReference.get();
        if (activity != null && activity.getProgressBar() != null) {
            activity.getProgressBar().setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected String doInBackground(String... params) {
        MainActivity activity = activityReference.get();
        if (activity == null || params.length != 2) {
            return null;
        }

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String searchTerm = URLEncoder.encode(params[0], "UTF-8");
            String searchType = URLEncoder.encode(params[1], "UTF-8");

            // Make request to web service
            String urlString = String.format("%s/recipe/search?term=%s&type=%s",
                WEB_SERVICE_URL, searchTerm, searchType);

            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new Exception("Server returned code: " + responseCode);
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (reader != null) reader.close();
                if (connection != null) connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        MainActivity activity = activityReference.get();
        if (activity != null) {
            activity.onPostExecute(result);
        }
    }
}
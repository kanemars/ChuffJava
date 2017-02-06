package kanemars.KaneHuxleyJavaConsumer;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kanemars.KaneHuxleyJavaConsumer.Models.Departures;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class RestfulAsyncTasks extends AsyncTask<String, Integer, Departures>{

    private ProgressBar progressBar;

    public RestfulAsyncTasks() {}

    public RestfulAsyncTasks(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;

        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPostExecute(Departures departures) {
        super.onPostExecute(departures);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected Departures doInBackground(String... params) {
        try {
            String url = String.format("http://kanehuxleyapi.azurewebsites.net/departures/%s/to/%s/%s", params[0], params[1], params[2]);
            String json = readUrl(url);

            GsonBuilder gsonBuilder = new GsonBuilder();
//            gsonBuilder.setDateFormat("d/M/yy hh:mm a");
            Gson gson = gsonBuilder.create();

            return gson.fromJson(json, Departures.class);
        } catch (Exception ex) {
            return null;
        }
    }

}
package kanemars.KaneHuxleyJavaConsumer;

import android.os.AsyncTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kanemars.KaneHuxleyJavaConsumer.Models.Departures;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class GetDeparturesAsyncTask extends AsyncTask<String, Integer, Departures>{

    @Override
    protected Departures doInBackground(String... params) {
        try {
            String url = String.format("http://kanehuxleyapi.azurewebsites.net/departures/%s/to/%s/%s", params[0], params[1], params[2]);
            String json = readUrl(url);

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            return gson.fromJson(json, Departures.class);
        } catch (Exception ex) {
            return null;
        }
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
}
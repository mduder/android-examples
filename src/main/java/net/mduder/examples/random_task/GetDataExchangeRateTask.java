package net.mduder.examples.random_task;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;

import de.greenrobot.event.EventBus;

/**
 * Async task will fetch exchange rate data.
 */
public class GetDataExchangeRateTask extends AsyncTask<String, Void, LinkedHashMap<String, Object>> {
    @Override
    protected LinkedHashMap<String, Object> doInBackground (String... URLs) {
        JSONObject results = getLiveData(URLs[0]);
        if (results == null) {
            // Error already logged
            return null;
        }

        LinkedHashMap<String, Object> exchangeRateData;
        try {
            exchangeRateData = JsonHelper.toMap(results);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return exchangeRateData;
    }

    @Override
    protected void onPostExecute(LinkedHashMap<String, Object> exchangeRateData) {
        if (exchangeRateData == null) {
            Log.d(getClass().getSimpleName(),
                    "retrieve exchange rate - null JSON Object, data not sent");
            return;
        }
        EventBus.getDefault().post(new EventBusMessage(
                EventBusMessage.MessageType.setDataExchangeRate, exchangeRateData));
    }

    private JSONObject mockDataValid() {
        JSONObject testObject = new JSONObject();
        LinkedHashMap<String, Double> testRates = new LinkedHashMap<String, Double>();
        testRates.put("CAD", 1.207784);
        testRates.put("CNY", 6.187673);
        testRates.put("INR", 63.80698);
        testRates.put("JPY", 119.7949);
        testRates.put("USD", 1.000000);

        try {
            testObject.put("timestamp", 1431151261);
            testObject.put("base", "USD");
            testObject.put("rates", testRates);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return testObject;
    }

    private JSONObject mockDataError() {
        JSONObject testObject = new JSONObject();
        try {
            testObject.put("error", true);
            testObject.put("status", 429);
            testObject.put("message", "access_restricted");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return testObject;
    }

    private JSONObject getLiveData(String URL) {
        if (isCancelled()) {
            Log.d(getClass().getSimpleName(),
                    "retrieve exchange rate - cancelled before request");
            return null;
        }

        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        HttpResponse response;
        try {
            response = client.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (isCancelled()) {
            Log.d(getClass().getSimpleName(),
                    "retrieve exchange rate - cancelled after response");
            return null;
        }

        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() != 200) {
            Log.d(getClass().getSimpleName(),
                    "retrieve exchange rate - bad response code " +
                            Integer.toString(statusLine.getStatusCode()));
            return null;
        }

        InputStream content;
        try {
            content = response.getEntity().getContent();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        StringBuilder builder = new StringBuilder();
        String inputLine;
        try {
            while ((inputLine = reader.readLine()) != null) {
                builder.append(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        JSONObject exchangeRateData;
        try {
            exchangeRateData = new JSONObject(builder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return exchangeRateData;
    }
}

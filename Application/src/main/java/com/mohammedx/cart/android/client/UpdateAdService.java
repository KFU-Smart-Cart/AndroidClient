package com.mohammedx.cart.android.client;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class UpdateAdService extends IntentService {

    public UpdateAdService() {
        super("UpdateAdService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            String url = "http://192.168.1.3/SmartCartWeb/ad.php";
            Request request = new Request.Builder().url(url).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    MainActivity.AdReady = true;
                    MainActivity.DBUpdate = true;
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        if (response.isSuccessful()) {
                            MainActivity.AdReady = true;

                            parseDetailsList(jsonData);
                            MainActivity.DBUpdate = true;
                        } else {
                            MainActivity.AdReady = true;
                            MainActivity.DBUpdate = true;
                        }
                    } catch (IOException e) {
                        Log.d("IOExceptione", "" + e);
                    } catch (JSONException e) {
                        Log.d("JSONException", "" + e);
                    }
                }
            });
        } else {
            MainActivity.AdReady = true;
            MainActivity.DBUpdate = true;
//            Toast.makeText(this, "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void parseDetailsList(String jsonData) throws JSONException {
        JSONArray jArray = new JSONArray(jsonData);

        String[] Title = new String[jArray.length()];
        String[] Description = new String[jArray.length()];
        String[] UUID = new String[jArray.length()];
        String[] Major = new String[jArray.length()];
        String[] Minor = new String[jArray.length()];

        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jObject = jArray.getJSONObject(i);

            Title[i] = jObject.getString("Title");
            Description[i] = jObject.getString("Description");
            UUID[i] = jObject.getString("UUID");
            Major[i] = jObject.getString("Major");
            Minor[i] = jObject.getString("Minor");
        }

        SQLiteHelper mSqLiteHelper = new SQLiteHelper(this);
        SQLiteDatabase db = mSqLiteHelper.getWritableDatabase();
        ContentValues mContentValues = new ContentValues();

        long newRowId = -1;

        db.delete(mSqLiteHelper.TABLE_NAME, null, null);

        for (int i = 0; i < jArray.length(); i++) {
            mContentValues.put(mSqLiteHelper.TITLE, Title[i]);
            mContentValues.put(mSqLiteHelper.DESCRIPTION, Description[i]);
            mContentValues.put(mSqLiteHelper.UUID, UUID[i]);
            mContentValues.put(mSqLiteHelper.MAJOR, Major[i]);
            mContentValues.put(mSqLiteHelper.MINOR, Minor[i]);
            newRowId = db.insert(mSqLiteHelper.TABLE_NAME, null, mContentValues);
        }
        if (newRowId != -1) {
            Log.d("XXX", "t");
            MainActivity.AdReady = true;
            MainActivity.DBUpdate = true;
        } else {
            Log.d("XXX", "f");
        }
        db.close();
    }
}
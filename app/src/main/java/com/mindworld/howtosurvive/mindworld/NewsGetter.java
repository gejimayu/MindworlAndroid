package com.mindworld.howtosurvive.mindworld;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by albert on 10/1/2017.
 */

public class NewsGetter extends AsyncTask<Void, Void, String> {
    private TextView mNewsTitle;
    private TextView mNewsBody;

    public NewsGetter(TextView mNewsTitle, TextView mNewsBody) {
        this.mNewsTitle = mNewsTitle;
        this.mNewsBody = mNewsBody;
    }

    @Override
    protected String doInBackground(Void... voids) {
        return NetworkUtils.getNews();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray itemsArray = jsonObject.getJSONArray("articles");

            int random = (int) (Math.random() * itemsArray.length() + 1);
            JSONObject newsItem = itemsArray.getJSONObject(random);

            try {
                mNewsTitle.setText(newsItem.getString("title"));
                mNewsBody.setText(newsItem.getString("description"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            mNewsTitle.setText("No News");
            mNewsBody.setText("");
            e.printStackTrace();
        }
    }
}

package com.mindworld.howtosurvive.mindworld;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class NewsActivity extends AppCompatActivity {
    TextView mNewsTitle;
    TextView mNewsBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        mNewsTitle = (TextView) findViewById(R.id.news_title);
        mNewsBody = (TextView) findViewById(R.id.news_body);

        mNewsTitle.setText("Loading ...");
        mNewsBody.setText("");

        new NewsGetter(mNewsTitle, mNewsBody).execute();
    }
}

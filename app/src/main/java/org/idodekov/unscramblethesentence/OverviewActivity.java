package org.idodekov.unscramblethesentence;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.idodekov.unscramblethesentence.library.sentence.SentenceManager;

public class OverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        Intent intent = getIntent();
        String sentence = intent.getStringExtra(SentenceManager.INTENT_PARAM_FULL_SENTENCE);
        String result = intent.getStringExtra(SentenceManager.INTENT_PARAM_RESULT);

        TextView completionMessage = (TextView) findViewById(R.id.completionMessage);
        if(completionMessage != null) {
            completionMessage.setText(result);
        }

        TextView completeSentence = (TextView) findViewById(R.id.completeSentence);
        if(completeSentence != null) {
            completeSentence.setText(sentence.toUpperCase());
        }

        SentenceManager stncManager = new SentenceManager(getApplicationContext());
        if(!stncManager.hasMoreSentences()) {
            Button nextButton = (Button) findViewById(R.id.nextButton);
            nextButton.setVisibility(View.GONE);
        }

        AdView bottomAdView = (AdView) findViewById(R.id.bottomAdView);
        AdRequest bottomAdRequest = new AdRequest.Builder().build();
        bottomAdView.loadAd(bottomAdRequest);
    }

    public void next(View view) {
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
    }

    public void finish(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}

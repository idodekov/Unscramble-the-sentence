package org.idodekov.unscramblethesentence;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.idodekov.unscramblethesentence.library.sentence.SentenceManager;

public class MainActivity extends AppCompatActivity {
    private SentenceManager sntcManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sntcManager = new SentenceManager(this.getApplicationContext());
        long failureCount = sntcManager.getSentenceCount(SentenceManager.INTENT_PARAM_RESULT_FLAG_FAILURE);
        long passedCount = sntcManager.getSentenceCount(SentenceManager.INTENT_PARAM_RESULT_FLAG_SUCCESS);
        long totalCount = sntcManager.getSentenceCount(null);
        long playedCount = failureCount + passedCount;

        TextView playedCountView = (TextView) findViewById(R.id.totalCount);
        TextView passedCountView = (TextView) findViewById(R.id.passedCount);
        TextView failedCountView = (TextView) findViewById(R.id.failedCount);

        if(playedCountView != null) {
            playedCountView.setText(playedCount + "/" + totalCount);
        }

        if(passedCountView != null) {
            passedCountView.setText(passedCount + "/" + totalCount);
        }

        if(failedCountView != null) {
            failedCountView.setText(failureCount + "/" + totalCount);
        }

        AdView bottomAdView = (AdView) findViewById(R.id.bottomAdView);
        AdRequest bottomAdRequest = new AdRequest.Builder().build();
        bottomAdView.loadAd(bottomAdRequest);
    }

    public void play(View view) {
        if(!sntcManager.hasMoreSentences()) {
            Toast toast = Toast.makeText(this, R.string.noMoreLevels, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Intent intent = new Intent(this, PlayActivity.class);
            startActivity(intent);
        }
    }

}

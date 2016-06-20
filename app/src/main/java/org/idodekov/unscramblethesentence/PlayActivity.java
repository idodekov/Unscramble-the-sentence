package org.idodekov.unscramblethesentence;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.idodekov.unscramblethesentence.global.GlobalTracker;
import org.idodekov.unscramblethesentence.library.sentence.Sentence;
import org.idodekov.unscramblethesentence.library.sentence.SentenceManager;

import java.util.List;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {
    private Sentence sentence;
    private Toast toast;
    private SentenceManager sntcMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sntcMgr = new SentenceManager(getApplicationContext());
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        GlobalTracker.getInstance().resetCounters();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        sentence = sntcMgr.getNextSentence();

        populateWordList(sentence);
        populateSentence(sentence);

        AdView bottomAdView = (AdView) findViewById(R.id.bottomAdView);
        AdRequest bottomAdRequest = new AdRequest.Builder().build();
        bottomAdView.loadAd(bottomAdRequest);
    }

    private void populateSentence(Sentence sentence) {
        LinearLayout masterLayout = (LinearLayout)findViewById(R.id.sentenceLayout);
        LinearLayout layout = null;
        List<String> words = sentence.getWords();

        for(int i = 0; i < words.size(); i++) {

            if(i % 3 == 0) {
                layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                layout.setGravity(1);

                masterLayout.addView(layout);
            }

            String word = words.get(i);

            final TextView currentTextView = new TextView(this);
            currentTextView.setText(word.toUpperCase());
            currentTextView.setId(i);
            currentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            currentTextView.setTextColor(ContextCompat.getColor(this.getApplicationContext(), R.color.colorWhite));
            currentTextView.setTypeface(null,Typeface.BOLD);
            if (i != 0) {
                currentTextView.setVisibility(View.INVISIBLE);
            }

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 10, 10, 10);
            layout.addView(currentTextView, lp);
        }
    }

    private void populateWordList(final Sentence sentence) {
        LinearLayout masterLayout = (LinearLayout)findViewById(R.id.wordListLayout);
        LinearLayout layout = null;
        List<String> randomizedWords = sentence.randomizeWords();

        for(int i = 0; i < randomizedWords.size(); i++) {

            if(i % 2 == 0) {
                layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                layout.setGravity(1);
                masterLayout.addView(layout);
            }

            String word = randomizedWords.get(i);

            Button currentButton = new Button(this);
            currentButton.setText(word.toUpperCase());
            currentButton.setOnClickListener(this);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int marginSize = Math.round(getResources().getDimension(R.dimen.button_margin));
            lp.setMargins(marginSize, marginSize, marginSize, marginSize);
            layout.addView(currentButton, lp);
        }
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        GlobalTracker tracker = GlobalTracker.getInstance();
        int index = tracker.getCurrentWordIndex();
        String text = button.getText().toString();
        if(sentence.getWordAtIndex(index).equalsIgnoreCase(text)) {
            TextView textView = (TextView) findViewById(index);
            textView.setVisibility(View.VISIBLE);

            tracker.incrementCurrentWordNumber();
            button.setVisibility(View.GONE);

            if(tracker.getCurrentWordIndex() >= sentence.getWordCount()) {
                String passMessage = getResources().getString(R.string.youPass);
                finishLevel(passMessage, SentenceManager.INTENT_PARAM_RESULT_FLAG_SUCCESS);
            }
        } else {
            int remainingRetries = tracker.incrementRetryNumber();

            if(remainingRetries <= 0) {
                String failMessage = getResources().getString(R.string.youFail);
                finishLevel(failMessage, SentenceManager.INTENT_PARAM_RESULT_FLAG_FAILURE);
            }

            Resources res = getResources();
            String toastText = String.format(res.getString(R.string.incorrectSelection), remainingRetries);
            toast.setText(toastText);
            toast.show();
        }
    }

    private void finishLevel(String result, int resultFlag) {
        toast.cancel();

        sntcMgr.markSentenceAsPlayed(sentence, resultFlag);

        Intent intent = new Intent(this, OverviewActivity.class);
        intent.putExtra(SentenceManager.INTENT_PARAM_FULL_SENTENCE, sentence.toString());
        intent.putExtra(SentenceManager.INTENT_PARAM_RESULT, result);
        startActivity(intent);
    }
}

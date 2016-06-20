package org.idodekov.unscramblethesentence.library.sentence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Iliyan on 25.4.2016 г..
 */
public class Sentence {
    private long id;
    private List<String> words;

    public Sentence(String text, long id) {
        this.id = id;
        words = new ArrayList<String>();

        String[] splitText = text.split(" ");

        for (String currentWord: splitText) {
            words.add(currentWord);
        }
    }

    public int getWordCount() {
        return words.size();
    }

    public List<String> getWords() {
        return words;
    }

    public List<String> randomizeWords() {
        List<String> result = new ArrayList<String>();

        result.addAll(words);
        result.remove(0);

        while(true) {
            Collections.shuffle(result);

            /* At least 75% of the words should be shuffled */
            int totalCount = result.size();
            int errorThreshhold = totalCount/4;
            int matchCount = 0;

            for (int i = 0; i < result.size(); i++) {
                if(result.get(i).equals(words.get(i))) {
                    matchCount++;
                }
            }

            if(matchCount <= errorThreshhold) {
                break;
            }
        }

        return result;
    }

    public String getWordAtIndex(int index) {
        return words.get(index);
    }

    @Override
    public String toString() {
        String result = "";
        Iterator<String> it = words.iterator();
        while(it.hasNext()) {
            String word = it.next();

            if(!word.equals(",") && !word.equals(".") && !word.equals("?") && !word.equals("!")&& !word.equals(":") && !result.isEmpty()) {
                result += " ";
            }

            result += word;
        }

        return result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

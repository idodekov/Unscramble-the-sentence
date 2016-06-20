package org.idodekov.unscramblethesentence.global;

/**
 * Created by Iliyan on 27.4.2016 г..
 */
public class GlobalTracker {
    private static GlobalTracker ourInstance = new GlobalTracker();
    private int currentWordIndex;
    private int errorCount;
    private static final Object LOCK = new Object();

    public static final int RETRY_COUNT = 4;

    public static GlobalTracker getInstance() {
        return ourInstance;
    }

    private GlobalTracker() {
        currentWordIndex = 0;
        errorCount = 0;
    }

    public int getCurrentWordIndex() {
        synchronized (LOCK) {
            return currentWordIndex;
        }
    }

    public void incrementCurrentWordNumber() {
        synchronized (LOCK) {
            currentWordIndex++;
        }
    }

    public int incrementRetryNumber() {
        synchronized (LOCK) {
            errorCount++;
            return RETRY_COUNT - errorCount;
        }
    }

    public void resetCounters() {
        synchronized (LOCK) {
            errorCount = 0;
            currentWordIndex = 1;
        }
    }
}

package edu.nyu.cs9053.homework10;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: blangel
 * Date: 11/16/14
 * Time: 3:50 PM
 */
public class ThreadedFilesWordCounter extends AbstractThreadedCounter implements FilesWordCounter {

    public ThreadedFilesWordCounter(int concurrencyFactor) {
        super(concurrencyFactor);
    }

    @Override public void count(Map<String, String> files, String word, Callback callback) {
        // TODO - implement this class using Thread objects; one Thread per {@link #concurrencyFactor} with each Thread handling at most one file at one time
        // HINT - do not create the ExecutorService object in this method

        for (Map.Entry<String, String> entry : files.entrySet()){
            final String name = entry.getKey();
            final String contents = entry.getValue();
            getWork().offer(new Runnable() {
                @Override public void run() {
                    final ThreadedWordCounter wordCounter = new ThreadedWordCounter(getConcurrencyFactor());
                    wordCounter.count(contents, word, new WordCounter.Callback() {
                        @Override
                        public void counted(long count) {
                            callback.counted(name, count);
                            wordCounter.stop();
                        }
                    });
                }
            });
        }
    }

    @Override public void stop() {
        // TODO - stop the threads
        stopThreads();
    }

}

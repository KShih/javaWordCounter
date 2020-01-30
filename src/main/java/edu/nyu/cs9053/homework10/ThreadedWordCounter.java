package edu.nyu.cs9053.homework10;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: blangel
 * Date: 11/16/14
 * Time: 3:16 PM
 */
public class ThreadedWordCounter extends AbstractThreadedCounter implements WordCounter {

    public ThreadedWordCounter(int concurrencyFactor) {
        super(concurrencyFactor);
    }

    @Override public void count(String fileContents, String word, Callback callback) {
        // TODO - implement this class using Thread objects; one Thread per {@link #concurrencyFactor}
        // HINT - break up {@linkplain fileContents} and distribute the work across the threads
        // HINT - do not create the Thread objects in this method

        final AtomicLong counter = new AtomicLong(0L);

        String[] split = fileContents.split("\n");

        final CountDownLatch latch = new CountDownLatch(split.length);

        final Pattern wordRegex = Pattern.compile(String.format("\\b%s\\b", word), Pattern.CASE_INSENSITIVE);

        for (final String line : split){
            getWork().offer(new Runnable() {
                @Override public void run() {
                    Matcher matcher = wordRegex.matcher(line);
                    while (matcher.find()){
                        counter.incrementAndGet();
                    }
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException ie){
            Thread.currentThread().interrupt();
            throw new RuntimeException(ie);
        }

        callback.counted(counter.get());
    }

    @Override public void stop() {
        // TODO - stop the threads
        stopThreads();
    }

}

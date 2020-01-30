package edu.nyu.cs9053.homework10;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: blangel
 * Date: 11/16/14
 * Time: 3:33 PM
 */
public class ExecutorWordCounter extends AbstractConcurrencyFactorProvider implements WordCounter {

    public ExecutorWordCounter(int concurrencyFactor) {
        super(concurrencyFactor);
        executorService = Executors.newFixedThreadPool(concurrencyFactor);

    }

    private final ExecutorService executorService;

    @Override public void count(String fileContents, String word, Callback callback) {
        // TODO - implement this class using calls to an ExecutorService; one call per {@link #concurrencyFactor}
        // HINT - break up {@linkplain fileContents} and distribute the work across the calls
        // HINT - do not create the ExecutorService object in this method

        final List<Runnable> jobs = new ArrayList<>();

        final AtomicLong counter = new AtomicLong(0L);

        String[] split = fileContents.split("\n");

        final CountDownLatch latch = new CountDownLatch(split.length); // set how many part need to be finished

        final Pattern wordRegex = Pattern.compile(String.format("\\b%s\\b", word), Pattern.CASE_INSENSITIVE);

        for (String line : split){
            jobs.add(new Runnable() {
                @Override
                public void run() { // Match the word in each split part

                    Matcher matcher = wordRegex.matcher(line);
                    while (matcher.find()){
                        counter.incrementAndGet();
                    }
                    latch.countDown(); // Means this part has finished counting
                }
            });
        }

        for (Runnable job : jobs){
            executorService.submit(job);
        }

        try{
            latch.await(); // Wait until all of the split part finish finding
        } catch (InterruptedException ie){
            throw new RuntimeException(ie);
        }

        callback.counted(counter.get());
    }

    @Override public void stop() {
        // TODO - stop the executor
        executorService.shutdown();
    }
}

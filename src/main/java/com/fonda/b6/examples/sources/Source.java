package com.fonda.b6.examples.sources;

import com.fonda.b6.examples.data.Data;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;

import java.util.concurrent.ThreadLocalRandom;

public class Source implements SourceFunction<Data> {

    private volatile boolean running = true;
    private Long maxTime = 10000000000L;
    private Long minDelay = 200L;
    private Long maxDelay = 5000L;
    private Long minWmDelay = 1000L;
    private Long maxWmDelay = 10000L;
    private Double minValue = -100.0;
    private Double maxValue = 100.0;

    public Source() {}

    public Source(Long maxTime, Long minDelay, Long maxDelay, Long minWmDelay, Long maxWmDelay, Double minValue, Double maxValue) {
        this.maxTime = maxTime;
        this.minWmDelay = minWmDelay;
        this.maxWmDelay = maxWmDelay;
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public void run(SourceContext<Data> context) {
        Long time = ThreadLocalRandom.current().nextLong(minDelay, maxDelay + 1);
        Long wm = ThreadLocalRandom.current().nextLong(minWmDelay, maxWmDelay + 1);
        Long wmTrigger = ThreadLocalRandom.current().nextLong(0L, maxDelay);
        Double value = ThreadLocalRandom.current().nextDouble(minValue, maxValue);
        Long key = ThreadLocalRandom.current().nextLong(1, 3 + 1);

        while (running) {
            context.collectWithTimestamp(new Data(key, value), time);

            key = ThreadLocalRandom.current().nextLong(1, 3 + 1);
            value = ThreadLocalRandom.current().nextDouble(minValue, maxValue);
            time += ThreadLocalRandom.current().nextLong(minDelay, maxDelay + 1);

            if (maxTime < time)
                running = false;

            if (wm + wmTrigger < time) {
                context.emitWatermark(new Watermark(wm));
                wm = time + ThreadLocalRandom.current().nextLong(minWmDelay, maxWmDelay + 1);
                wmTrigger = ThreadLocalRandom.current().nextLong(0L, maxDelay);
            }
        }
    }

    @Override
    public void cancel() {
        running = false;
    }

    @Override
    public String toString() {
        return "StreamGenerator{" +
                "minDelay=" + minDelay +
                ", maxDelay=" + maxDelay +
                '}';
    }
}
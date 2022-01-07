package com.fonda.b6.examples.sources;

import com.fonda.b6.examples.data.Data;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;

import java.util.concurrent.ThreadLocalRandom;

public class Source implements SourceFunction<Data<Double>> {

    private volatile boolean running = true;
    private Long sleep = 100L;

    private Long maxEventTime = 10000000000L;
    private Long minEventDelay = 200L;
    private Long maxEventDelay = 5000L;

    private Long minWmDelay = 1000L;
    private Long maxWmDelay = 10000L;

    private Long minKey = 1L;
    private Long maxKey = 100L;

    private Double minValue = -100.0;
    private Double maxValue = 100.0;

    public Source(Long sleep) {
        this.sleep = sleep;
    }

    @Override
    public void run(SourceContext<Data<Double>> context) {
        Long time = ThreadLocalRandom.current().nextLong(minEventDelay, maxEventDelay + 1);
        Long wm = ThreadLocalRandom.current().nextLong(minWmDelay, maxWmDelay + 1);
        Long wmTrigger = ThreadLocalRandom.current().nextLong(maxEventDelay + 1);
        Long key = ThreadLocalRandom.current().nextLong(minKey, maxKey + 1);
        Double value = ThreadLocalRandom.current().nextDouble(minValue, maxValue);

        while (running) {
            context.collectWithTimestamp(new Data<>(key, value), time);

            time += ThreadLocalRandom.current().nextLong(minEventDelay, maxEventDelay + 1);
            key = ThreadLocalRandom.current().nextLong(minKey, maxKey + 1);
            value = ThreadLocalRandom.current().nextDouble(minValue, maxValue);

            if (maxEventTime < time)
                running = false;

            if (wm + wmTrigger < time) {
                context.emitWatermark(new Watermark(wm));
                wm = time + ThreadLocalRandom.current().nextLong(minWmDelay, maxWmDelay + 1);
                wmTrigger = ThreadLocalRandom.current().nextLong(maxEventDelay + 1);
            }

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void cancel() {
        running = false;
    }

    @Override
    public String toString() {
        return "Source{" +
                "running=" + running +
                ", minEventDelay=" + minEventDelay +
                ", maxEventDelay=" + maxEventDelay +
                ", minWmDelay=" + minWmDelay +
                ", maxWmDelay=" + maxWmDelay +
                ", minKey=" + minKey +
                ", maxKey=" + maxKey +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                "}";
    }
}
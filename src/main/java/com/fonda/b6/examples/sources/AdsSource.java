package com.fonda.b6.examples.sources;

import com.fonda.b6.examples.data.Ads;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;

import java.util.concurrent.ThreadLocalRandom;

public class AdsSource implements SourceFunction<Ads> {

    private volatile boolean running = true;
    private Long maxEventTime = 100000L;
    private Long minEventDelay = 8L;
    private Long maxEventDelay = 30L;

    private Long minWmDelay = 1000L;
    private Long maxWmDelay = 10000L;

    private Long minId = 1L;
    private Long maxId = 100L;

    private Long minGroup = 1L;
    private Long maxGroup = 30L;

    private Double minLength = 0.0;
    private Double maxLength = 300.0;

    public AdsSource(Long maxEventTime) {
        this.maxEventTime = maxEventTime;
    }

    @Override
    public void run(SourceContext<Ads> context) {
        Long time = ThreadLocalRandom.current().nextLong(minEventDelay, maxEventDelay + 1);
        Long wm = ThreadLocalRandom.current().nextLong(minWmDelay, maxWmDelay + 1);
        Long wmTrigger = ThreadLocalRandom.current().nextLong(maxEventDelay + 1);
        Long id = ThreadLocalRandom.current().nextLong(minId, maxId + 1);
        Long group = ThreadLocalRandom.current().nextLong(minGroup, maxGroup + 1);
        Double length = ThreadLocalRandom.current().nextDouble(minLength, maxLength);

        while (running) {
            context.collectWithTimestamp(new Ads(id, group, length), time);

            time += ThreadLocalRandom.current().nextLong(minEventDelay, maxEventDelay + 1);
            id = ThreadLocalRandom.current().nextLong(minId, maxId + 1);
            group = ThreadLocalRandom.current().nextLong(minGroup, maxGroup + 1);
            length = ThreadLocalRandom.current().nextDouble(minLength, maxLength);

            if (maxEventTime < time)
                running = false;

            if (wm + wmTrigger < time) {
                context.emitWatermark(new Watermark(wm));
                wm = time + ThreadLocalRandom.current().nextLong(minWmDelay, maxWmDelay + 1);
                wmTrigger = ThreadLocalRandom.current().nextLong(maxEventDelay + 1);
            }
        }
    }

    @Override
    public void cancel() {
        running = false;
    }

    @Override
    public String toString() {
        return "AdsSource{" +
                "running=" + running +
                ", minEventDelay=" + minEventDelay +
                ", maxEventDelay=" + maxEventDelay +
                ", minWmDelay=" + minWmDelay +
                ", maxWmDelay=" + maxWmDelay +
                ", minId=" + minId +
                ", maxId=" + maxId +
                ", minGroup=" + minGroup +
                ", maxGroup=" + maxGroup +
                ", minLength=" + minLength +
                ", maxLength=" + maxLength +
                "}";
    }
}
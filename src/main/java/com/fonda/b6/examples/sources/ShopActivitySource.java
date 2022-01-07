package com.fonda.b6.examples.sources;

import com.fonda.b6.examples.data.ShopActivity;
import com.fonda.b6.examples.utils.TextGenerator;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class ShopActivitySource implements SourceFunction<ShopActivity> {

    private volatile boolean running = true;
    private Long maxEventTime = 100000L;
    private Long minEventDelay = 2L;
    private Long maxEventDelay = 20L;

    private Long minWmDelay = 1000L;
    private Long maxWmDelay = 10000L;

    private Integer playerCount = 20;
    private Integer nameCount = 100;
    private Double maxPrice = 100.0;

    private Integer removeItemWeigt = 40;

    private HashMap<Integer, Double> items;

    public ShopActivitySource(Long maxEventTime, Integer playerCount) {
        this.maxEventTime = maxEventTime;
        this.playerCount = playerCount;

        items = new HashMap<>();
    }

    @Override
    public void run(SourceContext<ShopActivity> context) {
        TextGenerator gen = new TextGenerator();

        Long time = ThreadLocalRandom.current().nextLong(minEventDelay, maxEventDelay + 1);
        Long wm = ThreadLocalRandom.current().nextLong(minWmDelay, maxWmDelay + 1);
        Long wmTrigger = ThreadLocalRandom.current().nextLong(maxEventDelay + 1);
        int name = ThreadLocalRandom.current().nextInt(nameCount + 1);
        Integer player = ThreadLocalRandom.current().nextInt(playerCount);
        Double price = ThreadLocalRandom.current().nextDouble(maxPrice + 1);
        String activity = gen.shopActivities[0];

        items.put(name, price);

        while (running) {
            context.collectWithTimestamp(
                new ShopActivity(Long.valueOf(name), gen.randomName(name), player, price, activity),
                time
            );

            time += ThreadLocalRandom.current().nextLong(minEventDelay, maxEventDelay + 1);
            name = ThreadLocalRandom.current().nextInt(nameCount + 1);
            player = ThreadLocalRandom.current().nextInt(playerCount);

            if (items.containsKey(name)) {
                price = items.get(name);
                activity = gen.randomShopActivity(removeItemWeigt);

                while (activity == gen.shopActivities[0])
                    activity = gen.randomShopActivity(removeItemWeigt);
            } else {
                price = ThreadLocalRandom.current().nextDouble(maxPrice + 1);
                items.put(name, price);

                activity = gen.shopActivities[0];
            }

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
                ", playerCount=" + playerCount +
                ", nameCount=" + nameCount +
                ", maxPrice=" + maxPrice +
                ", removeItemWeigt=" + removeItemWeigt +
                "}";
    }
}
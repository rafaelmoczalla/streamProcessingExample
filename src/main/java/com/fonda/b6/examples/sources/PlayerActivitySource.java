package com.fonda.b6.examples.sources;

import com.fonda.b6.examples.data.PlayerActivity;
import com.fonda.b6.examples.utils.TextGenerator;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerActivitySource implements SourceFunction<PlayerActivity> {

    private volatile boolean running = true;
    private Long maxEventTime = 100000L;
    private Long minEventDelay = 2L;
    private Long maxEventDelay = 10L;

    private Long minWmDelay = 1000L;
    private Long maxWmDelay = 10000L;

    private Integer playerCount = 20;
    private Integer locCount = 300;

    private Integer logoutWeigt = 50;

    private HashMap<Integer, Integer> onlinePlayers;

    public PlayerActivitySource(Long maxEventTime, Integer playerCount) {
        this.maxEventTime = maxEventTime;
        this.playerCount = playerCount;

        onlinePlayers = new HashMap<>();
    }

    @Override
    public void run(SourceContext<PlayerActivity> context) {
        TextGenerator gen = new TextGenerator();

        Long time = ThreadLocalRandom.current().nextLong(minEventDelay, maxEventDelay + 1);
        Long wm = ThreadLocalRandom.current().nextLong(minWmDelay, maxWmDelay + 1);
        Long wmTrigger = ThreadLocalRandom.current().nextLong(maxEventDelay + 1);
        int name = ThreadLocalRandom.current().nextInt(playerCount);
        int loc = ThreadLocalRandom.current().nextInt(locCount + 1);
        String activity = gen.playerActivities[0];

        onlinePlayers.put(name, loc);

        while (running) {
            context.collectWithTimestamp(
                new PlayerActivity(Long.valueOf(name), gen.randomName(name), gen.randomLocation(loc), activity),
                time
            );

            time += ThreadLocalRandom.current().nextLong(minEventDelay, maxEventDelay + 1);
            name = ThreadLocalRandom.current().nextInt(playerCount);

            if (onlinePlayers.containsKey(name)) {
                loc = onlinePlayers.get(name);
                activity = gen.randomPlayerActivity(logoutWeigt);

                while (activity == gen.playerActivities[0])
                    activity = gen.randomPlayerActivity(logoutWeigt);
            } else {
                loc = ThreadLocalRandom.current().nextInt(locCount + 1);
                onlinePlayers.put(name, loc);

                activity = gen.playerActivities[0];
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
                ", nameCount=" + playerCount +
                ", locCount=" + locCount +
                ", logoutWeigt=" + logoutWeigt +
                "}";
    }
}
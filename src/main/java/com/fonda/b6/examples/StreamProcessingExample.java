package com.fonda.b6.examples;

import java.util.HashMap;
import java.util.stream.StreamSupport;

import com.fonda.b6.examples.data.Ads;
import com.fonda.b6.examples.data.Feedback;
import com.fonda.b6.examples.data.PlayerActivity;
import com.fonda.b6.examples.data.ShopActivity;
import com.fonda.b6.examples.sources.AdsSource;
import com.fonda.b6.examples.sources.FeedbackSource;
import com.fonda.b6.examples.sources.PlayerActivitySource;
import com.fonda.b6.examples.sources.ShopActivitySource;
import com.fonda.b6.examples.utils.TextGenerator;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class StreamProcessingExample {

    public StreamProcessingExample() {}

    // computes continuously for 2 seconds the average number of players online
    // for each continuouse range of 150 milliseconds
    public static DataStream<Double> query1(DataStream<PlayerActivity> dataStream) {
        DataStream<Long> countStream = dataStream
            .windowAll(TumblingEventTimeWindows.of(Time.milliseconds(150)))
            .process(
                new ProcessAllWindowFunction<PlayerActivity, Long, TimeWindow>() {
                    @Override
                    public void process(Context context, Iterable<PlayerActivity> input, Collector<Long> out) {
                        HashMap<Long, Boolean> players = new HashMap<>();
                        TextGenerator gen = new TextGenerator();
                        Long count = 0L;

                        for (PlayerActivity player : input) {
                            if (player.getActivity() != gen.playerActivities[gen.playerActivities.length - 1] &&
                                !players.containsKey(player.getId())) {
                                count++;
                                players.put(player.getId(), true);
                            }
                        }

                        out.collect(count);
                    }
                }
            );

        DataStream<Double> avgStream = countStream.windowAll(TumblingEventTimeWindows.of(Time.seconds(2)))
            .process(
                new ProcessAllWindowFunction<Long, Double, TimeWindow>() {
                    @Override
                    public void process(Context context, Iterable<Long> input, Collector<Double> out) {
                        Long count = 0L;
                        Double sum = 0.0;

                        for (Long item : input) {
                            count++;
                            sum += item;
                        }

                        out.collect(sum / count);
                    }
                }
            );

        return avgStream;
    }

    // query that computes the top 10 items there were sold every second
    public static DataStream<HashMap<String, Long>> query2(DataStream<ShopActivity> dataStream) {
        DataStream<Tuple2<String, Long>> countStream = dataStream
            .keyBy(data -> data.getName())
            .window(TumblingEventTimeWindows.of(Time.seconds(1)))
            .process(
                new ProcessWindowFunction<ShopActivity, Tuple2<String, Long>, String, TimeWindow>() {
                    @Override
                    public void process(String key, Context context, Iterable<ShopActivity> input, Collector<Tuple2<String, Long>> out) {
                        TextGenerator gen = new TextGenerator();
                        Long count = 0L;

                        for (ShopActivity item : input) {
                            if (item.getActivity().equals(gen.shopActivities[2]))
                                count++;
                        }

                        if (0 < count)
                            out.collect(new Tuple2<>(key, count));
                    }
                }
            );

        DataStream<HashMap<String, Long>> topStream = countStream
            .windowAll(SlidingEventTimeWindows.of(Time.seconds(40), Time.seconds(15)))
            .process(
                new ProcessAllWindowFunction<Tuple2<String, Long>, HashMap<String, Long>, TimeWindow>() {
                    @Override
                    public void process(Context context, Iterable<Tuple2<String, Long>> input, Collector<HashMap<String, Long>> out) {
                        HashMap<String, Long> items = new HashMap<>();

                        for (Tuple2<String, Long> item : input) {
                            if (items.size() < 10) {
                                items.put(item.f0, 0L);
                                continue;
                            }

                            String less = "";

                            for (String name : items.keySet()) {
                                if (items.get(name) < item.f1) {
                                    less = name;
                                    break;
                                }
                            }

                            if (less != "") {
                                items.remove(less);
                                items.put(item.f0, item.f1);
                            }
                        }

                        out.collect(items);
                    }
                }
            );

        return topStream;
    }

    // query that computes the ratio of ads to bad feedback
    public static DataStream<Double> query3(DataStream<Feedback> feedbackStream, DataStream<Ads> adsStream) {
        DataStream<Tuple2<String, Long>> countNegFeedsStream = feedbackStream
            .windowAll(TumblingEventTimeWindows.of(Time.seconds(1)))
            .process(
                new ProcessAllWindowFunction<Feedback, Tuple2<String, Long>, TimeWindow>() {
                    @Override
                    public void process(Context context, Iterable<Feedback> input, Collector<Tuple2<String, Long>> out) {
                        Long count = 0L;

                        for (Feedback item : input) {
                            if (item.getGroup() != 1L) // count only bad feedback
                                count++;
                        }

                        if (0 < count)
                            out.collect(new Tuple2<>("Feedback", count));
                    }
                }
            );

        DataStream<Tuple2<String, Long>> countAdsStream = adsStream
            .windowAll(TumblingEventTimeWindows.of(Time.seconds(1)))
            .process(
                new ProcessAllWindowFunction<Ads, Tuple2<String, Long>, TimeWindow>() {
                    @Override
                    public void process(Context context, Iterable<Ads> input, Collector<Tuple2<String, Long>> out) {
                        Long count = 0L;

                        count = StreamSupport.stream(input.spliterator(), false).count();

                        if (0 < count)
                            out.collect(new Tuple2<>("Ad", count));
                    }
                }
            );

        DataStream<Double> ratioStream = countNegFeedsStream
            .union(countAdsStream)
            .windowAll(TumblingEventTimeWindows.of(Time.seconds(2)))
            .process(
                new ProcessAllWindowFunction<Tuple2<String, Long>, Double, TimeWindow>() {
                    @Override
                    public void process(Context context, Iterable<Tuple2<String, Long>> input, Collector<Double> out) {
                        Long countFeedback = 0L;
                        Long countAds = 0L;
                        Double ratio = null;

                        for (Tuple2<String, Long> item : input) {
                            if (item.f0.equals("Feedback"))
                                countFeedback += item.f1;

                            if (item.f0.equals("Ad"))
                                countAds += item.f1;
                        }

                        if (countFeedback != 0L) {
                            ratio = countAds.doubleValue() / countFeedback.doubleValue();
                        }

                        out.collect(ratio);
                    }
                }
            );

        return ratioStream;
    }

    public static void main(String[] args) {
        int parallelism = 3;
        long maxEventTime = 3140000L;
        long millisecondsBetweenTwoEventGenerations = 1L;
        int playerCount = 30;

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(parallelism);

        DataStream<PlayerActivity> playerStream = env
            .addSource(new PlayerActivitySource(millisecondsBetweenTwoEventGenerations, maxEventTime, playerCount));
        DataStream<ShopActivity> shopStream = env
            .addSource(new ShopActivitySource(millisecondsBetweenTwoEventGenerations, maxEventTime, playerCount));
        DataStream<Feedback> feedbackStream = env
            .addSource(new FeedbackSource(millisecondsBetweenTwoEventGenerations, maxEventTime));
        DataStream<Ads> adsStream = env
            .addSource(new AdsSource(millisecondsBetweenTwoEventGenerations, maxEventTime));

        // run all queries and dump the output...
        // to print query results replace .addSink(new DiscardingSink<>()) with .print();
        query1(playerStream).addSink(new DiscardingSink<>());
        query2(shopStream).addSink(new DiscardingSink<>());
        query3(feedbackStream, adsStream).addSink(new DiscardingSink<>());

        try {
            env.execute("Stream Processing Example");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

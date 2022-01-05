package com.fonda.b6.examples;

import com.fonda.b6.examples.data.Average;
import com.fonda.b6.examples.data.Data;
import com.fonda.b6.examples.sources.Source;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class StreamProcessingExample {

    public StreamProcessingExample() {}

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<Data> dataStream = env.addSource(new Source());

        dataStream
            .keyBy(data -> data.getKey())
            .window(SlidingEventTimeWindows.of(Time.seconds(2000), Time.seconds(500)))
            .process(
                new ProcessWindowFunction<Data, Average, Long, TimeWindow>() {
                    @Override
                    public void process(Long key, Context context, Iterable<Data> input, Collector<Average> out) {
                        Average avg = new Average(key, 0L, 0.0);

                        for (Data value : input) {
                            avg.addValues(1L, value.getValue());
                        }

                        out.collect(avg);
                    }
                }
            )
            .print();

        try {
            env.execute("Stream Processing Example");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

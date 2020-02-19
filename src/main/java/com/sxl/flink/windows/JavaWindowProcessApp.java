package com.sxl.flink.windows;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 *
 * ProcessFunction 实现
 * 全量：可以对进来的数据进行排序
 * because elements cannot be incrementally aggregated but instead need to be buffered internally until the window is considered ready for processing.
 * Date: 2020/2/18 17:39
 * Author: sxl
 */
public class JavaWindowProcessApp {
    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<String> data =  env.socketTextStream("localhost",9999);

        data.flatMap(new FlatMapFunction<String, Tuple2<Integer,Integer>>() {

            @Override
            public void flatMap(String value, Collector<Tuple2<Integer, Integer>> out) throws Exception {
                String[] splits =  value.split(",");
                for (String split: splits){
                    if (split.length()>0){
                        out.collect(new Tuple2<>(1,Integer.parseInt(split)));
                    }
                }
            }
        }).keyBy(0)
                .timeWindow(Time.seconds(5))
                .process(new ProcessWindowFunction<Tuple2<Integer,Integer>, Object, Tuple, TimeWindow>() {
                    @Override
                    public void process(Tuple tuple, Context context, Iterable<Tuple2<Integer, Integer>> elements, Collector<Object> out) throws Exception {

                        System.out.println("~~~~~~~");

                        long count = 0;
                        for (Tuple2<Integer,Integer> in : elements){
                            count++;
                        }
                        out.collect("Window:"+context.window()+"count:"+count);
                    }
                })
                .print()
                .setParallelism(1);

        env.execute("JavaWindowsApp");
    }
}

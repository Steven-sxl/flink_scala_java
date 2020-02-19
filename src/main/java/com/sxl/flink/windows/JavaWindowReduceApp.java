package com.sxl.flink.windows;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

/**
 *
 * windows  中  ReduceFunction的实现
 * Date: 2020/2/18 17:25
 * Author: sxl
 */
public class JavaWindowReduceApp {

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
                .reduce(new ReduceFunction<Tuple2<Integer, Integer>>() {
                    @Override
                    public Tuple2<Integer, Integer> reduce(Tuple2<Integer, Integer> value1, Tuple2<Integer, Integer> value2) throws Exception {
                        return new Tuple2<>(value1.f0,value1.f1+value2.f1);
                    }
                })
                .print()
                .setParallelism(1);

        env.execute("JavaWindowsApp");
    }
}

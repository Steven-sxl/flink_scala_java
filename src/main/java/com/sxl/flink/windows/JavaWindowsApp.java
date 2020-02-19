package com.sxl.flink.windows;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;


/**
 *
 * Date: 2020/2/18 16:45
 * Author: sxl
 */
public class JavaWindowsApp {
    public static void main(String[] args) throws Exception{

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<String> data =  env.socketTextStream("localhost",9999);

        data.flatMap(new FlatMapFunction<String, Tuple2<String,Integer>>() {

            @Override
            public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
               String[] splits =  value.split(",");
               for (String split: splits){
                   if (split.length()>0){
                       out.collect(new Tuple2<>(split,1));
                   }
               }
            }
        }).keyBy(0)
                .timeWindow(Time.seconds(5))
                .sum(1)
                .print()
                .setParallelism(1);

        env.execute("JavaWindowsApp");
    }
}

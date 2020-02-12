package com.sxl.flink.datastream;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Desc:Custom Data Source 的三种方式
 *     implements SourceFunction  不能并行
 *     implements ParallelSourceFunction 可以并行
 *     extends RichParallelSourceFunction 可以并行
 * Date: 2020/2/12 11:47
 * Author: sxl
 */
public class JavaDataStreamSourceApp  {
    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

//        nonParallelSourceFunction(env);

//        parallelSourceFunction(env);

        richParallelSourceFunction(env);
        env.execute("JavaDataStreamSourceApp");
    }

    public static void richParallelSourceFunction(StreamExecutionEnvironment env) {
        DataStreamSource<Long> data = env.addSource(new JavaCustomRichParallelSourceFunction()).setParallelism(2);
        data.print();
    }

    public static void parallelSourceFunction(StreamExecutionEnvironment env) {
        DataStreamSource<Long> data = env.addSource(new JavaCustomParallelSourceFunction()).setParallelism(2);
        data.print();
    }

    public static void nonParallelSourceFunction(StreamExecutionEnvironment env) {
        DataStreamSource<Long> data = env.addSource(new JavaCustomNonParallelSourceFunction());
        data.print();
    }
}

package com.sxl.flink.dataset;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.accumulators.LongCounter;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;


/**
 * java 实现flink 计数器
 * Date: 2020/2/13 10:44
 * Author: sxl
 */
public class JavaCounterApp {

    public static void main(String[] args) throws Exception{
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        DataSource<String> data = env.fromElements("hadoop","java","spark","scala","python");

        String filePath = "/Users/sxl/IdeaProjectsCo/flink_scala_java/src/main/resources/sink-java-counter";

        data.map(new RichMapFunction<String, String>() {

            //1。定义计数器
            LongCounter counter = new LongCounter();

            @Override
            public void open(Configuration parameters) throws Exception {
                //2.注册计数器
                getRuntimeContext().addAccumulator("ele-counts-java",counter);
            }

            @Override
            public String map(String value) throws Exception {
                counter.add(1);
                return value;
            }
        })
        .writeAsText(filePath, FileSystem.WriteMode.OVERWRITE).setParallelism(3);

        JobExecutionResult jobResult = env.execute("JavaCounterApp");
        long num = jobResult.getAccumulatorResult("ele-counts-java");

        System.out.println("num:" + num);
    }
}

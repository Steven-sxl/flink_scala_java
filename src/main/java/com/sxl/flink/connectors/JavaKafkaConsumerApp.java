package com.sxl.flink.connectors;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

/**
 *
 * Date: 2020/2/19 16:12
 * Author: sxl
 */
public class JavaKafkaConsumerApp {

    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("group.id", "test");

        String topic = "pktest";
        DataStreamSource<String> data = env.addSource(new FlinkKafkaConsumer<String>(topic,new SimpleStringSchema(),properties));


        data.print().setParallelism(1);
        env.execute("JavaKafkaConsumerApp");
    }
}

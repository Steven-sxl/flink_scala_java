package com.sxl.flink.connectors;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.internals.KeyedSerializationSchemaWrapper;

import java.util.Properties;

/**
 * Date: 2020/2/19 16:19
 * Author: sxl
 */
public class JavaKafkaConnectorProducerApp {
    public static void main(String[] args)  throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<String> data = env.socketTextStream("localhost",9999);
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");

        String topic = "pktest";
        FlinkKafkaProducer<String> myProducer = new FlinkKafkaProducer<String>(topic,
                    new KeyedSerializationSchemaWrapper<>(new SimpleStringSchema()),properties
                );

        data.addSink(myProducer);
        env.execute("JavaKafkaConnectorProducerApp");
    }
}

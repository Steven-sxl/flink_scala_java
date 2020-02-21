package com.sxl.flink.connectors

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer
import org.apache.flink.streaming.connectors.kafka.internals.KeyedSerializationSchemaWrapper

/**
  * Date: 2020/2/19 15:58
  * Author: sxl
  */
object KafkaConnectorProducerApp {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    //从socket接收数据，通过flink，将数据sink到Kafka
    val data = env.socketTextStream("localhost",9999)

    val topic = "pktest"
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "localhost:9092")
    val kafkaSink = new FlinkKafkaProducer[String](topic,
      new KeyedSerializationSchemaWrapper[String](new SimpleStringSchema()),
    properties
    )

    data.addSink(kafkaSink)


    env.execute("KafkaConnectorProducerApp")
  }

}

package com.sxl.flink.connectors

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.api.scala._

/**
  * flink 对接 kafka consumer,消费生产者生产的数据
  * Date: 2020/2/19 15:49
  * Author: sxl
  */
object KafkaConsumerApp {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "localhost:9092")
    properties.setProperty("group.id", "test")


    val topic = "pktest"
    val data = env.addSource(new FlinkKafkaConsumer[String](topic,new SimpleStringSchema(),properties))

    data.print()
    env.execute("KafkaConsumerApp")
  }
}

package com.sxl.flink.connectors

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.fs.StringWriter
import org.apache.flink.streaming.connectors.fs.bucketing.{BucketingSink, DateTimeBucketer}

/**
  * Hadoop FileSystem Connector
  *(有问题）
  * Date: 2020/2/19 14:57
  * Author: sxl
  */
object FileSystemSinkApp {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val data = env.socketTextStream("localhost",9999)

    data.print().setParallelism(1)

    val filePath = "/Users/sxl/IdeaProjectsCo/flink_scala_java/src/main/resources/hdfssink"
    val sink = new BucketingSink[String](filePath)
    sink.setBucketer(new DateTimeBucketer[String]("yyyy-MM-dd--HHmm"))
    sink.setWriter(new StringWriter())
    sink.setUseTruncate(false)
    //sink.setBatchSize(1024 * 1024 * 400) // this is 400 MB,
    sink.setBatchRolloverInterval(2000); //

    data.addSink(sink)
    env.execute("FileSystemSinkApp")
  }
}

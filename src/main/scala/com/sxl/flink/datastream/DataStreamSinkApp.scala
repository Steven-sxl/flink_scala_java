package com.sxl.flink.datastream

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.core.fs.FileSystem.WriteMode

/**
  * DataStream Data Sinks 实现
  * Date: 2020/2/13 10:10
  * Author: sxl
  */
object DataStreamSinkApp {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val data = 1.to(10)
    val text = env.fromCollection(data)

    val filePath = "/Users/sxl/IdeaProjectsCo/flink_scala_java/src/main/resources/sink-out"

    text.writeAsText(filePath,WriteMode.OVERWRITE).setParallelism(1)

    env.execute("DataStreamSinkApp")
  }
}

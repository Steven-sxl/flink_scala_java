package com.sxl.flink.windows

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows

/**
  *
  * Tumbling Windows 使用
  * Sliding Windows 使用
  * 默认是processing time
  * Date: 2020/2/18 16:35
  * Author: sxl
  */
object WindowsApp {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val data = env.socketTextStream("localhost",9999)

    data.flatMap(_.split(","))
      .map((_,1))
      .keyBy(0)
      //Tumbling Windows
//      .timeWindow(Time.seconds(5))
//        .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
      //Sliding Windows 使用
        .timeWindow(Time.seconds(10),Time.seconds(5))
      .sum(1)
      .print()
      .setParallelism(1)

    env.execute("WindowsApp")
  }

}

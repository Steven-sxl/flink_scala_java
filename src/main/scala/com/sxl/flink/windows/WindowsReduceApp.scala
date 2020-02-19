package com.sxl.flink.windows

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.api.scala._

/**
  * Date: 2020/2/18 17:10
  * Author: sxl
  */
object WindowsReduceApp {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val data = env.socketTextStream("localhost",9999)

    data.flatMap(_.split(","))
      .map(x =>(1,x.toInt))
      .keyBy(0) //key 聚合到一个里
      .timeWindow(Time.seconds(5))
      .reduce((v1,v2)=>{
        (v1._1,v1._2+v2._2)
      })
      .print()
      .setParallelism(1)

    env.execute("WindowsReduceApp")
  }


}

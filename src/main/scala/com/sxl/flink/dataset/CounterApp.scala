package com.sxl.flink.dataset

import org.apache.flink.api.common.accumulators.LongCounter
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.configuration.Configuration
import org.apache.flink.api.scala._
import org.apache.flink.core.fs.FileSystem.WriteMode

/**
  * flink 的计数器
  * Date: 2020/2/13 10:31
  * Author: sxl
  */
object CounterApp {

  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment

    val data = env.fromElements("hadoop","java","spark","scala","python")

    val info = data.map(new RichMapFunction[String,String]() {

      //1.定义计数器
      val counter = new LongCounter()

      override def open(parameters: Configuration): Unit = {
        //2.注册计数器
        getRuntimeContext.addAccumulator("ele-counts-scala",counter)
      }

      override def map(value: String) :String ={
        counter.add(1)
        value
      }
    })

    val filePath = "/Users/sxl/IdeaProjectsCo/flink_scala_java/src/main/resources/sink-scala-counter"
    info.writeAsText(filePath,WriteMode.OVERWRITE).setParallelism(5)
    //3.获取计数器
    val jobResult = env.execute("CounterApp")
    val num = jobResult.getAccumulatorResult[Long]("ele-counts-scala")

    println("num:" + num)
  }
}

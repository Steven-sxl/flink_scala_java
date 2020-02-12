package com.sxl.flink.datastream

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._
/**
  * Desc:Custom Data Source 的三种方式
  *     SourceFunction  不能并行
  *     ParallelSourceFunction 可以并行
  *     RichParallelSourceFunction 可以并行
  *
  * Date: 2020/2/12 10:41
  * Author: sxl
  */
object DataStreamSourceApp {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

//    socketFunction(env)

//    nonParallelSourceFunction(env)

//    parallelSourceFunction(env)

    richParallelSourceFunction(env)

    env.execute("DataStreamSourceApp")
  }

  /**
    * 自定义Data Source 继承 RichParallelSourceFunction 能并行
    * @param env
    */
  def richParallelSourceFunction(env:StreamExecutionEnvironment):Unit={
    val data = env.addSource(new CustomRichParallelSourceFunction).setParallelism(2)
    data.print().setParallelism(1)
  }

  /**
    * 自定义Data Source 继承 ParallelSourceFunction 能并行
    * @param env
    */
  def parallelSourceFunction(env:StreamExecutionEnvironment):Unit={
    val data = env.addSource(new CustomParallelSourceFunction).setParallelism(2)
    data.print().setParallelism(1)
  }

  /**
    * 自定义Data Source 继承 SourceFunction 不能并行
    * @param env
    */
  def nonParallelSourceFunction(env:StreamExecutionEnvironment):Unit={
    val data = env.addSource(new CustomNonParallelSourceFunction)
    //如果后面设置并行度为2，会报错：Source: 1 is not a parallel source
//    val data = env.addSource(new CustomNonParallelSourceFunction).setParallelism(2)
    data.print().setParallelism(1)
  }

  /**
    * Data Source：socketTextStream实现
    * @param env
    */
  def socketFunction(env:StreamExecutionEnvironment):Unit ={
    val data = env.socketTextStream("localhost",9999)
    data.print().setParallelism(1)
  }
}

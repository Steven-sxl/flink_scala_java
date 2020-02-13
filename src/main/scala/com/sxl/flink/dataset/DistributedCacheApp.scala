package com.sxl.flink.dataset

import org.apache.commons.io.FileUtils
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration

/**
  * scala 实现flink 的 distributed cache
  *
  * Date: 2020/2/13 11:02
  * Author: sxl
  */
object DistributedCacheApp {

  def main(args: Array[String]): Unit = {

    val env = ExecutionEnvironment.getExecutionEnvironment

    val filePath = "/Users/sxl/IdeaProjectsCo/flink_scala_java/src/main/resources/test.txt"

    //1.注册一个 本地/HDFS文件
    env.registerCachedFile(filePath,"dc-scala")

    val data = env.fromElements("hadoop","java","spark","scala","python")

    data.map(new RichMapFunction[String,String] {
      override def open(parameters: Configuration): Unit = {

        val dcFile = getRuntimeContext.getDistributedCache().getFile("dc-scala")

        val lines = FileUtils.readLines(dcFile)
        //这里会出现一个异常：java集合和scala集合不兼容的问题
        import scala.collection.JavaConversions._
        for (ele <- lines){
          println(ele)
        }
      }
      override def map(value: String) :String ={
        value
      }
    }).print()
  }
}

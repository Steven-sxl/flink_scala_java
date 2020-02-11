package com.sxl.flink.dataset

import org.apache.flink.api.scala.ExecutionEnvironment

/**
  * Date: 2020/2/11 12:20
  * Author: sxl
  */
object DataSetDSourceApp {

  def main(args: Array[String]): Unit = {

    val env = ExecutionEnvironment.getExecutionEnvironment
    //通过集合的方式创建
//    fromCollection(env)
    //通过读取文件方式创建
//    textFile(env)
//    csvFile(env)
//    readCompressionFile(env)
  }

//  def readCompressionFile(env:ExecutionEnvironment): Unit ={
//    val path = "/Users/sxl/Desktop/test/hello.txt.zip"
//    env.readTextFile(path).print()
//  }

  /**
    * 通过csv创建
    * @param env
    */
  def csvFile(env:ExecutionEnvironment):Unit = {
    import org.apache.flink.api.scala._
    val path = "/Users/sxl/IdeaProjectsCo/flink_scala_java/src/main/resources/user.csv"
//    env.readCsvFile[(String, Int, String)](path,ignoreFirstLine = true).print()
    //取两列
//    env.readCsvFile[(String,Int)](path,ignoreFirstLine = true).print()

    //通过POJO读取++
    env.readCsvFile[Person](path,ignoreFirstLine = true,pojoFields = Array("name","age","job")).print()
  }

  /**
    * 通过读取文件创建
    * @param env
    */
  def textFile(env:ExecutionEnvironment): Unit = {
    val path = "/Users/sxl/IdeaProjectsCo/flink_scala_java/src/main/resources/test.txt"
    env.readTextFile(path).print()
  }

  /**
    * 通过集合方式创建
    * @param env
    */
  def fromCollection(env:ExecutionEnvironment): Unit = {

    import org.apache.flink.api.scala._
    val data = 1 to 10
    env.fromCollection(data).print()
  }
}

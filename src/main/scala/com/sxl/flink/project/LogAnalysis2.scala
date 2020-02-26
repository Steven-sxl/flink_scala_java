package com.sxl.flink.project

import java.text.SimpleDateFormat
import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
  * Date: 2020/2/21 16:34
  * Author: sxl
  */
object LogAnalysis2 {

  def main(args: Array[String]): Unit = {

    //日志
    val logger = LoggerFactory.getLogger("LogAnalysis2")

    //执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    val topic = "pktest"

    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "localhost:9092")
    properties.setProperty("group.id", "test-group")

    val source = new FlinkKafkaConsumer[String](topic,new SimpleStringSchema(),properties)

    val data = env.addSource(source)


    val logData = data.map(x => {
      val splits = x.split("\t")
      val level =  splits(2)

      //将时间格式 yyyy-MM-DD HH:mm:ss 转换程long类型
      val timeTmp = splits(3)
      var time = 0l
      try{
        val sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        time = sourceFormat.parse(timeTmp).getTime
      }catch {
        case e:Exception =>{
          logger.error(s"time parse error $timeTmp" + e.getMessage)
        }
      }

      val domain = splits(5)
      val traffic = splits(6).toLong

      (level,time,domain,traffic)
    }).filter(_._2 != 0).filter(_._1 == "E")
      //抛掉level
      .map(x=>{
        (x._2,x._3,x._4)
      })

    val mysqlData = env.addSource(new MySQLSource)

    val connectData = logData.connect(mysqlData)
      .flatMap(new CoFlatMapFunction[(Long,String,Long),mutable.HashMap[String,String],String] {

        var userDomainMap = mutable.HashMap[String,String]()

        //处理log
        override def flatMap1(value: (Long, String, Long), out: Collector[String])  = {
          val domain = value._2
          val userId = userDomainMap.getOrElse(domain,"")

          println(userId)

          out.collect(value._1 + "\t" + value._2 + "\t" + value._3 + "\t" +  userId)
        }

        //处理mysql
        override def flatMap2(value: mutable.HashMap[String, String], out: Collector[String]) = {
          userDomainMap = value
        }
      }).print().setParallelism(1)



    env.execute("LogAnalysis2")
  }
}

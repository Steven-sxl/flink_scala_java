package com.sxl.flink.project

import java.text.SimpleDateFormat
import java.util
import java.util.Properties

import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink
import org.apache.flink.util.Collector
import org.apache.http.HttpHost
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests
import org.slf4j.LoggerFactory

/**
  * Date: 2020/2/21 16:34
  * Author: sxl
  */
object LogAnalysis {

  def main(args: Array[String]): Unit = {

    //日志
    val logger = LoggerFactory.getLogger("LogAnalysis")

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

    //生成watermark
    val input = logData.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarks[(Long,String,Long)] {

      val maxOutOfOrderness = 10000L // 10 seconds

      var currentMaxTimestamp: Long = _

      override def getCurrentWatermark(): Watermark = {
        // return the watermark as current highest timestamp minus the out-of-orderness bound
        new Watermark(currentMaxTimestamp - maxOutOfOrderness)
      }

      override def extractTimestamp(element: (Long, String, Long), previousElementTimestamp: Long) :Long = {
        val timestamp = element._1
        currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp)
        timestamp
      }
    }).keyBy(1)
      .window(TumblingEventTimeWindows.of(Time.seconds(60)))
      .apply(new WindowFunction[(Long,String,Long),(String,String,Long),Tuple,TimeWindow] {
        override def apply(
                            key: Tuple,
                            window: TimeWindow,
                            input: Iterable[(Long, String, Long)],
                            out: Collector[(String, String, Long)]): Unit = {

          val domain = key.getField(0).toString
          var sum = 0l
          var time = ""


          val iterator = input.iterator
          while (iterator.hasNext){
            val next = iterator.next()
            //traffic 求和
            sum += next._3

            time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(next._1)
          }

          /**
            * 参数1：这一分钟的时间
            * 参数2：域名
            * 参数3：traffic的和
            */
          out.collect(time,domain,sum)

        }
      })

    //将数据提交到ES
    val httpsHosts = new util.ArrayList[HttpHost]
    httpsHosts.add(new HttpHost("localhost",9200,"http"))

    val esSinkBuilder = new ElasticsearchSink.Builder[((String, String, Long))](
      httpsHosts,
      new ElasticsearchSinkFunction[(String, String, Long)] {
        override def process(t: (String, String, Long),
                             ctx: RuntimeContext,
                             indexer: RequestIndexer): Unit ={
          val json = new util.HashMap[String,Any]()
          json.put("time",t._1)
          json.put("domain",t._2)
          json.put("traffics",t._3)

          val id = t._1 + "-" + t._2

          val rqst:IndexRequest = Requests.indexRequest
            .index("flink-index")
            .`type`("traffic")
            .id(id)
            .source(json)

          indexer.add(rqst)

        }
      }
    )
    //configuration for the bulk requests;this instructs the sink to emit after every element,otherwise they would be buffered
    esSinkBuilder.setBulkFlushMaxActions(1)

    //finally ,bulid and add the sink to the job's pipeline
    input.addSink(esSinkBuilder.build())
    input.print().setParallelism(1)

    env.execute("LogAnalysis")
  }
}

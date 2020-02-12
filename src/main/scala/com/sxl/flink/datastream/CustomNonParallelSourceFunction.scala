package com.sxl.flink.datastream

import org.apache.flink.streaming.api.functions.source.SourceFunction

/**
  * 自定义Data Source ：继承SourceFunction ，不能并行
  * Date: 2020/2/12 11:10
  * Author: sxl
  */
class CustomNonParallelSourceFunction extends SourceFunction[Long]{

  var count = 1L

  var isRunning = true

  override def cancel() :Unit = {
    isRunning = false
  }

  override def run(ctx: SourceFunction.SourceContext[Long]) :Unit={
    while (isRunning){
      ctx.collect(count)
      count += 1
      Thread.sleep(1000)
    }
  }
}

package com.sxl.flink.datastream

import org.apache.flink.streaming.api.functions.source.{RichParallelSourceFunction, SourceFunction}

/**
  * Date: 2020/2/12 11:41
  * Author: sxl
  */
class CustomRichParallelSourceFunction extends RichParallelSourceFunction[Long] {

  var count = 1L

  var isRunning = true

  override def cancel():Unit={
    isRunning = false
  }

  override def run(ctx: SourceFunction.SourceContext[Long]):Unit={
    while (isRunning){
      ctx.collect(count)
      count+= 1
      Thread.sleep(1000)
    }
  }
}

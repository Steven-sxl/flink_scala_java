package com.sxl.flink.datastream

import org.apache.flink.streaming.api.functions.source.{ParallelSourceFunction, SourceFunction}

/**
  * 自定义Data Source ：继承ParallelSourceFunction
  * Date: 2020/2/12 11:34
  * Author: sxl
  */
class CustomParallelSourceFunction extends ParallelSourceFunction[Long]{

  var count = 1L

  var isRunning = true

  override def cancel() :Unit = {
    isRunning = false
  }

  override def run(ctx: SourceFunction.SourceContext[Long]) :Unit ={
    while (isRunning){
      ctx.collect(count)
      count+=1
      Thread.sleep(1000)
    }
  }
}

package com.sxl.flink.datastream;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

/**
 * 自定义Data Source ：implements SourceFunction
 * Date: 2020/2/12 11:48
 * Author: sxl
 */
public class JavaCustomNonParallelSourceFunction implements SourceFunction<Long> {

    long count = 1L;

    boolean isRunning = true;

    @Override
    public void run(SourceContext<Long> ctx) throws Exception {
        while (isRunning){
            ctx.collect(count);
            count+=1;
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}

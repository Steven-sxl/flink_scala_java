package com.sxl.flink.datastream;

import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Date: 2020/2/12 11:54
 * Author: sxl
 */
public class JavaCustomParallelSourceFunction implements ParallelSourceFunction<Long> {

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

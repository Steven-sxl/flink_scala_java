package com.sxl.flink.datastream;

import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

/**
 * Date: 2020/2/12 11:56
 * Author: sxl
 */
public class JavaCustomRichParallelSourceFunction extends RichParallelSourceFunction<Long> {

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

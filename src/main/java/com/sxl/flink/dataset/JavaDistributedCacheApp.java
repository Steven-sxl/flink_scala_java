package com.sxl.flink.dataset;

import org.apache.commons.io.FileUtils;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.configuration.Configuration;

import java.io.File;
import java.util.List;

/**
 * java 实现 flink 的Distributed Cache
 * Date: 2020/2/13 11:21
 * Author: sxl
 */
public class JavaDistributedCacheApp {

    public static void main(String[] args) throws Exception{
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        String filePath = "/Users/sxl/IdeaProjectsCo/flink_scala_java/src/main/resources/test.txt";

        env.registerCachedFile(filePath,"dc-java");

        DataSource<String> data = env.fromElements("hadoop","java","spark","scala","python");

        data.map(new RichMapFunction<String, String>() {

            @Override
            public void open(Configuration parameters) throws Exception {
                File myFile = getRuntimeContext().getDistributedCache().getFile("dc-java");

                List<String> lines = FileUtils.readLines(myFile);
                for (String line:lines
                     ) {
                    System.out.println(line);
                }
            }

            @Override
            public String map(String value) throws Exception {
                return value;
            }
        }).print();
    }
}

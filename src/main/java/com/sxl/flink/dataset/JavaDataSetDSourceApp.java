package com.sxl.flink.dataset;

import org.apache.flink.api.java.ExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2020/2/11 14:31
 * Author: sxl
 */
public class JavaDataSetDSourceApp {
    public static void main(String[] args) throws Exception{
        //执行环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        fromCollection(env);
        textFile(env);
    }

    //通过读取文件创建
    public static void textFile(ExecutionEnvironment env) throws Exception{
        String path = "/Users/sxl/IdeaProjectsCo/flink_scala_java/src/main/resources/test.txt";
        env.readTextFile(path).print();
    }

    //通过集合创建
    public static void fromCollection(ExecutionEnvironment env) throws Exception{
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i<=10; i++){
            list.add(i);
        }
        env.fromCollection(list).print();
    }
}

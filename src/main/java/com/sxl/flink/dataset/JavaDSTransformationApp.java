package com.sxl.flink.dataset;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

/**
 * Transformation 使用
 * Date: 2020/2/11 15:32
 * Author: sxl
 */
public class JavaDSTransformationApp {

    public static void main(String[] args) throws Exception{
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

//        mapFunction(env);
//        firstFunction(env);
//        flatMapFunction(env);
        joinFunction(env);
    }

    /**
     * join 实现
     * @param env
     * @throws Exception
     */
    public static void joinFunction(ExecutionEnvironment env) throws Exception{
        List<Tuple2<Integer,String>> info1 = new ArrayList<>();
        info1.add(new Tuple2<>(1,"zhangsan"));
        info1.add(new Tuple2<>(2,"lisi"));
        info1.add(new Tuple2<>(3,"wangwu"));
        info1.add(new Tuple2<>(4,"maliu"));
        List<Tuple2<Integer,String>> info2 = new ArrayList<>();
        info2.add(new Tuple2<>(1,"北京"));
        info2.add(new Tuple2<>(2,"上海"));
        info2.add(new Tuple2<>(3,"广州"));
        info2.add(new Tuple2<>(5,"深圳"));

        DataSource<Tuple2<Integer,String>> data1 = env.fromCollection(info1);
        DataSource<Tuple2<Integer,String>> data2 = env.fromCollection(info2);

        data1.join(data2).where(0).equalTo(0).with(new JoinFunction<Tuple2<Integer,String>, Tuple2<Integer,String>, Tuple3<Integer,String,String>>() {
            @Override
            public Tuple3<Integer, String, String> join(Tuple2<Integer, String> first, Tuple2<Integer, String> second) throws Exception {
                return new Tuple3<Integer, String, String>(first.f0,first.f1,second.f1);
            }
        }).print();

    }

    /**
     * flatmap 使用
     * @param env
     * @throws Exception
     */
    public static void flatMapFunction(ExecutionEnvironment env) throws Exception{
        List<String> info = new ArrayList<>();
        info.add("hadoop,hello,java");
        info.add("hadoop,java,spark");
        info.add("hadoop,flink,hello");

        DataSource<String> data = env.fromCollection(info);

        data.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String value, Collector<String> out) throws Exception {
                String[] tokens = value.split(",");
                for (String token : tokens){
                    out.collect(token);
                }
            }
        }).map(new MapFunction<String, Tuple2<String,Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String value) throws Exception {
                return new Tuple2<>(value,1);
            }
        }).groupBy(0).sum(1).print();
    }

    /**
     * first N 使用
     * @param env
     * @throws Exception
     */
    public static void firstFunction(ExecutionEnvironment env) throws Exception{
        List<Tuple2<Integer,String>> info = new ArrayList<>();
        info.add(new Tuple2<>(1,"Hadoop"));
        info.add(new Tuple2<>(1,"Spark"));
        info.add(new Tuple2<>(1,"Flink"));
        info.add(new Tuple2<>(2,"Java"));
        info.add(new Tuple2<>(2,"Spring Boot"));
        info.add(new Tuple2<>(3,"Linux"));
        info.add(new Tuple2<>(4,"VUE"));

        DataSource<Tuple2<Integer,String>> data = env.fromCollection(info);
        data.groupBy(0).first(2).print();
        data.groupBy(0).sortGroup(1, Order.ASCENDING).first(2).print();
    }

    /**
     * Map Filter 的使用
     * @param env
     * @throws Exception
     */
    public static void mapFunction(ExecutionEnvironment env) throws Exception{
        List<Integer>  list = new ArrayList<>();
        for (int i = 1 ; i<=10;i++){
            list.add(i);
        }
        DataSource<Integer> data = env.fromCollection(list);

        data.map(new MapFunction<Integer, Integer>() {
            @Override
            public Integer map(Integer value) throws Exception {
                return value + 1;
            }
        }).filter(new FilterFunction<Integer>() {
            @Override
            public boolean filter(Integer value) throws Exception {
                return value > 5;
            }
        }).print();
    }
}

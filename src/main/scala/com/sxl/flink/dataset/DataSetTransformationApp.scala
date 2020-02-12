package com.sxl.flink.dataset

import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._

import scala.collection.mutable.ListBuffer
/**
  * Transformation 操作
  *
  * Date: 2020/2/11 15:22
  * Author: sxl
  */
object DataSetTransformationApp {

  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment

//    mapFunction(env)
//    firstFunction(env)

//    flatMapFunction(env)
//    joinFunction(env)
//    outjoinFunction(env)
    crossFunction(env)
  }

  /**
    * 笛卡尔积 使用
    * @param env
    */
  def crossFunction(env:ExecutionEnvironment):Unit ={
    val info1 = List("火箭","湖人")
    val info2 = List(3,1,0)

    val data1 = env.fromCollection(info1)
    val data2 = env.fromCollection(info2)

    data1.cross(data2).print()
  }

  /**
    * outjoin 的实现
    * @param env
    */
  def outjoinFunction(env:ExecutionEnvironment):Unit ={
    val info1 = ListBuffer[(Int,String)]()
    info1.append((1,"zhangsan"))
    info1.append((2,"lisi"))
    info1.append((3,"wangwu"))
    info1.append((4,"maliu"))

    val info2 = ListBuffer[(Int,String)]()
    info2.append((1,"北京"))
    info2.append((2,"上海"))
    info2.append((3,"成都"))
    info2.append((5,"广州"))

    val data1 = env.fromCollection(info1)
    val data2 = env.fromCollection(info2)

    data1.leftOuterJoin(data2).where(0).equalTo(0).apply((left,right)=>{
      //不加判断会报空指针异常
      if(right == null){
        (left._1,left._2,"-")
      }else{
        (left._1,left._2,right._2)
      }


    }).print()
  }

  /**
    * join 使用
    * @param env
    */
  def joinFunction(env:ExecutionEnvironment):Unit ={

    val info1 = ListBuffer[(Int,String)]()
    info1.append((1,"zhangsan"))
    info1.append((2,"lisi"))
    info1.append((3,"wangwu"))
    info1.append((4,"maliu"))

    val info2 = ListBuffer[(Int,String)]()
    info2.append((1,"北京"))
    info2.append((2,"上海"))
    info2.append((3,"成都"))
    info2.append((4,"广州"))

    val data1 = env.fromCollection(info1)
    val data2 = env.fromCollection(info2)

    data1.join(data2).where(0).equalTo(0).apply((left,right)=>{
      (left._1,left._2,right._2)
    }).print()
  }

  /**
    * Flatmap 实现
    * @param env
    */
  def flatMapFunction(env:ExecutionEnvironment) : Unit ={
    val info = ListBuffer[String]()
    info.append("hadoop,spark")
    info.append("hadoop,java")
    info.append("java,java")

    val data = env.fromCollection(info)

    data.flatMap(_.split(",")).print()
    data.flatMap(_.split(",")).map((_,1)).groupBy(0).sum(1).print()
  }

  /**
    * First N 的使用
    * @param env
    */
  def firstFunction(env:ExecutionEnvironment):Unit = {
    val info = ListBuffer[(Int,String)]()
    info.append((1,"Hadoop"));
    info.append((1,"Spark"));
    info.append((1,"Flink"));
    info.append((2,"Java"));
    info.append((2,"Spring Boot"));
    info.append((3,"Linux"));
    info.append((4,"VUE"));

    val data = env.fromCollection(info)

//    data.first(4).print()
//    data.groupBy(0).first(2).print()
    data.groupBy(0).sortGroup(1,Order.ASCENDING).first(2).print()
  }

  /**
    * map  filter使用
    * @param env
    */
  def mapFunction(env:ExecutionEnvironment):Unit={
    val data = env.fromCollection(List(1,2,3,4,5,6,7,8,9,10))
//    data.map((x:Int) => x+1).print()
    data.map(_+1)
      .filter(_>5)
      .print()
  }
}

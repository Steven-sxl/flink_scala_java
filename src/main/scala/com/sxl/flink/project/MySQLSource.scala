package com.sxl.flink.project

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.{RichParallelSourceFunction, SourceFunction}

import scala.collection.mutable

/**
  * Date: 2020/2/25 10:41
  * Author: sxl
  */
class MySQLSource extends RichParallelSourceFunction[mutable.HashMap[String,String]]{

  var connection:Connection = null
  var ps:PreparedStatement = null

  //open:建立连接
  override def open(parameters: Configuration): Unit = {

    val driver = "com.mysql.cj.jdbc.Driver"
    val url = "jdbc:mysql://localhost:3306/flink?useSSL=false"
    val user = "root"
    val password = "root"
    Class.forName(driver)
    connection =DriverManager.getConnection(url,user,password)

    val sql = "select user_id,domain from user_domain_config"

    ps = connection.prepareStatement(sql)
  }

  override def close(): Unit = {
    if (ps != null){
      ps.close()

    }

    if (connection != null){
      connection.close()
    }
  }

  override def cancel():Unit = {}

  /**
    * 此处是代码的关键：要从MySQL表中把数据读取出来转换成Map进行数据的封装
    * @param ctx
    */
  override def run(ctx: SourceFunction.SourceContext[mutable.HashMap[String, String]]) : Unit = {

    val resultData = ps.executeQuery()
    var map = new mutable.HashMap[String,String]()

    while (resultData.next()){
      map.put(resultData.getString(2),resultData.getString(1))
    }
    ctx.collect(map)

  }
}
